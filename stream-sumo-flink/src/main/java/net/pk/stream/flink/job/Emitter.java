package net.pk.stream.flink.job;

import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.api.query.E1DetectorValueToEdgeConverter;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.EdgeValue;
import net.pk.stream.format.LaneValue;

/**
 * @author peter
 *
 */
public class Emitter {

	StreamExecutionEnvironment env;
	
	EnvironmentConfig envConfig = EnvironmentConfig.getInstance();

	/**
	 * @param env
	 */
	public Emitter(StreamExecutionEnvironment env) {
		this.env = env;
	}

	public void emit(final DataStream<E1DetectorValue> e1DetStream) {
		e1DetStream.map(e1DetVal -> {
			EdgeValue e = new EdgeValue();
			e.set(EdgeValue.KEY_ID, new E1DetectorValueToEdgeConverter().apply(e1DetVal));
			e.set(EdgeValue.KEY_TIMESTAMP, String.valueOf(e1DetVal.getEnd()));
			return e;
		}).writeAsText(envConfig.getAbsoluteFilePathEdgeValue(), WriteMode.OVERWRITE).setParallelism(1);
	}

	public void emit(final DataStream<E1DetectorValue> e1DetStream, final DataStream<LaneValue> laneStream) {
		//
	}

}
