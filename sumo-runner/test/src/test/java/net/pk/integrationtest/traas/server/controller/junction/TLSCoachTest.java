package net.pk.integrationtest.traas.server.controller.junction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.pk.integrationtest.traas.TraasIntegrationTest;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.EdgeValue;
import net.pk.stream.xml.util.TLSManager;
import net.pk.traas.server.TLSCoach;
import net.pk.traas.server.TLSKey;

/**
 * Tests the class {@link TLSCoach} and {@link TLSManager}.
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
		TLSManager manager = TLSManager.getInstance();
		TLSKey tls = new TLSKey("n2");
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), tls);

		manager.register(coach1);

		EdgeValue val = new EdgeValue();
		val.set(EdgeValue.KEY_ID, EDGE_ID);

		TLSCoach coach = (TLSCoach) manager.getTLS(val);

		assertEquals(coach1, coach);
	}

	/**
	 * Register and unregister a {@link TLSCoach}. {@link RuntimeException}
	 * expected.
	 */
	@Test
	void testRegisterUnregisterCoach() {
		TLSManager manager = TLSManager.getInstance();
		TLSKey tls = new TLSKey("n2");
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), tls);
		manager.register(coach1);

		EdgeValue val = new EdgeValue();
		val.set(EdgeValue.KEY_ID, EDGE_ID);

		TLSCoach coach = (TLSCoach) manager.getTLS(val);

		assertEquals(coach1, coach);

		manager.unregister(coach1);
		assertThrows(RuntimeException.class, () -> manager.getTLS(val));
	}

	/**
	 * Register duplicate coach. Expect that manager ignores it.
	 */
	@Test
	void testRegisterGetUnregister() {
		TLSManager manager = TLSManager.getInstance();
		TLSKey tls = new TLSKey("n2");
		TLSCoach coach1 = new TLSCoach(getTraciConnection(), tls);

		manager.register(coach1);

		EdgeValue val = new EdgeValue();
		val.set(EdgeValue.KEY_ID, EDGE_ID);
		TLSCoach coach = (TLSCoach) manager.getTLS(val);
		assertEquals(coach1, coach);
		manager.unregister(coach1);
	}

}
