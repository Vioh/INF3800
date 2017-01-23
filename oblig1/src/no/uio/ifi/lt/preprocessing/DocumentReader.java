
package no.uio.ifi.lt.preprocessing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.uio.ifi.lt.storage.Document;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.storage.InMemoryDocumentStore;

/**
 * Helper class for reading a collection of documents and populating the document
 * store with it.
 *
 * @author  aleks, plison
 *
 */
public abstract class DocumentReader {

		
	/**
	 * Reads data from the given named file, creates {@link Document} objects from this,
	 * and populates the given {@link InMemoryDocumentStore}.
	 * 
	 * @param filename the name of the file containing the document data
	 * @param normalizer defines how the searchable data is to be normalized
	 * @param documentStore where we will insert the created {@link IDocument} objects
	 */
	public boolean readDocuments(String filename, INormalizer normalizer,
			                            IDocumentStore documentStore, Logger logger) {
		
		// Make some noise?
		if (logger != null) {
			logger.info(String.format("Reading documents from '%s'...", filename));
		}
		
		// Open the named stream and populate the document store.
		// TODO: Propagate exceptions.
		
		try {
			InputStream source = new FileInputStream(filename);
			return readDocuments(source, normalizer, documentStore, logger);
			
		} catch (FileNotFoundException exception) {
			if (logger != null) {
				logger.log(Level.SEVERE, String.format("File '%s' not found.", filename), exception);
			}
		}
		return false;
	}
	
	
	/**
	 * Reads the input source and populate the document store with the documents
	 * described in it, if any.  The method returns true if the read operation has
	 * been successful, and false otherwise.
	 * 
	 * @param source the input source
	 * @param normalizer normalizer for the documents
	 * @param documentStore document store to populate
	 * @param logger the logger
	 * @return true if read operation is successful, else false
	 */
	protected abstract boolean readDocuments(InputStream source, INormalizer normalizer, 
			IDocumentStore documentStore, Logger logger);

	
	
	/**
	 * Creates an individual document given the original and extra data, and the
	 * normalizer to apply on it
	 * 
	 * @param originalData the original data for the document (main data)
	 * @param extraData the optional extract data
	 * @param normalizer normalizer for the document
	 * @return the created document
	 */
	protected Document createDocument(String originalData, String extraData, INormalizer normalizer) {
	
		// How much normalized document data is there?
		String normalizedData = normalizer.normalize(originalData);
		
		// Create the document object.
		// TODO: Implement support for static rank.
		Document document = new Document();
		
		document.setOriginalData(originalData);
		document.setExtraData(extraData);
		document.setNormalizedLength(normalizedData.length());
		document.setStaticRank(0.0);
		
		return document;
	}
	
}
