package no.uio.ifi.lt.nbclassifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.indexing.InMemoryLexicon;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.utils.Sieve;

/**
 * Representation of a multinomial Naive Bayes classifier for document classification.  
 * The classifier is composed of a global lexicon, a prior distribution P(c), 
 * and a likelihood distribution P(w|c).
 */
public class MultinomialNaiveBayes implements DocumentClassifier {
	/** Lexicon comprising lexicon entries from all the classes */
	ILexicon globalLexicon;
	
	/** Prior class distribution P(c) */
	Map<Integer,Double> prior;
	
	/** 
	 * Likelihood distribution P(w|c).  The probability P(w_i|c_i)
	 * is encoded as the value in likelihood.get(c_i).get(w_i)
	 */
	Map<Integer, Map<Integer,Double>> likelihood;
	
	/**
	 * Constructs a multinomial Naive Bayes classifier from a message store
	 * @param store the message store
	 */
	public MultinomialNaiveBayes(MessageStore store) {	
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
		for (int i = 0 ; i < store.getIndexes().length ; i++) {
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
		prior = new HashMap<Integer,Double>();
		throw new RuntimeException("IMPLEMENT THIS METHOD");
	}
	
	/**
	 * Constructs the likelihood distribution for the classifier
	 * @param store the message score
	 */
	private void constructLikelihood(MessageStore store) {
		likelihood = new HashMap<Integer,Map<Integer,Double>>();
		for (int classNumber = 0 ; classNumber < store.getIndexes().length; classNumber++) {
			likelihood.put(classNumber, new HashMap<Integer,Double>());
			
			// NB: the lexicon IDs from the local (class-specific) indexes will generally not
			// correspond to the same lexicon IDs as in the global lexicon!
			IInvertedIndex localIndex = store.getIndexes()[classNumber];
			throw new RuntimeException("IMPLEMENT THIS METHOD");
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
		throw new RuntimeException("IMPLEMENT THIS METHOD");
		// return posterior.iterator().next().data;
	}
	
	/**
	 * Returns the prior probability of a given class
	 * @param classNumber the class
	 * @return its probability
	 */
	public double getPriorProbability(int classNumber) {
		return prior.get(classNumber);
	}

	/**
	 * Returns the likelihood probability of a word given its class
	 * @param word the words
	 * @param classNumber the class to condition on
	 * @return the resulting probability
	 */
	public double getLikelihoodProbability(String word, int classNumber) {
		int lexiconID = globalLexicon.lookup(word);
		if (lexiconID != ILexicon.INVALID) {
			return likelihood.get(classNumber).get(lexiconID);
		}
		else {
			return 0.0f;
		}
	}

	/**
	 * Return the number of classes in the classifier.
	 * @return the number of classes
	 */
	public int getNumberOfClasses() {
		return prior.size();
	}
}