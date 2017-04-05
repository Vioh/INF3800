package no.uio.ifi.lt.search;
import java.util.logging.Logger;
import no.uio.ifi.lt.ranking.ShingleRanker;
import no.uio.ifi.lt.tokenization.ShingleGenerator;

/**
 * A simple search engine that does fuzzy matching using n-grams.
 */
public class FuzzySearchEngine extends SearchEngine {
	/**
	 * Constructor.
	 * @param filename
	 * @param separator
	 * @param logger
	 */
	public FuzzySearchEngine(String filename, Logger logger) {
		// Define suitable implementations for fuzzy matching.
		
		/*
		 * Using 3-gram shingles!
		 */
		super(filename, logger, new ShingleGenerator(3), new ShingleRanker(3, logger));
	}
}