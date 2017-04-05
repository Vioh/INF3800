package no.uio.ifi.lt.nbclassifier;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.IInvertedIndex;
import no.uio.ifi.lt.preprocessing.BrainDeadNormalizer;
import no.uio.ifi.lt.preprocessing.INormalizer;
import no.uio.ifi.lt.tokenization.BrainDeadTokenizer;
import no.uio.ifi.lt.tokenization.ITokenizer;

/**
 * Indexes the data in train/test folders. Each class/folder will have one index. 
 * This index can be used to retrieve the relevant data needed for Naive Bayes 
 * classification. Further, the folder names, corresponding to the class of the 
 * training data, is stored in {@link #newsGroupsName}, and can thus be retrieved.
 */
public class MessageStore {
	/** Defines where we emit messages, if at all. */
	@SuppressWarnings("unused")
	private Logger logger;
	
	/** Defines how we normalize queries and documents. */
	protected INormalizer normalizer;
	
	/** Defines how queries and documents are split into "words". */
	protected ITokenizer tokenizer;
	
	/* 
	 * The array of indexes, where one index is extracted from 
	 * one of the classes in the training/test data.
	 */
	private IInvertedIndex[] invertedIndex;

	/*
	 * Map from the folder name in the file system, to the corresponding 
	 * position index in the {@link #invertedIndex} array
	 */
	private Map<Integer, String> newsGroupsName;

	/**
	 * @param filename
	 * @param logger
	 */
	public MessageStore(String filename, Logger logger) {
		INormalizer normalizer = new BrainDeadNormalizer();
		ITokenizer tokenizer = new BrainDeadTokenizer();

		// Sets the map
		Map<Integer, String> newsGroupName = getFolderMap(filename);
		
		// Builds all the indexes found in the folders
		IInvertedIndex[] invertedIndex = 
				FolderReader.getInvertedIndexes(newsGroupName, normalizer, tokenizer, logger);
		create(logger, normalizer, tokenizer, invertedIndex, newsGroupName);
	}

	/**
	 * @param folderName
	 * @return a map of the folders in the data structure, 
	 */
	private Map<Integer, String> getFolderMap(String folderName) {
		HashMap<Integer,String> newsGroupName = new HashMap<Integer,String>();
		String[] children = new File(folderName).list();
		if (children == null) {
			// Either dir does not exist or is not a directory
		} else {
			for (int i=0; i<children.length; i++) {
				// Get filename of file or directory
				Arrays.sort(children);
				newsGroupName.put(newsGroupName.size(), folderName+"/"+children[i]);
			}
		}
		return newsGroupName;
	}

	private void create(Logger logger, INormalizer normalizer,
		                ITokenizer tokenizer,IInvertedIndex[] invertedIndex, 
		                Map<Integer,String> newsGroups) {
		this.logger = logger;
		this.normalizer = normalizer;
		this.tokenizer = tokenizer;
		this.invertedIndex = invertedIndex;
		this.newsGroupsName = newsGroups;
	}
	
	/**
	 * @return mapping form folder name to position index in the {@link #invertedIndex} array
	 */
	public Map<Integer,String> getNewsGroupsMap() {
		return this.newsGroupsName;
	}
	
	/**
	 * @return the indexes, where each index corresponds to a class. 
	 */
	public IInvertedIndex[] getIndexes() {
		return this.invertedIndex;
	}
}