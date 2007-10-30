
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.io.Serializable;

public  class OWLDatatypePropertyImpl extends OWLPropertyImpl implements OWLDatatypeProperty, Serializable
{
  public OWLDatatypePropertyImpl(String propertyName, Set<String> domainClassNames, 
                                 Set<String> rangeClassNames, Set<String> superPropertyNames, Set<String> subPropertyNames,
                                 Set<String> equivalentPropertyNames) 
    throws OWLFactoryException
  {
    super(propertyName, domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames, equivalentPropertyNames);
  } // OWLDatatypePropertyImpl
  
  public OWLDatatypePropertyImpl(String propertyName) { super(propertyName); }
} // OWLDatatypePropertyImpl
