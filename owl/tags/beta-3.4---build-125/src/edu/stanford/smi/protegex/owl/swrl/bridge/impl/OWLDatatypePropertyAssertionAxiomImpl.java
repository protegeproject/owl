
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class OWLDatatypePropertyAssertionAxiomImpl extends OWLPropertyAssertionAxiomImpl implements OWLDatatypePropertyAssertionAxiom
{
  private OWLDatatypeValue object;

  public OWLDatatypePropertyAssertionAxiomImpl(OWLIndividual subject, OWLProperty property, OWLDatatypeValue object)
  {
    super(subject, property);
    this.object = object;
  } // OWLDatatypePropertyAssertionAxiomImpl

  public OWLDatatypeValue getObject() { return object; }

  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
    String propertyName = getProperty().getPropertyName();
    String subjectIndividualName = getSubject().getIndividualName();
    RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyName);
    Object objectValue;
    
    if (property == null) throw new InvalidPropertyNameException(propertyName);
    
    subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualName);
    if (subjectIndividual == null) throw new InvalidIndividualNameException(subjectIndividualName);

    if (getObject().isString()) objectValue = getObject().getString(); // Store strings as String objects, not RDFSLiteral objects.
    else objectValue = getObject().asRDFSLiteral(owlModel); // Will throw exception if it cannot convert

    if (!subjectIndividual.hasPropertyValue(property, objectValue, false)) subjectIndividual.addPropertyValue(property, objectValue);    
  } // write2OWL

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

    if (object.isString()) result += "\"" + object + "\"";
    else result += "" + object;

    result += ")"; 

    return result;
  } // toString

} // OWLDatatypePropertyAssertionAxiomImpl
