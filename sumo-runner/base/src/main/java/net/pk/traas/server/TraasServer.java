package net.pk.traas.server;

import java.io.IOException;

import it.polito.appeal.traci.SumoTraciConnection;
import net.pk.stream.flink.job.E1DetectorValueStream;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.api.EnvironmentConfig;

/**
 * @author peter
 *
 */
public abstract class TraasServer {

	public static final double MIN_TLS_CYCLE = 9;
	private static EnvironmentConfig config = EnvironmentConfig.getInstance();
	private SumoTraciConnection connection;

	/**
	 * Constructor.
	 * 
	 */
	public TraasServer() {
		this.connection = new SumoTraciConnection(config.getSumoBinFile(), config.getConfigFile());
	}

	/**
	 * Returns the sumo traci connection which is the interface to SUMO.
	 * 
	 * @return sumo connection
	 */
	protected SumoTraciConnection getConnection() {
		return this.connection;
	}

	/**
	 * Second lifecycle phase: start stream job(s).
	 */
	protected void startStreamJob() {
		Thread streamThread = new Thread(new Runnable() {

			@Override
			public void run() {
				E1DetectorValueStream streamHandler = new E1DetectorValueStream(config.getStreamProcessingHost(), config.getStreamProcessingPortBy(E1DetectorValue.class));
				streamHandler.out();
			}
		});
		streamThread.start();
	}

	/**
	 * Init the simulation components (sumo, streaming app). The first two lifecycle
	 * steps are executed:
	 * <ul>
	 * <li>-> Startup (Sumo)</li>
	 * <li>-> Startup (Streaming)</li>
	 * <li>Run (Sumo)</li>
	 * <li>Shutdown (Sumo)</li>
	 * </ul>
	 * 
	 * The last two lifecycle steps are done in {@code TraasServer#runSimulation()}.
	 * 
	 * Hint: It is important to startup sumo beforehand and afterwards the streaming
	 * app because the socket server assumes the first client connection is sumo,
	 * the second one is the stream engine.
	 */
	public void startupComponents() {
		/*** FIRST: START SUMO SERVER TO ATTACH SOCAT PORT ***/
		getConnection().addOption("step-length", "0.1");
		// start Traci Server
		try {
			connection.runServer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/*** SECOND: START FLINK JOB ***/
		this.startStreamJob();
	}

	/**
	 * The last two lifecycle steps are executed:
	 * <ul>
	 * <li>Startup (Sumo)</li>
	 * <li>Startup (Streaming)</li>
	 * <li>-> Run (Sumo)</li>
	 * <li>-> Shutdown (Sumo)</li>
	 * </ul>
	 */
	public void runSimulation() {
		/*** THIRD: DO SUMO SIMULATION ***/
		beforeSimulation();
		try {
			doSimulation();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		/*** FOURTH: CLOSE SUMO CONNECTION ***/
		finish();
	}

	/**
	 * If the simulation has finished, do these past execution steps.
	 */
	protected void finish() {
		connection.close();
	}

	/**
	 * Do the simulation loop (and controls) here.
	 */
	protected abstract void doSimulation() throws Exception;

	/**
	 * This step is done just before the simulation starts. By default nothing
	 * happens here. The method should be used in subclasses to initialize or
	 * register certain controls.
	 */
	protected void beforeSimulation() {
		// do nothing by default
	}
}
