package no.uio.ifi.lt.search;

import no.uio.ifi.lt.storage.IDocument;

/**
 * Defines an entry in a result set.
 * 
 * @author aleks
 * @see IResultSet
 * @see IQueryEvaluator
 */
public interface IResult {

	/**
	 * Returns the matching {@link IDocument} object.
	 * 
	 * @return the matching document
	 */
	IDocument getDocument();
	
	/**
	 * Returns how relevant the document is to the query.
	 * 
	 * @return the matching document's relevance score
	 */
	double getRelevance();
	
}
