package edu.stanford.smi.protegex.owl.swrl.model.impl;

import java.util.Set;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

public class DefaultSWRLBuiltinAtom extends DefaultSWRLAtom implements SWRLBuiltinAtom 
{
  public DefaultSWRLBuiltinAtom(KnowledgeBase kb, FrameID id) { super(kb, id);} 
  public DefaultSWRLBuiltinAtom() {}

  public void getReferencedInstances(Set<RDFResource> set) {
    RDFList arguments = getArguments();
    if (arguments != null) {
      for (int size = arguments.size(); size > 0; size--) {
        Object first = arguments.getFirst();
        if (first instanceof RDFResource) set.add((RDFResource)first);
        arguments = arguments.getRest();
      } // for
    } // if
    SWRLBuiltin builtin = getBuiltin();
    if (builtin != null) set.add(builtin);
  } // getReferencedInstances

  public RDFList getArguments() { return (RDFList) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENTS)); }

  public void setArguments(RDFList arguments) { setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENTS), arguments); }

  public SWRLBuiltin getBuiltin() 
  {
    Object propertyValue = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BUILTIN));
    if (propertyValue instanceof SWRLBuiltin) return (SWRLBuiltin)propertyValue;
    else return null;
  } // SWRLBuiltin

  public void setBuiltin(SWRLBuiltin swrlBuiltin) { setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BUILTIN), swrlBuiltin); } 

  //TODO: Protege-OWL is clever about RDFLists so if an argument is deleted from the ontology, the deleted item is removed from the
  //list (unless it is the last item). Thus, there is no way of detecting deletions of non-last arguments for the moment.

  public String getBrowserText() 
  {
    SWRLBuiltin builtIn = getBuiltin();
    RDFList list = getArguments();
    Object propertyValue = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BUILTIN));
    String s = "";

    if (builtIn == null && propertyValue != null) s = "<DELETED_BUILTIN [" + SWRLUtil.getSWRLBrowserText(propertyValue, "BUILTIN") + "]>";
    else s = SWRLUtil.getSWRLBrowserText(propertyValue, "BUILTIN");

    s += "(";

    if (list != null) {
      for (int i = list.size(); i > 0; i--) {
        Object o = list.getFirst();
        if (o == null) s += "<DELETED_LIST>";
        else if (o instanceof RDFUntypedResource) {
          s += SWRLUtil.getSWRLBrowserText(o, "ARGUMENT");
        } else if (o instanceof RDFResource) {
          s += SWRLUtil.getSWRLBrowserText(o, "ARGUMENT");
        } else {
          RDFSLiteral l = list.getFirstLiteral();
          s += SWRLUtil.getSWRLBrowserText(l, "ARGUMENT");
        } // if
        list = list.getRest();
        if (list == null) s += ", <DELETED_LIST>";
          else if (list.size() > 0) s += ", ";
      } // for
    } else s += "<DELETED_ARGUMENTS>";
    
    s += ")";

    return s;  
  } // getBrowserText

} // DefaultSWRLBuiltinAtom
