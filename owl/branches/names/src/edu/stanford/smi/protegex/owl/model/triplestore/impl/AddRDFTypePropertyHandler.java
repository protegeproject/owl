package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import java.util.Collection;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

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
        directInstancesSlot = kb.getSystemFrames().getDirectInstancesSlot();
        directTypesSlot = kb.getSystemFrames().getDirectTypesSlot();
        owlClassClass = ((OWLModel) kb).getSystemFrames().getOwlNamedClassClass();
        untypedResourceClass = ((OWLModel) kb).getSystemFrames().getRdfExternalResourceClass();
        this.tripleStoreModel = tripleStoreModel;
        this.tripleStore = tripleStore;
    }



    public void handleAdd(RDFResource subject, Object object) {
        // Don't add owl:Class if this already has a type (e.g., :OWL-COMPLEMENT-CLASS)
        
        Collection oldValues = adder.getSlotValues(subject, directTypesSlot);

        if (oldValues.isEmpty()) {
            tripleStoreModel.setHomeTripleStore(subject, tripleStore);
        }
        
        if (!object.equals(owlClassClass)) {
            if (adder.addValue(subject, directTypesSlot, object)) {
                adder.addValueFast((Instance) object, directInstancesSlot, subject);
            }
        } else if (oldValues.isEmpty()) {
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
