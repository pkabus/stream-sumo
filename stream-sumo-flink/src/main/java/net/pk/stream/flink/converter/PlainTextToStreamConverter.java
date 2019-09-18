package net.pk.stream.flink.converter;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

import net.pk.stream.flink.function.XmlToAbstractValueFunction;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.TLSValue;

/**
 * Converts a plain text stream to a {@link E1DetectorValue} stream.
 * 
 * @author peter
 *
 */
public class PlainTextToStreamConverter {

	/**
	 * Convert function. Takes a xml stream and converts it to a
	 * {@link E1DetectorValue} typed stream.
	 * 
	 * @param plainStream text stream
	 * @return object stream
	 */
	public static DataStream<E1DetectorValue> convertXmlToE1DetectorValueStream(DataStream<String> plainStream) {
		// only allow records like '<interval[...]/>'
		return convertByType(plainStream, "interval")
				.flatMap(new XmlToAbstractValueFunction<E1DetectorValue>(E1DetectorValue.class))
				.returns(E1DetectorValue.class);
	}

	/**
	 * Convert function. Takes a xml stream and converts it to a {@link TLSValue}
	 * typed stream.
	 * 
	 * @param plainStream xml stream
	 * @return object stream
	 */
	public static DataStream<TLSValue> convertXmlToTLSValueStream(DataStream<String> plainStream) {
		// only allow records like '<tlsState[...]/>'
		return convertByType(plainStream, "tlsState").flatMap(new XmlToAbstractValueFunction<TLSValue>(TLSValue.class))
				.returns(TLSValue.class);
	}

	private static SingleOutputStreamOperator<String> convertByType(DataStream<String> plainStream,
			final String xmlTag) {
		return plainStream.filter(s -> s.trim().contains("<" + xmlTag) && s.trim().endsWith("/>"));
	}
}
