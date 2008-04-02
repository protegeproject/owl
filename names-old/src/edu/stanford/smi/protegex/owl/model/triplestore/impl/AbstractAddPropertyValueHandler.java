package edu.stanford.smi.protegex.owl.model.triplestore.impl;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
abstract class AbstractAddPropertyValueHandler implements AddPropertyValueHandler {

    protected ProtegeTripleAdder adder;


    protected AbstractAddPropertyValueHandler(ProtegeTripleAdder adder) {
        this.adder = adder;
    }
}
