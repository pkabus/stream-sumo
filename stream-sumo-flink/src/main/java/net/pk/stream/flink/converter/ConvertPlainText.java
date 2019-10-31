package net.pk.stream.flink.converter;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.LaneValue;
import net.pk.data.type.TLSValue;
import net.pk.stream.flink.function.XmlToAbstractValueFunction;
import net.pk.stream.flink.function.XmlToLaneValueFunction;

/**
 * Converts a plain text stream to a {@link E1DetectorValue} stream.
 * 
 * @author peter
 *
 */
public class ConvertPlainText {

	/**
	 * Convert function. Takes a xml stream and converts it to a
	 * {@link E1DetectorValue} typed stream.
	 * 
	 * @param plainStream text stream
	 * @return object stream
	 */
	public static DataStream<E1DetectorValue> toE1DetectorStream(DataStream<String> plainStream) {
		// only allow records like '<interval[...]/>'
		return singleLineToType(plainStream, "interval")
				.flatMap(new XmlToAbstractValueFunction<E1DetectorValue>(E1DetectorValue.class))
				.returns(E1DetectorValue.class);
	}

	/**
	 * Convert function. Takes a xml stream and converts it to a {@link LaneValue}
	 * typed stream.
	 * 
	 * @param plainStream text stream
	 * @param appendix    the appendix which should be added to each record of the
	 *                    plainStream to complete it. Usually this is the
	 *                    delimitting end tag
	 * @return object stream
	 */
	public static DataStream<LaneValue> toLaneStream(DataStream<String> plainStream, String appendix) {
		return plainStream.flatMap(new XmlToLaneValueFunction(appendix));
	}

	/**
	 * Convert function. Takes a xml stream and converts it to a {@link TLSValue}
	 * typed stream.
	 * 
	 * @param plainStream xml stream
	 * @return object stream
	 */
	public static DataStream<TLSValue> toTLSStream(DataStream<String> plainStream) {
		// only allow records like '<tlsState[...]/>'
		return singleLineToType(plainStream, "tlsState")
				.flatMap(new XmlToAbstractValueFunction<TLSValue>(TLSValue.class)).returns(TLSValue.class);
	}

	private static SingleOutputStreamOperator<String> singleLineToType(DataStream<String> plainStream,
			final String xmlTag) {
		return plainStream.filter(s -> s.trim().contains("<" + xmlTag) && s.trim().endsWith("/>"));
	}
}
