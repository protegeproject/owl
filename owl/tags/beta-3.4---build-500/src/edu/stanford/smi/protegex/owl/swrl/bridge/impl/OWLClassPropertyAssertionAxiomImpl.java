
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class OWLClassPropertyAssertionAxiomImpl extends OWLPropertyAssertionAxiomImpl implements OWLClassPropertyAssertionAxiom
{
  private OWLClass object;

  public OWLClassPropertyAssertionAxiomImpl(OWLIndividual subject, OWLProperty property, OWLClass object)
  {
    super(subject, property);
    this.object = object;
  } // OWLClassPropertyAssertionAxiomImpl

  public OWLClass getObject() { return object; }

  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    String propertyName = getProperty().getPropertyName();
    String subjectIndividualName = getSubject().getIndividualName();
    String objectClassName = getObject().getClassName();
    RDFProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyName);
    edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual;
    edu.stanford.smi.protegex.owl.model.OWLNamedClass objectClass;
    
    if (property == null) throw new InvalidPropertyNameException(propertyName);
    
    subjectIndividual = SWRLOWLUtil.getOWLIndividual(owlModel, subjectIndividualName);
    if (subjectIndividual == null) throw new InvalidIndividualNameException(subjectIndividualName);

    objectClass = SWRLOWLUtil.getOWLNamedClass(owlModel, objectClassName); 
    if (objectClass == null) throw new InvalidClassNameException(objectClassName);

    if (!subjectIndividual.hasPropertyValue(property, objectClass, false)) subjectIndividual.addPropertyValue(property, objectClass);
  } // write2OWL

  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLClassPropertyAssertionAxiomImpl impl = (OWLClassPropertyAssertionAxiomImpl)obj;
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

} // OWLClassPropertyAssertionAxiomImpl
