package no.uio.ifi.lt.search;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.ranking.IRanker;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;

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
	
	/**
	 * Implements the {@link IQueryEvaluator} interface.
	 */
	public IResultSet evaluate(IQuery query, IInvertedIndex invertedIndex, IRanker ranker) {		
		// Paranoia.
		if (query.getNormalizedLength() == 0) {
			return new ResultSet(query, 0);
		}
		// Spam the logs?
		boolean debug =  this.settings.debug && (this.logger != null) && this.logger.isLoggable(Level.FINEST);

		// Should the ranker spam the logs as well?
		ranker.debug(debug);
		
		// Synchronize query processing with document processing.
		INormalizer normalizer = invertedIndex.getNormalizer();
		ITokenizer tokenizer = invertedIndex.getTokenizer();
		ILexicon lexicon = invertedIndex.getLexicon();
		IDocumentStore documentStore = invertedIndex.getDocumentStore();

		// Process a normalized version, not the raw value.
		String normalizedQuery = normalizer.normalize(query.getOriginalQuery());

		// Split the query string up into terms.
		IToken[] queryTerms = tokenizer.toArray(normalizedQuery);
		
		// TODO: 
		// 
		// Now you have all the queries, and an inverted index. Your task is to retrieve ranks for each document 
		// according to the recall of the document, and make sure you keep track of the highest scoring documents!
		// 
		// After doing this, you can return the most relevant documents
		// as a ResultSet, containing no more than 10 results. 
		// (you should use the value in this.settings.candidates for the size of the ResultSet). 
		// 
		// Do program efficiently! Do not traverse unnecessary, or keep things in memory if
		// it is not called for. Optimizing will be rewarded!
		// 
		// There is pre-code (i.e. Sieve) you may find useful, or you may program everything yourself!
		//
		// THE LAST TWO LINES COULD LOOK LIKE THIS:		
		// 		results.sortByRelevance();
		// 		return results;
		throw new RuntimeException("Your task is to complete this method!");
	}
}