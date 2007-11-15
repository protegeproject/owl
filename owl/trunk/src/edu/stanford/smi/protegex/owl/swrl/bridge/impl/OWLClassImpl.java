
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import java.util.*;

/**
 ** Class representing an OWL named class
 */
public class OWLClassImpl extends BuiltInArgumentImpl implements OWLClass
{
  // equals() method defined in this class.
  private String className;
  private Set<String> superclassNames, directSuperClassNames, directSubClassNames, equivalentClassNames, equivalentClassSuperclassNames;
    
  public OWLClassImpl(OWLModel owlModel, String className) throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.OWLNamedClass owlNamedClass;

    this.className = className;

    owlNamedClass = SWRLOWLUtil.getOWLNamedClass(owlModel, className);
    if (owlNamedClass == null) throw new InvalidClassNameException(className);

    if (className.equals("owl:Thing")) {
      initialize(className);
    } else {
      superclassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSuperclasses(true));
      directSuperClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSuperclasses());
      directSubClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSubclasses());
      equivalentClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getEquivalentClasses());
      equivalentClassSuperclassNames = new HashSet<String>();

      for (String equivalentClassName : equivalentClassNames) {
        OWLNamedClass equivalentClass = SWRLOWLUtil.getOWLNamedClass(owlModel, equivalentClassName);
        Iterator equivalentClassSuperClassesIterator = equivalentClass.getSuperclasses(true).iterator();
        while (equivalentClassSuperClassesIterator.hasNext()) {
          Object o = equivalentClassSuperClassesIterator.next();
          if (o instanceof OWLNamedClass) { // Ignore anonymous classes
            OWLNamedClass equivalentClassSuperclass = (OWLNamedClass)o;
            equivalentClassSuperclassNames.add(equivalentClassSuperclass.getName());
          } // if
        } /// while
      } // for
    } // if

  } // OWLClassImpl

  // Constructor used when creating a OWLClassImpl object to pass as a built-in argument
  public OWLClassImpl(String className)
  {
    initialize(className);
  } // OWLClassImpl

  public String getClassName() { return className; }
  public Set<String> getSuperclassNames() { return superclassNames; }
  public Set<String> getDirectSuperClassNames() { return directSuperClassNames; }
  public Set<String> getDirectSubClassNames() { return directSubClassNames; }
  public Set<String> getEquivalentClassNames() { return equivalentClassNames; }
  public Set<String> getEquivalentClassSuperclassNames() { return equivalentClassSuperclassNames; }

  public boolean isNamedClass() { return true; }
  public String getRepresentation() { return getClassName(); }

  public String toString() { return getClassName(); }

  // We consider classes to be equal if they have the same name.
  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLClassImpl impl = (OWLClassImpl)obj;
    return (getClassName() == impl.getClassName() || (getClassName() != null && getClassName().equals(impl.getClassName()))) &&
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

  private void initialize(String className)
  {
    this.className = className;
    superclassNames = new HashSet<String>();
    directSuperClassNames = new HashSet<String>();
    directSubClassNames = new HashSet<String>();
    equivalentClassNames = new HashSet<String>();
    equivalentClassSuperclassNames = new HashSet<String>();
  } // initialize

} // OWLClassImpl
