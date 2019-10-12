package net.pk.traas.server;

import java.util.List;

import de.tudresden.sumo.cmd.Simulation;
import net.pk.stream.format.EdgeValue;
import net.pk.traas.server.controller.update.EdgeValueController;

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
	private EdgeValueController feedback;

	/**
	 * If created, this method returns the only instance of this class. If no
	 * instance exists, a {@link RuntimeException} is thrown.
	 * 
	 * @return instance
	 */
	public static AsyncServer getInstance() {
		if (instance == null) {
			throw new RuntimeException("No instance found.");
		}
		return instance;
	}

	/**
	 * Create instance. This is only once per application allowed. If this method is
	 * called twice a {@link RuntimeException} is thrown.
	 * 
	 * @return instance
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
		this.feedback = new EdgeValueController();
	}

	@Override
	protected void doSimulation() throws Exception {
		while (((int) getConnection().do_job_get(Simulation.getMinExpectedNumber()) > 0)) {
			this.setChanged();
			double currentTimestep = (double) getConnection().do_job_get(Simulation.getTime());

			List<EdgeValue> values = feedback.getValues();
			for (int i = 0; i < values.size(); i++) {
				EdgeValue current = values.get(i);
				TLSCoach coach = (TLSCoach) getCoachManager().getTLS(current);
				if (coach.acceptNextProgram(current, currentTimestep)) {
					coach.greenToYellow();
					feedback.remove(current);
				}
			}
			this.notifyObservers(currentTimestep);

			// do timestep in simulation
			getConnection().do_timestep();
		}

	}

	@Override
	protected void beforeSimulation() {
		super.beforeSimulation();
		feedback.start();
	}

}
