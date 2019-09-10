package net.pk.traas.server.controller.junction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.util.SumoCommand;
import it.polito.appeal.traci.SumoTraciConnection;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.api.ResponseToE1DetectorValue;
import net.pk.traas.server.TraasServer;

/**
 * Instances of this class are responsible for a single TLS. This means they
 * take care of tls switch validations and the switches themselves.
 * 
 * @author peter
 *
 */
public class TLSCoach {

	private double lastChangeTimestep;
	private final String tlsId;
	// private SumoCommand lastChangeCommand;
	private SumoTraciConnection connection;
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
}
