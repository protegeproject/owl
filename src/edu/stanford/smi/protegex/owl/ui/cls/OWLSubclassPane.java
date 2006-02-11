package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.ClsesTreeTarget;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.existential.ExistentialAction;
import edu.stanford.smi.protegex.owl.ui.search.finder.*;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A SubclassPane optimized for OWLModels.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLSubclassPane extends SelectableContainer implements ClassTreePanel {

    private HierarchyManager hierarchyManager;

    private OWLModel owlModel;

    private ClassTree tree;

    private ResourceFinder finder;


    /**
     * @deprecated
     */
    public OWLSubclassPane(OWLModel owlModel, Action doubleClickAction,
                           Cls root, Action deleteClsAction) {
        this(owlModel, doubleClickAction, (RDFSNamedClass) root);
    }


    public OWLSubclassPane(OWLModel owlModel, Action doubleClickAction,
                           RDFSNamedClass rootClass) {
        this.owlModel = owlModel;
        tree = createSelectableTree(doubleClickAction, rootClass);
        tree.setLargeModel(true);
        tree.setSelectionRow(0);
        tree.setAutoscrolls(true);
        setSelectable(tree);
        setLayout(new BorderLayout());
        JScrollPane pane = ComponentFactory.createScrollPane(tree);
        add(BorderLayout.CENTER, pane);

        FindAction fAction = new FindInDialogAction(new DefaultClassFind(owlModel, Find.CONTAINS),
                                            Icons.getFindClsIcon(),
                                            tree, true);
        finder = new ResourceFinder(fAction);

        add(BorderLayout.SOUTH, finder);
        setupDragAndDrop();
        getTree().setCellRenderer(FrameRenderer.createInstance());
        getTree().addMouseListener(new TreePopupMenuMouseListener(tree) {
            public JPopupMenu getPopupMenu() {
                return OWLSubclassPane.this.getPopupMenu();
            }
        });
        this.owlModel = owlModel;
        Slot directSuperclassesSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUPERCLASSES);
        ((JTree) getSelectable()).setCellRenderer(new ResourceRenderer(directSuperclassesSlot));
    }


    protected JPopupMenu createPopupMenu() {
        Collection sel = getSelection();
        if (sel.size() == 1) {
            Cls cls = (Cls) sel.iterator().next();
            if (cls instanceof RDFSNamedClass) {
                JPopupMenu menu = new JPopupMenu();
                ResourceActionManager.addResourceActions(menu, this, (RDFResource) cls);
                if (cls instanceof OWLNamedClass) {
                    if (hierarchyManager != null) {
                        menu.addSeparator();
                        menu.add(new ExistentialAction(this, hierarchyManager, (OWLNamedClass) cls));
                    }
                }
                return menu;
            }
        }
        return null;
    }


    protected ClassTree createSelectableTree(Action doubleClickAction, Cls rootCls) {
        this.owlModel = (OWLModel) rootCls.getKnowledgeBase();
        // LazyTreeRoot root = new ParentChildRoot(rootCls);
        // LazyTreeRoot root = new SubsumptionTreeRoot(rootCls, rootCls.getKnowledgeBase().getSlot(Model.Slot.DIRECT_SUBCLASSES));
        LazyTreeRoot root = new ClassTreeRoot(rootCls);
        return new ClassTree(doubleClickAction, root);
    }


    public void extendSelection(Cls cls) {
        ComponentUtilities.extendSelection(getTree(), cls);
    }


    public JComponent getDropComponent() {
        return getTree();
    }

    public ResourceFinder getFinder() {
        return finder;
    }

    private JPopupMenu getPopupMenu() {
        return createPopupMenu();
    }


    public JTree getTree() {
        return tree;
    }


    protected Cls pickConcreteCls(Collection allowedClses, String text) {
        RDFSNamedClass owlNamedClassMetaClass = owlModel.getOWLNamedClassClass();
        RDFSNamedClass rdfsNamedClassMetaClass = owlModel.getRDFSNamedClassClass();
        RDFSNamedClass metaClass = owlNamedClassMetaClass;
        boolean oldNamedVisible = owlNamedClassMetaClass.isVisible();
        boolean oldRDFSVisible = rdfsNamedClassMetaClass.isVisible();
        if (OWLUtil.hasRDFProfile(owlModel)) {
            metaClass = rdfsNamedClassMetaClass;
            rdfsNamedClassMetaClass.setVisible(true);
            owlNamedClassMetaClass.setVisible(true);
        }
        Cls result = ProtegeUI.getSelectionDialogFactory().selectClass(this, owlModel, Collections.singleton(metaClass), text);
        rdfsNamedClassMetaClass.setVisible(oldRDFSVisible);
        owlNamedClassMetaClass.setVisible(oldNamedVisible);
        return result;
    }


    public void removeSelection() {
        ComponentUtilities.removeSelection(getTree());
    }


    public void setHierarchyManager(HierarchyManager hierarchyManager) {
        this.hierarchyManager = hierarchyManager;
    }


    public void setSelectedClass(RDFSClass cls) {
        setSelectedClassDelegate(cls);
    }


    /**
     * @see #setSelectedClass
     * @deprecated
     */
    public void setSelectedCls(Cls cls) {
        setSelectedClassDelegate(cls);
    }


    private void setSelectedClassDelegate(Cls cls) {
        if (!getSelection().contains(cls)) {
            Collection path = ModelUtilities.getPathToRoot(cls);
            setSelectedObjectPath(getTree(), path);
        }
    }


    public void setSelectedClasses(Collection classes) {
        setSelectedClassesDelegate(classes);
    }


    private void setSelectedClassesDelegate(Collection clses) {
        Collection paths = new ArrayList();
        Iterator i = clses.iterator();
        while (i.hasNext()) {
            Cls cls = (Cls) i.next();
            paths.add(ModelUtilities.getPathToRoot(cls));
        }
        ComponentUtilities.setSelectedObjectPaths(getTree(), paths);
    }


    /**
     * @see #setSelectedClasses
     * @deprecated
     */
    public void setSelectedClses(Collection clses) {
        setSelectedClassesDelegate(clses);
    }


    private void setSelectedObjectPath(final JTree tree, Collection objectPath) {
        final TreePath path = ComponentUtilities.getTreePath(tree, objectPath);
        if (path != null) {
            final WaitCursor cursor = new WaitCursor(tree);
            tree.scrollPathToVisible(path);
            tree.setSelectionPath(path);
            cursor.hide();
        }
    }


    protected void setupDragAndDrop() {
        if (OWLUI.isDragAndDropSupported(owlModel)) {
            final JTree tree = (JTree) getSelectable();
            DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(tree,
                                                                                 DnDConstants.ACTION_COPY_OR_MOVE, new OWLClassesTreeDragSourceListener());
            new DropTarget(tree, DnDConstants.ACTION_COPY_OR_MOVE, new ClsesTreeTarget());
        }
    }


    public void setExpandedCls(Cls cls, boolean expanded) {
        Collection path = ModelUtilities.getPathToRoot(cls);
        ComponentUtilities.setExpanded(getTree(), path, expanded);
    }


    public void setFinderComponent(JComponent c) {
        add(c, BorderLayout.SOUTH);
    }


    public void setRenderer(DefaultRenderer renderer) {
        getTree().setCellRenderer(renderer);
    }


    public void setDisplayParent(Cls cls) {
        ComponentUtilities.setDisplayParent(getTree(), cls, new SuperclassTraverser());
    }


    public String toString() {
        return "SubclassPane";
    }
}
