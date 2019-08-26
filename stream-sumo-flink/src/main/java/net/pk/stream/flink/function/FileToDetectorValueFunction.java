package net.pk.stream.flink.function;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import net.pk.stream.flink.converter.DetectorValueConverter;
import net.pk.stream.format.DetectorValue;
import net.pk.stream.format.DetectorValueFactory;

/**
 * {@link FlatMapFunction}, that maps a text line to a {@link DetectorValue}.
 * The function does not validate the correctness of the given value. This is
 * done in the corresponding converter, see {@link DetectorValueConverter}.
 * 
 * @author peter
 *
 */
public class FileToDetectorValueFunction implements FlatMapFunction<String, DetectorValue> {

	/**
	 * serial version uid.
	 */
	private static final long serialVersionUID = 5956741522880445619L;

	@Override
	public void flatMap(String value, Collector<DetectorValue> out) {
		final DetectorValue detValue = DetectorValueFactory.getInstance().parseXml(value);
		if (detValue != null) {
			out.collect(detValue);
		}
	}

}
