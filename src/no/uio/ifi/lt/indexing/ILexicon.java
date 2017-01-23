package no.uio.ifi.lt.indexing;

/**
 * Defines a lexicon or vocabulary, i.e., a class that maps from
 * token values to an integer lexicon identifier. This is a 1-1
 * mapping, i.e., if there are N unique token values then there are
 * also N unique lexicon identifiers. Furthermore, these identifiers
 * are assumed to be in the range {0, ..., N - 1}.
 * 
 * @author aleks
 */
public interface ILexicon extends Iterable<String> {	

	/**
	 * Constant used to signify an invalid integer index, e.g., when a given
	 * token value has not been previously seen and thus does not map to an
	 * integer.
	 */
	static final int INVALID = -1;	
	
	/**
	 * Returns the size of the lexicon, i.e., the number of unique token values
	 * contained in the lexicon.
	 * 
	 * @return the number of unique token values contained in the lexicon
	 */
	int size();
	
	/**
	 * Looks up a given token value in the lexicon and returns the integer lexicon
	 * identifier associated with the token value. If the token value does not map
	 * to a lexicon identifier, then either {@link #INVALID} is returned
	 * 
	 * @param value the token value to look up
	 * @return the integer lexicon identifier that the token value maps to, or {@link #INVALID}
	 */
	int lookup(String value);
	
	
	/**
	 * Adds a value to the lexicon, and returns the integer lexicon identifier associated
	 * with the value.  If the value already exists in the lexicon, the lexicon is left
	 * unchanged and the identifier for the existing value is returned.
	 * 
	 * @param value the value to add to the lexicon
	 * @return the lexicon identifier associated with the value
	 */
	int addValue(String value);
	

}
