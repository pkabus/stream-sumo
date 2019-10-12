package net.pk.stream.api.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.EdgeValue;
import net.pk.stream.format.LaneValue;
import net.pk.stream.format.TLSValue;
import net.pk.stream.xml.util.DocumentDelivery;

/**
 * Singleton. Contains all necessary properties to run sumo and the stream
 * together. Loads the 'sumo.properties' config file into system properties.
 * 
 * @author peter
 *
 */
public class EnvironmentConfig {

	public static final String SUMO_DELAY_KEY = "sumo.delay";
	public static final String SUMO_DETECTOR_SEPARATOR_KEY = "sumo.detector.separator";
	public static final String STREAM_PROCESSING_PORT_TLSVALUE_KEY = "stream.processing.port.tlsvalue";
	public static final String STREAM_PROCESSING_PORT_E1DETECTORVALUE_KEY = "stream.processing.port.e1detectorvalue";
	public static final String STREAM_PROCESSING_PORT_LANEVALUE_KEY = "stream.processing.port.lanevalue";
	public static final String STREAM_PROCESSING_HOST_KEY = "stream.processing.host";
	public static final String SUMO_CONFIG_FILE_KEY = "sumo.config.file";
	public static final String SUMO_BIN_FILE_KEY = "sumo.bin.file";
	public static final String ADD_TLS_FILE_KEY = "tls.add.xml";
	public static final String STREAM_FILE_DIR_KEY = "stream.file.dir";
	public static final String E1DETECTOR_VALUE_KEY = "e1detectorvalue.filename";
	public static final String TLS_VALUE_KEY = "tlsvalue.filename";
	public static final String LANE_VALUE_KEY = "lanevalue.filename";
	public static final String EDGE_VALUE_KEY = "edgevalue.filename";

	private static EnvironmentConfig instance;

	private String sumoBinFilepath;
	private String configFilepath;
	private String configFileDir;
	private String streamProcessingHost;
	private int streamHostE1DetectorValuePort = -1;
	private int streamHostTLSValuePort = -1;
	private int streamHostLaneValuePort = -1;
	private String detectorIdSeparator;
	private int timestepDelay;

	private String streamFileDir;
	private String e1DetectorValueFile;
	private String tlsValueFile;
	private String laneValueFile;
	private String edgeValueFile;
	private String networkFile;

	/**
	 * Get singleton.
	 * 
	 * @return instance
	 */
	public static EnvironmentConfig getInstance() {
		if (instance == null) {
			instance = new EnvironmentConfig();
		}

		return instance;
	}

	private EnvironmentConfig() {
		load("sumo.properties");
		validate();
	}

	private void load(final String file) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(file);

		Properties prop = new Properties();
		try {
			prop.load(inputStream);
		} catch (IOException | NullPointerException e) {
			throw new RuntimeException(e);
		}

