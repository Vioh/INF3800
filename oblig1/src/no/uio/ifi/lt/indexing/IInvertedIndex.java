package no.uio.ifi.lt.indexing;

import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.tokenization.ITokenizer;

/**
 * Defines an inverted index, i.e., a set of posting lists and other
 * associated data. The inverted index is at the heart of any search
 * engine.
 * 
 * @author aleks
 */
public interface IInvertedIndex {
	
	/**
	 * Returns the {@link ITokenizer} used when the index was created.
	 * Queries that are to be evaluated against this index need to be
	 * processed using the same tokenizer.
	 * 
	 * @return the {@link ITokenizer} used to create the index
	 */
	ITokenizer getTokenizer();
	
	/**
	 * Returns the {@link INormalizer} used when the index was created.
	 * Queries that are to be evaluated against this index need to be
	 * processed using the same normalizer.
	 * 
	 * @return the {@link INormalizer} used to create the index
	 */
	INormalizer getNormalizer();
	
	/**
	 * Returns the {@link IDocumentStore} over which the index was
	 * created.
	 * 
	 * @return the {@link IDocumentStore} over which the index was created
	 */
	IDocumentStore getDocumentStore();
	
	/**
	 * Returns the {@link ILexicon} for this index, i.e., the mechanism
	 * that keeps track of which terms that are known and which identifiers
	 * these terms map to.
	 * 
	 * @return the {@link ILexicon} built during index creation
	 */
	ILexicon getLexicon();

	/**
	 * Returns the {@link PostingList} for a given lexicon identifier, i.e.,
	 * the posting list for a given term after that term has been mapped
	 * through the lexicon.
	 * 
	 * @param lexiconId identifies the term for which we want the associated posting list
	 * @return the {@link PostingList} for the given lexicon identifier
	 */
	PostingList getPostingList(int lexiconId);

}
