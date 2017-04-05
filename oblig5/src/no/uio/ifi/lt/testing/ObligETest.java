package no.uio.ifi.lt.testing;
import java.util.Iterator;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uio.ifi.lt.nbclassifier.DocumentClassifier;
import no.uio.ifi.lt.nbclassifier.MessageStore;
import no.uio.ifi.lt.nbclassifier.MultinomialNaiveBayes;
import no.uio.ifi.lt.nbclassifier.TestStore;
import no.uio.ifi.lt.search.FuzzySearchEngine;
import no.uio.ifi.lt.search.IResult;
import no.uio.ifi.lt.search.IResultSet;
import no.uio.ifi.lt.search.ISearchEngine;
import junit.framework.TestCase;

/**
 * Test program for assignment E
 */
public class ObligETest extends TestCase {
	Logger logger;	

	public ObligETest() {
		createLogger();
	}

	public void createLogger() {
		// Create a logger.
		logger = Logger.getLogger(ObligETest.class.getName());
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
	 * Fuzzy search on the CRAN document collection
	 */
	public void testCranFuzzy() {
		// CRAN document collection
		String filename = "data/cran.xml";

		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new FuzzySearchEngine(filename, logger);			
		String query = "is well-know";

		System.out.println("Looking up '" + query + "' with fuzzy search:");
		long before = System.nanoTime();
		IResultSet results = engine.search(query);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		assertEquals(10, results.size());
		Iterator<IResult> resultIterator = results.iterator();
		 
		// checking the first 2 result
		IResult firstResult = resultIterator.next();
		IResult secondResult = resultIterator.next();
		assertTrue(firstResult.getDocument().getOriginalData().startsWith("it is well-known that"));
		assertTrue(secondResult.getDocument().getOriginalData().startsWith("the problems of heat transfer"));

		// uncomment to see all results
		for (IResult result : results) {
			System.out.println(result.getRelevance());
			System.out.println(result.getDocument().getOriginalData());
			System.out.println(result.getDocument().getExtraData());
		}	 
	}

	/**
	 * Fuzzy search on the sentences from the WeScience project
	 */
	public void testWeScienceFuzzy() {
		// Collection of 1-line documents
		String filename = "data/wescience.txt";

		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new FuzzySearchEngine(filename, logger);			
		String query = "hat-driven face struct grammer";
		
		System.out.println("Looking up '" + query + "' with fuzzy search:");
		long before = System.nanoTime();
		IResultSet results = engine.search(query);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		assertEquals(10, results.size());
		Iterator<IResult> resultIterator = results.iterator();
		
		// checking the first result
		IResult firstResult = resultIterator.next();
		assertTrue(firstResult.getDocument().getOriginalData().startsWith("[10340610]") || 
		           firstResult.getDocument().getOriginalData().startsWith("10840450"));

		// uncomment to see all results
		for (IResult result : results) {
			System.out.println(result.getRelevance());
			System.out.println(result.getDocument().getOriginalData());
			System.out.println(result.getDocument().getExtraData());
		}
	}
	
	public void testClassification() {
		// Where are our documents?
		String trainFolder = "data/train";
		String testFolder = "data/test";
		
		long before, after;
		before = System.nanoTime();		
		MessageStore ms = new MessageStore(trainFolder, logger);        
		TestStore ts = new TestStore(testFolder, logger);
		
		before = System.nanoTime();
		DocumentClassifier naiveBayes = new MultinomialNaiveBayes(ms);
		after = System.nanoTime();
		System.out.println("Training done in " + ((after - before) / 1000000.0) + " ms.");
			 
		before = System.nanoTime();
		assertTrue(ts.evaluateTestSet(naiveBayes) > 0.81);
		after = System.nanoTime();
		System.out.println("Classifying done in " + ((after - before) / 1000000.0) + " ms.");
	}
	
	public static void main(String[] args) {
		ObligETest test = new ObligETest();
		test.createLogger();
		test.testCranFuzzy();
		test.testWeScienceFuzzy();
		test.testClassification();
	}
}