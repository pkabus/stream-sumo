package net.pk.traas.server;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents a key or id of a certain TLS in Sumo. Also a static set
 * exists, that is used to store the keys when a certain scenario is running.
 * 
 * @author peter
 *
 */
public class TLSKey {

	private static Set<TLSKey> set = new HashSet<>();

	private String id;

	/**
	 * Add the given object to the static list of registered {@link TLSKey} objects.
	 * 
	 * @param obj
	 */
	protected static void add(final TLSKey obj) {
		set.add(obj);
	}

	/**
	 * Checks if there is a {@link TLSKey} object that has an id which is equal to
	 * the end of the given edgeId. Conventions:
	 * <ul>
	 * <li>tls.id == junciton.id</li>
	 * <li>edge.id = edge.from + separator + edge.to</li>
	 * </ul>
	 * 
	 * @param edgeId to check
	 * @return object related to given edgeId
	 */
	public static TLSKey findByEdgeId(final String edgeId) {
		return set.stream().filter(tls -> StringUtils.endsWith(edgeId, tls.id)).findFirst()
				.orElseThrow(() -> new RuntimeException("No tls found that fits " + edgeId));
	}

	/**
	 * Constructor.
	 * 
	 * @param id of tls
	 */
	public TLSKey(final String id) {
		this.id = id;
	}

	/**
	 * Getter.
	 * 
	 * @return tls id
	 */
	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof TLSKey) ? ((TLSKey) o).id.equals(this.id) : false;
	}

	@Override
	public String toString() {
		return id;
	}
}
