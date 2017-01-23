package no.uio.ifi.lt.storage;


/**
 * Defines a document store, i.e., a mechanism that handles storage
 * and retrieval of {@link IDocument} objects. Documents are assumed
 * uniquely identified by means of integers. Furthermore, if there are
 * N documents stored, these integer identifiers are assumed to be in
 * the range {0, ..., N - 1}.
 * 
 * @author aleks
 */
public interface IDocumentStore {
	
	/**
	 * Returns the size of the document store, i.e., how many
	 * documents that are stored.
	 * 
	 * @return the number of documents contained in the document store
	 */
	int size();

	/**
	 * Returns the {@link IDocument} object identified by the document
	 * identifier. Throws an exception if an invalid document identifier
	 * was provided.
	 * 
	 * @param documentId identifies the document to retrieve
	 * @return the identified {@link IDocument} object
	 * @throws ArrayOutOfBoundsException
	 */
	IDocument getDocument(int documentId);
		
	/**
	 * Adds the given {@link IDocument} object to the store. A document
	 * identifier gets assigned by teh store and is returned.
	 * 
	 * 
	 * @param document the {@link IDocument} object to store
	 * @return the identifier associated with the stored document
	 */
	int putDocument(IDocument document);

}
