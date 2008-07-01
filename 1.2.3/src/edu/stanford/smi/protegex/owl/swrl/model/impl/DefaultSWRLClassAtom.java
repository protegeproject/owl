package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

import java.util.Set;

public class DefaultSWRLClassAtom extends DefaultSWRLAtom implements SWRLClassAtom {

    public DefaultSWRLClassAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    } // DefaultSWRLClassAtom


    public DefaultSWRLClassAtom() {
    }


    public void getReferencedInstances(Set set) {
        Cls cls = getClassPredicate();
        if (cls != null) {
            set.add(cls);
        }
        Object argument1 = getArgument1();
        if (argument1 != null) {
            set.add(argument1);
        }
    }


    public RDFResource getArgument1() {
        return (RDFResource) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
    } // getArgument1


    public void setArgument1(RDFResource iObject) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), iObject);
    } // setArgument1


    public RDFSClass getClassPredicate() {
        return (RDFSClass) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.CLASS_PREDICATE));
    } // getClassPredicate


    public void setClassPredicate(RDFSClass aClass) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.CLASS_PREDICATE), aClass);
    } // setClassPredicate


    public String getBrowserText() {

        RDFSClass aClass = getClassPredicate();
        String clsStr = aClass != null ?
                aClass.getNestedBrowserText() : "<classPredicate>";
        RDFResource argument = getArgument1();
        String argStr = argument != null ?
                argument.getBrowserText() : "<argument1>";

        return clsStr + "(" + argStr + ")";

    } // getBrowserText

} // DefaultSWRLClassAtom


