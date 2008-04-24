package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

import java.util.Collections;
import java.util.Iterator;

/**
 * Makes sure that all frames that have a value for rdf:first also have at least one type.
 * This is needed to add the type rdf:List to those list instances which don't have an explicit
 * type triple.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFListPostProcessor {

    public RDFListPostProcessor(OWLModel owlModel) {
        Iterator tripleStores = owlModel.getTripleStoreModel().listUserTripleStores();
        while (tripleStores.hasNext()) {
            TripleStore tripleStore = (TripleStore) tripleStores.next();
            process(owlModel, tripleStore);
        }
    }


    private void process(OWLModel owlModel, TripleStore tripleStore) {
        Slot directTypesSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_TYPES);
        Slot directInstancesSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_INSTANCES);
        RDFProperty rdfTypeProperty = owlModel.getRDFTypeProperty();
        owlModel.getTripleStoreModel().setActiveTripleStore(tripleStore);
        RDFSNamedClass rdfListClass = owlModel.getRDFListClass();
        RDFProperty rdfFirstProperty = owlModel.getRDFFirstProperty();
        Iterator subjects = tripleStore.listSubjects(rdfFirstProperty);
        while (subjects.hasNext()) {
            RDFResource subject = (RDFResource) subjects.next();
            if (((Instance) subject).getDirectOwnSlotValues(directTypesSlot).size() == 0) {
                tripleStore.getNarrowFrameStore().addValues(subject, directTypesSlot, null, false, Collections.singleton(rdfListClass));
                tripleStore.getNarrowFrameStore().addValues(rdfListClass, directInstancesSlot, null, false, Collections.singleton(subject));
                tripleStore.add(subject, rdfTypeProperty, rdfListClass);
                if (!(subject instanceof RDFList)) {
                    OWLJavaFactoryUpdater.run(subject);
                }
            }
        }
    }
}
