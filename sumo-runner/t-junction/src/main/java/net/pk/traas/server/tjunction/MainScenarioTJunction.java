package net.pk.traas.server.tjunction;

import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.TLSValue;
import net.pk.traas.server.AsyncServer;
import net.pk.traas.server.start.StartupUtil;

/**
 * Start the sumo simulation scenario 'TCross' supported by the intelligent
 * transportation system which is based on a streaming engine.
 * 
 * @author peter
 *
 */
public class MainScenarioTJunction {

	/**
	 * Starting point.
	 * 
	 * @param args arguments for the communication (mandatory: --host, --port).
	 */
	public static void main(String[] args) {
		StartupUtil util = new StartupUtil();

		Thread detectorThread = util.createSocketServerForType(E1DetectorValue.class);
		detectorThread.start();

		Thread tlsThread = util.createSocketServerForType(TLSValue.class);
		tlsThread.start();

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
