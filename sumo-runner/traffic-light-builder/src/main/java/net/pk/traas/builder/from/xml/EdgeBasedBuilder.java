package net.pk.traas.builder.from.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author peter
 *
 */
public abstract class EdgeBasedBuilder extends TrafficLightBuilder {

	protected EdgeBasedBuilder(Document doc) {
		super(doc);
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

		List<TLSConnection> connections = createTLSConnectionsForTLS(nodes, tlsId);
		this.addTlsId(tlsId);

		for (TLSConnection conn : connections) {
			if (!programsForSingleTls.containsKey(conn.getFromId())) {
				String phase = produceTLSPhase(connections, conn.getFromId());
				programsForSingleTls.put(conn.getFromId(), phase);
			}
		}

		return programsForSingleTls;

	}

	protected List<TLSConnection> createTLSConnectionsForTLS(NodeList nodes, final String tlsId) {
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

	/**
	 * @param nodes
	 * @param fromId
	 * @return
	 */
	protected abstract String produceTLSPhase(final List<TLSConnection> connectionsOfTls, final String fromId);

	/**
	 * @param nodes
	 * @param phase
	 * @param fromId
	 * @return
	 */
	protected char[] addOtherGreenPhases(final List<TLSConnection> connections, final char[] phase,
			final String fromId) {
		char[] additionalGreenPhases = phase;
		String complementary = REVERSE_EDGE_FUNCTION.apply(fromId);
		if (complementary == null) {
			return phase;
		}
		for (int j = 0; j < connections.size(); j++) {
			TLSConnection e = connections.get(j);
			int index = e.getLinkIndex();
			if (complementary.equals(e.getToId()) && "s".equals(e.getDir())) {
				additionalGreenPhases[index] = 'G';
			}
		}

		return additionalGreenPhases;
	}
}
