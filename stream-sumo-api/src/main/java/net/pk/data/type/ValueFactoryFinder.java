package net.pk.data.type;

/**
 * @author peter
 *
 */
public final class ValueFactoryFinder {

	private static E1DetectorValueFactory e1DetectorValueFactory = new E1DetectorValueFactory();
	private static TLSValueFactory tlsValueFactory = new TLSValueFactory();

	/**
	 * @param <V>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <V extends AbstractValue> ValueFromXmlFactory<V> createValueFactoryBy(Class<V> type) {
		if (E1DetectorValue.class.equals(type)) {
			return (ValueFromXmlFactory<V>) e1DetectorValueFactory;
		}
		if (TLSValue.class.equals(type)) {
			return (ValueFromXmlFactory<V>) tlsValueFactory;
		}

		throw new RuntimeException("No factory known for " + type);
	}
}
