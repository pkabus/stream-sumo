package net.pk.stream.format;

/**
 * @author peter
 *
 */
public class TLSValue implements AbstractValue {

	public final static String KEY_TIME = "time";
	public final static String KEY_ID = "id";
	public final static String KEY_PROGRAMID = "programID";
	public final static String KEY_PHASE = "phase";
	public final static String KEY_STATE = "state";

	private float time;
	private String id;
	private String programId;
	private int phase;
	private String state;

	@Override
	public void set(final String key, final String value) {
		switch (key) {
		case KEY_TIME:
			this.time = Float.parseFloat(value);
			break;
		case KEY_ID:
			this.id = value;
			break;
		case KEY_PROGRAMID:
			this.programId = value;
			break;
		case KEY_PHASE:
			this.phase = Integer.parseInt(value);
			break;
		case KEY_STATE:
			this.state = value;
			break;
		default:
			throw new RuntimeException("Unknown key '" + key + "'");
		}
	}

	/**
	 * Getter.
	 * 
	 * @return the keyTime
	 */
	public float getTime() {
		return time;
	}

	/**
	 * Getter.
	 * 
	 * @return the keyId
	 */
	public String getId() {
		return id;
	}

	/**
	 * Getter.
	 * 
	 * @return the keyProgramid
	 */
	public String getProgramid() {
		return programId;
	}

	/**
	 * Getter.
	 * 
	 * @return the keyPhase
	 */
	public int getPhase() {
		return phase;
	}

	/**
	 * Getter.
	 * 
	 * @return the keyState
	 */
	public String getState() {
		return state;
	}

	@Override
	public Number getTimestamp() {
		return this.time;
	}

}
