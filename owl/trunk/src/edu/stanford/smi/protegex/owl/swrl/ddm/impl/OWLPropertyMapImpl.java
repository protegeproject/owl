
package edu.stanford.smi.protegex.owl.swrl.ddm.impl;

import edu.stanford.smi.protegex.owl.swrl.ddm.*;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;


public abstract class OWLPropertyMapImpl implements OWLPropertyMap
{
  private OWLPropertyReference owlProperty;
  private PrimaryKey primaryKey;

  public OWLPropertyMapImpl(OWLPropertyReference owlProperty, PrimaryKey primaryKey)
  {
    this.owlProperty = owlProperty;
    this.primaryKey = primaryKey;
  } // OWLPropertyMapImpl

  public OWLPropertyReference getProperty() { return owlProperty; }
  public PrimaryKey getPrimaryKey() { return primaryKey; }
} // OWLPropertyMapImpl
