
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import java.util.*;
import java.io.Serializable;

/**
 ** Class representing an OWL individual. 
 */
public class OWLIndividualImpl extends PropertyValueImpl implements OWLIndividual, Serializable
{
  // NOTE: equals() method defined in this class

  private String individualName;  
  private Set<String> definingClassNames, definingSuperClassNames, definingEquivalentClassNames;
    
  /**
   ** Constructor used when creating from an OWL individual. We construct lists containing names of its direct defining classes, its
   ** indirect defining classes, and classes that are equivalent to the classes that define it. These names may be used by a rule engine to
   ** assert class membership information for individuals.
   */
  public OWLIndividualImpl(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  {
    initialize(individual.getName());

    buildDefiningClassNames(individual);
    buildDefiningSuperClassNames(individual);
    buildDefiningEquivalentClassNames(individual);
  } // OWLIndividualImpl

  /**
   ** Constructor used when creating from an individual name. We construct lists containing names of its direct defining classes, its
   ** indirect defining classes, and classes that are equivalent to the classes that define it. These names may be used by a rule engine to
   ** assert class membership information for individuals.
   */
  public OWLIndividualImpl(OWLModel owlModel, String individualName) throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.OWLIndividual individual = owlModel.getOWLIndividual(individualName);
    if (individual == null) throw new InvalidIndividualNameException(individualName);

    initialize(individualName);

    buildDefiningClassNames(individual);
    buildDefiningSuperClassNames(individual);
    buildDefiningEquivalentClassNames(individual);
  } // OWLIndividualImpl
  
  /**
   ** Constructor used when asserting new individual class membership information from an assertion made in a target rule engine. Only the
   ** individual name and the class that it is asserted to be a member of is recorded.
   */
  public OWLIndividualImpl(String individualName, String className) 
  {
    initialize(individualName);

    definingClassNames.add(className);
  } // OWLIndividualImpl

  /**
   ** Constructor used when creating an individual to pass as an argument to a built-in or to return as an argument from a built-in. Only
   ** the name of the individual is recorded.
   */
  public OWLIndividualImpl(String individualName)
  {
    initialize(individualName);
  } // OWLIndividualImpl

  public OWLIndividualImpl() // For serialization
  {
    initialize("");
  } // OWLIndividualImpl

  public String getIndividualName() { return individualName; }
  public Set<String> getDefiningClassNames() { return definingClassNames; }
  public Set<String> getDefiningSuperClassNames() { return definingSuperClassNames; }
  public Set<String> getDefiningEquivalentClassNames() { return definingEquivalentClassNames; }
  
  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    edu.stanford.smi.protegex.owl.model.OWLIndividual individual = owlModel.getOWLIndividual(getIndividualName());

    if (individual == null) throw new InvalidIndividualNameException(getIndividualName());

    for (String className : getDefiningClassNames()) {
      RDFSClass rdfsClass = owlModel.getOWLNamedClass(className);
      if (!individual.hasRDFType(rdfsClass)) 
        if (individual.hasRDFType(owlModel.getOWLThingClass())) individual.setRDFType(rdfsClass);
        else individual.addRDFType(rdfsClass);
    } // for
  } // write2OWL

  public String getRepresentation() { return getIndividualName(); }

  public String toString() { return getIndividualName(); }

  // We consider individuals to be equal if they have the same name.
  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLIndividualImpl impl = (OWLIndividualImpl)obj;
    return (getIndividualName() == impl.getIndividualName() || (getIndividualName() != null && getIndividualName().equals(impl.getIndividualName())));
  } // equals

  public int hashCode()
  {
    int hash = 7;

    hash = hash + (null == getIndividualName() ? 0 : getIndividualName().hashCode());

    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return individualName.compareTo(((OWLIndividualImpl)o).getIndividualName());
  } // compareTo

  private void initialize(String individualName)
  {
    this.individualName = individualName;

    definingClassNames = new HashSet<String>();
    definingSuperClassNames = new HashSet<String>();
    definingEquivalentClassNames = new HashSet<String>();
  } // initialize

  private void buildDefiningClassNames(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) 
  { 
    for (Object o : individual.getRDFTypes()) {
      RDFSClass cls = (RDFSClass)o; 
      if (!cls.isAnonymous()) definingClassNames.add(cls.getName());
    } // for
  } // buildDefiningClassNames

  private void buildDefiningSuperClassNames(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) 
  {
    Iterator definingClassesIterator = individual.getRDFTypes().iterator(); // Could be more than one defining type
    while (definingClassesIterator.hasNext()) {
      RDFSClass definingClass = (RDFSClass)definingClassesIterator.next();
      Iterator superClassesIterator = definingClass.getNamedSuperclasses(true).iterator();
      while (superClassesIterator.hasNext()) {
        RDFSClass superClass = (RDFSClass)superClassesIterator.next();
        if (superClass.isAnonymous()) continue;
        if (!definingSuperClassNames.contains(superClass.getName())) definingSuperClassNames.add(superClass.getName());
      } // while
    } // while
  } // buildDefiningSuperClassNames

  private void buildDefiningEquivalentClassNames(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) 
  {
    Iterator definingClassesIterator = individual.getRDFTypes().iterator(); // Could be more than one defining type
    while (definingClassesIterator.hasNext()) {
      RDFSClass definingClass = (RDFSClass)definingClassesIterator.next();
      Iterator equivalentClassesIterator = definingClass.getEquivalentClasses().iterator();
      while (equivalentClassesIterator.hasNext()) {
        RDFSClass equivalentClass = (RDFSClass)equivalentClassesIterator.next();
        if (!equivalentClass.isAnonymous() &&!definingEquivalentClassNames.contains(equivalentClass.getName()))
          definingEquivalentClassNames.add(equivalentClass.getName());
      } // while
    } // while
  } // buildDefiningEquivalentClassNames
  
} // OWLIndividualImpl
