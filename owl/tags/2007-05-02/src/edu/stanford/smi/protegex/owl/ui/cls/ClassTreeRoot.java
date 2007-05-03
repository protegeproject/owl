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
 * Tree Root for the superclass-subclass relationship
 *
 * @author    Ray Fergerson <fergerson@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassTreeRoot extends LazyTreeRoot {

    public ClassTreeRoot(Cls root) {
        super(root);
    }

    public ClassTreeRoot(Collection roots) {
        super(filter(roots));
    }

    public LazyTreeNode createNode(Object o) {
        return new ClassTreeNode(this, (Cls) o);
    }

    private static Collection filter(Collection roots) {
        Collection visibleRoots = new ArrayList(roots);
        Cls firstRoot = (Cls) CollectionUtilities.getFirstItem(roots);
        if (firstRoot != null && !firstRoot.getProject().getDisplayHiddenClasses()) {
            Iterator i = visibleRoots.iterator();
            while (i.hasNext()) {
                Cls cls = (Cls) i.next();
                if (!cls.isVisible()) {
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
