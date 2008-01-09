package edu.stanford.smi.protegex.owl.model.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
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

    private Collection ontologyURIs;


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
        ontologyURIs = new ArrayList();
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
     * Imports the ontologies that this helper was asked to import.  If the
     * Protege-OWL Application GUI is being used, then the UI is reloaded.
     * Use the alternative <code>reloadGUI(boolean)</code> in TabWidget
     * initialisation code to prevent the GUI being reloaded
     */
    public void importOntologies() throws Exception {
        importOntologies(true);
    }


    /**
     * Alternative method that allows the caller to block the GUI
     * reload (for example when initialising TabPlugins)
     * @param reloadGUI - false if no GUI reload is desired
     */
    public void importOntologies(boolean reloadGUI) throws Exception {
        ArrayList importedOntologies = new ArrayList();
        for(Iterator it = ontologyURIs.iterator(); it.hasNext();) {
            URI ontologyURI = (URI) it.next();
            if(owlModel.getAllImports().contains(ontologyURI.toString()) == false) {
                owlModel.addImport(ontologyURI);
                // Add the imported URI
                for(Iterator ontIt = owlModel.getOWLOntologies().iterator(); ontIt.hasNext();) {
                    OWLOntology owlOntology = (OWLOntology) ontIt.next();
                    final TripleStore ts = owlModel.getTripleStoreModel().getActiveTripleStore();
                    if(owlModel.getTripleStoreModel().getHomeTripleStore(owlOntology) == ts) {
                        owlOntology.addImports(ontologyURI);
                        break;
                    }
                }
                importedOntologies.add(ontologyURI);
            }
        }
        
        if(importedOntologies.size() > 0 && OWLUtil.runsWithGUI(owlModel)) {
            owlModel.getTripleStoreModel().updateEditableResourceState();
            OWLMenuProjectPlugin.makeHiddenClsesWithSubclassesVisible(owlModel);
            if (reloadGUI){
                ProtegeUI.reloadUI(owlModel);
            }
        }
    }
}

