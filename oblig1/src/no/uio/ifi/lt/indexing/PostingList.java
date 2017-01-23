package no.uio.ifi.lt.indexing;

import java.util.ArrayList;


/**
 * Defines information about in which documents a term is
 * found plus associated meta data with that, e.g., where
 * in the documents that the term is found.
 * *
 * @author aleks
 */
public class PostingList {

	/**
	 * Keeps tracks of in which documents the term occurs. Sorted
	 * in ascending order according to {@link Posting.getDocumentId()}.
	 */
    private ArrayList<Posting> postings;

    /**
     * Returns the size of the posting list, i.e., the number of postings
     * in this posting list. Since there is one posting per document that
     * contains the term, the size of the posting list reveals the term's
     * document frequency.
     * 
     * @return the size of the posting list
     */
    public int size() {
    	return (this.postings == null) ? 0 : this.postings.size();
    }

    /**
     * Returns the last {@link Posting} in the posting list. The posting
     * list is kept sorted by the document identifiers.
     * 
     * @return the last {@link Posting} in the posting list
     */
    public Posting getLastPosting() {
    	return this.postings.get(this.postings.size() - 1);
    }

    /**
     * Returns the first {@link Posting} in the posting list. The posting
     * list is kept sorted by the document identifiers.
     * 
     * @return the first {@link Posting} in the posting list
     */
    public Posting getFirstPosting() {
    	return this.postings.get(0);
    }

    /**
     * Returns a given {@link Posting} in the posting list.
     * 
     * @param i the index of the {@link Posting} to return
     * @return the identified {@link Posting}
     */
    public Posting getPosting(int i)
    {
        return this.postings.get(i);
    }
    
    /**
     * Updates the posting list with the given {@link Posting}. The
     * posting entries must be appended in sorted order, or else an
     * exception will be thrown.
     * 
     * @param posting the {@link Posting} entry to add to the posting list
     * @throws IllegalStateException
     */    
    public void appendPosting(Posting posting) {
    	
        // First entry?
        if (this.postings == null) {
            this.postings = new ArrayList<Posting>(1);
        }

        // Barf if the list isn't kept sorted. Puts the onus on the indexer.
        else if (this.getLastPosting().getDocumentId() >= posting.getDocumentId()) {
        	throw new IllegalStateException();
        }

        this.postings.add(posting);
    	
    }
    
    /**
     * Trims the size of the posting list so that it doesn't allocate
     * more memory than needed. This is different from index compression.
     */
    public void trim() {
    	
    	for (Posting posting : this.postings) {
    		posting.trim();
    	}
    	
    	this.postings.trimToSize();
    	
    }

	/**
	 * Prints the posting list for debugging and manual
	 * inspection.
	 */
	public void debugPrint() {
		
		for (Posting posting : this.postings) {
			System.out.print(String.format("[%d;{", posting.getDocumentId()));
			for (int position : posting.getPositions()) {
				System.out.print(String.format("%d,", position));				
			}
			System.out.print("}]");
		}
		
		System.out.println();
		
	}
    
}
