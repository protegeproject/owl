package edu.stanford.smi.protegex.owl.swrl.model.impl;

import java.util.Set;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

public abstract class AbstractSWRLIndividualsAtom extends DefaultSWRLAtom implements SWRLIndividualsAtom
{

	public AbstractSWRLIndividualsAtom(KnowledgeBase kb, FrameID id)
	{
		super(kb, id);
	}

	public AbstractSWRLIndividualsAtom()
	{
	}

	protected abstract String getOperatorName();

	@Override
	public void getReferencedInstances(Set<RDFResource> set)
	{
	}

	@Override
	public RDFResource getArgument1()
	{
		return (RDFResource)getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
	}

	@Override
	public void setArgument1(RDFResource instance)
	{
		setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), instance);
	}

	@Override
	public RDFResource getArgument2()
	{
		return (RDFResource)getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2));
	}

	@Override
	public void setArgument2(RDFResource instance)
	{
		setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2), instance);
	}

	@Override
	public String getBrowserText()
	{
		String s = getOperatorName() + "(";

		RDFResource argument1 = getArgument1();
		if (argument1 == null) {
			s += "<null>";
		} else {
			s += argument1.getBrowserText();
		}
		s += ", ";
		RDFResource argument2 = getArgument2();
		if (argument2 == null) {
			s += "<null>";
		} else {
			s += argument2.getBrowserText();
		}
		s += ")";

		return s;
	}
}
