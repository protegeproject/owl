package edu.stanford.smi.protegex.owl.ui.properties;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.SlotsTreeDragSourceListener;
import edu.stanford.smi.protege.ui.SlotsTreeTarget;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OverlayIcon;
import edu.stanford.smi.protegex.owl.ui.matrix.property.PropertyMatrixAction;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.properties.actions.CreateSubpropertyAction;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.search.finder.*;
import edu.stanford.smi.protegex.owl.ui.subsumption.TooltippedSelectableTree;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * A SelectableContainer displaying a tree of properties.
 * <p/>
 * This class is an adaptation of the core Protege class SubslotPane.
 * Actually this class started as a subclass of SubslotPane, but essentially
 * all methods were overloaded and most core features disabled or changed.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLSubpropertyPane extends SelectableContainer implements HostResourceDisplay {

    private Action createAnnotationOWLDatatypePropertyAction =
            new AbstractAction("Create annotation datatype property",
                               OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_DATATYPE_ANNOTATION_PROPERTY)) {
                public void actionPerformed(ActionEvent e) {
                    OWLProperty property = null;
                    try {
                        owlModel.beginTransaction(getValue(Action.NAME).toString());
                        String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_ANNOTATION_PROPERTY_NAME);
                        property = owlModel.createAnnotationOWLDatatypeProperty(name);
                    }
                    catch (Exception ex) {
                        OWLUI.handleError(owlModel, ex);
                    }
                    finally {
                        owlModel.endTransaction();
                    }
                    displayHostResource(property);
                }
            };


    private Action createAnnotationOWLObjectPropertyAction =
            new AbstractAction("Create annotation object property",
                               OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_OBJECT_ANNOTATION_PROPERTY)) {
                public void actionPerformed(ActionEvent e) {
                    RDFProperty property = null;
                    try {
                        owlModel.beginTransaction(getValue(Action.NAME).toString());
                        String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_ANNOTATION_PROPERTY_NAME);
                        property = owlModel.createAnnotationOWLObjectProperty(name);
                    }
                    catch (Exception ex) {
                        OWLUI.handleError(owlModel, ex);
                    }
                    finally {
                        owlModel.endTransaction();
                    }
                    displayHostResource(property);
                }
            };

    private Action createOWLDatatypePropertyAction =
            new AbstractAction("Create datatype property",
                               OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_DATATYPE_PROPERTY)) {
                public void actionPerformed(ActionEvent e) {
                    OWLProperty property = null;
                    try {
                        owlModel.beginTransaction(getValue(Action.NAME).toString());
                        String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_DATATYPE_PROPERTY_NAME);
                        property = owlModel.createOWLDatatypeProperty(name);
                        property.setDomainDefined(false);
                    }
                    catch (Exception ex) {
                        OWLUI.handleError(owlModel, ex);
                    }
                    finally {
                        owlModel.endTransaction();
                    }
                    displayHostResource(property);
                }
            };


    private Action createOWLObjectPropertyAction =
            new AbstractAction("Create object property",
                               OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_OBJECT_PROPERTY)) {
                public void actionPerformed(ActionEvent e) {
                    OWLProperty property = null;
                    try {
                        owlModel.beginTransaction(getValue(Action.NAME).toString());
                        String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_OBJECT_PROPERTY_NAME);
                        property = owlModel.createOWLObjectProperty(name);
                        property.setDomainDefined(false);
                    }
                    catch (Exception ex) {
                        OWLUI.handleError(owlModel, ex);
                    }
                    finally {
                        owlModel.endTransaction();
                    }
                    displayHostResource(property);
                }
            };


    private Action createRDFPropertyAction =
            new AbstractAction("Create RDF property",
                               OWLIcons.getCreatePropertyIcon(OWLIcons.RDF_PROPERTY)) {
                public void actionPerformed(ActionEvent e) {
                    RDFProperty property = null;
                    try {
                        owlModel.beginTransaction(getValue(Action.NAME).toString());
                        String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_PROPERTY_NAME);
                        property = owlModel.createRDFProperty(name);
                    }
                    catch (Exception ex) {
                        OWLUI.handleError(owlModel, ex);
                    }
                    finally {
                        owlModel.endTransaction();
                    }
                    displayHostResource(property);
                }
            };

    private Action createSubpropertyAction =
            new AbstractAction("Create subproperty",
                               OWLIcons.getCreatePropertyIcon("SubProperty")) {
                public void actionPerformed(ActionEvent e) {
                    Collection sel = getSelection();
                    if (sel.size() == 1) {
                        RDFProperty superProperty = (RDFProperty) sel.iterator().next();
                        CreateSubpropertyAction.performAction(owlModel, superProperty, OWLSubpropertyPane.this);
                    }
                }
            };

    private Action deletePropertyAction = new DeleteAction("Delete properties", this, OWLIcons.getDeleteIcon(OWLIcons.RDF_PROPERTY)) {
        public void onDelete(Collection slots) {
            handleDelete(slots);
        }


        public void onSelectionChange() {
            RDFProperty slot = (RDFProperty) CollectionUtilities.getFirstItem(this.getSelection());
            if (slot != null) {
                setAllowed(slot.isEditable());
            }
        }
    };

    private OWLModel owlModel;

    private OWLPropertySubpropertyRoot root;

    private Action viewPropertyAction = new ViewAction("View selected properties", this) {
        public void onView(Object o) {
            owlModel.getProject().show((RDFProperty) o);
        }
    };

    /**
     * @deprecated the other constructor is better
     */
    public OWLSubpropertyPane(Project p) {
        this((OWLModel) p.getKnowledgeBase());
    }


    public OWLSubpropertyPane(OWLModel owlModel) {

        this.owlModel = owlModel;

        root = createRoot();
        TooltippedSelectableTree tree = new TooltippedSelectableTree(viewPropertyAction, root);
        tree.setCellRenderer(new ResourceRenderer());
        tree.setShowsRootHandles(true);
        tree.setSelectionRow(0);
        tree.setLargeModel(true);
        setSelectable(tree);
        setLayout(new BorderLayout());
        LabeledComponent labeledComponent = new LabeledComponent(getHeaderLabel(), ComponentFactory.createScrollPane(tree));
        labeledComponent.setBorder(ComponentUtilities.getAlignBorder());
        add(labeledComponent, BorderLayout.CENTER);

        ResultsViewModelFind findAlg = new DefaultPropertyFind(owlModel, Find.CONTAINS) {
            protected boolean isValidFrameToSearch(Frame f) {
                return super.isValidFrameToSearch(f) &&
                       root.isSuitable((RDFProperty) f);
            }
        };

        FindAction fAction = new FindInDialogAction(findAlg,
                                                    Icons.getFindSlotIcon(),
                                                    this, true);

        ResourceFinder finder = new ResourceFinder(fAction);
        add(finder, BorderLayout.SOUTH);
        finder.addButton(new PropertyMatrixAction(owlModel));

        tree.addMouseListener(new TreePopupMenuMouseListener(tree) {
            public JPopupMenu getPopupMenu() {
                return OWLSubpropertyPane.this.getPopupMenu();
            }
        });
        setupDragAndDrop();
        // Necessary because the actions don't get notified when the tree is initialized.
        viewPropertyAction.setEnabled(true);
        deletePropertyAction.setEnabled(true);

        labeledComponent.setHeaderIcon(getHeaderIcon());
        WidgetUtilities.addViewButton(labeledComponent, viewPropertyAction);
        for (Iterator it = getActions().iterator(); it.hasNext();) {
            Action curAction = (Action) it.next();
            labeledComponent.addHeaderButton(curAction);
        }
        JButton deleteButton = labeledComponent.addHeaderButton(deletePropertyAction);
        deleteButton.setDisabledIcon(((OverlayIcon) deleteButton.getIcon()).getGrayedIcon());
    }

    protected String getHeaderLabel() {
        return "Properties";
    }

    protected Icon getHeaderIcon() {
        return OWLIcons.getImageIcon("Properties");
    }


    protected Action getCreateAnnotationOWLDatatypePropertyAction() {
        return createAnnotationOWLDatatypePropertyAction;
    }


    protected Action getCreateAnnotationOWLObjectPropertyAction() {
        return createAnnotationOWLObjectPropertyAction;
    }


    protected Action getCreateOWLDatatypePropertyAction() {
        return createOWLDatatypePropertyAction;
    }


    protected Action getCreateOWLObjectPropertyAction() {
        return createOWLObjectPropertyAction;
    }


    protected Action getCreateRDFPropertyAction() {
        return createRDFPropertyAction;
    }


    protected Action getCreateSubpropertyAction() {
        return createSubpropertyAction;
    }


    protected Action getDeletePropertyAction() {
        return deletePropertyAction;
    }


    public boolean contains(RDFProperty property) {
        TreeNode root = (TreeNode) getTree().getModel().getRoot();
        return contains(root, property, new HashSet());
    }


    private boolean contains(TreeNode node, RDFProperty property, Set reached) {
        if (!reached.contains(node)) {
            reached.add(node);
            if (node instanceof LazyTreeNode) {
                if (property.equals(((LazyTreeNode) node).getUserObject())) {
                    return true;
                }
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                if (contains(node.getChildAt(i), property, reached)) {
                    return true;
                }
            }
        }
        return false;
    }


    protected Collection getActions() {
        ArrayList list = new ArrayList();
        if (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.CreateRDFProperty)) {
            list.add(createRDFPropertyAction);
        }
        boolean datatypeSlots = ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Create_DatatypeProperty);
        boolean objectSlots = ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Create_ObjectProperty);
        if (datatypeSlots) {
            list.add(createOWLDatatypePropertyAction);
        }
        if (objectSlots) {
            list.add(createOWLObjectPropertyAction);
        }
        list.add(createSubpropertyAction);
        if (datatypeSlots) {
            list.add(createAnnotationOWLDatatypePropertyAction);
        }
        if (objectSlots) {
            list.add(createAnnotationOWLObjectPropertyAction);
        }
        return list;
    }


    protected OWLPropertySubpropertyRoot createRoot() {
        return new OWLPropertySubpropertyRoot(getOWLModel());
    }


    public void extendSelection(RDFProperty slot) {
        ComponentUtilities.extendSelection(getTree(), slot);
    }


    public RDFProperty getDisplayParent() {
        RDFProperty slot = null;
        TreePath childPath = getTree().getSelectionModel().getLeadSelectionPath();
        if (childPath != null) {
            TreePath path = childPath.getParentPath();
            LazyTreeNode node = (LazyTreeNode) path.getLastPathComponent();
            Object o = node.getUserObject();
            slot = (o instanceof RDFProperty) ? (RDFProperty) o : null;
        }
        return slot;
    }


    public JComponent getDropComponent() {
        return getTree();
    }


    public OWLModel getOWLModel() {
        return owlModel;
    }


    public List getPath(RDFProperty property, List list) {
        list.add(0, property);
        RDFProperty superproperty = (RDFProperty) CollectionUtilities.getFirstItem(property.getSuperproperties(false));
        if (superproperty != null) {
            getPath(superproperty, list);
        }
        return list;
    }


    protected JPopupMenu getPopupMenu() {
        JPopupMenu menu = null;
        Collection selection = getSelection();
        if (selection.size() == 1) {
            RDFProperty slot = (RDFProperty) CollectionUtilities.getFirstItem(selection);
            menu = new JPopupMenu();
            ResourceActionManager.addResourceActions(menu, this, slot);
        }
        return menu;
    }


    public JTree getTree() {
        return (JTree) getSelectable();
    }


    protected void handleDelete(Collection properties) {
        removeSelection();
        try {
            owlModel.beginTransaction("Delete properties " + properties);
            Iterator i = properties.iterator();
            while (i.hasNext()) {
                RDFProperty property = (RDFProperty) i.next();
                property.delete();
            }
        }
        finally {
            owlModel.endTransaction(true);
        }
    }


    public void removeSelection() {
        ComponentUtilities.removeSelection(getTree());
    }


    public void setExpandedProperty(RDFProperty property, boolean expanded) {
        ComponentUtilities.setExpanded(getTree(), getPath(property, new LinkedList()), expanded);
    }


    public void setRenderer(DefaultRenderer renderer) {
        getTree().setCellRenderer(renderer);
    }


    private void setupDragAndDrop() {
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(getTree(),
                                                                             DnDConstants.ACTION_COPY_OR_MOVE, new SlotsTreeDragSourceListener());
        new DropTarget(getTree(), DnDConstants.ACTION_COPY_OR_MOVE, new SlotsTreeTarget());
    }


    public void setDisplayParent(RDFProperty property) {
        ComponentUtilities.setDisplayParent(getTree(), property, new SuperslotTraverser());
    }


    public boolean displayHostResource(RDFResource resource) {
        if (!getSelection().contains(resource)) {
            ComponentUtilities.setSelectedObjectPath(getTree(),
                                                     getPath((RDFProperty) resource,
                                                             new LinkedList()));
        }
        return true;
    }
    
    public void setEnabled(boolean enabled) {    	
    	createAnnotationOWLDatatypePropertyAction.setEnabled(enabled);
    	createAnnotationOWLObjectPropertyAction.setEnabled(enabled);
    	createOWLDatatypePropertyAction.setEnabled(enabled);
    	createOWLObjectPropertyAction.setEnabled(enabled);
    	createRDFPropertyAction.setEnabled(enabled);
    	createSubpropertyAction.setEnabled(enabled);
    	deletePropertyAction.setEnabled(enabled);    
    	super.setEnabled(enabled);
    };
}
