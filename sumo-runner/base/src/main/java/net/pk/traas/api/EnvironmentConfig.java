package net.pk.traas.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.TLSValue;

/**
 * Singleton. Contains all necessary properties to run sumo and the stream
 * together. Loads the 'sumo.properties' config file into system properties.
 * 
 * @author peter
 *
 */
public class EnvironmentConfig {

	private static EnvironmentConfig instance;

	private String sumoBinFilepath;
	private String configFilepath;
	private String streamProcessingHost;
	private int streamHostE1DetectorValuePort;
	private int streamHostTLSValuePort;

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
		this.streamHostTLSValuePort = Integer
				.parseInt(System.getProperty("stream.processing.port.e1detectorvalue", "9001"));

		Objects.requireNonNull(sumoBinFilepath);
		Objects.requireNonNull(configFilepath);
		Objects.requireNonNull(streamProcessingHost);
		Objects.requireNonNull(streamHostE1DetectorValuePort);
		Objects.requireNonNull(streamHostTLSValuePort);

		File configFile = new File(this.configFilepath);

		if (!configFile.exists()) {
			throw new RuntimeException(
					new FileNotFoundException("No config file found at " + configFile.getAbsolutePath()));
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
}
