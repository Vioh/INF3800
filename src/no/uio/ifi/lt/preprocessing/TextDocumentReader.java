package no.uio.ifi.lt.preprocessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;


import no.uio.ifi.lt.storage.Document;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.storage.InMemoryDocumentStore;

/**
 * Helper class that reads in documents from some external location
 * and populates a given document store.
 * 
 * @author aleks, plison
 * @see IDocument
 * @see IDocumentStore
 */
public class TextDocumentReader extends DocumentReader {
	
	
	// separator between original and extra data for document collections
	// described in a text format with one line per document
	public static final String SEPARATOR = "\t";
	
	
	/**
	 * Reads data from the given source stream, creates {@link Document} objects from this,
	 * and populates the given {@link InMemoryDocumentStore}.
	 * 
	 * @param source the input stream containing the document data
	 * @param normalizer defines how the searchable data is to be normalized
	 * @param documentStore where we will insert the created {@link IDocument} objects
	 */
	@Override
	protected boolean readDocuments(InputStream source, INormalizer normalizer,
			                            IDocumentStore documentStore, Logger logger) {
		
		String line;
		
		// Keep track of how many lines we skipped.
		int skipCount = 0;
		
		// Keep track of how many documents we inserted.
		int insertCount = 0;
		
		// The return status.
		boolean ok = true;
		
		// One document per line.
		BufferedReader reader = new BufferedReader(new InputStreamReader(source));

		try {
			while ((line = reader.readLine()) != null) {
				
				// Skip empty lines.
				if (line.length() == 0 || line.matches("^\\s+$")) {
					++skipCount;
					continue;
				}
				
				String originalData = line;
				String extraData = null;

				// Split it up?
				if (SEPARATOR != null && SEPARATOR.length() > 0) {
					int where = line.indexOf(SEPARATOR);
					if (where != -1) {
						originalData = line.substring(0, where);
						extraData = line.substring(where + SEPARATOR.length());
					}
				}
				
				// create the document
				Document document = createDocument(originalData, extraData, normalizer);
				
				// Store it!
				documentStore.putDocument(document);
				
				++insertCount;
					
			}
		} catch (IOException exception) {
			ok = false;
			if (logger != null) {
				logger.log(Level.SEVERE, "Error reading stream.", exception);
			}
		}
				
		// Make some noise?
		if (logger != null) {
			logger.info(String.format("Added %d documents to the document store.", insertCount));
			logger.info(String.format("Skipped %d lines when reading stream data.", skipCount));
		}
		
		return ok;
	}
	
	

}
