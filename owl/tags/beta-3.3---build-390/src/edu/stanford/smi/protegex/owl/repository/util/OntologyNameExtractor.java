package edu.stanford.smi.protegex.owl.repository.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 12, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A utility class that extracts the name (URI) of
 * an ontology from an OWL file.  The name of an ontology is
 * the xml:base URI.
 */
public class OntologyNameExtractor {

    private InputStream is;

    private URI uri;

    private URL url;

    private boolean rdfRootPresent;


    /**
     * Constructs an <code>OntologyNameExtractor</code> that
     * should obtain the name of an ontology which can be
     * read via the specified input stream.
     *
     * @param is The input stream from which the ontology
     *           can be read.
     */
    public OntologyNameExtractor(InputStream is, URL documentURL) {
        this.is = is;
        this.url = documentURL;
        uri = null;
        rdfRootPresent = false;
        init();
    }


    private void init() {
        if (is != null) {
            try {
                XMLBaseExtractor extractor = new XMLBaseExtractor(is);

                uri = extractor.getXMLBase();
                String rootElementName = extractor.getRootElementName();
                if (rootElementName != null) {
                    rdfRootPresent = rootElementName.toLowerCase().equals("rdf:rdf");
                }
                else {
                    uri = null;
                }
            }
            finally {
                is = null;
            }
        }
    }


    public boolean isRDFRootElementPresent() {
        return rdfRootPresent;
    }


    public URI getOntologyName() throws IOException {
        return uri;
    }
}

