package net.pk.db.cassandra;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;

import net.pk.db.cassandra.config.DbConfig;

/**
 * @author peter
 *
 */
public class DbBuilder {

	private Logger log;
	private String keyspace;
	private String host;

	/**
	 * @param keyspace
	 * @param host
	 */
	public DbBuilder(final String keyspace) {
		this.host = DbConfig.getInstance().getCassandraHost();
		this.keyspace = keyspace;
		this.log = LoggerFactory.getLogger(getClass());
	}

	public void createKeyspace() {
		Cluster cluster = Cluster.builder().addContactPoint(host).build();
		Session session = cluster.connect();

		String createQuery = "CREATE KEYSPACE IF NOT EXISTS " + this.keyspace
				+ " WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'};";

		session.execute(createQuery);
	}

	public void dropKeyspace() {
		Cluster cluster = Cluster.builder().addContactPoint(host).build();
		Session session = cluster.connect();

		String dropQuery = "DROP KEYSPACE IF EXISTS " + this.keyspace + ";";

		session.execute(dropQuery);
	}

	/**
	 * @param tableName
	 */
	public void createTableE1DetectorValue(final String tableName) {
		Cluster cluster = Cluster.builder().addContactPoint(host).build();
		createKeyspace();
		Session session = cluster.connect(keyspace);

		String createQuery = "CREATE TABLE IF NOT EXISTS " + tableName + "(pk uuid PRIMARY KEY,"
				+ "	start float, end float, id text, nVehContrib int, flow float,"
				+ "	occupancy float, speed float, harmonicMeanSpeed float, length float, nVehEntered int);";

		session.execute(createQuery);
	}

	/**
	 * @param tableName
	 */
	public void dropTableE1DetectorValue(final String tableName) {
		Cluster cluster = Cluster.builder().addContactPoint(host).build();
		Session session = null;
		try {
			session = cluster.connect(keyspace);
		} catch (InvalidQueryException e) {
			// keyspace does not exists, so we do not need to do anything else
			this.log.debug("See exception, probably keyspace " + keyspace + " does not exist.", e);
			return;
		}

		String dropQuery = "DROP TABLE IF EXISTS " + tableName + ";";
		session.execute(dropQuery);
	}

}
