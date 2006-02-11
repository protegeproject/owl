package edu.stanford.smi.protegex.owl.repository.util;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.*;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 21, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class XMLBaseExtractor {

    private InputStream is;

    private URI xmlBase;

    private String rootElementName;

    private String defaultNamespace;


    public XMLBaseExtractor(InputStream is) {
        this.is = is;
        this.xmlBase = null;
    }


    public URI getXMLBase() {
        SAXParser parser = new SAXParser();
        parser.setContentHandler(new MyHandler());
        try {
            parser.parse(new InputSource(is));
        }
        finally {
            return xmlBase;
        }
    }


    public String getRootElementName() {
        return rootElementName;
    }


    public String getDefaultNamespace() {
        return defaultNamespace;
    }


    private class MyHandler implements ContentHandler {

        private boolean startElement;


        public void setDocumentLocator(Locator locator) {
        }


        public void startDocument()
                throws SAXException {
        }


        public void endDocument()
                throws SAXException {
        }


        public void startPrefixMapping(String prefix,
                                       String uri)
                throws SAXException {
        }


        public void endPrefixMapping(String prefix)
                throws SAXException {
        }


        public void startElement(String namespaceURI,
                                 String localName,
                                 String qName,
                                 Attributes atts)
                throws SAXException {
            if (startElement == false) {
                rootElementName = qName;
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getQName(i).equals("xml:base")) {
                        URI attURL = null;
                        try {
                            attURL = new URI(atts.getValue(i));
                        }
                        catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        xmlBase = attURL;
                    }
                    else if (atts.getLocalName(i).equals("xmlns")) {
                        defaultNamespace = atts.getValue(i);
                    }
                }
                startElement = true;
            }
            else {
                throw new SAXException("No xml:base");
            }
        }


        public void endElement(String namespaceURI,
                               String localName,
                               String qName)
                throws SAXException {
        }


        public void characters(char ch[],
                               int start,
                               int length)
                throws SAXException {
        }


        public void ignorableWhitespace(char ch[],
                                        int start,
                                        int length)
                throws SAXException {
        }


        public void processingInstruction(String target,
                                          String data)
                throws SAXException {
        }


        public void skippedEntity(String name)
                throws SAXException {
        }
    }
}

