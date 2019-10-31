package net.pk.stream.xml.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.pk.data.type.SumoEdge;
import net.pk.data.type.TLSKey;
import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * This class scans the corresponding sumo network file (*.net.xml) and stores
 * all the TLS' which are found in it.
 * 
 * @author peter
 *
 */
public class TLSFinder {

	private static TLSFinder instance;

	private Map<SumoEdge, TLSKey> map;
	private EdgeFinder edgeFinder;

	private TLSFinder() {
		this.map = new HashMap<>();
		this.edgeFinder = EdgeFinder.getInstance();
	}

	/**
	 * Get singleton instance.
	 * 
	 * @return instance
	 */
	public static TLSFinder getInstance() {
		if (instance == null) {
			instance = new TLSFinder();
			instance.scan();
		}

		return instance;
	}

	/**
	 * Find a {@link TLSKey} by {@link SumoEdge}. Multiple edges can be associated
	 * to the same TLS.
	 * 
	 * @param edge to which the TLS is wanted
	 * @return the associated TLS
	 */
	@Nullable
	public TLSKey bySumoEdge(final SumoEdge edge) {
		return map.get(edge);
	}

	/**
	 * Find a {@link TLSKey} by the id (of a {@link SumoEdge}). Multiple edges can
	 * be associated to the same TLS.
	 * 
	 * @param edgeId to which the TLS is wanted
	 * @return the associated TLS
	 */
	@Nullable
	public TLSKey bySumoEdgeId(final String edgeId) {
		return bySumoEdge(edgeFinder.byId(edgeId));
	}

	private void scan() {
		EnvironmentConfig conf = EnvironmentConfig.getInstance();

		Document document = DocumentDelivery.getDocument(new File(conf.getAbsolutePathNetworkFile()));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList tlConnections;
		try {
			tlConnections = (NodeList) xPath.evaluate("//connection[@tl]", document, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("XPath evaluation failed: ", e);
		}

		for (int i = 0; i < tlConnections.getLength(); i++) {
			Element current = (Element) tlConnections.item(i);
			String id = current.getAttribute("tl");
			String from = current.getAttribute("from");
			map.put(edgeFinder.byId(from), new TLSKey(id));
		}
	}

}
