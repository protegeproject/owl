package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class RDFSNamedClassPostProcessor {

    private KnowledgeBase kb;

    private OWLModel owlModel;


    RDFSNamedClassPostProcessor(OWLModel owlModel) {
        this.owlModel = owlModel;
        this.kb = owlModel;

        RDFProperty owlOneOfProperty = owlModel.getOWLOneOfProperty();

        if (owlModel.getOWLFrameStore() != null) {
          owlModel.getOWLFrameStore().setSuperclassSynchronizationBlocked(true);
        }
        Collection clses = owlModel.getUserDefinedRDFSNamedClasses();
        Iterator it = clses.iterator();
        while (it.hasNext()) {
            RDFSNamedClass cls = (RDFSNamedClass) it.next();
            if (cls.getPropertyValues(owlOneOfProperty).size() == 1) {
                convertToOWLEnumeratedClass(cls);
            }
            else {
                updateEquivalentClasses(cls);
                updateDirectSuperclasses(cls);
            }
        }
        if (owlModel.getOWLFrameStore() != null) {
          owlModel.getOWLFrameStore().setSuperclassSynchronizationBlocked(false);
        }
        if (((AbstractOWLModel) owlModel).isProtegeMetaOntologyImported()) {
            updateProtegeFeatures(clses);
        }
    }


    private void removeDuplicateSuperclasses(RDFSNamedClass cls) {
        List superclasses = new ArrayList(cls.getPureSuperclasses());
        for (int i = 0; i < superclasses.size() - 1; i++) {
            if (superclasses.get(i) instanceof OWLAnonymousClass) {
                OWLAnonymousClass anon = (OWLAnonymousClass) superclasses.get(i);
                for (int j = superclasses.size() - 1; j > i; j--) {
                    if (superclasses.get(j).getClass() == anon.getClass()) {
                        String browserText = ((Cls) superclasses.get(i)).getBrowserText();
                        OWLAnonymousClass other = (OWLAnonymousClass) superclasses.get(j);
                        if (other.getBrowserText().equals(browserText)) {
                            ((Cls) cls).removeDirectSuperclass(other);
                        }
                    }
                }
            }
        }
    }


    private void convertToOWLEnumeratedClass(Cls cls) {
        // Unfortunately this can only be done in a post-processor because owl:oneOf
        // is also used in datatype enumerations
        TripleStoreUtil.ensureActiveTripleStore((RDFResource) cls);
        cls.setDirectType(kb.getCls(OWLNames.Cls.ENUMERATED_CLASS));
        cls.setDirectOwnSlotValue(owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
    }


    private void updateDirectSuperclasses(Cls cls) {
        Collection supis = cls.getDirectSuperclasses();
        for (Iterator it = supis.iterator(); it.hasNext();) {
            Object next = it.next();
            if (!(next instanceof Cls)) {
                throw new RuntimeException("Illegal superclass " + next + " of " + cls.getName() + " has type " + next.getClass());
            }
            Cls other = (Cls) next;
            if (other instanceof RDFSNamedClass && !other.hasDirectSuperclass(cls)) {
                return;
            }
        }
        TripleStoreUtil.ensureActiveTripleStore((RDFResource) cls);
        cls.addDirectSuperclass(kb.getRootCls());
    }


    private void updateEquivalentClasses(Cls cls) {
        Collection equis = cls.getDirectOwnSlotValues(owlModel.getOWLEquivalentClassProperty());
        for (Iterator it = equis.iterator(); it.hasNext();) {
            Cls equiCls = (Cls) it.next();
            TripleStoreUtil.ensureActiveTripleStore((RDFResource) cls);
            cls.addDirectSuperclass(equiCls);
            equiCls.addDirectSuperclass(cls);
        }
    }


    private void updateProtegeFeatures(Collection clses) {
        Slot abstractSlot = owlModel.getRDFProperty(ProtegeNames.Slot.ABSTRACT);
        for (Iterator it = clses.iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();                  
           if (abstractSlot != null && Boolean.TRUE.equals(cls.getDirectOwnSlotValue(abstractSlot))) {
                TripleStoreUtil.ensureActiveTripleStore((RDFResource) cls);
                cls.setAbstract(true);
            }            
        }
    }
}
