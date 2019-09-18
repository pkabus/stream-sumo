package net.pk.stream.flink.job;

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

}
