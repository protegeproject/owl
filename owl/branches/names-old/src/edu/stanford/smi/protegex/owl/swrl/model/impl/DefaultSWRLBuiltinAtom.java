package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

import java.util.Set;

public class DefaultSWRLBuiltinAtom extends DefaultSWRLAtom implements SWRLBuiltinAtom {

    public DefaultSWRLBuiltinAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    } // DefaultSWRLBuiltinAtom


    public DefaultSWRLBuiltinAtom() {
    }


    public void getReferencedInstances(Set set) {
        RDFList arguments = getArguments();
        if (arguments != null) {
            for (int size = arguments.size(); size > 0; size--) {
                Object first = arguments.getFirst();
                if (first instanceof RDFResource)
                    set.add(first);
                arguments = arguments.getRest();
            }
        }
        SWRLBuiltin builtin = getBuiltin();
        if (builtin != null) {
            set.add(builtin);
        }
    }


    public RDFList getArguments() {
        return (RDFList) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENTS));
    } // getArguments


    public void setArguments(RDFList arguments) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENTS), arguments);
    } // setArgument1


    public SWRLBuiltin getBuiltin() {
        return (SWRLBuiltin) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BUILTIN));
    } // SWRLBuiltin


    public void setBuiltin(SWRLBuiltin swrlBuiltin) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BUILTIN), swrlBuiltin);
    } // swrlBuiltin


    public String getBrowserText() {
        String s = "";

        if (getBuiltin() == null || getArguments() == null) return super.getBrowserText();

        s += getBuiltin().getBrowserText() + "(";

        RDFList list = getArguments();
        for (int i = list.size(); i > 0; i--) {
            Object o = list.getFirst();
            if (o instanceof RDFResource)
                s += ((RDFResource) o).getBrowserText();
            else {
                RDFSLiteral l = list.getFirstLiteral();
                s += SWRLUtil.getSWRLBrowserText(l);
            }
            list = list.getRest();
            if (list.size() > 0) {
                s += ", ";
            }
        }

        s += ")";

        return s;

    } // getBrowserText

} // DefaultSWRLBuiltinAtom
