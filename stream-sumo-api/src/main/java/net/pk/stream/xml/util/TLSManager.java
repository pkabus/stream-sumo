package net.pk.stream.xml.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import net.pk.stream.api.query.E1DetectorValueToEdgeConverter;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.EdgeValue;
import net.pk.stream.format.LaneValue;

/**
 * CoachManager registers and obtains all {@link TLSCoach} of the used sumo
 * scenario. Before a simulation runs, it is necessary to register the TLS'
 * here.
 * 
 * @author peter
 *
 */
public class TLSManager {

	private static TLSManager instance;

	private Set<TLS> set;
	private HashMap<String, TLS> cache;

	/**
	 * Constructor.
	 */
	private TLSManager() {
		set = new HashSet<>();
		this.cache = new HashMap<>();
	}

	public static TLSManager getInstance() {
		if (instance == null) {
			instance = new TLSManager();
		}

		return instance;
	}

	/**
	 * Add given {@link TLSCoach}, if not yet registered.
	 * 
	 * @param coach to register
	 */
	public void register(final TLS coach) {
		set.add(coach);
	}

	/**
	 * Remove given {@link TLSCoach} from this manager.
	 * 
	 * @param coach to unregister
	 */
	public void unregister(final TLS coach) {
		if (coach != null) {
			set.remove(coach);
			cache.clear();
		}
	}

	/**
	 * Get the {@link TLS} that is responsible for the given value object.
	 * 
	 * @param value detector value
	 */
	public TLS getTLS(final EdgeValue value) {
		// look up cache
		TLS cachedCoach = cache.get(value.getId());
		if (cachedCoach != null) {
			return cachedCoach;
		}

		// if not cached, get delegate, put it to cache and return the delegate

		TLS coach = set.stream().filter(c -> StringUtils.endsWith(value.getId(), c.getTlsId())).findFirst()
				.orElseThrow(() -> new RuntimeException("No TLS registered that is responsible for the given value + "
						+ value + ". The wanted delegate should be associated with the id = " + value.getId()));

		cache.put(value.getId(), coach);
		return coach;
	}

	/**
	 * Get the {@link TLS} that is responsible for the given value object.
	 * 
	 * @param value detector value
	 */
	@Nullable
	public TLS getTLS(final LaneValue value) {
		// look up cache
		TLS cached = cache.get(value.getId());
		if (cached != null) {
			return cached;
		}

		// if not cached, get delegate, put it to cache and return the delegate

		TLS coach = set.stream()
				.filter(c -> StringUtils.endsWith(StringUtils.substringBeforeLast(value.getId(), "_"), c.getTlsId()))
				.findFirst().orElseGet(() -> null);

		cache.put(value.getId(), coach);
		return coach;
	}

	/**
	 * Get the {@link TLS} that is responsible for the given value object.
	 * 
	 * @param value detector value
	 */
	@Nullable
	public TLS getTLS(final E1DetectorValue value) {
		// look up cache
		TLS cached = cache.get(value.getId());
		if (cached != null) {
			return cached;
		}

		// if not cached, get delegate, put it to cache and return the delegate

		TLS coach = set.stream()
				.filter(c -> StringUtils.endsWith(new E1DetectorValueToEdgeConverter().apply(value), c.getTlsId()))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No TLS registered that is responsible for the given value + "
						+ value + ". The wanted delegate should be associated with the id = " + value.getId()));

		cache.put(value.getId(), coach);
		return coach;
	}

}
