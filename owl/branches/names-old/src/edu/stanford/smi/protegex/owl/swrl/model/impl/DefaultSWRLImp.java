package edu.stanford.smi.protegex.owl.swrl.model.impl;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParser;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DefaultSWRLImp extends DefaultOWLIndividual implements SWRLImp {

    public DefaultSWRLImp(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultSWRLImp() {
    }


    public SWRLImp createClone() {
        String text = getBrowserText();
        OWLModel owlModel = (OWLModel) getKnowledgeBase();
        SWRLParser parser = new SWRLParser(owlModel);
        parser.setParseOnly(false);
        try {
            return parser.parse(text);
        }
        catch (Exception ex) {
            return null;  // Cannot happen
        }
    }


    private void deleteHeadAndBody() {
        Slot directInstancesSlot = getKnowledgeBase().getSlot(Model.Slot.DIRECT_INSTANCES);
        Collection instances = getReferencedInstances();
        for (Iterator it = instances.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof SWRLIndividual && !(o instanceof SWRLBuiltin)) {
                SWRLIndividual swrlIndividual = (SWRLIndividual) o;
                if (!swrlIndividual.isDeleted()) {
                    Collection references = getKnowledgeBase().getReferences(swrlIndividual, -1);
                    boolean hasExternalRef = false;
                    for (Iterator rit = references.iterator(); rit.hasNext();) {
                        Reference reference = (Reference) rit.next();
                        if (!directInstancesSlot.equals(reference.getSlot())) {
                            Frame frame = reference.getFrame();
                            if (!instances.contains(frame) && !equals(frame)) {
                                hasExternalRef = true;
                            }
                        }
                    }
                    if (!hasExternalRef) {
                        swrlIndividual.delete();
                    }
                }
            }
        }
    }


    public void deleteImp() {
        deleteHeadAndBody();
        delete();
    }


    public SWRLAtomList getHead() {
        return (SWRLAtomList) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.HEAD));
    } // getHead


    public Icon getIcon() {
        return isEditable() ? SWRLIcons.getImpIcon() :
                OWLIcons.getReadOnlyIcon(SWRLIcons.getImpIcon(), "RoundedBoxFrame");
    }


    public Set getReferencedInstances() {
        Set set = new HashSet();
        getReferencedInstances(set);
        return set;
    }


    public void getReferencedInstances(Set set) {
        SWRLAtomList head = getHead();
        if (head != null) {
            set.add(head);
            head.getReferencedInstances(set);
        }
        SWRLAtomList body = getBody();
        if (body != null) {
            set.add(body);
            body.getReferencedInstances(set);
        }
    }


    public SWRLAtomList getBody() {
        return (SWRLAtomList) getPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BODY));
    } // getBody


    public void setBody(SWRLAtomList swrlAtomList) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.BODY), swrlAtomList);
    } // setBody


    public String getBrowserText() {

        SWRLAtomList body = getBody();
        SWRLAtomList head = getHead();
        if (body == null) {
            if (head == null) {
                return "<empty rule>";
            }
            else {
                return head.getBrowserText();
            }
        }
        else {
            String s = body.getBrowserText() + "  " + SWRLParser.IMP_CHAR + "  ";
            if (head == null) {
                return s + "<null>";
            }
            else {
                return s += head.getBrowserText();
            }
        }
    } // toString


    public void setHead(SWRLAtomList swrlAtomList) {
        setPropertyValue(getOWLModel().getRDFProperty(SWRLNames.Slot.HEAD), swrlAtomList);
    } // setHead


    public void setExpression(String parsableText) throws SWRLParseException {
        SWRLParser parser = new SWRLParser(getOWLModel());
        parser.parse(parsableText);
        deleteHeadAndBody();
        parser.setParseOnly(false);
        parser.parse(parsableText, this);
    }

} // DefaultSWRLImp
