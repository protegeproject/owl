
package edu.stanford.smi.protegex.owl.swrl.ormap.impl;

import edu.stanford.smi.protegex.owl.swrl.ormap.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLClass;

public class OWLClassMapImpl implements OWLClassMap
{
  private OWLClass owlClass;
  private PrimaryKey primaryKey;

  public OWLClassMapImpl(OWLClass owlClass, PrimaryKey primaryKey)
  {
    this.owlClass = owlClass;
    this.primaryKey = primaryKey;
  } // OWLClassMapImpl

  public OWLClass getOWLClass() { return owlClass; }
  public PrimaryKey getPrimaryKey() { return primaryKey; }
} // OWLClassMapImpl
