package net.pk.data.type;

import net.pk.stream.xml.util.TLS;
import net.pk.stream.xml.util.TLSManager;

/**
 * Edge representation used in the data-flow driven world.
 * 
 * @author peter
 *
 */
public class EdgeValue implements AbstractValue, TLSAssociated {

	public final static String PREFIX = "<edge";
	public final static String SUFFIX = "/>";
	public final static String KEY_ID = "id";
	public final static String KEY_TIMESTAMP = "timestamp";

	private String id;
	private double timestamp;
	private String tls;

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

	@Override
	public String toString() {
		return PREFIX + " " + KEY_ID + "=\"" + this.id + "\" " + KEY_TIMESTAMP + "=\"" + this.timestamp + "\" "
				+ SUFFIX;
	}

	@Override
	public String getTLS() {
		return tls;
	}

}
