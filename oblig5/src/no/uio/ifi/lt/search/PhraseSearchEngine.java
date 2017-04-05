package no.uio.ifi.lt.search;
import java.util.logging.Logger;
import no.uio.ifi.lt.preprocessing.BrainDeadNormalizer;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.storage.InMemoryDocumentStore;
import no.uio.ifi.lt.tokenization.ITokenizer;
import no.uio.ifi.lt.utils.SuffixArray;

/**
 * Implements a simple, in-memory search engine that does
 * exact phrase searches. No ranking.
 */
public class PhraseSearchEngine implements ISearchEngine {
	/** Defines where we emit messages, if at all. */
	private Logger logger;
	
	/** Defines how we normalize queries and documents. */
	private INormalizer normalizer;

	/** Defines how queries and documents are split into "words". */
	private ITokenizer tokenizer;
	
	/** Defines where and how documents are stored. */
	private IDocumentStore documentStore;
	
	/** Defines the "index" over the contents of the document store. */
	private SuffixArray suffixArray;
	
	/**
	 * Constructor. Uses simple default in-memory implementations.
	 * @param filename
	 * @param separator
	 * @param logger
	 * @param tokenizer
	 */
	public PhraseSearchEngine(String filename, Logger logger, ITokenizer tokenizer) {
		this.logger = logger;
		this.normalizer = new BrainDeadNormalizer();
		this.tokenizer = tokenizer;
		this.documentStore = new InMemoryDocumentStore(filename, this.normalizer, this.logger);
		this.suffixArray = new SuffixArray(this.documentStore, this.tokenizer);
	}
	
	/**
	 * Implements the {@link ISearchEngine} interface.
	 */
	public IResultSet search(String value) {
		int size = this.suffixArray.size();
		
		// TODO: make sure that the "document" and the "query" are equally normalized.
		IQuery query = new Query(value, this.normalizer);

		// Placeholder.
		ResultSet resultSet = new ResultSet(query, 50);
		
		// Consult the suffix data. A prefix of a suffix is an infix.
		int index = this.suffixArray.lookup(value);

		if (index >= size) {
			return resultSet;
		}
		// Key not found? The index tells us the logical insertion point, which is the
		// starting point for all the prefix matches.
		if (index < 0) {
			index = -(index + 1);
		}
		// Report all phrase matches.
		// TODO: Eliminate duplicates.
		// TODO: Check word boundaries?
		// TODO: Report only the "best" ones, according to some criterion.
		// TODO: Use precomputed LCP values instead of a "startsWith" check.
		while (index < size) {
			int entry = this.suffixArray.getEntry(index);
			int offset = this.suffixArray.getOffset(index);
			IDocument document = this.documentStore.getDocument(entry);
			String key = document.getOriginalData();
			String suffix = (offset == 0) ? key : key.substring(offset);
			if (!suffix.startsWith(query.getOriginalQuery())) {
				break;
			}
			// Just append lexicographically.
			resultSet.appendResult(new Result(document, 1.0));
			++index;
		}
		return resultSet;
	}
	
	@Override
	public IResultSet findSimilar(int docID) {
		// TODO Auto-generated method stub
		return null;
	}
}