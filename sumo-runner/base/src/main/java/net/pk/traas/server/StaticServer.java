package net.pk.traas.server;

import de.tudresden.sumo.cmd.Simulation;

/**
 * @author peter
 *
 */
public class StaticServer extends TraasServer {

	@Override
	protected void doSimulation() throws Exception {
		while (((int) getConnection().do_job_get(Simulation.getMinExpectedNumber()) > 0)) {
			getConnection().do_timestep();
		}
	}

}
