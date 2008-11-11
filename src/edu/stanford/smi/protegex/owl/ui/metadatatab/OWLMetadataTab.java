package edu.stanford.smi.protegex.owl.ui.metadatatab;

import java.awt.Component;
import java.util.Collection;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.server.metaproject.impl.OperationImpl;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.importstree.ImportsTreePanel;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourcePanel;
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

    private ResourcePanel resourceDisplay;

    private ImportsTreePanel importsTreePanel;

    private SelectionListener treeSelectionListener;

    private FrameListener frameListener;



    private JComponent createMainPanel() {

        treeSelectionListener = new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                Collection sel = importsTreePanel.getImportsTree().getSelectedResources();
                RDFResource res = (RDFResource) CollectionUtilities.getFirstItem(sel);
                resourceDisplay.setResource(res);
                ((ResourceDisplay)resourceDisplay).setEnabled(OWLMetadataTab.this.isEnabled());
            }
        };

        importsTreePanel = new ImportsTreePanel(getOWLModel().getDefaultOWLOntology());
        importsTreePanel.getImportsTree().addSelectionListener(treeSelectionListener);

        resourceDisplay = ProtegeUI.getResourcePanelFactory().createResourcePanel(getOWLModel(), ResourcePanel.DEFAULT_TYPE_ONTOLOGY);


        OWLOntology activeOntology = OWLUtil.getActiveOntology(getOWLModel());
        if (activeOntology != null) {
            displayHostResource(activeOntology);
        }

        JSplitPane mainSplitter = createLeftRightSplitPane("SlotsTab.left_right", 250);
        mainSplitter.setLeftComponent(importsTreePanel);
        mainSplitter.setRightComponent((Component) resourceDisplay);
        mainSplitter.setDividerLocation(250);

        return mainSplitter;
    }


    @Override
	public void dispose() {
        super.dispose();

        importsTreePanel.getImportsTree().removeSelectionListener(treeSelectionListener);
        treeSelectionListener = null;

        try {
			removeFrameListner();
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING, "Error at removing frame listener from " + getOWLModel().getDefaultOWLOntology(), e);
		}

		importsTreePanel.dispose();
		resourceDisplay.dispose();
    }


    public void initialize() {

        setIcon(OWLIcons.getImageIcon("Metadata"));

        JComponent comp = createMainPanel();
        add(comp);
        setClsTree(importsTreePanel.getImportsTree());
        setEnabled(true);

        /* It's important that the frame listener is added
         * after the imports tree is initialized, because
         * the order of attaching listeners is important.
         * We want first the imports tree to get the event that
         * the top ontology name has changed and then the tab should
         * get the event.
         */
        addFrameListener();
    }

    @SuppressWarnings("deprecation")
	protected void addFrameListener() {
    	frameListener = new FrameAdapter() {
    		@Override
    		public void frameReplaced(FrameEvent event) {
    			setLabel(getLabel());

    			try {
    				JTabbedPane tabbedPane = ProjectManager.getProjectManager().getCurrentProjectView().getTabbedPane();
    				int index = tabbedPane.indexOfComponent(OWLMetadataTab.this);
    				tabbedPane.setTitleAt(index, getLabel());
				} catch (Exception e) {
					//it's fine to do nothing
					Log.emptyCatchBlock(e);
				}

    			repaint();
    		}
    	};

    	getOWLModel().getDefaultOWLOntology().addFrameListener(frameListener);
	}

    protected void removeFrameListner() {
    	if (frameListener != null) {
    		getOWLModel().getDefaultOWLOntology().removeFrameListener(frameListener);
    	}
	}

	@Override
	public String getLabel() {
	    // Tania can change it back?
	    // OWLOntology displayedOntology = getOWLModel().getDefaultOWLOntology();
	    OWLOntology displayedOntology =  OWLUtil.getActiveOntology(getOWLModel());
        return "Metadata" + "(" + NamespaceUtil.getLocalName(displayedOntology.getName()) + ")";
    }

    public static boolean isSuitable(Project p, Collection errors) {
        return OWLClassesTab.isSuitable(p, errors);
    }


    /**
     * @see #setOntology
     * @deprecated
     */
    @Deprecated
	public void setOntologyInstance(OWLOntology oi) {
        setOntology(oi);
    }


    public void setOntology(OWLOntology ontology) {
        displayHostResource(ontology);
    }


    public boolean displayHostResource(RDFResource resource) {
        return importsTreePanel.displayHostResource(resource);
    }

    @Override
	public void setEnabled(boolean enabled) {
    	enabled = enabled && RemoteClientFrameStore.isOperationAllowed(getOWLModel(), OperationImpl.ONTOLOGY_TAB_WRITE);
    	((ResourceDisplay)resourceDisplay).setEnabled(enabled);
    	importsTreePanel.setEnabled(enabled);
    	super.setEnabled(enabled);
    };


}
