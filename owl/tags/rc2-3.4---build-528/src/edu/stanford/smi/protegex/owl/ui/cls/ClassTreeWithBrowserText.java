package edu.stanford.smi.protegex.owl.ui.cls;

import java.util.Collection;
import java.util.TreeSet;

import javax.swing.Action;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.FrameWithBrowserText;
import edu.stanford.smi.protege.util.LazyTreeRoot;

public class ClassTreeWithBrowserText extends ClassTree {
    private static final long serialVersionUID = -841459836070720483L;
    
    public ClassTreeWithBrowserText(Action doubleClickAction, LazyTreeRoot root) {
        super(doubleClickAction, root);
    }

    /*
     * Get selection as a collection of Cls
     */
    @Override
    public Collection getSelection() {
        Collection<Cls> selectedClses = new TreeSet<Cls>();
        Collection sel = ComponentUtilities.getSelection(this);
        for (Object object : sel) {
            if (object instanceof FrameWithBrowserText) {                
                FrameWithBrowserText fbt = (FrameWithBrowserText)object;
                Frame frame = fbt.getFrame();
                if (frame instanceof Cls) {
                    selectedClses.add((Cls) frame);
                }//TODO: what to do about the other ones?
            }
        }
        return selectedClses;
    }

}
