package edu.stanford.smi.protegex.owl.ui.individuals;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.PopupMenuMouseListener;
import edu.stanford.smi.protege.util.Selectable;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.navigation.NavigationHistoryTabWidget;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourcePanel;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractTabWidget;

/**
 * A tab used to acquire individuals.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLIndividualsTab extends AbstractTabWidget implements NavigationHistoryTabWidget {

	private IndividualsTabClassesPanel classesPanel;
    private AssertedInstancesListPanel assertedInstancesListPanel;
    private InferredInstancesListPanel inferredInstancesListPanel;
    private ResourcePanel resourcePanel;
    private AssertedTypesListPanel typesListPanel;


    private void adjustInstancesDisplayPanel() {
        Component lc = null;
        for (int i = 0; i < assertedInstancesListPanel.getComponentCount(); i++) {
            if (assertedInstancesListPanel.getComponent(i) instanceof LabeledComponent) {
                lc = assertedInstancesListPanel.getComponent(i);
            }
        }
        assert lc != null;
        Container parent = lc.getParent();
        JTabbedPane tabbedPane = new JTabbedPane(); // JTabbedPane.BOTTOM);
        tabbedPane.addTab("Asserted", lc);
        tabbedPane.addTab("Inferred", inferredInstancesListPanel);
        tabbedPane.setBorder(null);
        parent.add(BorderLayout.CENTER, tabbedPane);
    }


    protected AssertedInstancesListPanel createAssertedInstancesListPanel() {
        AssertedInstancesListPanel result = new AssertedInstancesListPanel(getOWLModel());
        result.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                Collection selection = assertedInstancesListPanel.getSelection();
                Instance selectedInstance;
                if (selection.size() == 1) {
                    selectedInstance = (Instance) CollectionUtilities.getFirstItem(selection);
                }
                else {
                    selectedInstance = null;
                }
                if(selectedInstance == null || selectedInstance instanceof RDFResource) {
                    RDFResource resource = (RDFResource) selectedInstance;
                    resourcePanel.setResource(resource);
                    typesListPanel.setResource(resource);
                }
                else if(resourcePanel instanceof ResourceDisplay) {  // legacy only
                    ((ResourceDisplay)resourcePanel).setInstance(selectedInstance);
                    typesListPanel.setResource(null);
                }
            }
        });
        setInstanceSelectable((Selectable) result.getDragComponent());
        final JList list = (JList) result.getDragComponent();
        list.addMouseListener(new PopupMenuMouseListener(list) {
            @Override
			protected JPopupMenu getPopupMenu() {
            	Object selection = list.getSelectedValue();
            	Frame frame = null;
            	if (selection instanceof FrameWithBrowserText) {
            		frame = ((FrameWithBrowserText) selection).getFrame();
            	} else if (selection instanceof Frame) {
            		frame = (Frame) selection;
            	}                 
                if (frame != null && frame instanceof RDFResource) {
                    JPopupMenu menu = new JPopupMenu();
                    ResourceActionManager.addResourceActions(menu, list, (RDFResource) frame);
                    if (menu.getComponentCount() > 0) {
                        return menu;
                    }
                }
                return null;
            }


            @Override
			protected void setSelection(JComponent c, int x, int y) {
                for (int i = 0; i < list.getModel().getSize(); i++) {
                    if (list.getCellBounds(i, i).contains(x, y)) {
                        list.setSelectedIndex(i);
                        return;
                    }
                }
                list.setSelectedIndex(-1);
            }
        });
        return result;
    }


    protected InferredInstancesListPanel createInferredInstancesListPanel() {
        inferredInstancesListPanel = new InferredInstancesListPanel();
        inferredInstancesListPanel.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                Collection selection = inferredInstancesListPanel.getSelection();
                Instance selectedInstance;
                if (selection.size() == 1) {
                    selectedInstance = (Instance) CollectionUtilities.getFirstItem(selection);
                }
                else {
                    selectedInstance = null;
                }
                if(selectedInstance == null || selectedInstance instanceof RDFResource) {
                    resourcePanel.setResource((RDFResource)selectedInstance);
                }
                else if(resourcePanel instanceof ResourceDisplay) {
                    ((ResourceDisplay)resourcePanel).setInstance(selectedInstance);
                }
            }
        });
        return inferredInstancesListPanel;
    }


    private IndividualsTabClassesPanel createClassesPanel() {
        IndividualsTabClassesPanel classesPanel = new IndividualsTabClassesPanel(getOWLModel());
        classesPanel.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                transmitSelection();
            }
        });

        return classesPanel;
    }


    private JComponent createClassSplitter() {
        JSplitPane pane = createLeftRightSplitPane("InstancesTab.left_right", 250);
        classesPanel = createClassesPanel();
        pane.setLeftComponent(classesPanel);
        pane.setRightComponent(createInstanceSplitter());
        return pane;
    }


    protected JComponent createDirectTypesList() {
        typesListPanel = new AssertedTypesListPanel(getOWLModel());
        return typesListPanel;
    }


    private JComponent createInstancesPanel() {
        JSplitPane panel = ComponentFactory.createTopBottomSplitPane();
        assertedInstancesListPanel = createAssertedInstancesListPanel();
        panel.setTopComponent(assertedInstancesListPanel);
        panel.setBottomComponent(createDirectTypesList());
        return panel;
    }


    private JComponent createInstanceSplitter() {
        JSplitPane pane = createLeftRightSplitPane("InstancesTab.right.left_right", 250);
        pane.setLeftComponent(createInstancesPanel());
        resourcePanel = ProtegeUI.getResourcePanelFactory().createResourcePanel(getOWLModel(), ResourcePanel.DEFAULT_TYPE_INDIVIDUAL);
        pane.setRightComponent((Component) resourcePanel);
        return pane;
    }


    public boolean displayHostResource(RDFResource resource) {
        if (resource instanceof RDFIndividual &&
                !(resource instanceof OWLOntology) &&
                !(resource instanceof OWLAllDifferent)) {
            setSelectedResource(resource);
            return true;
        }
        return false;
    }


    public Selectable getNestedSelectable() {
        return assertedInstancesListPanel;
    }


    public void initialize() {

        inferredInstancesListPanel = createInferredInstancesListPanel();

        add(createClassSplitter());
        transmitSelection();
        setupDragAndDrop();
        setClsTree(classesPanel.getDropComponent());
        setLabel("Individuals");
        setIcon(OWLIcons.getImageIcon(OWLIcons.RDF_INDIVIDUALS));

        adjustInstancesDisplayPanel();
    }


    public static boolean isSuitable(Project p, Collection errors) {
        return OWLClassesTab.isSuitable(p, errors);
    }


    protected void transmitSelection(Collection selection) {
        if (selection.contains(getOWLModel().getOWLThingClass()) && getOWLModel() instanceof OWLDatabaseModel) {
            selection = Collections.EMPTY_LIST;
        }
        assertedInstancesListPanel.setClses(selection);
        Collection types = new ArrayList();
        for (Iterator it = selection.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFSClass) {
                types.add(o);
            }
        }
        inferredInstancesListPanel.setTypes(types);
    }


    /**
     * @deprecated
     */
    @Deprecated
	public void setSelectedCls(Cls cls) {
        if (cls instanceof RDFSNamedClass) {
            setSelectedClass((RDFSNamedClass) cls);
        }
    }


    public void setSelectedClass(RDFSNamedClass cls) {
        classesPanel.setSelectedClass(cls);
    }


    /**
     * @deprecated
     */
    @Deprecated
	public void setSelectedInstance(Instance instance) {
        if (instance instanceof RDFResource) {
            setSelectedResource((RDFResource) instance);
        }
    }


    public void setSelectedResource(RDFResource instance) {
        classesPanel.setSelectedClass((RDFSNamedClass) instance.getRDFType());
        assertedInstancesListPanel.setSelectedInstance(instance);
        typesListPanel.setResource(instance);
    }


    private void setupDragAndDrop() {
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(assertedInstancesListPanel.getDragComponent(),
                DnDConstants.ACTION_COPY_OR_MOVE, new AssertedInstancesListDragSourceListener());
        new DropTarget(classesPanel.getDropComponent(), DnDConstants.ACTION_COPY_OR_MOVE, new IndividualsClassesTreeTarget());
    }


    protected void transmitSelection() {
        WaitCursor cursor = new WaitCursor(this);
        try {
            Collection selection = classesPanel.getSelection();
            transmitSelection(selection);
        }
        finally {
            cursor.hide();
        }
    }
}
