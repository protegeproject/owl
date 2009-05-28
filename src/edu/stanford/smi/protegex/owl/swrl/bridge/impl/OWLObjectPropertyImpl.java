
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;

public  class OWLObjectPropertyImpl extends OWLPropertyImpl implements OWLObjectProperty
{
  public OWLObjectPropertyImpl(String propertyURI) { super(propertyURI); }
  public OWLObjectPropertyImpl(String propertyURI, String prefixedPropertyName) { super(propertyURI, prefixedPropertyName); }
} // OWLObjectPropertyImpl
