package edu.stanford.smi.protegex.owl.model.framestore;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.Transaction;
import edu.stanford.smi.protege.model.framestore.DeleteSimplificationFrameStore;
import edu.stanford.smi.protege.util.transaction.TransactionMonitor;

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
        	//TT: This is a little bit cheating, but harmless...
            beginTransaction("Remove template slot from class " + cls + Transaction.APPLY_TO_TRAILER_STRING +
            		(cls == null ? null : cls.getName()));
            getDelegate().removeDirectTemplateSlot(cls, slot);
            commitTransaction();
        }
    }
}
