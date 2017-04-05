package no.uio.ifi.lt.nbclassifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.tokenization.IToken;
import no.uio.ifi.lt.tokenization.ITokenizer;
import no.uio.ifi.lt.utils.HeapItem;
import no.uio.ifi.lt.utils.Sieve;

public class TestStore extends MessageStore{
	/**
	 * Indexes the test data, which is somewhat of an overkill. We never 
	 * even use the index, we really just need the documents, wrapped in 
	 * a {@link IDocumentStore} for our convenience. 
	 * All we want is to be able to iterate over the normalized test data 
	 * in the document store, where the normalization/tokenization should
	 * confine to the same parameters as the training data.
	 * @param testFolderName 
	 * @param logger
	 */
	public TestStore(String testFolderName, Logger logger) {
		super(testFolderName, logger);
	}

	/**
	 * Evaluates the entire test set, one document at the time, and prints the results.
	 * @param naiveBayesProbabilities the class containing all conditional probabilities P(W|C) for all
	 * words encountered in the training data. 
	 */
	public double evaluateTestSet(DocumentClassifier classifier) {
		double correctlyClassified = 0;
		double incorrectlyClassified = 0;
		
		IInvertedIndex[] indexes = super.getIndexes();
		for (int i = 0; i<indexes.length;i++) {
			IDocumentStore documentStore = indexes[i].getDocumentStore();
			for (int j = 0; j<documentStore.size();j++) {
				if (this.evaluateTestDocument(documentStore.getDocument(j),classifier,i) == true) {
					correctlyClassified++;
				}
				else {
					incorrectlyClassified++;
				}
			}
		}
		int totalNumber = (int) (correctlyClassified+incorrectlyClassified);
		double accuracy = correctlyClassified/totalNumber;
		System.out.println("CLASSIFIED "+totalNumber+" documents");
		System.out.println("CORRECTLY CLASSIFIED "+accuracy*100+" % of the documents"); 
		return accuracy;
	}

	/**
	 * Checks the accuracy of the Naive-bayes classifier of a particular document, 
	 * given its gold standard class.  The method returns true if the classification 
	 * is correct, and false otherwise. 
	 * @param document the test document to be classified according to class
	 * @param classifier  the naive-bayes classifier 
	 * @param correctClass the correct (gold standard) class for the document
	 * @return a boolean value, according the whether the document was classified correct or not.
	 */
	private boolean evaluateTestDocument(IDocument document, DocumentClassifier classifier, int correctClass) {
		String normalized = super.normalizer.normalize(document.getOriginalData());
		if (normalized.length() != document.getNormalizedLength()) {
			throw new IllegalStateException();
		}
		List<IToken> documentContent = new LinkedList<IToken>();
		Iterator<IToken> it = super.tokenizer.iterator(normalized);
		while (it.hasNext()) {
			documentContent.add(it.next());
		}
		int classifiedClass = classifier.classify(documentContent);
		if (correctClass == classifiedClass) {
			return true;
		}
		else {
			return false;
		}
	}
}