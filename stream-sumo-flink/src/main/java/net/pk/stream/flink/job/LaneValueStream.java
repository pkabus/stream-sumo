package net.pk.stream.flink.job;

import javax.annotation.Nullable;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import net.pk.stream.api.query.Querying;
import net.pk.stream.flink.converter.ConvertPlainText;
import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.LaneValue;

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
public class LaneValueStream extends WindowedStreamJob implements Querying {

	public static final String DELIMITER = "</timestep>";

	private String host;
	private int port;
	@Nullable
	private DataStream<LaneValue> stream;

	/**
	 * Constructor with socket host and port.
	 * 
	 * @param host of socket connection
	 * @param port of socket connection
	 */
	public LaneValueStream(final String host, final int port, final StreamExecutionEnvironment env) {
		super(env);
		this.host = host;
		this.port = port;
	}

	/**
	 * Constructor with socket host and port.
	 * 
	 * @param host of socket connection
	 * @param port of socket connection
	 */
	public LaneValueStream(final String host, final int port, final StreamExecutionEnvironment env, final Time window,
			final Time slide) {
		super(env, window, slide);
		this.host = host;
		this.port = port;
	}

	@Override
	public void out() {
		filterStream();
//		stream.writeAsText(EnvironmentConfig.getInstance().getAbsoluteFilePathLaneValue(), WriteMode.OVERWRITE)
//				.setParallelism(1);
	}

	/**
	 * Query the socket stream. First, the stream is separated by the
	 * {@link AbstractValue#getId()}, then these streams are reduced so that only
	 * the most occupied values of each {@link KeyedStream} in the defined
	 * {@link TimeWindow} are returned.
	 */
	protected Querying filterStream() {
		DataStreamSource<String> streamSource = getEnv().socketTextStream(host, port, DELIMITER).setParallelism(1);
		DataStream<LaneValue> laneValuesAll = ConvertPlainText.toLaneStream(streamSource, DELIMITER);
		stream = laneValuesAll;
		return this;
	}

	@Override
	protected DataStream<?> getStream() {
		return this.stream;
	}
}
