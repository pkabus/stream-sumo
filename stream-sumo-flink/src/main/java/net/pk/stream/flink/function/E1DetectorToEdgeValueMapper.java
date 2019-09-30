package net.pk.stream.flink.function;

import org.apache.flink.api.common.functions.MapFunction;

import net.pk.stream.api.query.E1DetectorValueToEdgeConverter;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.EdgeValue;

/**
 * @author peter
 *
 */
public class E1DetectorToEdgeValueMapper implements MapFunction<E1DetectorValue, EdgeValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3229777269597303665L;

	@Override
	public EdgeValue map(E1DetectorValue value) throws Exception {
		EdgeValue e = new EdgeValue();
		e.set(EdgeValue.KEY_ID, new E1DetectorValueToEdgeConverter().apply(value));
		e.set(EdgeValue.KEY_TIMESTAMP, String.valueOf(value.getEnd()));
		return e;
	}

}
