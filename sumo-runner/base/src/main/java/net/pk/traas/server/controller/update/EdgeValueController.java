package net.pk.traas.server.controller.update;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.EdgeValue;
import net.pk.traas.api.tracker.job.AbstractJob;
import net.pk.traas.api.tracker.job.ReaderJob;
import net.pk.traas.server.TLSKey;

/**
 * This file driven controller takes care of values of type {@link EdgeValue}.
 * 
 * @author peter
 *
 */
public class EdgeValueController extends FileInputController<EdgeValue> {

	private ConcurrentHashMap<TLSKey, EdgeValue> mostRecentValues;

	/**
	 * Constructor.
	 */
	public EdgeValueController() {
		super(EdgeValue.class);
		this.mostRecentValues = new ConcurrentHashMap<TLSKey, EdgeValue>();
	}

	@Override
	protected void update() {
		AbstractJob readerJob = new ReaderJob<>(getType());
		try {
			Collection<EdgeValue> fromFile = readerJob.start();
			fromFile.stream() //
					.filter(eVal -> eVal.greaterThan(mostRecentValues.get(TLSKey.findByEdgeId(eVal.getId())))) //
					.forEach(eVal -> mostRecentValues.put(TLSKey.findByEdgeId(eVal.getId()), eVal));
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the most recent {@link AbstractValue}s of the generic type {@link V}.
	 * 
	 * @return collection of most recent values
	 */
	public List<EdgeValue> getValues() {
		return new LinkedList<>(mostRecentValues.values());
	}

	/**
	 * Removes the given object from the map of most recent values.
	 * 
	 * @param v object to remove
	 */
	public void remove(final EdgeValue v) {
		this.mostRecentValues.remove(TLSKey.findByEdgeId(v.getId()));
	}

}
