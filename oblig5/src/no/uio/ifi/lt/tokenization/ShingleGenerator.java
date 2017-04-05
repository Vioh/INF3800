package no.uio.ifi.lt.tokenization;
import java.util.Iterator;
import no.uio.ifi.lt.utils.ArrayIterator;

/**
 * A simple {@link ITokenizer} implementation that generates overlapping
 * shingles. Useful for approximate matching purposes.
 */
public class ShingleGenerator implements ITokenizer {
	/** The shingle size, i.e., the width of our sliding window over the text buffer */
	private int width;

	/**
	 * Constructor.
	 * @param width the shingle size
	 */
	public ShingleGenerator(int width) {
		this.width = width;
	}

	/**
	 * Implements the {@link ITokenizer} interface.
	 */
	@Override
	public Iterator<IToken> iterator(String text) {
		return new ArrayIterator<IToken>(this.toArray(text));
	}
	
	/**
	 * Implements the {@link ITokenizer} interface.
	 */
	@Override
	public IToken[] toArray(String text) {
		/*
		 * Return a k-gram array of "shingles" from the text. the 'k' is defined by 'this.width'.
		 * A k-gram is simply a window over n characters in a string. For example, the set of 3-grams 
		 * from the string backgammon would be the set {bac, ack, ckg, kga, gam, amm, mmo, mon}. 
		 */
		throw new RuntimeException("COMPLETE THIS METHOD!");
	}
}