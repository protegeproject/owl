
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;

public  class OWLObjectPropertyImpl extends OWLPropertyImpl implements OWLObjectProperty
{
  public OWLObjectPropertyImpl(OWLModel owlModel, String propertyName) throws OWLFactoryException { super(owlModel, propertyName); }
  public OWLObjectPropertyImpl(String propertyName) { super(propertyName); }
} // OWLObjectPropertyImpl
