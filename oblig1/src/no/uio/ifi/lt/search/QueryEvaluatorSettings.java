package no.uio.ifi.lt.search;

/**
 * Encapsulates the evaluation parameters for a {@link QueryEvaluator}
 * object.
 * 
 * @author aleks
 */
public class QueryEvaluatorSettings {
	
	/**
	 * Log verbose debug messages?
	 */
	public boolean debug = false;

    /**
     * Defines how many candidate matches to compute.
     */
    public int candidates = 2;

    /**
     * The minimum percentage of unique query terms that have to be
     * present in a document for it to be processed and ranked, i.e.,
     * the N in N-of-M matching. A value of 0.0 is a pure "OR" evaluation,
     * whereas a value of 1.0 is a pure "AND" evaluation.
     */
    public double recallThreshold = 0.5;
    
    /**
     * Ignore candidates with a relevance score below this value.
     */
    public double rankThreshold = 0.0;
	
}
