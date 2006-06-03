package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

import java.util.Set;

public class DefaultSWRLIndividualPropertyAtom extends DefaultSWRLAtom implements SWRLIndividualPropertyAtom {

    public DefaultSWRLIndividualPropertyAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    } // DefaultSWRLIndividualPropertyAtom


    public DefaultSWRLIndividualPropertyAtom() {
    }


    public void getReferencedInstances(Set set) {
        Object argument1 = getArgument1();
        if (argument1 != null) {
            set.add(argument1);
        }
        Object argument2 = getArgument2();
        if (argument2 != null) {
            set.add(argument2);
        }
        Slot slot = getPropertyPredicate();
        if (slot != null) {
            set.add(slot);
        }
    }


    public RDFResource getArgument1() {
        return (RDFResource) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
    } // getArgument1


    public void setArgument1(RDFResource iObject) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), iObject);
    } // setArgument1


    public RDFResource getArgument2() {
        return (RDFResource) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2));
    } // getArgument2


    public void setArgument2(RDFResource iObject) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2), iObject);
    } // setArgument2


    public OWLObjectProperty getPropertyPredicate() {
        return (OWLObjectProperty) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.PROPERTY_PREDICATE));
    } // getPropertyPredicate


    public void setPropertyPredicate(OWLObjectProperty objectSlot) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.PROPERTY_PREDICATE), objectSlot);
    } // setPropertyPredicate


    public String getBrowserText() {
        String s = "";

        if (getPropertyPredicate() == null || getArgument1() == null || getArgument2() == null) return super.getBrowserText();

        s += getPropertyPredicate().getBrowserText() + "(";
        s += getArgument1().getBrowserText() + ", ";
        s += getArgument2().getBrowserText() + ")";

        return s;

    } // getBrowserText

} // DefaultSWRLIndividualPropertyAtom


