package net.pk.db.cassandra.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nullable;

/**
 * @author peter
 *
 */
public class DbConfig {
	
	public final static String SYS_PROP_CASSANDRA_HOST = "cassandra.host";

	private static DbConfig instance;
	private String cassandraHost;

	/**
	 * Get singleton.
	 * 
	 * @return instance
	 */
	public static DbConfig getInstance() {
		if (instance == null) {
			instance = new DbConfig();
		}

		return instance;
	}

	private DbConfig() {
		load("db.properties");
		validate();
	}

	private void load(final String file) {
		File f = new File(file);
		if (!f.exists()) {
			return;
		}

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
		this.cassandraHost = System.getProperty(SYS_PROP_CASSANDRA_HOST);
	}

	/**
	 * @return
	 */
	@Nullable
	public String getCassandraHost() {
		return cassandraHost;
	}
	
	/**
	 * @param host
	 */
	public void setCassandraHost(final String host) {
		this.cassandraHost = host;
		System.getProperty(SYS_PROP_CASSANDRA_HOST);
	}
}
