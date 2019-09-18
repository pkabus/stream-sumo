package net.pk.db.cassandra;

import java.time.Duration;
import java.time.LocalTime;
import java.util.function.BooleanSupplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;

import net.pk.db.cassandra.config.DbConfig;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.TLSValue;

/**
 * This class is responsible for creating and editing a single keyspace using
 * the com.datastax driver.
 * 
 * @author peter
 *
 */
public class DbBuilder {

	private final static Duration TIMEOUT = Duration.ofMillis(5000);

	private Logger log;
	private String keyspace;
	private String host;
	private Cluster cluster;

	/**
	 * Constructor.
	 * 
	 * @param keyspace to create and/or edit.
	 * @param host     of cassandra DB
	 */
	public DbBuilder(final String keyspace) {
		this.host = DbConfig.getInstance().getCassandraHost();
		this.keyspace = keyspace;
		this.log = LoggerFactory.getLogger(getClass());
		this.cluster = Cluster.builder().addContactPoint(host).build();
	}

	/**
	 * This method offers a blocking wait until the given condition is fulfilled or
	 * the timeout expired.
	 * 
	 * @param waitUntil condition
	 * @param timeout   max wait
	 */
	protected void waitForDbOperation(BooleanSupplier waitUntil, Duration timeout) {
		LocalTime out = LocalTime.now().plus(timeout);

		while (!waitUntil.getAsBoolean() || out.isAfter(LocalTime.now())) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				log.error(e.getLocalizedMessage());
			}
		}

		if (!waitUntil.getAsBoolean()) {
			throw new RuntimeException("DB operation failed or timed out.");
		}
	}

	/**
	 * Create the keyspace given to this object's constructor. If the keyspace
	 * already exists, nothing happens. The method waits until the keyspace has been
	 * created.
	 */
	public void createKeyspace() {
		Session session = cluster.connect();
		String createQuery = "CREATE KEYSPACE IF NOT EXISTS " + this.keyspace
				+ " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};";

		session.execute(createQuery);

		waitForDbOperation(() -> cluster.getMetadata().getKeyspace(this.keyspace) != null, TIMEOUT);
		session.close();
	}

	/**
	 * Drop keyspace if it exists. The method waits until the modification can be
	 * retrieved from the DB.
	 */
	public void dropKeyspace() {
		Session session = cluster.connect();

		String dropQuery = "DROP KEYSPACE IF EXISTS " + this.keyspace + ";";

		session.execute(dropQuery);
		waitForDbOperation(() -> cluster.getMetadata().getKeyspace(this.keyspace) == null, TIMEOUT);
		this.log.info("Dropped cassandra keyspace " + this.keyspace);
		session.close();
	}

	/**
	 * Create table for abstract value E1DetectorValue. If the predefined keyspace
	 * does not yet exist, this method creates it first. After the table has been
	 * created, the method blocks until one get retrieve the table from the DB.
	 * 
	 * @param tableName of table to create
	 */
	public void createTableE1DetectorValue(final String tableName) {
		createKeyspace();
		Session session = cluster.connect(keyspace);

		String createQuery = "CREATE TABLE IF NOT EXISTS " + tableName + "(pk uuid PRIMARY KEY,"
				+ "	start float, end float, id text, nVehContrib int, flow float,"
				+ "	occupancy float, speed float, harmonicMeanSpeed float, length float, nVehEntered int);";

		session.execute(createQuery);
		waitForDbOperation(() -> cluster.getMetadata().getKeyspace(this.keyspace).getTable(tableName) != null, TIMEOUT);
		this.log.info("Created table " + tableName + " in keyspace " + this.keyspace);
		session.close();
	}

	/**
	 * Drops the table with the given name in the predefined keyspace. If the table
	 * does not exist, nothing happens. Method blocks until change is made.
	 * 
	 * @param tableName to delete
	 */
	public void dropTableE1DetectorValue(final String tableName) {
		Session session = null;
		try {
			session = cluster.connect(keyspace);
		} catch (InvalidQueryException e) {
			// keyspace does not exists, so we do not need to do anything else
			this.log.debug("See exception, probably the keyspace " + keyspace + " does not exist.", e);
			return;
		}

		String dropQuery = "DROP TABLE IF EXISTS " + tableName + ";";
		session.execute(dropQuery);
		waitForDbOperation(() -> cluster.getMetadata().getKeyspace(this.keyspace).getTable(tableName) == null, TIMEOUT);
		this.log.info("Dropped table " + tableName + " in keyspace " + this.keyspace);
		session.close();
	}

	/**
	 * @param tableName
	 */
	public void createTable(String tableName) {
		if (E1DetectorValue.CQL_TABLENAME.equals(tableName)) {
			this.createTableE1DetectorValue(tableName);
			return;
		}

		if (TLSValue.CQL_TABLENAME.equals(tableName)) {
			this.createTableTLSValue(tableName);
			return;
		}

		throw new RuntimeException("Cannot associate table name " + tableName + " with a value type.");
	}

	/**
	 * @param tableName
	 */
	public void createTableTLSValue(String tableName) {
		createKeyspace();
		Session session = cluster.connect(keyspace);

		String createQuery = "CREATE TABLE IF NOT EXISTS " + tableName
				+ "(pk uuid PRIMARY KEY, time float, id text, programId text, phase int, state text);";

		session.execute(createQuery);
		waitForDbOperation(() -> cluster.getMetadata().getKeyspace(this.keyspace).getTable(tableName) != null, TIMEOUT);
		this.log.info("Created table " + tableName + " in keyspace " + this.keyspace);
		session.close();
	}
	
	/**
	 * 
	 */
	public void close() {
		this.cluster.close();
	}

}
