package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

public class OwlSubclassFrameStore extends FrameStoreAdapter {
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
    private void updateRDFSSubClassOf(RDFSNamedClass cls) {
        Collection oldSuperclasses = new HashSet(super.getDirectOwnSlotValues(cls, rdfsSubClassOfProperty));
        Collection oldEquivalentClasses = new HashSet(super.getDirectOwnSlotValues(cls, owlEquivalentClassProperty));

        Collection newSuperclasses = new ArrayList();
        Collection newEquivalentClasses = new ArrayList();

        for (Iterator it = ((Cls) cls).getDirectSuperclasses().iterator(); it.hasNext();) {
            Cls superClass = (Cls) it.next();
            if (superClass instanceof RDFSClass) {
                if (superClass.hasDirectSuperclass(cls)) {  // is equivalent class
                    newEquivalentClasses.add(superClass);
                    if (!oldEquivalentClasses.contains(superClass)) {
                        TripleStore ts = TripleStoreUtil.getTripleStoreOf(cls, directSuperclassesSlot, superClass);
                        TripleStoreUtil.addToTripleStore(owlModel, ts, cls, owlEquivalentClassProperty, superClass);
                    }
                    if (superClass instanceof RDFSNamedClass) {
                        newSuperclasses.add(superClass);
                        if (!oldSuperclasses.contains(superClass)) {
                            TripleStore ts = TripleStoreUtil.getTripleStoreOf(cls, directSuperclassesSlot, superClass);
                            TripleStoreUtil.addToTripleStore(owlModel, ts, cls, rdfsSubClassOfProperty, superClass);
                        }
                    }
                }
                else {
                    newSuperclasses.add(superClass);
                    if (!oldSuperclasses.contains(superClass)) {
                        TripleStore ts = TripleStoreUtil.getTripleStoreOf(cls, directSuperclassesSlot, superClass);
                        TripleStoreUtil.addToTripleStore(owlModel, ts, cls, rdfsSubClassOfProperty, superClass);
                    }
                }
            }
        }

        // Remove all old values that are no longer needed
        oldSuperclasses.removeAll(newSuperclasses);
        for (Iterator it = oldSuperclasses.iterator(); it.hasNext();) {
            RDFSClass oldSuperclass = (RDFSClass) it.next();
            cls.removePropertyValue(rdfsSubClassOfProperty, oldSuperclass);
        }
        oldEquivalentClasses.removeAll(newEquivalentClasses);
        for (Iterator it = oldEquivalentClasses.iterator(); it.hasNext();) {
            RDFSClass oldEquivalentClass = (RDFSClass) it.next();
            cls.removePropertyValue(owlEquivalentClassProperty, oldEquivalentClass);
        }
    }
    
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
    public void addDirectSuperclass(Cls cls, Cls superCls) {
        if (!cls.hasDirectSuperclass(superCls)) {   // Disallow duplicates

            // log("-> " +cls.getBrowserText() + " ADDED " + superCls.getBrowserText());
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
