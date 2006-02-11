package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.action.ViewClsAction;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.existential.ExistentialAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.matrix.cls.ClassMatrixAction;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.subsumption.AssertedSubsumptionTreePanel;
import edu.stanford.smi.protegex.owl.ui.subsumption.InferredSubsumptionTreePanel;
import edu.stanford.smi.protegex.owl.ui.subsumption.SubsumptionTreePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * A component displaying the asserted classes tree on the OWLClassesTab.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AssertedClassesPanel extends SelectableContainer implements Hierarchy, ClassTreePanel {

    protected HeaderComponent classBrowserHeader;


    private Action createSiblingClassAction = new AbstractAction(CreateSiblingClassAction.TEXT,
                                                                 OWLIcons.getCreateIcon(OWLIcons.SIBLING_CLASS)) {
        public void actionPerformed(ActionEvent e) {
            createSibling();
        }
    };

    private Action createSubClassAction = new AbstractAction("Create subclass",
                                                             OWLIcons.getCreateIcon(OWLIcons.SUB_CLASS, 4)) {
        public void actionPerformed(ActionEvent e) {
            createSubclass();
        }
    };

    protected AllowableAction deleteAction;

    private ExistentialAction existentialAction;

    private HeaderComponent headerComponent;

    private HierarchyManager hierarchyManager;

    protected LabeledComponent labeledComponent;

    private OWLModel owlModel;

    protected OWLSubclassPane subclassPane;

    protected Action viewAction;


    /**
     * @deprecated
     */
    public AssertedClassesPanel(Project project, HierarchyManager hierarchyManager) {
        this((OWLModel) project.getKnowledgeBase(), hierarchyManager);
    }


    public AssertedClassesPanel(OWLModel owlModel, HierarchyManager hierarchyManager) {

        this.owlModel = owlModel;

        viewAction = getViewClassAction();
        deleteAction = getDeleteClsAction();
        createPanes();
        labeledComponent = new OWLLabeledComponent(getTitle(), subclassPane, true, false);
        labeledComponent.setBorder(ComponentUtilities.getAlignBorder());

        add(labeledComponent, BorderLayout.CENTER);
        add(createClsBrowserHeader(), BorderLayout.NORTH);
        setSelectable(subclassPane);
        updateDeleteActionState();
        this.hierarchyManager = hierarchyManager;
        labeledComponent.addHeaderButton(createSubClassAction);
        labeledComponent.addHeaderButton(createSiblingClassAction);
        createSiblingClassAction.setEnabled(false);
        labeledComponent.addHeaderButton(deleteAction);

        // the existential and inferred trees
        existentialAction = new ExistentialAction(this, hierarchyManager, null);
        existentialAction.setEnabled(false);
        if (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.OWL_DL)) {

            JButton existButton = subclassPane.getFinder().addButton(existentialAction);
            existentialAction.activateComboBox(existButton);
            Action showInferredAction = new AbstractAction("Explore inferred hierarchy",
                                                           OWLIcons.getImageIcon("ShowInferred")) {
                public void actionPerformed(ActionEvent e) {
                    showInferred();
                }
            };

            subclassPane.getFinder().addButton(showInferredAction);
        }
    }


    /**
     * @deprecated
     */
    protected OWLSubclassPane createSubclassPane(Action viewAction,
                                                 RDFSNamedClass rootClass,
                                                 Action createClsAction,
                                                 Action deleteClsAction) {
        return createSubclassPane(viewAction, rootClass, deleteClsAction);
    }


    protected OWLSubclassPane createSubclassPane(Action viewAction,
                                                 RDFSNamedClass rootClass,
                                                 Action deleteClsAction) {
        subclassPane = new OWLSubclassPane(owlModel, viewAction, rootClass);
        subclassPane.setHierarchyManager(hierarchyManager);
        subclassPane.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                if (subclassPane.getSelection().size() == 1) {
                    Cls cls = (Cls) subclassPane.getSelection().iterator().next();
                    createSiblingClassAction.setEnabled(!owlModel.getOWLThingClass().equals(cls));
                    if (cls instanceof OWLNamedClass) {
                        existentialAction.setEnabled(true);
                        existentialAction.setCls((OWLNamedClass) cls);
                    }
                    else {
                        existentialAction.setEnabled(false);
                    }
                }
            }
        });

        // matrix pane and superclass tree
        subclassPane.getFinder().addButton(new ClassMatrixAction(owlModel));
        subclassPane.getFinder().addButton(new ToggleSuperclassExplorerAction(this, false));

        return subclassPane;
    }

    public Hierarchy createClone() {
        AssertedClassesPanel panel = new AssertedClassesPanel(owlModel, hierarchyManager);
        panel.getLabeledComponent().removeHeaderButton(getLabeledComponent().getHeaderButtons().size() - 1);
        panel.getLabeledComponent().removeHeaderButton(getLabeledComponent().getHeaderButtons().size() - 1);
        return panel;
    }


    protected HeaderComponent createClsBrowserHeader() {
        JLabel label = ComponentFactory.createLabel(owlModel.getProject().getName(), Icons.getProjectIcon(), SwingConstants.LEFT);
        String forProject = LocalizedText.getText(ResourceKey.CLASS_BROWSER_FOR_PROJECT_LABEL);
        String classBrowser = LocalizedText.getText(ResourceKey.CLASS_BROWSER_TITLE);
        headerComponent = new HeaderComponent(classBrowser, forProject, label);
        return headerComponent;
    }


    private void createSibling() {
        Collection siblings = subclassPane.getSelection();
        if (siblings.size() == 1) {
            RDFSNamedClass sibling = (RDFSNamedClass) siblings.iterator().next();
            CreateSiblingClassAction.performAction(sibling, this);
        }
    }


    private void createSubclass() {
        Collection parents = subclassPane.getSelection();
        if (!parents.isEmpty()) {
            CreateSubclassAction.performAction(parents, this);
        }
    }


    protected void createPanes() {
        subclassPane = createSubclassPane(viewAction, owlModel.getOWLThingClass(), deleteAction);
    }


    public JTree getClsesTree() {
        return (JTree) subclassPane.getDropComponent();
    }


    protected AllowableAction getDeleteClsAction() {
        AllowableAction action = new AllowableAction("Delete selected class", OWLIcons.getDeleteClsIcon(), this) {

            public void actionPerformed(ActionEvent e) {
                RDFSNamedClass cls = (RDFSNamedClass) getSelectedClass();
                DeleteClassAction.performAction(cls, AssertedClassesPanel.this);
            }


            public void onSelectionChange() {
                updateDeleteActionState();
            }
        };
        action.setEnabled(true);

        action.putValue(Action.SMALL_ICON, OWLIcons.getDeleteClsIcon());
        return action;
    }


    protected JComponent getDisplayedComponent() {
        return (JComponent) labeledComponent.getCenterComponent();
    }


    public JComponent getDropComponent() {
        return subclassPane.getDropComponent();
    }


    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }


    public LabeledComponent getLabeledComponent() {
        return labeledComponent;
    }


    /**
     * @return edu.stanford.smi.protege.model.Project
     */
    public Project getProject() {
        return owlModel.getProject();
    }


    public Collection getSelection() {
        return ((Selectable) getDisplayedComponent()).getSelection();
    }


    public RDFSClass getSelectedClass() {
        Collection sels = subclassPane.getSelection();
        if (sels.isEmpty()) {
            return null;
        }
        else {
            return (RDFSClass) sels.iterator().next();
        }
    }


    public String getTitle() {
        return AssertedSubsumptionTreePanel.TITLE;
    }


    public JTree getTree() {
        return getClsesTree();
    }


    public String getType() {
        return SubsumptionTreePanel.TYPE;
    }


    private Action getViewClassAction() {
        return new ViewClsAction(this) {
            public void onView(Object o) {
                showInstance((Cls) o);
            }
        };
    }


    public boolean isDefaultSynchronized() {
        return false;
    }


    public void navigateToResource(RDFResource resource) {
        if (resource instanceof RDFSNamedClass) {
            setSelectedClass((RDFSNamedClass) resource);
        }
    }


    /**
     * @deprecated
     */
    public void setExpandedCls(Cls cls, boolean expanded) {
        if (cls instanceof RDFSNamedClass) {
            setExpandedClass((RDFSNamedClass) cls, expanded);
        }
    }


    public void setExpandedClass(RDFSNamedClass cls, boolean expanded) {
        subclassPane.setExpandedCls(cls, expanded);
    }


    public void setFinderComponent(JComponent c) {
        subclassPane.setFinderComponent(c);
    }


    public void setRenderer(DefaultRenderer renderer) {
        subclassPane.setRenderer(renderer);
    }


    public void setSelectedClass(RDFSClass cls) {
        subclassPane.setSelectedClass(cls);
    }


    /**
     * @see #setSelectedClass
     * @deprecated
     */
    public void setSelectedCls(Cls cls) {
        setSelectedClass((RDFSClass) cls);
    }


    public void showInferred() {
        InferredSubsumptionTreePanel inferredTreePanel = new InferredSubsumptionTreePanel(owlModel);
        hierarchyManager.addHierarchy(inferredTreePanel);
    }


    protected void showInstance(Instance instance) {
        owlModel.getProject().show(instance);
    }


    protected void updateDeleteActionState() {
        if (deleteAction != null) {
            boolean isEditable = true;
            Iterator i = getSelection().iterator();
            while (i.hasNext()) {
                Frame frame = (Frame) i.next();
                if (!frame.isEditable()) {
                    isEditable = false;
                    break;
                }
            }
            deleteAction.setAllowed(isEditable);
        }
    }
}
