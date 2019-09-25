package net.pk.traas.server.controller.update;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import net.pk.stream.format.AbstractValue;
import net.pk.traas.api.tracker.job.AbstractJob;
import net.pk.traas.api.tracker.job.JobFinder;

/**
 * This generic update controller is updating and providing the most recent
 * values of the generic type. The type is bound to {@link AbstractValue}.
 * 
 * @author peter
 *
 * @param <V> generic value type
 */
public class SimpleInputController<V extends AbstractValue> extends InputController<V> {

	private ConcurrentHashMap<String, V> mostRecentValues;

	private JobFinder jobFinder;
	
	/**
	 * Constructs a update controller having the given generic type.
	 * 
	 * @param type of the update controller/process
	 */
	public SimpleInputController(final Class<V> type) {
		super(type);
		this.mostRecentValues = new ConcurrentHashMap<String, V>();
		this.jobFinder = JobFinder.getInstance();
	}

	@Override
	public void update() {
		AbstractJob readerJob = jobFinder.findJobByType(getType());
		Collection<V> data = null;
		try {
			data = readerJob.readFromFile();
			this.merge(data);
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Merge input data with stored data. Values that do not yet exist in the map
	 * are put in the map, objects that are already existing are only stored if they
	 * are more recent then the already existing entry. The key of the {@link V} is
	 * always its id.
	 * 
	 * @param input merge into map
	 */
	protected void merge(final @Nonnull Collection<V> input) {
		input.stream().forEach(val -> {
			String id = val.getId();
			V byIdInMap = mostRecentValues.get(id);
			if (val.greaterThan(byIdInMap)) {
				mostRecentValues.put(id, val);
			}
		});
	}

	/**
	 * Returns the most recent {@link AbstractValue}s of the generic type {@link V}.
	 * 
	 * @return collection of most recent values
	 */
	@SuppressWarnings("unchecked")
	public List<V> getValues() {
		Object out = new HashMap<String, V>(this.mostRecentValues);
		return new LinkedList<>(((HashMap<String, V>) out).values());
	}


	/**
	 * Removes the given object from the map of most recent values.
	 * 
	 * @param v object to remove
	 */
	public void remove(final V v) {
		this.mostRecentValues.remove(v.getId());
	}
}
