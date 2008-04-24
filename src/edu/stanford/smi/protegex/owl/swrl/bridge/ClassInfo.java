
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;

import java.util.*;

/**
 ** Info object representing an OWL class. 
 */
public class ClassInfo extends Info implements Argument, ClassValue
{
  // equals() method defined in this class.
  private String className;
  private Set<String> directSuperClassNames, directSubClassNames, equivalentClassNames;
    
  public ClassInfo(OWLModel owlModel, String className) throws SWRLRuleEngineBridgeException
  {
    this.className = className;

    OWLNamedClass owlNamedClass = owlModel.getOWLNamedClass(className);
    if (owlNamedClass == null) throw new InvalidClassNameException(className);

    if (className.equals("owl:Thing")) {
      directSuperClassNames = new HashSet<String>();
      directSubClassNames = new HashSet<String>();
    } else {
      directSuperClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSuperclasses());
      directSubClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getNamedSubclasses());
      equivalentClassNames = SWRLOWLUtil.rdfResources2Names(owlNamedClass.getEquivalentClasses());
    } // if

  } // ClassInfo

  // Constructor used when creating a ClassInfo object to pass as a built-in argument
  public ClassInfo(String className)
  {
    this.className = className;

    directSuperClassNames = new HashSet<String>();
    directSubClassNames = new HashSet<String>();
    equivalentClassNames = new HashSet<String>();
  } // ClassInfo
  
  public String getClassName() { return className; }
  public Set<String> getDirectSuperClassNames() { return directSuperClassNames; }
  public Set<String> getDirectSubClassNames() { return directSubClassNames; }
  public Set<String> getEquivalentClassNames() { return equivalentClassNames; }

  public String toString() { return getClassName(); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    ClassInfo info = (ClassInfo)obj;
    return (getClassName() == info.getClassName() || (getClassName() != null && getClassName().equals(info.getClassName()))) && 
      (directSuperClassNames == info.directSuperClassNames || (directSuperClassNames != null 
                                                               && directSuperClassNames.equals(info.directSuperClassNames))) &&
      (directSubClassNames == info.directSubClassNames || (directSubClassNames != null 
                                                           && directSubClassNames.equals(info.directSubClassNames))) && 
      (equivalentClassNames == info.equivalentClassNames || (equivalentClassNames != null 
                                                           && equivalentClassNames.equals(info.equivalentClassNames)));
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
    return className.compareTo(((ClassInfo)o).getClassName());
  } // compareTo

} // ClassInfo
