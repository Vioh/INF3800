package no.uio.ifi.lt.nbclassifier;
import java.util.List;
import no.uio.ifi.lt.tokenization.IToken;

public interface DocumentClassifier {
	public int classify (List<IToken> documentContent);
}