package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class AddRDFSSubClassOfPropertyHandler extends AbstractAddPropertyValueHandler {

    private Slot directSubClassesSlot;

    private Slot directSuperClassesSlot;


    AddRDFSSubClassOfPropertyHandler(ProtegeTripleAdder adder, KnowledgeBase kb) {
        super(adder);
        directSubClassesSlot = kb.getSlot(Model.Slot.DIRECT_SUBCLASSES);
        directSuperClassesSlot = kb.getSlot(Model.Slot.DIRECT_SUPERCLASSES);
    }


    public void handleAdd(RDFResource subject, Object object) {
        if (adder.addValue(subject, directSuperClassesSlot, object)) {
            adder.addValueFast((Instance) object, directSubClassesSlot, subject);
        }
    }
}
