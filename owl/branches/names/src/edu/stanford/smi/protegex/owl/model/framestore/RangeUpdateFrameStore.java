package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.Collection;
import java.util.Collections;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;

public class RangeUpdateFrameStore extends  FrameStoreAdapter {
    
    private RDFProperty rdfsRangeProperty;
    private Slot valueType;
    
    public RangeUpdateFrameStore(OWLModel owlModel) {
        rdfsRangeProperty = owlModel.getRDFSRangeProperty();
        valueType = owlModel.getSystemFrames().getValueTypeSlot();
    }
    
    /**
     * Updates the ValueType of a datatype slot in response to changes in the range.
     */
    private void updatePropertyValueType(RDFProperty property, Collection values) {
        ValueType newValueType = ValueType.ANY;
        if (property instanceof OWLObjectProperty && property.getSuperpropertyCount() == 0) {
            newValueType = ValueType.INSTANCE;
        }
        if (!values.isEmpty()) {
            Object range = values.iterator().next();
            if (range instanceof RDFSDatatype) {
                newValueType = XMLSchemaDatatypes.getValueType(((RDFSDatatype) range).getURI());
            }
            else if (range instanceof RDFSClass) {
                newValueType = ValueType.INSTANCE;
            }
            else if (range instanceof OWLDataRange) {
                RDFSDatatype datatype = ((OWLDataRange) range).getRDFDatatype();
                if (datatype != null) {
                    newValueType = XMLSchemaDatatypes.getValueType(datatype.getURI());
                }
            }
        }
        if (newValueType != ((Slot) property).getValueType()) {
            ((Slot) property).setValueType(newValueType);
        }
        if (newValueType == ValueType.INSTANCE) {
            ((Slot) property).setAllowedClses(Collections.EMPTY_LIST);
        }
    }
    
    

    private void updatePropertyAllowedClasses(RDFProperty property, Collection values) {
        ((Slot) property).setValueType(ValueType.INSTANCE);
        RDFSClass rangeClass = (RDFSClass) values.iterator().next();
        if (rangeClass instanceof OWLUnionClass) {
            ((Slot) property).setAllowedClses(((OWLUnionClass) rangeClass).getOperands());
        }
        else {
            ((Slot) property).setAllowedClses(Collections.singleton(rangeClass));
        }
    }
    
    /*
     * FrameStore implementations
     */
    
    @Override
    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        if (frame instanceof RDFProperty && slot.equals(rdfsRangeProperty)) {
            if (values.size() > 0 && values.iterator().next() instanceof RDFSClass) {
                updatePropertyAllowedClasses((RDFProperty) frame, values);
            }
            else {
                updatePropertyValueType((RDFProperty) frame, values);
            }
        }
        super.setDirectOwnSlotValues(frame, slot, values);
    }
    
    @Override
    public Slot createSlot(FrameID id, Collection directTypes, Collection directSuperslots, boolean loadDefaults) {
        Slot slot = super.createSlot(id, directTypes, directSuperslots, loadDefaults);
        if (slot instanceof OWLObjectProperty && directSuperslots.isEmpty()) {
            slot.setValueType(ValueType.INSTANCE);
        }
        return slot;
    }
    
    @Override
    public void addDirectSuperslot(Slot slot, Slot superSlot) {
        super.addDirectSuperslot(slot, superSlot);
        if (slot instanceof RDFProperty && ((RDFProperty) slot).getRange() == null) {
            slot.setDirectOwnSlotValue(valueType, null);
        }
    }
    
    @Override
    public void removeDirectSuperslot(Slot slot, Slot superslot) {
        super.removeDirectSuperslot(slot, superslot);
        if (slot instanceof OWLObjectProperty &&
            slot.getDirectSuperslotCount() == 0 &&
            slot.getAllowedClses().isEmpty()) {
            slot.setValueType(ValueType.INSTANCE);
        }
    }
    
}
