package net.pk.data.type;

import java.util.List;

import javax.annotation.Nullable;

/**
 * This interface extends the {@link AbstractValueFactory} class to a factory that is able to
 * parse xml to create an {@link AbstractValue}.
 * 
 * @author peter
 *
 * @param <V> generic type that is created by the factory
 */
public interface ValueListFromXmlFactory<V extends AbstractValue> extends AbstractValueFactory<V> {

	/**
	 * Parse the given xml to a list of {@link AbstractValue} objects. Warning: some
	 * attributes may not be set.
	 * 
	 * @param str xml representation
	 * @return value object
	 */
	@Nullable
	public List<V> parseXml(@Nullable final String str);
}
