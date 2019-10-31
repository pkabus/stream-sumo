package net.pk.data.type;

import net.pk.stream.xml.util.TLS;
import net.pk.stream.xml.util.TLSManager;

/**
 * Lane representation used in the data-flow driven world. Each lane is a part
 * of an edge in Sumo.
 * 
 * @author peter
 *
 */
public class LaneValue implements AbstractValue, TLSAssociated {
	public final static String KEY_ID = "id";
	public final static String KEY_NUM_VEHICLES = "vehicles";
	public final static String KEY_TIMESTAMP = "time";
	public final static String KEY_POS_DISTRIB = "posDistrib";
	private static final String PREFIX = "<lane";
	private static final String SUFFIX = "/>";
	public static final double EPSILON = 0.5;

	private String id;
	private double timestamp;
	private double posDistrib;
	private int numVehicles;
	private String tls;

	@Override
	public void set(String key, String value) {
		switch (key) {
		case KEY_ID:
			this.id = value;
			TLS s = TLSManager.getInstance().getTLS(this);
			this.tls = (s != null) ? s.getTlsId() : null;
			break;
		case KEY_NUM_VEHICLES:
			this.numVehicles = Integer.parseInt(value);
			break;
		case KEY_TIMESTAMP:
			this.timestamp = Double.parseDouble(value);
			break;
		case KEY_POS_DISTRIB:
			this.posDistrib = Double.parseDouble(value);
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

	/**
	 * Getter.
	 * 
	 * @return the position distribution
	 */
	public double getPosDistrib() {
		return posDistrib;
	}

	@Override
	public String getTLS() {
		return tls;
	}

	@Override
	public String toString() {
		return PREFIX + " " + KEY_ID + "=\"" + this.id + "\" " + KEY_TIMESTAMP + "=\"" + this.timestamp + "\" "
				+ KEY_NUM_VEHICLES + "=\"" + this.numVehicles + "\" " + KEY_POS_DISTRIB + "=\"" + this.posDistrib
				+ "\" " + SUFFIX;
	}

}
