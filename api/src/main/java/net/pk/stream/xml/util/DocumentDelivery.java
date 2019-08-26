package net.pk.stream.xml.util;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Creates an xml document from a given xml snippet.
 * 
 * @author peter
 *
 */
public class DocumentDelivery {

	private Document document;

	/**
	 * Converts the given string to a document and returns it.
	 * 
	 * @param xmlSnippet to use
	 * @return the document filled by the given string
	 * @throws Exception that occur if the given string is not a valid xml snippet
	 */
	public Document convertDocument(final String xmlSnippet)
			throws SAXException, IOException, IllegalArgumentException, ParserConfigurationException {
		document = convertStringToDocument(xmlSnippet);
		return document;
	}

	private Document convertStringToDocument(final String xmlStr)
			throws SAXException, IOException, IllegalArgumentException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
		return doc;
	}

}
