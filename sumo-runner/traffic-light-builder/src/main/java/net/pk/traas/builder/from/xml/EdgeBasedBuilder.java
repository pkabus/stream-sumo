package net.pk.traas.builder.from.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
 * @author peter
 *
 */
public class EdgeBasedBuilder extends TrafficLightBuilder {


	private EdgeBasedBuilder(Document doc) {
		super(doc);
	}

	public static EdgeBasedBuilder createEdgeBasedBuilder(final String netFilePath)
			throws SAXException, IOException, ParserConfigurationException {
		return createEdgeBasedBuilder(new File(netFilePath));
	}

	public static EdgeBasedBuilder createEdgeBasedBuilder(final File netFile)
			throws SAXException, IOException, ParserConfigurationException {
		return new EdgeBasedBuilder(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(netFile));
	}

	@Override
	public Set<HashMap<String, String>> createAll() {
		Set<HashMap<String, String>> result = new HashSet<HashMap<String, String>>();

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList trafficLightIds;
		try {
			trafficLightIds = (NodeList) xPath.evaluate("//junction[@type='traffic_light']/@id", this.getSourceDoc(),
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
		for (int i = 0; i < nodes.getLength(); i++) {
			Element e = (Element) nodes.item(i);
			this.addTlsId(e.getAttribute("tl"));
			String fromId = e.getAttribute("from");

			if (!programsForSingleTls.containsKey(fromId)) {
				String phase = producePhaseString(nodes, fromId);
				programsForSingleTls.put(fromId, phase);
			}

		}

		return programsForSingleTls;
	}

	private String producePhaseString(final NodeList nodes, final String fromId) {
		boolean leftTurnConnection = false;
		char[] phase = new char[nodes.getLength()];
		for (int j = 0; j < nodes.getLength(); j++) {
			Element e = (Element) nodes.item(j);
			int index = Integer.parseInt(e.getAttribute("linkIndex"));
			if (fromId.equals(e.getAttribute("from"))) {
				phase[index] = 'G';
				leftTurnConnection = "l".equals(e.getAttribute("dir"));
			} else {
				phase[index] = 'r';
			}
		}

		if (!leftTurnConnection) {
			phase = addOtherGreenPhases(nodes, phase, fromId);
		}

		return new String(phase);
	}

	private char[] addOtherGreenPhases(final NodeList nodes, final char[] phase, final String fromId) {
		char[] additionalGreenPhases = phase;
		String complementary = REVERSE_EDGE_FUNCTION.apply(fromId);
		if (complementary == null) {
			return phase;
		}
		for (int j = 0; j < nodes.getLength(); j++) {
			Element e = (Element) nodes.item(j);
			int index = Integer.parseInt(e.getAttribute("linkIndex"));
			if (complementary.equals(e.getAttribute("to")) && "s".equals(e.getAttribute("dir"))) {
				additionalGreenPhases[index] = 'G';
			}
		}

		return additionalGreenPhases;
	}

}
