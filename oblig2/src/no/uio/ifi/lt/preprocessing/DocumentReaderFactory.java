package no.uio.ifi.lt.preprocessing;
import java.util.logging.Level;

/**
 * Factory class to create a document reader compatible with the filename extension.
 */
public class DocumentReaderFactory {
	/**
	 * Returns a document reader which is compatible with the filename
	 * @param filename the filename
	 * @return the corresponding document reader
	 */
	public static DocumentReader getInstance(String filename) {
		if (filename.contains(".txt")) {
			return new TextDocumentReader();
		}
		else if (filename.contains(".xml")) {
			return new XMLDocumentReader();
		}
		else {
			throw new RuntimeException("invalid file format: " + filename);
		}	
	}
}