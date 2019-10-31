package net.pk.stream.xml.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.pk.data.type.SumoEdge;
import net.pk.data.type.SumoEdgeFactory;
import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * Stores {@link SumoEdge} objects represented in the sumo network file.
 * 
 * @author peter
 *
 */
public class EdgeFinder {

	private static EdgeFinder instance;
	private SumoEdgeFactory factory;
	private Set<SumoEdge> edgeCache;

	private EdgeFinder() {
		this.factory = new SumoEdgeFactory();
		this.edgeCache = new HashSet<>();
	}

	/**
	 * Get singleton instance.
	 * 
	 * @return instance
	 */
	public static EdgeFinder getInstance() {
		if (instance == null) {
			instance = new EdgeFinder();
			instance.scan();
		}

		return instance;
	}

	/**
	 * Find the edge object by the given id. Might return null, if no edge is found
	 * with the given id.
	 * 
	 * @param id of edge
	 * @return the edge object associated to the given parameter
	 */
	@Nullable
	public SumoEdge byId(final String id) {
		if (StringUtils.isEmpty(id)) {
			throw new RuntimeException(new IllegalArgumentException("id must not be empty"));
		}

		return edgeCache.stream().filter(s -> s.getId().equals(id)).findFirst().orElseGet(() -> null);

	}

	/**
	 * Find the edge object by the given from- and to- nodes. Might return null, if
	 * no edge is found with the given id.
	 * 
	 * @param from node id of edge origin
	 * @param to   node id of edge target
	 * @return the edge object associated to the given parameters
	 */
	@Nullable
	public SumoEdge byFromTo(final String from, final String to) {
		if (StringUtils.isEmpty(from) || StringUtils.isEmpty(to)) {
			throw new RuntimeException(new IllegalArgumentException("from and/or to must not be empty"));
		}

		return edgeCache.stream().filter(s -> s.getFrom().equals(from) && s.getTo().equals(to)).findFirst()
				.orElseGet(() -> null);
	}

	private void scan() {
		EnvironmentConfig conf = EnvironmentConfig.getInstance();

		Document document = DocumentDelivery.getDocument(new File(conf.getAbsolutePathNetworkFile()));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nonInternalEdges;
		try {
			nonInternalEdges = (NodeList) xPath.evaluate("//edge[not(@internal) and @from and @to]", document,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("XPath evaluation failed: ", e);
		}

		for (int i = 0; i < nonInternalEdges.getLength(); i++) {
			Element current = (Element) nonInternalEdges.item(i);
			String id = current.getAttribute("id");
			String from = current.getAttribute("from");
			String to = current.getAttribute("to");

			// current.setAttribute("id", from + conf.getNodeSeparator() + to);
			// refactor all appearances in whole XML
			// also lanes like C3C4_0 -> C3_C4_0

			edgeCache.add(factory.create(id, from, to));
		}
	}
}
