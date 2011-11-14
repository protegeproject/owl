
package edu.stanford.smi.protegex.owl.swrl.ddm.impl;

import edu.stanford.smi.protegex.owl.swrl.ddm.*;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;


public class OWLClassMapImpl implements OWLClassMap
{
  private OWLClassReference owlClass;
  private PrimaryKey primaryKey;

  public OWLClassMapImpl(OWLClassReference owlClass, PrimaryKey primaryKey)
  {
    this.owlClass = owlClass;
    this.primaryKey = primaryKey;
  } // OWLClassMapImpl

  public OWLClassReference getOWLClass() { return owlClass; }
  public PrimaryKey getPrimaryKey() { return primaryKey; }
} // OWLClassMapImpl
