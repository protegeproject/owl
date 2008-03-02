package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

public class OwlSubclassFrameStore extends FrameStoreAdapter {
    private static transient Logger log = Log.getLogger(OwlSubclassFrameStore.class);
    
    private OWLModel owlModel;
    
    private Slot directSuperclassesSlot;
    private RDFProperty rdfsSubClassOfProperty;
    private RDFProperty owlEquivalentClassProperty;
    
    public OwlSubclassFrameStore(OWLModel owlModel) {
        this.owlModel = owlModel;
        directSuperclassesSlot =owlModel.getSystemFrames().getDirectSuperclassesSlot();
        rdfsSubClassOfProperty = owlModel.getRDFSSubClassOfProperty();
        owlEquivalentClassProperty = owlModel.getOWLEquivalentClassProperty();
    }


    private void addNamedOperandsToDirectSuperclasses(OWLNamedClass cls, OWLIntersectionClass superCls) {
        for (Iterator it = superCls.getOperands().iterator(); it.hasNext();) {
            RDFSClass operand = (RDFSClass) it.next();
            if (operand instanceof OWLNamedClass) {
                cls.addSuperclass(operand);
            }
        }
    }
    
    /**
     * Updates the values of rdfs:subClassOf (and owl:equivalentClass) in response
     * to changes in the :SLOT-DIRECT-SUPERCLASSES.
     *
     * @param cls the RDFSClass that has changed its superclasses
     */
    @SuppressWarnings("unchecked")
    private void updateRDFSSubClassOf(RDFSNamedClass cls) {
        Collection<RDFSClass> newSuperclasses = new ArrayList<RDFSClass>();
        Collection<RDFSClass> newEquivalentClasses = new ArrayList<RDFSClass>();

        for (Cls superClass : super.getDirectSuperclasses(cls)) {
            if (superClass instanceof RDFSClass) {
                if (super.getDirectSuperclasses(superClass).contains(cls)) {  // is equivalent class
                    newEquivalentClasses.add((RDFSClass) superClass);
                }
                newSuperclasses.add((RDFSClass) superClass);
            }
        }
        super.setDirectOwnSlotValues(cls, rdfsSubClassOfProperty, newSuperclasses);
        super.setDirectOwnSlotValues(cls, owlEquivalentClassProperty, newEquivalentClasses);
    }
    
    @SuppressWarnings("unchecked")
    private void removeNamedOperandsFromDirectSuperclasses(OWLNamedClass cls,
                                                           OWLIntersectionClass intersectionCls,
                                                           Slot slot) {
        Collection toRemove = intersectionCls.getNamedOperands();
        if (!toRemove.isEmpty()) {
            for (Iterator it = ((Cls) cls).getDirectOwnSlotValues(slot).iterator(); it.hasNext();) {
                RDFSClass superClass = (RDFSClass) it.next();
                if (superClass instanceof OWLIntersectionClass) {
                    toRemove.removeAll(((OWLIntersectionClass) superClass).getNamedOperands());
                }
            }
            for (Iterator it = toRemove.iterator(); it.hasNext();) {
                RDFSNamedClass namedCls = (RDFSNamedClass) it.next();
                if (!namedCls.hasEquivalentClass(cls)) {
                    cls.removeSuperclass(namedCls);
                }
            }
        }
    }

    
    /*
     * Frame Store implementation
     */
    
    @Override
    public Cls createCls(FrameID id, Collection directTypes, Collection directSuperclasses, boolean loadDefaults) {
        
        Cls cls = super.createCls(id, directTypes, directSuperclasses, loadDefaults);
        if (cls instanceof RDFSNamedClass) {
            super.setDirectOwnSlotValues(cls, owlModel.getRDFSSubClassOfProperty(), directSuperclasses);
        }
        return cls;
    }

    @Override
    public void addDirectSuperclass(Cls cls, Cls superCls) {
        Collection<Cls> superClasses = super.getDirectSuperclasses(cls);
        if (!superClasses.contains(superCls)) {   // Disallow duplicates
            if (superClasses.contains(owlModel.getOWLThingClass()) && superCls instanceof RDFSClass &&
                    !((RDFSClass) superCls).isAnonymous()) {
                super.removeDirectSuperclass(cls, owlModel.getOWLThingClass());
            }

            if (log.isLoggable(Level.FINE)) {
                log.fine("-> " +cls.getBrowserText() + " ADDED " + superCls.getBrowserText());
            }
            super.addDirectSuperclass(cls, superCls);
            if (superCls instanceof OWLIntersectionClass &&
                cls instanceof OWLNamedClass &&
                superCls.hasDirectSuperclass(cls)) {
                addNamedOperandsToDirectSuperclasses((OWLNamedClass) cls, (OWLIntersectionClass) superCls);
            }
            else if (cls instanceof OWLIntersectionClass &&
                     superCls instanceof OWLNamedClass &&
                     superCls.hasDirectSuperclass(cls)) {
                addNamedOperandsToDirectSuperclasses((OWLNamedClass) superCls, (OWLIntersectionClass) cls);
            }

            if (cls instanceof OWLNamedClass &&
                superCls instanceof OWLNamedClass &&
                cls.isEditable() &&
                ((OWLNamedClass) superCls).getSubclassesDisjoint()) {
                OWLUtil.ensureSubclassesDisjoint((OWLNamedClass) superCls);
            }

            if (cls instanceof RDFSNamedClass) {
                updateRDFSSubClassOf((RDFSNamedClass) cls);
            }
            if (superCls instanceof RDFSNamedClass) {
                updateRDFSSubClassOf((RDFSNamedClass) superCls);
            }
        }
    }
    
    @Override
    public void removeDirectSuperclass(Cls cls, Cls superCls) {

        boolean wasEquivalentCls = superCls.hasDirectSuperclass(cls);

        // log("-> " +cls.getBrowserText() + " REMOVED " + superCls.getBrowserText());
        super.removeDirectSuperclass(cls, superCls);

        if (cls instanceof OWLNamedClass && superCls instanceof OWLIntersectionClass && wasEquivalentCls) {
            removeNamedOperandsFromDirectSuperclasses((OWLNamedClass) cls,
                                                      (OWLIntersectionClass) superCls, directSuperclassesSlot);
        }
        else if (superCls instanceof OWLNamedClass && cls instanceof OWLIntersectionClass && wasEquivalentCls) {
            removeNamedOperandsFromDirectSuperclasses((OWLNamedClass) superCls,
                                                      (OWLIntersectionClass) cls, directSuperclassesSlot);
        }

        if (cls instanceof RDFSNamedClass) {
            updateRDFSSubClassOf((RDFSNamedClass) cls);
        }
        else if (superCls instanceof RDFSNamedClass) {
            updateRDFSSubClassOf((RDFSNamedClass) superCls);
        }
    }

}
