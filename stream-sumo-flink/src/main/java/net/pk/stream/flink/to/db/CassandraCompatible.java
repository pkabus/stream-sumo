package net.pk.stream.flink.to.db;

import org.apache.flink.streaming.connectors.cassandra.CassandraSink.CassandraPojoSinkBuilder;
import org.apache.flink.streaming.connectors.cassandra.CassandraSink.CassandraSinkBuilder;

import com.datastax.driver.mapping.Mapper;

import net.pk.db.cassandra.config.DbConfig;
import net.pk.stream.flink.Streamable;
import net.pk.stream.format.AbstractValue;
import net.pk.stream.format.Loggable;

/**
 * @author peter
 *
 */
public interface CassandraCompatible<V extends AbstractValue> extends Streamable<V>, Loggable {

	/**
	 * 
	 */
	default void addCassandraSink() {
		String cassandraHost = DbConfig.getInstance().getCassandraHost();

		CassandraSinkBuilder<V> sinkBuilder = new CassandraPojoSinkBuilder<>(getStream(), getStream().getType(),
				getStream().getType().createSerializer(getStream().getExecutionEnvironment().getConfig()));

		try {
			sinkBuilder.setHost(cassandraHost)
					.setMapperOptions(() -> new Mapper.Option[] { Mapper.Option.saveNullFields(true) }).build();
		} catch (Exception e) {
			getLog().error("Cassandra failed", e);
		}
	}

	/**
	 * @return
	 */
	String getKeyspace();

	/**
	 * @return
	 */
	String getTableName();
}
