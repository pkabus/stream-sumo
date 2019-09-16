package net.pk.stream.format;

/**
 * This factory is responsible for the creation and parsing (xml) of
 * {@link E1DetectorValue} objects.
 * 
 * @author peter
 *
 */
public class TLSValueFactory implements ValueFromXmlFactory<TLSValue> {

	public TLSValueFactory() {
		//
	}

	@Override
	public TLSValue create() {
		return new TLSValue();
	}
}
