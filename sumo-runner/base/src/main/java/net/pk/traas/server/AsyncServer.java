package net.pk.traas.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.sumo.cmd.Simulation;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.server.controller.update.SimpleUpdateController;

/**
 * This server handles the tls switches and the timesteps in its main method
 * {@link #doSimulation()}. The detectorValues are going to be updated and
 * written in a different thread. This is initialized in
 * {@link #beforeSimulation()}. The separation of IO and the simulation is done
 * because of performance reasons.
 * 
 * @author peter
 *
 */
public class AsyncServer extends CoachedServer {

	private SimpleUpdateController<E1DetectorValue> updater;
	private Logger log;

	/**
	 * Use host and port to create sumo session.
	 * 
	 */
	public AsyncServer() {
		this.updater = new SimpleUpdateController<E1DetectorValue>(E1DetectorValue.class);
		this.log = LoggerFactory.getLogger(getClass());
	}

	@Override
	protected void doSimulation() throws Exception {
		while (((int) getConnection().do_job_get(Simulation.getMinExpectedNumber()) > 0)) {

			List<E1DetectorValue> values = updater.getValues();
			for (int i = 0; i < values.size(); i++) {
				E1DetectorValue current = values.get(i);
				if (getCoachManager().getCoach(current).substitute(current)) {
					updater.remove(current);
				}
			}

			getConnection().do_timestep();

		}

	}

	@Override
	protected void beforeSimulation() {
		super.beforeSimulation();

		Thread updateThread = new Thread(new RunUpdate());
		updateThread.start();
	}

	/**
	 * The update job is done in this runnable class.
	 * 
	 * @author peter
	 *
	 */
	private class RunUpdate implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					updater.update();
					Thread.sleep(100);

				} catch (RuntimeException e) {
					log.error("Exit because of " + e.getLocalizedMessage() + " in RunUpdate");
					System.exit(1);
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}
			}
		}

	}
}
