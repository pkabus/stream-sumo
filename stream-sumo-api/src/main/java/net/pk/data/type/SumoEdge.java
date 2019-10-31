package net.pk.data.type;

/**
 * Java object representation of edges in the sumo network file. Internal edges
 * are not supposed to be represented by this class. Only xml nodes in the
 * following form:
 * 
 * <pre>
 * {@code
 * <edge id="n1_n2" from="n1" to="n2" priority spreadType shape>
 *      <lane id="n1_n2_0" index="0" speed length shape />
 *      <lane id="n1_n2_1" index="1" speed length shape/>
 * </edge>
 * }
 * </pre>
 *
 * The attributes priority, spreadType, shape, speed and length are not empty,
 * but for this representation not important. Moreover, edges do not necessarily
 * need a lane to be represented by this class (but in Sumo edges without lanes
 * are not allowed).
 * 
 * @author peter
 *
 */
public class SumoEdge {

	private String id;
	private String from;
	private String to;

	/**
	 * Constructor.
	 * 
	 * @param id   of edge
	 * @param from node id
	 * @param to   node id
	 */
	protected SumoEdge(String id, String from, String to) {
		this.id = id;
		this.from = from;
		this.to = to;
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
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Getter.
	 * 
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof SumoEdge ? ((SumoEdge) o).id.equals(this.id) : false;
	}

	@Override
	public String toString() {
		return "id: " + id + " [" + from + ", " + to + "]";
	}

}
