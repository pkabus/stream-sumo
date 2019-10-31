package net.pk.stream.api.query;

import org.apache.commons.lang3.StringUtils;

import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.SumoEdge;
import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.xml.util.EdgeFinder;

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
	public SumoEdge apply(final E1DetectorValue v) {
		String separator = EnvironmentConfig.getInstance().getSeparator();

		// turns
		// e1det_26704628-cluster_1910176242_251106770_26938227_4911322574_654381302_0
		// into
		// 26704628-cluster_1910176242_251106770_26938227_4911322574_654381302
		String substringAfterFirstSeparator = StringUtils.substringAfter(v.getId(), separator);
		String substringAfterFirstAndBeforeLastSeparator = StringUtils.substringBeforeLast(substringAfterFirstSeparator,
				separator);

		SumoEdge edge = EdgeFinder.getInstance().byId(substringAfterFirstAndBeforeLastSeparator);
		if (edge != null) {
			return edge;
		}

		throw new RuntimeException(
				"No edge found by id " + substringAfterFirstAndBeforeLastSeparator + " for E1Detector " + v.getId());
	}

}
