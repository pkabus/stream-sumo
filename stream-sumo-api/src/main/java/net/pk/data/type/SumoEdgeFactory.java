package net.pk.data.type;

/**
 * Factory class for {@link SumoEdge} objects.
 * 
 * @author peter
 *
 */
public class SumoEdgeFactory {

	/**
	 * Producer method.
	 * 
	 * @param id   of edge
	 * @param from node id of origin
	 * @param to   node id of target
	 * @return produced object
	 */
	public SumoEdge create(String id, String from, String to) {
		return new SumoEdge(id, from, to);
	}
}
