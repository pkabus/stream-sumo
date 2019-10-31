package net.pk.data.type;

/**
 * This factory is responsible for the creation and parsing (xml) of
 * {@link E1DetectorValue} objects.
 * 
 * @author peter
 *
 */
public class EdgeValueFactory implements ValueFromXmlFactory<EdgeValue> {

	public EdgeValueFactory() {
		//
	}

	@Override
	public EdgeValue create() {
		return new EdgeValue();
	}
}
