
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataProperty;

public  class OWLDataPropertyImpl extends OWLPropertyImpl implements OWLDataProperty
{
  public OWLDataPropertyImpl(String propertyURI) { super(propertyURI); }
  public OWLDataPropertyImpl(String propertyURI, String prefixedPropertyName) { super(propertyURI, prefixedPropertyName); }

} // OWLDatatypePropertyImpl
