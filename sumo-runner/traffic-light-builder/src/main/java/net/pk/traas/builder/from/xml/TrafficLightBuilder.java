package net.pk.traas.builder.from.xml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.pk.data.type.SumoEdge;
import net.pk.data.type.TLSKey;
import net.pk.data.type.TLSValue;
import net.pk.stream.api.conversion.function.ReverseEdgeFunction;
import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.xml.util.DocumentDelivery;
import net.pk.stream.xml.util.EdgeFinder;
import net.pk.stream.xml.util.TLSFinder;

/**
 * @author peter
 *
 */
public abstract class TrafficLightBuilder {

	public static final ReverseEdgeFunction REVERSE_EDGE_FUNCTION = new ReverseEdgeFunction();
	private Document sourceDoc;
	private Set<TLSKey> tls;
	private Set<HashMap<String, String>> resultSet;
	private EdgeFinder edgeFinder;
	private TLSFinder tlsFinder;

	protected TrafficLightBuilder(final Document doc) {
		this.sourceDoc = doc;
		this.tls = new HashSet<>();
		this.edgeFinder = EdgeFinder.getInstance();
		this.tlsFinder = TLSFinder.getInstance();
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
		if (tls.isEmpty() || this.resultSet == null) {
			createAll();
		}

		EnvironmentConfig conf = EnvironmentConfig.getInstance();
		File targetFile = Paths.get(conf.getConfigFileDir(), EnvironmentConfig.ADD_TLS_FILE).toFile();

		Document document = DocumentDelivery.createDocument();
		Element root = document.createElement("additional");
		document.appendChild(root);

		for (HashMap<String, String> map : this.resultSet) {
			// take any key (which is an edge id) and find the corresponding tls to it
			String anyEdgeKey = map.keySet().iterator().next();
			SumoEdge edge = edgeFinder.byId(anyEdgeKey);
			String tlsId = tlsFinder.bySumoEdge(edge).getId();

			for (String incomingEdge : map.keySet()) {
				Element tlLogic = document.createElement("tlLogic");
				tlLogic.setAttribute("id", tlsId);
				tlLogic.setAttribute("type", "static");
				tlLogic.setAttribute("programID", incomingEdge);
				tlLogic.setAttribute("offset", "0");
				root.appendChild(tlLogic);

				Element phase = document.createElement("phase");
				phase.setAttribute("duration", "60");
				phase.setAttribute("state", map.get(incomingEdge));
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
		this.tls.add(new TLSKey(tlsId));
	}

	protected Set<TLSKey> getTlsKeys() {
		return tls;
	}

	protected void setResultSet(final Set<HashMap<String, String>> set) {
		this.resultSet = set;
	}

	protected Set<HashMap<String, String>> getResultSet() {
		return resultSet;
	}

}
