package net.pk.stream.xml.util;

import java.util.HashMap;

import net.pk.data.type.LaneInformation;

/**
 * @author peter
 *
 */
public class LaneCache {

	private static LaneCache instance;

	private HashMap<String, LaneInformation> cache;

	private LaneCache() {
		this.cache = new HashMap<String, LaneInformation>();
	}

	/**
	 * @return
	 */
	public static LaneCache getInstance() {
		if (instance == null) {
			instance = new LaneCache();
		}

		return instance;
	}

	/**
	 * @param id
	 * @return
	 */
	public LaneInformation findBy(final String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		}

		LaneInformation obj = LaneUtil.by(id);
		cache.put(id, obj);
		return obj;
	}
}
