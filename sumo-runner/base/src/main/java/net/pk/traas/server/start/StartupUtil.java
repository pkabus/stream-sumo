package net.pk.traas.server.start;

import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.pk.comm.socket.server.ForwardingSocketServer;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.TLSValue;
import net.pk.traas.api.EnvironmentConfig;

/**
 * Startup helper class that takes care of the program arguments and feeds the
 * {@link EnvironmentConfig} with them. Also methods are provided that create
 * threads for the socketServers of the different input types, according to
 * {@link AbstractValue}.
 * 
 * @author peter
 *
 */
public final class StartupUtil {

	private EnvironmentConfig env = EnvironmentConfig.getInstance();
	private List<ForwardingSocketServer> socketServers = new LinkedList<ForwardingSocketServer>();

	/**
	 * Ingest program arguments.
	 * 
	 * @param args
	 */
	public void ingestArgs(String... args) {
		Options options = new Options();

		Option h = new Option("h", "host", true, "Socket Host, where the stream processor gets its input from");
		h.setRequired(false);
		options.addOption(h);

		Option p = new Option("p", "port", true, "Socket Port");
		p.setRequired(false);
		options.addOption(p);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException pe) {
			formatter.printHelp("Block-Cross SUMO scenario", options);
		}

		String host = cmd.getOptionValue("host");
		String port = cmd.getOptionValue("port");

		if (host != null) {
			env.setStreamProcessingHost(host);
		}

		if (port != null) {
			int portNumber = Integer.parseInt(port);

			env.setStreamProcessingPortFor(E1DetectorValue.class, portNumber++);
			env.setStreamProcessingPortFor(TLSValue.class, portNumber++);
		}
	}

	/**
	 * Create {@link Thread} for {@link E1DetectorValue} socketServer.
	 * 
	 * @return thread
	 */
	public Thread createE1DetectorValueSocketServer() {
		final ForwardingSocketServer detectorSocketServer = new ForwardingSocketServer(
				env.getStreamProcessingPortBy(E1DetectorValue.class));
		socketServers.add(detectorSocketServer);

		return new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					detectorSocketServer.run();
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});

	}

	/**
	 * Create {@link Thread} for {@link TLSValue} socketServer.
	 * 
	 * @return thread
	 */
	public Thread createTLSValueSocketServer() {
		final ForwardingSocketServer tlsSocketServer = new ForwardingSocketServer(
				env.getStreamProcessingPortBy(TLSValue.class));
		socketServers.add(tlsSocketServer);

		return new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					tlsSocketServer.run();
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});

	}

	/**
	 * Checks the status of the stored instances of {@link ForwardingSocketServer}.
	 * 
	 * @return true if all socketServers are running, false otherwise
	 */
	public boolean readyToStartSimulation() {
		return socketServers.stream().filter(server -> !server.isReady()).count() == 0;
	}
}
