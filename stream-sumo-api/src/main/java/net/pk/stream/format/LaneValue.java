package net.pk.stream.format;

/**
 * @author peter
 *
 */
public class LaneValue implements AbstractValue {
	public final static String KEY_ID = "id";
	public final static String KEY_NUM_VEHICLES = "vehicles";
	public final static String KEY_TIMESTAMP = "time";
	private static final String PREFIX = "<lane";
	private static final String SUFFIX = "/>";

	private String id;
	private double timestamp;
	private int numVehicles;

	@Override
	public void set(String key, String value) {
		switch (key) {
		case KEY_ID:
			this.id = value;
			break;
		case KEY_NUM_VEHICLES:
			this.numVehicles = Integer.parseInt(value);
			break;
		case KEY_TIMESTAMP:
			this.timestamp = Double.parseDouble(value);
			break;
		default:
			throw new RuntimeException("Unknown key '" + key + "'");
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Number getTimestamp() {
		return timestamp;
	}

	/**
	 * Getter.
	 * 
	 * @return the number of vehicles
	 */
	public int getNumVehicles() {
		return numVehicles;
	}

	@Override
	public String toString() {
		return PREFIX + " " + KEY_ID + "=\"" + this.id + "\" " + KEY_TIMESTAMP + "=\"" + this.timestamp + "\" "
				+ KEY_NUM_VEHICLES + "=\"" + this.numVehicles + "\" " + SUFFIX;
	}

}
