package net.pk.stream.api.query;

import java.util.Arrays;
import java.util.List;

import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.format.E1DetectorValue;

/**
 * Converts the id of a {@link E1DetectorValue} to the id of an edge (in the
 * running sumo network). Convention: id of {@link E1DetectorValue} contains a
 * prefix, the id of an edge and the lane number. These parts are separated by a
 * separator which is retrievable from {@link EnvironmentConfig#getSeparator()}.
 * 
 * @author peter
 *
 */
public class E1DetectorValueToEdgeConverter implements ToEdgeConverter<E1DetectorValue> {

	@Override
	public String apply(final E1DetectorValue v) {
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
