
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.*;

/**
 ** Info object representing an OWL individual. 
 */
public class IndividualInfo extends Info implements Argument, ObjectValue
{
  // NOTE: equals() method defined in this class.

  private String individualName;  
  private Set<String> definingClassNames, definingSuperClassNames, definingEquivalentClassNames;
    
  /**
   ** Constructor used when creating an info object from an OWL individual. We construct lists containing names of its direct defining
   ** classes, its indirect defining classes, and classes that are equivalent to the classes that define it. These names may be used by a
   ** rule engine to assert class membership information for individuals.
   */
  public IndividualInfo(OWLIndividual individual) throws SWRLRuleEngineBridgeException
  {
    individualName = individual.getName();

    initialize();

    buildDefiningClassNames(individual);
    buildDefiningSuperClassNames(individual);
    buildDefiningEquivalentClassNames(individual);
  } // IndividualInfo

  /**
   ** Constructor used when creating an info object from an individual name. We construct lists containing names of its direct defining
   ** classes, its indirect defining classes, and classes that are equivalent to the classes that define it. These names may be used by a
   ** rule engine to assert class membership information for individuals.
   */
  public IndividualInfo(OWLModel owlModel, String individualName) throws SWRLRuleEngineBridgeException
  {
    this.individualName = individualName;

    OWLIndividual individual = owlModel.getOWLIndividual(individualName);
    if (individual == null) throw new InvalidIndividualNameException(individualName);

    initialize();

    buildDefiningClassNames(individual);
    buildDefiningSuperClassNames(individual);
    buildDefiningEquivalentClassNames(individual);
  } // IndividualInfo
  
  /**
   ** Constructor used when asserting new individual class membership information from an assertion made in a target rule engine. Only the
   ** individual name and the class that it is asserted to be a member of is recorded.
   */
  public IndividualInfo(String individualName, String className) 
  {
    this.individualName = individualName;

    initialize();

    definingClassNames.add(className);
  } // IndividualInfo        

  /**
   ** Constructor used when creating an individual to pass as an argument to a built-in or to return as an argument from a built-in. Only
   ** the name of the individual is recorded.
   */
  public IndividualInfo(String individualName)
  {
    this.individualName = individualName;

    initialize();
  } // IndividualInfo

  public String getIndividualName() { return individualName; }
  public Set<String> getDefiningClassNames() { return definingClassNames; }
  public Set<String> getDefiningSuperClassNames() { return definingSuperClassNames; }
  public Set<String> getDefiningEquivalentClassNames() { return definingEquivalentClassNames; }
  
  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    OWLIndividual individual = owlModel.getOWLIndividual(getIndividualName());

    if (individual == null) throw new InvalidIndividualNameException(getIndividualName());

    for (String className : getDefiningClassNames()) {
      RDFSClass rdfsClass = owlModel.getOWLNamedClass(className);
      if (!individual.hasRDFType(rdfsClass)) 
        if (individual.hasRDFType(owlModel.getOWLThingClass())) individual.setRDFType(rdfsClass);
        else individual.addRDFType(rdfsClass);
    } // for
  } // write2OWL

  public String toString() { return getIndividualName(); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    IndividualInfo info = (IndividualInfo)obj;
    return (getIndividualName() == info.getIndividualName() || (getIndividualName() != null && 
                                                                getIndividualName().equals(info.getIndividualName()))) && 
      (definingClassNames == info.definingClassNames || (definingClassNames != null && definingClassNames.equals(info.definingClassNames))) &&
      (definingSuperClassNames == info.definingSuperClassNames || (definingSuperClassNames != null && definingSuperClassNames.equals(info.definingSuperClassNames))) &&
      (definingEquivalentClassNames == info.definingEquivalentClassNames || (definingEquivalentClassNames != null && definingEquivalentClassNames.equals(info.definingEquivalentClassNames)));
  } // equals

  public int hashCode()
  {
    int hash = 7;
    hash = hash + (null == getIndividualName() ? 0 : getIndividualName().hashCode());
    hash = hash + (null == definingClassNames ? 0 : definingClassNames.hashCode());
    hash = hash + (null == definingSuperClassNames ? 0 : definingSuperClassNames.hashCode());
    hash = hash + (null == definingEquivalentClassNames ? 0 : definingEquivalentClassNames.hashCode());

    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return individualName.compareTo(((IndividualInfo)o).getIndividualName());
  } // compareTo

  private void initialize()
  {
    definingClassNames = new HashSet<String>();
    definingSuperClassNames = new HashSet<String>();
    definingEquivalentClassNames = new HashSet<String>();
  } // initialize

  private void buildDefiningClassNames(OWLIndividual individual) 
  { 
    for (Object o : individual.getRDFTypes()) {
      RDFSClass cls = (RDFSClass)o; 
      if (!cls.isAnonymous()) definingClassNames.add(cls.getName());
    } // for
  } // buildDefiningClassNames

  private void buildDefiningSuperClassNames(OWLIndividual individual) 
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

  private void buildDefiningEquivalentClassNames(OWLIndividual individual) 
  {
    Iterator definingClassesIterator = individual.getRDFTypes().iterator(); // Could be more than one defining type
    while (definingClassesIterator.hasNext()) {
      RDFSClass definingClass = (RDFSClass)definingClassesIterator.next();
      Iterator equlvalentClassesIterator = definingClass.getEquivalentClasses().iterator();
      while (equlvalentClassesIterator.hasNext()) {
        RDFSClass equlvalentClass = (RDFSClass)equlvalentClassesIterator.next();
        if (equlvalentClass.isAnonymous()) continue;
        if (!definingEquivalentClassNames.contains(equlvalentClass.getName())) definingEquivalentClassNames.add(equlvalentClass.getName());
      } // while
    } // while
  } // buildDefiningEquivalentClassNames
  
} // IndividualInfo
