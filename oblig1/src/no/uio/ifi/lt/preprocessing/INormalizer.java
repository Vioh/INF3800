package no.uio.ifi.lt.preprocessing;

/**
 * Defines a normalizer, i.e., a mechanism that produces a
 * "normalized" string from a given possibly "unnormalized"
 * string, for some suitable definition of "normalized".
 * 
 * @author aleks
 */
public interface INormalizer {
	
	/**
	 * Produces a "normalized" version of the input string,
	 * for some suitable definition of "normalized".
	 * 
	 * @param value the possibly unnormalized string to normalize
	 * @return the normalized version of the input string
	 */
	String normalize(String value);

}
