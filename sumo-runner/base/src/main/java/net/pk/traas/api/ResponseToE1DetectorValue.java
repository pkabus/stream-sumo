package net.pk.traas.api;

import java.util.function.Function;

import javax.annotation.Nullable;

import de.tudresden.sumo.cmd.Inductionloop;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.util.SumoCommand;
import de.tudresden.ws.container.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;
import net.pk.stream.format.E1DetectorValue;

/**
 * This function creates a {@link SumoCommand} based on a given
 * {@link E1DetectorValue}.
 * 
 * @author peter
 *
 */
public class ResponseToE1DetectorValue implements Function<E1DetectorValue, SumoCommand> {

	private SumoTraciConnection connection;

	/**
	 * Constructor.
	 * 
	 * @param connection sumo connection
	 */
	public ResponseToE1DetectorValue(final SumoTraciConnection connection) {
		this.connection = connection;
	}

	@Override
	@Nullable
	public SumoCommand apply(@Nullable E1DetectorValue t) {
		if (t == null) {
			return null;
		}

		SumoStringList tlsList = null;
		String tlsID = null;
		try {
			String laneID = (String) connection.do_job_get(Inductionloop.getLaneID(t.getId()));
			tlsList = (SumoStringList) connection.do_job_get(Trafficlight.getIDList());

			tlsID = tlsList.stream().filter(id -> {
				try {
					return ((SumoStringList) connection.do_job_get(Trafficlight.getControlledLanes(id)))
							.contains(laneID);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).findFirst().orElseThrow(() -> new RuntimeException("No TLS referring to lane with ID " + laneID));
		} catch (Exception e) {
			return null;
		}

		String programID = new E1DetectorValueToTLSProgramConverter().apply(t);
		return Trafficlight.setProgram(tlsID, programID);
	}

}
