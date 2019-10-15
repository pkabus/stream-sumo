package net.pk.traas.builder.from.xml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.pk.stream.api.conversion.function.ReverseEdgeFunction;
import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.format.TLSValue;
import net.pk.stream.xml.util.DocumentDelivery;

/**
 * @author peter
 *
 */
public abstract class TrafficLightBuilder {

	public static final ReverseEdgeFunction REVERSE_EDGE_FUNCTION = new ReverseEdgeFunction();
	private Document sourceDoc;
	private Set<String> tlsIds;
	private Set<HashMap<String, String>> resultSet;

	protected TrafficLightBuilder(final Document doc) {
		this.sourceDoc = doc;
		this.tlsIds = new HashSet<>();
	}

	/**
	 * @return
	 */
	public abstract Set<HashMap<String, String>> createAll();

	/**
	 * @param tlsId
	 * @return
	 */
	public abstract HashMap<String, String> createFor(String tlsId);

	/**
	 * 
	 */
	public void buildAll() {
		if (tlsIds.isEmpty() || this.resultSet == null) {
			createAll();
		}

		EnvironmentConfig conf = EnvironmentConfig.getInstance();
		File targetFile = Paths.get(conf.getConfigFileDir(), EnvironmentConfig.ADD_TLS_FILE).toFile();

		Document document = DocumentDelivery.createDocument();
		Element root = document.createElement("additional");
		document.appendChild(root);

		for (HashMap<String, String> map : this.resultSet) {
			String tlsId = tlsIds.stream()
					.filter(id -> id.equals(StringUtils.substringAfter(
							map.keySet().stream().findAny().orElseThrow(() -> new RuntimeException("No key in map")),
							conf.getNodeSeparator())))
					.findFirst().orElseThrow(() -> new RuntimeException("Missing tls id in " + tlsIds));
			for (String s : map.keySet()) {
				Element tlLogic = document.createElement("tlLogic");
				tlLogic.setAttribute("id", tlsId);
				tlLogic.setAttribute("type", "static");
				tlLogic.setAttribute("programID", s);
				tlLogic.setAttribute("offset", "0");
				root.appendChild(tlLogic);

				Element phase = document.createElement("phase");
				phase.setAttribute("duration", "60");
				phase.setAttribute("state", map.get(s));
				tlLogic.appendChild(phase);
			}

			Element timedEvent = document.createElement("timedEvent");
			timedEvent.setAttribute("type", "SaveTLSStates");
			timedEvent.setAttribute("source", tlsId);
			timedEvent.setAttribute("dest",
					conf.getStreamProcessingHost() + ":" + conf.getStreamProcessingPortBy(TLSValue.class));
			root.appendChild(timedEvent);
		}

		try {
			DocumentDelivery.writeDomToFile(targetFile, document);
		} catch (TransformerException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected Document getSourceDoc() {
		return sourceDoc;
	}

	protected void addTlsId(final String tlsId) {
		this.tlsIds.add(tlsId);
	}

	protected Set<String> getTlsIds() {
		return tlsIds;
	}

	protected void setResultSet(final Set<HashMap<String, String>> set) {
		this.resultSet = set;
	}

	protected Set<HashMap<String, String>> getResultSet() {
		return resultSet;
	}

}
