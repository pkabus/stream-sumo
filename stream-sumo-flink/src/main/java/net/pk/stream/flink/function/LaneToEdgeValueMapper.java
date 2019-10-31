package net.pk.stream.flink.function;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;

import net.pk.data.type.EdgeValue;
import net.pk.data.type.LaneValue;

/**
 * @author peter
 *
 */
public class LaneToEdgeValueMapper implements MapFunction<LaneValue, EdgeValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3229777269597303665L;

	@Override
	public EdgeValue map(LaneValue value) throws Exception {
		EdgeValue e = new EdgeValue();
		e.set(EdgeValue.KEY_ID, StringUtils.substringBeforeLast(value.getId(), "_"));
		e.set(EdgeValue.KEY_TIMESTAMP, String.valueOf(value.getTimestamp()));
		return e;
	}

}
