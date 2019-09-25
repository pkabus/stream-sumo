package net.pk.traas.server.controller.update;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import net.pk.stream.format.EdgeValue;
import net.pk.traas.api.tracker.job.JobFinder;
import net.pk.traas.api.tracker.job.ReaderJob;

/**
 * @author peter
 *
 */
public class FileInputController extends InputController<EdgeValue> {

	private ConcurrentHashMap<String, EdgeValue> mostRecentValues;
	private ReaderJob<EdgeValue> readerJob;

	/**
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	public FileInputController(final Class<EdgeValue> type) {
		super(type);
		this.mostRecentValues = new ConcurrentHashMap<String, EdgeValue>();
		this.readerJob = (ReaderJob<EdgeValue>) JobFinder.getInstance().findJobByType(type);
	}

	@Override
	protected void update() {
		try {
			Collection<EdgeValue> fromFile = readerJob.readFromFile();
			fromFile.stream() //
					.filter(eVal -> eVal.greaterThan(mostRecentValues.get(eVal.getId()))) //
					.forEach(eVal -> mostRecentValues.put(eVal.getId(), eVal));
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
