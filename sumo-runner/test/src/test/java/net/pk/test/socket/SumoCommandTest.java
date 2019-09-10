package net.pk.test.socket;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.util.SumoCommand;

/**
 * @author peter
 *
 */
class SumoCommandTest {

	@Test
	void testTrafficlightEqualsPositive00() {
		SumoCommand tls0 = Trafficlight.setProgram("e2", "-e2_e3");
		SumoCommand tls1 = new SumoCommand(Constants.CMD_SET_TL_VARIABLE, Constants.TL_PROGRAM, "e2", "-e2_e3");

		assertEquals(tls0, tls1);
	}

	@Test
	void testTrafficlightEqualsPositive01() {
		SumoCommand tls0 = Trafficlight.setProgram("e2", "-e2_e3");
		SumoCommand tls1 = Trafficlight.setProgram("e2", "-e2_e3");
		
		assertEquals(tls0, tls1);
	}

	@Test
	void testTrafficlightEqualsNegative00() {
		SumoCommand tls0 = Trafficlight.setProgram("e2", "e2_e3");
		SumoCommand tls1 = new SumoCommand(Constants.CMD_SET_TL_VARIABLE, Constants.TL_PROGRAM, "e2", "-e2_e3");

		assertNotEquals(tls0, tls1);
	}

	@Test
	void testTrafficlightEqualsNullNegative() {
		SumoCommand tls0 = Trafficlight.setProgram("e2", "e2_e3");

		assertNotEquals(tls0, null);
	}

	@Test
	void testTrafficlightEqualsNegative01() {
		SumoCommand tls0 = Trafficlight.setProgram("e2", "e2_e3");
		SumoCommand tls1 = Trafficlight.setPhase("e2", 3);

		assertNotEquals(tls0, tls1);
	}
}
