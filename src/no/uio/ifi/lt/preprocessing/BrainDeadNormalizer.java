package no.uio.ifi.lt.preprocessing;



/**
 * An extremely simple implementation of {@link INormalizer} for
 * testing purposes. Trims the input buffer and bumps it to lowercase.
 * 
 * @author aleks
 */
public class BrainDeadNormalizer implements INormalizer {

	/**
	 * Implements the {@link INormalizer} interface.
	 */
	public String normalize(String value) {
		
		// Only consider ordinary English characters.
		value = value.replaceAll("[^a-zA-Z0-9]", " ");
		
		// Squeeze blanks.
		value = value.replaceAll("\\s+", " ").trim();
		
		// Bump case.
		return value.toLowerCase();

	}

}
