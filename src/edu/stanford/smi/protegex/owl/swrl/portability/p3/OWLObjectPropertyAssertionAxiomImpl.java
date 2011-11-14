
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;

public class OWLObjectPropertyAssertionAxiomImpl extends P3OWLPropertyAssertionAxiomReference implements OWLObjectPropertyAssertionAxiomReference
{
  private OWLNamedIndividualReference object;

  public OWLObjectPropertyAssertionAxiomImpl(OWLNamedIndividualReference subject, OWLPropertyReference property, OWLNamedIndividualReference object)
  {
    super(subject, property);
    this.object = object;
  } // OWLObjectPropertyAssertionAxiomImpl

  public OWLNamedIndividualReference getObject() { return object; }

  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLObjectPropertyAssertionAxiomImpl impl = (OWLObjectPropertyAssertionAxiomImpl)obj;
    return (super.equals((P3OWLPropertyAssertionAxiomReference)impl) &&
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
