package net.pk.stream.flink.function;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import net.pk.stream.flink.converter.PlainTextToStreamConverter;
import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.ValueFactoryFinder;

/**
 * {@link FlatMapFunction}, that maps a text line to a {@link E1DetectorValue}.
 * The function does not validate the correctness of the given value. This is
 * done in the corresponding converter, see {@link PlainTextToStreamConverter}.
 * 
 * @author peter
 *
 */
public class XmlToAbstractValueFunction<V extends AbstractValue> implements FlatMapFunction<String, V> {

	/**
	 * serial version uid.
	 */
	private static final long serialVersionUID = 5956741522880445619L;

	private Class<V> type;

	/**
	 * @param type
	 */
	public XmlToAbstractValueFunction(Class<V> type) {
		this.type = type;
	}

	@Override
	public void flatMap(String value, Collector<V> out) {
		final V val = ValueFactoryFinder.createValueFactoryBy(type).parseXml(value);
		if (val != null) {
			out.collect(val);
		}
	}

}
