package net.pk.data.type;

import net.pk.stream.xml.util.TLS;
import net.pk.stream.xml.util.TLSManager;

/**
 * @author peter
 *
 */
public class EdgeValue implements AbstractValue, TLSAssociated {

	public final static String PREFIX = "<edge";
	public final static String SUFFIX = "/>";
	public final static String KEY_ID = "id";
	public final static String KEY_TIMESTAMP = "timestamp";
//	public final static String KEY_NUM_VEHICLES = "numVehicles";

	private String id;
	private double timestamp;
	private String tls;
//	private int numVehicles;

	@Override
	public void set(String key, String value) {
		switch (key) {
		case KEY_ID:
			this.id = value;
			TLS s = TLSManager.getInstance().getTLS(this);
			this.tls = (s != null) ? s.getTlsId() : null;
			break;
		case KEY_TIMESTAMP:
			this.timestamp = Double.parseDouble(value);
			break;
//		case KEY_NUM_VEHICLES:
//			this.numVehicles = Integer.parseInt(value);
//			break;
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
	 * @return the {@link #numVehicles}
	 */
//	public int getNumVehicles() {
//		return numVehicles;
//	}

	@Override
	public String toString() {
		return PREFIX + " " + KEY_ID + "=\"" + this.id + "\" " + KEY_TIMESTAMP + "=\"" + this.timestamp + "\" "
				+ /* KEY_NUM_VEHICLES + "=\"" + this.numVehicles + "\" " + */ SUFFIX;
	}

	@Override
	public String getTLS() {
		return tls;
	}


}
