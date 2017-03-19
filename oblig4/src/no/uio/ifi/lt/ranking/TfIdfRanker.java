package no.uio.ifi.lt.ranking;
import java.util.logging.Logger;
import no.uio.ifi.lt.indexing.Posting;
import no.uio.ifi.lt.indexing.PostingList;
import no.uio.ifi.lt.search.IQuery;
import no.uio.ifi.lt.storage.IDocument;
import no.uio.ifi.lt.tokenization.IToken;

public class TfIdfRanker implements IRanker {
	private Logger logger;
	private boolean debug;
	private double accumulatedResult;
	private final int N = 10000; // total number of documents hardcoded in

	public TfIdfRanker(Logger logger) {
		this.logger = logger;
		this.debug = false;
		this.reset();
	}
	
	/**
	 * Implements the {@link IRanker} interface.
	 */
	@Override
	public IRanker clone() {
		return new TfIdfRanker(this.logger);
	}
	
	@Override
	public void debug(boolean value) {
		this.debug = value;
	}

	@Override
	public double evaluate(IQuery query, IDocument document) {
		// TODO Auto-generated method stub
		return this.accumulatedResult;
	}

	@Override
	public void reset() {
		this.accumulatedResult = 0;
	}

	@Override
	public void update(IToken token, Posting posting, PostingList postingList) {			
		int tf = posting.getOccurrenceCount();
		int df = postingList.size();
		double idf = Math.log(N/df);
		this.accumulatedResult += (tf*idf);		
		
		// Log spam?
		if (this.debug && this.logger != null) {
			this.logger.finest(String.format("Token '%s' occurs across %d documents.", token.getValue(), postingList.size()));
			this.logger.finest(String.format("Token '%s' occurs %d times in the current document.", token.getValue(), posting.getOccurrenceCount()));
		}
	}
}