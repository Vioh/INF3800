package no.uio.ifi.lt.utils;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
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

	/**
	 * Keeps tracks of (entry, offset) pairs, packed into a 64-bit long. <p/>
	 * We can use <code>long</code> instead of <code>Long</code> if we write our own sorting and binary
	 * search routines. Also, the offset can be reduced to 2 bytes, so we can additionally store the LCP
	 * in 2 bytes if we need that.
	 */
	private final Long[] suffixArray;
	
	/** 
	 * Internal helper class for lexicographical sorting of suffixes.
	 */
	private static final class PackedLongComparator implements Comparator<Long> {
		/** Where the actual data is kept, i.e., our "haystack". */
		private final IDocumentStore dictionary;

		/** The input query, if relevant. I.e., our "needle". */
		private final String needle;

		/**
		 * Constructor.
		 * @param dictionary contains the entries whose substrings we are comparing, i.e., our "haystack"
		 * @param needle     the input query, if relevant, or <code>null</code>
		 */
		public PackedLongComparator(IDocumentStore dictionary, String needle) {
			this.dictionary = dictionary;
			this.needle = needle;
		}

		/** {@inheritDoc} */
		public int compare(Long o1, Long o2) {
			long packed1 = o1.longValue();
			long packed2 = o2.longValue();

			// Unpack.
			int entry1 = BitKit.unpackFirst(packed1);
			int offset1 = BitKit.unpackSecond(packed1);
			int entry2 = BitKit.unpackFirst(packed2);
			int offset2 = BitKit.unpackSecond(packed2);

			// Compare.
			String key1 = (entry1 < 0) ? this.needle : this.dictionary.getDocument(entry1).getOriginalData();
			String key2 = (entry2 < 0) ? this.needle : this.dictionary.getDocument(entry2).getOriginalData();
			String suffix1 = (offset1 == 0) ? key1 : key1.substring(offset1);
			String suffix2 = (offset2 == 0) ? key2 : key2.substring(offset2);
			return suffix1.compareTo(suffix2);
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
	}

	/**
	 * Builds a suffix array up from the keys in the given dictionary.
	 * @param dictionary the dictionary whose keys we want to generate a suffix array for
	 * @param tokenizer  the tokenizer that will determine where the suffixes start
	 * @return the generated suffix array
	 */
	private static Long[] buildSuffixArray(IDocumentStore dictionary, ITokenizer tokenizer) {
		// First, count how many expanded items there are.
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
		Long[] suffixArray = new Long[expandedCount];

		// Set the data.
		for (int i = 0; i < dictionary.size(); ++i) {
			String key = dictionary.getDocument(i).getOriginalData();
			Iterator<IToken> iterator = tokenizer.iterator(key);
			while (iterator.hasNext()) {
				IToken token = iterator.next();
				suffixArray[--expandedCount] = BitKit.pack(i, token.getStartIndex());
			}
		}
		// Sort!
		Arrays.sort(suffixArray, new PackedLongComparator(dictionary, null));
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
		return BitKit.unpackFirst(this.suffixArray[index].longValue());
	}

	/**
	 * Returns the offset into the dictionary entry that the given suffix index is for.
	 * @param index a suffix index
	 * @return an offset into a dictionary entry
	 */
	public int getOffset(int index) {
		return BitKit.unpackSecond(this.suffixArray[index].longValue());
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
		// A prefix of a suffix is an infix.
		return Arrays.binarySearch(this.suffixArray, BitKit.pack(-1, 0), new PackedLongComparator(this.dictionary, key));
	}
}