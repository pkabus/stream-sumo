package net.pk.stream.flink.job;

import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * This windowed stream job uses a {@link Time} window for its stream job. The
 * time window is set in the constructor.
 * 
 * @author peter
 *
 */
public abstract class WindowedStreamJob extends StreamJob {

	/**
	 * Default time window.
	 */
	public final static Time DEFAULT_TIME_WINDOW = Time.milliseconds(100);

	private Time window;

	/**
	 * Uses the default time window (100 milliseconds) for this stream job.
	 */
	public WindowedStreamJob() {
		this.window = DEFAULT_TIME_WINDOW;
	}

	/**
	 * Constructor. Uses the given time window for this stream job.
	 * 
	 * @param window to use
	 */
	public WindowedStreamJob(final Time window) {
		this.window = window;
	}

	/**
	 * Getter.
	 * 
	 * @return the time window to use
	 */
	public Time getWindow() {
		return window;
	}
}
