package net.pk.stream.xml.util;

import java.nio.file.Paths;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.format.LaneInformation;

/**
 * @author peter
 *
 */
public class LaneUtil {

	/**
	 * @param id
	 * @return
	 */
	public static LaneInformation by(final String id) {
		EnvironmentConfig conf = EnvironmentConfig.getInstance();
		Document netDocument = DocumentDelivery.getDocument(Paths.get(conf.getConfigFileDir(), conf.getNetworkFile()));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList) xPath.evaluate("//lane[@id='" + id + "']", netDocument, XPathConstants.NODESET);
			Element laneElement = (Element) nodes.item(0);
			return new LaneInformation(laneElement.getAttribute("id"), laneElement.getAttribute("index"), laneElement.getAttribute("speed"), laneElement.getAttribute("length"));
		} catch (XPathExpressionException e) {
			throw new RuntimeException("XPath evaluation failed: ", e);
		}
	}
}
