package net.pk.traas.server.controller.junction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.api.EnvironmentConfig;

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
	public TLSCoach getCoach(final E1DetectorValue value) {
		// look up cache
		TLSCoach cachedCoach = cache.get(value.getId());
		if (cachedCoach != null) {
			return cachedCoach;
		}

		// if not cached, get delegate, put it to cache and return the delegate

		// split id "detector_nx_ny_z" {x, y, z e N} by "_" and then look for last
		// appearance of element containing an "n" to get the id of the junction
		// List<String> nodes =
		// Arrays.asList(value.getId().split("_")).stream().sequential().filter(s ->
		// s.contains("n"))
		// .collect(Collectors.toList());

		// cut away prefix and suffix, e.g. e1det_N0A0_0 -> N0A0 or e1det_n0_n1_1 ->
		// n0_n1
		String separator = EnvironmentConfig.getInstance().getSeparator();
		String edgeId = StringUtils.substringBeforeLast(StringUtils.substringAfter(value.getId(), separator),
				separator);

		TLSCoach coach = list.stream().filter(c -> StringUtils.endsWith(edgeId, c.getTlsId())).findFirst().orElseThrow(
				() -> new RuntimeException("No TLSCoach registered that is responsible for the given detectorValue + "
						+ value + ". The wanted delegate should have the tlsId = " + edgeId));

		cache.put(value.getId(), coach);
		return coach;
	}
}
