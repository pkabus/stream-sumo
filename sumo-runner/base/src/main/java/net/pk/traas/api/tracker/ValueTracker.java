package net.pk.traas.api.tracker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.list.SynchronizedList;
import org.apache.commons.io.input.ReversedLinesFileReader;

import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.ValueFromXmlFactory;

/**
 * This reader reads a stream text (xml-formatted) file and parses them to
 * {@link AbstractValue} objects.
 * 
 * @author peter
 *
 * @param <V> generic type extending AbstractValue
 */
public class ValueTracker<V extends AbstractValue> implements AbstractTracker {

	private File file;
	private List<V> registeredValues;
	private List<V> valueHistory;
	private ValueFromXmlFactory<V> factory;

	/**
	 * Constructs a file line reader.
	 */
	@SuppressWarnings("unchecked")
	public ValueTracker(final ValueFromXmlFactory<V> fac, final String filePath) {
		this.registeredValues = SynchronizedList.decorate(new ArrayList<V>());
		this.valueHistory = SynchronizedList.decorate(new ArrayList<V>());
		this.factory = fac;
		this.file = ValueTracker.getFile(filePath);
	}

	private static File getFile(final String filePath) {
		File file = new File(filePath);

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	/**
	 * Value factory according to the generic type of this class.
	 * 
	 * @return factory
	 */

	@Override
	public void readRecentDataFromFile() throws IOException {
		V value = null;
		boolean stop = false;
		ReversedLinesFileReader reversedLinesReader = new ReversedLinesFileReader(this.file);

		while (!stop) {
			String currentLine = reversedLinesReader.readLine();

			// start of file reached - break here
			if (currentLine == null) {
				break;
			}

			value = factory.parseXml(currentLine);

			// detectorValue not yet added to list? - add to list
			if (!getRegisteredValues().contains(value) && !getValueHistory().contains(value)) {
				getRegisteredValues().add(0, value);
			} else {
				stop = true;
			}

		}
		reversedLinesReader.close();
	}

	@SuppressWarnings("unchecked")
	public List<V> popAll() {
		List<V> list = new LinkedList<>();
		list.addAll(registeredValues);
		valueHistory.addAll(registeredValues);
		registeredValues.clear();
		return list;
	}

	/**
	 * Getter.
	 * 
	 * @return valid values
	 */
	public List<V> getRegisteredValues() {
		return registeredValues;
	}

	/**
	 * Getter.
	 * 
	 * @return obsolete values
	 */
	public List<V> getValueHistory() {
		return valueHistory;
	}
}
