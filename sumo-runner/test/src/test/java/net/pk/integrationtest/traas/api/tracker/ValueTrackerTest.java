package net.pk.integrationtest.traas.api.tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.E1DetectorValueFactory;
import net.pk.integrationtest.traas.api.UpdateFileIntegrationTest;
import net.pk.traas.api.tracker.ValueTracker;

/**
 * Tests the class {@link ValueTracker}.
 * 
 * @author peter
 *
 */
@Disabled
public class ValueTrackerTest extends UpdateFileIntegrationTest {

	/**
	 * Read complete file and expect that all entries are there before they are
	 * popped from the tracker.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdateFull() throws IOException {
		ValueTracker<E1DetectorValue> e1DetectorValueTracker = loadTestFileToTracker(TEST_FILE_FULL);
		e1DetectorValueTracker.readRecentDataFromFile();

		assertEquals(0, e1DetectorValueTracker.getValueHistory().size());
		assertEquals(438, e1DetectorValueTracker.popAll().size());
		assertEquals(438, e1DetectorValueTracker.getValueHistory().size());
	}

	/**
	 * Read part-0 and expect that all 13 entries are stored in the tracker.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPart0() throws IOException {
		ValueTracker<E1DetectorValue> e1DetectorValueTracker = loadTestFileToTracker(TEST_FILE_PART_0);
		e1DetectorValueTracker.readRecentDataFromFile();

		assertEquals(13, e1DetectorValueTracker.getRegisteredValues().size());
		assertEquals(0, e1DetectorValueTracker.getValueHistory().size());
	}

	/**
	 * Reads part-0 and part-1 using the same tracker.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdatePart0Part1() throws IOException {
		copyFileForce(TEST_FILE_PART_0, TEST_FILE_IMAGINARY);

		ValueTracker<E1DetectorValue> e1DetectorValueTracker = loadTestFileToTracker(TEST_FILE_IMAGINARY);
		e1DetectorValueTracker.readRecentDataFromFile();

		assertEquals(13, e1DetectorValueTracker.getRegisteredValues().size());
		assertEquals(0, e1DetectorValueTracker.getValueHistory().size());

		copyFileForce(TEST_FILE_PART_1, TEST_FILE_IMAGINARY);

		e1DetectorValueTracker.readRecentDataFromFile();

		assertEquals(14, e1DetectorValueTracker.getRegisteredValues().size());
	}

	/**
	 * Reads part-0, part-3 and part-4 using the same tracker.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdatePart0Part3Part4() throws IOException {
		copyFileForce(TEST_FILE_PART_0, TEST_FILE_IMAGINARY);

		ValueTracker<E1DetectorValue> e1DetectorValueTracker = loadTestFileToTracker(TEST_FILE_IMAGINARY);
		e1DetectorValueTracker.readRecentDataFromFile();
		assertEquals(13, e1DetectorValueTracker.getRegisteredValues().size());
		assertEquals(0, e1DetectorValueTracker.getValueHistory().size());

		copyFileForce(TEST_FILE_PART_3, TEST_FILE_IMAGINARY);
		e1DetectorValueTracker.readRecentDataFromFile();
		assertEquals(115, e1DetectorValueTracker.getRegisteredValues().size());

		copyFileForce(TEST_FILE_PART_4, TEST_FILE_IMAGINARY);
		e1DetectorValueTracker.readRecentDataFromFile();
		assertEquals(437, e1DetectorValueTracker.getRegisteredValues().size());
	}

	/**
	 * Reads the same file several times.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testNoUpdate() throws IOException {
		ValueTracker<E1DetectorValue> e1DetectorValueTracker = loadTestFileToTracker(TEST_FILE_FULL);
		int stop = 500;

		for (int i = 0; i < stop; i++) {
			e1DetectorValueTracker.readRecentDataFromFile();
		}

		assertEquals(0, e1DetectorValueTracker.getValueHistory().size());
		assertEquals(438, e1DetectorValueTracker.getRegisteredValues().size());

	}

	/**
	 * Reads the same file several times and expects that all values are kept in the
	 * history after they have been popped.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdatePop() throws IOException {
		ValueTracker<E1DetectorValue> e1DetectorValueTracker = loadTestFileToTracker(TEST_FILE_FULL);
		int stop = 400;

		for (int i = 0; i < stop; i++) {
			e1DetectorValueTracker.readRecentDataFromFile();
		}

		e1DetectorValueTracker.popAll();

		assertEquals(0, e1DetectorValueTracker.getRegisteredValues().size());
		assertEquals(438, e1DetectorValueTracker.getValueHistory().size());

	}

	/**
	 * Reads the same file several times. In the same loop the values are popped.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdatePopUpdate() throws IOException {
		ValueTracker<E1DetectorValue> e1DetectorValueTracker = loadTestFileToTracker(TEST_FILE_FULL);
		int stop = 300;

		for (int i = 0; i < stop; i++) {
			e1DetectorValueTracker.readRecentDataFromFile();
			assertEquals(438, e1DetectorValueTracker.getRegisteredValues().size()
					+ e1DetectorValueTracker.getValueHistory().size());
			e1DetectorValueTracker.popAll();
			assertEquals(0, e1DetectorValueTracker.getRegisteredValues().size());
			assertEquals(438, e1DetectorValueTracker.getValueHistory().size());
		}

	}

	/**
	 * Reads the part-4 and afterwards the smaller part-1. Must behave as if only
	 * part-4 has been read.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdatePart4Part1() throws IOException {
		copyFileForce(TEST_FILE_PART_4, TEST_FILE_IMAGINARY);

		ValueTracker<E1DetectorValue> e1DetectorValueTracker = loadTestFileToTracker(TEST_FILE_IMAGINARY);
		e1DetectorValueTracker.readRecentDataFromFile();
		assertEquals(437, e1DetectorValueTracker.getRegisteredValues().size());
		assertEquals(0, e1DetectorValueTracker.getValueHistory().size());

		copyFileForce(TEST_FILE_PART_1, TEST_FILE_IMAGINARY);
		e1DetectorValueTracker.readRecentDataFromFile();
		assertEquals(437, e1DetectorValueTracker.getRegisteredValues().size());
		assertEquals(0, e1DetectorValueTracker.getValueHistory().size());
	}

	private ValueTracker<E1DetectorValue> loadTestFileToTracker(final String file) {
		String path = Paths.get("target", "test-classes", file).toString();
		ValueTracker<E1DetectorValue> e1DetectorValueTracker = new ValueTracker<>(new E1DetectorValueFactory(), path);
		return e1DetectorValueTracker;
	}

}
