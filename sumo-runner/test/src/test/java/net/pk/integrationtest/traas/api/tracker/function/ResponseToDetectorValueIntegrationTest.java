package net.pk.integrationtest.traas.api.tracker.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.util.SumoCommand;
import net.pk.integrationtest.traas.TraasIntegrationTest;
import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.api.ResponseToE1DetectorValue;

/**
 * @author peter
 *
 */
public class ResponseToDetectorValueIntegrationTest extends TraasIntegrationTest {

	private static final String DETECTOR_ID = "e1det_n1_n2_1";
	
	/**
	 * 
	 */
	@Test
	void testFunctionPositive() {
		ResponseToE1DetectorValue detectorValueFunction = new ResponseToE1DetectorValue(getTraciConnection());
		
		E1DetectorValue mock = new E1DetectorValue();
		mock.setId(DETECTOR_ID);
		SumoCommand toTest = detectorValueFunction.apply(mock);
		
		assertNotNull(toTest);
		
		SumoCommand expected = Trafficlight.setProgram("n2", "n1_n2");
		assertEquals(expected, toTest);
	}
	

	/**
	 * 
	 */
	@Test
	void testFunctionNegative() {
		ResponseToE1DetectorValue detectorValueFunction = new ResponseToE1DetectorValue(getTraciConnection());
		
		E1DetectorValue mock = new E1DetectorValue();
		mock.setId(DETECTOR_ID);
		SumoCommand toTest = detectorValueFunction.apply(mock);
		
		assertNotNull(toTest);
		
		SumoCommand expected = Trafficlight.setProgram("n2", "n3_n2");
		assertNotEquals(expected, toTest);
	}	
	
}
