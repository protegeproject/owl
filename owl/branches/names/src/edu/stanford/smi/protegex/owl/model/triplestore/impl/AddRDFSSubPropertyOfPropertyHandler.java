package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class AddRDFSSubPropertyOfPropertyHandler extends AbstractAddPropertyValueHandler {

    private Slot directSubSlotsSlot;


    AddRDFSSubPropertyOfPropertyHandler(ProtegeTripleAdder adder, KnowledgeBase kb) {
        super(adder);
        directSubSlotsSlot = kb.getSlot(Model.Slot.DIRECT_SUBSLOTS);
    }


    public void handleAdd(RDFResource subject, Object object) {
        adder.addValue((Instance) object, directSubSlotsSlot, subject);
    }
}
