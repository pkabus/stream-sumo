package net.pk.traas.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.pk.stream.format.EdgeValue;

/**
 * CoachManager registers and obtains all {@link TLSCoach} of the used sumo
 * scenario. Before a simulation runs, it is necessary to register the TLS'
 * here.
 * 
 * @author peter
 *
 */
public class CoachManager {

	private List<TLSCoach> list;
	private HashMap<String, TLSCoach> cache;

	/**
	 * Constructor.
	 */
	public CoachManager() {
		list = new LinkedList<>();
		this.cache = new HashMap<>();
	}

	/**
	 * Add given {@link TLSCoach}, if not yet registered.
	 * 
	 * @param coach to register
	 */
	public void register(final TLSCoach coach) {
		if (!list.contains(coach)) {
			list.add(coach);
		}
	}

	/**
	 * Remove given {@link TLSCoach} from this manager.
	 * 
	 * @param coach to unregister
	 */
	public void unregister(final TLSCoach coach) {
		if (coach != null) {
			list.remove(coach);
			cache.clear();
		}
	}

	/**
	 * Get the {@link TLSCoach} that is responsible for the given value object.
	 * 
	 * @param value detector value
	 */
	public TLSCoach getCoach(final EdgeValue value) {
		// look up cache
		TLSCoach cachedCoach = cache.get(value.getId());
		if (cachedCoach != null) {
			return cachedCoach;
		}

		// if not cached, get delegate, put it to cache and return the delegate


		TLSCoach coach = list.stream().filter(c -> StringUtils.endsWith(value.getId(), c.getTlsId())).findFirst()
				.orElseThrow(
						() -> new RuntimeException("No TLSCoach registered that is responsible for the given edge + "
								+ value + ". The wanted delegate should have the tlsId = " + value.getId()));

		cache.put(value.getId(), coach);
		return coach;
	}
}
