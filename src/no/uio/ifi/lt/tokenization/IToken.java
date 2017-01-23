package no.uio.ifi.lt.tokenization;


/**
 * Defines a token, i.e., a "word" in a text buffer
 * for some suitable definition of "word".
 * 
 * @author aleks
 * @see ITokenizer
 */
public interface IToken {
	
	/**
	 * Returns the "word" itself.
	 */
	String getValue();
	
	/**
	 * Returns the position of the "word" in the text buffer
	 * from which the token was produced. For example, in the buffer
	 * "hello world", "hello" has position 0 and "world" has position 1.
	 */
	int getPosition();
	
	/**
	 * Returns the index of the start of the "word" in the text
	 * buffer from which the token was produced. For example, in the
	 * buffer "hello world", "world" starts at index 6.
	 */
	int getStartIndex();
	
}
