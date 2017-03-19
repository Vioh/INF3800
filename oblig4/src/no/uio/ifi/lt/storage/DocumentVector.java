package no.uio.ifi.lt.storage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.search.IQueryEvaluator;
import no.uio.ifi.lt.tokenization.IToken;

public class DocumentVector implements IDocumentVector {
	/** The lexicon that contains all the unique terms in the collection */
	ILexicon lexicon;
	
	/** Length of the vector, defined by the formal mathematical definition */
	private double length;
	
	/** Total number of documents hardcoded in */
	private final int N = 10000;
	
	/** 
	 * The vector itself is represented by a hashmap that maps from 
	 * termId (int) to the corresponding ifidf weight (double). 
	 * <p>
	 * Note that the hashmap stores only the lexicon terms that have
	 * nonzero weights. This is nothing more than an optimization.
	 */
	HashMap<Integer, Double> vector = new HashMap<Integer, Double>();
	
	/**
	 * Constructor. 
	 */
	public DocumentVector(IToken[] documentTerms, ILexicon lexicon, IInvertedIndex invertedIndex) {
		this.lexicon = lexicon;
		
		// Count the term occurrences to compute the tf weights.
		for(IToken term : documentTerms) {
			int termId = lexicon.lookup(term.getValue());
			double tf = 0.0;
			
			if(this.vector.containsKey(termId)) {
				tf = this.vector.get(termId);
			}
			this.vector.put(termId, ++tf);
		}
		
		// Add the idf contributions to the tfidf weights in the vector.
		for(int termId : this.vector.keySet()) {			
			int df = invertedIndex.getPostingList(termId).size();
			double idf = Math.log(N/df);			
			double tfidf = this.vector.get(termId) * idf;
			this.vector.put(termId, tfidf);			
			this.length += (tfidf*tfidf); // accumulate to length^2
		}
		length = Math.sqrt(length);	
	}

	/**
	 * Implements the {@link IDocumentVector} interface.
	 */
	public double getCosineSimilarity(IDocumentVector docVector) {
		double cosine = 0.0;
		for(int termId : this.vector.keySet()) {
			cosine += this.getWeight(termId) * docVector.getWeight(termId);
		}
		return cosine / (this.length * docVector.getLength());
	}
	
	/**
	 * Implements the {@link IDocumentVector} interface.
	 */
	public double getWeight(int lexiconId) {
		Double weight = this.vector.get(lexiconId);
		if(weight == null) return 0.0;
		return weight;
	}

	/**
	 * Implements the {@link IDocumentVector} interface.
	 */
	public double getLength() {
		return this.length;
	}
	
	/**
	 * Implements the {@link IDocumentVector} interface.
	 */
	public Iterator<Double> getTfIdfScoreIterator() {
		
		return new Iterator<Double>() {	
			private int pos = 0;
			
			public boolean hasNext() {
				return pos < lexicon.size();
			}
			
			public Double next() {
				return getWeight(pos++);
			}
			
			public void remove() {
				String error = "Iterator does not support element removal!";
				throw new UnsupportedOperationException(error);
			}
		};
	}
}