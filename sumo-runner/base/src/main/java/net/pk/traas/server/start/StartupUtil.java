package net.pk.traas.server.start;

import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pk.comm.socket.server.ForwardingServerSocket;
import net.pk.data.type.AbstractValue;
import net.pk.stream.api.environment.EnvironmentConfig;

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
	private List<ForwardingServerSocket> serverSockets = new LinkedList<ForwardingServerSocket>();
	private Logger log = LoggerFactory.getLogger(getClass());


	/**
	 * Create socketServer {@link Thread} for the given type.
	 * 
	 * @param <V>  type
	 * @param type class of type {@link AbstractValue}
	 * @param waitForServer if true, this 
	 * @return new thread
	 */
	public <V extends AbstractValue> Thread createServerSocketForType(final Class<V> type, boolean waitForServer) {
		int port = env.getStreamProcessingPortBy(type);
		final ForwardingServerSocket typeSpecificSocketServer = new ForwardingServerSocket(port);

		if (waitForServer) {
			serverSockets.add(typeSpecificSocketServer);
		}

		return new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String waitBefore = (waitForServer) ? " and wait for the socket server to start up." : "";
					StartupUtil.this.log.info("Use port " + port + " for values of " + type + waitBefore);
					typeSpecificSocketServer.run();
				} catch (SocketTimeoutException e) {
					StartupUtil.this.log.error("Shutdown. Exception for " + type, e);
					System.exit(1);
				}
			}
		});
	}

	/**
	 * Create socketServer {@link Thread} for the given type. Wait for socketServer included.
	 * 
	 * @param <V>  type
	 * @param type class of type {@link AbstractValue}
	 * @return new thread
	 */
	public <V extends AbstractValue> Thread createServerSocketForType(final Class<V> type) {
		return this.createServerSocketForType(type, true);
	}

	/**
	 * Checks the status of the stored instances of {@link ForwardingServerSocket}.
	 * 
	 * @return true if all socketServers are running, false otherwise
	 */
	public boolean readyToStartSimulation() {
		return serverSockets.stream().filter(server -> !server.isReady()).count() == 0;
	}
}
