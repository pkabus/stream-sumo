package net.pk.traas.server;

import java.util.List;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;

/**
 * @author peter
 *
 */
public class StaticServer extends CoachedServer {

	@Override
	protected void beforeSimulation() {
		super.beforeSimulation();
		try {
			@SuppressWarnings("unchecked")
			List<String> tlsIds = (List<String>) getConnection().do_job_get(Trafficlight.getIDList());
			for (String tlsID : tlsIds) {
				getConnection().do_job_set(Trafficlight.setProgram(tlsID, "0"));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doSimulation() throws Exception {
		while (((int) getConnection().do_job_get(Simulation.getMinExpectedNumber()) > 0)) {
			getConnection().do_timestep();
		}
	}

}
