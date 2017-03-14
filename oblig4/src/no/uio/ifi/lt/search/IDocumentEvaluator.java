package no.uio.ifi.lt.search;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.ranking.IRanker;

public interface IDocumentEvaluator {
	IResultSet evaluate(IQuery document, int documentID, IInvertedIndex invertedIndex, IRanker ranker);
}