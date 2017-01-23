package no.uio.ifi.lt.tokenization;

import java.util.Iterator;


/**
 * Defines a tokenizer, i.e., a mechanism that produces an
 * iterable stream of {@link IToken} objects from a given
 * text buffer.
 * 
 * @author aleks
 * @see IToken
 */
public interface ITokenizer {
	
	/**
	 * Returns an iterator that iterates over the tokens
	 * produced from the given text buffer.
	 * 
	 * @param text the buffer to tokenize
	 * @return an iterator that iterates over the buffer's tokens
	 */
	Iterator<IToken> iterator(String text);

	/**
	 * Returns an array of tokens produced from the given text
	 * buffer.
	 * 
	 * @param text the buffer to tokenize
	 * @return an array of {@link IToken} objects
	 */
	IToken[] toArray(String text);
	
}
