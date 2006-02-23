package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.ui.LazyTreeNodeFrameComparator;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         23-Feb-2006
 */
public class HiddenClassTreeRoot extends LazyTreeRoot {

    public HiddenClassTreeRoot(Cls root) {
        super(root);
    }

    public HiddenClassTreeRoot(Collection roots) {
        super(filter(roots));
    }

    public LazyTreeNode createNode(Object o) {
        return new HiddenClassTreeNode(this, (Cls) o);
    }

    protected static Collection filter(Collection roots) {
        Collection visibleRoots = new ArrayList(roots);
        Cls firstRoot = (Cls) CollectionUtilities.getFirstItem(roots);
        if (firstRoot != null && !firstRoot.getProject().getDisplayHiddenClasses()) {
            Iterator i = visibleRoots.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                if (cls.isVisible()) {
                    i.remove();
                }
            }
        }
        return visibleRoots;
    }

    public Comparator getComparator() {
        return new LazyTreeNodeFrameComparator();
    }
}
