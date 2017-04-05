package no.uio.ifi.lt.search;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.ranking.IRanker;
import no.uio.ifi.lt.storage.DocumentVector;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.storage.IDocumentVector;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;
import no.uio.ifi.lt.utils.HeapItem;
import no.uio.ifi.lt.utils.Sieve;

public class DocumentEvaluator implements IDocumentEvaluator {
	/** Defines the evaluation parameters. */
	private QueryEvaluatorSettings settings;

	/** Where we emit messages, if at all. */
	private Logger logger;

	/**
	 * Constructor.
	 * @param settings defines the evaluation parameters
	 * @param logger defines where to emit log messages, if at all
	 */
	public DocumentEvaluator(QueryEvaluatorSettings settings, Logger logger) {
		this.settings = settings;
		this.logger = logger;
	}
	
	public IResultSet evaluate(IQuery document, int documentID, IInvertedIndex invertedIndex, IRanker ranker) {
		// Paranoia.
		if (document.getNormalizedLength() == 0) {
			return new ResultSet(document, 0);
		}
		// Spam the logs?
		boolean debug =  this.settings.debug && (this.logger != null) && this.logger.isLoggable(Level.FINEST);
		ranker.debug(debug);

		// Synchronize query processing with document processing.
		INormalizer normalizer = invertedIndex.getNormalizer();
		ITokenizer tokenizer = invertedIndex.getTokenizer();
		ILexicon lexicon = invertedIndex.getLexicon();
		IDocumentStore documentStore = invertedIndex.getDocumentStore();

		// Process a normalized version, not the raw value.
		String normalizedDocument = normalizer.normalize(document.getOriginalQuery());
		IToken[] documentTerms = tokenizer.toArray(normalizedDocument);
		IDocumentVector docVectorOrg, docVector;
		
		// Creates the document vector for the document that will be compared with other documents
		docVectorOrg = new DocumentVector(documentTerms,lexicon,invertedIndex);
		Sieve<Integer, Double> bestDocumentIds = new Sieve<Integer, Double>(this.settings.candidates); 

		// Computes the cosine similarity between the document vector of
		// the query document, and all other documents in the doc store
		for (int currentDocId = 0;currentDocId<documentStore.size();currentDocId++) {
			if (currentDocId==documentID) {
				/* 
				 * For sanity testing, you can check if the consine similarity 
				 * of two identical document vectors is equal to 1!
				 */
				continue;
			}
			normalizedDocument = normalizer.normalize(documentStore.getDocument(currentDocId).getOriginalData());
			documentTerms = tokenizer.toArray(normalizedDocument);
			docVector = new DocumentVector(documentTerms,lexicon,invertedIndex);
			
			// The actual consine similarity is computed here, and sent directly to the sift!
			bestDocumentIds.sift(currentDocId,docVector.getCosineSimilarity(docVectorOrg));
		}
		ResultSet results = new ResultSet(document, bestDocumentIds.size());
		System.out.println("\n\n FINDING SIMILAR DOCUMENTS TO: \n"+document.getOriginalQuery());
		for (HeapItem<Integer, Double> item : bestDocumentIds) {        	
			int documentId = item.data;
			double rankScore = item.rank;
			results.appendResult(new Result(documentStore.getDocument(documentId), rankScore));
		}
		results.sortByRelevance();
		return results;
	}
}