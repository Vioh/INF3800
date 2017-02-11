package no.uio.ifi.lt.indexing;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import no.uio.ifi.lt.utils.HeapItem;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;
import no.uio.ifi.lt.indexing.InMemoryLexicon;
import no.uio.ifi.lt.indexing.Posting;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.utils.Sieve;

/**
 * A simple in-memory implementation of {@link IInvertedIndex}, suitable for 
 * small to medium-sized document collections.
 */
public class InMemoryInvertedIndex implements IInvertedIndex {	
	/** The tokenizer used when creating the inverted index. */
	private ITokenizer tokenizer;
	
	/** The normalizer used when creating the inverted index. */
	private INormalizer normalizer;
	
	/** The lexicon built when creating the inverted index. */
	private ILexicon lexicon;
	
	/** The set of documents over which the inverted index was created. */
	private IDocumentStore documentStore;
	
	/** The actual inverted index. */
	private ArrayList<PostingList> invertedIndex;
	
	/** For emitting log messages, if any. */
	private Logger logger;

	/**
	 * Constructor.
	 * @param documentStore the set of document we want to index
	 * @param normalizer defines how the documents should be normalized
	 * @param tokenizer defines how the documents should be tokenized
	 * @param logger defines where to emit log messages
	 */
	public InMemoryInvertedIndex(IDocumentStore documentStore, INormalizer normalizer,
			                     ITokenizer tokenizer, Logger logger) {		
		// Emit messages here. Optional.
		this.logger = logger;
		
		// Crank up the actual indexing process.
		this.BuildIndex(documentStore, normalizer, tokenizer);
	}
	
	/**
	 * Constructor, sort of. Actually builds up the inverted index here.
	 * @param documentStore the set of document we want to index
	 * @param normalizer defines how the documents should be normalized
	 * @param tokenizer defines how the documents should be tokenized
	 */
	private void BuildIndex(IDocumentStore documentStore, INormalizer normalizer,
                            ITokenizer tokenizer) {
		// Keep references to the stuff that defines the what and the how.
		// We're going to need it later when doing lookups.
		this.documentStore = documentStore;
		this.normalizer = normalizer;
		this.tokenizer = tokenizer;
		
		// Reset.
		this.lexicon = new InMemoryLexicon();
		this.invertedIndex = new ArrayList<PostingList>();
        
		if (this.logger != null) {
			this.logger.info(String.format("Indexing %d documents...", this.documentStore.size()));
		}

		// Index all documents.
		for (int documentId = 0; documentId < this.documentStore.size(); ++documentId) {
			this.addDocument(this.documentStore.getDocument(documentId), documentId);
		}

		// Emit some basic index statistics.
		if (this.logger != null) {
			this.logger.info(String.format("Indexed %d unique terms.", this.lexicon.size()));
		}

		// Detect and deal with stopwords, i.e., terms that occur "very often".        
		//this.detectStopwords();
		
		// Be stingy on memory, although without actually compressing the index.        
		this.trim();
		
		// Debugging/development.
		//this.debugPrint();
	}

	/**
	 * Trims the size of the inverted index so that it doesn't allocate
	 * more memory than needed. This is different from index compression.
	 */
	private void trim() {
		for (PostingList postings : this.invertedIndex) {
			postings.trim();
		}		
		this.invertedIndex.trimToSize();
	}
	
	/**
	 * Prints the inverted index to standard out for debugging and manual inspection.
	 */
	@SuppressWarnings("unused")
	private void debugPrint() {
		for (String term : this.lexicon) {
			int lexiconId = this.lexicon.lookup(term);
			System.out.print(String.format("'%s':%d->", term, lexiconId));
			this.getPostingList(lexiconId).debugPrint();			
		}
	}
	
	/**
	 * Adds the given document to the inverted index.
	 * @param document the {@link IDocument} to index
	 * @param documenId the document's identifier
	 */
	private void addDocument(IDocument document, int documentId) {		
		// Index a normalized version of the document's data.
		String normalized = this.normalizer.normalize(document.getOriginalData());
				
		// Process all document tokens.
		Iterator<IToken> tokenIterator = this.tokenizer.iterator(normalized);
					
		while (tokenIterator.hasNext()) {		
			IToken token = tokenIterator.next();

			/* THIS IS ASSIGNMENT 1:
			 * Iterate through the document tokens. If the token has been
			 * processed/known, it should be added to the posting list for 
			 * the correct document (with the correct position). If it is 
			 * new, a new posting list should be created. 
			 */
			
			int tokenID = this.lexicon.lookup(token.getValue());			
			if (tokenID == ILexicon.INVALID) { 
				PostingList pl = new PostingList();
				this.invertedIndex.add(pl);
				pl.appendPosting(new Posting(documentId, token.getPosition()));
			} else {
				PostingList pl = this.invertedIndex.get(tokenID);
				Posting p = pl.getLastPosting();
				if (p.getDocumentId() == documentId) 
					p.appendPosition(token.getPosition());
				else
					pl.appendPosting(new Posting(documentId, token.getPosition()));
			}
			this.lexicon.addValue(token.getValue());
		}
	}

	/**
	 * Implements the {@link IInvertedIndex} interface.
	 */	
	public ILexicon getLexicon() {
		return this.lexicon;
	}

	/**
	 * Implements the {@link IInvertedIndex} interface.
	 */	
	public INormalizer getNormalizer() {
		return this.normalizer;
	}

	/**
	 * Implements the {@link IInvertedIndex} interface.
	 */	
	public PostingList getPostingList(int lexiconId) {
		return this.invertedIndex.get(lexiconId);
	}

	/**
	 * Implements the {@link IInvertedIndex} interface.
	 */	
	public ITokenizer getTokenizer() {
		return this.tokenizer;
	}

	/**
	 * Implements the {@link IInvertedIndex} interface.
	 */	
	public IDocumentStore getDocumentStore() {
		return this.documentStore;
	}

	
	/**
	 * For debugging. Identifies "stopwords", i.e., words that occur in very many documents
	 * and thus have very long posting lists.
	 */
//	private void detectStopwords() {		
//		Sieve<String, Integer> sieve = new Sieve<String, Integer>(10);
//		
//		for (String value : this.lexicon) {
//			sieve.sift(value, this.invertedIndex.get(this.lexicon.lookup(value)).size());			
//		}		
//		if (this.logger != null) {
//			for (HeapItem<String, Integer> item : sieve) {
//				String value = item.data;
//				int documentFrequency = item.rank;
//				this.logger.finest(String.format("Term '%s' occurs in %d documents (%f%%)",
//						                          value, documentFrequency, (100.0 * documentFrequency) / this.documentStore.size()));				
//			}
//		}			
//	}	
}