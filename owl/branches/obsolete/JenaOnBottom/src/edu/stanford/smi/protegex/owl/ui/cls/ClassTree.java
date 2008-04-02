package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.ui.TripleSelectable;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.subsumption.TooltippedSelectableTree;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassTree extends TooltippedSelectableTree implements TripleSelectable, HostResourceDisplay {

    public ClassTree(Action doubleClickAction, LazyTreeRoot root) {
        super(doubleClickAction, root);
    }


    public List getPrototypeTriples() {
        List triples = new ArrayList();
        Iterator it = getSelection().iterator();
        while (it.hasNext()) {
            Object sel = it.next();
            if (sel instanceof RDFSNamedClass) {
                RDFSNamedClass object = (RDFSNamedClass) sel;
                RDFProperty predicate = object.getOWLModel().getRDFSSubClassOfProperty();
                triples.add(new DefaultTriple(null, predicate, object));
            }
        }
        return triples;
    }


    public List getSelectedTriples() {
        List results = new ArrayList();
        TreePath[] paths = getSelectionPaths();
        if (paths != null) {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                if (path.getPathCount() > 1 && path.getLastPathComponent() instanceof LazyTreeNode) {
                    LazyTreeNode node = (LazyTreeNode) path.getLastPathComponent();
                    Object subject = node.getUserObject();
                    if (subject instanceof RDFSNamedClass) {
                        RDFSNamedClass subjectClass = (RDFSNamedClass) subject;
                        TreeNode parent = node.getParent();
                        if (parent instanceof LazyTreeNode) {
                            Object object = ((LazyTreeNode) parent).getUserObject();
                            if (object instanceof RDFSNamedClass) {
                                RDFSNamedClass objectClass = (RDFSNamedClass) object;
                                RDFProperty predicate = objectClass.getOWLModel().getRDFSSubClassOfProperty();
                                Triple triple = new DefaultTriple(subjectClass, predicate, objectClass);
                                results.add(triple);
                            }
                        }
                    }
                }
            }
        }
        return results;
    }


    public void setSelectedTriples(Collection triples) {
        // TODO
    }


    public boolean displayHostResource(RDFResource resource) {

        boolean result = false;

        if (resource instanceof RDFSClass) {

            //@@TODO this is currently only implemented for the asserted superclasses

            if (!getSelection().contains(resource)) {

                Collection rootClses = getRoots();
                Collection allSupers = ((RDFSClass) resource).getSuperclasses(true);

                for (Iterator i = rootClses.iterator(); i.hasNext();) {
                    RDFSClass root = (RDFSClass) i.next();
                    if (allSupers.contains(root)) {
                        List objectPath = getPathToRoot((RDFSClass) resource, root, new LinkedList());
                        final TreePath path = ComponentUtilities.getTreePath(this, objectPath);
                        if (path != null) {
                            final WaitCursor cursor = new WaitCursor(this);
                            this.scrollPathToVisible(path);
                            this.setSelectionPath(path);
                            this.updateUI();
                            cursor.hide();
                            result = true;
                        }
                        else {
                            System.out.println("path = " + objectPath);
                        }
                    }
                }
            }
        }
        return result;
    }

    public Collection getRoots() {
        return (Collection) ((LazyTreeRoot) getModel().getRoot()).getUserObject();
    }

    private List getPathToRoot(RDFSClass cls, RDFSClass rootCls, LinkedList list) {
        list.add(0, cls);
        Collection superclasses = cls.getSuperclasses(false);
        for (Iterator it = superclasses.iterator(); it.hasNext();) {
            RDFSClass superclass = (RDFSClass) it.next();
            if (superclass.equals(rootCls)) {
                list.add(0, superclass);
                return list;
            }
            else if (cls.isVisible() && superclass instanceof OWLNamedClass) {
                getPathToRoot((OWLNamedClass) superclass, rootCls, list);
                break;
            }
        }
        return list;
    }
}
