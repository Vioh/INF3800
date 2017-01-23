package no.uio.ifi.lt.storage;

import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.search.IQuery;

/**
 * Defines a document, i.e., our unit of retrieval.
 * Matched against a {@link IQuery}. For the sake of
 * simplicity, this interface does not define a fielded
 * document model.
 * 
 * @author aleks
 * @see IQuery
 */
public interface IDocument {
	
	/**
	 * Returns the document's "searchable data" as it was
	 * originally defined. 
	 * 
	 * @return the original document data that we are searching against
	 */
	String getOriginalData();
	
	/**
	 * Returns the length of the document's "searchable data" as it
	 * was indexed, i.e., after it had been normalized by a suitable
	 * {@link INormalizer}.
	 * 
	 * @return the length of the normalized version of the "searchable data"
	 */
	int getNormalizedLength();
	
	/**
	 * Returns a string that encodes arbitrary extra meta data for this
	 * document.
	 * 
	 * @return the meta data for this document
	 */
	String getExtraData();

	/**
	 * Returns the static rank for this document, i.e., a number that defines
	 * how relevant we a priori consider this document to be if we don't
	 * take an {@link IQuery} into account.
	 * 
	 * @return the document's static rank
	 */
	double getStaticRank();

}
