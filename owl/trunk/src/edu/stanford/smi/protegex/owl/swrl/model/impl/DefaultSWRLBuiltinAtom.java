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
	public DefaultSWRLBuiltinAtom(KnowledgeBase kb, FrameID id)
	{
		super(kb, id);
	}

	public DefaultSWRLBuiltinAtom()
	{
	}

	@Override
	public void getReferencedInstances(Set<RDFResource> set)
	{
		RDFList arguments = getArguments();
		if (arguments != null) {
			for (int size = arguments.size(); size > 0; size--) {
				Object first = arguments.getFirst();
				if (first instanceof RDFResource)
					set.add((RDFResource)first);
				arguments = arguments.getRest();
			}
		}
		SWRLBuiltin builtin = getBuiltin();
		if (builtin != null)
			set.add(builtin);
	}

	@Override
	public RDFList getArguments()
	{
		return (RDFList)getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENTS));
	}

	@Override
	public void setArguments(RDFList arguments)
	{
		setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENTS), arguments);
	}

	@Override
	public SWRLBuiltin getBuiltin()
	{
		Object propertyValue = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BUILTIN));
		if (propertyValue instanceof SWRLBuiltin)
			return (SWRLBuiltin)propertyValue;
		else
			return null;
	} // SWRLBuiltin

	@Override
	public void setBuiltin(SWRLBuiltin swrlBuiltin)
	{
		setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BUILTIN), swrlBuiltin);
	}

	// TODO: Protege-OWL is clever about RDFLists so if an argument is deleted from the ontology, the deleted item is
	// removed from the list (unless it is the last item). Thus, there is no way of detecting deletions of non-last
	// arguments for the moment.

	@Override
	public String getBrowserText()
	{
		SWRLBuiltin builtIn = getBuiltin();
		RDFList list = getArguments();
		Object propertyValue = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BUILTIN));
		StringBuilder sb = new StringBuilder();

		if (builtIn == null && propertyValue != null)
			sb.append("<DELETED_BUILTIN [" + SWRLUtil.getSWRLBrowserText(propertyValue, "BUILTIN") + "]>");
		else
			sb.append(SWRLUtil.getSWRLBrowserText(propertyValue, "BUILTIN"));

		sb.append("(");

		if (list != null) {
			for (int i = list.size(); i > 0; i--) {
				Object o = list.getFirst();
				if (o == null)
					sb.append("<DELETED_LIST>");
				else if (o instanceof RDFUntypedResource) {
					sb.append(SWRLUtil.getSWRLBrowserText(o, "ARGUMENT"));
				} else if (o instanceof RDFResource) {
					sb.append(SWRLUtil.getSWRLBrowserText(o, "ARGUMENT"));
				} else {
					RDFSLiteral l = list.getFirstLiteral();
					sb.append(SWRLUtil.getSWRLBrowserText(l, "ARGUMENT"));
				}
				list = list.getRest();
				if (list == null)
					sb.append(", <DELETED_LIST>");
				else if (list.size() > 0)
					sb.append(", ");
			}
		} else
			sb.append("<DELETED_ARGUMENTS>");

		sb.append(")");

		return sb.toString();
	}
}
