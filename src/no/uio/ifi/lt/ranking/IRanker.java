package no.uio.ifi.lt.ranking;

import no.uio.ifi.lt.indexing.Posting;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.search.IQuery;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.tokenization.IToken;

/**
 * Defines a ranker, i.e., an object that assesses how relevant
 * a document is for a query.
 * 
 * @author aleks
 * @see Evaluator
 */
public interface IRanker extends Cloneable {
	
	/**
	 * Constructor, sort of. For cloning a prototype.
	 * 
	 * @return a new, cloned instance of the ranker object.
	 */
	IRanker clone();
	
	/**
	 * Resets the ranker, i.e., prepares for evaluating another
	 * document.
	 */
	void reset();
	
	/**
	 * Tells the ranker that the client is interested in receiving debug
	 * information about the relevance computations , if possible. How
	 * and if this gets done is up to the ranker implementation.
	 * 
	 * @param value true if and only if the ranker should emit debug information
	 */
	void debug(boolean value);
	
	/**
	 * Tells the ranker to update its internals based on information
	 * from one query term. This method might be invoked multiple times
	 * if the query contains multiple terms.
	 * 
	 * @param token the query {@link IToken} for which the given {@link Posting} is for 
	 * @param posting the {@link Posting} that holds information about which document the term occurs in
	 * @param postingList the posting list in which the given {@link Posting} occurs
	 */
	void update(IToken token, Posting posting, PostingList postingList);
	
	/**
	 * Tells the ranker to evaluate how relevant the given {@link IDocument} is
	 * for the given {@link IQuery}, given all the previous invocations of
	 * {@link #update(IToken, Posting, PostingList)}.
	 * 
	 * @param query the {@link IQuery} used for the retrieval task
	 * @param document the {@link IDocument} to rank
	 * @return the relevance score for the given document
	 */
	double evaluate(IQuery query, IDocument document);

}
