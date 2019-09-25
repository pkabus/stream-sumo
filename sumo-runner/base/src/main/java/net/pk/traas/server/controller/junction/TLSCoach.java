package net.pk.traas.server.controller.junction;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.util.SumoCommand;
import it.polito.appeal.traci.SumoTraciConnection;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.api.ResponseToE1DetectorValue;
import net.pk.traas.server.AsyncServer;
import net.pk.traas.server.TraasServer;

/**
 * Instances of this class are responsible for a single TLS. This means they
 * take care of tls switch validations and the switches themselves.
 * 
 * @author peter
 *
 */
public class TLSCoach implements Observer {

	private double lastChangeTimestep = -TraasServer.MIN_TLS_CYCLE;
	private final String tlsId;
	private SumoTraciConnection connection;
	private String program;
	private String nextProgram;
	private double nextProgramScheduledTimestep = -1;
	private double minChangeInterval = TraasServer.MIN_TLS_CYCLE;

	private Logger log;

	/**
	 * Constructor. Default {@code TraasServer#MIN_TLS_CYCLE} is used as minimum TLS
	 * cycle time.
	 * 
	 * @param conn  sumo connection
	 * @param tlsId id of tls that this object is responsible for
	 */
	public TLSCoach(final SumoTraciConnection conn, final String tlsId) {
		this.connection = conn;
		this.tlsId = tlsId;
		this.log = LoggerFactory.getLogger(getClass());
	}

	/**
	 * Constructor.
	 * 
	 * @param conn              sumo connection
	 * @param tlsId             id of tls that this object is responsible for
	 * @param minChangeInterval minimum time steps before a next switch may occur
	 */
	public TLSCoach(final SumoTraciConnection conn, final String tlsId, final double minChangeInterval) {
		this.connection = conn;
		this.tlsId = tlsId;
		this.minChangeInterval = minChangeInterval;
	}

	/**
	 * This method is the heart of the class. It must check if a tls switch is
	 * allowed to the given timestep (check when this TLS has switched the last
	 * time). Also the {@link ResponseToE1DetectorValue} function is used to
	 * determine which program must be used to switch the TLS.
	 * 
	 * @param value detectorValue object
	 * @return true only if the coach commands the execution of a
	 *         {@link SumoCommand}, false otherwise
	 */
	@Deprecated
	public boolean substitute(final E1DetectorValue value) {
		try {
			double now = (double) connection.do_job_get(Simulation.getTime());
			if (now - lastChangeTimestep > minChangeInterval) {
				ResponseToE1DetectorValue responseFunction = new ResponseToE1DetectorValue(connection);
				SumoCommand cmd = responseFunction.apply(value);
				if (cmd != null) {
					connection.do_job_set(cmd);
					lastChangeTimestep = now;
					// lastChangeCommand = cmd;
					log.info(tlsId + " -> SWITCH at " + now);
					return true;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
			// System.err.println(e);
		}

		return false;
	}

	/**
	 * @param programId
	 * @return
	 */
	public boolean acceptNextProgram(final String programId, final double currentTimestep) {
		if (!StringUtils.isEmpty(this.nextProgram)) {
			this.log.debug("Program has already been set.");
			return false;
		}

		boolean eval = !StringUtils.equals(program, programId)
				&& this.lastChangeTimestep + this.minChangeInterval < currentTimestep;
		if (eval) {
			this.nextProgram = programId;
		}

		return eval;
	}

	/**
	 * @param program
	 * @throws Exception
	 */
	public void greenToYellow() {
		if (StringUtils.isEmpty(this.nextProgram)) {
			throw new RuntimeException(
					new IllegalStateException("Method call not allowed as long as nextProgram is not set."));
		}

		String state;
		try {
			state = (String) connection.do_job_get(Trafficlight.getRedYellowGreenState(tlsId));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String newState = TlsUtil.getRedYellowGreenState(this.tlsId, this.nextProgram, 0);

		if (state.length() != newState.length()) {
			throw new RuntimeException("TlsState " + state + " and " + newState + " need to have the same length!");
		}

		char[] yellowState = state.toCharArray();
		for (int i = 0; i < state.length(); i++) {
			char cCurrent = state.charAt(i);
			char cNew = newState.charAt(i);

			if (cNew == 'r' && (cCurrent == 'G' || cCurrent == 'g')) {
				yellowState[i] = 'y';
			}
		}

		try {
			connection.do_job_set(Trafficlight.setRedYellowGreenState(tlsId, new String(yellowState)));
			this.nextProgramScheduledTimestep = ((double) connection.do_job_get(Simulation.getTime()))
					+ TraasServer.YELLOW_PHASE;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		registerMe();
	}

	private void registerMe() {
		AsyncServer.getInstance().addObserver(this);
	}

	private void unregisterMe() {
		AsyncServer.getInstance().deleteObserver(this);
	}

	/**
	 * 
	 */
	public void switchToNewProgram() {
		if (StringUtils.isEmpty(this.nextProgram)) {
			throw new RuntimeException(
					new IllegalStateException("Method call not allowed as long as newProgram is not set."));
		}

		double c = -1;
		try {
			this.connection.do_job_set(Trafficlight.setProgram(tlsId, nextProgram));
			c = (double) connection.do_job_get(Simulation.getTime());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		this.log.info("[" +nextProgramScheduledTimestep + "] Switched " + tlsId + " to " + this.nextProgram);
		this.program = nextProgram;
		this.lastChangeTimestep = c;
		this.nextProgram = null;
		this.nextProgramScheduledTimestep = -1;
		unregisterMe();
	}

	/**
	 * Getter.
	 * 
	 * @return the tlsId
	 */
	public String getTlsId() {
		return tlsId;
	}

	@Override
	public String toString() {
		return "Of TLS: " + tlsId;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof AsyncServer && arg != null) {
			double timestep = (double) arg;
			if (timestep == nextProgramScheduledTimestep) {
				switchToNewProgram();
			}
			return;
		}

		throw new RuntimeException("Unexpected observable " + o + " and/or argument " + arg);

	}
}
