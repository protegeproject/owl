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
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.repository.Repository;
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

    public ImportsTreePanel(OWLOntology rootOntology) {
        this.rootOntology = rootOntology;
        tree = new ImportsTree(rootOntology);
        OWLLabeledComponent lc = new OWLLabeledComponent("Ontologies",
                                                         new JScrollPane(tree));
        lc.addHeaderButton(createAddImportAction());
        lc.addHeaderButton(createAddEmptyImportAction());
        lc.addHeaderButton(createRemoveImportAction());
        lc.addHeaderButton(createSetActiveOntologyAction());

        lc.addHeaderButton(new DownloadImportsAction(tree));

        setLayout(new BorderLayout());

        add(BorderLayout.NORTH, createOntologyBrowserHeader());
        add(BorderLayout.CENTER, lc);

        setPreferredSize(new Dimension(250, 300));
    }

    private Action createAddEmptyImportAction() {
        return new AllowableAction(CreateImportedTripleStorePanel.TITLE,
                                   OWLIcons.getCreateIcon(OWLIcons.IMPORT),
                                   tree) {
            public void actionPerformed(ActionEvent e) {
                createImportedTripleStore();
            }

            public void onSelectionChange() {
                Collection sel = getSelection();
                DefaultOWLOntology ont = (DefaultOWLOntology) CollectionUtilities.getFirstItem(sel);
                setAllowed(ont == getActiveOntology(ont.getOWLModel()));
            }
        };
    }

    private Action createAddImportAction() {
        return new AllowableAction("Import ontology...",
                                   OWLIcons.getAddIcon(OWLIcons.IMPORT),
                                   tree) {
            public void actionPerformed(ActionEvent e) {
                addImport();
            }

            public void onSelectionChange() {
                Collection sel = getSelection();
                DefaultOWLOntology ont = (DefaultOWLOntology) CollectionUtilities.getFirstItem(sel);
                setAllowed(ont == getActiveOntology(ont.getOWLModel()));
            }
        };
    }

    private Action createRemoveImportAction() {
        return new AllowableAction("Remove import",
                                   OWLIcons.getRemoveIcon(OWLIcons.IMPORT),
                                   tree) {
            public void actionPerformed(ActionEvent e) {
                removeSelectedImport();
            }

            public void onSelectionChange() {
                setAllowed(tree.getSelectionCount() == 1 &&
                           tree.getSelectionRows()[0] != 0);
            }
        };
    }

    private Action createSetActiveOntologyAction() {
        return new AllowableAction("Set active ontology",
                                   OWLIcons.getImageIcon(OWLIcons.SELECT_ACTIVE_TRIPLESTORE),
                                   tree) {
            public void actionPerformed(ActionEvent e) {
                setActiveOntology();
            }

            public void onSelectionChange() {
                Collection sel = getSelection();
                DefaultOWLOntology ont = (DefaultOWLOntology) CollectionUtilities.getFirstItem(sel);
                setAllowed(sel.size() == 1 && ont.isAssociatedTriplestoreEditable());
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
                Collection addedPrefixes = new ArrayList(owlModel.getNamespaceManager().getPrefixes());
                addedPrefixes.removeAll(prefixes);
                if (addedPrefixes.size() > 0) {
                    Component topLevelContainer = ProtegeUI.getTopLevelContainer(owlModel.getProject());
                    AddedPrefixesTable.showDialog(topLevelContainer, owlModel, addedPrefixes);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeSelectedImport() {
        OWLOntology sel = (OWLOntology) CollectionUtilities.getFirstItem(tree.getSelectedResources());
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

        if (ont != getActiveOntology(owlModel)) {
            TripleStoreModel tsm = owlModel.getTripleStoreModel();
            TripleStore tripleStore = tsm.getHomeTripleStore(ont);
            TripleStoreUtil.switchTripleStore(owlModel, tripleStore);
        }
    }

    public void dispose() {
        tree.dispose();
    }

    public OWLOntology getActiveOntology(OWLModel owlModel) {
        OWLOntology owlOntology = owlModel.getDefaultOWLOntology();
        for (Iterator it = owlModel.getOWLOntologies().iterator(); it.hasNext();) {
            OWLOntology curOnt = (OWLOntology) it.next();
            TripleStoreModel tsm = owlModel.getTripleStoreModel();
            TripleStore activeTripleStore = tsm.getActiveTripleStore();
            if (activeTripleStore.contains(curOnt,
                                           owlModel.getRDFTypeProperty(),
                                           owlModel.getOWLOntologyClass())) {
                owlOntology = curOnt;
                break;
            }
        }
        return owlOntology;
    }
}
