package net.pk.stream.api.conversion.function;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * @author peter
 *
 */
public class ReverseEdgeFunction implements Function<String, String> {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public String apply(String edgeId) {
		String separator = EnvironmentConfig.getInstance().getNodeSeparator();
		
		String[] nodes = StringUtils.split(edgeId, separator);
		if (nodes.length != 2) {
			this.log.info("Cannot find reverse edge for " + edgeId);
			return null;
		}
			return nodes[1] + separator + nodes[0];
	}

}
