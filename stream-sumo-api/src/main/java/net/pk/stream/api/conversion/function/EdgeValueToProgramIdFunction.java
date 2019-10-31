package net.pk.stream.api.conversion.function;

import java.util.function.Function;

import net.pk.data.type.EdgeValue;
import net.pk.stream.xml.util.TLSManager;

/** This function defines how an {@link EdgeValue} is translated in a program ID.
 * @see TLSManager
 * @author peter
 *
 */
public class EdgeValueToProgramIdFunction implements Function<EdgeValue, String> {

	@Override
	public String apply(EdgeValue v) {
		return v.getId();
	}

}
