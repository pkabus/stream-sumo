package net.pk.stream.flink.job;

import javax.annotation.Nullable;

import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import net.pk.stream.api.file.ValueFilePaths;
import net.pk.stream.api.query.Querying;
import net.pk.stream.flink.converter.DetectorValueConverter;
import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;

/**
 * This stream encapsulation is a set of flink stream jobs. It defines the
 * stream queries that are put on the incoming socket stream of sensor data.
 * First, it converts the incoming raw line separated text to value-objects
 * {@link E1DetectorValue}s. Then a set of filtering and grouping jobs reduce
 * the amount of data. In the end the output is written to a file (and printed
 * on the command line).
 * 
 * @see E1DetectorValue
 * @author peter
 *
 */
public class E1DetectorValueStream extends WindowedStreamJob implements Querying {

	private String host;
	private int port;
	@Nullable
	private DataStream<E1DetectorValue> stream;

	/**
	 * Constructor with socket host and port.
	 * 
	 * @param host of socket connection
	 * @param port of socket connection
	 */
	public E1DetectorValueStream(final String host, final int port) {
		super();
		this.host = host;
		this.port = port;
	}

	/**
	 * Constructor with socket host and port.
	 * 
	 * @param host of socket connection
	 * @param port of socket connection
	 */
	public E1DetectorValueStream(final String host, final int port, Time window) {
		super(window);
		this.host = host;
		this.port = port;
	}

	@Override
	public void out() {
		filterStream();
		stream.writeAsText(ValueFilePaths.getPathE1DetectorValue(), WriteMode.OVERWRITE).setParallelism(1);

		try {
			getEnv().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Query the socket stream. First, the stream is separated by the
	 * {@link AbstractValue#getId()}, then these streams are reduced so that only
	 * the most occupied values of each {@link KeyedStream} in the defined
	 * {@link TimeWindow} are returned.
	 */
	protected Querying filterStream() {
		DataStreamSource<String> streamSource = getEnv().socketTextStream(host, port);
		DataStream<E1DetectorValue> detectorValuesAll = DetectorValueConverter.convert(streamSource);
		stream = detectorValuesAll //
				.keyBy("id").reduce((v1, v2) -> v1.getBegin() > v2.getBegin() ? v1 : v2) //
				/* .keyBy("begin") */.filter(v -> v.getOccupancy() > 0).timeWindowAll(getWindow()) //
				.reduce((v1, v2) -> v1.getOccupancy() > v2.getOccupancy() ? v1 : v2);
		return this;
	}

}
