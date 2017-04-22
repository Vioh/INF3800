package no.uio.ifi.lt.nbclassifier;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
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
	
	/** Total number of documents in the training set */
	private int nDocs;
	
	/** Lexicon comprising lexicon entries from all the classes */
	private ILexicon globalLexicon;
	
	/** Prior class distribution prior[c] = P(c) = pc */
	private double[] prior;
	
	/** 
	 * Likelihood distribution P(w|c).  The probability P(w|c)
	 * is encoded as the value in likelihood[c][w].
	 * <p>
	 * NB: Variable 'i' will mostly be used for the class number, and
	 * variable 'j' will be used for the termID in the global lexicon.
	 * In other words, P(w_j|c_i) = likelihood[i][j]
	 */
	private double[][] likelihood;
	
	/**
	 * Threshold ratio for declaring whether a word is a stop word 
	 * or not. This represents the percentage of the total number
	 * of documents in which a term must appear in, such that we
	 * can declare that term to be a stop word.
	 * <p>
	 * NB: Setting the threshold to be 1 means that none of the terms
	 * will be declared as stop words (i.e. set it to 1 if and only
	 * if we don't want to use the stop words).
	 */
	private final double THRESHOLD = 1.0;
	
	/**
	 * The stop dictionary (which is a set containing all the stop words
	 */
	private HashSet<String> stopDict = new HashSet<String>();
	
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
		this.nDocs = 0; // reset this to 0 (just in case)
		
		// Counting the documents in each class and add to the total
		for(int i = 0; i < this.nClasses; ++i) {
			this.prior[i] = store.getIndexes()[i].getDocumentStore().size();
			this.nDocs += this.prior[i];
		}		
		// Find the percentage (i.e. the prior) for each class 
		for(int i = 0; i < this.nClasses; ++i) {
			this.prior[i] /= nDocs;
		}
	}
	
	/**
	 * Constructs the likelihood distribution for the classifier, and
	 * identify all the stop words at the same time.
	 * @param store the message score
	 */
	private void constructLikelihood(MessageStore store) {		
		int nTerms = this.globalLexicon.size();
		this.likelihood = new double[nClasses][nTerms];
		
		/* This maps a term to the number of docs that the term is in */
		HashMap<String, Integer> stopMap = new HashMap<String, Integer>();

		/* LOOP FOR COMPUTING LIKELIHOODS FOR EACH CLASS, ONE AT A TIME */
		for(int i = 0; i < this.nClasses; ++i) {
			IInvertedIndex localIndex = store.getIndexes()[i];
			int nWords = 0; // total number of words in this entire class
			
			// Counting of term occurrences and document frequency for stop words
			Iterator<String> iter = localIndex.getLexicon().iterator();
			while(iter.hasNext()) {
				String term = iter.next();
				int localID = localIndex.getLexicon().lookup(term);
				int globalID = this.globalLexicon.lookup(term);
				PostingList pl = localIndex.getPostingList(localID);
				
				// Counting the occurrences
				int count = 0;
				for(int k = 0; k < pl.size(); ++k) {
					count += pl.getPosting(k).getOccurrenceCount();
				}
				likelihood[i][globalID] = (double) count;
				nWords += count;
				
				// Counting the number of documents a term is in (for stop words)
				if(stopMap.get(term) == null) {
					// no duplicate! simply add this new term to the map
					stopMap.put(term, pl.size());
				} else {
					// duplicate (from other classes)! simply update the counts
					int tmp = stopMap.get(term) + pl.size();
					stopMap.put(term, tmp);
				}
			}
			// The final Laplace smoothing for all of the likelihood in the 2D-array
			nWords += nTerms; // plus 1 on the denominator of the smoothing formula
			for(int j = 0; j < nTerms; ++j) {
				likelihood[i][j] = (likelihood[i][j] + 1) / nWords;
			}
		}
		/* CONSTRUCT THE FINAL STOP DICTIONARY FROM THE COUNT-DATA */
		int boundary = (int) (this.THRESHOLD * this.nDocs);
		Set<String> terms = stopMap.keySet();
		for(String t : terms) {
			if(stopMap.get(t) > boundary) stopDict.add(t);
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
				double prob = getLikelihoodProbability(tok.getValue(), i);
				if(prob < 0.0 || stopDict.contains(tok.getValue())) 
					continue; // skip this token!
				totalProb += Math.log(prob);
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
	 * @return the word probability, or -1 if the word is not in global lexicon
	 */
	public double getLikelihoodProbability(String word, int classNumber) {
		int lexiconID = this.globalLexicon.lookup(word);
		if (lexiconID != ILexicon.INVALID) {
			return this.likelihood[classNumber][lexiconID];
		}
		else {
			return -1;
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