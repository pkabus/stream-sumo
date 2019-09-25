package net.pk.traas.api.utils;

import net.pk.stream.api.query.E1DetectorValueToEdgeConverter;
import net.pk.stream.api.query.ToEdgeConverter;
import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;

/**
 * Finder class that returns a {@link ToEdgeConverter} according to the given
 * type.
 * 
 * @author peter
 *
 */
public class ToTlsConverterFinder {

	@SuppressWarnings("unchecked")
	public static <V extends AbstractValue> ToEdgeConverter<V> findByType(Class<V> type) {
		if (E1DetectorValue.class.equals(type)) {
			return (ToEdgeConverter<V>) new E1DetectorValueToEdgeConverter();
		}

		throw new RuntimeException("No ToTlsConverter defined for " + type);
	}

}
