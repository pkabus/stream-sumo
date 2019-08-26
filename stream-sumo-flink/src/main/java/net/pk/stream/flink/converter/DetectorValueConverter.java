package net.pk.stream.flink.converter;

import org.apache.flink.streaming.api.datastream.DataStream;

import net.pk.stream.flink.function.FileToDetectorValueFunction;
import net.pk.stream.format.DetectorValue;

/** Converts a plain text stream to a {@link DetectorValue} stream.
 * @author peter
 *
 */
public class DetectorValueConverter {

	/** Convert function. 
	 * @param plainStream text stream
	 * @return object stream
	 */
	public static DataStream<DetectorValue> convert(DataStream<String> plainStream) {
		return plainStream.filter(s -> {
			// only allow records like '<interval[...]/>'
			return s.trim().startsWith("<interval") && s.trim().endsWith("/>");
		}).flatMap(new FileToDetectorValueFunction());
	}
}
