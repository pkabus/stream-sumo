package net.pk.stream.api.query;

import java.util.function.Function;

import net.pk.stream.format.AbstractValue;

/**
 * Converts something to the id of an (sumo) edge.
 * 
 * @author peter
 *
 * @param <V> generic type extends {@link AbstractValue}
 */
public interface ToEdgeConverter<V extends AbstractValue> extends Function<V, String> {

}
