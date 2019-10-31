package net.pk.data.type;

import java.util.UUID;

/**
 * @author peter
 *
 */
public class TLSValue implements AbstractValue {
	public static final String CQL_TABLENAME = "tlsvalue";

	public final static String KEY_TIME = "time";
	public final static String KEY_ID = "id";
	public final static String KEY_PROGRAMID = "programID";
	public final static String KEY_PHASE = "phase";
	public final static String KEY_STATE = "state";


	private UUID pk;

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
	 * Constructs new value with random uuid.
	 */
	public TLSValue() {
		this.pk = UUID.randomUUID();
	}

	/**
	 * Getter.
	 * 
	 * @return the pk
	 */
	public UUID getPk() {
		return pk;
	}

	/**
	 * Setter.
	 * 
	 * @param pk the pk to set
	 */
	public void setPk(final UUID pk) {
		this.pk = pk;
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
	 * Setter.
	 * 
	 * @param time the time to set
	 */
	public void setTime(float time) {
		this.time = time;
	}

	/**
	 * Setter.
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Setter.
	 * 
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId) {
		this.programId = programId;
	}

	/**
	 * Setter.
	 * 
	 * @param phase the phase to set
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}

	/**
	 * Setter.
	 * 
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Getter.
	 * 
	 * @return the keyProgramid
	 */
	public String getProgramId() {
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

	@Override
	public String toString() {
		return "<tlsState " + KEY_TIME + "=\"" + this.getTime() + "\" " + KEY_ID + "=\"" + this.id + "\" "
				+ KEY_PROGRAMID + "=\"" + this.programId + "\" " + KEY_PHASE + "=\"" + this.phase + "\" " + KEY_STATE
				+ "=\"" + this.state + "\" />";
	}

}
