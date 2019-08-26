package net.pk.stream.api.converter;

import java.io.File;

public interface Querying {

	public final static String DETECTOR_VALUE_FILE_PATH = System.getProperty("detectorvalue.filepath",
			System.getProperty("user.dir") + File.separator + "detector-value.csv");

	/**
	 * Output this object's stream.
	 */
	void out();

}
