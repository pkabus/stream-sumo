package net.pk.traas.builder.from.xml;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;

import net.pk.stream.api.environment.EnvironmentConfig;

public class BuilderStart {

	public static void main(String[] args) {
		Options options = new Options();

		Option s = new Option("s", "sumocfg", true, "Sumo config file path");
		s.setRequired(true);
		options.addOption(s);

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
		
		try {
			EdgeBasedBuilder builder = TwoWayPrioritizedBuilder.createTwoWayPrioritizedBuilder(EnvironmentConfig.getInstance().getAbsolutePathNetworkFile());
			builder.buildAll();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

}