		prop.putAll(System.getProperties());
		System.setProperties(prop);
	}

	private void validate() {
		this.sumoBinFilepath = System.getProperty(SUMO_BIN_FILE_KEY);
		this.configFilepath = System.getProperty(SUMO_CONFIG_FILE_KEY);

		this.streamFileDir = System.getProperty(STREAM_FILE_DIR_KEY, System.getProperty("user.dir"));
		this.e1DetectorValueFile = System.getProperty(E1DETECTOR_VALUE_KEY, "e1detector-value.xml");
		this.tlsValueFile = System.getProperty(TLS_VALUE_KEY, "tls-value.xml");
		this.laneValueFile = System.getProperty(LANE_VALUE_KEY, "lane-value.xml");
		this.edgeValueFile = System.getProperty(EDGE_VALUE_KEY, "edge-value.xml");

		// streaming host
		this.streamProcessingHost = System.getProperty(STREAM_PROCESSING_HOST_KEY, "localhost");

		// streaming ports
		this.streamHostE1DetectorValuePort = Integer
				.parseInt(System.getProperty(STREAM_PROCESSING_PORT_E1DETECTORVALUE_KEY, "9000"));
		String tlsPort = System.getProperty(STREAM_PROCESSING_PORT_TLSVALUE_KEY);
		this.streamHostTLSValuePort = (tlsPort != null) ? Integer.parseInt(tlsPort) : -1;
		String lanePort = System.getProperty(STREAM_PROCESSING_PORT_LANEVALUE_KEY);
		this.streamHostLaneValuePort = (tlsPort != null) ? Integer.parseInt(lanePort) : -1;

		// separator char used in the ids of the detectors
		this.detectorIdSeparator = System.getProperty(SUMO_DETECTOR_SEPARATOR_KEY, "_");
		this.timestepDelay = Integer.parseInt(System.getProperty(SUMO_DELAY_KEY, "500"));

		Objects.requireNonNull(sumoBinFilepath);
		Objects.requireNonNull(configFilepath);
		Objects.requireNonNull(streamProcessingHost);
		Objects.requireNonNull(streamHostE1DetectorValuePort);
		Objects.requireNonNull(streamHostTLSValuePort);
		Objects.requireNonNull(streamHostLaneValuePort);
		Objects.requireNonNull(detectorIdSeparator);
		Objects.requireNonNull(timestepDelay);
		this.configFileDir = StringUtils.substringBeforeLast(configFilepath, File.separator);
		Objects.requireNonNull(configFileDir);

		File configFile = new File(this.configFilepath);

		if (!configFile.exists()) {
			throw new RuntimeException(
					new FileNotFoundException("No config file found at " + configFile.getAbsolutePath()));
		}

		File settingsFile = Paths.get(configFileDir, "settings.xml").toFile();

		if (!settingsFile.exists()) {
			throw new RuntimeException(
					new FileNotFoundException("No settings.xml file found at " + settingsFile.getAbsolutePath()));
		} else {
			// add (or edit) <delay value="'this.timestepDelay'" /> in settings.xml
			try {
				DocumentDelivery.editElementInDom(settingsFile, "viewsettings", "delay", "value",
						"" + this.timestepDelay);
			} catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
				throw new RuntimeException(e);
			}

		}
	}

	/**
	 * Get sumo bin file to start sumo.
	 * 
	 * @return bin file path
	 */
	public String getSumoBinFile() {
		return sumoBinFilepath;
	}

	/**
	 * Get the sumo config file path.
	 * 
	 * @return config file path
	 */
	public String getConfigFile() {
		return configFilepath;
	}

	/**
	 * Set stream processing host.
	 */
	public void setStreamProcessingHost(final String host) {
		this.streamProcessingHost = host;
	}

	/**
	 * Set stream processing port.
	 * 
	 * @param <T>  for type
	 * @param type of stream
	 * @param port of stream
	 */
	public <T extends AbstractValue> void setStreamProcessingPortFor(final Class<T> type, final int port) {
		if (E1DetectorValue.class.equals(type)) {
			this.streamHostE1DetectorValuePort = port;
			return;
		}

		if (TLSValue.class.equals(type)) {
			this.streamHostTLSValuePort = port;
			return;
		}

		throw new RuntimeException("Cannot set port for type " + ((type != null) ? type : "null"));
	}

	/**
	 * Get the host of the stream processing job(s).
	 * 
	 * @return stream processing host
	 */
	public String getStreamProcessingHost() {
		return this.streamProcessingHost;
	}

	/**
	 * Get the host of the stream processing job(s).
	 * 
	 * @return stream processing host
	 */
	public <T extends AbstractValue> int getStreamProcessingPortBy(final Class<T> type) {
		if (E1DetectorValue.class.equals(type)) {
			return this.streamHostE1DetectorValuePort;
		}

		if (TLSValue.class.equals(type)) {
			return this.streamHostTLSValuePort;
		}

		if (LaneValue.class.equals(type)) {
			return this.streamHostLaneValuePort;
		}

		throw new RuntimeException("No port defined for type " + ((type != null) ? type : "null"));
	}

	/**
	 * True only if the stream host is running a job that emits {@link TLSValue}s.
	 * 
	 * @return whether a tls driven streaming job is running or not
	 */
	public boolean tlsPortRegistered() {
		return this.streamHostTLSValuePort != -1;
	}

	/**
	 * True only if the stream host is running a job that emits {@link LaneValue}s.
	 * 
	 * @return whether a lane driven streaming job is running or not
	 */
	public boolean lanePortRegistered() {
		return this.streamHostLaneValuePort != -1;
	}

	/**
	 * Returns the (detector) id separator character(s). Default separator is
	 * underline ("_").
	 * 
	 * @return separator
	 */
	public String getSeparator() {
		return this.detectorIdSeparator;
	}

	/**
	 * @return
	 */
	public int getTimestepDelay() {
		return this.timestepDelay;
	}

	/**
	 * Get the absolute path of the file that the stream engine writes to.
	 * 
	 * @return file path for {@link E1DetectorValue}
	 */
	public String getAbsoluteFilePathE1DetectorValue() {
		return this.streamFileDir + File.separator + this.e1DetectorValueFile;
	}

	/**
	 * Get the absolute path of the file that the stream engine writes to.
	 * 
	 * @return file path for {@link TLSValue}
	 */
	public String getAbsoluteFilePathTLSValue() {
		return this.streamFileDir + File.separator + this.tlsValueFile;
	}

	/**
	 * Get the absolute path of the file that the stream engine writes to.
	 * 
	 * @return file path for {@link LaneValue}
	 */
	public String getAbsoluteFilePathLaneValue() {
		return this.streamFileDir + File.separator + this.laneValueFile;
	}

	/**
	 * Get the absolute path of the directory that the stream engine writes to.
	 * 
	 * @return stream file directory
	 */
	public String getStreamFileDir() {
		return this.streamFileDir;
	}

	/**
	 * Setter.
	 * 
	 * @param e1DetectorValueFile the e1DetectorValueFile to set
	 */
	public void setE1DetectorValueFile(String e1DetectorValueFile) {
		this.e1DetectorValueFile = e1DetectorValueFile;
	}

	/**
	 * Setter.
	 * 
	 * @param tlsValueFile the tlsValueFile to set
	 */
	public void setTlsValueFile(String tlsValueFile) {
		this.tlsValueFile = tlsValueFile;
	}

	/**
	 * Setter.
	 * 
	 * @param laneValueFile the laneValueFile to set
	 */
	public void setLaneValueFile(String laneValueFile) {
		this.laneValueFile = laneValueFile;
	}

	public String getAbsoluteFilePathEdgeValue() {
		return this.streamFileDir + File.separator + this.edgeValueFile;
	}

	/**
	 * This method returns true only if the bin file path {@link #sumoBinFilepath}
	 * does end with 'sumo' which is only the case if the headless sumo is used
	 * (instead of sumo-gui which offers a UI).
	 * 
	 * @return true if bin ends with 'sumo', false otherwise
	 */
	public boolean runHeadless() {
		return this.sumoBinFilepath.endsWith("sumo");
	}

	/**
	 * @param type
	 * @return
	 */
	public String getAbsoluteFilePathByType(Class<?> type) {
		if (E1DetectorValue.class.equals(type)) {
			return this.getAbsoluteFilePathE1DetectorValue();
		}
		if (LaneValue.class.equals(type)) {
			return this.getAbsoluteFilePathLaneValue();
		}
		if (EdgeValue.class.equals(type)) {
			return this.getAbsoluteFilePathEdgeValue();
		}
		if (TLSValue.class.equals(type)) {
			return this.getAbsoluteFilePathTLSValue();
		}

		throw new RuntimeException("Unknown type " + type);
	}

	public String getNetworkFile() {
		if (networkFile == null) {
			Document configDom = DocumentDelivery.getDocument(new File(this.configFilepath));
			networkFile = ((Element) configDom.getElementsByTagName("net-file").item(0)).getAttribute("value");
		}
		
		return networkFile;
	}

	/**
	 * @return
	 */
	public String getConfigFileDir() {
		return configFileDir;
	}

}
