package net.pk.stream.flink.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;

import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.flink.function.E1DetectorToEdgeValueMapper;
import net.pk.stream.format.E1DetectorValue;

/**
 * @author peter
 *
 */
public class Emitter {

//	StreamExecutionEnvironment env;

	EnvironmentConfig envConfig = EnvironmentConfig.getInstance();

	private List<StreamJob> streams;

	/**
	 * @param env
	 * @param e1DetStream
	 */
	public Emitter(E1DetectorValueStream e1DetStream) {
//		this.env = env;
		this.streams = new ArrayList<>();
		this.streams.add(e1DetStream);
		if (this.streams.isEmpty()) {
			throw new RuntimeException(
					new IllegalStateException("Emitter cannot be instantiated with empty list of stream jobs."));
		}
	}

	/**
	 * @param env
	 * @param e1DetStream
	 * @param laneStream
	 */
	public Emitter(E1DetectorValueStream e1DetStream, LaneValueStream laneStream) {
//		this.env = env;
		this.streams = new ArrayList<>();
		this.streams.add(e1DetStream);
		this.streams.add(laneStream);
		if (this.streams.isEmpty()) {
			throw new RuntimeException(
					new IllegalStateException("Emitter cannot be instantiated with empty list of stream jobs."));
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void toFile() {
		E1DetectorValueStream e1DetStream = (E1DetectorValueStream) streams.stream()
				.filter(s -> s instanceof E1DetectorValueStream).findFirst()
				.orElseThrow(() -> new RuntimeException("Emitter needs a object of " + E1DetectorValueStream.class));

		DataStream<E1DetectorValue> stream = (DataStream<E1DetectorValue>) e1DetStream.getStream();
		stream.map(new E1DetectorToEdgeValueMapper())
				.writeAsText(this.envConfig.getAbsoluteFilePathEdgeValue(), WriteMode.OVERWRITE).setParallelism(1);
	}

}
