package net.pk.traas.builder.from.xml;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * This strategy creates green phases for the focusing origin edge, but also for
 * its reverse edge right on the other side of the junction.
 * 
 * @author peter
 *
 */
public class TwoWayPrioritizedStrategy implements TLSProgramProducer {

	private String from;
	private List<TLSConnection> tlsConns;

	/**
	 * Constructor.
	 * 
	 * @param tlsConns connections of TLS
	 * @param fromId   highlighted from edge
	 */
	public TwoWayPrioritizedStrategy(List<TLSConnection> tlsConns, String fromId) {
		this.from = fromId;
		this.tlsConns = tlsConns;
	}

	@Override
	public String produceTLSPhase() {
		char[] phase = new char[tlsConns.size()];
		Optional<TLSConnection> findAnyStraightFromOrigin = tlsConns.stream()
				.filter(c -> StringUtils.equals(c.getFromId(), from) && StringUtils.equals(c.getDir(), "s")).findAny();
		String reverseToId = null;
		if (findAnyStraightFromOrigin.isPresent()) {
			reverseToId = TrafficLightBuilder.REVERSE_EDGE_FUNCTION.apply(findAnyStraightFromOrigin.get().getToId());
		}

		for (int j = 0; j < tlsConns.size(); j++) {
			TLSConnection conn = tlsConns.get(j);
			int index = conn.getLinkIndex();
			if (from.equals(conn.getFromId()) || StringUtils.equals(conn.getFromId(), reverseToId)) {
				phase[index] = chooseGreenPhase(conn);
			} else {
				phase[index] = 'r';
			}
		}

		return new String(phase);
	}

	private char chooseGreenPhase(final TLSConnection conn) {
		return "l".equals(conn.getDir()) ? 'g' : 'G';
	}

}
