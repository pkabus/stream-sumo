package net.pk.traas.builder.from.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author peter
 *
 */
public class DirectionPrioritizedBuilder extends EdgeBasedBuilder {

	private DirectionPrioritizedBuilder(Document doc) {
		super(doc);
	}

	public static DirectionPrioritizedBuilder createDirectionPrioritizedBuilder(final String netFilePath)
			throws SAXException, IOException, ParserConfigurationException {
		return createDirectionPrioritizedBuilder(new File(netFilePath));
	}

	public static DirectionPrioritizedBuilder createDirectionPrioritizedBuilder(final File netFile)
			throws SAXException, IOException, ParserConfigurationException {
		return new DirectionPrioritizedBuilder(
				DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(netFile));
	}
	
	@Override
	protected String produceTLSPhase(final List<TLSConnection> connections, final String fromId) {
		boolean leftTurnConnection = false;
		char[] phase = new char[connections.size()];
		for (int j = 0; j < connections.size(); j++) {
			TLSConnection conn = connections.get(j);
			int index = conn.getLinkIndex();
			if (fromId.equals(conn.getFromId())) {
				phase[index] = 'G';
				leftTurnConnection = "l".equals(conn.getDir());
			} else {
				phase[index] = 'r';
			}
		}

		if (!leftTurnConnection) {
			phase = addOtherGreenPhases(connections, phase, fromId);
		}

		return new String(phase);
	}

	
}
