package net.pk.traas.server.start;

import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pk.comm.socket.server.ForwardingSocketServer;
import net.pk.db.cassandra.DbBuilder;
import net.pk.db.cassandra.config.DbConfig;
import net.pk.stream.format.AbstractValue;
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
	private DbConfig dbConfig = DbConfig.getInstance();
	private List<ForwardingSocketServer> socketServers = new LinkedList<ForwardingSocketServer>();
	private Logger log = LoggerFactory.getLogger(getClass());


	public void buildDatabase() {
		if (dbConfig.getCassandraHost() == null) {
			this.log.info("No database host set. Cannot build cassandra DB!");
			return;
		}
		
		DbBuilder b = new DbBuilder(AbstractValue.CQL_KEYSPACE);

		// drop old keyspace!
		b.dropKeyspace();

		// create (keyspace and) new table
		b.createTable(E1DetectorValue.CQL_TABLENAME);
		b.createTable(TLSValue.CQL_TABLENAME);
		b.close();
	}
	
	
	/**
	 * Create socketServer {@link Thread} for the given type.
	 * 
	 * @param <V>  type
	 * @param type class of type {@link AbstractValue}
	 * @param waitForServer if true, this 
	 * @return new thread
	 */
	public <V extends AbstractValue> Thread createSocketServerForType(final Class<V> type, boolean waitForServer) {
		int port = env.getStreamProcessingPortBy(type);
		final ForwardingSocketServer typeSpecificSocketServer = new ForwardingSocketServer(port);

		if (waitForServer) {
			socketServers.add(typeSpecificSocketServer);
		}

		return new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String waitBefore = (waitForServer) ? " and wait for the socket server to start up." : "";
					StartupUtil.this.log.info("Use port " + port + " for values of " + type + waitBefore);
					typeSpecificSocketServer.run();
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
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
	public <V extends AbstractValue> Thread createSocketServerForType(final Class<V> type) {
		return this.createSocketServerForType(type, true);
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
