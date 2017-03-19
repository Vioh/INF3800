package no.uio.ifi.lt.search;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.InMemoryInvertedIndex;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.preprocessing.BrainDeadNormalizer;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.ranking.IRanker;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.storage.InMemoryDocumentStore;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;

/**
 * Implements a simple search engine.
 */
public class SearchEngine implements ISearchEngine {
	/** Defines where we emit messages, if at all. */
	private Logger logger;

	/** Defines how we normalize queries and documents. */
	private INormalizer normalizer;

	/** Defines how queries and documents are split into "words". */
	private ITokenizer tokenizer;

	/** Defines where and how documents are stored. */
	private IDocumentStore documentStore;

	/** Defines the inverted index over the contents of the document store. */
	private IInvertedIndex invertedIndex;

	/** Defines how the query is matched against the inverted index. */
	private IQueryEvaluator queryEvaluator;

	/** Defines how we assess relevance. Gets cloned on per lookup basis. */
	private IRanker ranker;
	
	/** Defines how we document is scores against other documents in the document score. */
	private IDocumentEvaluator documentEvaluator;

	/**
	 * Constructor, sort of. For internal use.
	 * @param logger
	 * @param normalizer
	 * @param tokenizer
	 * @param documentStore
	 * @param invertedIndex
	 * @param queryEvaluator
	 * @param ranker
	 * @param docEvaluator
	 */
	private void create(Logger logger, INormalizer normalizer, ITokenizer tokenizer,
		                IDocumentStore documentStore, IInvertedIndex invertedIndex,
		                IQueryEvaluator queryEvaluator, IRanker ranker, IDocumentEvaluator docEvaluator) {
		this.logger = logger;
		this.normalizer = normalizer;
		this.tokenizer = tokenizer;
		this.documentStore = documentStore;
		this.invertedIndex = invertedIndex;
		this.queryEvaluator = queryEvaluator;
		this.ranker = ranker;
		this.documentEvaluator = docEvaluator;
	}

	/**
	 * Constructor. Uses simple default in-memory implementations.
	 * @param filename
	 * @param separator
	 * @param logger
	 * @param tokenizer
	 * @param ranker
	 */
	protected SearchEngine(String filename, Logger logger, ITokenizer tokenizer, IRanker ranker) {
		// Define hoe to normalize the strings, if at all.
		// TODO: Use dependency injection.
		INormalizer normalizer = new BrainDeadNormalizer();

		// Populate the document store from the named file.
		// TODO: Use dependency injection.
		IDocumentStore documentStore = new InMemoryDocumentStore(filename, normalizer, logger);

		// Index the documents.
		// TODO: Use dependency injection.
		IInvertedIndex invertedIndex = new InMemoryInvertedIndex(documentStore, normalizer, tokenizer, logger);

		// For now, evaluate all queries using default settings.
		// TODO: Use dependency injection.
		IQueryEvaluator queryEvaluator = new QueryEvaluator(new QueryEvaluatorSettings(), logger);
		IDocumentEvaluator docEvaluator = new DocumentEvaluator(new QueryEvaluatorSettings(), logger);

		// Invoke the "constructor".
		create(logger, normalizer, tokenizer, documentStore, invertedIndex, queryEvaluator, ranker,docEvaluator);
	}

	/**
	 * Implements the {@link ISearchEngine} interface.
	 */
	public IResultSet search(String value) {
		// Normalize the query.
		// TODO: Use dependency injection.
		IQuery query = new Query(value, this.normalizer);

		// Rankers have state. Clone to be thread-safe.
		IRanker ranker = this.ranker.clone();

		// Evaluate!
		return this.queryEvaluator.evaluate(query, this.invertedIndex, ranker);
	}

	/**
	 * Implements the {@link ISearchEngine} interface.
	 */
	public IResultSet findSimilar(int documentID) {
		// Wrap the document as a query
		IQuery query = new Query(this.documentStore.getDocument(documentID).getOriginalData(), this.normalizer);

		// Rankers have state. Clone to be thread-safe.
		IRanker ranker = this.ranker.clone();

		// Evaluate!
		return this.documentEvaluator.evaluate(query,documentID, this.invertedIndex, ranker);
	}

	/**
	 * Implements the {@link ISearchEngine} interface.
	 */
	public Map<String,Integer> getFrequencies(String lookups) {
		// frequencies for each lookup token
		Map<String,Integer> frequencies = new HashMap<String,Integer>();
		
		IToken[] loopupsTokenized = this.tokenizer.toArray(lookups);
		for (IToken lookup : loopupsTokenized) {
			int lexiconID = this.invertedIndex.getLexicon().lookup(lookup.getValue());
			if (lexiconID != -1) {
				PostingList ps = this.invertedIndex.getPostingList(lexiconID);
				frequencies.put(lookup.getValue(), ps.size());
			}
		}
		return frequencies;
	}
}