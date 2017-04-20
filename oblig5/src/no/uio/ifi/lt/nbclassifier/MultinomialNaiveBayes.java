package no.uio.ifi.lt.nbclassifier;
import java.util.Iterator;
import java.util.List;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.indexing.InMemoryLexicon;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.utils.Sieve;

/**
 * Representation of a multinomial Naive Bayes classifier for document classification.  
 * The classifier is composed of a global lexicon, a prior distribution P(c), 
 * and a likelihood distribution P(w|c).
 */
public class MultinomialNaiveBayes implements DocumentClassifier {
	/** Total number of classifying classes (or categories) */
	private int nClasses;
	
	/** Lexicon comprising lexicon entries from all the classes */
	private ILexicon globalLexicon;
	
	/** Prior class distribution prior[c] = P(c) = pc */
	private double[] prior;
		
	/** 
	 * Likelihood distribution P(w|c).  The probability P(w_j|c_i)
	 * is encoded as the value in likelihood[c_i][w_j].
	 * Note that I will mostly use "i" for the class number, and "j" 
	 * for the term ID in the global lexicon.
	 */
	private double[][] likelihood;
	
	/**
	 * Constructs a multinomial Naive Bayes classifier from a message store
	 * @param store the message store
	 */
	public MultinomialNaiveBayes(MessageStore store) {	
		this.nClasses = store.getIndexes().length;
		constructGlobalLexicon(store);	
		constructPrior(store);
		constructLikelihood(store);
	}

	/**
	 * Constructs a global lexicon comprising the lexicon entries for all the classes,
	 * as defined by their local indexes
	 * @param store the message store
	 */
	private void constructGlobalLexicon (MessageStore store) {
		globalLexicon = new InMemoryLexicon();
		for (int i = 0 ; i < this.nClasses; ++i) {
			IInvertedIndex localIndex = store.getIndexes()[i];
			Iterator<String> iterator = localIndex.getLexicon().iterator();
			while (iterator.hasNext()) {
				globalLexicon.addValue(iterator.next());
			}
		}
	}
	
	/**
	 * Constructs the prior distribution for the classifier
	 * @param store the message store
	 */
	private void constructPrior(MessageStore store) {
		this.prior = new double[this.nClasses];
		double nDocs = 0; // total number of docs in the entire training set
		
		for(int i = 0; i < this.nClasses; ++i) {
			this.prior[i] = store.getIndexes()[i].getDocumentStore().size();
			nDocs += this.prior[i];
		}
		for(int i = 0; i < this.nClasses; ++i) {
			this.prior[i] /= nDocs;
		}
	}
	
	/**
	 * Constructs the likelihood distribution for the classifier
	 * @param store the message score
	 */
	private void constructLikelihood(MessageStore store) {
		int nTerms = this.globalLexicon.size();
		this.likelihood = new double[nClasses][nTerms];
		
		for(int i = 0; i < this.nClasses; ++i) {
			IInvertedIndex localIndex = store.getIndexes()[i];
			double nTokens = 0; // total number of tokens in this entire class
			
			// The counting of occurrences of tokens will be done here
			Iterator<String> iter = localIndex.getLexicon().iterator();
			while(iter.hasNext()) {
				String term = iter.next();
				int localID = localIndex.getLexicon().lookup(term);
				int j = this.globalLexicon.lookup(term);
				PostingList pl = localIndex.getPostingList(localID);
				for(int k = 0; k < pl.size(); ++k) {
					likelihood[i][j] += pl.getPosting(k).getOccurrenceCount();
				}
				nTokens += likelihood[i][j];
			}			
			// Final Laplace smoothing of all the likelihood
			nTokens += nTerms; 
			for(int j = 0; j < nTerms; ++j) {
				likelihood[i][j] = (likelihood[i][j] + 1) / nTokens;
			}
		}
	}

	/**
	 * Classifies the document into one of the possible classes, given
	 * its content. The returned value is the class number for the class
	 * which has the highest probability given the document content.
	 * @param documentContent the document content (already normalized and tokenized)
	 * @return the class with highest probability
	 */
	public int classify (List<IToken> documentContent) {
		Sieve<Integer, Double> posterior = new Sieve<Integer, Double>(1);
		for(int i = 0; i < this.nClasses; ++i) {
			double totalProb = Math.log(getPriorProbability(i));
			for(IToken tok : documentContent) {
				totalProb += Math.log(getLikelihoodProbability(tok.getValue(), i));
			}
			posterior.sift(i, totalProb);
		}
		return posterior.iterator().next().data;
	}
	
	/**
	 * Returns the prior probability of a given class
	 * @param classNumber the class
	 * @return its probability
	 */
	public double getPriorProbability(int classNumber) {
		return this.prior[classNumber];
	}

	/**
	 * Returns the likelihood probability of a word given its class
	 * @param word the words
	 * @param classNumber the class to condition on
	 * @return the resulting probability
	 */
	public double getLikelihoodProbability(String word, int classNumber) {
		int lexiconID = this.globalLexicon.lookup(word);
		if (lexiconID != ILexicon.INVALID) {
			return this.likelihood[classNumber][lexiconID];
		}
		else {
			return 0.0;
		}
	}

	/**
	 * Return the number of classes in the classifier.
	 * @return the number of classes
	 */
	public int getNumberOfClasses() {
		return this.nClasses;
	}
}