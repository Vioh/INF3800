package no.uio.ifi.lt.storage;
import java.util.Iterator;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.tokenization.IToken;

public class DocumentVector implements IDocumentVector {
	/*
	 * YOU MAY IMPLEMENT THIS CLASS AS YOU WANT, THIS INCLUDES THE INTERFACE AS WELL!
	 * THE INTERFACE REFLECTS THE METHODS USED IN THE SOLUTION.
	 */
	
	public DocumentVector(IToken[] documentTerms, ILexicon lexicon, IInvertedIndex invertedIndex) {
		//TODO: IMPLEMENT THIS CLASS!
		throw new RuntimeException("ASSIGNMENT D, (B): Complete the constructor!");	
	}
	
	@Override
	public double get(int lexiconId) {
		throw new RuntimeException("ASSIGNMENT D, (B): Complete this method!");
	}

	@Override
	public double getCosineSimilarity(IDocumentVector docVector) {
		throw new RuntimeException("ASSIGNMENT D, (B): Complete this method!");
	}

	@Override
	public Iterator<Double> getTfIdfScoreIterator() {
		throw new RuntimeException("ASSIGNMENT D, (B): Complete this method!");
	}

	@Override
	public int size() {
		throw new RuntimeException("ASSIGNMENT D, (B): Complete this method!");
	}
}