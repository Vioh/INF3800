package no.uio.ifi.lt.search;

import java.util.logging.Logger;

import no.uio.ifi.lt.ranking.BrainDeadRanker;
import no.uio.ifi.lt.tokenization.BrainDeadTokenizer;

/**
 * A simple search engine that does exact matching.
 * 
 * @author aleks
 */
public class SimpleSearchEngine extends SearchEngine {

	/**
	 * Constructor.
	 * 
	 * @param filename
	 * @param separator
	 * @param logger
	 */
	public SimpleSearchEngine(String filename, Logger logger) {
	
		// Define suitable implementations for exact matching.	
	    // TODO: Use dependency injection.
		super(filename, new BrainDeadTokenizer(), new BrainDeadRanker(logger), logger);

	}

}
