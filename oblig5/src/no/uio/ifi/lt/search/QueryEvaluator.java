package no.uio.ifi.lt.search;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.indexing.Posting;
import no.uio.ifi.lt.ranking.IRanker;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.utils.HeapItem;
import no.uio.ifi.lt.utils.Sieve;

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
	@Override
	public IResultSet evaluate(IQuery query, IInvertedIndex invertedIndex, IRanker ranker) {
		// Paranoia.
		if (query.getNormalizedLength() == 0) {
			return new ResultSet(query, 0);
		}
		// Spam the logs?
		boolean debug =  this.settings.debug && (this.logger != null) && this.logger.isLoggable(Level.FINEST);
		ranker.debug(debug);

		// Core of the query evaluation: extract a set of ranked documents for
		// the query, inserted in a sieve data structure for efficient sorting
		Sieve<Integer,Double> bestDocumentIds = extractRankedDocumentsForQuery(query, invertedIndex, ranker, debug);
	
		// Create the set of matches to emit.
		// TODO: For fuzzy matching, implement optional reevaluation of candidates, e.g., based on edit distance.
		ResultSet results = new ResultSet(query, bestDocumentIds.size());

		for (HeapItem<Integer, Double> item : bestDocumentIds) {
			int documentId = item.data;
			double rankScore = item.rank;
			results.appendResult(new Result(invertedIndex.getDocumentStore().getDocument(documentId), rankScore));
		}
		// For client convenience, sort the results according to relevance.
		// TODO: Make this configurable, some clients may not care.
		results.sortByRelevance();

		// Your results, sir!
		return results;
	}

	/**
	 * Extracts a set of ranked documents for the query, and populate a sieve 
	 * data structure which <documentId, documentRank> pairs
	 * @param query the query
	 * @param invertedIndex the inverted index
	 * @param ranker the ranker
	 * @param debug whether to show debug information
	 * @return the sieve data structure containing the ranked documents
	 */
	private Sieve<Integer,Double> extractRankedDocumentsForQuery(IQuery query, 
				IInvertedIndex invertedIndex, IRanker ranker, boolean debug) {
		// Process a normalized version, not the raw value.
		String normalizedQuery = invertedIndex.getNormalizer().normalize(query.getOriginalQuery());

		// Split the query string up into terms.
		// TODO: Move to IQuery, to support a real query language.
		IToken[] queryTerms = invertedIndex.getTokenizer().toArray(normalizedQuery);

		// for each term in the query, we extract its corresponding posting list in the
		// inverted index, since we will have to traverse them
		List<TraversedPostingList> postingListsToTraverse = getPostingLists(queryTerms, invertedIndex);
		
		// We can require that at least N of the M query terms are present in the document,
		// for the document to be considered part of the result set. What should N be?
		// TODO: Take multiplicity into account, and not just uniqueness.
		int requiredCount = Math.max(1, Math.min(postingListsToTraverse.size(),
				(int) Math.round(this.settings.recallThreshold * postingListsToTraverse.size())));

		if (debug) {
			this.logger.finest(String.format("There are %d unique query terms to consider.", postingListsToTraverse.size()));
			this.logger.finest(String.format("At least %d unique query terms must occur in a document.", requiredCount));
		}
		// Define a helper to let us efficiently find the "best" results.
		Sieve<Integer, Double> bestDocumentIds = new Sieve<Integer, Double>(this.settings.candidates);

		// continue traversing the posting lists until we reach the minimum threshold
		while (postingListsToTraverse.size() >= requiredCount) {
			if (debug) {
				this.logger.finest(String.format("There are still %d lists remaining.", postingListsToTraverse.size()));
			}
			// Which document identifier are we evaluating now? We simply take the minimum
			// of the document Ids amongst the traversed posting lists
			int currentDocumentId = Collections.min(postingListsToTraverse).getCurrentPosting().getDocumentId();

			// Given the selected document ID, which posting lists contains it? 
			List<TraversedPostingList> matchingPostingLists = 
				getMatchingPostingListsForDocId(postingListsToTraverse, currentDocumentId);            

			// if the number of matching posting lists is above the required threshold,
			// we compute the relevance score.  Else, we ignore it
			if (matchingPostingLists.size() >= requiredCount) {
				IDocument document = invertedIndex.getDocumentStore().getDocument(currentDocumentId);

				// We compute the relevance score
				double score = computeRelevanceScore(matchingPostingLists, query, document, ranker, debug);

				// The current document may or may not be a keeper, depending on how well it
				// scored. Sift it through the sieve that preserves the "best" matches.
				if (score > this.settings.rankThreshold) {
					bestDocumentIds.sift(currentDocumentId, score);
				}
			}

			// we now increment the traversal position for the matching posting lists
			for (TraversedPostingList postingList : matchingPostingLists) {
				postingList.incrementPosition();

				// Has the current posting list reached its end? If so, there's one less list
				// remaining to be be dealt with for this query. 
				if (postingList.isFullyTraversed()) {
					postingListsToTraverse.remove(postingList);
				}
			}
		}
		return bestDocumentIds;
	}
	
	/**
	 * Returns a list of TraversedPostingList objects corresponding
	 * to the query terms, and initialized at their start position.
	 * @param queryTerms the query terms
	 * @param index the inverted index which contains the initial posting lists
	 * @return a list of TraversedPostingList
	 */
	private List<TraversedPostingList> getPostingLists(IToken[] queryTerms, IInvertedIndex index) {
		List<TraversedPostingList> postingLists = new ArrayList<TraversedPostingList>();
		ILexicon lexicon = index.getLexicon();
		for (int i = 0 ; i < queryTerms.length; i++) {
			int lexiconId = lexicon.lookup(queryTerms[i].getValue());
			if (lexiconId != ILexicon.INVALID) {
				TraversedPostingList newTraversedPostingList = 
					new TraversedPostingList(index.getPostingList(lexiconId), queryTerms[i]);
				postingLists.add(newTraversedPostingList);
			}
		}
		return postingLists;
	}

	/**
	 * Returns the subset of posting lists which contain the given 
	 * document id at their current index position.
	 * @param postingLists the traversed posting lists
	 * @param currentDocumentId the current document id
	 * @return the posting lists which contain the document id
	 */
	private List<TraversedPostingList> getMatchingPostingListsForDocId(
				List<TraversedPostingList> postingLists, int currentDocumentId) {
		List<TraversedPostingList> matchingPostingLists = new ArrayList<TraversedPostingList>();
		for (TraversedPostingList postingList : postingLists) {
			if (postingList.getCurrentDocumentId() == currentDocumentId) {
				matchingPostingLists.add(postingList);
			}
		}		
		return matchingPostingLists;
	}

	/**
	 * Computes the relevance score for the given set of matching 
	 * posting lists.  The matching posting lists must all an 
	 * identical document id at their current position.
	 * @param matchingPostingLists the matching posting lists
	 * @param query the query
	 * @param document the document
	 * @param ranker the ranker
	 * @param debug whether to show debug information
	 * @return the computed relevance score
	 */
	private double computeRelevanceScore(List<TraversedPostingList> matchingPostingLists, 
				IQuery query, IDocument document, IRanker ranker, boolean debug) {
		// Each document starts with a clean slate.
		ranker.reset();
		
		for (TraversedPostingList postingList : matchingPostingLists) {
			IToken queryTerm = postingList.getQueryTerm();
			if (debug) {
				this.logger.finest(String.format("Considering query term '%s' and that occurs in position %d in the query.",
						queryTerm.getValue(), queryTerm.getPosition()));
				this.logger.finest(String.format("Considering entry %d in the posting list", postingList.getCurrentPosition()));
			}
			// Get the entry in the current posting list that pertains to the
			// current document ID.
			Posting posting = postingList.getCurrentPosting();

			if (debug) {
				this.logger.finest(String.format("Current entry is for document %d and has %d position occurrences.",
						posting.getDocumentId(), posting.getOccurrenceCount()));
			}
			// Accumulate relevance data.
			ranker.update(queryTerm, posting, postingList.getPostingList());
		}

		// We now have fully accumulated all the relevance statistics for the current
		// document identifier, i.e., we have traversed all the relevant segments of the
		// relevant posting lists. Now, convert the accumulated statistics into a compound
		// relevance score for this document identifier.
		double rankScore = ranker.evaluate(query, document);

		if (debug) {
			this.logger.finest(String.format("Document %d is assigned rank score %g.", 
					matchingPostingLists.get(0).getCurrentDocumentId(), rankScore));
		}
		return rankScore;
	}
}