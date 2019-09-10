package net.pk.integrationtest.traas.server.controller.junction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.pk.integrationtest.traas.TraasIntegrationTest;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.server.controller.junction.CoachManager;
import net.pk.traas.server.controller.junction.TLSCoach;

/**
 * Tests the class {@link TLSCoach} and {@link CoachManager}.
 * 
 * @author peter
 *
 */
public class TLSCoachTest extends TraasIntegrationTest {

	private static final String DETECTOR_ID = "e1det_n1_n2_1";

	/**
	 * Get {@link TLSCoach} by {@link E1DetectorValue}.
	 */
	@Test
	void testGetCoach() {
		CoachManager manager = new CoachManager();
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), "n2");
		TLSCoach coachDuplicate = new TLSCoach(getTraciConnection(), "n2");

		manager.register(coach1);
		manager.register(coachDuplicate);

		E1DetectorValue detVal = new E1DetectorValue();
		detVal.set(E1DetectorValue.KEY_ID, DETECTOR_ID);

		TLSCoach coach = manager.getCoach(detVal);

		assertEquals(coach1, coach);
		assertNotEquals(coachDuplicate, coach);
	}

	/**
	 * Register and unregister a {@link TLSCoach}. {@link RuntimeException}
	 * expected.
	 */
	@Test
	void testRegisterUnregisterCoach() {
		CoachManager manager = new CoachManager();
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), "n2");
		manager.register(coach1);

		E1DetectorValue detVal = new E1DetectorValue();
		detVal.set(E1DetectorValue.KEY_ID, DETECTOR_ID);

		TLSCoach coach = manager.getCoach(detVal);

		assertEquals(coach1, coach);

		manager.unregister(coach1);
		assertThrows(RuntimeException.class, () -> manager.getCoach(detVal));
	}

	/**
	 * Register duplicate coach. Expect that manager ignores it.
	 */
	@Test
	void testRegisterUnregisterDuplicate() {
		CoachManager manager = new CoachManager();
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), "n2");
		TLSCoach coachDuplicate = new TLSCoach(getTraciConnection(), "n2");

		manager.register(coach1);
		manager.register(coachDuplicate);
		manager.unregister(coachDuplicate);

		E1DetectorValue detVal = new E1DetectorValue();
		detVal.set(E1DetectorValue.KEY_ID, DETECTOR_ID);
		TLSCoach coach = manager.getCoach(detVal);
		assertEquals(coach1, coach);
	}

}
