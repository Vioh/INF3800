package no.uio.ifi.lt.search;
import no.uio.ifi.lt.indexing.Posting;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.tokenization.IToken;

/**
 * Representation of a posting list currently traversed for query evaluation.  
 * The class contains 3 elements: <ol>
 * <li> a posting list, 
 * <li> an index position which keeps track of the current index position 
 *      of the posting list in the traversal;
 * <li> the query token related to the traversed posting list
 * </ol>
 */
public class TraversedPostingList implements Comparable<TraversedPostingList> {
	IToken queryTerm; // the query term
	PostingList postingList;
	int currentPosition = 0 ;
	
	/**
	 * Creates a new posting list to traverse
	 * @param postingList the posting list
	 * @param queryTerm the associated query term
	 */
	public TraversedPostingList(PostingList postingList, IToken queryTerm) {
		this.postingList = postingList;
		this.queryTerm = queryTerm;
	}
	
	/**
	 * Increment the current position in the traversal
	 */
	public void incrementPosition() {
		currentPosition++;
	}
	
	/**
	 * @return the current index position in the traversal
	 */
	public int getCurrentPosition() {
		return currentPosition;
	}
	
	/**
	 * @return the posting for the current position
	 */
	public Posting getCurrentPosting() {
		return postingList.getPosting(currentPosition);
	}
	
	/**
	 * @return the document ID for the current position
	 */
	public int getCurrentDocumentId() {
		return postingList.getPosting(currentPosition).getDocumentId();
	}
	
	/**
	 * @return the query term associated with the posting list
	 */
	public IToken getQueryTerm() {
		return queryTerm;
	}
	
	/**
	 * @return true if the posting list has been fully traversed, false otherwise
	 */
	public boolean isFullyTraversed() {
		return (currentPosition >= postingList.size());
	}

	/**
	 * @return the original posting list
	 */
	public PostingList getPostingList() {
		return postingList;
	}
	
	/**
	 * Compares the current document Ids for the two traversed posting
	 * lists.  This method is used to quickly extract the posting list
	 * with a minimum document ID.
	 * @param arg0 the other posting list
	 * @return 
	 */
	public int compareTo(TraversedPostingList arg0) {
		return (getCurrentDocumentId() - arg0.getCurrentDocumentId());
	}
}