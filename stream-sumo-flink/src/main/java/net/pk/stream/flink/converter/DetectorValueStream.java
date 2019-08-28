package net.pk.stream.flink.converter;

import java.io.File;

import javax.annotation.Nullable;

import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

import net.pk.stream.api.query.Querying;
import net.pk.stream.format.E1DetectorValue;

/**
 * This stream encapsulation is a set of flink stream jobs. It defines the
 * stream queries that are put on the incoming socket stream of sensor data.
 * First, it converts the incoming raw line separated text to value-objects
 * {@link E1DetectorValue}s. Then a set of filtering and grouping jobs reduce the
 * amount of data. In the end the output is written to a file (and printed on
 * the command line).
 * 
 * @see E1DetectorValue
 * @author peter
 *
 */
public class DetectorValueStream implements Querying {

	/**
	 * 
	 */
	public final static String DETECTOR_VALUE_FILE_PATH = System.getProperty("detectorvalue.filepath",
			System.getProperty("user.dir") + File.separator + "detector-value.csv");
	private String host;
	private int port;
	private final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
	@Nullable
	private DataStream<E1DetectorValue> stream;

	/**
	 * Constructor with socket host and port.
	 * 
	 * @param host of socket connection
	 * @param port of socket connection
	 */
	public DetectorValueStream(final String host, final int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void out() {
		filterStream();
		stream.writeAsText(DETECTOR_VALUE_FILE_PATH, WriteMode.OVERWRITE).setParallelism(1);
//		stream.print();

		try {
			env.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Query the socket stream.
	 */
	protected Querying filterStream() {
		DataStreamSource<String> streamSource = env.socketTextStream(host, port);
		DataStream<E1DetectorValue> detectorValuesAll = DetectorValueConverter.convert(streamSource);
		stream = detectorValuesAll //
				.keyBy("id").reduce((v1, v2) -> v1.getBegin() > v2.getBegin() ? v1 : v2) //
				/* .keyBy("begin") */.filter(v -> v.getOccupancy() > 0).timeWindowAll(Time.milliseconds(100)) //
				.reduce((v1, v2) -> v1.getOccupancy() > v2.getOccupancy() ? v1 : v2);
		return this;
	}

}
