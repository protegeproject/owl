
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class OWLObjectPropertyAssertionAxiomImpl extends OWLPropertyAssertionAxiomImpl implements OWLObjectPropertyAssertionAxiom
{
  private OWLIndividual object;

  public OWLObjectPropertyAssertionAxiomImpl(OWLIndividual subject, OWLProperty property, OWLIndividual object)
  {
    super(subject, property);
    this.object = object;
  } // OWLObjectPropertyAssertionAxiomImpl

  public OWLIndividual getObject() { return object; }

  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual, objectIndividual;
    String propertyName = getProperty().getPropertyName();
    String subjectIndividualName = getSubject().getIndividualName();
    String objectIndividualName = getObject().getIndividualName();
    RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyName);
    
    if (property == null) throw new InvalidPropertyNameException(propertyName);
    
    subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualName);
    if (subjectIndividual == null) throw new InvalidIndividualNameException(subjectIndividualName);
 
    objectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, objectIndividualName); 
    if (objectIndividual == null) throw new InvalidIndividualNameException(objectIndividualName);

    if (!subjectIndividual.hasPropertyValue(property, objectIndividual, false)) subjectIndividual.addPropertyValue(property, objectIndividual);
  } // write2OWL

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
