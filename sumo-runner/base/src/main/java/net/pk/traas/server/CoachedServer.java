package net.pk.traas.server;

import java.util.List;

import de.tudresden.sumo.cmd.Trafficlight;

/**
 * A traas server that is managed by a {@link CoachManager}. Therefore the
 * registration of all TLS is necessary before the simulation starts. This is
 * done in {@link #beforeSimulation()}. For each junction an
 * {@link TLSCoach} is created. Each changeMaker is responsible for the
 * tls switches of one TLS.
 * 
 * @author peter
 *
 */
public abstract class CoachedServer extends TraasServer {

	private CoachManager coachManager;

	/**
	 * Constructor.
	 * 
	 */
	public CoachedServer() {
		coachManager = new CoachManager();
	}

	@Override
	protected void beforeSimulation() {
		registerAllJunctions();
	}

	private void registerAllJunctions() {
		try {
			@SuppressWarnings("unchecked")
			List<String> tlsIds = (List<String>) getConnection().do_job_get(Trafficlight.getIDList());
			tlsIds.forEach(tlsId -> {
				TLSKey key = new TLSKey(tlsId);
				TLSKey.add(key);
				coachManager.register(new TLSCoach(getConnection(), key));
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return junction manager.
	 * 
	 * @return junction manager
	 */
	protected CoachManager getCoachManager() {
		return coachManager;
	}
}
