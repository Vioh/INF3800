package no.uio.ifi.lt.tokenization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.uio.ifi.lt.utils.ArrayIterator;


/**
 * An extremely simple implementation of {@link ITokenizer} for
 * testing purposes. Splits the buffer on whitespace.
 * 
 * @author aleks
 */
public class BrainDeadTokenizer implements ITokenizer {
	
	private static final Pattern SPLITTER = Pattern.compile("\\b[a-zA-Z0-9æøåÆØÅ]+\\b");

	/**
	 * Implements the {@link ITokenizer} interface.
	 */
	
	public Iterator<IToken> iterator(String text) {
		return new ArrayIterator<IToken>(this.toArray(text));
	}

	/**
	 * Implements the {@link ITokenizer} interface.
	 * 
	 * A real tokenizer wouldn't be implemented this way. Kids,
	 * don't do this at home.
	 */
	
	public IToken[] toArray(String text) {		
		Matcher matcher = SPLITTER.matcher(text);
		List<IToken> tokens = new ArrayList<IToken>();
		int counter = 0;
		while (matcher.find()) {
			tokens.add(new Token(matcher.group(), counter++, matcher.start()));
		}
		return tokens.toArray(new IToken[counter]);
	}
	
}
