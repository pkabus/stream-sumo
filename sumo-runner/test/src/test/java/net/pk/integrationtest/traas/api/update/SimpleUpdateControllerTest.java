package net.pk.integrationtest.traas.api.update;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import net.pk.integrationtest.traas.api.UpdateFileIntegrationTest;
import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.server.controller.update.SimpleInputController;

/**
 * Tests the class {@link SimpleInputController}.
 * 
 * @author peter
 *
 */
public class SimpleUpdateControllerTest extends UpdateFileIntegrationTest {

	/**
	 * Test the remove method.
	 */
	@Test
	public void testRemove() {
		EnvironmentConfig.getInstance().setE1DetectorValueFile(TEST_FILE_IMAGINARY);

		SimpleInputController<E1DetectorValue> controller = new SimpleInputController<>(E1DetectorValue.class);
		controller.start();
		copyFileForce(TEST_FILE_FULL, EnvironmentConfig.getInstance().getAbsoluteFilePathE1DetectorValue());
		controller.update();
		Collection<E1DetectorValue> values = controller.getValues();
		assertEquals(15, values.size());

		controller.remove(values.iterator().next());
		values = controller.getValues();
		assertEquals(14, values.size());
	}

	/**
	 * Reads the full text file and expects 15 entries for all the most recent
	 * E1Detector values.
	 */
	@Test
	public void testFullGetValues() {
		EnvironmentConfig.getInstance().setE1DetectorValueFile(TEST_FILE_IMAGINARY);

		SimpleInputController<E1DetectorValue> controller = new SimpleInputController<>(E1DetectorValue.class);
		controller.start();
		copyFileForce(TEST_FILE_FULL, EnvironmentConfig.getInstance().getAbsoluteFilePathE1DetectorValue());
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
		EnvironmentConfig.getInstance().setE1DetectorValueFile(TEST_FILE_IMAGINARY);

		SimpleInputController<E1DetectorValue> controller = new SimpleInputController<>(E1DetectorValue.class);
		controller.start();
		copyFileForce(TEST_FILE_PART_0, EnvironmentConfig.getInstance().getAbsoluteFilePathE1DetectorValue());
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
		EnvironmentConfig.getInstance().setE1DetectorValueFile(TEST_FILE_IMAGINARY);

		SimpleInputController<E1DetectorValue> controller = new SimpleInputController<>(E1DetectorValue.class);
		controller.start();
		copyFileForce(TEST_FILE_PART_0, EnvironmentConfig.getInstance().getAbsoluteFilePathE1DetectorValue());
		controller.update();
		Collection<E1DetectorValue> values = controller.getValues();
		assertEquals(4, values.size());

		copyFileForce(TEST_FILE_FULL, EnvironmentConfig.getInstance().getAbsoluteFilePathE1DetectorValue());
		controller.update();
		assertEquals(15, controller.getValues().size());
	}

}
