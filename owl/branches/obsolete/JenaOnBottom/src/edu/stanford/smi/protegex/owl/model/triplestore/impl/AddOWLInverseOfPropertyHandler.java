package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class AddOWLInverseOfPropertyHandler extends AbstractAddPropertyValueHandler {

    private Slot inverseOfProperty;


    AddOWLInverseOfPropertyHandler(ProtegeTripleAdder adder, KnowledgeBase kb) {
        super(adder);
        this.inverseOfProperty = kb.getSlot(OWLNames.Slot.INVERSE_OF);
    }


    public void handleAdd(RDFResource subject, Object object) {
        // Also add inverse direction
        adder.addValue((Instance) object, inverseOfProperty, subject);
    }
}
