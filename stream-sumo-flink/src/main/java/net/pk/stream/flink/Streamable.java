package net.pk.stream.flink;

import org.apache.flink.streaming.api.datastream.DataStream;

import net.pk.data.type.AbstractValue;

/**
 * @author peter
 *
 * @param <V>
 */
public interface Streamable<V extends AbstractValue> {
	
	/**
	 * @return
	 */
	DataStream<V> getStream();

}
