package net.pk.stream.api.query;

import java.util.function.Function;

import net.pk.data.type.AbstractValue;
import net.pk.data.type.SumoEdge;

/**
 * Converts something to the id of an (sumo) edge.
 * 
 * @author peter
 *
 * @param <V> generic type extends {@link AbstractValue}
 */
public interface ToEdgeConverter<V extends AbstractValue> extends Function<V, SumoEdge> {

}
