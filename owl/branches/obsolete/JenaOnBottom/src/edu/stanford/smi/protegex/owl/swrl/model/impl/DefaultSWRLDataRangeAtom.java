package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

import java.util.Set;

public class DefaultSWRLDataRangeAtom extends DefaultSWRLAtom implements SWRLDataRangeAtom {

    public DefaultSWRLDataRangeAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    } // DefaultSWRLDataRangeAtom


    public DefaultSWRLDataRangeAtom() {
    }


    public void getReferencedInstances(Set set) {
        Object argument1 = getArgument1();
        if (argument1 != null && argument1 instanceof SWRLVariable) {
            set.add(argument1);
        }
        Instance dataRange = getDataRange();
        if (dataRange != null) {
            set.add(dataRange);
        }
    }


    /**
     * This argument is either an RDFList of RDFSLiterals, or
     * a SWRLVariable.
     */
    public RDFObject getArgument1() {
        Object o = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
        if (o instanceof RDFResource) {
            return (RDFResource) o;
        }
        else {
            return getPropertyValueLiteral(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
        }
    } // getArgument1


    /**
     * This argument is either an RDFList of RDFSLiterals, or
     * a SWRLVariable.
     */
    public void setArgument1(RDFObject dObject) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), dObject);
    } // setArgument1


    public RDFResource getDataRange() {
        return (RDFResource) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.DATA_RANGE));
    } // getDataRange


    public void setDataRange(RDFResource instance) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.DATA_RANGE), instance);
    } // setDataRange


    public String getBrowserText() {
        String s = "";

        if (getDataRange() == null || getArgument1() == null) return super.getBrowserText();

        s += "xsd:" + getDataRange().getBrowserText() + "(";

        RDFObject dObject = getArgument1();

        s += SWRLUtil.getSWRLBrowserText(dObject) + ")";

        return s;

    } // getBrowserText

} // DefaultSWRLDataRangeAtom


