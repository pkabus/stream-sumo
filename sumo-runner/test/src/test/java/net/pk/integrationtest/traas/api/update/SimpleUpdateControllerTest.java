package net.pk.integrationtest.traas.api.update;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import net.pk.integrationtest.traas.api.UpdateFileIntegrationTest;
import net.pk.stream.api.file.ValueFilePaths;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.server.controller.update.SimpleUpdateController;

/**
 * Tests the class {@link SimpleUpdateController}.
 * 
 * @author peter
 *
 */
public class SimpleUpdateControllerTest extends UpdateFileIntegrationTest {

	/**
	 * Reads the full text file and expects 15 entries for all the most recent
	 * E1Detector values.
	 */
	@Test
	public void testFullGetValues() {
		ValueFilePaths.setPathE1DetectorValue(Paths.get("target", "test-classes", TEST_FILE_IMAGINARY).toString());

		copyFileForce(TEST_FILE_FULL, TEST_FILE_IMAGINARY);
		SimpleUpdateController<E1DetectorValue> controller = new SimpleUpdateController<>(E1DetectorValue.class);
		controller.update();
		Collection<E1DetectorValue> values = controller.getValues();
		assertEquals(15, values.size());

		controller.update();
		assertEquals(15, values.size());

	}

	/**
	 * Reads the part-0 text file and expects 4 entries for the most recent
	 * E1Detector values.
	 */
	@Test
	public void testPart0GetValues() {
		ValueFilePaths.setPathE1DetectorValue(Paths.get("target", "test-classes", TEST_FILE_IMAGINARY).toString());

		copyFileForce(TEST_FILE_PART_0, TEST_FILE_IMAGINARY);
		SimpleUpdateController<E1DetectorValue> controller = new SimpleUpdateController<>(E1DetectorValue.class);
		controller.update();
		Collection<E1DetectorValue> values = controller.getValues();
		assertEquals(4, values.size());
	}

	/**
	 * First, reads the part-0 file and expects 4 entries. Then, reads the full text
	 * file and expects 15 entries for all the most recent E1Detector values.
	 */
	@Test
	public void testPart0GetValuesFullPopValues() {
		System.setProperty("e1detectorvalue.filepath",
				Paths.get("target", "test-classes", TEST_FILE_IMAGINARY).toString());

		copyFileForce(TEST_FILE_PART_0, TEST_FILE_IMAGINARY);
		SimpleUpdateController<E1DetectorValue> controller = new SimpleUpdateController<>(E1DetectorValue.class);
		controller.update();
		Collection<E1DetectorValue> values = controller.getValues();
		assertEquals(4, values.size());

		copyFileForce(TEST_FILE_FULL, TEST_FILE_IMAGINARY);
		controller.update();
		assertEquals(15, controller.getValues().size());
	}

}
