package edu.stanford.smi.protegex.owl.model.framestore.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;

public abstract class AbstractRestrictionUpdater implements RestrictionUpdater {
    private final static transient Logger log = Log.getLogger(AbstractRestrictionUpdater.class);

    /**
     * The OWLModel this operates on
     */
    protected OWLModel owlModel;


    public AbstractRestrictionUpdater(OWLModel owlModel) {
        this.owlModel = owlModel;
    }


    protected Cls getDirectRestriction(Cls cls, Slot slot, Class metaClass) {
        for (Iterator it = cls.getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (metaClass.isAssignableFrom(superCls.getClass())) {
                final Slot restrictedSlot = ((OWLRestriction) superCls).getOnProperty();
                if (slot.equals(restrictedSlot)) {
                    return superCls;
                }
            }
        }
        return null;
    }


    protected Collection getDirectRestrictions(Cls cls, Slot slot, Class metaClass) {
        Collection result = new ArrayList();
        for (Iterator it = cls.getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (metaClass.isAssignableFrom(superCls.getClass())) {
                Slot restrictedSlot = ((OWLRestriction) superCls).getOnProperty();
                if (restrictedSlot != null && restrictedSlot.equals(slot)) {
                    result.add(superCls);
                }
            }
        }
        return result;
    }


    protected void removeRestrictions(Cls cls, Slot slot, Cls metaCls) {
        Collection copy = new ArrayList(cls.getDirectSuperclasses());
        for (Iterator it = copy.iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls.getDirectType().equals(metaCls)) {
                OWLRestriction restriction = (OWLRestriction) superCls;
                Slot restrictedSlot = restriction.getOnProperty();
                if (restrictedSlot.equals(slot)) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("- OWLRestriction " + restriction.getBrowserText() + " from " + cls.getName() + "." + slot.getName());
                    }
                    cls.removeDirectSuperclass(restriction);
                }
            }
        }
    }
}
