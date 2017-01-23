package no.uio.ifi.lt.search;

import no.uio.ifi.lt.preprocessing.INormalizer;

/**
 * A very simple and straightforward implementation of the
 * {@link IQuery} interface.

 * @author aleks
 */
public class Query implements IQuery {

	/**
	 * The original query string.
	 */
	private String originalQuery;
		
	/**
	 * The length of a normalized version of {@link #originalQuery}.
	 */
	private int normalizedLength;
	
	/**
	 * Constructor.
	 * 
	 * @param originalQuery the raw query string
	 * @param normalizer defines how to normalize the query string
	 */
	public Query(String originalQuery, INormalizer normalizer) {
		this.originalQuery = originalQuery;
		this.normalizedLength = normalizer.normalize(originalQuery).length();
	}
	
	/**
	 * Implements the {@link IQuery} interface.
	 */
	
	public int getNormalizedLength() {
		return this.normalizedLength;
	}

	/**
	 * Implements the {@link IQuery} interface.
	 */
	
	public String getOriginalQuery() {
		return this.originalQuery;
	}
	
}
