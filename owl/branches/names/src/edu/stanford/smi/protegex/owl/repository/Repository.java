package edu.stanford.smi.protegex.owl.repository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;

import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 12, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface Repository {

    /**
     * Determines if the repository contains the
     * specified ontology.
     *
     * @param ontologyName The name of the ontology.
     */
    public boolean contains(URI ontologyName);


    /**
     * Causes the repository to contain the latest
     * information.
     */
    public void refresh();


    /**
     * Gets the name of the ontologies that this
     * repository contains.
     *
     * @return A <code>Collection</code> containing
     *         <code>URI</code>s.
     */
    public Collection<URI> getOntologies();





    /**
     * Determines if the specified ontology is writable.
     *
     * @param ontologyName
     * @return <code>true</code> if the ontology is writable,
     *         or <code>false</code> if the ontology is not writable.
     */
    public boolean isWritable(URI ontologyName);





    /**
     * Determines if the ontology repository is a system repository.
     */
    public boolean isSystem();


    /**
     * Gets a description of the type of the repository. For example,
     * "Local folder".
     */
    public String getRepositoryDescription();


    /**
     * Gets a description of where the specified ontology is located.
     *
     * @param ontologyName The name of the ontology.
     * @return A <code>String</code> description of the location of
     *         the ontology, for example, "file:/Users/Blah/Blah/Blah".  This
     *         return value will be an empty <code>String</code> if the repository
     *         does not contain the specified ontology.
     */
    public String getOntologyLocationDescription(URI ontologyName);


    /**
     * Gets the descriptor for this repository.  This is used to
     * serialise the repository in a list of the available repositories.
     */
    public String getRepositoryDescriptor();
    
    /**
     * 
     * 
     * @param owlModel the model to add the import to
     * @param ontologyName the ontology name to use to look up the imported ontology.
     */
    public void addImport(OWLModel owlModel, URI ontologyName) throws IOException;
    
    /*
     * TODO the following method should probably be refactored to AbstractStreamBasedRepositoryImpl and replaced 
     *      with something like a save of just the single triple store.  But for the time being any repository that is not
     *      io based does not need a save.  Currently Protege2Jena is written to loop through and save everything
     *      but it should not be too hard to break the loops.
     */

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
    
}
