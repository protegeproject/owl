package edu.stanford.smi.protegex.owl.ui.importstree;

import edu.stanford.smi.protege.resource.Colors;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.swrl.ui.SWRLProjectPlugin;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.CreateImportedTripleStorePanel;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.emptyimport.EmptyImportWizard;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard.AddedPrefixesTable;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard.ImportEntry;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.wizard.ImportWizard;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ImportsTreePanel extends JPanel implements HostResourceDisplay, Disposable {

    private ImportsTree tree;

    private OWLOntology rootOntology;
    
    private AllowableAction createAddImportAction;

	private AllowableAction addEmptyImportAction;

	private AllowableAction removeImportAction;
	
	private AllowableAction setActiveOntologyAction;

	private Action downloadImportsAction;
    

    public ImportsTreePanel(OWLOntology rootOntology) {
        this.rootOntology = rootOntology;
        tree = new ImportsTree(rootOntology);
        OWLLabeledComponent lc = new OWLLabeledComponent("Ontologies", new JScrollPane(tree));
        
        createAddImportAction = createAddImportAction(); 
        lc.addHeaderButton(createAddImportAction);
        
        addEmptyImportAction = createAddEmptyImportAction(); 
        lc.addHeaderButton(addEmptyImportAction);
        
        removeImportAction = createRemoveImportAction(); 
        lc.addHeaderButton(removeImportAction);
        
        setActiveOntologyAction = createSetActiveOntologyAction();
        lc.addHeaderButton(setActiveOntologyAction);
        
        downloadImportsAction = new DownloadImportsAction(tree);
        lc.addHeaderButton(downloadImportsAction);

        setLayout(new BorderLayout());

        add(BorderLayout.NORTH, createOntologyBrowserHeader());
        add(BorderLayout.CENTER, lc);

        setPreferredSize(new Dimension(250, 300));
    }

    private AllowableAction createAddEmptyImportAction() {
        return new AllowableAction(CreateImportedTripleStorePanel.TITLE,
                                   OWLIcons.getCreateIcon(OWLIcons.IMPORT),
                                   tree) {
            public void actionPerformed(ActionEvent e) {
                createImportedTripleStore();
            }

            public void onSelectionChange() {
                Collection sel = getSelection();
                RDFResource ont = (RDFResource) CollectionUtilities.getFirstItem(sel);
                setAllowed(ont != null &&
                           ont instanceof OWLOntology &&
                           ont.equals(OWLUtil.getActiveOntology(ont.getOWLModel())) && 
                           ImportsTreePanel.this.isEnabled());
            }
        };
    }

    private AllowableAction createAddImportAction() {
        return new AllowableAction("Import ontology...",
                                   OWLIcons.getAddIcon(OWLIcons.IMPORT),
                                   tree) {
            public void actionPerformed(ActionEvent e) {
                addImport();
            }

            public void onSelectionChange() {
                Collection sel = getSelection();
                RDFResource ont = (RDFResource) CollectionUtilities.getFirstItem(sel);         
               
                setAllowed(ont != null &&
                           ont instanceof OWLOntology &&
                           ont.equals(OWLUtil.getActiveOntology(ont.getOWLModel())) && 
                           ImportsTreePanel.this.isEnabled());
            }
        };
    }

    private AllowableAction createRemoveImportAction() {
    	return new AllowableAction("Remove import",
    			OWLIcons.getRemoveIcon(OWLIcons.IMPORT),
    			tree) {
    		public void actionPerformed(ActionEvent e) {
    			removeSelectedImport();
    		}

    		public void onSelectionChange() {

    			setAllowed(tree.getSelectionCount() == 1 &&
    					tree.getSelectionRows()[0] != 0 &&
    					ImportsTreePanel.this.isEnabled());    			
    		}
    	};
    }

    private AllowableAction createSetActiveOntologyAction() {
        return new AllowableAction("Set active ontology",
                                   OWLIcons.getImageIcon(OWLIcons.SELECT_ACTIVE_TRIPLESTORE),
                                   tree) {
            public void actionPerformed(ActionEvent e) {
                setActiveOntology();
            }

            public void onSelectionChange() {
                Collection sel = getSelection();
                RDFResource ont = (RDFResource) CollectionUtilities.getFirstItem(sel);
                
                setAllowed(sel.size() == 1 &&
                           ont instanceof DefaultOWLOntology &&
                           !ont.equals(OWLUtil.getActiveOntology(ont.getOWLModel())) &&
                           ((DefaultOWLOntology) ont).isAssociatedTriplestoreEditable() &&
                           ImportsTreePanel.this.isEnabled());
            }
        };
    }

    protected HeaderComponent createOntologyBrowserHeader() {
        String projName = ProjectManager.getProjectManager().getCurrentProject().getName();
        JLabel label = ComponentFactory.createLabel(projName,
                                                    Icons.getProjectIcon(),
                                                    SwingConstants.LEFT);
        String forProject = LocalizedText.getText(ResourceKey.CLASS_BROWSER_FOR_PROJECT_LABEL);
        String classBrowser = "Ontology Browser";
        HeaderComponent headerComponent = new HeaderComponent(classBrowser, forProject, label);
        headerComponent.setColor(Colors.getFacetColor());
        return headerComponent;
    }


    public static void showDialog(OWLModel owlModel) {
        OWLOntology ontology = owlModel.getDefaultOWLOntology();
        ImportsTreePanel panel = new ImportsTreePanel(ontology);
        Component parent = ProtegeUI.getTopLevelContainer(owlModel.getProject());
        ProtegeUI.getModalDialogFactory().showDialog(parent, panel,
                                                     "owl:imports Between Ontologies",
                                                     ModalDialogFactory.MODE_CLOSE);
    }


    public boolean displayHostResource(RDFResource resource) {
        return tree.displayHostResource(resource);
    }


    public ImportsTree getImportsTree() {
        return tree;
    }

    private void addImport() {
        try {
            OWLModel owlModel = rootOntology.getOWLModel();
            ImportWizard wizard = new ImportWizard(this, owlModel);
            if (wizard.execute() == Wizard.RESULT_FINISH) {
                ImportHelper importHelper = new ImportHelper((JenaOWLModel) owlModel);
                Collection prefixes = new ArrayList(owlModel.getNamespaceManager().getPrefixes());
                
                for (Iterator it = wizard.getImportData().getImportEntries().iterator(); it.hasNext();) {
                    ImportEntry importEntry = (ImportEntry) it.next();
                    Repository rep = importEntry.getRepositoryToAdd();
                    if (rep != null) {
                        owlModel.getRepositoryManager().addProjectRepository(importEntry.getRepository());
                    }
                    URI ontologyURI = importEntry.getOntologyURI();
                    importHelper.addImport(ontologyURI);
                }
                importHelper.importOntologies();
                
                //TODO: This should be moved in the import code
                owlModel.getNamespaceManager().setPrefix(ProtegeNames.NS, ProtegeNames.PROTEGE_PREFIX);
                
                Collection addedPrefixes = new ArrayList(owlModel.getNamespaceManager().getPrefixes());
                addedPrefixes.removeAll(prefixes);
                if (addedPrefixes.size() > 0) {
                    Component topLevelContainer = ProtegeUI.getTopLevelContainer(owlModel.getProject());
                    AddedPrefixesTable.showDialog(topLevelContainer, owlModel, addedPrefixes);
                }
                
            	if (SWRLProjectPlugin.isSWRLImported(owlModel)) {
            		SWRLProjectPlugin.adjustGUI(owlModel.getProject());
            	}
            	
            	ProtegeUI.reloadUI(owlModel);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeSelectedImport() {
        RDFResource sel = (RDFResource) CollectionUtilities.getFirstItem(tree.getSelectedResources());
        if (sel != null) {
            if (OWLUtil.confirmSaveAndReload(rootOntology.getProject())) {
                String uri = sel.getURI();
                TreePath path = tree.getSelectionModel().getSelectionPath();
                ImportsTreeNode parentNode = (ImportsTreeNode) path.getParentPath().getLastPathComponent();
                OWLOntology importingOntology = (OWLOntology) parentNode.getUserObject();
                importingOntology.removeImports(uri);
                OWLUtil.saveAndReloadProject();
            }
        }
    }

    private void createImportedTripleStore() {
        EmptyImportWizard wizard = new EmptyImportWizard(this,
                                                         rootOntology.getOWLModel());
        wizard.execute();
    }

    private void setActiveOntology() {
        Collection sel = tree.getSelection();
        OWLOntology ont = (OWLOntology) CollectionUtilities.getFirstItem(sel);
        OWLModel owlModel = ont.getOWLModel();

        if (ont != OWLUtil.getActiveOntology(owlModel)) {
            TripleStoreModel tsm = owlModel.getTripleStoreModel();
            TripleStore tripleStore = tsm.getHomeTripleStore(ont);
            TripleStoreUtil.switchTripleStore(owlModel, tripleStore);
        }
    }

    public void dispose() {
        tree.dispose();
    }
    
    public void setEnabled(boolean enabled) {
    	createAddImportAction.setAllowed(enabled);
    	removeImportAction.setAllowed(enabled);
    	addEmptyImportAction.setAllowed(enabled);
    	setActiveOntologyAction.setAllowed(enabled);
    	downloadImportsAction.setEnabled(enabled);    	
    	super.setEnabled(enabled);
    };
}
