package net.pk.integrationtest.traas.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

/**
 * Abstract helper test class.
 * 
 * @author peter
 *
 */
public abstract class UpdateFileIntegrationTest {

	/**
	 * 
	 */
	public static final String TEST_FILE_FULL = "detector-value-test-input.csv";
	/**
	 * 
	 */
	public static final String TEST_FILE_PART_0 = "detector-value-test-input-part0.csv";
	/**
	 * 
	 */
	public static final String TEST_FILE_PART_1 = "detector-value-test-input-part1.csv";
	/**
	 * 
	 */
	public static final String TEST_FILE_PART_2 = "detector-value-test-input-part2.csv";
	/**
	 * 
	 */
	public static final String TEST_FILE_PART_3 = "detector-value-test-input-part3.csv";
	/**
	 * 
	 */
	public static final String TEST_FILE_PART_4 = "detector-value-test-input-part4.csv";
	/**
	 * 
	 */
	public static final String TEST_FILE_IMAGINARY = "detector-value-test-input-i.csv";

	/**
	 * Copy the file with the given name to the file with the second argument. This
	 * all happens in the folder 'target/test-classes'.
	 * 
	 * @param sourceName of file in target/test-classes
	 * @param targetName of file in target/test-classes
	 */
	protected void copyFileForce(String sourceName, String targetName) {
		File source = Paths.get("target", "test-classes", sourceName).toFile();
		File target = Paths.get("target", "test-classes", targetName).toFile();

		if (target.exists()) {
			target.delete();
		}

		try {
			FileUtils.copyFile(source, target);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
