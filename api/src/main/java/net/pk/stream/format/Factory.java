package net.pk.stream.format;

/**
 * Factory of any kind of sensor data.
 * 
 * @author peter
 *
 * @param <T>
 */
public interface Factory<T extends AbstractValue> {

	/**
	 * Create function. Used to create a value object.
	 * 
	 * @return created object
	 */
	T create();

}
