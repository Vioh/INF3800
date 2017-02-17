package no.uio.ifi.lt.utils;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;

/**
 * Simple class for representing a suffix array over
 * the keys in a {@link IDocumentStore} object.
 */
public class SuffixArray {
	/** The dictionary whose keys this suffix array is for. */
	private final IDocumentStore dictionary;

	/** The comparator to compare 2 suffices lexicographically */
	private final SuffixComparator suffixComparator;	
	
	/** The suffix array with suffix objects that represent tokens from the docs */
	private final Suffix[] suffixArray;
	
	/** 
	 * Simple class for a suffix object (i.e. a data unit for the suffix array).
	 * This datastructure can either store:
	 * -- A simple text string representing the suffix itself, or
	 * -- A token, represented by an entry (docID) and an offset (position). 
	 */
	private static class Suffix {
		private String value = null; // used only for the query text
		private int entry  = -1; // used only for the token in the dictionary
		private int offset = -1; // used only for the token in the dictionary
		
		public Suffix(String value) {
			this.value = value;
		}		
		public Suffix(int documentID, int positionIndex) {
			this.entry  = documentID;
			this.offset = positionIndex;
		}		
		public String getValue() {
			return value;
		}
		public int getEntry() {
			return entry;
		}
		public int getOffset() {
			return offset;
		}
	}
	
	/** Comparator for comparing 2 suffices lexicographically */
	private static class SuffixComparator implements Comparator<Suffix> {		
		private final IDocumentStore dictionary;
		
		public SuffixComparator(IDocumentStore dictionary) {
			this.dictionary = dictionary;
		}
		
		public int compare(Suffix suf1, Suffix suf2) {
			String s1 = suf1.getValue();
			String s2 = suf2.getValue();
			if(s1 == null) {
				IDocument doc = dictionary.getDocument(suf1.getEntry());
				s1 = doc.getOriginalData().substring(suf1.getOffset());
			}
			if(s2 == null) {
				IDocument doc = dictionary.getDocument(suf2.getEntry());
				s2 = doc.getOriginalData().substring(suf2.getOffset());
			}
			return s1.compareTo(s2);
		}
	}
	
	/**
	 * Constructor.
	 * @param dictionary the dictionary whose keys this suffix array is for
	 * @param tokenizer  the tokenizer that will determine where the suffixes start
	 */
	public SuffixArray(IDocumentStore dictionary, ITokenizer tokenizer) {
		this.dictionary = dictionary;
		this.suffixArray = buildSuffixArray(this.dictionary, tokenizer);
		this.suffixComparator = new SuffixComparator(dictionary);
	}

	/**
	 * Builds a suffix array up from the keys in the given dictionary.
	 * @param dictionary the dictionary whose keys we want to generate a suffix array for
	 * @param tokenizer  the tokenizer that will determine where the suffixes start
	 * @return the generated suffix array
	 */
	private static Suffix[] buildSuffixArray(IDocumentStore dictionary, ITokenizer tokenizer) {
		// Generate the unsorted suffix list with an ArrayList
		ArrayList<Suffix> suffixList = new ArrayList<Suffix>();
		for(int i = 0; i < dictionary.size(); ++i) {
			String key = dictionary.getDocument(i).getOriginalData();
			Iterator<IToken> iterator = tokenizer.iterator(key);
			while (iterator.hasNext()) {
				IToken token = iterator.next();
				suffixList.add(new Suffix(i, token.getStartIndex()));
			}
		}
		// Convert the unsorted ArrayList to an unsorted suffix array
		Suffix[] suffixArray = new Suffix[suffixList.size()];
		suffixArray = suffixList.toArray(suffixArray);
		suffixList = null;
		
		// Sort the suffix array and return it
		Arrays.sort(suffixArray, new SuffixComparator(dictionary));
		return suffixArray;
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
		return this.suffixArray[index].getEntry();		
	}

	/**
	 * Returns the offset into the dictionary entry that the given suffix index is for.
	 * @param index a suffix index
	 * @return an offset into a dictionary entry
	 */
	public int getOffset(int index) {
		return this.suffixArray[index].getOffset();
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
		// First create a "suffix" for the query (the key)
		Suffix query = new Suffix(key);
		
		// Then use binary search to find the query in the suffix array
		return Arrays.binarySearch(this.suffixArray, query, this.suffixComparator);		
	}
}