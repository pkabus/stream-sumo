package net.pk.stream.api.file;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper class that provides system property keys for output files. Also
 * default values are set.
 * 
 * @author peter
 *
 */
public final class ValueFilePaths {

	public final static String E1DETECTOR_VALUE_KEY = "e1detectorvalue.filepath";
	public final static String TLS_VALUE_KEY = "tlsvalue.filepath";

	public static void setPathE1DetectorValue(final String path) {
		System.setProperty(E1DETECTOR_VALUE_KEY, path);
	}

	public static void setPathTLSValue(final String path) {
		System.setProperty(TLS_VALUE_KEY, path);
	}

	public static String getPathE1DetectorValue() {
		return System.getProperty(E1DETECTOR_VALUE_KEY,
				System.getProperty("user.dir") + File.separator + "e1detector-value.xml");
	}

	public static String getPathTLSValue() {
		return System.getProperty(TLS_VALUE_KEY, System.getProperty("user.dir") + File.separator + "tls-value.xml");
	}

	public static String getStreamDir() {
		if (System.getProperty(E1DETECTOR_VALUE_KEY) != null) {
			return StringUtils.substringBeforeLast(System.getProperty(E1DETECTOR_VALUE_KEY), File.separator);
		}
		
		return System.getProperty("user.dir");
	}
}
