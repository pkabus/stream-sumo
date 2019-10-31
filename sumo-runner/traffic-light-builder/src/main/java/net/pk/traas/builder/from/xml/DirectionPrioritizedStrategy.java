package net.pk.traas.builder.from.xml;

import java.util.List;

/**
 * This strategy creates right of way green phases for all the lanes of the
 * focusing origin edge. Additionally, green phases are created for
 * non-intersecting connections of the focusing origin edge.
 * 
 * @author peter
 *
 */
public class DirectionPrioritizedStrategy implements TLSProgramProducer {

	private String from;
	private List<TLSConnection> tlsConns;

	/**
	 * Constructor.
	 * 
	 * @param tlsConns connections of TLS
	 * @param fromEdgeId   highlighted from edge
	 */
	public DirectionPrioritizedStrategy(List<TLSConnection> tlsConns, String fromEdgeId) {
		this.from = fromEdgeId;
		this.tlsConns = tlsConns;
	}

	@Override
	public String produceTLSPhase() {
		boolean leftTurnConnection = false;
		char[] phase = new char[tlsConns.size()];
		for (int j = 0; j < tlsConns.size(); j++) {
			TLSConnection conn = tlsConns.get(j);
			int index = conn.getLinkIndex();
			if (from.equals(conn.getFromId())) {
				phase[index] = 'G';
				leftTurnConnection = "l".equals(conn.getDir());
			} else {
				phase[index] = 'r';
			}
		}

		if (!leftTurnConnection) {
			phase = addOtherGreenPhases(tlsConns, phase, from);
		}

		return new String(phase);
	}

	private char[] addOtherGreenPhases(final List<TLSConnection> connections, final char[] phase, final String fromEdgeId) {
		char[] additionalGreenPhases = phase;
		String reverseEdgeFromId = TrafficLightBuilder.REVERSE_EDGE_FUNCTION.apply(fromEdgeId);
		if (reverseEdgeFromId == null) {
			return phase;
		}
		for (int j = 0; j < connections.size(); j++) {
			TLSConnection e = connections.get(j);
			int index = e.getLinkIndex();
			if (reverseEdgeFromId.equals(e.getToId()) && "s".equals(e.getDir())) {
				additionalGreenPhases[index] = 'G';
			}
		}

		return additionalGreenPhases;
	}

}
