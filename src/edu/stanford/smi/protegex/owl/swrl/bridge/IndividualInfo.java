
// Info object representing an OWL individual. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.*;

// An individual can be an argument to an atom or a built-in.

public class IndividualInfo extends Info implements Argument
{
  private List classNames;
    
  // Constructor used when creating an info object from an OWL individual.
  public IndividualInfo(OWLIndividual individual) throws SWRLRuleEngineBridgeException
  {
    super(individual.getName());

    classNames = rdfResources2NamesList(individual.getRDFTypes());
  } // IndividualInfo

  // Constructor used when creating an info object from an individual name.
  public IndividualInfo(OWLModel owlModel, String individualName) throws SWRLRuleEngineBridgeException
  {
    super(individualName);

    OWLIndividual individual = owlModel.getOWLIndividual(individualName);
    if (individual == null) throw new InvalidIndividualNameException(individualName);

    classNames = rdfResources2NamesList(individual.getRDFTypes());
  } // IndividualInfo
  
  // Constructor used when asserting new individual class membership information from an assertion made in a target rule engine.
  public IndividualInfo(String individualName, String className) throws SWRLRuleEngineBridgeException
  {
    super(individualName);

    classNames = new ArrayList();
    classNames.add(className);
  } // IndividualInfo        

  // Constructor used when creating an individual to pass as an argument.
  public IndividualInfo(String individualName)
  {
    super(individualName);
    classNames = new ArrayList();
  } // IndividualInfo
  
  public List getClassNames() { return classNames; }
  
  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    OWLIndividual individual;
    Iterator classNamesIterator;
    
    individual = owlModel.getOWLIndividual(getName());
    if (individual == null) throw new InvalidIndividualNameException(getName());
    
    classNamesIterator = getClassNames().iterator();
    
    while (classNamesIterator.hasNext()) {
      String className = (String)classNamesIterator.next();
      RDFSClass rdfsClass = owlModel.getOWLNamedClass(className);
      individual.addRDFType(rdfsClass);
    } // while
    
  } // write2OWL

  public String toString()
  {
    return "Individual(name: " + getName() + ", classNames: " + classNames + ")";
  } // toString

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    IndividualInfo info = (IndividualInfo)obj;
    return (getName() == info.getName() || (getName() != null && getName().equals(info.getName()))) && 
      (classNames == info.classNames || (classNames != null && classNames.equals(info.classNames)));
  } // equals

  public int hashCode()
  {
    int hash = 7;
    hash = hash + (null == getName() ? 0 : getName().hashCode());
    hash = hash + (null == classNames ? 0 : classNames.hashCode());
    return hash;
  } // hashCode
  
} // IndividualInfo
