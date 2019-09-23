package net.pk.traas.server;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.sumo.cmd.Simulation;
import net.pk.stream.api.file.ValueFilePaths;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.api.E1DetectorValueToEdgeConverter;
import net.pk.traas.server.controller.junction.TLSCoach;
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

	private static AsyncServer instance;
	private SimpleUpdateController<E1DetectorValue> updater;
	private Logger log;

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
		this.updater = new SimpleUpdateController<E1DetectorValue>(E1DetectorValue.class);
		this.log = LoggerFactory.getLogger(getClass());
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
			final Path path = Paths.get(ValueFilePaths.getStreamDir());
			try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
				path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
				while (true) {
					final WatchKey wk = watchService.take();
					for (WatchEvent<?> event : wk.pollEvents()) {
						// we only register "ENTRY_MODIFY" so the context is always a Path.
						final Path changed = (Path) event.context();
						if (ValueFilePaths.getPathE1DetectorValue().equals(changed.toAbsolutePath().toString())) {
							updater.update();
						}
					}
					// reset the key
					wk.reset();
				}
			} catch (IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}
}
