package no.uio.ifi.lt.indexing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple in-memory implementation of {@link ILexicon}, suitable for small to
 * medium-sized vocabularies.
 * 
 * @author aleks
 */
public class InMemoryLexicon implements ILexicon {
	
	/**
	 * Maps token values to their associated integer indices.
	 */
	private Map<String, Integer> vocabulary = new HashMap<String, Integer>();

	/**
	 * Implements the {@link ILexicon} interface.
	 * 
	 * @see no.uio.ifi.lt.indexing.ILexicon#lookup(java.lang.String)
	 */
	
	public int lookup(String value) {
		
		// Known token value?
        if (this.vocabulary.containsKey(value)) {
        	return this.vocabulary.get(value);
        }

       return INVALID;
	}
	
	
	/**
	 * Implements the {@link Ilexicon} interface
	 *
	 * @see no.uio.ifi.lt.indexing.ILexicon#addValue(java.lang.String)
	 */
	public int addValue(String value) {
		
		if (!vocabulary.containsKey(value)) {
			int index = this.size();
			this.vocabulary.put(value, index);
			return index;
		}
		else {
			return vocabulary.get(value);
		}
	}

	/**
	 * Implements the {@link ILexicon} interface.
	 * 
	 * @see no.uio.ifi.lt.indexing.ILexicon#size()
	 */
	
	public int size() {
		return this.vocabulary.size();
	}

	/**
	 * Implements the {@link Iterable<String>} interface.
	 */
	
	public Iterator<String> iterator() {
		return this.vocabulary.keySet().iterator();
	}

}
