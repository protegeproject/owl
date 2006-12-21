
// Info object representing an OWL class. 

package edu.stanford.smi.protegex.owl.swrl.bridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;

import java.util.*;

public class ClassInfo extends Info implements Argument, ClassValue, Comparable
{
  // equals() method defined in this class.
  private String className;
  private Set<String> directSuperClassNames, directSubClassNames;
    
  public ClassInfo(OWLModel owlModel, String className) throws SWRLRuleEngineBridgeException
  {
    this.className = className;

    OWLNamedClass owlNamedClass = owlModel.getOWLNamedClass(className);
    if (owlNamedClass == null) throw new InvalidClassNameException(className);

    if (className.equals("owl:Thing")) {
      directSuperClassNames = new HashSet<String>();
      directSubClassNames = new HashSet<String>();
    } else {
      directSuperClassNames = rdfResources2Names(owlNamedClass.getNamedSuperclasses());
      directSubClassNames = rdfResources2Names(owlNamedClass.getNamedSubclasses());
    } // if
    
  } // ClassInfo
  
  public String getClassName() { return className; }
  public Set<String> getDirectSuperClassNames() { return directSuperClassNames; }
  public Set<String> getDirectSubClassNames() { return directSubClassNames; }

  public String toString()
  {
    return "Class(name: " + getClassName() + ", directSubClassNames: " + directSubClassNames + ", directSuperClassNames: " + directSuperClassNames+")";
  } // toString

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    ClassInfo info = (ClassInfo)obj;
    return (getClassName() == info.getClassName() || (getClassName() != null && getClassName().equals(info.getClassName()))) && 
      (directSuperClassNames == info.directSuperClassNames || (directSuperClassNames != null 
                                                               && directSuperClassNames.equals(info.directSuperClassNames))) &&
      (directSubClassNames == info.directSubClassNames || (directSubClassNames != null 
                                                           && directSubClassNames.equals(info.directSubClassNames)));
  } // equals

  public int hashCode()
  {
    int hash = 7;
    hash = hash + (null == getClassName() ? 0 : getClassName().hashCode());
    hash = hash + (null == directSuperClassNames ? 0 : directSuperClassNames.hashCode());
    hash = hash + (null == directSubClassNames ? 0 : directSubClassNames.hashCode());
    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return className.compareTo((String)o);
  } // compareTo

} // ClassInfo
