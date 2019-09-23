package net.pk.traas.api.utils;

import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.api.E1DetectorValueToEdgeConverter;
import net.pk.traas.api.ToTlsConverter;

public class ToTlsConverterFinder {

	@SuppressWarnings("unchecked")
	public static <V extends AbstractValue> ToTlsConverter<V> findByType(Class<V> type) {
		if (E1DetectorValue.class.equals(type)) {
			return (ToTlsConverter<V>) new E1DetectorValueToEdgeConverter();
		}

		throw new RuntimeException("No ToTlsConverter defined for " + type);
	}

}
