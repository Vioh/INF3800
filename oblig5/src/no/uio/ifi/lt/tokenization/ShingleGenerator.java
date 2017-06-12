package no.uio.ifi.lt.tokenization;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.uio.ifi.lt.utils.ArrayIterator;

/**
 * A simple {@link ITokenizer} implementation that generates overlapping
 * shingles. Useful for approximate matching purposes.
 */
public class ShingleGenerator implements ITokenizer {
	/** The shingle size, i.e., the width of our sliding window over the text buffer */
	private int width;
	
	/** Regex for splitting hte text */
	private static final Pattern SPLITTER = Pattern.compile("\\b[a-zA-Z0-9æøåÆØÅ]+\\b");

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
		 * The strategy is simply to first remove all non-letter symbols
		 * from the text, and then squeeze all the blanks. After that,
		 * we can just slide our k-wide window over the texxt to generate
		 * the series of k-gram tokens. Note that the sliding will be
		 * done character-wise, not word-wise.
		 */
		
//		Matcher matcher = SPLITTER.matcher(text);
//		List<IToken> tokens = new ArrayList<IToken>();
//		
//		int counter = 0;
//		while(matcher.find()) {
//			String tok = matcher.group();
//			int pos = matcher.start();
//			for(int i = 0, j = i + width; j <= tok.length(); ++i) {
//				tokens.add(new Token(tok.substring(i, j++), counter++, pos++));
//			}
//		}
//		return tokens.toArray(new IToken[counter]);
		
		
		List<IToken> tokens = new ArrayList<IToken>();
		int stop = text.length() - width + 1;
		for(int i = 0, j = width; i < stop; ++i) {
			tokens.add(new Token(text.substring(i, j++), i, i));
		}
		return tokens.toArray(new IToken[stop]);
	}
}