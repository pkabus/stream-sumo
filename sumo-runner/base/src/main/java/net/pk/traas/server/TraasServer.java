package net.pk.traas.server;

import java.io.IOException;
import java.util.Observable;
import java.util.Set;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.sumo.cmd.Simulation;
import it.polito.appeal.traci.SumoTraciConnection;
import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.LaneValue;
import net.pk.data.type.TLSValue;
import net.pk.stream.api.environment.EngineMode;
import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.flink.job.E1DetectorValueStream;
import net.pk.stream.flink.job.Emitter;
import net.pk.stream.flink.job.LaneValueStream;
import net.pk.stream.flink.job.TLSValueStream;
import net.pk.stream.xml.util.TLS;
import net.pk.stream.xml.util.TLSManager;

/**
 * Abstract class that is defining the TraCI lifecycle for the scenarios. Starts
 * Sumo and the stream job(s).
 * 
 * @author peter
 *
 */
public abstract class TraasServer extends Observable {

	private EnvironmentConfig config = EnvironmentConfig.getInstance();
	public static final double MIN_TLS_CYCLE = 9;
	public static final double YELLOW_PHASE = 3;
	private SumoTraciConnection connection;
	private Logger log;

	/**
	 * Constructor.
	 * 
	 */
	public TraasServer() {
		this.connection = new SumoTraciConnection(config.getSumoBinFile(), config.getConfigFile());
		this.log = LoggerFactory.getLogger(getClass());
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
	private void startStreamJobs() {
		Thread streamThread = new Thread(new StreamRunner());
		streamThread.start();
	}

	private class StreamRunner implements Runnable {

		@Override
		public void run() {
			StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			int tlsPort = config.getStreamProcessingPortBy(TLSValue.class);
			int e1DetPort = config.getStreamProcessingPortBy(E1DetectorValue.class);
			int lanePort = config.getStreamProcessingPortBy(LaneValue.class);

			TLSValueStream streamTls = null;
			E1DetectorValueStream streamE1Detector = null;
			LaneValueStream streamLane = null;

			if (tlsPort > 0) {
				streamTls = new TLSValueStream(config.getStreamProcessingHost(),
						config.getStreamProcessingPortBy(TLSValue.class), env);
				streamTls.enable();
				TraasServer.this.log.info("ADD STREAM " + TLSValueStream.class + ".");
			}

			if (e1DetPort > 0) {
				streamE1Detector = new E1DetectorValueStream(config.getStreamProcessingHost(),
						config.getStreamProcessingPortBy(E1DetectorValue.class), env);
				streamE1Detector.enable();
				TraasServer.this.log.info("ADD STREAM " + E1DetectorValueStream.class + ".");
			}

			if (lanePort > 0) {
				streamLane = new LaneValueStream(config.getStreamProcessingHost(),
						config.getStreamProcessingPortBy(LaneValue.class), env);
				streamLane.enable();
				TraasServer.this.log.info("ADD STREAM " + LaneValueStream.class + ".");
			}

			Emitter emitter = new Emitter(streamE1Detector, streamLane);
			emitter.toFile();

			try {
				env.execute();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
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
		connection.addOption("step-length", "0.1");
		connection.addOption("start", "true");
		// start Traci Server
		try {
			connection.runServer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		/*** SECOND: START FLINK JOB ***/
		this.startStreamJobs();
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

			/*** FOURTH: LOG FINISHING TIME AND STATISTICS ***/
			this.log.info("Finished at timestep " + connection.do_job_get(Simulation.getTime()));
			if (config.getEngineMode() != EngineMode.STATIC) {
				Set<TLS> tls = TLSManager.getInstance().all();
				this.log.info("Number of TLS Switches: " + tls.stream().filter(o -> o instanceof TLSCoach)
						.mapToInt(o -> ((TLSCoach) o).getNumberOfSwitches()).sum());
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		/*** FIFTH: CLOSE SUMO CONNECTION ***/
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
