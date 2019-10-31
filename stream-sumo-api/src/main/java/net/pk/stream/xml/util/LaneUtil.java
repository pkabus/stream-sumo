package net.pk.stream.xml.util;

import java.nio.file.Paths;

import javax.annotation.Nullable;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.pk.data.type.LaneInformation;
import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * Utility that creates {@link LaneInformation} objects using the sumo network
 * xml file.
 * 
 * @author peter
 *
 */
public class LaneUtil {

	/**
	 * Create {@link LaneInformation} according to the given id. Returns null, if no
	 * lane is found with the given id.
	 * 
	 * @param id of {@link LaneInformation}
	 * @return {@link LaneInformation} object or null
	 */
	@Nullable
	public static LaneInformation by(final String id) {
		EnvironmentConfig conf = EnvironmentConfig.getInstance();
		Document netDocument = DocumentDelivery.getDocument(Paths.get(conf.getAbsolutePathNetworkFile()));
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			nodes = (NodeList) xPath.evaluate("//lane[@id='" + id + "']", netDocument, XPathConstants.NODESET);

			if (nodes.getLength() == 0) {
				return null;
			}

			Element laneElement = (Element) nodes.item(0);
			return new LaneInformation(laneElement.getAttribute("id"), laneElement.getAttribute("index"),
					laneElement.getAttribute("speed"), laneElement.getAttribute("length"));
		} catch (XPathExpressionException e) {
			throw new RuntimeException("XPath evaluation failed: ", e);
		}
	}
}
