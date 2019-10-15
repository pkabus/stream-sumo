package net.pk.traas.builder.from.xml.network;

import java.io.File;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.xml.util.DocumentDelivery;

public class RefactorEdgesFromTo {

	public void build(final File file) {
		EnvironmentConfig conf = EnvironmentConfig.getInstance();

		Document document = DocumentDelivery.getDocument(file);
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
			String oldId = current.getAttribute("id");
			String from = current.getAttribute("from");
			String to = current.getAttribute("to");

			current.setAttribute("id", from + conf.getNodeSeparator() + to);
			// refactor all appearances in whole XML
			// also lanes like C3C4_0 -> C3_C4_0

		}
		throw new RuntimeException("Not yet implemented");
	}

}
