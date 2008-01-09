package edu.stanford.smi.protegex.owl.repository.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.util.XMLBaseExtractor;

public abstract class AbstractStreamBasedRepositoryImpl implements Repository {
    /**
     * Gets an inputstream to read the specified ontology
     * from
     *
     * @param ontologyName The name of the ontology.
     * @return an <code>InputStream</code> to read the ontology
     *         from, or <code>null</code> if the repository does not
     *         contain the ontology or the ontology cannot be retrieved.
     */
    public abstract InputStream getInputStream(URI ontologyName) throws IOException;
    
    /**
     * Gets an output stream which can be used to write
     * changes to the specified ontology.
     *
     * @param ontologyName The ontology to be written
     * @return an <code>OutputStream</code> that can be used to
     *         write changes to the ontology, or <code>null</code> if the
     *         ontology is not writable.
     */
    public abstract OutputStream getOutputStream(URI ontologyName) throws IOException;
    
    public void addImport(OWLModel owlModel, URI ontologyName) throws IOException {
        TripleStore importingTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
        TripleStore importedTripleStore = owlModel.getTripleStoreModel().createTripleStore(ontologyName.toString());
        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel, true);
        parser.setImporting(true);
        InputStream is = getInputStream(ontologyName);
        URI xmlBase = XMLBaseExtractor.getXMLBase(getInputStream(ontologyName));
        
        try {
            owlModel.getTripleStoreModel().setActiveTripleStore(importedTripleStore);
            parser.loadTriples(ontologyName.toString(), xmlBase, is);
        } finally {
            owlModel.getTripleStoreModel().setActiveTripleStore(importingTripleStore);
        }
        owlModel.getOWLFrameStore().copyFacetValuesIntoNamedClses();
    }

}
