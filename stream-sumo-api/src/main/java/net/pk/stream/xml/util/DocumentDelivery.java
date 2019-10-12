package net.pk.stream.xml.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Creates an xml document from a given xml snippet.
 * 
 * @author peter
 *
 */
public class DocumentDelivery {

	/**
	 * Converts the given string to a document and returns it.
	 * 
	 * @param xmlSnippet to use
	 * @return the document filled by the given string
	 * @throws Exception that occur if the given string is not a valid xml snippet
	 */
	public static Document convertDocument(final String xmlSnippet)
			throws SAXException, IOException, IllegalArgumentException, ParserConfigurationException {
		Document document = convertStringToDocument(xmlSnippet);
		return document;
	}

	private static Document convertStringToDocument(final String xmlStr)
			throws SAXException, IOException, IllegalArgumentException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
		return doc;
	}

	public static void editElementInDom(final File file, final String parentTag, final String tag,
			final String attribute, final String value)
			throws ParserConfigurationException, IOException, SAXException, TransformerException {
		Document document = getDocument(file);

		Element parent = (Element) document.getElementsByTagName(parentTag).item(0);
		NodeList delayNodeList = document.getElementsByTagName(tag);
		Element item = null;
		if (delayNodeList.getLength() == 1) {
			item = (Element) delayNodeList.item(0);
		} else {
			item = document.createElement(tag);
			parent.appendChild(item);
		}

		item.setAttribute(attribute, value);

		// create the xml file
		// transform the DOM Object to an XML File
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(file);
		transformer.transform(domSource, streamResult);
	}

	/**
	 * @param file
	 * @return
	 */
	public static Document getDocument(final File file) {
		// Make an instance of the DocumentBuilderFactory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// use the factory to take an instance of the document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			// parse using the builder to get the DOM mapping of the
			// XML file
			return db.parse(file);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param path
	 * @return
	 */
	public static Document getDocument(Path path) {
		return getDocument(path.toFile());
	}

}
