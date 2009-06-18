
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class OWLObjectPropertyAssertionAxiomImpl extends OWLPropertyAssertionAxiomImpl implements OWLObjectPropertyAssertionAxiom
{
  private OWLIndividual object;

  public OWLObjectPropertyAssertionAxiomImpl(OWLIndividual subject, OWLProperty property, OWLIndividual object)
  {
    super(subject, property);
    this.object = object;
  } // OWLObjectPropertyAssertionAxiomImpl

  public OWLIndividual getObject() { return object; }

  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLObjectPropertyAssertionAxiomImpl impl = (OWLObjectPropertyAssertionAxiomImpl)obj;
    return (super.equals((OWLPropertyAssertionAxiomImpl)impl) &&
            (object != null && impl.object != null && object.equals(impl.object)));
  } // equals

  public int hashCode()
  {
    int hash = 45;
    hash = hash + super.hashCode();
    hash = hash + (null == object ? 0 : object.hashCode());
    return hash;
  } // hashCode

  public String toString() { return "" + getProperty() + "(" + getSubject() + ", " + object + ")"; }

} // OWLObjectPropertyAssertionAxiomImpl
