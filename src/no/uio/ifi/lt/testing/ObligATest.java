
package no.uio.ifi.lt.testing;
 

import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import junit.framework.TestCase;


import no.uio.ifi.lt.search.ISearchEngine;
import no.uio.ifi.lt.search.SimpleSearchEngine;


/**
 * Testing class for the assignment A of INF3800/INF4800.
 *
 * @author  plison
 */
public class ObligATest extends TestCase {

	Logger logger;	
	
	public ObligATest() {
		createLogger();
	}
 
	
//	 @Before
	public void createLogger() {
		// Create a logger.
		logger = Logger.getLogger(ObligATest.class.getName());
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
	 * Simple test on the MESH data (one document per line)
	 */
//	@Test
	public void testMesh() {
		
        // Where are our documents?
        String filename = "data/mesh.txt";
		
		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new SimpleSearchEngine(filename, logger);
		
		// silly sanity check:
		String query = "as acid group joint lead";
		Map<String,Integer> frequencies = engine.evaluateBrainDead(query);

		Assert.assertEquals(74, frequencies.get("as").intValue());
		Assert.assertEquals(273, frequencies.get("acid").intValue());
		Assert.assertEquals(84, frequencies.get("group").intValue());
		Assert.assertEquals(30, frequencies.get("joint").intValue());
		Assert.assertEquals(7, frequencies.get("lead").intValue());	
	}
	
	
	
	/**
	 * Simple test on the CRAN data (XML-structured)
	 */
//	 @Test
	public void testCran() {
		
        // Where are our documents?
        String filename = "data/cran.xml";
		
		// Create a simple search engine and do a simple lookup.
		ISearchEngine engine = new SimpleSearchEngine(filename, logger);
		
		// silly sanity check:
		String query = "rarefied hypersonic analysis";
		Map<String,Integer> frequencies = engine.evaluateBrainDead(query);

		Assert.assertEquals(6, frequencies.get("rarefied").intValue());
		Assert.assertEquals(145, frequencies.get("hypersonic").intValue());
		Assert.assertEquals(249, frequencies.get("analysis").intValue());
	}
	
	
	
/**	public static void main(String[] args) {
		ObligATest test = new ObligATest();
		test.createLogger();
		test.testCran();
	} */
	
}
