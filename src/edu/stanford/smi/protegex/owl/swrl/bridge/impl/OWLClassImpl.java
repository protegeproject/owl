
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import java.util.*;

/**
 ** Class representing an OWL named class
 */
public class OWLClassImpl extends BuiltInArgumentImpl implements OWLClass
{
  // equals() method defined in this class.
  private String className, prefixedClassName;
  private Set<String> superclassNames, directSuperClassNames, directSubClassNames, equivalentClassNames, equivalentClassSuperclassNames;
    
  public OWLClassImpl(OWLModel owlModel) throws OWLFactoryException
  {
    String anonymousName = SWRLOWLUtil.getNextAnonymousResourceName(owlModel);

    initialize(anonymousName, anonymousName);
  } // OWLClassImpl

  public OWLClassImpl(OWLModel owlModel, String className) throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.OWLNamedClass owlNamedClass = null;

    this.className = className;

    try {
      owlNamedClass = SWRLOWLUtil.getOWLNamedClass(owlModel, className);
    } catch (SWRLOWLUtilException e) {
      throw new InvalidClassNameException(className);
    } // try

    prefixedClassName = owlNamedClass.getPrefixedName();

    if (className.equals(OWLNames.Cls.THING)) {
      initialize(className, prefixedClassName);
    } else {
      superclassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSuperclasses(true));
      directSuperClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSuperclasses());
      directSubClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSubclasses());
      equivalentClassNames = SWRLOWLUtil.rdfResources2OWLNamedClassNames(owlNamedClass.getEquivalentClasses());
      equivalentClassSuperclassNames = new HashSet<String>();

      for (String equivalentClassName : equivalentClassNames) {
        OWLNamedClass equivalentClass = null;
        Iterator equivalentClassSuperClassesIterator = equivalentClass.getNamedSuperclasses(true).iterator();

        try {
          equivalentClass = SWRLOWLUtil.getOWLNamedClass(owlModel, equivalentClassName);
        } catch (SWRLOWLUtilException e) {
          throw new InvalidClassNameException(className);
        } // try

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

  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    try {
      edu.stanford.smi.protegex.owl.model.OWLClass cls, superclass;

      if (SWRLOWLUtil.isClass(owlModel, className)) cls = SWRLOWLUtil.getOWLNamedClass(owlModel, className);
      else cls = SWRLOWLUtil.createOWLNamedClass(owlModel, className);
        
      for (String superclassName : getSuperclassNames()) {
        if (SWRLOWLUtil.isClass(owlModel, superclassName)) superclass = SWRLOWLUtil.getOWLNamedClass(owlModel, superclassName);
        else superclass = SWRLOWLUtil.createOWLNamedClass(owlModel, superclassName);

        if (!cls.isSubclassOf(superclass)) cls.addSuperclass(superclass);
      } // for
    } catch (SWRLOWLUtilException e) {
      throw new SWRLRuleEngineBridgeException("error writing OWL class '" + className + "': " + e.getMessage());
    } // try
  } // write2OWL

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
