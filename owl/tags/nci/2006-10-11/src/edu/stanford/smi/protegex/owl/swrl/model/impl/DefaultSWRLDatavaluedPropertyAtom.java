package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;

import java.util.Set;

public class DefaultSWRLDatavaluedPropertyAtom extends DefaultSWRLAtom implements SWRLDatavaluedPropertyAtom {

    public DefaultSWRLDatavaluedPropertyAtom(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    } // DefaultSWRLDatavaluedPropertyAtom


    public DefaultSWRLDatavaluedPropertyAtom() {
    }


    public void getReferencedInstances(Set set) {
        Object argument1 = getArgument1();
        if (argument1 != null) {
            set.add(argument1);
        }
        RDFProperty property = getPropertyPredicate();
        if (property != null) {
            set.add(property);
        }
    }


    public RDFResource getArgument1() {
        return (RDFResource) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1));
    } // getArgument1


    public void setArgument1(RDFResource iObject) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT1), iObject);
    } // setArgument1


    public RDFObject getArgument2() {
        Object o = getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2));
        if (o instanceof RDFResource) {
            return (RDFResource) o;
        }
        else {
            return getPropertyValueLiteral(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2));
        }
    } // getArgument2


    public void setArgument2(RDFObject dObject) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.ARGUMENT2), dObject);
    } // setArgument2


    public OWLDatatypeProperty getPropertyPredicate() {
        return (OWLDatatypeProperty) getDirectOwnSlotValue(getOWLModel().getRDFProperty(SWRLNames.Slot.PROPERTY_PREDICATE));
    } // getPropertyPredicate


    public void setPropertyPredicate(OWLDatatypeProperty datatypeSlot) {
        setOwnSlotValue(getOWLModel().getRDFProperty(SWRLNames.Slot.PROPERTY_PREDICATE), datatypeSlot);
    } // setPropertyPredicate


    public String getBrowserText() {
        String s = "";

        final RDFObject argument2 = getArgument2();
        if (getPropertyPredicate() == null || getArgument1() == null || argument2 == null) return super.getBrowserText();

        s += getPropertyPredicate().getBrowserText() + "(";
        s += getArgument1().getBrowserText() + ", ";
        s += SWRLUtil.getSWRLBrowserText(argument2) + ")";

        return s;

    } // getBrowserText

} // DefaultSWRLDatavaluedPropertyAtom


