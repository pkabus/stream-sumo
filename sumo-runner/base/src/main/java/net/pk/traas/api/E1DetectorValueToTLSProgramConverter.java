package net.pk.traas.api;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.pk.stream.format.E1DetectorValue;

/**
 * @author peter
 *
 */
public class E1DetectorValueToTLSProgramConverter implements Function<E1DetectorValue, String> {

	@Override
	public String apply(E1DetectorValue t) {
		List<String> splitByUnderline = Arrays.asList(t.getId().split("_"));
		if (splitByUnderline.size() < 2) {
			throw new RuntimeException("Invalid string format " + t.getId()); 
		}
		
		return splitByUnderline.get(1) + "_" + splitByUnderline.get(2);
	}

}
