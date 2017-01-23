package no.uio.ifi.lt.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


/**
 * A very simple and straightforward implementation of the
 * {@link IResultSet} interface.
 *
 * @author aleks
 */
public class ResultSet implements IResultSet {
		
	/**
	 * The query that generated this result set.
	 */
	private IQuery query;
	
	/**
	 * The individual results in the result set.
	 */
	private List<IResult> results;
	
	/**
	 * Constructor.
     *
	 * @param query the query that generated this result set
	 * @param capacity the estimated size of the result set
	 */
	public ResultSet(IQuery query, int capacity) {
		this.query = query;
		this.results = new ArrayList<IResult>(capacity);
	}

	/**
	 * Implements the {@link IResultSet} interface.
	 */
	
	public IQuery getQuery() {
		return this.query;
	}

	/**
	 * Implements the {@link IResultSet} interface.
	 */
	
	public Iterator<IResult> iterator() {
		return this.results.iterator();
	}

	/**
	 * Appends a result to the result set.
	 * 
	 * @param result the result to append to the result set
	 */
	public void appendResult(IResult result) {
		this.results.add(result);
	}

	/**
	 * Sorts the results by their relevance scores.
	 */
	public void sortByRelevance() {
	
		// Define a small helper.
		class ByRelevance implements Comparator<IResult> {
			public int compare(IResult x, IResult y) {
				return (int) Math.signum(y.getRelevance() - x.getRelevance());
			}
		}	

		// Do it!
		Collections.sort(this.results, new ByRelevance());
		
	}

}
