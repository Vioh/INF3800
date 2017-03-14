package no.uio.ifi.lt.storage;
import java.util.Iterator;

public interface IDocumentVector {	
	/**
	 * @param lexiconId identifies the term for which we want the associated posting list
	 * @return tf-idf for term associated with lexiconId
	 */
	double get(int lexiconId);
	
	/**
	 * @param docVector for comparison
	 * @return cosine similarity between the two documents
	 */
	double getCosineSimilarity(IDocumentVector docVector);
	
	/**
	 * @return the length of the document vector
	 */
	int size();
	
	/**
	 * @return iterator of the tf-idf weights, aka the actual vector
	 */
	Iterator<Double> getTfIdfScoreIterator();
}