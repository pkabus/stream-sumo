package net.pk.traas.api;

import java.util.Arrays;
import java.util.List;

import net.pk.stream.format.E1DetectorValue;

/**
 * @author peter
 *
 */
public class E1DetectorValueToEdgeConverter implements ToTlsConverter<E1DetectorValue> {

	@Override
	public String apply(E1DetectorValue v) {
		String separator = EnvironmentConfig.getInstance().getSeparator();
		List<String> splitByUnderline = Arrays.asList(v.getId().split(separator));

		if (splitByUnderline.size() == 3) {
			// e.g. e1det_A3A4_0
			return splitByUnderline.get(1);
		}

		if (splitByUnderline.size() == 4) {
			// e.g. e1det_n0_n1_0
			return splitByUnderline.get(1) + separator + splitByUnderline.get(2);
		}

		throw new RuntimeException("Invalid id format " + v.getId());
	}

}
