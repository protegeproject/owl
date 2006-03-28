package edu.stanford.smi.protegex.owl.ui.metadatatab;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.importstree.ImportsTreePanel;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourcePanel;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractTabWidget;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * The OWLMetadataTab is a tab in the OWL-Plugin.
 * It can be used for manipulating the ontology header of an OWL ontology
 * (e.g. versionInfo, priorVersion, backwardCompatibleWith, etc.), namespaces
 * or AllDifferent elements and its distinct members.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLMetadataTab extends AbstractTabWidget implements HostResourceDisplay {

    private ResourcePanel resourceDisplay;

    private ImportsTreePanel importsTreePanel;

    private SelectionListener treeSelectionListener;

    private JComponent createMainPanel() {

        treeSelectionListener = new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                Collection sel = importsTreePanel.getImportsTree().getSelectedResources();
                RDFResource res = (RDFResource) CollectionUtilities.getFirstItem(sel);
                resourceDisplay.setResource(res);
            }
        };

        importsTreePanel = new ImportsTreePanel(getOWLModel().getDefaultOWLOntology());
        importsTreePanel.getImportsTree().addSelectionListener(treeSelectionListener);

        resourceDisplay = ProtegeUI.getResourcePanelFactory().createResourcePanel(getOWLModel(), ResourcePanel.DEFAULT_TYPE_ONTOLOGY);


        OWLOntology activeOntology = importsTreePanel.getActiveOntology(getOWLModel());
        if (activeOntology != null) {
            displayHostResource(activeOntology);
        }

        JSplitPane mainSplitter = createLeftRightSplitPane("SlotsTab.left_right", 250);
        mainSplitter.setLeftComponent(importsTreePanel);
        mainSplitter.setRightComponent((Component) resourceDisplay);
        mainSplitter.setDividerLocation(250);

        return mainSplitter;
    }


    public void dispose() {
        super.dispose();
//        if (!isAncestorOf(resourceDisplay)) {
//            resourceDisplay.dispose();
//        }

        importsTreePanel.getImportsTree().removeSelectionListener(treeSelectionListener);

        resourceDisplay = null;
        importsTreePanel = null;
        treeSelectionListener = null;
    }


    public void initialize() {

        //setLabel("Metadata (" + );
        setIcon(OWLIcons.getImageIcon("Metadata"));
        JComponent comp = createMainPanel();
        add(comp);
    }

    public String getLabel() {
        String label = "Metadata";
        TripleStore ts = getOWLModel().getTripleStoreModel().getActiveTripleStore();
        OWLOntology ont = (OWLOntology) TripleStoreUtil.getFirstOntology(getOWLModel(), ts);
        if (ont != null) {
            String file = ont.getURI();
            int index = file.lastIndexOf('/');
            if (index < 0) {
                index = file.lastIndexOf('\\');
            }
            if (index >= 0) {
                file = file.substring(index + 1);
            }
            if (file.length() > 25) {
                int endIndex = file.length();
                file = file.substring(0, 10) + "..." +
                       file.substring(endIndex - 10, endIndex);
            }
            label += " (" + file + ")";
        }
        return label;
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
        displayHostResource(ontology);
    }


    public boolean displayHostResource(RDFResource resource) {
        return importsTreePanel.displayHostResource(resource);
    }
}
