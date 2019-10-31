package net.pk.stream.flink.function;

import java.util.List;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import net.pk.data.type.LaneValue;
import net.pk.data.type.LaneValueFactory;

/**
 * Convert function. Takes a xml stream and converts it to a {@link LaneValue}
 * typed stream.
 * 
 * @author peter
 *
 */
public class XmlToLaneValueFunction implements FlatMapFunction<String, LaneValue> {

	private final String appendix;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param appendix end tag
	 */
	public XmlToLaneValueFunction(final String appendix) {
		this.appendix = appendix;
	}

	@Override
	public void flatMap(String str, Collector<LaneValue> out) throws Exception {
		LaneValueFactory laneFac = new LaneValueFactory();
		List<LaneValue> objects = laneFac.parseXml(str + appendix);
		objects.forEach(o -> out.collect(o));
	}
}
