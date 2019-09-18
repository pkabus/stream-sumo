package net.pk.stream.flink;

import org.apache.flink.streaming.api.datastream.DataStream;

import net.pk.stream.format.AbstractValue;

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
