package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

public class TypeUpdateFrameStore extends FrameStoreAdapter {
    private RDFSNamedClass untypedResource;
    private RDFSNamedClass functionalPropertyClass;
    private RDFSNamedClass restrictionClass;
    private RDFSNamedClass owlClass;
    
    private RDFProperty rdfType;
    private RDFProperty rdfSubClassOfProperty;
    private RDFSNamedClass annotationPropertyClass;
    

    
    public TypeUpdateFrameStore(OWLModel owlModel) {
        untypedResource = owlModel.getRDFUntypedResourcesClass();
        functionalPropertyClass = owlModel.getOWLFunctionalPropertyClass();
        restrictionClass = owlModel.getSystemFrames().getOwlRestrictionClass();
        owlClass = owlModel.getOWLNamedClassClass();
        annotationPropertyClass = owlModel.getOWLAnnotationPropertyClass();
        
        rdfType = owlModel.getRDFTypeProperty();
        rdfSubClassOfProperty = owlModel.getRDFSSubClassOfProperty();
    }
    
    /*
     * FrameStore implementations
     */
    
    
    @Override
    public Cls createCls(FrameID id, Collection directTypes, Collection directSuperclasses, boolean loadDefaults) {
        Cls cls = super.createCls(id, directTypes, directSuperclasses, loadDefaults);
        if (cls instanceof RDFSNamedClass) {
            ((RDFSNamedClass) cls).setPropertyValues(rdfSubClassOfProperty, directSuperclasses);
            super.setDirectOwnSlotValues(cls, rdfType, directTypes);
        }
        else if (cls instanceof OWLRestriction) {
            super.setDirectOwnSlotValues(cls, rdfType, Collections.singleton(restrictionClass));
        }
        else if (cls instanceof OWLAnonymousClass) {
            super.setDirectOwnSlotValues(cls, rdfType, Collections.singleton(owlClass));
        }
        return cls;
    }
    
    @Override
    public Slot createSlot(FrameID id, Collection directTypes, Collection directSuperslots, boolean loadDefaults) {
        Slot slot = super.createSlot(id, directTypes, directSuperslots, loadDefaults);
        if (slot instanceof RDFProperty) {
            super.setDirectOwnSlotValues(slot, rdfType, directTypes);
        }
        return slot;
    }
    
    @Override
    public SimpleInstance createSimpleInstance(FrameID id, Collection directTypes, boolean loadDefaults) {
        SimpleInstance instance = super.createSimpleInstance(id, directTypes, loadDefaults);
        if (instance instanceof RDFResource && !directTypes.contains(untypedResource)) {
            super.setDirectOwnSlotValues(instance, rdfType, directTypes);
        }
        return instance;
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    public void addDirectType(Instance instance, Cls type) {
        super.addDirectType(instance, type);
        instance = (Instance) super.getFrame(instance.getFrameID());
        if (instance instanceof RDFProperty) {
            if (type.equals(functionalPropertyClass)) {
                ((Slot) instance).setAllowsMultipleValues(false);
            }
        }
        if (instance instanceof OWLRestriction) {
            super.setDirectOwnSlotValues(instance, rdfType, Collections.singleton(restrictionClass));
        }
        else if (instance instanceof OWLAnonymousClass) {
            super.setDirectOwnSlotValues(instance, rdfType, Collections.singleton(owlClass));
        }
        else if (instance instanceof RDFResource &&
            !instance.getDirectTypes().contains(type) &&
            !type.equals(untypedResource)) {
            Collection types = new ArrayList(super.getDirectOwnSlotValues(instance, rdfType));
            types.add(type);
            super.setDirectOwnSlotValues(instance, rdfType, types);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void removeDirectType(Instance instance, Cls directType) {
        if (instance instanceof RDFProperty) {
            if (directType.equals(functionalPropertyClass)) {
                ((Slot) instance).setAllowsMultipleValues(true);
            }
        }
        if (instance instanceof OWLRestriction) {
            super.setDirectOwnSlotValues(instance, rdfType, Collections.singleton(restrictionClass));
        }
        else if (instance instanceof OWLAnonymousClass) {
            super.setDirectOwnSlotValues(instance, rdfType, Collections.singleton(owlClass));
        }
        else if (instance instanceof RDFResource) {
            Collection types = new ArrayList(super.getDirectOwnSlotValues(instance, rdfType));
            if (types.contains(directType)) {
                types.remove(directType);
                super.setDirectOwnSlotValues(instance, rdfType, types);
            }
        }
        super.removeDirectType(instance, directType);
    }
    
    @Override
    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        super.setDirectOwnSlotValues(frame, slot, values);
        if (frame instanceof RDFResource && slot.equals(rdfType)) {
            Collection directTypes = super.getDirectTypes((RDFResource) frame);
            for (Object newType : values) {
                if (!directTypes.contains(newType) && newType instanceof Cls) {
                    super.addDirectType((RDFResource) frame, (Cls) newType); 
                }
            }
            for (Object oldType : directTypes) {
                if (!values.contains(oldType) && oldType instanceof Cls) {
                    super.removeDirectType((RDFResource) frame, (Cls) oldType);
                }
            }
        }
    }
    
    @Override
    public void addDirectSuperslot(Slot slot, Slot superSlot) {
        super.addDirectSuperslot(slot, superSlot);
        if (slot instanceof RDFProperty) {
            RDFProperty property = (RDFProperty) slot;
            if (property instanceof OWLProperty && superSlot instanceof OWLProperty && ((OWLProperty) superSlot).isAnnotationProperty()) {
                addDirectType(slot, annotationPropertyClass);
            }
        }
    }
}
