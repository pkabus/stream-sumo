package net.pk.stream.format;

import java.io.IOException;

import javax.annotation.Nullable;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.pk.stream.xml.util.DocumentDelivery;

/**
 * This interface extends the {@link Factory} class to a factory that is able to
 * parse xml to create an {@link AbstractValue}.
 * 
 * @author peter
 *
 * @param <V> generic type that is created by the factory
 */
public interface ValueFromXmlFactory<V extends AbstractValue> extends Factory<V> {

	/**
	 * Parse the given xml to a {@link E1DetectorValue} object. Warning: some
	 * attributes may not be set.
	 * 
	 * @param str xml representation
	 * @return value object
	 */
	@Nullable
	default V parseXml(@Nullable final String str) {
		if (str == null) {
			return null;
		}

		final V detValue = this.create();
		Document document = null;
		try {
			document = DocumentDelivery.convertDocument(str);
		} catch (IllegalArgumentException | SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Node node = document.getFirstChild();
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			detValue.set(attributes.item(i).getNodeName(), attributes.item(i).getNodeValue());
		}
		return detValue;
	}
}
