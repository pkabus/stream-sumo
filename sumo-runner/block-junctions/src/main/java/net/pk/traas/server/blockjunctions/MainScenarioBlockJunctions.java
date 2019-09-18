package net.pk.traas.server.blockjunctions;

import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.server.AsyncServer;
import net.pk.traas.server.start.StartupUtil;

/**
 * Start the sumo simulation scenario 'Block-Cross' supported by the intelligent
 * transportation system which is based on a streaming engine.
 * 
 * @author peter
 *
 */
public class Main {

	/**
	 * Starting point.
	 * 
	 * @param args arguments for the communication (mandatory: --host, --port,
	 *             default: localhost and 9000).
	 */
	public static void main(String[] args) {
		StartupUtil util = new StartupUtil();

		Thread detectorThread = util.createSocketServerForType(E1DetectorValue.class);
		detectorThread.start();

//		Thread tlsThread = util.createTLSValueSocketServer();
//		tlsThread.start();

		AsyncServer sumoServer = new AsyncServer();
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
