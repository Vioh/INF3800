
package no.uio.ifi.lt.preprocessing;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import no.uio.ifi.lt.storage.Document;
import no.uio.ifi.lt.storage.IDocumentStore;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Helper class for reading an XML file with a top node <docs>..</docs> containing
 * a sequence of <doc>, each representing the text for an individual document.
 *
 * @author  plison
 */
public class XMLDocumentReader extends DocumentReader {


	/**
	 * Reads the XML file given as source, and populate the document store with
	 * the set of documents defined in it.  The method returns true if the read
	 * operation is successful, and false otherwise
	 *
	 * @param source the input source
	 * @param normalizer text normalizer
	 * @param documentStore document store to populate
	 * @param logger logger
	 * @return true if read operation successful, else false
	 */
	protected boolean readDocuments (InputStream source, INormalizer normalizer,
			IDocumentStore documentStore, Logger logger) {

		try {
			org.w3c.dom.Document xmldoc = getXMLDocument(source);

			int docCount = 0;

			for (int i = 0 ; i < xmldoc.getChildNodes().getLength(); i++) {
				Node topNode = xmldoc.getChildNodes().item(i);
				if (topNode.getNodeName().equals("docs")) {

					for (int j = 0 ; j < topNode.getChildNodes().getLength(); j++) {
						Node docNode = topNode.getChildNodes().item(j);

						if (docNode.getNodeName().equals("doc")) {
							
							// extract the content
							String docContent = docNode.getTextContent();

							// create the document
							Document document = createDocument(docContent, "", normalizer);

							// Store it!
							documentStore.putDocument(document);
							docCount++;
						}
					}
				}
			}

			// Make some noise?
			if (logger != null) {
				logger.info(String.format("Added %d documents to the document store.", docCount));
			}

			return true;

		} catch (ParserConfigurationException e) {
			if (logger != null) {
				logger.log(Level.SEVERE, "parse configuration error with XML file", e);
			}
			return false;
		} catch (SAXException e) {
			if (logger != null) {
				logger.log(Level.SEVERE, "SAX error with XML file", e);
			}
			return false;
		} catch (IOException e) {
			if (logger != null) {
				logger.log(Level.SEVERE, "i/o error with XML file", e);
			}
			return false;
		}
	}


	/**
	 * Opens the XML document referenced by the filename, and returns it
	 * 
	 * @param filename the input source
	 * @return the XML document
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static org.w3c.dom.Document getXMLDocument (InputStream source) 
	throws ParserConfigurationException, SAXException, IOException  {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(source);

	}



}
