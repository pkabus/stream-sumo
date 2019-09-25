package net.pk.traas.server;

import java.util.List;

import de.tudresden.sumo.cmd.Simulation;
import net.pk.stream.api.query.E1DetectorValueToEdgeConverter;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.server.controller.junction.TLSCoach;
import net.pk.traas.server.controller.update.SimpleInputController;

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

	private static AsyncServer instance;
	private SimpleInputController<E1DetectorValue> updater;

	/**
	 * @return
	 */
	public static AsyncServer getInstance() {
		if (instance == null) {
			throw new RuntimeException("No instance found.");
		}
		return instance;
	}

	/**
	 * @return
	 */
	public static AsyncServer createInstance() {
		if (instance != null) {
			throw new RuntimeException("Instance already exists.");
		}

		instance = new AsyncServer();
		return instance;
	}

	/**
	 * Use host and port to create sumo session.
	 * 
	 */
	private AsyncServer() {
		this.updater = new SimpleInputController<E1DetectorValue>(E1DetectorValue.class);
	}

	@Override
	protected void doSimulation() throws Exception {
		while (((int) getConnection().do_job_get(Simulation.getMinExpectedNumber()) > 0)) {
			this.setChanged();
			double currentTimestep = (double) getConnection().do_job_get(Simulation.getTime());

			List<E1DetectorValue> values = updater.getValues();
			for (int i = 0; i < values.size(); i++) {
				E1DetectorValue current = values.get(i);
				TLSCoach coach = getCoachManager().getCoach(current);
				if (coach.acceptNextProgram(new E1DetectorValueToEdgeConverter().apply(current), currentTimestep)) {
					coach.greenToYellow();
					updater.remove(current);
				}

			}

			this.notifyObservers(currentTimestep);
			getConnection().do_timestep();

		}

	}

	@Override
	protected void beforeSimulation() {
		super.beforeSimulation();
		updater.start();
	}

}
