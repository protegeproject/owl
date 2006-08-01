package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FormsPanel;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protege.widget.FormsTab;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.search.finder.*;

import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         25-Oct-2005
 */
public class OWLFormsTab extends FormsTab implements HostResourceDisplay {

    private SelectableTree theTree;

    public void initialize() {
        super.initialize();
        FormsPanel panel = (FormsPanel) getSubComponent(this, FormsPanel.class);
        LabeledComponent lc = (LabeledComponent) getSubComponent(panel, LabeledComponent.class);
        theTree = (SelectableTree) panel.getSelectable();

        FindAction fAction = new FindInDialogAction(new DefaultClassFind((OWLModel) getKnowledgeBase(),
                                                                         Find.CONTAINS),
                                                                         Icons.getFindFormIcon(),
                                                                         this, true);

        ResourceFinder finder = new ResourceFinder(fAction);

        lc.setFooterComponent(finder);
    }

    private Object getSubComponent(Component start, Class searchClass) {
        Object component = null;
        java.util.List components = new LinkedList();
        components.add(start);

        while ((component == null) && (components.size() > 0)) {
            Component current = (Component) components.remove(0);
            if (searchClass.isAssignableFrom(current.getClass())) {
                component = current;
            }
            else if (current instanceof Container) {
                components.addAll(Arrays.asList(((Container) current).getComponents()));
            }
        }
        return component;
    }

    public boolean displayHostResource(RDFResource resource) {
        if (resource instanceof RDFSClass) {
            if (!getSelection().contains(resource)) {
                java.util.List objectPath = getPathToRoot((RDFSClass) resource, new LinkedList());
                final TreePath path = ComponentUtilities.getTreePath(theTree, objectPath);
                if (path != null) {
                    final WaitCursor cursor = new WaitCursor(this);
                    theTree.scrollPathToVisible(path);
                    theTree.setSelectionPath(path);
                    theTree.updateUI();
                    cursor.hide();
                    return true;
                }
            }
        }
        return false;
    }

    private java.util.List getPathToRoot(RDFSClass cls, LinkedList list) {
        list.add(0, cls);
        Cls rootCls = cls.getOWLModel().getOWLThingClass();
        //@@TODO this is currently only implemented for the asserted superclasses
        Collection superclasses = cls.getSuperclasses(false);
        for (Iterator it = superclasses.iterator(); it.hasNext();) {
            Cls superclass = (Cls) it.next();
            if (superclass.equals(rootCls)) {
                list.add(0, superclass);
                return list;
            }
            else if (cls.isVisible() && superclass instanceof OWLNamedClass) {
                getPathToRoot((OWLNamedClass) superclass, list);
                break;
            }
        }
        return list;
    }
}
