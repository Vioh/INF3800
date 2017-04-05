package no.uio.ifi.lt.testing;
import java.util.Iterator;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uio.ifi.lt.search.IResult;
import no.uio.ifi.lt.search.IResultSet;
import no.uio.ifi.lt.search.ISearchEngine;
import no.uio.ifi.lt.search.SimpleSearchEngine;
import junit.framework.TestCase;

/**
 * Test program for assignment D
 */
public class ObligDTest extends TestCase {
	Logger logger;

	public ObligDTest() {
		createLogger();
	}

	public void createLogger() {
		// Create a logger.
		logger = Logger.getLogger(ObligDTest.class.getName());
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
	 * Search with TF-IDF on the CRAN document collection
	 */
	public void testCranTfIdf() {
		// CRAN document collection
		String filename = "data/cran.xml";

		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new SimpleSearchEngine(filename, logger);
		String query = "paper flow lateral pressure body boundary article";

		System.out.println("Looking up '" + query + "' with TF-IDF ranker:");
		long before = System.nanoTime();
		IResultSet results = engine.search(query);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		assertEquals(10, results.size());
		Iterator<IResult> resultIterator = results.iterator();
	
		// checking the first 3 results
		IResult firstResult = resultIterator.next();
		IResult secondResult = resultIterator.next();
		IResult thirdResult = resultIterator.next();
		assertTrue(firstResult.getDocument().getOriginalData().startsWith("\nthe present article describes an investigation of"));
		assertTrue(secondResult.getDocument().getOriginalData().startsWith("\nthe interaction between shock waves"));
		assertTrue(thirdResult.getDocument().getOriginalData().startsWith("\napproximate analytical solutions"));

		// uncomment to see all results
		for (IResult result : results) {
			System.out.println(result.getRelevance());
			System.out.println(result.getDocument().getOriginalData());
			System.out.println(result.getDocument().getExtraData());	
		}
	}

	/**
	 * N-of-M search on the sentences from the WeScience project
	 */
	public void testWeScienceTfIdf() {
		// Collection of 1-line documents
		String filename = "data/wescience.txt";

		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new SimpleSearchEngine(filename, logger);
		String query = "java do the linux";

		System.out.println("Looking up '" + query + "' with TF-IDF ranker:");
		long before = System.nanoTime();
		IResultSet results = engine.search(query);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		assertEquals(10, results.size());
		Iterator<IResult> resultIterator = results.iterator();
		
		// checking the first 3 results
		IResult firstResult = resultIterator.next();
		IResult secondResult = resultIterator.next();
		IResult thirdResult = resultIterator.next();
		assertTrue(firstResult.getDocument().getOriginalData().startsWith("[10490810]"));
		assertTrue(secondResult.getDocument().getOriginalData().startsWith("[10491770]"));
		assertTrue(thirdResult.getDocument().getOriginalData().startsWith("[10441280]"));

		// uncomment to see all results
		for (IResult result : results) {
			System.out.println(result.getRelevance());
			System.out.println(result.getDocument().getOriginalData());
			System.out.println(result.getDocument().getExtraData());
		}
	}

	/**
	 * Test document similarity on the CRAN document collection
	 */
	public void testCranSimilarity() {
		// CRAN document collection
		String filename = "data/cran.xml";

		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new SimpleSearchEngine(filename, logger);
		int docID = 225;

		System.out.println("Looking up document similar to " + docID);
		long before = System.nanoTime();
		IResultSet results = engine.findSimilar(docID);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		assertEquals(10, results.size());
		Iterator<IResult> resultIterator = results.iterator();

		// checking the first 3 results
		IResult firstResult = resultIterator.next();
		IResult secondResult = resultIterator.next();
		IResult thirdResult = resultIterator.next();
		assertTrue(firstResult.getDocument().getOriginalData().startsWith("\nthe flow past a slender delta"));
		assertTrue(secondResult.getDocument().getOriginalData().startsWith("\nan investigation has been conducted"));
		assertTrue(thirdResult.getDocument().getOriginalData().startsWith("\npressure-distribution and force"));
		
		// uncomment to see all results
		for (IResult result : results) {
			System.out.println(result.getRelevance());
			System.out.println(result.getDocument().getOriginalData());
			System.out.println(result.getDocument().getExtraData());
		}
	}

	/**
	 * Test document similarity on the sentences from the WeScience project
	 */
	public void testWeScienceSimilarity() {
		// Collection of 1-line documents
		String filename = "data/wescience.txt";

		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new SimpleSearchEngine(filename, logger);
		int docID = 9981;

		System.out.println("Looking up document similar to " + docID);
		long before = System.nanoTime();
		IResultSet results = engine.findSimilar(docID);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		assertEquals(10, results.size());
		Iterator<IResult> resultIterator = results.iterator();
		
		// checking the first 3 results
		IResult firstResult = resultIterator.next();
		IResult secondResult = resultIterator.next();
		IResult thirdResult = resultIterator.next();
		assertTrue(firstResult.getDocument().getOriginalData().startsWith("[10051350]"));
		assertTrue(secondResult.getDocument().getOriginalData().startsWith("[10051370]"));
		assertTrue(thirdResult.getDocument().getOriginalData().startsWith("[10051420]"));

		// uncomment to see all results
		for (IResult result : results) {
			System.out.println(result.getRelevance());
			System.out.println(result.getDocument().getOriginalData());
			System.out.println(result.getDocument().getExtraData());
		}
	}
	
	public static void main(String[] args) {
		ObligDTest test = new ObligDTest();
		test.createLogger();
		test.testCranTfIdf();
		test.testWeScienceTfIdf();
		test.testCranSimilarity();
		test.testWeScienceSimilarity();
	}
}