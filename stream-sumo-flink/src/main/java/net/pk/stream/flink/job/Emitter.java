package net.pk.stream.flink.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;

import net.pk.stream.api.environment.EngineMode;
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
		
		// if server engine is lane based, create sink for lane value stream
		if (this.envConfig.getEngineMode() == EngineMode.LANE_BASED) {
			DataStream<LaneValue> laneStream = (DataStream<LaneValue>) streams.stream()
					.filter(s -> s instanceof LaneValueStream).findFirst()
					.orElseThrow(() -> new RuntimeException("No lane stream found.")).getStream();
			laneStream.map(new LaneToEdgeValueMapper()) //
					.writeAsText(this.envConfig.getAbsoluteFilePathEdgeValue(), WriteMode.OVERWRITE).setParallelism(1);
		}

		// if server engine is e1 detector based, create sink for e1 detector value stream
		if (this.envConfig.getEngineMode() == EngineMode.E1DETECTOR_BASED) {
			DataStream<E1DetectorValue> e1DetStream = (DataStream<E1DetectorValue>) streams.stream()
					.filter(s -> s instanceof E1DetectorValueStream).findFirst()
					.orElseThrow(() -> new RuntimeException("No e1 detector stream found.")).getStream();
			e1DetStream.map(new E1DetectorToEdgeValueMapper()) //
					.writeAsText(this.envConfig.getAbsoluteFilePathEdgeValue(), WriteMode.OVERWRITE).setParallelism(1);
		}

	}

}
