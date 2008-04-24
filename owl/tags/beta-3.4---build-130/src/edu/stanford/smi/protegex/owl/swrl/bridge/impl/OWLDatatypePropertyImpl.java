
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;

public  class OWLDatatypePropertyImpl extends OWLPropertyImpl implements OWLDatatypeProperty
{
  public OWLDatatypePropertyImpl(OWLModel owlModel, String propertyName) throws OWLFactoryException { super(owlModel, propertyName); }
  public OWLDatatypePropertyImpl(String propertyName) { super(propertyName); }
} // OWLDatatypePropertyImpl
