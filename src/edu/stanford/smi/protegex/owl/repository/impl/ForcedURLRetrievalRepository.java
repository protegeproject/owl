package edu.stanford.smi.protegex.owl.repository.impl;

import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 21, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ForcedURLRetrievalRepository implements Repository {

    private URL url;

    private URI uri;

    private URI actualOntologyName;


    public ForcedURLRetrievalRepository(URL url) {
        this.url = url;
        this.uri = null;
        refresh();
    }


    public boolean contains(URI ontologyName) {
        if (uri != null) {
            return ontologyName.equals(uri);
        }
        return false;
    }


    public void refresh() {
        try {
            InputStream is = ProtegeOWLParser.getInputStream(url);
            OntologyNameExtractor extractor = new OntologyNameExtractor(is, url);
            if (extractor.isRDFRootElementPresent()) {
                actualOntologyName = extractor.getOntologyName();
                try {
                    uri = new URI(url.toString());
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            else {
                uri = null;
            }

        }
        catch (IOException e) {
            uri = null;
        }
    }


    public Collection getOntologies() {
        return Collections.singleton(uri);
    }


    public InputStream getInputStream(URI ontologyName)
            throws IOException {
        if (uri != null) {
            return ProtegeOWLParser.getInputStream(url);
        }
        else {
            return null;
        }
    }


    public boolean isWritable(URI ontologyName) {
        return false;
    }


    public OutputStream getOutputStream(URI ontologyName)
            throws IOException {
        return null;
    }


    public boolean isSystem() {
        return false;
    }


    public String getRepositoryDescription() {
        return "Forced HTTP retrieval";
    }


    public String getOntologyLocationDescription(URI ontologyName) {
        return url.toString() + " [actual ontology at this location: " + actualOntologyName + "]";
    }


    public String getRepositoryDescriptor() {
        return url.toString();
    }

}

