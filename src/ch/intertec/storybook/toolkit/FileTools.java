package ch.intertec.storybook.toolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

/**
 * File tools.
 * 
 * @author martin
 *
 */
public class FileTools {
	
	public static File getHomeDir() {
		return new File(System.getProperty("user.home"));
	}
	
	/**
	 * Ensures that a file has the given extension. If the given
	 * file doesn't have this extension, the extension is added.
	 * 
	 * @param file the file
	 * @param extension the extension without the ".", eg. "txt"
	 * @return the file
	 */
	public static File ensureFileHasExtension(File file, String extension) {
		if (!FilenameUtils.isExtension(file.getName(), extension)) {
			file = new File(file.getAbsoluteFile() + "." + extension);
		}
		return file;
	}
	
	/**
	 * Reads a text file and returns the contents as a string.
	 * 
	 * @param file the file to read from
	 * @return the file contents
	 */
	public static String importTextFromFile(File file)
			throws FileNotFoundException {
		try {
			FileReader reader = new FileReader(file);
			char[] buf = new char[(int) file.length()];
			reader.read(buf);
			reader.close();
			return new String(buf);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
