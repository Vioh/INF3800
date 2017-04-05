package no.uio.ifi.lt.search;

public interface ISearchEngine {
	/**
	 * Implements the actual retrieval task.
	 * @param value the raw query string
	 * @return a result set comprising the most relevant documents
	 */
	IResultSet search(String value);
	
	/**
	 * Implements the find similar document lookup
	 * @param docID corresponding to document in document store
	 * @return a result set comprising the most relevant documents
	 */
	IResultSet findSimilar(int docID);
}