package net.pk.stream.flink.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;

import net.pk.stream.api.environment.EnvironmentConfig;
import net.pk.stream.flink.function.E1DetectorToEdgeValueMapper;
import net.pk.stream.flink.function.LaneToEdgeValueMapper;
import net.pk.stream.format.E1DetectorValue;
import net.pk.stream.format.LaneValue;

/**
 * @author peter
 *
 */
public class Emitter {

//	StreamExecutionEnvironment env;

	EnvironmentConfig envConfig = EnvironmentConfig.getInstance();

	private List<StreamJob> streams;

	/**
	 * @param e1DetStream
	 */
	public Emitter(E1DetectorValueStream e1DetStream) {
		this.streams = new ArrayList<>();
		this.streams.add(e1DetStream);
		if (this.streams.isEmpty()) {
			throw new RuntimeException(
					new IllegalStateException("Emitter cannot be instantiated with empty list of stream jobs."));
		}
	}

	/**
	 * @param e1DetStream
	 * @param laneStream
	 */
	public Emitter(E1DetectorValueStream e1DetStream, LaneValueStream laneStream) {
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
		E1DetectorValueStream e1 = (E1DetectorValueStream) streams.stream()
				.filter(s -> s instanceof E1DetectorValueStream).findFirst()
				.orElseThrow(() -> new RuntimeException("Emitter needs an object of " + E1DetectorValueStream.class));
		DataStream<E1DetectorValue> e1DetStream = (DataStream<E1DetectorValue>) e1.getStream();

		Optional<StreamJob> optionalLaneStream = (Optional<StreamJob>) streams.stream()
				.filter(s -> s instanceof LaneValueStream).findFirst();

		if (optionalLaneStream.isPresent()) {
			DataStream<LaneValue> laneStream = (DataStream<LaneValue>) optionalLaneStream.get().getStream();
			laneStream.map(new LaneToEdgeValueMapper())
					.writeAsText(this.envConfig.getAbsoluteFilePathEdgeValue(), WriteMode.OVERWRITE).setParallelism(1);

		} else {
			e1DetStream.map(new E1DetectorToEdgeValueMapper())
					.writeAsText(this.envConfig.getAbsoluteFilePathEdgeValue(), WriteMode.OVERWRITE).setParallelism(1);
		}

	}

}
