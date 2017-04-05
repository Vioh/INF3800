package no.uio.ifi.lt.storage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.ILexicon;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.tokenization.IToken;

/**
 * A simple document vector instantiating a "sparse" document vector, 
 * which means that it leaves out all theoretical zeros in the vector. 
 * {@link nonZeroList} holds the lexicon IDs that are not zero, their 
 * corresponding tf-idf values are kept in {@link tfIdfScoreList}.
 */
public class DocumentVector implements IDocumentVector {
	/** Sorted list of instantiated lexicon IDs */
	private ArrayList<Integer> nonZeroList;
	
	/** tf-idf score for the lexicon ids kept in {@link nonZeroList} */
	private ArrayList<Double> tfIdfScoreList;

	public DocumentVector(IToken[] documentTerms, ILexicon lexicon, IInvertedIndex invertedIndex) {
		createDocVector(documentTerms,lexicon,invertedIndex);

	}
	
	public int size() {
		return this.tfIdfScoreList.size();
	}

	public Iterator<Double> getTfIdfScoreIterator() {
		return this.tfIdfScoreList.iterator();
	}

	/**
	 * The quasi-constructor expands the condensed document vector.
	 * @param documentTerms array of terms in document
	 * @param documentID document ID for document
	 * @param lexicon 
	 * @param invertedIndex
	 */
	private void createDocVector(IToken[] documentTerms, ILexicon lexicon,
		                         IInvertedIndex invertedIndex) {
		double tfValue,idfValue;
		int dfCount;
		int[] lexIDs = new int[documentTerms.length];
		for(int i = 0; i<lexIDs.length;i++) {
			lexIDs[i] = lexicon.lookup(documentTerms[i].getValue());
		}
		Arrays.sort(lexIDs);
		this.nonZeroList = new ArrayList<Integer>(lexIDs.length);
		this.tfIdfScoreList = new ArrayList<Double>(lexIDs.length);
		
		PostingList[] postingListsForTerms = new PostingList[lexIDs.length];
		for (int i = 0; i < lexIDs.length; i++) {
			if (lexIDs[i] != -1) {
				postingListsForTerms[i] = invertedIndex.getPostingList(lexIDs[i]);
			}
		}
		for(int i = 0; i<lexIDs.length;i++) {
			// Since we don't have immediate access to the postings, we use this 
			// relatively crude way of finding duplicates in the document
			int occurrences = 1;
			while(lexIDs.length>i+1) {
				if (lexIDs[i]==lexIDs[i+1]) {
					occurrences++;
					i++;
				}
				else {
					break;
				}
			}
			// tf- idf weights are computed:
			dfCount = postingListsForTerms[i].size();
			tfValue = occurrences;
			idfValue = Math.log(10000 / dfCount);
			this.nonZeroList.add(lexIDs[i]);
			this.tfIdfScoreList.add(tfValue * idfValue);
		}
		this.nonZeroList.trimToSize();
		this.tfIdfScoreList.trimToSize();

	}


	/* 
	 * @see no.uio.ifi.lns.IDocumentVector#get(int)
	 */
	@Override
	public double get(int lexiconId) {
		int insertionPoint = Collections.binarySearch(this.nonZeroList, lexiconId);
		if (insertionPoint < 0) {
			return 0;
		}
		else {
			return this.tfIdfScoreList.get(insertionPoint);
		}
	}

	/* 
	 * @see no.uio.ifi.lns.IDocumentVector#getCosineSimilarity(no.uio.ifi.lns.IDocumentVector)
	 */
	@Override
	public double getCosineSimilarity(IDocumentVector docVector) {
		double numerator = 0;
		
		// Computes the dot product
		for (int i = 0; i < this.nonZeroList.size(); i++) {
			numerator += docVector.get(nonZeroList.get(i)) * get(nonZeroList.get(i));
		}
		if (numerator == 0) {
			return 0;
		}
		// Computes the euclidean length, unless the numerator is zero.
		double eucDist1 = 0;
		for (int i = 0; i<this.size();i++) {
			eucDist1 += Math.pow(this.get(this.nonZeroList.get(i)),2);
		}
		eucDist1 =  Math.sqrt(eucDist1);
		double eucDist2 = 0;
		Iterator<Double> tfIdfIterator = docVector.getTfIdfScoreIterator();
		while (tfIdfIterator.hasNext()) {
			eucDist2 += Math.pow(tfIdfIterator.next(), 2);
		}
		eucDist2 =  Math.sqrt(eucDist2);
		return numerator/(eucDist1*eucDist2);
	}
}