package net.pk.stream.format;

/**
 * This factory is responsible for the creation and parsing (xml) of
 * {@link E1DetectorValue} objects.
 * 
 * @author peter
 *
 */
public class E1DetectorValueFactory implements ValueFromXmlFactory<E1DetectorValue> {

	public E1DetectorValueFactory() {
		//
	}

	@Override
	public E1DetectorValue create() {
		return new E1DetectorValue();
	}
}
