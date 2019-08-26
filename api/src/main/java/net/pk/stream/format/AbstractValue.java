package net.pk.stream.format;

/**
 * Abstraction of any kind of sensor data that appears in the simulation.
 * 
 * @author peter
 *
 */
public interface AbstractValue {

	/**
	 * Set given attribute with the given value.
	 * 
	 * @param key   field to set
	 * @param value value to set
	 */
	void set(String key, String value);

}
