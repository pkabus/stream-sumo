package net.pk.integrationtest.traas.tls;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;
import net.pk.integrationtest.traas.TraasIntegrationTest;

/**
 * @author peter
 *
 */
public class TLSControllerTest extends TraasIntegrationTest {

	/**
	 * @throws Exception
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testRedToYellow() throws Exception {
		List<String> tlsIds = (List<String>) getTraciConnection().do_job_get(Trafficlight.getIDList());
		String tlsId = tlsIds.get(0);
		getTraciConnection().do_timestep();
		getTraciConnection().do_timestep();
		getTraciConnection().do_timestep();
		getTraciConnection().do_timestep();

		double currentTimestep = (double) getTraciConnection().do_job_get(Simulation.getTime());
		double yellowPhaseEnd = currentTimestep + 3;
		String rygState = (String) getTraciConnection().do_job_get(Trafficlight.getRedYellowGreenState(tlsId));
		String yellowState = rygState.replace("G", "y").replace("g", "y");
		getTraciConnection()
				.do_job_set(Trafficlight.setRedYellowGreenState(tlsId, yellowState));

		while ((double) getTraciConnection().do_job_get(Simulation.getTime()) < yellowPhaseEnd) {
			getTraciConnection().do_timestep();
			assertEquals(yellowState, getTraciConnection().do_job_get(Trafficlight.getRedYellowGreenState(tlsId)));
		}

	}

}
