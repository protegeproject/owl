package edu.stanford.smi.protegex.owl.model.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.framestore.InMemoryFrameDb;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.menu.OWLMenuProjectPlugin;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportHelper {

    private JenaOWLModel owlModel;

    private Collection<URI> ontologyURIs;
    
    private Collection<InputStream> ontologyStreams;


    /**
     * Creates an import helper that will import ontologies into the
     * specified <code>OWLModel</code>.  The pattern of usage is:
     * <ol>
     * <li>Create an instance specifying the <code>OWLModel</code> to be used
     * in the constructor</li>
     * <li>Add the ontology URIs of the ontologies to be imported using the
     * <code>addImport</code> method.</li>
     * <li>Finally call the <code>importOntologies</code> method, which will
     * do the actual importing (and reloading of the UI if necessary).</li>
     * </ol>
     *
     *
     */
    public ImportHelper(JenaOWLModel owlModel) {
        this.owlModel = owlModel;
        ontologyURIs = new ArrayList<URI>();
        ontologyStreams = new ArrayList<InputStream>();
    }


    /**
     * Adds an ontology to the set of ontologies that will be imported.  Note that
     * this does not actually import the ontologies - it merely adds them to the list
     * of ontologies to be imported.  To perform the actual imports, use the
     * <code>importOntologies()</code> method.
     *
     * @param ontologyURI The URI of the ontology to import
     */
    public void addImport(URI ontologyURI) {
        ontologyURIs.add(ontologyURI);
    }
    
    /**
     * Adds the ontology in an input stream to the set of ontologies that will be imported.
     * Note that this does not actually import the ontology - it merely adds it to the list of
     * ontologies to be imported.  To perform the actual imports use the <code>importOntologes()</code>
     * method.
     */
    public void addImport(InputStream is) {
        ontologyStreams.add(is);
    }


    /**
     * Imports the ontologies that this helper was asked to import.  If the
     * Protege-OWL Application GUI is being used, then the UI is reloaded.
     * Use the alternative <code>reloadGUI(boolean)</code> in TabWidget
     * initialisation code to prevent the GUI being reloaded
     */
    public void importOntologies() throws IOException {
        importOntologies(true);
    }


    /**
     * Alternative method that allows the caller to block the GUI
     * reload (for example when initialising TabPlugins)
     * @param reloadGUI - false if no GUI reload is desired
     */
    public void importOntologies(boolean reloadGUI) throws IOException {
        ArrayList<URI> importedOntologies = new ArrayList<URI>();
        for (InputStream is : ontologyStreams) {
            URI ontologyURI = loadImportedAssertions(is);
            is.close();
            if (ontologyURI != null) {
                importedOntologies.add(ontologyURI);
            }
        }
        for(URI ontologyURI : ontologyURIs) {
            if (owlModel.getAllImports().contains(ontologyURI.toString()) == false &&
                    !importedOntologies.contains(ontologyURI)) {
                URI realOntologyUri = owlModel.loadImportedAssertions(ontologyURI);
                if (realOntologyUri != null) {
                    importedOntologies.add(realOntologyUri);
                }
            }
        }
        ProtegeOWLParser.doFinalPostProcessing(owlModel);
        OWLOntology  importingOntology = owlModel.getTripleStoreModel().getActiveTripleStore().getOWLOntology();
        for (URI importedOntologyURI : importedOntologies) {
            importingOntology.addImports(importedOntologyURI);
        }
        if(importedOntologies.size() > 0 && OWLUtil.runsWithGUI(owlModel)) {
            owlModel.getTripleStoreModel().updateEditableResourceState();
            OWLMenuProjectPlugin.makeHiddenClsesWithSubclassesVisible(owlModel);
            if (reloadGUI){
                ProtegeUI.reloadUI(owlModel);
            }
        }
    }
    
    private URI loadImportedAssertions(InputStream is) throws IOException {
        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);
        parser.setImporting(true);
        TripleStore importedTripleStore = null;
        TripleStore importingTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
        try {
            NarrowFrameStore frameStore = new InMemoryFrameDb("**temp_name**");
            importedTripleStore = owlModel.getTripleStoreModel().createActiveImportedTripleStore(frameStore);
            parser.run(is, null);
            String ontologyName = importedTripleStore.getName();
            return new URI(ontologyName);
        }
        catch (URISyntaxException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        } finally {
            owlModel.getTripleStoreModel().setActiveTripleStore(importingTripleStore);
        }
    }
}

