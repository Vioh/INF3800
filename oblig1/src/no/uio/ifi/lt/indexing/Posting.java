package no.uio.ifi.lt.indexing;

import java.util.Arrays;

/**
 * Defines information about in which document a term is
 * found plus associated meta data with that, e.g., where
 * in the document that the term is found.
 *
 * @author aleks
 */
public class Posting {
	
	/**
	 * Identifies the document for which this posting is valid.
	 */
	private int documentId;

	/**
	 * Keeps track of where in the document the term occurs.
	 * Sorted in ascending order. Using {@link ArrayList<Integer>}
	 * turned out to be a memory hog.
	 */
	private int[] positions;
    
    /**
     * Constructor.
     * 
     * @param documentId identifies the document for which this posting is valid
     * @param position identifies the position of the first occurrence of the term
     */
    public Posting(int documentId, int position)
    {
        this.documentId = documentId;
        this.positions = new int[] { position };
    }

    /**
	 * Identifies the document for which this posting is valid.
     * 
     * @return the identifier of the document for which this posting is valid
     */
    public int getDocumentId() {
    	return this.documentId;
    }

    /**
     * Returns the position occurrence data for the term.
     * 
     * @return the set of positions in the document where the term occurs
     */
    public int[] getPositions() {
    	return this.positions;
    }
    
    /**
     * Returns the number of times the term occurs in the document, i.e., the 
     * length of the position occurrence list.
     *
     * @return the number of times the term occurs in the document
     */
    public int getOccurrenceCount() {
    	return this.positions.length;
    }
    
    /**
     * Returns the last position occurrence for the term. 
     * 
     * @return the last position occurrence for the term
     */
    private int lastPosition() {
    	return this.positions[this.positions.length - 1];
    }

    /**
     * Updates the position occurrence data with the given position. The
     * positions must be appended in sorted order, or else an exception
     * will be thrown.
     * 
     * @param position the document position to add to the posting entry
     * @throws IllegalStateException
     */
    public void appendPosition(int position) {
    	
        // Barf if the list isn't kept sorted. Puts the onus on the indexer.
        if (this.lastPosition() > position) {
        	throw new IllegalStateException();
        }
        
        // For now, only allocate one extra element to avoid the overhead of
        // having to differentiate between the array's logical size and its capacity.
        // TODO: Revisit this, when we deal with "real" documents.
        this.positions = Arrays.copyOf(this.positions, 1 + this.positions.length);
        
        // Append.
        this.positions[this.positions.length - 1] = position;
    
    }

    /**
     * Trims the size of the posting entry so that it doesn't allocate
     * more memory than needed. This is different from index compression.
     */
    public void trim() {
    }

}
