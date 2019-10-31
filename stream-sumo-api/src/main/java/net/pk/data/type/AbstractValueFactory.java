package net.pk.data.type;

/**
 * Factory of any kind of sensor data.
 * 
 * @author peter
 *
 * @param <T>
 */
public interface AbstractValueFactory<T extends AbstractValue> {

	/**
	 * Create function. Used to create a value object.
	 * 
	 * @return created object
	 */
	T create();

}
