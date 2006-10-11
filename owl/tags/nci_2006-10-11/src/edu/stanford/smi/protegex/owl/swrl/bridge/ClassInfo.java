
// Info object representing an OWL class. 

package edu.stanford.smi.protegex.owl.swrl.bridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;

import java.util.*;

public class ClassInfo extends Info
{
  private List directSuperClassNames, directSubClassNames;
    
  public ClassInfo(OWLModel owlModel, String className) throws SWRLRuleEngineBridgeException
  {
    super(className);

    OWLNamedClass owlNamedClass = owlModel.getOWLNamedClass(className);
    if (owlNamedClass == null) throw new InvalidClassNameException(className);

    if (className.equals("owl:Thing")) {
      directSuperClassNames = new ArrayList();
      directSubClassNames = new ArrayList();
    } else {
      directSuperClassNames = rdfResources2NamesList(owlNamedClass.getNamedSuperclasses());
      directSubClassNames = rdfResources2NamesList(owlNamedClass.getNamedSubclasses());
    } // if
    
  } // ClassInfo
  
  public List getDirectSuperClassNames() { return directSuperClassNames; }
  public List getDirectSubClassNames() { return directSubClassNames; }

  public String toString()
  {
    return "Class(name: " + getName() + ", directSubClassNames: " + directSubClassNames + ", directSuperClassNames: " + directSuperClassNames+")";
  } // toString

} // ClassInfo
