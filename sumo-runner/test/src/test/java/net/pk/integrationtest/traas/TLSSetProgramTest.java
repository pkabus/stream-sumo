package net.pk.integrationtest.traas;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import de.tudresden.sumo.cmd.Trafficlight;

/**
 * Test {@link Trafficlight#setProgram} of TraCI.
 * 
 * @author peter
 *
 */
public class TLSSetProgramTest extends TraasIntegrationTest {
	public final static String TLS_ID = "n2";
	public final static String TLS_PROGRAM_0 = "n3_n2";
	public final static String TLS_PROGRAM_1 = "n1_n2";
	public final static String TLS_PROGRAM_2 = "n20_n2";

	/**
	 * Changes tls program of the same tls very frequently. It is expected that the
	 * changes are set to TraCI and are consistently returned from TraCI.
	 */
	@Test
	void testPenetrateSetProgram() throws Exception {
		for (int i = 0; i < 200; i++) {
			// it is checked if the tls program id is still the same if nothing has changed
			// by using TraCI
			String startProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			getTraciConnection().do_timestep();
			String currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(startProgram, currentProgram);

			// switch to TLS_PROGRAM_0
			getTraciConnection().do_job_set(Trafficlight.setProgram(TLS_ID, TLS_PROGRAM_0));
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_0, currentProgram);
			getTraciConnection().do_timestep();
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_0, currentProgram);
			getTraciConnection().do_timestep();
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_0, currentProgram);
			getTraciConnection().do_timestep();
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_0, currentProgram);

			// switch to TLS_PROGRAM_1
			getTraciConnection().do_job_set(Trafficlight.setProgram(TLS_ID, TLS_PROGRAM_1));
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_1, currentProgram);
			getTraciConnection().do_timestep();
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_1, currentProgram);
			getTraciConnection().do_timestep();
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_1, currentProgram);
			getTraciConnection().do_timestep();
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_1, currentProgram);

			// switch to TLS_PROGRAM_2
			getTraciConnection().do_job_set(Trafficlight.setProgram(TLS_ID, TLS_PROGRAM_2));
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_2, currentProgram);
			getTraciConnection().do_timestep();
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_2, currentProgram);
			getTraciConnection().do_timestep();
			currentProgram = (String) getTraciConnection().do_job_get(Trafficlight.getProgram(TLS_ID));
			assertEquals(TLS_PROGRAM_2, currentProgram);
		}
	}

}
