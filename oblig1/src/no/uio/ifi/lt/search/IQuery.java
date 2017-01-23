package no.uio.ifi.lt.search;

import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.storage.IDocument;

/**
 * Defines a query, i.e., the basis for the document retrieval.
 * Matched against a {@link IDocument}.
 * 
 * @author aleks
 * @see IDocument
 */
public interface IQuery {
	
	/**
	 * Returns the query string as it was originally entered.
	 * 
	 * @return the original query string
	 */
	String getOriginalQuery();
	
	/**
	 * Returns the length of the query string after it has been
	 * normalized by a suitable {@link INormalizer}.
	 * 
	 * @return the length of the normalized version of the query string
	 */
	int getNormalizedLength();

}
