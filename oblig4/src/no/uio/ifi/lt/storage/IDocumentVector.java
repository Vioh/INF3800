package no.uio.ifi.lt.storage;
import java.util.Iterator;

public interface IDocumentVector {	
	/**
	 * Get the tfidf weight corresponding to a specific lexicon term.
	 * @param lexiconId identifies a specific term in the lexicon. 
	 * @return tfidf weight for the term associated with lexiconId.
	 */
	double getWeight(int lexiconId);
	
	/**
	 * Compute the normalized cosine scores between this document and another.
	 * @param docVector the other document for the comparison.
	 * @return cosine similarity between the two documents.
	 */
	double getCosineSimilarity(IDocumentVector docVector);
	
	/**
	 * @return the magnitude (aka. lenght) of the document vector. 
	 */
	double getMagnitude();
	
	/**
	 * @return iterator of the tfidf weights, aka the actual vector. 
	 */
	Iterator<Double> getTfIdfScoreIterator();
}