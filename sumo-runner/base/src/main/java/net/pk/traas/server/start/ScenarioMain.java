package net.pk.traas.server.start;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.pk.data.type.E1DetectorValue;
import net.pk.data.type.LaneValue;
import net.pk.data.type.TLSValue;
import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.traas.server.ServerFactory;
import net.pk.traas.server.TraasServer;

/**
 * @author peter
 *
 */
public class ScenarioMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// create Options object
		Options options = new Options();

		// add a option
		Option s = new Option("s", "sumocfg", true, "Sumo config file path");
		s.setRequired(true);
		options.addOption(s);

		Option m = new Option("m", "mode", true, "Choose traffic light mode");
		m.setRequired(false);
		options.addOption(m);

		// Create a parser
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			// parse the options passed as command line arguments
			cmd = parser.parse(options, args);
		} catch (ParseException e1) {
			throw new RuntimeException(e1);
		}

		// hasOptions checks if option is present or not
		if (cmd.hasOption("s")) {
			System.setProperty(EnvironmentConfig.SUMO_CONFIG_FILE_KEY, cmd.getOptionValue("s"));
		}
		
		if (cmd.hasOption("m")) {
			System.setProperty(EnvironmentConfig.ENGINE_MODE, cmd.getOptionValue("m"));
		}

		StartupUtil util = new StartupUtil();

		Thread detectorThread = util.createServerSocketForType(E1DetectorValue.class);
		detectorThread.start();

		Thread tlsThread = util.createServerSocketForType(TLSValue.class);
		tlsThread.start();

		Thread laneThread = util.createServerSocketForType(LaneValue.class);
		laneThread.start();

		TraasServer sumoServer = ServerFactory.createServer();
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
