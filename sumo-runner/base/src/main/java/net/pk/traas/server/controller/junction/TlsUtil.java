package net.pk.traas.server.controller.junction;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * @author peter
 *
 */
public class TlsUtil {
	
	/**
	 * @param tlsId
	 * @param programId
	 * @param phase
	 * @return
	 */
	public static String getRedYellowGreenState(final String tlsId, final String programId, final int phase) {
		Document tlsDocument = EnvironmentConfig.getInstance().getAdditionalDom(EnvironmentConfig.ADD_TLS_FILE_KEY);
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList) xPath.evaluate("//tlLogic[@id='" + tlsId + "' and @programID='" + programId + "']/phase['" + (phase + 1) + "']", tlsDocument, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("XPath evaluation failed: ", e);
		}
		
		if (nodes.getLength() != 1) {
			throw new RuntimeException("Expression " + xPath + " is supposed to have a single result, but has " + nodes.getLength());
		}
		
		return ((Element) nodes.item(0)).getAttribute("state");
	}

}
