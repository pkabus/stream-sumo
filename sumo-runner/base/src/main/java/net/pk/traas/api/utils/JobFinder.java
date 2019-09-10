package net.pk.traas.api.utils;

import javax.annotation.Nonnull;

import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.api.tracker.job.AbstractJob;
import net.pk.traas.api.tracker.job.ReaderJob;

/**
 * Helper factory, that is able to create {@link AbstractJob}s dependent from a
 * specific sub-type of {@link AbstractValue}.
 * 
 * @author peter
 *
 */
public final class JobFinder {
	
	private TrackerFinder trackerFinder;
	
	/**
	 * Constructor.
	 */
	public JobFinder() {
		this.trackerFinder = new TrackerFinder();
	}

	/**
	 * Create reader job according to the given type.
	 * 
	 * @param <V>  generic type information
	 * @param type of the wanted job
	 * @return abstract job related to given type
	 */
	public <V extends AbstractValue> AbstractJob findJobByType(@Nonnull final Class<V> type) {
		if (type.equals(E1DetectorValue.class)) {
			return new ReaderJob<V>(trackerFinder.findByType(type));
		}

		// add readerJob constructions

		throw new RuntimeException("No " + AbstractJob.class + " for type " + type + "found.");
	}

}
