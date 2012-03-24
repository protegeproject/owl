package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLComplementClass extends AbstractOWLLogicalClass
        implements OWLComplementClass {

    /**
     * The unicode operator symbol for this kind of class
     */
    public final static char OPERATOR = '\u00AC';


    public DefaultOWLComplementClass(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultOWLComplementClass() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLComplementClass(this);
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof OWLComplementClass) {
            return getComplement().equalsStructurally(((OWLComplementClass) object).getComplement());
        }
        else {
            return false;
        }
    }


    public RDFSClass getComplement() {
        return (RDFSClass) getPropertyValue(getOperandsProperty());
    }


    public String getIconName() {
        return OWLIcons.OWL_COMPLEMENT_CLASS;
    }


    public Collection getOperands() {
        RDFSClass complement = getComplement();
        if (complement == null) {
            return Collections.EMPTY_LIST;
        }
        else {
            return Collections.singleton(complement);
        }
    }


    public RDFProperty getOperandsProperty() {
        return getOWLModel().getRDFProperty(OWLNames.Slot.COMPLEMENT_OF);
    }


    public char getOperatorSymbol() {
        return OPERATOR;
    }


    public String getNestedBrowserText() {
        return getBrowserText();
    }


    public void setComplement(RDFSClass complement) {
        setOwnSlotValue(getOperandsProperty(), complement);
    }


    /**
     * Overloaded to throw an IllegalArgumentException if the number of operands is greater
     * than 1 (there can be only one complement).
     *
     * @param clses the complement class
     */
    public void setOperands(Collection clses) {
        if (clses != null && clses.size() > 1) {
            throw new IllegalArgumentException("Only one complement class allowed.");
        }
        setPropertyValues(getOperandsProperty(), clses);
    }
}
