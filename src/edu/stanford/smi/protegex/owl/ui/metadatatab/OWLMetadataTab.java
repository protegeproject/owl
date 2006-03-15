package edu.stanford.smi.protegex.owl.ui.metadatatab;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComponent;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.InstanceDisplay;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractTabWidget;

/**
 * The OWLMetadataTab is a tab in the OWL-Plugin.
 * It can be used for manipulating the ontology header of an OWL ontology
 * (e.g. versionInfo, priorVersion, backwardCompatibleWith, etc.), namespaces
 * or AllDifferent elements and its distinct members.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLMetadataTab extends AbstractTabWidget implements HostResourceDisplay {

    private InstanceDisplay instanceDisplay;


    private JComponent createMainPanel() {
        instanceDisplay = new InstanceDisplay(getProject(), false, false);
	    OWLOntology owlOntology = getOWLModel().getDefaultOWLOntology();
	    for(Iterator it = getOWLModel().getOWLOntologies().iterator(); it.hasNext(); ) {
		    OWLOntology curOnt = (OWLOntology) it.next();
		    TripleStoreModel tsm = getOWLModel().getTripleStoreModel();
		    TripleStore activeTripleStore = tsm.getActiveTripleStore();
		    if(activeTripleStore.contains(curOnt, 
                                                   getOWLModel().getRDFTypeProperty(), 
                                                   getOWLModel().getOWLOntologyClass())) {
			    owlOntology = curOnt;
			    break;
		    }
	    }
	    if(owlOntology != null) {
		    instanceDisplay.setInstance(owlOntology);
	    }
	    return instanceDisplay;
    }


    public boolean displayHostResource(RDFResource resource) {
        if (resource instanceof OWLOntology) {
            return true;
        }
        else if (resource instanceof OWLAllDifferent) {
            return true; // TODO: Show AllDifferent
        }
        else {
            return false;
        }
    }


    public void dispose() {
        super.dispose();
        if (!isAncestorOf(instanceDisplay)) {
            instanceDisplay.dispose();
        }
    }


    public void initialize() {
        setLabel("Metadata");
        setIcon(OWLIcons.getImageIcon("Metadata"));
        JComponent comp = createMainPanel();
        add(comp);
    }


    public static boolean isSuitable(Project p, Collection errors) {
        return OWLClassesTab.isSuitable(p, errors);
    }


    /**
     * @see #setOntology
     * @deprecated
     */
    public void setOntologyInstance(OWLOntology oi) {
        setOntology(oi);
    }


    public void setOntology(OWLOntology ontology) {
        instanceDisplay.setInstance(ontology);
    }
}
