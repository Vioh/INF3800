package no.uio.ifi.lt.search;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.indexing.Posting;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.ranking.IRanker;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;
import no.uio.ifi.lt.utils.Sieve;
import no.uio.ifi.lt.utils.HeapItem;

/**
 * Implements the query evaluation logic in a search engine.
 */
public class QueryEvaluator implements IQueryEvaluator {
	/** Defines the evaluation parameters. */
	private QueryEvaluatorSettings settings;	
	
	/** Where we emit messages, if at all. */
	private Logger logger;
	
	/**
	 * Constructor.
	 * @param settings defines the evaluation parameters
	 * @param logger defines where to emit log messages, if at all
	 */
	public QueryEvaluator(QueryEvaluatorSettings settings, Logger logger) {
		this.settings = settings;
		this.logger = logger;
	}
	
	//*************************** NEWLY ADDED CODES ****************************//
	
	/** The document collection which stores all the documents */
	private IDocumentStore documentStore;
	
	/** Defines the way we rank a document */
	private IRanker ranker;
	
	/** The query to evaluate */
	private IQuery query;
	
	/** For N-of-M matching, N is equal to M multiplied by the recallThreshold */
	private double N;
	
	/** An array of terms that are present in the query */
	private IToken[] queryTerms;
	
	/** An array over the postings lists for all of the query terms */
	private PostingList[] plists;
	
	/**
	 * Implements the {@link IQueryEvaluator} interface.
	 */
	public IResultSet evaluate(IQuery query, IInvertedIndex invertedIndex, IRanker ranker) {		
		// Paranoia.
		if (query.getNormalizedLength() == 0) {
			return new ResultSet(query, 0);
		}
		// Spam the logs?
		boolean debug = this.settings.debug && (this.logger != null) && this.logger.isLoggable(Level.FINEST);
		ranker.debug(debug); // should the ranker spam the logs as well?
		
		// Synchronize query processing with document processing.
		INormalizer normalizer = invertedIndex.getNormalizer();
		ITokenizer tokenizer = invertedIndex.getTokenizer();
		ILexicon lexicon = invertedIndex.getLexicon();
		this.documentStore = invertedIndex.getDocumentStore();

		// Split the normalized query into separate terms 
		String normalizedQuery = normalizer.normalize(query.getOriginalQuery());
		this.queryTerms = tokenizer.toArray(normalizedQuery);
		
		// Gather the postings lists of all the query terms
		this.plists = new PostingList[queryTerms.length];
		for(int i = 0; i < queryTerms.length; ++i) {
			String term = queryTerms[i].getValue();
			this.plists[i] = invertedIndex.getPostingList(lexicon.lookup(term));
		}
		
		// Initialize other necessary variables
		this.ranker = ranker;
		this.N = this.settings.rankThreshold * this.queryTerms.length;
		int[] pointers = new int[this.plists.length];
		Sieve<IDocument, Double> sieve = new Sieve<IDocument, Double>(this.settings.candidates);
		
		// Perform the document-at-at-time evaluation, and return the result set.
		while(evaluateDoc(pointers, sieve)) {}
		ResultSet results = new ResultSet(query, sieve.capacity());
		Iterator<HeapItem<IDocument, Double>> iterator = sieve.iterator();
		while(iterator.hasNext()) {
			HeapItem<IDocument, Double> item = iterator.next();
			results.appendResult(new Result(item.data, item.rank));
		}
 		results.sortByRelevance();
 		return results;
	}
	
	/**
	 * Evaluate the score/rank for a single document. The document to be evaluated
	 * is the one with the lowest docID among those that are located at the current
	 * positions on the postings lists, specified by the pointers[] array.
	 * <p>
	 *  
	 * @param pointers array of indices that point to current positions on the postings lists.
	 * @param sieve the sieve with the documents as data and integers as ranking.
	 * @return false if there are no docs left to evaluate, and true otherwise. 
	 */
	private boolean evaluateDoc(int[] pointers, Sieve<IDocument, Double> sieve) {
		// Get the minimum docID to know which document to evaluate.
		int minDocID = Integer.MAX_VALUE;
		int counter = 0; 
		for(int i = 0; i < pointers.length; ++i) {
			if(pointers[i] >= this.plists[i].size()) {
				continue; // no docs left in this postings list to evaluate.
			}
			int docID = plists[i].getPosting(pointers[i]).getDocumentId();
			if(docID < minDocID) minDocID = docID;
			++counter;
		}
		// Return false if there are no docs left to evaluate, or if we know 
		// for certain that the recall will be lower than the threshold.
		if(counter < this.N || counter == 0) return false;
	
		// Compute the ranking for the document
		this.ranker.reset();
		for(int i = 0; i < pointers.length; ++i) {
			if(pointers[i] >= this.plists[i].size()) {
				continue; // skip this list as it has already been traversed
			}
			Posting posting = this.plists[i].getPosting(pointers[i]);
			if(posting.getDocumentId() == minDocID) {
				ranker.update(this.queryTerms[i], posting, plists[i]);
				++pointers[i]; // increment the pointer for next document
			}
		}
		// Sift the document if the rank is higher than recall threshold
		IDocument doc = this.documentStore.getDocument(minDocID);
		double rank = ranker.evaluate(this.query, doc);
		if(rank >= N) sieve.sift(doc, rank);
		return true;
	}
}