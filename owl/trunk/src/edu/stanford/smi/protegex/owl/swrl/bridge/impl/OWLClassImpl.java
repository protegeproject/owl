
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;

/**
 ** Class representing an OWL named class
 */
public class OWLClassImpl extends BuiltInArgumentImpl implements OWLClass
{
  // equals() method defined in this class.
  private String className, prefixedClassName;
  private Set<String> superclassNames, directSuperClassNames, directSubClassNames, equivalentClassNames, equivalentClassSuperclassNames;
    
  // Constructor used when creating a OWLClass object to pass as a built-in argument 
  public OWLClassImpl(String className)
  {
    initialize(className, className);
  } // OWLClassImpl

  // Constructor used when creating a OWLClass object from a built-in
  public OWLClassImpl(String className, String superclassName)
  {
    initialize(className, className);
    superclassNames.add(superclassName);
  } // OWLClassImpl

  public void setSuperclassNames(Set<String> superclassNames) { this.superclassNames = superclassNames; }
  public void setDirectSuperClassNames(Set<String> directSuperClassNames) { this.directSuperClassNames = directSuperClassNames; }
  public void setDirectSubClassNames(Set<String> directSubClassNames) { this.directSubClassNames = directSubClassNames; }
  public void setEquivalentClassNames(Set<String> equivalentClassNames) { this.equivalentClassNames = equivalentClassNames; }
  public void setEquivalentClassSuperclassNames(Set<String> equivalentClassSuperclassNames) { this.equivalentClassSuperclassNames = equivalentClassSuperclassNames; }

  public String getClassName() { return className; }
  public String getPrefixedClassName() { return prefixedClassName; }
  public Set<String> getSuperclassNames() { return superclassNames; }
  public Set<String> getDirectSuperClassNames() { return directSuperClassNames; }
  public Set<String> getDirectSubClassNames() { return directSubClassNames; }
  public Set<String> getEquivalentClassNames() { return equivalentClassNames; }
  public Set<String> getEquivalentClassSuperclassNames() { return equivalentClassSuperclassNames; }

  public boolean isNamedClass() { return true; }
  public String getRepresentation() { return getPrefixedClassName(); }

  public String toString() { return getPrefixedClassName(); }

  // We consider classes to be equal if they have the same name.
  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLClassImpl impl = (OWLClassImpl)obj;
    return (getClassName() == impl.getClassName() || (getClassName() != null && getClassName().equals(impl.getClassName()))) &&
           (getPrefixedClassName() == impl.getPrefixedClassName() || (getPrefixedClassName() != null && getPrefixedClassName().equals(impl.getPrefixedClassName()))) &&
           (superclassNames != null && impl.superclassNames != null && superclassNames.equals(impl.superclassNames)) &&
           (directSuperClassNames != null && impl.directSuperClassNames != null && directSuperClassNames.equals(impl.directSuperClassNames)) &&
           (directSubClassNames != null && impl.directSubClassNames != null && directSubClassNames.equals(impl.directSubClassNames)) &&
           (equivalentClassNames != null && impl.equivalentClassNames != null && equivalentClassNames.equals(impl.equivalentClassNames)) &&
           (equivalentClassSuperclassNames != null && impl.equivalentClassSuperclassNames != null && equivalentClassSuperclassNames.equals(impl.equivalentClassSuperclassNames));
  } // equals

  public int hashCode()
  {
    int hash = 12;

    hash = hash + (null == getClassName() ? 0 : getClassName().hashCode());
    hash = hash + (null == getPrefixedClassName() ? 0 : getPrefixedClassName().hashCode());
    hash = hash + (null == getSuperclassNames() ? 0 : getSuperclassNames().hashCode());
    hash = hash + (null == getDirectSuperClassNames() ? 0 : getDirectSuperClassNames().hashCode());
    hash = hash + (null == getDirectSubClassNames() ? 0 : getDirectSubClassNames().hashCode());
    hash = hash + (null == getEquivalentClassNames() ? 0 : getEquivalentClassNames().hashCode());
    hash = hash + (null == getEquivalentClassSuperclassNames() ? 0 : getEquivalentClassSuperclassNames().hashCode());

    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return className.compareTo(((OWLClassImpl)o).getClassName());
  } // compareTo

  private void initialize(String className, String prefixedClassName)
  {
    this.className = className;
    this.prefixedClassName = prefixedClassName;
    superclassNames = new HashSet<String>();
    directSuperClassNames = new HashSet<String>();
    directSubClassNames = new HashSet<String>();
    equivalentClassNames = new HashSet<String>();
    equivalentClassSuperclassNames = new HashSet<String>();
  } // initialize

} // OWLClassImpl
