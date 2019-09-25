package net.pk.traas.server.gridstretched;

import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.LaneValue;
import net.pk.stream.format.TLSValue;
import net.pk.traas.server.AsyncServer;
import net.pk.traas.server.start.StartupUtil;

/**
 * Start the sumo simulation scenario 'grid-stretched' supported by the
 * intelligent transportation system which is based on a streaming engine.
 * 
 * @author peter
 *
 */
public class MainScenarioGridStretched {

	/**
	 * Starting point.
	 * 
	 * @param args no args expected
	 */
	public static void main(String[] args) {
		StartupUtil util = new StartupUtil();

		Thread detectorThread = util.createSocketServerForType(E1DetectorValue.class);
		detectorThread.start();

		Thread tlsThread = util.createSocketServerForType(TLSValue.class);
		tlsThread.start();


		Thread laneThread = util.createSocketServerForType(LaneValue.class);
		laneThread.start();
		
		AsyncServer sumoServer = AsyncServer.createInstance();
		sumoServer.startupComponents();

		while (!util.readyToStartSimulation()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				//
			}
		}

		sumoServer.runSimulation();
		System.exit(0);
	}

}
