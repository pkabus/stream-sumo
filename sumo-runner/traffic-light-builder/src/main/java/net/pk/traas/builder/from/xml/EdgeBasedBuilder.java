package net.pk.traas.builder.from.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Builder class, that can create TLS programs for an entire network.
 * 
 * @author peter
 *
 */
public class EdgeBasedBuilder extends TrafficLightBuilder {

	private EdgeBasedBuilder(Document doc) {
		super(doc);
	}

	/**
	 * Construct method. The given file path must point to a network file.
	 * 
	 * @param netFilePath network file path
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static EdgeBasedBuilder create(final String netFilePath)
			throws SAXException, IOException, ParserConfigurationException {
		return create(new File(netFilePath));
	}

	/**
	 * Construct method. The given file must be a network file.
	 * 
	 * @param netFile network file
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static EdgeBasedBuilder create(final File netFile)
			throws SAXException, IOException, ParserConfigurationException {
		return new EdgeBasedBuilder(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(netFile));
	}

	@Override
	public Set<HashMap<String, String>> createAll() {
		Set<HashMap<String, String>> result = new HashSet<HashMap<String, String>>();

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList trafficLightIds;
		try {
			trafficLightIds = (NodeList) xPath.evaluate("//tlLogic/@id", this.getSourceDoc(),
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("XPath evaluation failed: ", e);
		}

		if (trafficLightIds == null) {
			return result;
		}

		for (int i = 0; i < trafficLightIds.getLength(); i++) {
			String id = trafficLightIds.item(i).getTextContent();
			result.add(createFor(id));
		}

		result.removeIf(HashMap::isEmpty);
		this.setResultSet(result);
		return result;
	}

	@Override
	public HashMap<String, String> createFor(final String tlsId) {
		HashMap<String, String> programsForSingleTls = new HashMap<>();
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList) xPath.evaluate("//connection[@tl='" + tlsId + "']", this.getSourceDoc(),
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("XPath evaluation failed: ", e);
		}

		List<TLSConnection> connections = createTLSConnectionsForTLS(nodes, tlsId);
		this.addTlsId(tlsId);

		for (TLSConnection conn : connections) {
			if (!programsForSingleTls.containsKey(conn.getFromId())) {
				TLSProgramProducer programProducer = TLSProgramProducerFactory.create(connections, conn);
				if (programProducer != null) {
					String phase = programProducer.produceTLSPhase();
					programsForSingleTls.put(conn.getFromId(), phase);
				}
			}
		}

		return programsForSingleTls;

	}

	/**
	 * Factory method which creates {@link TLSConnection} objects of a given TLS.
	 * 
	 * @param nodes input nodes
	 * @param tlsId corresponding tls id to the returned list of objects
	 * @return list of {@link TLSConnection} of the given tls id
	 */
	private static List<TLSConnection> createTLSConnectionsForTLS(NodeList nodes, final String tlsId) {
		ArrayList<TLSConnection> connections = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Element e = (Element) nodes.item(i);
			String fromId = e.getAttribute("from");
			String toId = e.getAttribute("to");
			String dir = e.getAttribute("dir");
			int linkIndex = Integer.parseInt(e.getAttribute("linkIndex"));
			TLSConnection conn = new TLSConnection(fromId, toId, dir, tlsId, linkIndex);
			connections.add(conn);
		}
		return connections;
	}
}
