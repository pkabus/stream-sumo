package net.pk.stream.flink.function;

import org.apache.flink.api.common.functions.MapFunction;

import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.EdgeValue;
import net.pk.stream.api.query.E1DetectorValueToEdgeConverter;

/**
 * Mapper from {@link E1DetectorValue} to {@link EdgeValue}. The mapped edge is
 * the edge on which the detector is located.
 * 
 * @author peter
 *
 */
public class E1DetectorToEdgeValueMapper implements MapFunction<E1DetectorValue, EdgeValue> {

	/**
	 * serial id.
	 */
	private static final long serialVersionUID = -3229777269597303665L;

	@Override
	public EdgeValue map(E1DetectorValue value) throws Exception {
		EdgeValue e = new EdgeValue();
		e.set(EdgeValue.KEY_ID, new E1DetectorValueToEdgeConverter().apply(value).getId());
		e.set(EdgeValue.KEY_TIMESTAMP, String.valueOf(value.getEnd()));
		return e;
	}

}
