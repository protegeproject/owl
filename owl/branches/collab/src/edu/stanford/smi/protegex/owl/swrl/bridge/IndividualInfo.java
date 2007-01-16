
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.*;

/* 
** Info object representing an OWL individual. 
*/
public class IndividualInfo extends Info implements Argument, ObjectValue, Comparable
{
  // equals() method defined in this class.
  private String individualName;  
  private Set<String> classNames;
    
  // Constructor used when creating an info object from an OWL individual.
  public IndividualInfo(OWLIndividual individual) throws SWRLRuleEngineBridgeException
  {
    individualName = individual.getName();

    classNames = getDefiningClassNames(individual);
  } // IndividualInfo

  // Constructor used when creating an info object from an individual name.
  public IndividualInfo(OWLModel owlModel, String individualName) throws SWRLRuleEngineBridgeException
  {
    this.individualName = individualName;

    OWLIndividual individual = owlModel.getOWLIndividual(individualName);
    if (individual == null) throw new InvalidIndividualNameException(individualName);

    classNames = getDefiningClassNames(individual);
  } // IndividualInfo
  
  // Constructor used when asserting new individual class membership information from an assertion made in a target rule engine.
  public IndividualInfo(String individualName, String className) throws SWRLRuleEngineBridgeException
  {
    this.individualName = individualName;

    classNames = new HashSet<String>();
    classNames.add(className);
  } // IndividualInfo        

  // Constructor used when creating an individual to pass as an argument to a built-in or to return as an argument from a built-in.
  public IndividualInfo(String individualName)
  {
    this.individualName = individualName;
    classNames = new HashSet<String>();
  } // IndividualInfo

  public String getIndividualName() { return individualName; }
  public Set<String> getClassNames() { return classNames; }
  
  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    OWLIndividual individual;
    
    individual = owlModel.getOWLIndividual(getIndividualName());
    if (individual == null) throw new InvalidIndividualNameException(getIndividualName());

    for (String className : getClassNames()) {
      RDFSClass rdfsClass = owlModel.getOWLNamedClass(className);
      if (!individual.hasRDFType(rdfsClass)) individual.addRDFType(rdfsClass);
    } // for
  } // write2OWL

  public String toString() { return getIndividualName(); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    IndividualInfo info = (IndividualInfo)obj;
    return (getIndividualName() == info.getIndividualName() || (getIndividualName() != null && getIndividualName().equals(info.getIndividualName()))) && 
      (classNames == info.classNames || (classNames != null && classNames.equals(info.classNames)));
  } // equals

  public int hashCode()
  {
    int hash = 7;
    hash = hash + (null == getIndividualName() ? 0 : getIndividualName().hashCode());
    hash = hash + (null == classNames ? 0 : classNames.hashCode());
    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return individualName.compareTo((String)o);
  } // compareTo

  // Build up a list of all names of classes that define this individual. Not straightforward because getRDFTypes only returns asserted
  // types and does not include superclasses.
  private Set<String> getDefiningClassNames(OWLIndividual individual) throws SWRLRuleEngineBridgeException
  {
    Set<String> definingClassNames = new HashSet<String>();

    Iterator assertedClassesIterator = individual.getRDFTypes().iterator(); // Could be more than one asserted type
    while (assertedClassesIterator.hasNext()) {
      RDFSClass assertedClass = (RDFSClass)assertedClassesIterator.next();
      if (!definingClassNames.contains(assertedClass.getName())) definingClassNames.add(assertedClass.getName());

      Iterator superClassesIterator = assertedClass.getNamedSuperclasses(true).iterator();
      while (superClassesIterator.hasNext()) {
        RDFSClass superClass = (RDFSClass)superClassesIterator.next();
      if (!definingClassNames.contains(superClass.getName())) definingClassNames.add(superClass.getName());
      } // while
    } // while

    return definingClassNames;
  } // getDefiningClassNames    
  
} // IndividualInfo
