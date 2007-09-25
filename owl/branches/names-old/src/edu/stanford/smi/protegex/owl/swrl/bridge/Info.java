
// Internally, the bridge uses Info objects to store generic representations of all rules and relevant OWL knowledge. This representation is
// used to bridge between SWRL rules and OWL knowledge and the representation used by the target rule engine. It stores only knowledge about
// entities that are relevant to rule engine execution. This class is specialized into classes representing SWRL rules and OWL knowledge
// relevant to the execution of those rules.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.model.*;

import java.util.*;

public abstract class Info 
{
  private String name;

  public Info(String name) { this.name = name; }

  public String getName() { return name; }

  // Convenience method.
  protected static List rdfResources2NamesList(Collection resources) 
  {
    List result = new ArrayList();
    
    Iterator iterator = resources.iterator();
    while (iterator.hasNext()) {
      RDFResource resource = (RDFResource)iterator.next();
      result.add(resource.getName());
    } // while
    return result;
  } // rdfResources2NamesList            

} // Info

