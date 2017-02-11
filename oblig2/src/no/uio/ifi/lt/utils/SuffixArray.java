package no.uio.ifi.lt.utils;
import java.util.Iterator;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;

/**
 * Simple class for representing a suffix array over
 * the keys in a {@link IDocumentStore} object.
 */
public class SuffixArray {
	/**
	 * The dictionary whose keys this suffix array is for.
	 */
	private final IDocumentStore dictionary;

	/*
	 * TODO:
	 * You should decide the data structure of the tokens in the document yourself, and use a class accordingly. 
	 * This data structure should for all token in the document:
	 * 1. Store the document id
	 * 2. Store the index where the token starts
	 */
	private final Object[] suffixArray;

	/**
	 * Constructor.
	 * @param dictionary the dictionary whose keys this suffix array is for
	 * @param tokenizer  the tokenizer that will determine where the suffixes start
	 */
	public SuffixArray(IDocumentStore dictionary, ITokenizer tokenizer) {
		this.dictionary = dictionary;
		this.suffixArray = buildSuffixArray(this.dictionary, tokenizer);
	}

	/**
	 * Builds a suffix array up from the keys in the given dictionary.
	 * @param dictionary the dictionary whose keys we want to generate a suffix array for
	 * @param tokenizer  the tokenizer that will determine where the suffixes start
	 * @return the generated suffix array
	 */
	private static Object[] buildSuffixArray(IDocumentStore dictionary, ITokenizer tokenizer) {
		// TODO: Choose a data structure, do NOT use Object :) 
		// you need to store document id and position index, 
		// and write a comparator accordingly
		
		// First, count how many expanded items there are. That's easy:
		int expandedCount = 0;

		for (int i = 0; i < dictionary.size(); ++i) {
			String key = dictionary.getDocument(i).getOriginalData();
			Iterator<IToken> iterator = tokenizer.iterator(key);
			while (iterator.hasNext()) {
				IToken token = iterator.next();
				++expandedCount;
			}
		}

		// Allocate memory.
		Object[] suffixArray = new Object[expandedCount];

		// Set the data.
		// TODO: Return a sorted suffix array!
		// HINT: Make a custom comparator MyComparator, and sort the suffix list with:         
		// Arrays.sort

		throw new RuntimeException("Your task is to complete this method");
		//return suffixArray;
	}

	/**
	 * Returns the number of suffixes.
	 * @return the number of suffixes
	 */
	public int size() {
		return this.suffixArray.length;
	}

	/**
	 * Returns the index of the dictionary entry that the given suffix index is for.
	 * @param index a suffix index
	 * @return a dictionary entry index
	 */
	public int getEntry(int index) {
		//TODO: IMPLEMENT THIS!
		throw new RuntimeException("Complete this method!");
	}

	/**
	 * Returns the offset into the dictionary entry that the given suffix index is for.
	 * @param index a suffix index
	 * @return an offset into a dictionary entry
	 */
	public int getOffset(int index) {
		//TODO: IMPLEMENT THIS!
		throw new RuntimeException("Complete this method!");
	}

	/**
	 * Looks up the given key by performing a binary search. A binary search
	 * allows prefix matching as well as exact matching.
	 * <p/>
	 * The caller must ensure that the probe key has the expected case.
	 * @param key the key we want to look up in the suffix array
	 * @return the suffix index of the key, if found, or the insertion point
	 */
	public int lookup(String key) {
		// TODO: HINT: Make sure the comparator is working properly, 
		// and let Arrays.binarySearch do the whole job!
		throw new RuntimeException("Fix the binary search call according to your data structure!");
	}
}