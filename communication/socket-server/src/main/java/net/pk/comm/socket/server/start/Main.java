package net.pk.comm.socket.server.start;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.pk.comm.socket.server.ForwardingServerSocket;

/**
 * Run socket server that forwards input from the first connecting client to the
 * second one.
 * 
 * @author peter
 *
 */
public class Main {

	/**
	 * Starting point.
	 * 
	 * @param args arguments (mandatory: --port)
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Options options = new Options();

		Option p = new Option("p", "port", true, "Socket Port");
		p.setRequired(true);
		options.addOption(p);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException pe) {
			formatter.printHelp("ForwardingSocketServer needs a port number", options);
		}

		String port = cmd.getOptionValue("port");
		int portNumber = Integer.parseInt(port);

		new ForwardingServerSocket(portNumber);
	}

}
