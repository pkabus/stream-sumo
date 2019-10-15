package net.pk.traas.builder.from.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author peter
 *
 */
public class TwoWayPrioritizedBuilder extends EdgeBasedBuilder {

	private TwoWayPrioritizedBuilder(Document doc) {
		super(doc);
	}

	public static TwoWayPrioritizedBuilder createTwoWayPrioritizedBuilder(final String netFilePath)
			throws SAXException, IOException, ParserConfigurationException {
		return createTwoWayPrioritizedBuilder(new File(netFilePath));
	}

	public static TwoWayPrioritizedBuilder createTwoWayPrioritizedBuilder(final File netFile)
			throws SAXException, IOException, ParserConfigurationException {
		return new TwoWayPrioritizedBuilder(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(netFile));
	}

	@Override
	protected String produceTLSPhase(final List<TLSConnection> connections, final String originFromId) {
		char[] phase = new char[connections.size()];
		Optional<TLSConnection> findAnyStraightFromOrigin = connections.stream()
				.filter(c -> StringUtils.equals(c.getFromId(), originFromId) && StringUtils.equals(c.getDir(), "s"))
				.findAny();
		String reverseToId = null;
		if (findAnyStraightFromOrigin.isPresent()) {
			reverseToId = REVERSE_EDGE_FUNCTION.apply(findAnyStraightFromOrigin.get().getToId());
		}

		for (int j = 0; j < connections.size(); j++) {
			TLSConnection conn = connections.get(j);
			int index = conn.getLinkIndex();
			if (originFromId.equals(conn.getFromId()) || StringUtils.equals(conn.getFromId(), reverseToId)) {
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
