package net.pk.integrationtest.db.cassandra;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Session;

import net.pk.db.cassandra.DbBuilder;
import net.pk.db.cassandra.config.DbConfig;

/**
 * @author peter
 *
 */
@Disabled
public class DbBuilderTest {
	public final static String TEST_KEYSPACE = "junit5_testcase";
	public final static String TEST_TABLE_E1DETECTORVALUE = "e1detectorvalue";
	public final static String TEST_HOST = "127.0.0.1";

	private DbBuilder builder;

	@BeforeEach
	private void init() {
		DbConfig.getInstance().setCassandraHost(TEST_HOST);
		builder = new DbBuilder(TEST_KEYSPACE);

	}

	@AfterEach
	private void tearDown() {
		Cluster c = Cluster.builder().addContactPoint(TEST_HOST).build();
		Session s = c.connect();
		String drop = "DROP KEYSPACE IF EXISTS " + TEST_KEYSPACE + ";";
		s.execute(drop);
		c.close();
	}

	@Test
	public void testCreateKeyspace() {
		Cluster c = Cluster.builder().addContactPoint(TEST_HOST).build();

		assertNull(c.getMetadata().getKeyspace(TEST_KEYSPACE));
		builder.createKeyspace();
		assertNotNull(c.getMetadata().getKeyspace(TEST_KEYSPACE));
	}

	@Test
	public void testDropKeyspace() {
		Cluster c = Cluster.builder().addContactPoint(TEST_HOST).build();

		assertNull(c.getMetadata().getKeyspace(TEST_KEYSPACE));
		builder.createKeyspace();
		builder.dropKeyspace();
		assertNull(c.getMetadata().getKeyspace(TEST_KEYSPACE));
	}

	@Test
	public void testCreateTable() {
		Cluster c = Cluster.builder().addContactPoint(TEST_HOST).build();
		builder.createTableE1DetectorValue(TEST_TABLE_E1DETECTORVALUE);
		
		KeyspaceMetadata keyspace = c.getMetadata().getKeyspace(TEST_KEYSPACE);
		assertTrue(keyspace != null && keyspace.getTable(TEST_TABLE_E1DETECTORVALUE) != null);
	}

	@Test
	public void testDropTable() {
		Cluster c = Cluster.builder().addContactPoint(TEST_HOST).build();
		builder.createTableE1DetectorValue(TEST_TABLE_E1DETECTORVALUE);
		
		KeyspaceMetadata keyspace = c.getMetadata().getKeyspace(TEST_KEYSPACE);
		assertTrue(keyspace != null && keyspace.getTable(TEST_TABLE_E1DETECTORVALUE) != null);
		builder.dropTableE1DetectorValue(TEST_TABLE_E1DETECTORVALUE);
		
		assertNull(keyspace.getTable(TEST_TABLE_E1DETECTORVALUE));
	}

}
