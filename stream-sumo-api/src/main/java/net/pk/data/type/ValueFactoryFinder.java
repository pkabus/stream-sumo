package net.pk.data.type;

/**
 * @author peter
 *
 */
public final class ValueFactoryFinder {

	/**
	 * @param <V>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V extends AbstractValue> ValueFromXmlFactory<V> createValueFactoryBy(Class<V> type) {
		if (E1DetectorValue.class.equals(type)) {
			return (ValueFromXmlFactory<V>) new E1DetectorValueFactory();
		}
		if (TLSValue.class.equals(type)) {
			return (ValueFromXmlFactory<V>) new TLSValueFactory();
		}

		throw new RuntimeException("No factory known for " + type);
	}
}
