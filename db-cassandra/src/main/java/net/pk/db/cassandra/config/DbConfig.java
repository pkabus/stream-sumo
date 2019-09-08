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
		this.cassandraHost = System.getProperty("cassandra.host");
	}

	/**
	 * @return
	 */
	@Nullable
	public String getCassandraHost() {
		return cassandraHost;
	}
}
