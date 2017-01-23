package no.uio.ifi.lt.storage;


/**
 * A very simple and straightforward implementation of the
 * {@link IDocument} interface.

 * @author aleks
 */
public class Document implements IDocument {
	
	/**
	 * The document's original and searchable data.
	 */
	private String originalData;
	
	/**
	 * Arbitrary meta data for this document.
	 */
	private String extraData;
	
	/**
	 * The length of a normalized version of {@link #originalData}.
	 */
	private int normalizedLength;
	
	/**
	 * The document's static rank.
	 */
	private double staticRank;

	/**
	 * Implements the {@link IDocument} interface.
	 */
	
	public String getExtraData() {
		return this.extraData;
	}
	
	/**
	 * The logical inverse of {@link #getExtraData()}.
     *
	 * @param extraData the document's meta data
	 */
	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	/**
	 * Implements the {@link IDocument} interface.
	 */
	
	public int getNormalizedLength() {
		return this.normalizedLength;
	}

	/**
	 * The logical inverse of {@link #getNormalizedLength()}.
	 *
	 * @param normalizedLength the document's normalized length
	 */
	public void setNormalizedLength(int normalizedLength) {
		this.normalizedLength = normalizedLength;
	}
	
	/**
	 * Implements the {@link IDocument} interface.
	 */
	
	public String getOriginalData() {
		return this.originalData;
	}

	/**
	 * The logical inverse of {@link #getOriginalData()}.
     *
	 * @param extraData the document's searchable data
	 */
	public void setOriginalData(String originalData) {
		this.originalData = originalData;
	}
	
	/**
	 * Implements the {@link IDocument} interface.
	 */
	
	public double getStaticRank() {
		return this.staticRank;
	}

	/**
	 * The logical inverse of {@link #getStaticRank()}.
     *
	 * @param staticRank the document's static rank
	 */
	public void setStaticRank(double staticRank) {
		this.staticRank = staticRank;
	}
	
}
