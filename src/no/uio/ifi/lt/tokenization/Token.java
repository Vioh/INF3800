package no.uio.ifi.lt.tokenization;


/**
 * A very simple and straightforward implementation of the
 * {@link IToken} interface.
 * 
 * @author aleks
 */
public class Token implements IToken {
	
	/**
	 * Backs the {@link #getValue()} method.
	 */
	private String value;
	
	/**
	 * Backs the {@link #getPosition()} method.
	 */
	private int position;

	/**
	 * Backs the {@link #getStartIndex()} method.
	 */
	private int startIndex;
	
	/**
	 * Constructor.
	 * 
	 * @param value the "word" itself
	 * @param position the position of the "word" in the buffer from which it was produced
	 * @param startIndex the index in the buffer where the "word" starts
	 */
	public Token(String value, int position, int startIndex) {
		this.value = value;
		this.position = position;
		this.startIndex = startIndex;
	}

	/**
	 * Implements the {@link IToken} interface.
	 */
	
	public int getPosition() {
		return position;
	}

	/**
	 * Implements the {@link IToken} interface.
	 */
	
	public String getValue() {
		return value;
	}

	/**
	 * Implements the {@link IToken} interface.
	 */
	
	public int getStartIndex() {
		return startIndex;
	}
	
}
