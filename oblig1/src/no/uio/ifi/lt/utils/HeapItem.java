package no.uio.ifi.lt.utils;




/**
 * Implements an item in a {@link Heap}.
 * 
 * @author aleks
 * @param <D> the type of the data item
 * @param <R> the type of the data item's rank value
 */
public class HeapItem<D, R> {

	/**
	 * The data item.
	 */
    public D data;

    /**
     * The rank value associated with the data item.
     */
    public R rank;

    /**
     * Assignment operator, sort of. Sets this item's
     * values to the values of the given item.
     * 
     * @param item the item whose values we are copying
     */
    public void set(HeapItem<D, R> item) {
    	this.data = item.data;
    	this.rank = item.rank;
    }

    /**
     * Assignment operator, sort of.
     * 
     * @param data the new data item
     * @param rank the rank value associated with the new data item
     */
    public void set(D data, R rank) {
    	this.data = data;
    	this.rank = rank;
    }

    /**
     * Swaps the values of this item with the given item, i.e.,
     * exchanges their values.
     * 
     * @param item the item with which we are exchanging values
     */
    public void swap(HeapItem<D, R> item) {
    	D data = this.data;
    	R rank = this.rank;
    	this.data = item.data;
    	this.rank = item.rank;
    	item.data = data;
    	item.rank = rank;    	
    }
	
}
