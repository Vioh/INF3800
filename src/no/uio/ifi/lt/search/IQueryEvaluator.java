package no.uio.ifi.lt.search;

import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.ranking.IRanker;

/**
 * Defines a query evaluator, i.e., the engine that evaluates the
 * query and actually implements the document retrieval task.
 * 
 * @author aleks
 */
public interface IQueryEvaluator {
	
	/**
	 * Evaluates the given {@link IQuery} against the set of {@link IDocuments}
	 * indexed in the given {@link IInvertedIndex}. The results are ranked by
	 * the given {@link IRanker}.
	 *
	 * @param query the query to evaluate
	 * @param invertedIndex the indexed documents to evaluate the query against
	 * @param ranker the ranker that assesses relevance and scores each document
	 * @return a set containing the highest-ranked results
	 */
	IResultSet evaluate(IQuery query, IInvertedIndex invertedIndex, IRanker ranker);

}
