package no.uio.ifi.lt.testing;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uio.ifi.lt.search.IResult;
import no.uio.ifi.lt.search.IResultSet;
import no.uio.ifi.lt.search.ISearchEngine;
import no.uio.ifi.lt.search.PhraseSearchEngine;
import no.uio.ifi.lt.search.SimpleSearchEngine;
import no.uio.ifi.lt.tokenization.BrainDeadTokenizer;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test program for assignment C
 */
public class ObligCTest extends TestCase {
	Logger logger;	

	public ObligCTest() {
		createLogger();
	}
	
	public void createLogger() {
		// Create a logger.
		logger = Logger.getLogger(ObligBTest.class.getName());
		Handler[] handlers = logger.getHandlers();
		for (int i = 0; i < handlers.length; ++i) {
			logger.removeHandler(handlers[i]);
		}
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);

		//  logger.addHandler(consoleHandler);
		logger.setLevel(Level.ALL);		
	}

	/**
	 * N-of-M search on the CRAN document collection
	 */
	public void testCran() {
		// CRAN document collection
		String filename = "data/cran.xml";

		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new SimpleSearchEngine(filename, logger);			
		String query = "paper flow lateral pressure body boundary article";

		System.out.println("Looking up '" + query + "' with N-of-M matching:");
		long before = System.nanoTime();

		IResultSet results = engine.evaluate(query);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		assertEquals(10, results.size());
		Iterator<IResult> resultIterator = results.iterator();

		// Checking the first result
		IResult firstResult = resultIterator.next();
		assertTrue(firstResult.getDocument().getOriginalData().startsWith("the present article describes an investigation of"));
		assertEquals(6.0, firstResult.getRelevance(), 0.00001);

		// Uncomment to see all results
		for (IResult result : results) {
			System.out.println(result.getRelevance());
			System.out.println(result.getDocument().getOriginalData());
			System.out.println(result.getDocument().getExtraData());
		}
	}

	/**
	 * N-of-M search on the sentences from the WeScience project
	 */
	public void testWeScience() {
		// Collection of 1-line documents
		String filename = "data/wescience.txt";

		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new SimpleSearchEngine(filename, logger);			
		String query = "java linux programs functionality";

		System.out.println("Looking up '" + query + "' with N-of-M matching:");
		long before = System.nanoTime();

		IResultSet results = engine.evaluate(query);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		assertEquals(10, results.size());
		Iterator<IResult> resultIterator = results.iterator();

		// Checking the first result
		IResult firstResult = resultIterator.next();
		assertTrue(firstResult.getDocument().getOriginalData().startsWith("[10160370]"));
		assertEquals(4.0, firstResult.getRelevance(), 0.00001);

		// Uncomment to see all results
		// for (IResult result : results) {
			// System.out.println(result.getRelevance());
			// System.out.println(result.getDocument().getOriginalData());
			// System.out.println(result.getDocument().getExtraData());
		// }
	}
	
	public static void main(String[] args) {
		ObligCTest test = new ObligCTest();
		test.createLogger();
		test.testCran();
		test.testWeScience();
	}
}