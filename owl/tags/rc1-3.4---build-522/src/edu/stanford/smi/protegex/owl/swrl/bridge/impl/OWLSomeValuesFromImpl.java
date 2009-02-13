
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLClass;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

public class OWLSomeValuesFromImpl extends OWLRestrictionImpl implements OWLSomeValuesFrom 
{
  private OWLClass someValuesFrom;

  public OWLSomeValuesFromImpl(OWLClass owlClass, OWLProperty onProperty, OWLClass someValuesFrom)
  {
    super(owlClass, onProperty);
    this.someValuesFrom = someValuesFrom;
  } // OWLRestrictionImpl

  public OWLClass getSomeValuesFrom() { return someValuesFrom; }

  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    try {
      edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom someValuesFrom = SWRLOWLUtil.getOWLSomeValuesFrom(owlModel, asOWLClass().getClassName());
      edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, getProperty().getPropertyName());
      edu.stanford.smi.protegex.owl.model.RDFResource filler = SWRLOWLUtil.getClass(owlModel, getSomeValuesFrom().getClassName());
      
      someValuesFrom.setOnProperty(property);
      someValuesFrom.setFiller(filler); 
    } catch (SWRLOWLUtilException e) {
      throw new SWRLRuleEngineBridgeException("error writing someValuesFrom " + toString() + ": " + e.getMessage());
    } // try
  } // write2OWL

  public String toString()
  {
    return "someValuesFrom(" + asOWLClass().getClassName() + ", " + getProperty().getPropertyName() + ", " + getSomeValuesFrom().getClassName() + ")";
  } // toString

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLSomeValuesFromImpl impl = (OWLSomeValuesFromImpl)obj;
    return (asOWLClass().getClassName() == impl.asOWLClass().getClassName() || (asOWLClass().getClassName() != null && asOWLClass().getClassName().equals(impl.asOWLClass().getClassName()))) &&
           (getProperty().getPropertyName() == impl.getProperty().getPropertyName() || (getProperty().getPropertyName() != null && getProperty().getPropertyName().equals(impl.getProperty().getPropertyName()))) &&
           (getSomeValuesFrom().getClassName() == impl.getSomeValuesFrom().getClassName() || (getSomeValuesFrom().getClassName() != null && getSomeValuesFrom().getClassName().equals(impl.getSomeValuesFrom().getClassName())));
  } // equals

  public int hashCode()
  {
    int hash = 232;

    hash = hash + (null == asOWLClass().getClassName() ? 0 : asOWLClass().getClassName().hashCode());
    hash = hash + (null == getProperty().getPropertyName() ? 0 : getProperty().getPropertyName().hashCode());
    hash = hash + (null == getSomeValuesFrom().getClassName() ? 0 : getSomeValuesFrom().getClassName().hashCode());

    return hash;
  } // hashCode

} // OWLRestrictionImpl
