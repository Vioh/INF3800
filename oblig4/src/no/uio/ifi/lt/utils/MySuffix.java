package no.uio.ifi.lt.utils;
import no.uio.ifi.lt.storage.IDocumentStore;

public class MySuffix implements Comparable<Object> {	
	int docId;
	int startIndex;
	IDocumentStore store;
	
	public MySuffix(int docId, int startIndex, IDocumentStore store) {
		this.docId = docId;
		this.startIndex = startIndex;
		this.store = store;
	}
	
	public String getString() {
		return store.getDocument(docId).getOriginalData().substring(startIndex);
	}

	public int compareTo(Object arg0) {
		if (arg0 instanceof MySuffix) {
			return getString().compareTo(((MySuffix)arg0).getString());
		}
		else if (arg0 instanceof String) {
			return getString().compareTo((String)arg0);
		}
		return 0;
	}

	public int getDocId() {
		return docId;
	}
	
	public int getStartIndex() {
		return startIndex;
	}

	public IDocumentStore getDictionary() {
		return store;
	}
}