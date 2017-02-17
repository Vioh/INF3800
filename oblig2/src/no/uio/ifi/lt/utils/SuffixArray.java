package no.uio.ifi.lt.utils;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;

/**
 * Simple class for representing a suffix array over
 * the keys in a {@link IDocumentStore} object.
 */
public class SuffixArray {
	/** The comparator to compare 2 suffices lexicographically */
	private final SuffixComparator suffixComparator;	
	
	/** The suffix array with suffix objects that represent tokens from the docs */
	private final Suffix[] suffixArray;
	
	/** An array of strings containing normalized data from all documents */
	private final String[] normalizedDocs;
	
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
	
	/** Comparator for comparing 2 normalized  suffices lexicographically */
	private static class SuffixComparator implements Comparator<Suffix> {		
		private final String[] normalizedDocs;
		
		public SuffixComparator(String[] normalizedDocs) {
			this.normalizedDocs = normalizedDocs;
		}
		
		public int compare(Suffix suf1, Suffix suf2) {
			String s1 = suf1.getValue();
			String s2 = suf2.getValue();
			if(s1 == null) {
				s1 = normalizedDocs[suf1.getEntry()];
				s1 = s1.substring(suf1.getOffset());
			}
			if(s2 == null) {
				s2 = normalizedDocs[suf2.getEntry()];
				s2 = s2.substring(suf2.getOffset());
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
		this.normalizedDocs = new String[dictionary.size()];
		for(int i = 0; i < dictionary.size(); ++i) {
			String key = dictionary.getDocument(i).getOriginalData();
			this.normalizedDocs[i] = SuffixArray.normalize(key);
		}
		this.suffixArray = buildSuffixArray(this.normalizedDocs, tokenizer);
		this.suffixComparator = new SuffixComparator(normalizedDocs);	
	}

	/**
	 * Builds a suffix array up from the keys in the given dictionary.
	 * @param normalizedDocs the normalized documents we want to generate suffix array for.
	 * @param tokenizer the tokenizer that will determine where the suffixes start
	 * @return the generated suffix array
	 */
	private static Suffix[] buildSuffixArray(String[] normalizedDocs, ITokenizer tokenizer) {
		// Generate the unsorted suffix list with an ArrayList
		ArrayList<Suffix> suffixList = new ArrayList<Suffix>();
		for(int i = 0; i < normalizedDocs.length; ++i) {
			Iterator<IToken> iterator = tokenizer.iterator(normalizedDocs[i]);
			while(iterator.hasNext()) {
				IToken token = iterator.next();
				suffixList.add(new Suffix(i, token.getStartIndex()));
			}
		}
		// Convert the unsorted ArrayList to an unsorted suffix array
		Suffix[] suffixArray = new Suffix[suffixList.size()];
		suffixArray = suffixList.toArray(suffixArray);
		suffixList = null;
		
		// Sort the suffix array and return it
		Arrays.sort(suffixArray, new SuffixComparator(normalizedDocs));
		return suffixArray;
	}

	/** 
	 * Normalize a text string by squeezing all blanks and bump case.
	 * @param text the text string to be normalized.
	 * @return the normalized text string.
	 */
	public static String normalize(String text) {		
		text = text.replaceAll("\\s+", " ").trim(); // Squeeze blanks.
		return text.toLowerCase();
	}
	
	/** 
	 * Return the normalized data of a particular document.
	 * @param docID the ID of the document be retrieved.
	 * @return the normalized data of the given document.
	 */
	public String getNormalizedData(int docID) {
		return this.normalizedDocs[docID];
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