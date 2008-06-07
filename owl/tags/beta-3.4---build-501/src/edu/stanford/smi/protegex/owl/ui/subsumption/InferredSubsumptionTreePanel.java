package edu.stanford.smi.protegex.owl.ui.subsumption;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.tree.TreePath;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.creator.JenaCreator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.cls.ClassTree;
import edu.stanford.smi.protegex.owl.ui.cls.Hierarchy;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * A SubsumptionTreePanel optimized for the inferred tree.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InferredSubsumptionTreePanel extends SubsumptionTreePanel {

    private Action assertAction = new AbstractAction("Assert selected change(s)",
                                                     OWLIcons.getAssertChangeIcon()) {
        public void actionPerformed(ActionEvent e) {
            assertSelectedChange();
        }
    };

    private Action displayChangedAction = new AbstractAction("Display changed classes in list",
                                                             OWLIcons.getDisplayChangedClassesIcon()) {
        public void actionPerformed(ActionEvent e) {
            OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(InferredSubsumptionTreePanel.this);
            if (tab != null) {
                tab.refreshChangedClses();
            }
        }
    };

    private Action saveInferredAction = new AbstractAction("Save inferred version as file...",
                                                           OWLIcons.getSaveInferredIcon()) {
        public void actionPerformed(ActionEvent e) {
            saveInferred();
        }
    };

    private final static String TITLE = "Inferred Hierarchy";


    public InferredSubsumptionTreePanel(OWLModel owlModel) {
        super(owlModel.getOWLThingClass(),
              ((AbstractOWLModel) owlModel).getProtegeInferredSubclassesProperty(),
              ((AbstractOWLModel) owlModel).getProtegeInferredSuperclassesProperty(),
              true);

        assertAction.setEnabled(false);
        getLabeledComponent().addHeaderButton(assertAction);
        getLabeledComponent().addHeaderButton(displayChangedAction);
        getLabeledComponent().addHeaderButton(saveInferredAction);
    }


    private void assertSelectedChange() {
        OWLNamedClass cls = getSelectedCls();
        OWLModel owlModel = getOWLModel();
        ChangedClassesPanel ccp = ChangedClassesPanel.get(owlModel);
        try {
            owlModel.beginTransaction("Assert change for " + cls.getBrowserText());
            ccp.getTableModel().assertChange(cls);
            owlModel.commitTransaction();           
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }


    public Hierarchy createClone() {
        return new InferredSubsumptionTreePanel(getOWLModel());
    }


    protected ClassTree createSelectableTree(Action viewAction, LazyTreeRoot root) {
        return new InferredChangesClassTree(viewAction, root);
    }


    protected Action createViewClsAction() {
        return new AbstractAction("View class", OWLIcons.getViewIcon()) {
            public void actionPerformed(ActionEvent e) {
                Collection selection = getSelection();
                if (!selection.isEmpty()) {
                    Cls cls = (Cls) selection.iterator().next();
                    cls.getProject().show(cls);
                }
            }
        };
    }


    public String getTitle() {
        return TITLE;
    }


    public void navigateToResource(RDFResource resource) {
        if (resource instanceof RDFSClass) {
            setSelectedClass((RDFSClass) resource);
        }
    }


    private void saveInferred() {
        JFileChooser fileChooser = ComponentFactory.createFileChooser("Select file", "owl");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            OWLModel owlModel = getOWLModel();
            Collection clses = new ArrayList();
            for (Iterator it = owlModel.getUserDefinedOWLNamedClasses().iterator(); it.hasNext();) {
                OWLNamedClass cls = (OWLNamedClass) it.next();
                if (!cls.isProbeClass()) {
                    clses.add(cls);
                }
            }
            String baseURI = owlModel.getNamespaceManager().getDefaultNamespace();
            if (baseURI.endsWith("#")) {
                baseURI = baseURI.substring(0, baseURI.length() - 1);
            }
            /*ExtractorEngine engine = new ExtractorEngine(owlModel);
            int all = ExtractorEngine.CLASSES | ExtractorEngine.PROPERTIES | ExtractorEngine.INDIVIDUALS;
            engine.setExtractTypes(all);
            engine.setExtractLogic(ExtractorEngine.CLASSES | ExtractorEngine.PROPERTIES);
            engine.setExtractAnnotations(all);
            engine.setExtractPropertyValues(all);
            engine.setBaseURI(baseURI);
            engine.setUseInferredRelationships(true);
            engine.setOutputFile(file);*/
            OntModel ontModel = new JenaCreator(owlModel, false, true, clses,
                                                new ModalProgressBarManager("Preparing File")).createOntModel();
            try {
                //engine.run();
                final String ns = owlModel.getNamespaceManager().getDefaultNamespace();
                Ontology ontology = Jena.getDefaultJenaOntology(ns, ontModel);
                ontology.addProperty(OWL.versionInfo, ontModel.createTypedLiteral("classified"));
                JenaOWLModel.save(file, ontModel, FileUtils.langXMLAbbrev, ns);
                ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                                                                    "Successfully saved to " + file + ".");
            }
            catch (IOException ex) {
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                ProtegeUI.getModalDialogFactory().showThrowable(owlModel, ex);
            }
        }
    }


    protected void updateActions() {
        super.updateActions();
        OWLNamedClass cls = getSelectedCls();
        assertAction.setEnabled(cls != null &&
                                cls.getClassificationStatus() == OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED &&
                                ChangedClassesPanel.get(getOWLModel()).getTableModel().contains(cls));
    }


    private class InferredChangesClassTree extends ClassTree {

        public InferredChangesClassTree(Action viewAction, LazyTreeRoot root) {
            super(viewAction, root);

            RDFProperty property = getOWLModel().getRDFProperty(ProtegeNames.Slot.INFERRED_SUPERCLASSES);
            setCellRenderer(new MovedResourcesRenderer(property));
        }

        public String getToolTipText(MouseEvent event) {
            String str = null;
            int row = getRowForLocation(event.getX(), event.getY());
            TreePath path = getPathForRow(row);
            if (path != null && path.getPathCount() > 0) {
                SubsumptionTreeNode node = (SubsumptionTreeNode) path.getLastPathComponent();
                RDFSClass cls = (RDFSClass) node.getCls();
                ChangedClassesPanel ccp = ChangedClassesPanel.get(getOWLModel());
                str = ccp.getChangeText(cls);
                if (str == null /*&& OWLMenuProjectPlugin.isProseActivated()*/) {
                    str = OWLUI.getOWLToolTipText(cls);
                }
            }
            return str;
        }
    }

    private class MovedResourcesRenderer extends ResourceRenderer {

        public MovedResourcesRenderer(Slot directSuperclassesSlot) {
            super(directSuperclassesSlot);
        }

        protected Color getTextColor() {
            ChangedClassesPanel ccp = ChangedClassesPanel.get(getOWLModel());
            if (ccp.contains(loadedClass)) {
                return Color.blue;
            }
            return super.getTextColor();
        }
    }
}
