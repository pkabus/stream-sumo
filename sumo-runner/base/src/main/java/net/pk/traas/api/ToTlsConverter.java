package net.pk.traas.api;

import java.util.function.Function;

import net.pk.stream.format.AbstractValue;

public interface ToTlsConverter<V extends AbstractValue> extends Function<V, String>   {

}
