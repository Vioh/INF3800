package no.uio.ifi.lt.search;


/**
 * Defines a result set, i.e., the output from a query evaluation task.
 *
 * @author aleks
 * @see IResult
 * @see IQueryEvaluator
 */
public interface IResultSet extends Iterable<IResult> {
	
	/**
	 * Returns the query that generated the result set.
	 * 
	 * @return the query that generated the result set
	 */
	IQuery getQuery();
			
}
