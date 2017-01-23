package no.uio.ifi.lt.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.uio.ifi.lt.preprocessing.DocumentReader;
import no.uio.ifi.lt.preprocessing.DocumentReaderFactory;
import no.uio.ifi.lt.preprocessing.INormalizer;

/**
 * A simple in-memory implementation of {@link IDocumentStore}, suitable for small to
 * medium-sized document collections.
 * 
 * @author aleks
 */
public class InMemoryDocumentStore implements IDocumentStore {
	
	/**
	 * All documents.
	 */
	private List<IDocument> documents = new ArrayList<IDocument>();
	
	/**
	 * Constructor.
	 */
	public InMemoryDocumentStore() {		
	}

	/**
	 * Constructor.
	 * 
	 * @param filename
	 * @param separator
	 * @param normalizer
	 * @param logger
	 */
	public InMemoryDocumentStore(String filename, INormalizer normalizer, Logger logger) {		
		if (!DocumentReaderFactory.getInstance(filename).readDocuments(filename, normalizer, this, logger)) {
			throw new RuntimeException();
		}
	}

	/**
	 * Implements the {@link IDocumentStore} interface.
	 */
	
	public IDocument getDocument(int documentId) {
		return this.documents.get(documentId);
	}

	/**
	 * Implements the {@link IDocumentStore} interface.
	 */
	
	public int putDocument(IDocument document) {
		int documentId = this.size();
		this.documents.add(document);
		return documentId;
	}

	/**
	 * Implements the {@link IDocumentStore} interface.
	 */
	
	public int size() {
		return this.documents.size();
	}

}
