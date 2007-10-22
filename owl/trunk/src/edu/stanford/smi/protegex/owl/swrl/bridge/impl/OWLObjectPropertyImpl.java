
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.io.Serializable;

public  class OWLObjectPropertyImpl extends OWLPropertyImpl implements OWLObjectProperty, Serializable
{
  public OWLObjectPropertyImpl(String propertyName, Set<String> domainClassNames, 
                               Set<String> rangeClassNames, Set<String> superPropertyNames, Set<String> subPropertyNames,
                               Set<String> equivalentPropertyNames) 
    throws SWRLRuleEngineBridgeException
  {
    super(propertyName, domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames, equivalentPropertyNames);
  } // OWLObjectPropertyImpl
  
  public OWLObjectPropertyImpl(String propertyName) { super(propertyName); }
} // OWLObjectPropertyImpl
