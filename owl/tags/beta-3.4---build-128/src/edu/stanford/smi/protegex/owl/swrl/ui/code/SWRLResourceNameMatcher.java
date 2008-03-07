
package edu.stanford.smi.protegex.owl.swrl.ui.code;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.ui.code.OWLResourceNameMatcher;
import edu.stanford.smi.protegex.owl.ui.code.ResourceNameMatcher;
import edu.stanford.smi.protegex.owl.ui.code.SymbolTextField;

public class SWRLResourceNameMatcher implements ResourceNameMatcher 
{

  public String getInsertString(RDFResource resource) 
  {
    if (resource instanceof SWRLVariable) return resource.getName();
    else return resource.getBrowserText();
  } // getInsertString

  public Set<RDFResource> getMatchingResources(String prefix, String leftString, OWLModel owlModel) 
  {
    Set<RDFResource> resources = new HashSet<RDFResource>();
    if (leftString.endsWith("?")) {
      for (Iterator it = owlModel.getOWLNamedClass(SWRLNames.Cls.VARIABLE).getInstances(true).iterator(); it.hasNext();) {
        SWRLVariable var = (SWRLVariable)it.next();
        if (var.getName().startsWith(prefix)) resources.add(var);
      } // for
    } else if (prefix.length() > 0) {
      getMatchingOWLNamedClasses(prefix, resources, owlModel);
      getMatchingOWLIndividuals(prefix, resources, owlModel);
      getMatchingOWLProperties(prefix, resources, owlModel);
    } // if
    return resources;
  } // getMatchingResources
    
  public boolean isIdChar(char ch) { return SymbolTextField.isIdChar(ch); }
    
  public static void getMatchingOWLNamedClasses(String prefix, Set<RDFResource> result, OWLModel owlModel)
  {
    Set<RDFResource> localResult = new HashSet<RDFResource>(); 
    Iterator iterator;
    
    OWLResourceNameMatcher.addMatchingRDFSNamedClasses(prefix, localResult, owlModel);
    iterator = localResult.iterator();
    while (iterator.hasNext()) {
      RDFSNamedClass aClass = (RDFSNamedClass)iterator.next();
      if (aClass instanceof OWLNamedClass) result.add(aClass);
    } // while
  } // getMatchingOWLNamedClasses
    
  public static void getMatchingOWLProperties(String prefix, Set<RDFResource> result, OWLModel owlModel)
  {
    Set<RDFResource> localResult = new HashSet<RDFResource>(); 
    Iterator iterator;
    
    OWLResourceNameMatcher.addMatchingRDFProperties(prefix, localResult, owlModel);
    iterator = localResult.iterator();
    while (iterator.hasNext()) {
      RDFProperty aProperty = (RDFProperty)iterator.next();
      if (aProperty instanceof OWLProperty) result.add(aProperty);
    } // while
  } // getMatchingOWLProperties
    
  public static void getMatchingOWLIndividuals(String prefix, Set result, OWLModel owlModel)
  {
    Set<RDFResource> localResult = new HashSet<RDFResource>(); 
    Iterator iterator;
    
    OWLResourceNameMatcher.addMatchingRDFIndividuals(prefix, localResult, owlModel);
    iterator = localResult.iterator();
    while (iterator.hasNext()) {
      RDFIndividual anIndividual = (RDFIndividual)iterator.next();
      if (anIndividual instanceof OWLIndividual) result.add(anIndividual);
    } // while
  } // getMatchingOWLIndividuals

} // SWRLResourceNameMatcher
