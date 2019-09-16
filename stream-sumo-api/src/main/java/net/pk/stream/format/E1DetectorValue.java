package net.pk.stream.format;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;

/**
 * @author peter
 *
 */
@Table(keyspace = E1DetectorValue.CQL_KEYSPACE, name = E1DetectorValue.CQL_TABLENAME)
public class E1DetectorValue implements AbstractValue {
	public final static String CQL_KEYSPACE = "sumo";
	public final static String CQL_TABLENAME = "e1detectorvalue";

	public final static String SUFFIX = "/>";
	public final static String PREFIX = "<interval";
	public final static String KEY_BEGIN = "begin";
	public final static String KEY_END = "end";
	public final static String KEY_ID = "id";
	public final static String KEY_NVEHCONTRIB = "nVehContrib";
	public final static String KEY_FLOW = "flow";
	public final static String KEY_OCCUPANCY = "occupancy";
	public final static String KEY_SPEED = "speed";
	public final static String KEY_HARMONICMEANSPEED = "harmonicMeanSpeed";
	public final static String KEY_LENGTH = "length";
	public final static String KEY_NVEHENTERED = "nVehEntered";

	@Column(name = "pk")
	private UUID pk;
	
	@Column(name = "start")
	private float begin;

	@Column(name = "end")
	private float end;

	@Column(name = "id")
	private String id;

	@Column(name = "nvehcontrib")
	private int nVehContrib;

	@Column(name = "flow")
	private float flow;

	@Column(name = "occupancy")
	private float occupancy;

	@Column(name = "speed")
	private float speed;

	@Column(name = "harmonicmeanspeed")
	private float harmonicMeanSpeed;

	@Column(name = "length")
	private float length;

	@Column(name = "nvehentered")
	private int nVehEntered;

	@Override
	public void set(String key, String value) {
		switch (key) {
		case KEY_BEGIN:
			this.begin = Float.parseFloat(value);
			break;
		case KEY_END:
			this.end = Float.parseFloat(value);
			break;
		case KEY_ID:
			this.id = value;
			break;
		case KEY_NVEHCONTRIB:
			this.nVehContrib = Integer.parseInt(value);
			break;
		case KEY_FLOW:
			this.flow = Float.parseFloat(value);
			break;
		case KEY_OCCUPANCY:
			this.occupancy = Float.parseFloat(value);
			break;
		case KEY_SPEED:
			this.speed = Float.parseFloat(value);
			break;
		case KEY_HARMONICMEANSPEED:
			this.harmonicMeanSpeed = Float.parseFloat(value);
			break;
		case KEY_LENGTH:
			this.length = Float.parseFloat(value);
			break;
		case KEY_NVEHENTERED:
			this.nVehEntered = Integer.parseInt(value);
			break;
		default:
			throw new RuntimeException("Unknown key '" + key + "'");
		}
	}
	
	public E1DetectorValue() {
		this.pk = UUID.randomUUID();
	}
	
	/** Getter.
	 * @return the pk
	 */
	public UUID getPk() {
		return pk;
	}
	
	/** Setter.
	 * @param pk the pk to set
	 */
	public void setPk(final UUID pk) {
		this.pk = pk;
	}
	

	/**
	 * Getter.
	 * 
	 * @return the begin
	 */
	public float getBegin() {
		return begin;
	}

	/**
	 * Setter.
	 * 
	 * @param begin the begin to set
	 */
	public void setBegin(float begin) {
		this.begin = begin;
	}

	/**
	 * Getter.
	 * 
	 * @return the end
	 */
	public float getEnd() {
		return end;
	}

	/**
	 * Setter.
	 * 
	 * @param end the end to set
	 */
	public void setEnd(float end) {
		this.end = end;
	}

	/**
	 * Getter.
	 * 
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
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
	 * Getter.
	 * 
	 * @return the nVehContrib
	 */
	public int getNVehContrib() {
		return nVehContrib;
	}

	/**
	 * Setter.
	 * 
	 * @param nVehContrib the nVehContrib to set
	 */
	public void setNVehContrib(int nVehContrib) {
		this.nVehContrib = nVehContrib;
	}

	/**
	 * Getter.
	 * 
	 * @return the flow
	 */
	public float getFlow() {
		return flow;
	}

	/**
	 * Setter.
	 * 
	 * @param flow the flow to set
	 */
	public void setFlow(float flow) {
		this.flow = flow;
	}

	/**
	 * Getter.
	 * 
	 * @return the occupancy
	 */
	public float getOccupancy() {
		return occupancy;
	}

	/**
	 * Setter.
	 * 
	 * @param occupancy the occupancy to set
	 */
	public void setOccupancy(float occupancy) {
		this.occupancy = occupancy;
	}

	/**
	 * Getter.
	 * 
	 * @return the speed
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Setter.
	 * 
	 * @param speed the speed to set
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Getter.
	 * 
	 * @return the harmonicMeanSpeed
	 */
	public float getHarmonicMeanSpeed() {
		return harmonicMeanSpeed;
	}

	/**
	 * Setter.
	 * 
	 * @param harmonicMeanSpeed the harmonicMeanSpeed to set
	 */
	public void setHarmonicMeanSpeed(float harmonicMeanSpeed) {
		this.harmonicMeanSpeed = harmonicMeanSpeed;
	}

	/**
	 * Getter.
	 * 
	 * @return the length
	 */
	public float getLength() {
		return length;
	}

	/**
	 * Setter.
	 * 
	 * @param length the length to set
	 */
	public void setLength(float length) {
		this.length = length;
	}

	/**
	 * Getter.
	 * 
	 * @return the nVehEntered
	 */
	public int getNVehEntered() {
		return nVehEntered;
	}

	/**
	 * Setter.
	 * 
	 * @param nVehEntered the nVehEntered to set
	 */
	public void setNVehEntered(int nVehEntered) {
		this.nVehEntered = nVehEntered;
	}

	@Override
	public boolean equals(Object other) {
		E1DetectorValue val = null;
		if (other instanceof E1DetectorValue) {
			val = (E1DetectorValue) other;
		} else {
			return false;
		}

		return this.id.equals(val.id) && this.begin == val.begin && this.end == val.end;
	}

	/**
	 * This toString() implementation creates an xml representation of this object.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "<interval " + KEY_BEGIN + "=\"" + this.getBegin() + "\" " + KEY_END + "=\"" + this.end + "\" " + KEY_ID
				+ "=\"" + this.id + "\" " + KEY_NVEHCONTRIB + "=\"" + this.nVehContrib + "\" " + KEY_FLOW + "=\""
				+ this.flow + "\" " + KEY_OCCUPANCY + "=\"" + this.occupancy + "\" " + KEY_SPEED + "=\"" + this.speed
				+ "\" " + KEY_HARMONICMEANSPEED + "=\"" + this.harmonicMeanSpeed + "\" " + KEY_LENGTH + "=\""
				+ this.length + "\" " + KEY_NVEHENTERED + "=\"" + this.nVehEntered + "\" />";
	}

	@Override
	public Number getTimestamp() {
		return this.end;
	}

}
