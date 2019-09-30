package net.pk.traas.api.tracker.job;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import net.pk.stream.format.AbstractValue;

/**
 * @author peter
 *
 */
public interface AbstractJob {

	/**
	 * @return
	 * @throws Exception
	 */
	public <V extends AbstractValue> Collection<V> start()
			throws IOException, InterruptedException, ExecutionException;

}
