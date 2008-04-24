package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ProtegeTripleAdder {

    boolean addValue(Instance subject, Slot slot, Object object);


    void addValueFast(Instance subject, Slot slot, Object object);


    Collection getSlotValues(Instance instance, Slot slot);
}
