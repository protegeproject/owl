package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.event.*;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.ui.LazyTreeNodeFrameComparator;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import java.util.*;

/**
 * Tree node that contains the superclass-subclass relations.
 *
 * @author Ray Fergerson <fergerson@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassTreeNode extends LazyTreeNode {

    private ClsListener _clsListener = new ClsAdapter() {
        public void directSubclassAdded(ClsEvent event) {
            if (event.getSubclass().isVisible()) {
                childAdded(event.getSubclass());
            }
        }


        public void directSubclassRemoved(ClsEvent event) {
            if (event.getSubclass().isVisible()) {
                childRemoved(event.getSubclass());
            }
        }


        public void directSubclassMoved(ClsEvent event) {
            Cls subclass = event.getSubclass();
            int index = (new ArrayList(getChildObjects())).indexOf(subclass);
            if (index != -1) {
                childRemoved(subclass);
                childAdded(subclass, index);
            }
        }


        public void directInstanceAdded(ClsEvent event) {
            notifyNodeChanged();
        }


        public void directInstanceRemoved(ClsEvent event) {
            notifyNodeChanged();
        }


        public void templateFacetValueChanged(ClsEvent event) {
            notifyNodeChanged();
        }


        public void directSuperclassAdded(ClsEvent event) {
            notifyNodeChanged();
        }
    };

    private FrameListener _frameListener = new FrameAdapter() {
        public void browserTextChanged(FrameEvent event) {
            notifyNodeChanged();
        }


        public void ownSlotValueChanged(FrameEvent event) {
            if (event.getSlot().getName().equals(Model.Slot.DIRECT_TYPES)) {
                // refresh the stale cls reference
                Cls cls = getCls().getKnowledgeBase().getCls(getCls().getName());
                reload(cls);
            }
            else {
                notifyNodeChanged();
            }
        }


        public void visibilityChanged(FrameEvent event) {
            notifyNodeChanged();
        }
    };


    public ClassTreeNode(LazyTreeNode parentNode, Cls parentCls) {
        super(parentNode, parentCls);
        parentCls.addClsListener(_clsListener);
        parentCls.addFrameListener(_frameListener);
    }


    protected LazyTreeNode createNode(Object o) {
        return new ClassTreeNode(this, (Cls) o);
    }


    protected void dispose() {
        super.dispose();
        getCls().removeClsListener(_clsListener);
        getCls().removeFrameListener(_frameListener);
    }


    protected int getChildObjectCount() {
        if(showHidden()) {
            return getCls().getDirectSubclassCount();
        }
        else {
            return getChildObjects().size();
        }
    }


    protected Collection getChildObjects() {
        if (showHidden()) {
            return getCls().getDirectSubclasses();
        }
        else {
            Cls cls = getCls();
            List result = new ArrayList(cls.getVisibleDirectSubclasses());
            // Collections.sort(result);
            // Remove all equivalent classes that have other superclasses as well
            if (cls instanceof OWLNamedClass) {
                Iterator equis = ((OWLNamedClass) cls).getEquivalentClasses().iterator();
                while (equis.hasNext()) {
                    RDFSClass equi = (RDFSClass) equis.next();
                    if (equi instanceof OWLNamedClass && equi.getSuperclassCount() > 1) {
                        result.remove(equi);
                    }
                }
            }
            return result;
        }
    }


    protected Cls getCls() {
        return (Cls) getUserObject();
    }


    protected Comparator getComparator() {
        return new LazyTreeNodeFrameComparator();
    }


    protected void notifyNodeChanged() {
        notifyNodeChanged(this);
    }


    private boolean showHidden() {
        return getCls().getProject().getDisplayHiddenClasses();
    }


    public String toString() {
        return "ParentChildNode(" + getCls() + ")";
    }
}
