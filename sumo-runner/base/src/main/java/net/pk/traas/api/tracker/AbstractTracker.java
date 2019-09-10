package net.pk.traas.api.tracker;

import java.io.IOException;
import java.util.List;

import net.pk.stream.format.AbstractValue;

/**
 * Abstraction layer for reader implementations.
 * 
 * @author peter
 *
 */
public interface AbstractTracker {

	/**
	 * Read most recent data from the given file path. New values are stored in this
	 * object.
	 * 
	 * @throws IOException may occur when accessing the file
	 */
	void readRecentDataFromFile() throws IOException;

	/**
	 * Return a list of valid (not yet known) values. Furthermore, this method
	 * stores all values that have ever been popped from here. This is done so that
	 * the reader does not return the same object twice.
	 * 
	 * @return list of registered and valid values
	 */
	<V extends AbstractValue> List<V> popAll();
}
