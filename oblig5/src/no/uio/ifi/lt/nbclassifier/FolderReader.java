package no.uio.ifi.lt.nbclassifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.indexing.InMemoryInvertedIndex;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.storage.Document;
import no.uio.ifi.lt.storage.IDocumentStore;
import no.uio.ifi.lt.storage.InMemoryDocumentStore;
import no.uio.ifi.lt.tokenization.ITokenizer;

public class FolderReader {
	/**
	 * Reads data from the given named folder, creates {@link Document} objects from this,
	 * and populates the given {@link InMemoryDocumentStore}.
	 * @param newsGroupName map of the folders to be indexed, where the document data if found
	 * @param separator optional separator for separating the searchable data from the extra meta data
	 * @param normalizer defines how the searchable data is to be normalized
	 * @param tokenizer 
	 * @param documentStore where we will insert the created {@link IDocument} objects
	 * @return an array of indexes, corresponding to the classes in the training/test folder
	 */
	public static IInvertedIndex[] getInvertedIndexes(Map<Integer,String> newsGroupName,
	                                                  INormalizer normalizer,
	                                                  ITokenizer tokenizer, Logger logger) {
		IInvertedIndex[] invertedIndex = new InMemoryInvertedIndex[newsGroupName.size()];
		IDocumentStore documentStore;
		for (int i = 0; i<newsGroupName.size();i++) {
			if (logger != null) {
				logger.info(String.format("Reading documents from '%s'...", newsGroupName.get(i)));
			}
			try {
				documentStore = getDocumentStore(newsGroupName.get(i), normalizer, logger);
				invertedIndex[i]  = new InMemoryInvertedIndex(documentStore, normalizer, tokenizer, logger);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		return invertedIndex;
	}

	public static String getDocumentAsLine(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line;
		String documentAsLine = null;
		int lineNumber = 0;
		
		/*
		 * HERE WOULD BE A GOOD PLACE TO SPEED UP INDEXING DURING TESTING!
		 * Use the lineNumber-restricted while-loop for faster indexing! 
		 * And actually, the accuracy would probably go up, too!
		 */
		while ((line = reader.readLine()) != null) {
		// while ((line = reader.readLine()) != null && lineNumber < 5) {
			lineNumber++;
			if (line.length() == 0 || line.matches("^\\s+$")) continue;
			else {
				if (documentAsLine != null) {
					documentAsLine = documentAsLine + line.trim()+" ";
				} else {
					documentAsLine = line.trim()+" ";
				}
			}
		}
		reader.close();
		return documentAsLine;
	}

	/**
	 * Reads data from the given source stream, creates {@link Document} objects from this,
	 * and populates the given {@link InMemoryDocumentStore}.
	 * @param source the input stream containing the document data
	 * @param separator optional separator for separating the searchable data from the extra meta data
	 * @param normalizer defines how the searchable data is to be normalized
	 * @param documentStore where we will insert the created {@link IDocument} objects
	 * @throws IOException 
	 */
	public static IDocumentStore getDocumentStore(String folderName, 
	                                              INormalizer normalizer, 
	                                              Logger logger) throws IOException {
		String separator = "\t";
		int insertCount = 0;
		String[] doc = new File(folderName).list();
		
		// One document per file.
		int numberOfDocuments = 0;
		IDocumentStore documentStore = new InMemoryDocumentStore();
		for (String d : doc) {
			numberOfDocuments++;
			String documentAsLine = getDocumentAsLine(folderName+"/"+d);
			if (documentAsLine == null) {
				continue;
			}
			String originalData = documentAsLine;
			String extraData = null;

			// Split it up?
			if (separator != null && separator.length() > 0) {
				int where = documentAsLine.indexOf(separator);
				if (where != -1) {
					originalData = documentAsLine.substring(0, where);
					extraData = documentAsLine.substring(where + separator.length());
				}
			}
			// How much normalized document data is there?
			String normalizedData = normalizer.normalize(originalData);

			// Create the document object.
			// TODO: Implement support for static rank.
			Document document = new Document();
			document.setOriginalData(originalData);
			document.setExtraData(extraData);
			document.setNormalizedLength(normalizedData.length());
			document.setStaticRank(0.0);

			// Store it!
			documentStore.putDocument(document);
			++insertCount;
		}
		return documentStore;
	}
}