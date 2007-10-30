
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import java.util.*;
import java.io.Serializable;

/**
 ** Class representing an OWL named class
 */
public class OWLClassImpl extends BuiltInArgumentImpl implements OWLClass, Serializable
{
  // equals() method defined in this class.
  private String className;
  private Set<String> directSuperClassNames, directSubClassNames, equivalentClassNames;
    
  public OWLClassImpl(OWLModel owlModel, String className) throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.OWLNamedClass owlNamedClass;

    this.className = className;

    owlNamedClass = SWRLOWLUtil.getOWLNamedClass(owlModel, className);
    if (owlNamedClass == null) throw new InvalidClassNameException(className);

    if (className.equals("owl:Thing")) {
      directSuperClassNames = new HashSet<String>();
      directSubClassNames = new HashSet<String>();
    } else {
      directSuperClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSuperclasses());
      directSubClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSubclasses());
      equivalentClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getEquivalentClasses());
    } // if

  } // OWLClassImpl

  // Constructor used when creating a OWLClassImpl object to pass as a built-in argument
  public OWLClassImpl(String className)
  {
    initialize(className);
  } // OWLClassImpl

  public OWLClassImpl() // For serialization
  {
    initialize("");
  } // OWLClassImpl
  
  public String getClassName() { return className; }
  public Set<String> getDirectSuperClassNames() { return directSuperClassNames; }
  public Set<String> getDirectSubClassNames() { return directSubClassNames; }
  public Set<String> getEquivalentClassNames() { return equivalentClassNames; }

  public boolean isNamedClass() { return true; }
  public String getRepresentation() { return getClassName(); }

  public String toString() { return getClassName(); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLClassImpl impl = (OWLClassImpl)obj;
    return (getClassName() == impl.getClassName() || (getClassName() != null && getClassName().equals(impl.getClassName()))) && 
      (directSuperClassNames == impl.directSuperClassNames || (directSuperClassNames != null 
                                                               && directSuperClassNames.equals(impl.directSuperClassNames))) &&
      (directSubClassNames == impl.directSubClassNames || (directSubClassNames != null 
                                                           && directSubClassNames.equals(impl.directSubClassNames))) && 
      (equivalentClassNames == impl.equivalentClassNames || (equivalentClassNames != null 
                                                           && equivalentClassNames.equals(impl.equivalentClassNames)));
  } // equals

  public int hashCode()
  {
    int hash = 7;
    hash = hash + (null == getClassName() ? 0 : getClassName().hashCode());
    hash = hash + (null == directSuperClassNames ? 0 : directSuperClassNames.hashCode());
    hash = hash + (null == directSubClassNames ? 0 : directSubClassNames.hashCode());
    hash = hash + (null == equivalentClassNames ? 0 : equivalentClassNames.hashCode());
    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return className.compareTo(((OWLClassImpl)o).getClassName());
  } // compareTo

  private void initialize(String className)
  {
    this.className = className;
    directSuperClassNames = new HashSet<String>();
    directSubClassNames = new HashSet<String>();
    equivalentClassNames = new HashSet<String>();
  } // initialize

} // OWLClassImpl
