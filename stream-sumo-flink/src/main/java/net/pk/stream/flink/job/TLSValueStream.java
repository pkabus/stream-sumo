package net.pk.stream.flink.job;

import javax.annotation.Nullable;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.pk.db.cassandra.config.DbConfig;
import net.pk.stream.api.query.Querying;
import net.pk.stream.flink.converter.PlainTextToStreamConverter;
import net.pk.stream.format.TLSValue;

/**
 * @author peter
 *
 */
public class TLSValueStream extends StreamJob implements Querying {

	private String host;
	private int port;
	private boolean useDb;
	@Nullable
	private DataStream<TLSValue> stream;
	private Logger log;
	
	/**
	 * @param host
	 * @param port
	 */
	public TLSValueStream(final String host, final int port) {
		super();
		this.host = host;
		this.port = port;
		this.useDb = DbConfig.getInstance().getCassandraHost() != null;
		this.log = LoggerFactory.getLogger(this.getClass());
	}
	
	@Override
	public void out() {
		DataStreamSource<String> streamSource = getEnv().socketTextStream(host, port);
		DataStream<TLSValue> tlsValueStream = PlainTextToStreamConverter.convertXmlToTLSValueStream(streamSource);
		tlsValueStream.print();
		
		try {
			getEnv().execute();
		} catch (Exception e) {
			log.error("Flink job " + this.getClass() + " failed.", e);
		}
	}

}
