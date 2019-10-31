package net.pk.traas.builder.from.xml;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class, that decides which {@link TLSProgramProducer} implementation
 * is recommended for a certain TLS.
 * 
 * @author peter
 *
 */
public class TLSProgramProducerFactory {

	private static Logger log = LoggerFactory.getLogger(TLSProgramProducerFactory.class);

	/**
	 * Create {@link TLSProgramProducer} or log an error if no strategy fits the
	 * junction setting.
	 * 
	 * @param tlsConns list of tls connections
	 * @param focus    origin connection
	 * @return program producer instance
	 */
	public static TLSProgramProducer create(List<TLSConnection> tlsConns, TLSConnection focus) {
		int count = countDirections(tlsConns, focus);

		if (count == 3 || count == 4) {
			// junction with four incoming edges
			return new TwoWayPrioritizedStrategy(tlsConns, focus.getFromId());
		}

		if (count == 1 || count == 2) {
			// T-junction (three incoming edges or cycle TLS with only a single outgoing
			// direction (in the cycle))
			return new DirectionPrioritizedStrategy(tlsConns, focus.getFromId());
		}

		log.error("No TLS program created for connection: " + focus.getFromId() + " in TLS "
				+ tlsConns.get(0).getTlsId());

		return null;
	}

	private static int countDirections(List<TLSConnection> tlsConns, TLSConnection focus) {
		ArrayList<String> registered = new ArrayList<String>();

		for (TLSConnection c : tlsConns) {
			if (c.getFromId().equals(focus.getFromId()) && !c.getDir().equals("t")
					&& !registered.contains(c.getToId())) {
				registered.add(c.getToId());
			}
		}

		return registered.size();
	}
}
