package net.pk.traas.server.singlebigjunction;

import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.server.StaticServer;
import net.pk.traas.server.start.StartupUtil;

/**
 * Start the sumo simulation scenario 'TCross' supported by the intelligent
 * transportation system which is based on a streaming engine.
 * 
 * @author peter
 *
 */
public class MainScenarioSingleBigJunction {

	/**
	 * Starting point.
	 * 
	 * @param args arguments for the communication (mandatory: --host, --port).
	 */
	public static void main(String[] args) {
		StartupUtil util = new StartupUtil();

		Thread detectorThread = util.createSocketServerForType(E1DetectorValue.class);
		detectorThread.start();

//		Thread tlsThread = util.createSocketServerForType(TLSValue.class);
//		tlsThread.start();
		
//		Thread laneThread = util.createSocketServerForType(LaneValue.class);
//		laneThread.start();

		StaticServer sumoServer = new StaticServer();
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
