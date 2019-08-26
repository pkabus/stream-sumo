package net.pk.stream.format;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.pk.stream.xml.util.DocumentDelivery;

/** This factory is responsible for the creation and parsing (xml) of {@link DetectorValue} objects.
 * @author peter
 *
 */
public class DetectorValueFactory implements Factory<DetectorValue> {

	private static DetectorValueFactory instance;

	private DetectorValueFactory() {
		//
	}

	/** Singleton class. Use this to get the only instance.
	 * @return factory instance
	 */
	public static DetectorValueFactory getInstance() {
		if (instance == null) {
			instance = new DetectorValueFactory();
		}

		return instance;
	}

	@Override
	public DetectorValue create() {
		return new DetectorValue();
	}

	/** Parse the given xml to a {@link DetectorValue} object. Warning: some attributes may not be set.
	 * @param str xml representation
	 * @return value object
	 */
	@Nullable
	public DetectorValue parseXml(@Nullable final String str) {
		if (str == null) {
			return null;
		}
		
		final DetectorValue detValue = this.create();
		Document document = null;
		try {
			document = new DocumentDelivery().convertDocument(str);
		} catch (IllegalArgumentException | SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		Node node = document.getFirstChild();
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			detValue.set(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
		}
		return detValue;
	}
}
