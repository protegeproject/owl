package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;

public class DuplicateValuesFrameStore extends FrameStoreAdapter {

    /*
     * FrameStore implementation
     */

    @Override
    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        final int valueCount = values.size();

        if (valueCount > 1 &&
            valueCount != new HashSet(values).size()) {
            System.err.println("[OWLFrameStore] Warning: Attempted to assign duplicate value to " +
                               frame.getBrowserText() + "." + slot.getBrowserText());
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object o = it.next();
                System.err.println("[OWLFrameStore]  - " + o);
            }
            values = new HashSet(values);
        }
        super.setDirectOwnSlotValues(frame, slot, values);
    }

}
