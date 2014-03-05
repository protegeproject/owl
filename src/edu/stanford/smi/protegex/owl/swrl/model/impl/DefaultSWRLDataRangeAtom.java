package edu.stanford.smi.protegex.owl.swrl.model.impl;

import java.util.Set;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

public class DefaultSWRLDataRangeAtom extends DefaultSWRLAtom implements SWRLDataRangeAtom
{

	public DefaultSWRLDataRangeAtom(KnowledgeBase kb, FrameID id)
	{
		super(kb, id);
	}

	public DefaultSWRLDataRangeAtom()
	{
	}

	@Override
	public void getReferencedInstances(Set<RDFResource> set)
	{
		RDFObject argument1 = getArgument1();
		if (argument1 != null && argument1 instanceof SWRLVariable) {
			set.add((SWRLVariable)argument1);
		}
		Instance dataRange = getDataRange();
		if (dataRange != null && dataRange instanceof RDFResource) {
			set.add((RDFResource)dataRange);
		}
	}

	/**
	 * This argument is either an RDFList of RDFSLiterals, or a SWRLVariable.
	 */
	@Override
	public RDFObject getArgument1()
	{
		Object o = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
		if (o instanceof RDFResource) {
			return (RDFResource)o;
		} else {
			return getPropertyValueLiteral(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
		}
	}

	/**
	 * This argument is either an RDFList of RDFSLiterals, or a SWRLVariable.
	 */
	@Override
	public void setArgument1(RDFObject dObject)
	{
		setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), dObject);
	}

	@Override
	public OWLDataRange getDataRange()
	{
		Object propertyValue = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.DATA_RANGE));

		if (propertyValue instanceof OWLDataRange)
			return (OWLDataRange)propertyValue;
		else
			return null;
	}

	@Override
	public void setDataRange(OWLDataRange dataRange)
	{
		setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.DATA_RANGE), dataRange);
	}

	@Override
	public String getBrowserText()
	{
		Object dataRange = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.DATA_RANGE));
		RDFObject dObject = getArgument1();
		String s = "";

		s += SWRLUtil.getSWRLBrowserText(dataRange, "DATA_RANGE");
		s += "(";
		s += SWRLUtil.getSWRLBrowserText(dObject, "ARGUMENT1");
		s += ")";

		return s;
	}
}
