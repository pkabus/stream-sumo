package net.pk.stream.api.conversion.function;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pk.data.type.SumoEdge;
import net.pk.stream.xml.util.EdgeFinder;

/**
 * @author peter
 *
 */
public class ReverseEdgeFunction implements Function<String, String> {

	private Logger log = LoggerFactory.getLogger(getClass());
	private EdgeFinder edgeFinder = EdgeFinder.getInstance();
	
	@Override
	public String apply(String edgeId) {
		SumoEdge origin = edgeFinder.byId(edgeId);
		SumoEdge reverse = edgeFinder.byFromTo(origin.getTo(), origin.getFrom());
		if (reverse == null) {
			this.log.debug("No reverse edge for " + edgeId);
			return null;
		}
		return reverse.getId();
	}
}
