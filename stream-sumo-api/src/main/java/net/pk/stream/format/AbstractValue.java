package net.pk.stream.format;

/**
 * Abstraction of any kind of sensor data that appears in the simulation.
 * 
 * @author peter
 *
 */
public interface AbstractValue extends Comparable<AbstractValue> {

	public final static String CQL_KEYSPACE = "sumo";
	
	/**
	 * Set given attribute with the given value.
	 * 
	 * @param key   field to set
	 * @param value value to set
	 */
	void set(String key, String value);

	/**
	 * An id for this value type.
	 * 
	 * @return id
	 */
	String getId();

	/**
	 * A (relative) timestamp related to this object.
	 * 
	 * @return timestamp
	 */
	Number getTimestamp();

	@Override
	public default int compareTo(final AbstractValue o) {
		if (o == null) {
			return 1;
		}
		
		if (!this.getClass().equals(o.getClass())) {
			throw new IllegalArgumentException("Cannot compare type " + o.getClass() + " with " + this.getClass());
		}

		float diff = this.getTimestamp().floatValue() - o.getTimestamp().floatValue();
		return Math.round(Math.signum(diff));
	}

	/**
	 * True, if this value is greater than the given value, false otherwise.
	 * 
	 * @param o compare to value
	 * @return true, if greater
	 */
	default boolean greaterThan(final AbstractValue o) {
		return this.compareTo(o) > 0;
	}

	/**
	 * True, if this value is less than the given value, false otherwise.
	 * 
	 * @param o compare to value
	 * @return true, if less
	 */
	default boolean lessThan(final AbstractValue o) {
		return this.compareTo(o) < 0;
	}

}
