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
import no.uio.ifi.lt.tokenization.BrainDeadTokenizer;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test program for assignment B
 */
public class ObligBTest extends TestCase {
	Logger logger;

	public ObligBTest() {
		createLogger();
	}

	// @Before
	public void createLogger() {
		// Create a logger.
		logger = Logger.getLogger(ObligBTest.class.getName());
		Handler[] handlers = logger.getHandlers();
		for (int i = 0; i < handlers.length; ++i) {
			logger.removeHandler(handlers[i]);
		}
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);

		// logger.addHandler(consoleHandler);
		logger.setLevel(Level.ALL);		
	}

	/**
	 * Phrase search on the CRAN document collection
	 */
	public void testCran() {
		// Collection of 1-line documents
		String filename = "data/cran.xml";
		
		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new PhraseSearchEngine(filename, logger, new BrainDeadTokenizer());
		
		// Lookup
		String query = "low speed";		
		System.out.println("Looking up '" + query + "' with phrase matching:");
		long before = System.nanoTime();
		IResultSet results = engine.evaluate(query);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		
		List<String> resultContent = new LinkedList<String>();
		Iterator<IResult> resultIt = results.iterator();
		while (resultIt.hasNext()) {		
			resultContent.add(resultIt.next().getDocument().getOriginalData());
		}
		System.out.println("results: " + resultContent);
		
		// we should have 12 matches here
		Assert.assertEquals(12, resultContent.size());
		// example of match
		Assert.assertTrue(resultContent.contains("\nreasons for enquiry--to provide a standard instrument for\n" + 
				"the calibration of low speed anemometers .\n"));
	}
	
	/**
	 * Phrase search on the sentences from the WeScience project
	 */
	public void testWeScience() {
		// Collection of 1-line documents
		String filename = "data/wescience.txt";
		
		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new PhraseSearchEngine(filename, logger, new BrainDeadTokenizer());
		
		// Lookup
		String query = "is a very";		
		System.out.println("Looking up '" + query + "' with phrase matching:");
		long before = System.nanoTime();
		IResultSet results = engine.evaluate(query);
		long after = System.nanoTime();
		System.out.println("Lookup took " + ((after - before) / 1000000.0) + " ms.");
		
		Set<String> resultContent = new HashSet<String>();
		Iterator<IResult> resultIt = results.iterator();
		while (resultIt.hasNext()) {		
			resultContent.add(resultIt.next().getDocument().getOriginalData());
		}
		System.out.println("results: " + resultContent);
		
		// we should have 7 matches here
		Assert.assertEquals(7, resultContent.size());
		
		// 2 examples of matches
		Assert.assertTrue(resultContent.contains("[10590070] |In theory, natural-language processing is a very " +
				"attractive method of [[human-computer interaction]]."));
		Assert.assertTrue(resultContent.contains("[10712410] |[[R (programming_language)|R]] is a very powerful tool " +
				"that can be used to perform almost any kind of statistical analysis, and is freely downloadable."));
	}

	 public static void main(String[] args) {
		 ObligBTest test = new ObligBTest();
		 test.createLogger();
		 test.testCran();
		 test.testWeScience();
	 }
}