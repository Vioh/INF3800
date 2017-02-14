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
	 * Simple class for a suffix object. This datastructure can either store:
	 * <ul>
	 * <li>A simple text string, which represents a NORMALIZED query text, or</li>
	 * <li>A token, which is represented by a pair (entry, offset), where:
	 *   <ol>
	 *   <li> entry is the docID that tells us which document the token belongs to.
	 *   <li> offset is the positional index of the token in the NORMALIZED document.
	 * </ol></li></ul>
	 */
	private static class Suffix {
		private String value = null; // used only for the query text
		private int entry = -1;  // used only for the token in the dictionary
		private int offset = -1; // used only for the token in the dictionary
		
		/** Constructor to tranform a normalized query text into a suffix */
		public Suffix(String queryNorm) {
			this.value = queryNorm;
		}
		
		/** Constructor to create a suffix that is based on a whole token */
		public Suffix(int docID, int positionInNormalizedDoc) {
			this.entry = docID;
			this.offset = positionInNormalizedDoc;
		}
		
		/** 
		 * Returns the text value of the suffix (i.e. the normalized query).
		 * @return the suffix value, or <code>null</code> if the suffix is not a query.
		 */
		public String getValue() {
			return this.value;
		}
		
		/** 
		 * Returns the document ID (docID) that the suffix belongs to.
		 * @return the docID of the suffix, or -1 if the suffix is a query. 
		 */
		public int getEntry() {
			return this.entry;
		}
		
		/** 
		 * Returns the start position (offset) of the suffix in the normalized document.
		 * @return the offset of the suffix, or -1 if the suffix is a query. 
		 */
		public int getOffset() {
			return this.offset;
		}
	}
	
	/** A comparator class that compares 2 normalized suffices lexicographically. */
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
				s1 = doc.getNormalizedData().substring(suf1.getOffset());
			}
			if(s2 == null) {
				IDocument doc = dictionary.getDocument(suf2.getEntry());
				s2 = doc.getNormalizedData().substring(suf2.getOffset());
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
			String key = dictionary.getDocument(i).getNormalizedData();
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
	 * Returns the document ID (docID) that the given suffix belongs to.
	 * @param index on the suffix array
	 * @return the docID of the suffix
	 */
	public int getEntry(int index) {
		return this.suffixArray[index].getEntry();		
	}

	/**
	 * Returns the offset position of the given suffix in the normalized document.
	 * @param index on the suffix array
	 * @return the offset of the suffix
	 */
	public int getOffset(int index) {
		return this.suffixArray[index].getOffset();
	}

	/**
	 * Looks up the given NORMALIZED query by performing a binary search. 
	 * This binary search allows prefix matching as well as exact matching.
	 * <p/>
	 * The caller must ensure that the probe query has the expected case.
	 * @param queryNorm the query that we want to look up in its normalized form
	 * @return the suffix index of the query, if found, or the insertion point
	 */
	public int lookup(String queryNorm) {		
		// First, create a "suffix" for the normalized query
		Suffix key = new Suffix(queryNorm);
		
		// Then use binary search to find the query in the suffix array
		return Arrays.binarySearch(this.suffixArray, key, this.suffixComparator);		
	}
}