package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class AddRDFTypePropertyHandler extends AbstractAddPropertyValueHandler {

    private Slot directInstancesSlot;

    private Slot directTypesSlot;

    private Cls owlClassClass;

    private TripleStore tripleStore;

    private TripleStoreModel tripleStoreModel;

    private Cls untypedResourceClass;


    AddRDFTypePropertyHandler(ProtegeTripleAdder adder, KnowledgeBase kb, TripleStoreModel tripleStoreModel, TripleStore tripleStore) {
        super(adder);
        directInstancesSlot = kb.getSlot(Model.Slot.DIRECT_INSTANCES);
        directTypesSlot = kb.getSlot(Model.Slot.DIRECT_TYPES);
        owlClassClass = kb.getCls(OWLNames.Cls.NAMED_CLASS);
        untypedResourceClass = kb.getCls(RDFNames.Cls.EXTERNAL_RESOURCE);
        this.tripleStoreModel = tripleStoreModel;
        this.tripleStore = tripleStore;
    }


    public void handleAdd(RDFResource subject, Object object) {
        // Don't add owl:Class if this already has a type (e.g., :OWL-COMPLEMENT-CLASS)
        // System.out.println("Adding " + object + " to types of " + subject);
        Collection oldValues = adder.getSlotValues(subject, directTypesSlot);
        if (oldValues.isEmpty()) {
            tripleStoreModel.setHomeTripleStore(subject, tripleStore);
        }
        if (!object.equals(owlClassClass)) {
            if (adder.addValue(subject, directTypesSlot, object)) {
                adder.addValueFast((Instance) object, directInstancesSlot, subject);
            }
        }
        else if (oldValues.isEmpty()) {
            if (adder.addValue(subject, directTypesSlot, object)) {
                adder.addValueFast((Instance) object, directInstancesSlot, subject);
            }
        }
        if (oldValues.size() == 1 && oldValues.contains(untypedResourceClass)) {
            ((Instance) subject).removeDirectType(untypedResourceClass);
            tripleStoreModel.setHomeTripleStore(subject, tripleStore);
        }
    }
}
