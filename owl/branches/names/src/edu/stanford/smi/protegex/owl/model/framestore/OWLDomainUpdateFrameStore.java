package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class OWLDomainUpdateFrameStore extends FrameStoreAdapter {
    
    private OWLModel owlModel;
    
    private OWLNamedClass owlThing;
    private RDFSNamedClass rdfPropertyClass;
    
    private RDFProperty rdfsDomainProperty;
    private Slot directDomainSlot;
    private RDFProperty superSlotsSlot;
    
    public OWLDomainUpdateFrameStore(OWLModel owlModel) {
        this.owlModel = owlModel;
        
        owlThing = owlModel.getOWLThingClass();
        rdfPropertyClass = owlModel.getRDFPropertyClass();
        
        rdfsDomainProperty = owlModel.getRDFSDomainProperty();
        directDomainSlot = owlModel.getSystemFrames().getDirectDomainSlot();
        superSlotsSlot = owlModel.getSystemFrames().getDirectSuperslotsSlot();
    }
    
    
    private void updateRDFSDomain(RDFProperty property) {
        Collection domainClses = super.getDirectDomain(property);
        RDFSClass newDomain = null;
        if (domainClses.size() == 1) {
            newDomain = (RDFSClass) domainClses.iterator().next();
        }
        else {
            newDomain = owlModel.createOWLUnionClass(domainClses);
        }
        super.setDirectOwnSlotValues(property, rdfsDomainProperty, Collections.singleton(newDomain));
    }
    
    /**
     * Updates the values of :SLOT-DIRECT-DOMAIN and :SLOT-DIRECT-TEMPLATE-SLOTS
     * in response to changes in rdfs:domain.
     *
     * @param slot
     * @param values
     */
    @SuppressWarnings("unchecked")
    private void updateSlotDomain(Slot slot, Collection values) {
        Collection<RDFSClass> newDomainClasses = new ArrayList<RDFSClass>();
        if (values.size() == 1 && values.iterator().next() instanceof RDFSClass) {
            RDFSClass cls = (RDFSClass) values.iterator().next();
            if (cls instanceof OWLUnionClass) {
                newDomainClasses.addAll(((OWLUnionClass) cls).getOperands());
            }
            else {
                newDomainClasses.add(cls);
            }
        }
        if (!newDomainClasses.isEmpty()) {
            Collection oldDomain = new ArrayList(super.getDirectDomain(slot));
            for (Object o : oldDomain) {
                if (o instanceof Cls && !newDomainClasses.contains(o)) {
                    super.removeDirectTemplateSlot((Cls) o, slot);
                }
            }
            for (RDFSClass newDomainClass : newDomainClasses) {
                if (!oldDomain.contains(newDomainClass)) {
                    super.addDirectTemplateSlot(newDomainClass, slot);
                }
            }
        }
        else {
            int superSlotsCount = super.getSuperslots(slot).size();
            if (superSlotsCount == 0) {
                super.setDirectOwnSlotValues(slot, directDomainSlot, Collections.singleton(owlThing));
            }
            else {
                super.setDirectOwnSlotValues(slot, directDomainSlot, Collections.emptyList());
            }
        }
    }
    
    private void updateAddSuperSlot(Slot slot) {
        if (slot instanceof RDFProperty) {
            RDFProperty property = (RDFProperty) slot;
            Collection values = super.getDirectOwnSlotValues(property, directDomainSlot);
            if (values.size() == 1 && values.contains(owlModel.getOWLThingClass())) {
                super.setDirectOwnSlotValues(slot, directDomainSlot, Collections.emptyList());
            }
        }
    }
    
    private void updateRemoveSuperSlot(Slot slot) {
        if (slot instanceof RDFProperty) {
            int valuesCount = super.getDirectOwnSlotValuesCount(slot, directDomainSlot);
            int superSlotsCount = super.getDirectOwnSlotValuesCount(slot, superSlotsSlot);
            if (valuesCount == 0 && superSlotsCount == 0) {
                super.addDirectTemplateSlot(owlModel.getOWLThingClass(), slot);
            }
        }
    }
    
    

    
    /* ****************************************************************************************
     * Frame Store Methods
     */

    
    @Override
    @SuppressWarnings("unchecked")
    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        if (frame instanceof RDFProperty && slot.equals(rdfsDomainProperty)) {
            if (values.size() > 1 && values.contains(owlThing)) {
                values = new ArrayList(values);
                values.remove(owlThing);
            }
            super.setDirectOwnSlotValues(frame, slot, values);
            updateSlotDomain((Slot) frame, values);
            return;
        }
        else if (slot.equals(owlModel.getRDFTypeProperty()) && values.contains(rdfPropertyClass)) {
            super.setDirectOwnSlotValues(frame, slot, values);
            Slot swizzledFrame = (Slot) super.getFrame(frame.getFrameID());
            Collection domains = super.getDirectDomain(swizzledFrame);
            if (domains != null && !domains.isEmpty()) {
                return;
            }
            Collection superProperties = super.getSuperslots(swizzledFrame);
            if (superProperties != null && !superProperties.isEmpty()) {
                return;
            }
            super.setDirectOwnSlotValues(swizzledFrame, directDomainSlot, Collections.singleton(owlThing));
            return;
        }
        else if (slot.equals(owlModel.getRDFSSubPropertyOfProperty()) && values != null) {
            super.setDirectOwnSlotValues(frame, slot, values); 
            if (frame instanceof Slot) {
                updateAddSuperSlot((Slot) frame);
            }
            return;
        }
        super.setDirectOwnSlotValues(frame, slot, values); 
    }
    
    @Override
    public void addDirectTemplateSlot(Cls cls, Slot slot) {
        super.addDirectTemplateSlot(cls, slot);
        if (slot instanceof RDFProperty && cls instanceof RDFSClass) {
            //printDeprecationWarning("addDirectTemplateSlot");
            updateRDFSDomain((RDFProperty) slot);
        }
    }
    
    @Override
    public void removeDirectTemplateSlot(Cls cls, Slot slot) {
        super.removeDirectTemplateSlot(cls, slot);
        if (slot instanceof RDFProperty && cls instanceof RDFSClass) {
            //printDeprecationWarning("removeDirectTemplateSlot");
            updateRDFSDomain((RDFProperty) slot);
        }
    }
    
    @Override
    public Slot createSlot(FrameID id, Collection directTypes, Collection directSuperslots, boolean loadDefaults) {
        Slot slot = super.createSlot(id, directTypes, directSuperslots, loadDefaults);
        if (slot instanceof RDFProperty) {
            addDirectTemplateSlot(owlModel.getOWLThingClass(), slot);
        }
        return slot;
    }
    
    @Override
    public void removeDirectSuperslot(Slot slot, Slot superslot) {
        super.removeDirectSuperslot(slot, superslot);
        updateRemoveSuperSlot(slot);
    }
    
    @Override
    public void addDirectSuperslot(Slot slot, Slot superSlot) {
        super.addDirectSuperslot(slot, superSlot);
        updateAddSuperSlot(slot);
    }

}
