package net.pk.data.type;

/**
 * This class delivers additional information to a {@link LaneValue}.
 * 
 * @author peter
 *
 */
public class LaneInformation {

	private String id;
	private int index;
	private float speed;
	private float length;

	/**
	 * Constructor.
	 * 
	 * @param id     must be equal to a {@link LaneValue} id
	 * @param index  of lane
	 * @param speed  allowed max speed of lane
	 * @param length of lane
	 */
	public LaneInformation(final String id, final int index, final float speed, final float length) {
		this.id = id;
		this.index = index;
		this.speed = speed;
		this.length = length;
	}

	/**
	 * Constructor.
	 * 
	 * @param id     must be equal to a {@link LaneValue} id
	 * @param index  of lane
	 * @param speed  allowed max speed of lane
	 * @param length of lane
	 */
	public LaneInformation(String id, String index, String speed, String length) {
		this.id = id;
		this.index = Integer.parseInt(index);
		this.speed = Float.parseFloat(speed);
		this.length = Float.parseFloat(length);
	}

	/**
	 * Getter.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Getter.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
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
	 * Getter.
	 * 
	 * @return the length
	 */
	public float getLength() {
		return length;
	}

}
