package no.uio.ifi.lt.utils;

/**
 * Scope for static utilities and helper methods related to bit fiddling.
 */
public final class BitKit {
	private static final long NUM_INT_BITS = 32;

	private BitKit() {}

	/**
	 * Packs two 32-bit <code>int</code> values into a 64-bit <code>long</code> value.
	 * @param first  the first 32-bit <code>int</code>
	 * @param second the first 32-bit <code>int</code>
	 * @return the packed <code>long</code> value
	 */
	public static long pack(int first, int second) {
		return ((long) first << NUM_INT_BITS) | (second);
	}

	/**
	 * Unpacks the first of the two 32-bit <code>int</code> values packed
	 * into a 64-bit <code>long</code> value.
	 * @param packed a packed <code>long</code> value
	 * @return the first 32-bit <code>int</code> packed into the input
	 */
	public static int unpackFirst(long packed) {
		return (int) (packed >> NUM_INT_BITS);
	}

	/**
	 * Unpacks the second of the two 32-bit <code>int</code> values packed
	 * into a 64-bit <code>long</code> value.
	 * @param packed a packed <code>long</code> value
	 * @return the second 32-bit <code>int</code> packed into the input
	 */
	public static int unpackSecond(long packed) {
		return (int) (packed);
	}
}