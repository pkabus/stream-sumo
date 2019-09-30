package net.pk.stream.flink.job;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * This abstraction is used to provide basic flink stream functionality and the
 * entrance point to run a flink job.
 * 
 * @author peter
 *
 */
public abstract class StreamJob {

	private final StreamExecutionEnvironment env;

	/**
	 * @param env
	 */
	public StreamJob(StreamExecutionEnvironment env) {
		this.env = env;
	}

	/**
	 * Returns the execution environment.
	 * 
	 * @return execution environment
	 */
	protected StreamExecutionEnvironment getEnv() {
		return env;
	}

	/**
	 * In this method the querying of the stream is defined. Only if this method is
	 * called, the {@link StreamExecutionEnvironment} will take this stream into
	 * account.
	 */
	public abstract void enable();

	/**
	 * This objects stream.
	 * 
	 * @return stream
	 */
	protected abstract DataStream<?> getStream();
}
