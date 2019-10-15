/**
 * 
 */
package net.pk.test.traas.builder.from.xml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import net.pk.traas.builder.from.xml.EdgeBasedBuilder;

/**
 * @author peter
 *
 */
class TrafficLightBuilderTest {

	private final static String NET_FILE = "block-cross.net.xml";
	
	
	@Test
	void testCreateFor() throws SAXException, IOException, ParserConfigurationException {
		EdgeBasedBuilder trafficLightBuilder = EdgeBasedBuilder
				.createEdgeBasedBuilder(Paths.get("target", "test-classes", NET_FILE).toFile());
		HashMap<String, String> programs = trafficLightBuilder.createFor("n2");

		assertEquals(4, programs.size());
		assertTrue(programs.containsKey("n1_n2"));
		assertTrue(programs.containsKey("n20_n2"));
		assertTrue(programs.containsKey("n21_n2"));
		assertTrue(programs.containsKey("n3_n2"));
		
		System.out.println(programs);

		assertEquals("rrrrrrrrGGGGrrrr", programs.get("n3_n2"));
	}

	@Test
	void testSizeCreateAll() throws SAXException, IOException, ParserConfigurationException {
		EdgeBasedBuilder trafficLightBuilder = EdgeBasedBuilder
				.createEdgeBasedBuilder(Paths.get("target", "test-classes", NET_FILE).toFile());
		Set<HashMap<String, String>> createAll = trafficLightBuilder.createAll();

		assertEquals(4, createAll.size());
	}
}
