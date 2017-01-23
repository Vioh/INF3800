package no.uio.ifi.lt.utils;

import java.util.Iterator;

/**
 * Utility class for iterating over primitive arrays.
 * 
 * @author aleks
 * @param <T> the type of each array element
 */
public class ArrayIterator<T> implements Iterator<T> {

	/**
	 * The array over which we are iterating.
	 */
	T[] array;
	
	/**
	 * The current position into {@link #array}.
	 */
	int current;
	
	/**
	 * The logical size of the array. It might differ from the physical size. 
	 */
	int size;

	/**
	 * Constructor.
	 * 
	 * @param array the array that we want to iterate over
	 */
	public ArrayIterator(T[] array) {
		this.array = array;
		this.current = 0;
		this.size = array.length;
	}

	/**
	 * Constructor.
	 * 
	 * @param array the array that we want to iterate over
	 * @param size the logical size of the array
	 */
	public ArrayIterator(T[] array, int size) {
		this.array = array;
		this.current = 0;
		this.size = size;
	}
	
	/**
	 * Implements the {@link Iterator<T>} interface.
	 * 
	 * @return true if and only if there are more elements to be iterated over
	 */
	
	public boolean hasNext() {
		return (this.current < this.size);
	}
	
	/**
	 * Implements the {@link Iterator<T>} interface.
	 * 
	 * @return the next array element
	 */
	
	public T next() {
		return this.array[this.current++];
	}

	/**
	 * Implements the {@link Iterator<T>} interface. Not supported,
	 * always throws.
	 * 
	 * @throws UnsupportedOperationException
	 */
	
	public void remove() {
		throw new UnsupportedOperationException();
	}		

}
