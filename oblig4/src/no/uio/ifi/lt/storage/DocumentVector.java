package no.uio.ifi.lt.storage;
import java.util.Iterator;
import java.util.HashMap;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.tokenization.IToken;

public class DocumentVector implements IDocumentVector {
	/** The lexicon that contains all the unique terms in the collection */
	ILexicon lexicon;
	
	/** Length of the vector, aka. the magnitude */
	private double magnitude;
	
	/** Total number of documents hardcoded in */
	private final int N = 10000;
	
	/** 
	 * The document vector itself can be considered as a map that maps the
	 * termId (an int that represents the vector index) to the corresponding
	 * tfidf weight (a double that represents the vector entry).
	 * <p>
	 * We use HashMap as the main datastructure to access the data in O(1) time.
	 * And for optimization, this HashMap will only store the tfidf weights for
	 * the lexicon terms that have nonzero weights. 
	 */
	HashMap<Integer, Double> vector = new HashMap<Integer, Double>();
	
	/**
	 * Constructor. 
	 */
	public DocumentVector(IToken[] documentTerms, ILexicon lexicon, IInvertedIndex invertedIndex) {
		this.lexicon = lexicon;
		
		// Count the term occurrences to compute the tf weights.
		for(IToken term : documentTerms) {
			double tf = 0.0;
			int termId = lexicon.lookup(term.getValue());
			if(this.vector.containsKey(termId))
				tf = this.vector.get(termId);
			this.vector.put(termId, ++tf);
		}		
		// Add the idf contributions to the weights in the vector
		for(int termId : this.vector.keySet()) {			
			int df = invertedIndex.getPostingList(termId).size();
			double idf = Math.log(N/df);			
			double tfidf = this.vector.get(termId) * idf;
			this.vector.put(termId, tfidf);			
			this.magnitude += (tfidf*tfidf); // accumulate to magnitude^2
		}
		magnitude = Math.sqrt(magnitude);	
	}

	/**
	 * Implements the {@link IDocumentVector} interface. 
	 * See there for more details!
	 */
	public double getCosineSimilarity(IDocumentVector docVector) {
		double cosine = 0.0;
		for(int termId : this.vector.keySet()) {
			cosine += this.getWeight(termId) * docVector.getWeight(termId);
		}
		return cosine / (this.magnitude * docVector.getMagnitude());
	}
	
	/**
	 * Implements the {@link IDocumentVector} interface.
	 * See there for more details!
	 */
	public double getWeight(int lexiconId) {
		Double weight = this.vector.get(lexiconId);
		if(weight == null) return 0.0;
		return weight;
	}

	/**
	 * Implements the {@link IDocumentVector} interface.
	 * See there for more details!
	 */
	public double getMagnitude() {
		return this.magnitude;
	}
	
	/**
	 * Implements the {@link IDocumentVector} interface.
	 * See there for more details!
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
				String err = "This iterator does not support element removal!";
				throw new UnsupportedOperationException(err);
			}
		};
	}
}