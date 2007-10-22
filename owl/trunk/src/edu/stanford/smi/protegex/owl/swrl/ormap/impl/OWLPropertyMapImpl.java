
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;

public abstract class OWLPropertyMapImpl implements OWLPropertyMap
{
  private OWLProperty owlProperty;
  private PrimaryKey primaryKey;

  public OWLPropertyMapImpl(OWLProperty owlProperty, PrimaryKey primaryKey)
  {
    this.owlProperty = owlProperty;
    this.primaryKey = primaryKey;
  } // OWLPropertyMapImpl

  public OWLProperty getProperty() { return owlProperty; }
  public PrimaryKey getPrimaryKey() { return primaryKey; }
} // OWLPropertyMapImpl
