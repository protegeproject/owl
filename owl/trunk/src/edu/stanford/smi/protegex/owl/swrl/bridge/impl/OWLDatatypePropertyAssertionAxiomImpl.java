
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypePropertyAssertionAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;

public class OWLDatatypePropertyAssertionAxiomImpl extends OWLPropertyAssertionAxiomImpl implements OWLDatatypePropertyAssertionAxiom
{
  private OWLDatatypeValue object;

  public OWLDatatypePropertyAssertionAxiomImpl(OWLIndividual subject, OWLProperty property, OWLDatatypeValue object)
  {
    super(subject, property);
    this.object = object;
  } // OWLDatatypePropertyAssertionAxiomImpl

  public OWLDatatypeValue getObject() { return object; }

  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLDatatypePropertyAssertionAxiomImpl impl = (OWLDatatypePropertyAssertionAxiomImpl)obj;
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

  public String toString() 
  { 
    String result = "" + getProperty() + "(" + getSubject() + ", ";

    if (object.isString() || object.isXSDType()) result += "\"" + object + "\"";
    else result += "" + object;

    result += ")"; 

    return result;
  } // toString

} // OWLDatatypePropertyAssertionAxiomImpl
