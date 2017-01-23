package no.uio.ifi.lt.search;

import java.util.Map;


public interface ISearchEngine {

	/**
	 * Implements the actual retrieval task.
	 * 
	 * @param value the raw query string
	 * @return a result set comprising the most relevant documents
	 */
	IResultSet evaluate(String value);
	
	
	/**
	 * A simplistic evaluation, which returns a mapping
	 * between each lookup token and its frequency.
	 * 
	 * @param value the raw query string
	 * @return a mapping defining the frequency of each token
	 */
	Map<String,Integer> evaluateBrainDead(String value);

}