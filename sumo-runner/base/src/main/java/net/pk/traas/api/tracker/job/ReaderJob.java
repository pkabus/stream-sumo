package net.pk.traas.api.tracker.job;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import net.pk.stream.format.AbstractValue;
import net.pk.traas.api.tracker.AbstractTracker;

/**
 * This abstract reader job runs a future task in a new thread.
 * 
 * @author peter
 *
 * @param <V>
 */
public class ReaderJob<V extends AbstractValue> implements AbstractJob {

	private FutureTask<Collection<V>> task;
	private AbstractTracker reader;

	/**
	 * Constructor.
	 * 
	 * @param readFrom file path
	 * @param reader   to use
	 */
	public ReaderJob(final AbstractTracker reader) {
		this.task = new FutureTask<Collection<V>>(new ReaderCallable());
		this.reader = reader;
	}

	/**
	 * Update job with return value.
	 * 
	 * @return most recent file lines parsed to {@link V} objects.
	 * @throws IOException          io exception
	 * @throws InterruptedException interrupted exception
	 * @throws ExecutionException   execution exception
	 */
	@SuppressWarnings("unchecked")
	public Collection<V> readFromFile() throws IOException, InterruptedException, ExecutionException {
		Thread t = new Thread(task, "FileReader-FutureTask");
		t.start();
		return task.get();
	}

	private class ReaderCallable implements Callable<Collection<V>> {

		@Override
		public Collection<V> call() {
			try {
				reader.readRecentDataFromFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return reader.popAll();
		}
	}

}
