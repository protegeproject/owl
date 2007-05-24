package edu.stanford.smi.protegex.owl.model.classparser;

import java.util.Collection;
import java.util.List;

import edu.stanford.smi.protege.model.BrowserSlotPattern;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;

public class ParserUtils {
  
  public static Frame getFrameByName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return getFrameByName(model, name, null);
  }
  
  public static OWLClass getOWLClassFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return (OWLClass) getFrameByName(model, name, OWLClass.class);
  }
  
  public static RDFProperty getRDFPropertyFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return (RDFProperty) getFrameByName(model, name, RDFProperty.class);
  }
  
  public static OWLDatatypeProperty getOWLDatatypePropertyFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return (OWLDatatypeProperty) getFrameByName(model, name, OWLDatatypeProperty.class);
  }
  
  public static OWLObjectProperty getOWLObjectPropertyFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return (OWLObjectProperty) getFrameByName(model, name, OWLObjectProperty.class);
  }
  
  public static RDFResource getRDFResourceFromName(OWLModel model, String name) 
  throws AmbiguousNameException {
    return (RDFResource) getFrameByName(model, name, RDFResource.class);
  }
  
  public static OWLIndividual getOWLIndividualFromName(OWLModel model, String  name)  
  throws AmbiguousNameException {
    return (OWLIndividual) getFrameByName(model, name, OWLIndividual.class); 
  }
  
  /**
   * Calculates the frame with the given name using the rdfs:label slot as needed.
   * This method either looks for an rdfs property or an rdfs class depending on the is
   * Property parameter.  This method determines whether to use the rdfs:label to look up
   * the frame based on whether 
   * 
   * @param model the owl model
   * @param name the string which is either an rdfs label or a name.
   * @param isProperty if true search for an rdf property, if false look for an rdfs class.  If null
   *            return whatever is found.
   * @return
   * @throws AmbiguousNameException
   */
  @SuppressWarnings("unchecked")
  private static RDFResource getFrameByName(OWLModel model, 
                                            String name, 
                                            Class targetClass) 
  throws AmbiguousNameException {
    RDFResource resource = (RDFResource) ((KnowledgeBase) model).getFrame(name);
    if (resource != null && resourceCorrectlyTyped(resource, targetClass)) {
      return resource;
    }
    else {
      resource = null;
    }
    
    String lang = model.getDefaultLanguage();
    Collection frames = ((KnowledgeBase) model).getFramesWithValue(model.getRDFSLabelProperty(), 
                                                                   null, 
                                                                   false, 
                                                                   DefaultRDFSLiteral.getRawValue(name, lang));
    for (Object o : frames) {
      if (resourceCorrectlyTyped((Frame) o, targetClass) && displaysWithRDFSLabel(model, (Instance) o)) {
        if (resource != null) {
          throw new AmbiguousNameException("Multiple resourcese share the same name "  + name);
        }
        resource = (RDFResource) o;
      }
    }
    if (resource != null) {
      return resource;
    }
    // if the above failed try again with the null language
    if (lang != null) {
      frames = ((KnowledgeBase) model).getFramesWithValue(model.getRDFSLabelProperty(), 
                                                          null, 
                                                          false, 
                                                          DefaultRDFSLiteral.getRawValue(name, (String) null));
      for (Object o : frames) {
        if (resourceCorrectlyTyped((Frame) o, targetClass) && displaysWithRDFSLabel(model, (Instance) o)) {
          if (resource != null) {
            throw new AmbiguousNameException("After falling back to the null language multiple resources share the same name " + name);
          }
          resource = (RDFResource) o;
        }
      }
    }
    return resource;
  }
  
  private static boolean displaysWithRDFSLabel(OWLModel model, Instance i) {
    Cls type = i.getDirectType();
    BrowserSlotPattern bsp = type.getBrowserSlotPattern();
    List<Slot> slots = bsp.getSlots();
    return slots.size() == 1 && slots.contains(model.getRDFSLabelProperty());
  }
  
  @SuppressWarnings("unchecked")
  private static boolean resourceCorrectlyTyped(Frame resource, Class targetClass) {
    if (targetClass == null) {
      return true;
    }
    return targetClass.isAssignableFrom(resource.getClass());
  }
}
