package no.uio.ifi.lt.utils;

import java.util.Iterator;



/**
 * Implements a binary heap of (data, rank) pairs. The maximum number of values
 * in the heap is fixed. The types of the data items and their rank values are
 * parameterized. The data items are organized according to their rank values
 * such that the top of the heap always contains the item having the smallest
 * rank value. I.e., this is a "min heap".
 * 
 * @author aleks
 * @param <D> the type of the data item
 * @param <R> the type of the data item's rank value
 */
public class Heap<D, R extends Comparable<R>> implements Iterable<HeapItem<D, R>> {
	
    /**
     * The array of data items, organized according to the heap principle.
     */
    private HeapItem<D, R>[] items;
    
    /**
     * The logical size of the heap.
     */
    private int size;
    
    /**
     * Constructor
     * 
     * @param capacity the maximum number of items in the heap.
     */
    @SuppressWarnings("unchecked")
	public Heap(int capacity)
    {
    	
    	// TODO: This looks clunky.
    	this.items = new HeapItem[capacity];
    	
    	for (int i = 0; i < capacity; ++i) {
    		this.items[i] = new HeapItem();
    	}
    	
    	// Logically no entries yet.
    	this.size = 0;
    }
    
    /**
     * Returns the size of the heap, i.e., how many elements that
     * are currently organized in the heap.
     * 
     * @return the number of elements in the heap
     */
    public int size() {
    	return this.size;
    }

    /**
     * Returns the capacity of the heap, i.e., how many elements that
     * at most can be contained in the heap.
     * 
     * @return the maximum number of elements in the heap
     */
    public int capacity() {
    	return this.items.length;
    }
    
    /**
     * Returns true if the heap is empty, i.e., if it contain no
     * elements.
     * 
     * @return true if and only if the heap contains no elements
     */
    public boolean isEmpty() {
    	return (this.size() == 0);
    }
    
    /**
     * Clears the heap, i.e., logically resets it back to having no
     * elements.
     */
    public void clear() {
    	this.size = 0;
    }

    /**
     * Returns the item on top of the heap, i.e., the item
     * having the smallest rank value.
     * 
     * @return the item on top of the heap
     */
    public HeapItem<D, R> getRoot() {
    	if (this.isEmpty()) {
    		throw new IndexOutOfBoundsException();
    	}
    	return this.items[0];
    }
    
    /**
     * Returns the data item on top of the heap, i.e., the data item
     * having the smallest rank value.
     * 
     * @return the data item on top of the heap
     */
    public D getRootData() {
    	if (this.isEmpty()) {
    		throw new IndexOutOfBoundsException();
    	}
    	return this.items[0].data;
    }

    /**
     * Returns the rank value associated with the data item on top of the heap,
     * i.e., the smallest rank value.
     * 
     * @return the smallest rank value
     */
    public R getRootRank() {
    	if (this.isEmpty()) {
    		throw new IndexOutOfBoundsException();
    	}
    	return this.items[0].rank;
    }
        
    /**
     * Utility method for heap index navigation, identifies the index
     * holding the "parent" of the given index.
     * 
     * @param i the index whose parent index we want t determine
     * @return the parent index of the input index
     */
    private static int getParentIndex(int i) {
    	return ((i - 1) >> 1);
    }

    /**
     * Utility method for heap index navigation, identifies the index
     * holding the "left child" of the given index.
     * 
     * @param i the index whose left child index we want t determine
     * @return the left child index of the input index
     */
    private static int getLeftChildIndex(int i) {
    	return ((i << 1) + 1);
    }

    /**
     * Utility method for heap index navigation, identifies the index
     * holding the "right child" of the given index.
     * 
     * @param i the index whose right child index we want t determine
     * @return the right child index of the input index
     */
    private static int getRightChildIndex(int i) {
    	return ((i << 1) + 2);
    }

    /**
     * Utility method that swaps two identified entries in the heap's
     * backing array.
     * 
     * @param i the index that identifies one of the items to swap 
     * @param j the index that identifies the other item to swap
     */
    private void swap(int i, int j)
    {
    	this.items[i].swap(this.items[j]);
    }
    
    /**
     * Ensures that the backing array is kept organized according to
     * the heap principle.
     * 
     * @param i the start index of the heapification process
     */
    private void heapify(int i) {

    	// Look to the left and right of the current index.
        int left = Heap.getLeftChildIndex(i);
        int right = Heap.getRightChildIndex(i);

        // Which is smaller?
        int smallest = i;

        if (left < this.size() && this.items[left].rank.compareTo(this.items[smallest].rank) < 0) {
            smallest = left;
        }

        if (right < this.size() && this.items[right].rank.compareTo(this.items[smallest].rank) < 0) {
            smallest = right;
        }
        
        // Play cup! Percolate downwards.
        if (smallest != i) {
            this.swap(i, smallest);
            this.heapify(smallest);
        }
    	
    }
    
    /**
     * Insert a new item into the heap.
     * 
     * @param data
     * @param rank
     */
    public void insert(D data, R rank)
    {

    	// Start searching here.
        int i = this.size++;
        int j = Heap.getParentIndex(i);

        // Define where to insert the element.
        while (i != 0 && rank.compareTo(this.items[j].rank) < 0)
        {
        	this.items[i].set(this.items[j]);
            i = j;
            j = Heap.getParentIndex(i);
        }

        // Insert!
        this.items[i].set(data, rank);
        
    }

    /**
     * Deletes the current root element. A new root element takes
     * its place.
     */
    public void deleteRoot()
    {

    	if (this.isEmpty()) {
    		throw new IndexOutOfBoundsException();
    	}
    	
        // Replace the root with the tail. Then delete the tail.
        this.items[0].set(this.items[--this.size]);

        // Reorganize and find the new root.
        this.heapify(0);

    }

    /**
     * Implements the {@link Iterable<HeapItem<D, R>>} interface.
     * Note that the iterator does not iterate over the items in
     * sorted order.
     */
	
	public Iterator<HeapItem<D, R>> iterator() {
		return new ArrayIterator<HeapItem<D, R>>(this.items, this.size);
	}
	
}
