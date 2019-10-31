package net.pk.stream.flink.job;

import javax.annotation.Nullable;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.LaneValue;
import net.pk.stream.flink.converter.ConvertPlainText;

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
public class LaneValueStream extends WindowedStreamJob {

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
	public void enable() {
		DataStreamSource<String> streamSource = getEnv().socketTextStream(host, port, DELIMITER).setParallelism(1);
		DataStream<LaneValue> laneValuesAll = ConvertPlainText.toLaneStream(streamSource, DELIMITER);
		stream = laneValuesAll.filter(v -> v.getTLS() != null) //
				.keyBy(new TLSSelector<LaneValue>())
				.timeWindow(Time.seconds(4), Time.milliseconds(500)) //
				.reduce((v1, v2) -> v1.getPosDistrib() > v2.getPosDistrib() + LaneValue.EPSILON ? v1 : v2);
		stream.print();
	}

	@Override
	protected DataStream<?> getStream() {
		return this.stream;
	}
}
