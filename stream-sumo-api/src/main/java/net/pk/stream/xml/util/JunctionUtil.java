package net.pk.stream.xml.util;

import java.nio.file.Paths;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * Utility that uses the network xml file of the corresponding Sumo scenario and
 * offers a helper method which returns the green-yellow-red state of TLS
 * programs.
 * 
 * @author peter
 *
 */
public class JunctionUtil {

	private final static Logger LOG = LoggerFactory.getLogger(JunctionUtil.class);

	/**
	 * Returns the green-yellow-red state (as a string representation) of the tls
	 * and program of the given ids in the given phase. Usually, TLS programs
	 * contain a single phase only, so the phase parameter should always be 0.
	 * 
	 * @param tlsId     of tls
	 * @param programId of tls
	 * @param phase     of tls
	 * @return the green-yellow-red state string representation
	 */
	public static String getRedYellowGreenState(final String tlsId, final String programId, final int phase) {
		EnvironmentConfig conf = EnvironmentConfig.getInstance();
		Document tlsDocument = DocumentDelivery
				.getDocument(Paths.get(conf.getConfigFileDir(), EnvironmentConfig.ADD_TLS_FILE));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		String expression = "//tlLogic[@id='" + tlsId + "' and @programID='" + programId + "']/phase['" + (phase + 1)
				+ "']";
		try {
			nodes = (NodeList) xPath.evaluate(expression, tlsDocument, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("XPath evaluation failed: ", e);
		}

		if (nodes.getLength() != 1) {
			LOG.error("No result for xpath expression: " + expression);
			throw new RuntimeException(
					"Expression " + xPath + " is supposed to have a single result, but has " + nodes.getLength());
		}

		return ((Element) nodes.item(0)).getAttribute("state");
	}
}
