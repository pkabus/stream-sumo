package net.pk.traas.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author peter
 *
 */
public class TLSKey {

	private static List<TLSKey> list = new ArrayList<>();

	private String id;

	protected static void add(final TLSKey obj) {
		if (list.stream().noneMatch(k -> k.getId().equals(obj.getId()))) {
			list.add(obj);
		}
	}

	public static TLSKey findByEdgeId(final String edgeId) {
		return list.stream().filter(tls -> StringUtils.endsWith(edgeId, tls.id)).findFirst()
				.orElseThrow(() -> new RuntimeException("No tls found that fits " + edgeId));
	}

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
	public String toString() {
		return id;
	}
}
