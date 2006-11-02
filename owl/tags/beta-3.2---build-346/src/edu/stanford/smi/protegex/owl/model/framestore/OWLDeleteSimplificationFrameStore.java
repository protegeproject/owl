package edu.stanford.smi.protegex.owl.model.framestore;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.DeleteSimplificationFrameStore;

/**
 * A modified DeleteSimplificationFrameStore that does not automatically delete
 * own slot values if the domain of a slot has changed.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDeleteSimplificationFrameStore extends DeleteSimplificationFrameStore {

    public void removeDirectTemplateSlot(Cls cls, Slot slot) {
        if (slot.isBeingDeleted()) {
            super.removeDirectTemplateSlot(cls, slot);
        }
        else {
            beginTransaction("Remove template slot");
            getDelegate().removeDirectTemplateSlot(cls, slot);
            commitTransaction();
        }
    }
}
