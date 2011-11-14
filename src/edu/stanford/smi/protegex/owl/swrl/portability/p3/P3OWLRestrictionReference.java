
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLRestrictionReference;

public abstract class P3OWLRestrictionReference implements OWLRestrictionReference
{
  private OWLClassReference owlClass;
  private OWLPropertyReference onProperty;

  public P3OWLRestrictionReference(OWLClassReference owlClass, OWLPropertyReference onProperty)
  {
    this.owlClass = owlClass;
    this.onProperty = onProperty;
  } 

  public OWLClassReference asOWLClass() { return owlClass; }
  public OWLPropertyReference getProperty() { return onProperty; }
} 
