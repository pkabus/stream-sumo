package net.pk.traas.api.utils;

import java.util.HashMap;
import java.util.Map;

import net.pk.data.type.AbstractValue;
import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.E1DetectorValueFactory;
import net.pk.data.type.EdgeValue;
import net.pk.data.type.EdgeValueFactory;
import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.traas.api.tracker.AbstractTracker;
import net.pk.traas.api.tracker.ValueTracker;

/**
 * This helper class stores all the tracker that are used in the simulation
 * context. New trackers are registered right here in the constructor.
 * 
 * @author peter
 *
 */
public final class TrackerFinder {

	private static TrackerFinder instance;
	private Map<Class<? extends AbstractValue>, AbstractTracker> trackerStorage;

	/**
	 * Constructs new trackers according to the known {@link AbstractValue} types.
	 * Each tracker instance observes a file for changes.
	 */
	private TrackerFinder() {
		EnvironmentConfig envConfig = EnvironmentConfig.getInstance();
		this.trackerStorage = new HashMap<>();
		trackerStorage.put(E1DetectorValue.class, new ValueTracker<E1DetectorValue>(new E1DetectorValueFactory(),
				envConfig.getAbsoluteFilePathE1DetectorValue()));
		trackerStorage.put(EdgeValue.class,
				new ValueTracker<EdgeValue>(new EdgeValueFactory(), envConfig.getAbsoluteFilePathEdgeValue()));
		// add tracker instances
	}

	/**
	 * Get (and create if necessary) singleton instance.
	 * 
	 * @return instance
	 */
	public static TrackerFinder getInstance() {
		if (instance == null) {
			instance = new TrackerFinder();
		}

		return instance;
	}

	/**
	 * Returns the {@link AbstractTracker} that is responsible for the given type.
	 * 
	 * @param <V>  type
	 * @param type of tracker
	 * @return tracker belonging to type
	 */
	public <V extends AbstractValue> AbstractTracker findByType(final Class<V> type) {
		AbstractTracker tracker = trackerStorage.get(type);

		if (tracker == null) {
			throw new IllegalArgumentException("No Tracker found with type " + type);
		}

		return tracker;
	}
}
