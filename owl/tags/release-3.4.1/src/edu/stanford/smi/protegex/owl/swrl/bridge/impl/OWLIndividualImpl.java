
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import java.util.*;

/**
 ** Class representing an OWL individual. 
 */
public class OWLIndividualImpl extends PropertyValueImpl implements OWLIndividual
{
  // NOTE: equals() method defined in this class

  private String individualName, prefixedIndividualName;  
  private Set<OWLClass> definingClasses, definingSuperclasses, definingEquivalentClasses, definingEquivalentClassSuperclasses;
  private Set<OWLIndividual> sameAsIndividuals;

  public OWLIndividualImpl(String individualName)
  {
    initialize(individualName, individualName);
  } // OWLIndividualImpl

  public OWLIndividualImpl(String individualName, String prefixedIndividualName, OWLClass owlClass) 
  {
    initialize(individualName, prefixedIndividualName);

    definingClasses.add(owlClass);
  } // OWLIndividualImpl

  public OWLIndividualImpl(String individualName, String prefixedIndividualName) 
  {
    initialize(individualName, prefixedIndividualName);
  } // OWLIndividualImpl

  public void addDefiningClass(OWLClass definingClass) { definingClasses.add(definingClass); }
  public void addDefiningSuperclass(OWLClass definingSuperclass) { definingSuperclasses.add(definingSuperclass); }
  public void addDefiningEquivalentClass(OWLClass definingEquivalentClass) { definingEquivalentClasses.add(definingEquivalentClass); }
  public void addDefiningEquivalentClassSuperclass(OWLClass definingEquivalentClassSuperclass) { definingEquivalentClassSuperclasses.add(definingEquivalentClassSuperclass); }

  public void addSameAsIndividual(OWLIndividual sameAsIndividual) { sameAsIndividuals.add(sameAsIndividual); }

  public String getIndividualName() { return individualName; }
  public String getPrefixedIndividualName() { return prefixedIndividualName; }
  public Set<OWLClass> getDefiningClasses() { return definingClasses; }
  public Set<OWLClass> getDefiningSuperclasses() { return definingSuperclasses; }
  public Set<OWLClass> getDefiningEquivalentClasses() { return definingEquivalentClasses; }
  public Set<OWLClass> getDefiningEquivalentClassSuperclasses() { return definingEquivalentClassSuperclasses; }
  public Set<OWLIndividual> getSameAsIndividuals() { return sameAsIndividuals; }
  
  public String getRepresentation() { return getPrefixedIndividualName(); }

  public String toString() { return getPrefixedIndividualName(); }

  // We consider individuals to be equal if they have the same name.
  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLIndividualImpl impl = (OWLIndividualImpl)obj;
    return (getIndividualName() == impl.getIndividualName() || (getIndividualName() != null && getIndividualName().equals(impl.getIndividualName()))) &&
           (getPrefixedIndividualName() == impl.getPrefixedIndividualName() || (getPrefixedIndividualName() != null && getPrefixedIndividualName().equals(impl.getPrefixedIndividualName()))) &&
           (definingClasses != null && impl.definingClasses != null && definingClasses.equals(impl.definingClasses)) &&
           (definingSuperclasses != null && impl.definingSuperclasses != null && definingSuperclasses.equals(impl.definingSuperclasses)) &&
           (definingEquivalentClasses != null && impl.definingEquivalentClasses != null && definingEquivalentClasses.equals(impl.definingEquivalentClasses)) &&
           (definingEquivalentClassSuperclasses != null && impl.definingEquivalentClassSuperclasses != null && definingEquivalentClassSuperclasses.equals(impl.definingEquivalentClassSuperclasses));
  } // equals

  public int hashCode()
  {
    int hash = 8;

    hash = hash + (null == getIndividualName() ? 0 : getIndividualName().hashCode());
    hash = hash + (null == getPrefixedIndividualName() ? 0 : getPrefixedIndividualName().hashCode());
    hash = hash + (null == getDefiningClasses() ? 0 : getDefiningClasses().hashCode());
    hash = hash + (null == getDefiningSuperclasses() ? 0 : getDefiningSuperclasses().hashCode());
    hash = hash + (null == getDefiningEquivalentClasses() ? 0 : getDefiningEquivalentClasses().hashCode());
    hash = hash + (null == getDefiningEquivalentClassSuperclasses() ? 0 : getDefiningEquivalentClassSuperclasses().hashCode());

    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return individualName.compareTo(((OWLIndividualImpl)o).getIndividualName());
  } // compareTo

  private void initialize(String individualName, String prefixedIndividualName)
  {
    this.individualName = individualName;
    this.prefixedIndividualName = prefixedIndividualName;

    definingClasses = new HashSet<OWLClass>();
    definingSuperclasses = new HashSet<OWLClass>();
    definingEquivalentClasses = new HashSet<OWLClass>();
    sameAsIndividuals = new HashSet<OWLIndividual>();
    definingEquivalentClassSuperclasses = new HashSet<OWLClass>();
  } // initialize

} // OWLIndividualImpl
