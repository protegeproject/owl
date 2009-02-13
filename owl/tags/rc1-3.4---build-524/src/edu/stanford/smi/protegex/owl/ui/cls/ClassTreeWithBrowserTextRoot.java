package edu.stanford.smi.protegex.owl.ui.cls;

import java.util.Collection;
import java.util.Comparator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protege.util.LazyTreeRoot;

public class ClassTreeWithBrowserTextRoot extends LazyTreeRoot {

    public ClassTreeWithBrowserTextRoot(Collection roots, boolean isSorted) {
        super(roots, isSorted);
    }
    
    public ClassTreeWithBrowserTextRoot(Cls rootCls, boolean isSorted) {
        super(new FrameWithBrowserText(rootCls, rootCls.getBrowserText(), rootCls.getDirectTypes()), isSorted);
    }

    @Override
    public LazyTreeNode createNode(Object o) {
        return new ClassTreeWithBrowserTextNode(this, (FrameWithBrowserText) o);
    }

    @Override
    protected Comparator getComparator() {
        // TODO Auto-generated method stub
        return null;
    }   

}
