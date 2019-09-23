package net.pk.integrationtest.traas;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import it.polito.appeal.traci.SumoTraciConnection;

public class TraasIntegrationTest {

	private SumoTraciConnection connection;

	
	private void loadSumoProperties() {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sumo.properties");

		Properties prop = new Properties();
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		prop.putAll(System.getProperties());
		System.setProperties(prop);
	}

	@BeforeEach
	void startTraas() {
		loadSumoProperties();

		connection = new SumoTraciConnection(System.getProperty("sumo.bin.file"),
				System.getProperty("sumo.config.file"));
		connection.addOption("start", "true"); 

		// start Traci Server
		try {
			connection.runServer();
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@AfterEach
	void closeTraas() {
		connection.close();
	}

	/**
	 * @return
	 */
	protected SumoTraciConnection getTraciConnection() {
		return connection;
	}
}
