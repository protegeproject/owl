package edu.stanford.smi.protegex.owl.model.event;

import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyAdapter implements PropertyListener {

    /**
     * @deprecated
     */
    public final void directSubslotAdded(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getSubslot() instanceof RDFProperty) {
            subpropertyAdded((RDFProperty) event.getSlot(), (RDFProperty) event.getSubslot());
        }
    }


    /**
     * @deprecated
     */
    public final void directSubslotMoved(SlotEvent event) {
        // Not supported in OWL
    }


    /**
     * @deprecated
     */
    public final void directSubslotRemoved(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getSubslot() instanceof RDFProperty) {
            subpropertyRemoved((RDFProperty) event.getSlot(), (RDFProperty) event.getSubslot());
        }
    }


    /**
     * @deprecated
     */
    public final void directSuperslotAdded(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getSubslot() instanceof RDFProperty) {
            superpropertyAdded((RDFProperty) event.getSlot(), (RDFProperty) event.getSubslot());
        }
    }


    /**
     * @deprecated
     */
    public final void directSuperslotRemoved(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getSubslot() instanceof RDFProperty) {
            superpropertyRemoved((RDFProperty) event.getSlot(), (RDFProperty) event.getSubslot());
        }
    }


    // Overload this to do something useful
    public void subpropertyAdded(RDFProperty property, RDFProperty subproperty) {
        // Do nothing
    }


    // Overload this to do something useful
    public void subpropertyRemoved(RDFProperty property, RDFProperty subproperty) {
        // Do nothing
    }


    // Overload this to do something useful
    public void superpropertyAdded(RDFProperty property, RDFProperty superproperty) {
        // Do nothing
    }


    // Overload this to do something useful
    public void superpropertyRemoved(RDFProperty property, RDFProperty superproperty) {
        // Do nothing
    }


    /**
     * @deprecated
     */
    public final void templateSlotClsAdded(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getCls() instanceof RDFSClass) {
            unionDomainClassAdded((RDFProperty) event.getSlot(), (RDFSClass) event.getCls());
        }
    }


    /**
     * @deprecated
     */
    public final void templateSlotClsRemoved(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getCls() instanceof RDFSClass) {
            unionDomainClassRemoved((RDFProperty) event.getSlot(), (RDFSClass) event.getCls());
        }
    }


    // Overload this to do something useful
    public void unionDomainClassAdded(RDFProperty property, RDFSClass rdfsClass) {
        // Do nothing
    }


    // Overload this to do something useful
    public void unionDomainClassRemoved(RDFProperty property, RDFSClass rdfsClass) {
        // Do nothing
    }
}
