package net.pk.traas.server;

import net.pk.stream.api.environment.EnvironmentConfig;

/**
 * @author peter
 *
 */
public final class ServerFactory {

	/**
	 * This producer method creates a {@link TraasServer} dependent from the chosen
	 * {@link EngineMode}.
	 * 
	 * @return sumo server
	 */
	public static TraasServer createServer() {
		switch (EnvironmentConfig.getInstance().getEngineMode()) {
		case STATIC:
			return new StaticServer();
		case LANE_BASED:
			return AsyncServer.createInstance();
		case E1DETECTOR_BASED:
			return AsyncServer.createInstance();
		default:
			throw new RuntimeException("Unknown engine mode: " + EnvironmentConfig.getInstance().getEngineMode());
		}
	}
}
