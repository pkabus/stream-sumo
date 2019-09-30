package net.pk.integrationtest.traas.server.controller.junction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.pk.integrationtest.traas.TraasIntegrationTest;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.EdgeValue;
import net.pk.traas.server.CoachManager;
import net.pk.traas.server.TLSCoach;
import net.pk.traas.server.TLSKey;

/**
 * Tests the class {@link TLSCoach} and {@link CoachManager}.
 * 
 * @author peter
 *
 */
public class TLSCoachTest extends TraasIntegrationTest {

	private static final String EDGE_ID = "n1_n2";

	/**
	 * Get {@link TLSCoach} by {@link E1DetectorValue}.
	 */
	@Test
	void testGetCoach() {
		CoachManager manager = new CoachManager();
		TLSKey tls = new TLSKey("n2");
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), tls);
		TLSCoach coachDuplicate = new TLSCoach(getTraciConnection(), tls);

		manager.register(coach1);
		manager.register(coachDuplicate);

		EdgeValue val = new EdgeValue();
		val.set(EdgeValue.KEY_ID, EDGE_ID);

		TLSCoach coach = manager.getCoach(val);

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
		TLSKey tls = new TLSKey("n2");
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), tls);
		manager.register(coach1);

		EdgeValue val = new EdgeValue();
		val.set(EdgeValue.KEY_ID, EDGE_ID);

		TLSCoach coach = manager.getCoach(val);

		assertEquals(coach1, coach);

		manager.unregister(coach1);
		assertThrows(RuntimeException.class, () -> manager.getCoach(val));
	}

	/**
	 * Register duplicate coach. Expect that manager ignores it.
	 */
	@Test
	void testRegisterUnregisterDuplicate() {
		CoachManager manager = new CoachManager();
		TLSKey tls = new TLSKey("n2");
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), tls);
		TLSCoach coachDuplicate = new TLSCoach(getTraciConnection(), tls);

		manager.register(coach1);
		manager.register(coachDuplicate);
		manager.unregister(coachDuplicate);

		EdgeValue val = new EdgeValue();
		val.set(EdgeValue.KEY_ID, EDGE_ID);
		TLSCoach coach = manager.getCoach(val);
		assertEquals(coach1, coach);
	}

}
