
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
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
    
  /**
   ** Constructor used when creating from an OWL individual. We construct lists containing its direct defining classes, its indirect
   ** defining classes, and classes that are equivalent to the classes that define it. These names may be used by a rule engine to assert
   ** class membership information for individuals.
   */
  public OWLIndividualImpl(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  {
    initialize(individual.getName(), individual.getPrefixedName());

    buildDefiningClasses(individual);
    buildDefiningSuperclasses(individual);
    buildDefiningEquivalentClasses(individual);
    buildSameAsIndividuals(individual);
  } // OWLIndividualImpl

  /**
   ** Constructor used when creating an indiviudla from an individual name. We construct lists containing its direct defining classes, its
   ** indirect defining classes, and classes that are equivalent to the classes that define it. These names may be used by a rule engine to
   ** assert class membership information for individuals.
   */
  public OWLIndividualImpl(OWLModel owlModel, String individualName) throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.OWLIndividual individual = owlModel.getOWLIndividual(individualName);
    if (individual == null) throw new InvalidIndividualNameException(individualName);

    initialize(individualName, individual.getPrefixedName());

    buildDefiningClasses(individual);
    buildDefiningSuperclasses(individual);
    buildDefiningEquivalentClasses(individual);
    buildSameAsIndividuals(individual);
  } // OWLIndividualImpl
  
  /**
   ** Constructor used when asserting new individual class membership information from an assertion made in a target rule engine. Only the
   ** individual name and the class that it is asserted to be a member of is recorded.
   */
  public OWLIndividualImpl(OWLIndividual owlIndividual, OWLClass owlClass) 
  {
    initialize(owlIndividual.getIndividualName(), owlIndividual.getPrefixedIndividualName());

    definingClasses.add(owlClass);
  } // OWLIndividualImpl

  /**
   ** Constructor used when creating an individual to pass as an argument to a built-in or to return as an argument from a built-in. Only
   ** the name of the individual is recorded.
   */
  public OWLIndividualImpl(String individualName)
  {
    initialize(individualName, individualName);
  } // OWLIndividualImpl

  /**
   ** Constructor used when generating a new individual from a rule engine.
   */
  public OWLIndividualImpl(String individualName, String prefixedIndividualName, OWLClass owlClass) 
  {
    initialize(individualName, prefixedIndividualName);

    definingClasses.add(owlClass);
  } // OWLIndividualImpl

  public String getIndividualName() { return individualName; }
  public String getPrefixedIndividualName() { return prefixedIndividualName; }
  public Set<OWLClass> getDefiningClasses() { return definingClasses; }
  public Set<OWLClass> getDefiningSuperclasses() { return definingSuperclasses; }
  public Set<OWLClass> getDefiningEquivalentClasses() { return definingEquivalentClasses; }
  public Set<OWLClass> getDefiningEquivalentClassSuperclasses() { return definingEquivalentClassSuperclasses; }
  public Set<OWLIndividual> getSameAsIndividuals() { return sameAsIndividuals; }
  
  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    edu.stanford.smi.protegex.owl.model.OWLIndividual individual = owlModel.getOWLIndividual(getIndividualName());

    if (individual == null) throw new InvalidIndividualNameException(getIndividualName());

    for (OWLClass owlClass : getDefiningClasses()) {
      RDFSClass rdfsClass = owlModel.getOWLNamedClass(owlClass.getClassName());
      if (!individual.hasRDFType(rdfsClass)) 
        if (individual.hasRDFType(owlModel.getOWLThingClass())) individual.setRDFType(rdfsClass);
        else individual.addRDFType(rdfsClass);
    } // for
  } // write2OWL

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

  private void buildDefiningClasses(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  { 
    for (Object o : individual.getRDFTypes()) {
      RDFSClass cls = (RDFSClass)o; 
      if (!cls.isAnonymous() && cls instanceof OWLNamedClass) definingClasses.add(OWLFactory.createOWLClass((OWLNamedClass)cls));
    } // for
  } // buildDefiningClasses

  private void buildDefiningSuperclasses(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  {
    Iterator definingClassesIterator = individual.getRDFTypes().iterator(); // Could be more than one defining type
    while (definingClassesIterator.hasNext()) {
      RDFSClass definingClass = (RDFSClass)definingClassesIterator.next();
      Iterator superClassesIterator = definingClass.getNamedSuperclasses(true).iterator();
      while (superClassesIterator.hasNext()) {
        RDFSClass cls = (RDFSClass)superClassesIterator.next();
        if (cls instanceof OWLNamedClass) {
          OWLClass superClass = OWLFactory.createOWLClass((OWLNamedClass)cls);
          if (!definingSuperclasses.contains(superClass)) definingSuperclasses.add(superClass);
        } // if
      } // while
    } // while
  } // buildDefiningSuperclasses

  private void buildDefiningEquivalentClasses(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  {
    Iterator definingClassesIterator = individual.getRDFTypes().iterator(); // Could be more than one defining type
    while (definingClassesIterator.hasNext()) {
      RDFSClass definingClass = (RDFSClass)definingClassesIterator.next();
      Iterator equivalentClassesIterator = definingClass.getEquivalentClasses().iterator();
      while (equivalentClassesIterator.hasNext()) {
        RDFSClass cls1 = (RDFSClass)equivalentClassesIterator.next();
        if (cls1 instanceof OWLNamedClass) {
          OWLClass equivalentClass = OWLFactory.createOWLClass((OWLNamedClass)cls1);
          if (!definingEquivalentClasses.contains(equivalentClass)) {
            Iterator equivalentClassesSuperclassesIterator = cls1.getNamedSuperclasses(true).iterator();
            while (equivalentClassesSuperclassesIterator.hasNext()) {
              RDFSClass cls2 = (RDFSClass)equivalentClassesSuperclassesIterator.next();
              if (cls2 instanceof OWLNamedClass) {
                OWLClass equivalentClassSuperclass = OWLFactory.createOWLClass((OWLNamedClass)cls2);
                if (!definingEquivalentClassSuperclasses.contains(equivalentClassSuperclass)) definingEquivalentClassSuperclasses.add(equivalentClassSuperclass);
              } // if
            } // if
            definingEquivalentClasses.add(equivalentClass);
          } // if
        } // if
      } // while
    } // while
  } // buildDefiningEquivalentClasses

  private void buildSameAsIndividuals(edu.stanford.smi.protegex.owl.model.OWLIndividual individual) throws OWLFactoryException
  {
    RDFProperty sameAsProperty = SWRLOWLUtil.getOWLSameAsProperty(individual.getOWLModel());

    if (individual.hasPropertyValue(sameAsProperty)) {
      Collection individuals = (Collection)individual.getPropertyValues(sameAsProperty);
      Iterator individualsIterator = individuals.iterator();
      while (individualsIterator.hasNext()) {
        Object object = individualsIterator.next();
        if (!(object instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)) continue;
        edu.stanford.smi.protegex.owl.model.OWLIndividual sameAsIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object;
        sameAsIndividuals.add(OWLFactory.createOWLIndividual(sameAsIndividual));
      } // while
    } // if
  } // buildSameAsIndividuals
  
} // OWLIndividualImpl
