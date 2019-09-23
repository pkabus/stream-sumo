package net.pk.traas.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;
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

	public static final String ADD_TLS_FILE = "tls.add.xml";

	private static EnvironmentConfig instance;

	private String sumoBinFilepath;
	private String configFilepath;
	private String configFileDir;
	private String streamProcessingHost;
	private int streamHostE1DetectorValuePort = -1;
	private int streamHostTLSValuePort = -1;
	private String detectorIdSeparator;
	private String timestepDelay;

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
		this.sumoBinFilepath = System.getProperty("sumo.bin.file");
		this.configFilepath = System.getProperty("sumo.config.file");
		this.streamProcessingHost = System.getProperty("stream.processing.host", "localhost");
		this.streamHostE1DetectorValuePort = Integer
				.parseInt(System.getProperty("stream.processing.port.e1detectorvalue", "9000"));

		String tlsPort = System.getProperty("stream.processing.port.tlsvalue");
		this.streamHostTLSValuePort = (tlsPort != null) ? Integer.parseInt(tlsPort) : -1;
		this.detectorIdSeparator = System.getProperty("sumo.detector.separator", "_");
		this.timestepDelay = System.getProperty("sumo.delay", "500");

		Objects.requireNonNull(sumoBinFilepath);
		Objects.requireNonNull(configFilepath);
		Objects.requireNonNull(streamProcessingHost);
		Objects.requireNonNull(streamHostE1DetectorValuePort);
		Objects.requireNonNull(streamHostTLSValuePort);
		Objects.requireNonNull(detectorIdSeparator);
		Objects.requireNonNull(timestepDelay);
		this.configFileDir = StringUtils.substringBeforeLast(configFilepath, File.separator);
		Objects.requireNonNull(configFileDir);
		
		
		File configFile = new File(this.configFilepath);

		if (!configFile.exists()) {
			throw new RuntimeException(
					new FileNotFoundException("No config file found at " + configFile.getAbsolutePath()));
		}

		File settingsFile = Paths.get("config", "settings.xml").toFile();

		if (!settingsFile.exists()) {
			throw new RuntimeException(
					new FileNotFoundException("No settings.xml file found at " + settingsFile.getAbsolutePath()));
		} else {
			// add (or edit) <delay value="'this.timestepDelay'" /> in settings.xml
			try {
				DocumentDelivery.editElementInDom(settingsFile, "viewsettings", "delay", "value", this.timestepDelay);
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

		throw new RuntimeException("No port defined for type " + ((type != null) ? type : "null"));
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
	public String getTimestepDelay() {
		return this.timestepDelay;
	}

	/**
	 * @param filename
	 * @return
	 */
	public Document getAdditionalDom(final String filename) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(this.configFileDir + File.separator + filename);
			return document;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
