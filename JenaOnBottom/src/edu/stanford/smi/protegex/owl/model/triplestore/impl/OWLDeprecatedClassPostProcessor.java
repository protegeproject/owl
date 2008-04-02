package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class OWLDeprecatedClassPostProcessor {

    private KnowledgeBase kb;

    private OWLModel owlModel;


    OWLDeprecatedClassPostProcessor(OWLModel owlModel) {
        this.owlModel = owlModel;
        this.kb = owlModel;

        updateDeprecatedClasses();
    }


    /**
     * Makes sure that deprecated classes have at least one additional type, and that the
     * type owl:DeprecatedClass is the last one.
     */
    private void updateDeprecatedClasses() {
        Slot directTypesSlot = kb.getSlot(Model.Slot.DIRECT_TYPES);
        Slot directInstancesSlot = kb.getSlot(Model.Slot.DIRECT_INSTANCES);
        RDFProperty rdfTypeProperty = owlModel.getRDFTypeProperty();
        RDFSNamedClass metaclass = owlModel.getRDFSNamedClass(OWLNames.Cls.DEPRECATED_CLASS);
        TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
        Iterator tripleStores = tripleStoreModel.listUserTripleStores();
        while (tripleStores.hasNext()) {
            TripleStore tripleStore = (TripleStore) tripleStores.next();
            NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
            Set clses = nfs.getFrames(rdfTypeProperty, null, false, metaclass);
            Iterator subjects = clses.iterator();
            while (subjects.hasNext()) {
                Cls cls = (Cls) subjects.next();
                if (cls.getDirectTypes().size() == 1) {
                    RDFSNamedClass extraMetaclass = owlModel.getOWLNamedClassClass();
                    if (!nfs.getValues(cls, rdfTypeProperty, null, false).contains(extraMetaclass)) {
                        nfs.addValues(cls, rdfTypeProperty, null, false, Collections.singleton(extraMetaclass));
                    }
                    nfs.addValues(cls, directTypesSlot, null, false, Collections.singleton(extraMetaclass));
                    nfs.addValues(extraMetaclass, directInstancesSlot, null, false, Collections.singleton(cls));
                }

                nfs.removeValue(cls, directTypesSlot, null, false, metaclass);
                nfs.removeValue(metaclass, directInstancesSlot, null, false, cls);

                nfs.addValues(cls, directTypesSlot, null, false, Collections.singleton(metaclass));
                nfs.addValues(metaclass, directInstancesSlot, null, false, Collections.singleton(cls));
            }
            OWLJavaFactoryUpdater.run(owlModel, clses);
        }
    }
}
