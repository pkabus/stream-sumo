package net.pk.traas.server.controller.update;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pk.data.type.AbstractValue;
import net.pk.data.type.EdgeValue;
import net.pk.data.type.TLSKey;
import net.pk.stream.xml.util.TLSFinder;
import net.pk.traas.api.tracker.job.AbstractJob;
import net.pk.traas.api.tracker.job.ReaderJob;

/**
 * This file driven controller takes care of values of type {@link EdgeValue}.
 * 
 * @author peter
 *
 */
public class EdgeValueController extends FileInputController<EdgeValue> {

	private TLSFinder tlsFinder;
	private Logger log;
	private ConcurrentHashMap<TLSKey, EdgeValue> mostRecentValues;

	/**
	 * Constructor.
	 */
	public EdgeValueController() {
		super(EdgeValue.class);
		this.mostRecentValues = new ConcurrentHashMap<TLSKey, EdgeValue>();
		this.log = LoggerFactory.getLogger(getClass());
		this.tlsFinder = TLSFinder.getInstance();
	}

	@Override
	protected void update() {
		AbstractJob readerJob = new ReaderJob<>(getType());
		try {
			Collection<EdgeValue> fromFile = readerJob.start();
			fromFile.stream() //
					.filter(eVal -> eVal.greaterThan(mostRecentValues.get(tlsFinder.bySumoEdgeId(eVal.getId())))) //
					.forEach(eVal -> mostRecentValues.put(tlsFinder.bySumoEdgeId(eVal.getId()), eVal));
		} catch (IOException | InterruptedException | ExecutionException e) {
			this.log.error("ERR in update()", e);
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
		this.mostRecentValues.remove(tlsFinder.bySumoEdgeId(v.getId()));
	}

}
