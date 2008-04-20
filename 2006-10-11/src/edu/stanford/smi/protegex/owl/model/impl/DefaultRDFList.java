package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultRDFList extends DefaultRDFIndividual implements RDFList {

    public DefaultRDFList(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultRDFList() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitRDFList(this);
    }


    public void append(Object value) {

        boolean done = false;
        RDFList current = this;

        while (!done) {
            if (current.getFirst() == null) {
                current.setFirst(value);
                current.setRest(getOWLModel().getRDFNil());
                done = true;
            }
            else if (current.getRest() == null ||
                     getOWLModel().getRDFNil().equals(current.getRest())) {
                RDFSNamedClass listClass = (RDFSNamedClass) getDirectType();
                RDFList newRest = (RDFList) listClass.createAnonymousInstance();
                newRest.setFirst(value);
                newRest.setRest(getOWLModel().getRDFNil());
                current.setRest(newRest);
                done = true;
            }
            else {
                current = current.getRest(); // Recursion into tail
            }
        }
    }


    public boolean contains(Object value) {
        return getValues().contains(value);
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof RDFList) {
            RDFList list = (RDFList) object;
            if (getValues().size() == list.getValues().size()) {
                Iterator it = getValues().iterator();
                for (Iterator listIt = list.getValues().iterator(); listIt.hasNext();) {
                    OWLModel model = getOWLModel();
                    RDFObject curListObj = model.asRDFObject(listIt.next());
                    RDFObject curThisObj = model.asRDFObject(it.next());
                    if (curListObj != null &&
                        curThisObj != null) {
                        if (curListObj.equalsStructurally(curThisObj) == false) {
                            return false;
                        }
                    }
                    else {
                        if (curListObj != curThisObj) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }


    public String getBrowserText() {
        String str = getDirectType().getBrowserText() + " (";
        for (Iterator it = getValues().iterator(); it.hasNext();) {
            Object value = it.next();
            if (value instanceof Instance) {
                str += ((Instance) value).getBrowserText();
            }
            else {
                str += value;
            }
            if (it.hasNext()) {
                str += ", ";
            }
        }
        return str + ")";
    }


    public Object getFirst() {
        RDFProperty firstProperty = getOWLModel().getRDFFirstProperty();
        return getPropertyValue(firstProperty);
    }


    public RDFSLiteral getFirstLiteral() {
        RDFProperty firstProperty = getOWLModel().getRDFFirstProperty();
        return getPropertyValueLiteral(firstProperty);
    }


    public List getValueLiterals() {
        List values = getValues();
        return ((AbstractOWLModel) getOWLModel()).getValueLiterals(getOWLModel(), values);
    }


    public RDFList getRest() {
        RDFProperty restProperty = getOWLModel().getRDFRestProperty();
        return (RDFList) getPropertyValue(restProperty);
    }


    public RDFList getStart() {
        Collection refs = ((KnowledgeBase) getOWLModel()).getReferences(this, 1000);
        for (Iterator it = refs.iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (reference.getFrame() instanceof RDFList && reference.getSlot().equals(getOWLModel().getRDFRestProperty())) {
                return ((RDFList) reference.getFrame()).getStart();
            }
        }
        return this;
    }


    public List getValues() {
        List result = new ArrayList();
        RDFList l = this;
        while (l != null && l.getFirst() != null) {
            result.add(l.getFirst());
            l = l.getRest();
        }
        return result;
    }


    public boolean isClosed() {
        RDFList rest = getRest();
        if (rest == null) {
            return false;
        }
        else if (rest.equals(getOWLModel().getRDFNil())) {
            return true;
        }
        else {
            return rest.isClosed();
        }
    }


    public static void removeListValue(RDFResource resource, RDFProperty property, Object value) {
        RDFList list = (RDFList) resource.getPropertyValue(property);
        List values = list.getValues();
        int index = values.indexOf(value);
        removeListValue(resource, property, list, index);
    }


    public static void removeListValue(RDFResource resource, RDFProperty property, RDFList li, int index) {
        if (index == 0) {
            RDFList rest = li.getRest();
            if (li.getOWLModel().getRDFNil().equals(rest)) {
                rest = null;
            }
            li.setRest(null);
            resource.setPropertyValue(property, rest);
            li.delete();
        }
        else {
            RDFList pred = null;
            while (index > 0) {
                index--;
                pred = li;
                li = li.getRest();
            }
            RDFList rest = li.getRest();
            li.setRest(null);
            pred.setRest(rest);
            li.delete();
        }
    }


    public void setFirst(Object value) {
        Slot slot = getKnowledgeBase().getSlot(RDFNames.Slot.FIRST);
        setOwnSlotValue(slot, value);
    }


    public void setRest(RDFList rest) {
        Slot slot = getKnowledgeBase().getSlot(RDFNames.Slot.REST);
        setOwnSlotValue(slot, rest);
    }


    public int size() {
        return getValues().size();
    }
}
