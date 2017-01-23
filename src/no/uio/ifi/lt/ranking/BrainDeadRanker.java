package no.uio.ifi.lt.ranking;

import java.util.logging.Logger;

import no.uio.ifi.lt.indexing.Posting;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.search.IQuery;
import no.uio.ifi.lt.search.IQueryEvaluator;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.tokenization.IToken;

/**
 * Implements an extremely simple relevance computation.
 * 
 * @author aleks
 * @see IQueryEvaluator
 */
public class BrainDeadRanker implements IRanker {
	
	/**
	 * For emitting log messages, if any.
	 */
	private Logger logger;
	
	/**
	 * Keeps track of how many times the {@link #update(IToken, Posting, PostingList)}
	 * method was invoked.
	 */
	private int hitCount;
	
	/**
	 * Emit debug information?
	 */
	private boolean debug;
	
	/**
	 * Constructor.
	 */
	public BrainDeadRanker(Logger logger) {
		this.logger = logger;
		this.debug = false;
		this.reset();
	}

	/**
	 * Implements the {@link IRanker} interface.
	 */
	
	public IRanker clone() {
		return new BrainDeadRanker(this.logger);
	}

	/**
	 * Implements the {@link IRanker} interface.
	 */
	
	public void debug(boolean value) {
		this.debug = value;
	}
	
	/**
	 * Implements the {@link IRanker} interface.
	 */
	
	public double evaluate(IQuery query, IDocument document) {
		return this.hitCount;
	}

	/**
	 * Implements the {@link IRanker} interface.
	 */
	
	public void reset() {
		this.hitCount = 0;
	}

	/**
	 * Implements the {@link IRanker} interface.
	 */
	
	public void update(IToken token, Posting posting, PostingList postingList) {
		
		this.hitCount++;
		
		// Log spam?
		if (this.debug && this.logger != null) {
			this.logger.finest(String.format("Token '%s' occurs across %d documents.", token.getValue(), postingList.size()));
			this.logger.finest(String.format("Token '%s' occurs %d times in the current document.", token.getValue(), posting.getOccurrenceCount()));
		}

	}

}
