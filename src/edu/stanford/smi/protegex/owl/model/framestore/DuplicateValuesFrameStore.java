package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protege.util.Log;

public class DuplicateValuesFrameStore extends FrameStoreAdapter {
    private static final transient Logger log = Log.getLogger(DuplicateValuesFrameStore.class);

    /*
     * FrameStore implementation
     */

    @Override
    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        final int valueCount = values.size();

        if (valueCount > 1 &&
            valueCount != new HashSet(values).size()) {
            log.warning("[OWLFrameStore] Warning: Attempted to assign duplicate value to " +
                               frame.getBrowserText() + "." + slot.getBrowserText());
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object o = it.next();
                log.warning("[OWLFrameStore]  - " + o);
            }
            values = new HashSet(values);
        }
        super.setDirectOwnSlotValues(frame, slot, values);
    }

}
