package net.pk.traas.server;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;
import it.polito.appeal.traci.SumoTraciConnection;
import net.pk.stream.api.conversion.function.EdgeValueToProgramIdFunction;
import net.pk.stream.format.EdgeValue;
import net.pk.stream.xml.util.JunctionUtil;
import net.pk.stream.xml.util.TLS;

/**
 * Instances of this class are responsible for a single TLS. This means they
 * take care of tls switch validations and the switches themselves.
 * 
 * @author peter
 *
 */
public class TLSCoach implements Observer, TLS {
	private static final EdgeValueToProgramIdFunction EDGE_PROGRAM_FUNCTION = new EdgeValueToProgramIdFunction();
	private double lastChangeTimestep = -TraasServer.MIN_TLS_CYCLE;
	private final TLSKey tls;
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
	 * @param conn sumo connection
	 * @param tls  id of tls that this object is responsible for
	 */
	public TLSCoach(final SumoTraciConnection conn, final TLSKey tls) {
		this.connection = conn;
		this.tls = tls;
		this.log = LoggerFactory.getLogger(getClass());
	}

	/**
	 * Constructor.
	 * 
	 * @param conn              sumo connection
	 * @param tls               id of tls that this object is responsible for
	 * @param minChangeInterval minimum time steps before a next switch may occur
	 */
	public TLSCoach(final SumoTraciConnection conn, final TLSKey tls, final double minChangeInterval) {
		this.connection = conn;
		this.tls = tls;
		this.minChangeInterval = minChangeInterval;
	}

	/**
	 * This method checks when the last program change has been made and if a new
	 * one can already be set. If so, the new program id is registered and true is
	 * returned. Otherwise nothing changes and false is returned.
	 * 
	 * @param programId id of new program
	 * @return true if new program is accepted, false otherwise
	 */
	public boolean acceptNextProgram(final EdgeValue edgeValue, final double currentTimestep) {
		if (!StringUtils.isEmpty(this.nextProgram)) {
			this.log.debug("Program has already been set.");
			return false;
		}

		String programId = EDGE_PROGRAM_FUNCTION.apply(edgeValue);
		boolean eval = !StringUtils.equals(program, programId)
				&& this.lastChangeTimestep + this.minChangeInterval < currentTimestep;
		if (eval) {
			this.nextProgram = programId;
		}

		return eval;
	}

	/**
	 * Current tls program ends by switching the green traffic lights to yellow that
	 * are going to turn to red in the new program.
	 * 
	 */
	public void greenToYellow() {
		if (StringUtils.isEmpty(this.nextProgram)) {
			throw new RuntimeException(
					new IllegalStateException("Method call not allowed as long as nextProgram is not set."));
		}

		String state;
		try {
			state = (String) connection.do_job_get(Trafficlight.getRedYellowGreenState(tls.getId()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String newState = JunctionUtil.getRedYellowGreenState(this.tls.getId(), this.nextProgram, 0);

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
			connection.do_job_set(Trafficlight.setRedYellowGreenState(tls.getId(), new String(yellowState)));
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
	 * Switches the tls to the {@link #nextProgram}. If no any next program is set,
	 * this method call ends in a {@link IllegalStateException}. After the new
	 * program is set, this object is no longer observing the {@link AsyncServer}
	 * instance.
	 */
	protected void switchToNewProgram() {
		if (StringUtils.isEmpty(this.nextProgram)) {
			throw new RuntimeException(
					new IllegalStateException("Method call not allowed as long as newProgram is not set."));
		}

		double c = -1;
		try {
			this.connection.do_job_set(Trafficlight.setProgram(tls.getId(), nextProgram));
			c = (double) connection.do_job_get(Simulation.getTime());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		this.log.info("[" + nextProgramScheduledTimestep + "] Switched " + tls + " to " + this.nextProgram);
		this.program = nextProgram;
		this.lastChangeTimestep = c;
		this.nextProgram = null;
		this.nextProgramScheduledTimestep = -1;
		unregisterMe();
	}

	/**
	 * Getter.
	 * 
	 * @return the tls id
	 */
	@Override
	public String getTlsId() {
		return tls.getId();
	}

	@Override
	public String toString() {
		return "Of TLS: " + tls;
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
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof TLSCoach) ? ((TLSCoach) o).tls.equals(this.tls) : false;
	}
}
