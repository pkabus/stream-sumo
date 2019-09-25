package net.pk.stream.flink.job;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * This windowed stream job uses a {@link Time} window for its stream job. The
 * time window is set in the constructor.
 * 
 * @author peter
 *
 */
public abstract class WindowedStreamJob extends StreamJob {

	private static final int DEFAULT_WINDOW_MILLIS = 100;
	private static final int DEFAULT_SLIDE_MILLIS = 20;

	/**
	 * Default time window.
	 */
	public final static Time DEFAULT_TIME_WINDOW = EnvironmentConfig.getInstance().runHeadless()
			? Time.milliseconds(DEFAULT_WINDOW_MILLIS)
			: Time.milliseconds(EnvironmentConfig.getInstance().getTimestepDelay() * DEFAULT_WINDOW_MILLIS);

	/**
	 * Default time slide.
	 */
	public final static Time DEFAULT_TIME_SLIDE = EnvironmentConfig.getInstance().runHeadless()
			? Time.milliseconds(DEFAULT_SLIDE_MILLIS)
			: Time.milliseconds(EnvironmentConfig.getInstance().getTimestepDelay() * DEFAULT_SLIDE_MILLIS);

	private Time window;
	private Time slide;

	/**
	 * Uses the default time window (100 milliseconds) for this stream job.
	 */
	public WindowedStreamJob(final StreamExecutionEnvironment env) {
		super(env);
		this.window = DEFAULT_TIME_WINDOW;
		this.slide = DEFAULT_TIME_SLIDE;
	}

	/**
	 * Constructor. Uses the given time window for this stream job.
	 * 
	 * @param window to use
	 */
	public WindowedStreamJob(final StreamExecutionEnvironment env, final Time window, final Time slide) {
		super(env);
		this.window = window;
		this.slide = slide;
	}

	/**
	 * Getter.
	 * 
	 * @return the time window to use
	 */
	public Time getWindow() {
		return window;
	}

	/**
	 * Setter.
	 * 
	 * @param window to set
	 */
	public void setWindow(Time window) {
		this.window = window;
	}

	/**
	 * Getter.
	 * 
	 * @return the time slide to use
	 */
	public Time getSlide() {
		return slide;
	}

	/**
	 * Setter.
	 * 
	 * @param slide to set
	 */
	public void setSlide(Time slide) {
		this.slide = slide;
	}

}
