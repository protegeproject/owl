package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
interface AddPropertyValueHandler {

    void handleAdd(RDFResource subject, Object object);
}
