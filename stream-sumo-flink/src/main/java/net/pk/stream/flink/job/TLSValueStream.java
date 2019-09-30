package net.pk.stream.flink.job;

import javax.annotation.Nullable;

import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.flink.converter.ConvertPlainText;
import net.pk.stream.format.TLSValue;

/**
 * @author peter
 *
 */
public class TLSValueStream extends StreamJob {

	private String host;
	private int port;
	@Nullable
	private DataStream<TLSValue> stream;

	/**
	 * @param host
	 * @param port
	 */
	public TLSValueStream(final String host, final int port, final StreamExecutionEnvironment env) {
		super(env);
		this.host = host;
		this.port = port;
	}

	@Override
	public void enable() {
		DataStreamSource<String> streamSource = getEnv().socketTextStream(host, port);
		DataStream<TLSValue> s = ConvertPlainText.toTLSStream(streamSource);

		this.stream = s.keyBy("id").reduce((v1, v2) -> v1.getTime() > v2.getTime() ? v1 : v2)
				.timeWindowAll(Time.seconds(2)).max("time");
		this.stream.writeAsText(EnvironmentConfig.getInstance().getAbsoluteFilePathTLSValue(), WriteMode.OVERWRITE).setParallelism(1);

	}

	@Override
	protected DataStream<?> getStream() {
		return this.stream;
	}

}
