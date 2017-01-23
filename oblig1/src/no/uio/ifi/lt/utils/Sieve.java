package no.uio.ifi.lt.utils;

import java.util.Iterator;




/**
 * Implements a "sieve", i.e., a data structure through which
 * one can "sift" M (data, rank) items, and be left with the
 * N (data, rank) pairs having the largest rank values. Ties are
 * resolved arbitrarily. A sieve is an efficient way of selecting
 * the "best" N items from a set of M items, where N << M.
 * 
 * @author aleks
 * @param <D> the type of the data item
 * @param <R> the type of the data item's rank value
 */
public class Sieve<D, R extends Comparable<R>> implements Iterable<HeapItem<D, R>> {
	
	/**
	 * A "max sieve" uses a "min heap" to keep track
	 * of the worst of the best items seen so far.
	 */
	private Heap<D, R> heap;

    /**
     * Constructor
     * 
     * @param capacity the maximum number of items to keep in the sieve.
     */
	public Sieve(int capacity)
    {
    	this.heap = new Heap<D, R>(capacity);
    }
    
    /**
     * Returns the size of the sieve, i.e., how many elements that
     * are currently organized in the sieve.
     * 
     * @return the number of elements in the sieve
     */
    public int size() {
    	return this.heap.size();
    }
    
    /**
     * Returns the capacity of the sieve, i.e., how many elements that
     * at most are kept in the sieve after sifting.
     * 
     * @return the maximum number of elements in the sieve
     */
    public int capacity() {
    	return this.heap.capacity();
    }

    /**
     * Sifts a given (data, rank) pair through the sieve.
     * 
     * @param data the current data item to sift
     * @param rank the rank value associated with the current data item
     */
    public void sift(D data, R rank)
    {
    	
    	// If we haven't seen enough items yet, it's a keeper.
        if (this.size() < this.capacity())
        {
            this.heap.insert(data, rank);
        }
        
        // If the current item is better than the worst of the items
        // seen so far, forget about the worst item and keep the current
        // item instead.
        else if (this.heap.getRootRank().compareTo(rank) < 0)
        {
            this.heap.deleteRoot();
            this.heap.insert(data, rank);
        }
        
    }

    /**
     * Implements the {@link Iterable<HeapItem<D, R>>} interface.
     * Note that the iterator does not iterate over the items in
     * sorted order.
     */
	
	public Iterator<HeapItem<D, R>> iterator() {
		return this.heap.iterator();
	}
    
}
