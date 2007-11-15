
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class OWLPropertyPropertyAssertionAxiomImpl extends OWLPropertyAssertionAxiomImpl implements OWLPropertyPropertyAssertionAxiom
{
  private OWLProperty object;

  public OWLPropertyPropertyAssertionAxiomImpl(OWLIndividual subject, OWLProperty property, OWLProperty object)
  {
    super(subject, property);
    this.object = object;
  } // OWLPropertyPropertyAssertionAxiomImpl

  public OWLProperty getObject() { return object; }

  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    String propertyName = getProperty().getPropertyName();
    String subjectIndividualName = getSubject().getIndividualName();
    String objectPropertyName = getObject().getPropertyName();
    RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyName);
    edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
    edu.stanford.smi.protegex.owl.model.OWLProperty objectProperty;
    
    if (property == null) throw new InvalidPropertyNameException(propertyName);
    
    subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualName);
    if (subjectIndividual == null) throw new InvalidIndividualNameException(subjectIndividualName);

    objectProperty = SWRLOWLUtil.getOWLProperty(owlModel, objectPropertyName); 
    if (objectProperty == null) throw new InvalidPropertyNameException(objectPropertyName);

    if (!subjectIndividual.hasPropertyValue(property, objectProperty, false)) subjectIndividual.addPropertyValue(property, objectProperty);
  } // write2OWL

  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLPropertyPropertyAssertionAxiomImpl impl = (OWLPropertyPropertyAssertionAxiomImpl)obj;
    return (super.equals((OWLPropertyAssertionAxiomImpl)impl) &&
            (object != null && impl.object != null && object.equals(impl.object)));
  } // equals

  public int hashCode()
  {
    int hash = 49;
    hash = hash + super.hashCode();
    hash = hash + (null == object ? 0 : object.hashCode());
    return hash;
  } // hashCode

  public String toString() { return "" + getProperty() + "(" + getSubject() + ", " + object + ")"; }

} // OWLPropertyPropertyAssertionAxiomImpl
