package no.uio.ifi.lt.search;

import no.uio.ifi.lt.storage.IDocument;

/**
 * A very simple and straightforward implementation of the
 * {@link IResult} interface.
 * 
 * @author aleks
 */
public class Result implements IResult {

	/**
	 * Backs the {@link #getDocument()} method.
	 */
	private IDocument document;
	
	/**
	 * Backs the {@link #getRelevance()} method.
	 */
	private double relevance;

	/**
	 * Constructor.
	 * 
	 * @param document the matching document
	 * @param relevance determines how relevant the matching document is to the query
	 */
	public Result(IDocument document, double relevance) {
		this.document = document;
		this.relevance = relevance;
	}

	/**
	 * Implements the {@link IResult} interface.
	 */
	
	public IDocument getDocument() {
		return this.document;
	}

	/**
	 * Implements the {@link IResult} interface.
	 */
	
	public double getRelevance() {
		return this.relevance;
	}

}
