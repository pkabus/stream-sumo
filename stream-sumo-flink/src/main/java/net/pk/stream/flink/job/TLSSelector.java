package net.pk.stream.flink.job;

import org.apache.flink.api.java.functions.KeySelector;

import net.pk.data.type.TLSAssociated;

/**
 * @author peter
 * @param <V>
 *
 */
public class TLSSelector<V extends TLSAssociated> implements KeySelector<V, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2204921403382589063L;

	@Override
	public String getKey(V value) throws Exception {
		return value.getTLS();
	}

}
