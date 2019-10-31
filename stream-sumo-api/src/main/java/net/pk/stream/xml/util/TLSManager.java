package net.pk.stream.xml.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.EdgeValue;
import net.pk.data.type.LaneValue;
import net.pk.data.type.SumoEdge;
import net.pk.data.type.TLSKey;
import net.pk.stream.api.query.E1DetectorValueToEdgeConverter;

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
	
	private TLSFinder tlsFinder;
	private EdgeFinder edgeFinder;
	private Set<TLS> set;
	private HashMap<String, TLS> cache;

	/**
	 * Constructor.
	 */
	private TLSManager() {
		set = new HashSet<>();
		this.cache = new HashMap<>();
		this.edgeFinder = EdgeFinder.getInstance();
		this.tlsFinder = TLSFinder.getInstance();
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

		SumoEdge edge = edgeFinder.byId(value.getId());
		if (edge == null) {
			return null;
		}
		
		TLSKey tlsKey = tlsFinder.bySumoEdge(edge);
		
		if (tlsKey == null) {
			return null;
		}
		
		TLS coach = set.stream().filter(c -> StringUtils.equals(tlsKey.getId(), c.getTlsId())).findFirst()
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

		// use convention: laneId = edgeId + "_" + index
		String edgeId = StringUtils.substringBeforeLast(value.getId(), "_");
		SumoEdge edge = edgeFinder.byId(edgeId);
		if (edge == null) {
			return null;
		}
		
		TLSKey tlsKey = tlsFinder.bySumoEdge(edge);
		if (tlsKey == null) {
			return null;
		}
		
		TLS coach = set.stream()
				.filter(c -> StringUtils.equals(tlsKey.getId(), c.getTlsId()))
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
		
		SumoEdge edge = new E1DetectorValueToEdgeConverter().apply(value);
		if (edge == null) {
			return null;
		}
		
		TLSKey tlsKey = tlsFinder.bySumoEdge(edge);
		TLS coach = set.stream()
				.filter(c -> StringUtils.equals(tlsKey.getId(), c.getTlsId()))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No TLS registered that is responsible for the given value + "
						+ value + ". SumoEdge " + edge + " could not be found in " + set));

		cache.put(value.getId(), coach);
		return coach;
	}
	
	
}
