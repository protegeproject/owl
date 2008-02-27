package edu.stanford.smi.protegex.owl.model.framestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;

public class ProtegeOWLFrameStore extends FrameStoreAdapter {
    
    private OWLModel owlModel;
    
    private Map<String, String> equivalentClsMap = new HashMap<String, String>();
    
    private Map<String, String> equivalentSlotMap = new HashMap<String, String>();
    
    public ProtegeOWLFrameStore(OWLModel owlModel) {
        this.owlModel = owlModel;
        
        addToEquivalentMap(Model.Cls.DIRECTED_BINARY_RELATION, equivalentClsMap);
        
        addToEquivalentMap(Model.Slot.TO, equivalentSlotMap);
        addToEquivalentMap(Model.Slot.FROM, equivalentSlotMap);
    
        addToEquivalentMap(Model.Cls.PAL_CONSTRAINT, equivalentClsMap);
        
       
        addToEquivalentMap(Model.Slot.CONSTRAINTS, equivalentSlotMap);
        addToEquivalentMap(Model.Slot.PAL_STATEMENT, equivalentSlotMap);
        addToEquivalentMap(Model.Slot.PAL_DESCRIPTION, equivalentSlotMap);
        addToEquivalentMap(Model.Slot.PAL_NAME, equivalentSlotMap);
        addToEquivalentMap(Model.Slot.PAL_RANGE, equivalentSlotMap);
    }
    
    private void addToEquivalentMap(String protegeName, Map<String, String> map) {
        String owlName = convertProtegeFrameNameToOwl(protegeName);
        map.put(protegeName, owlName);
        map.put(owlName, protegeName);
    }
    
    public static String convertProtegeFrameNameToOwl(String protegeName) {
        if (!protegeName.startsWith(":")) { return null; }
        return ProtegeNames.PROTEGE_OWL_NAMESPACE + protegeName.substring(1);
    }
    
    private Cls convertCls(Cls slot) {
        String otherName = equivalentClsMap.get(slot.getName());
        if (otherName != null) { 
            Frame frame = super.getFrame(otherName);
            if (frame != null && frame instanceof Cls) {
                return (Cls) frame;
            }
        }
        return null;
    }
    
    private Slot convertSlot(Slot slot) {
        String otherName = equivalentSlotMap.get(slot.getName());
        if (otherName != null) { 
            Frame frame = super.getFrame(otherName);
            if (frame != null && frame instanceof Slot) {
                return (Slot) frame;
            }
        }
        return null;
    }
    
    
    /*
     * Frame Store Method overrides
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        super.setDirectOwnSlotValues(frame, slot, values);
        Slot otherSlot = convertSlot(slot);
        if (otherSlot  != null) {
            super.setDirectOwnSlotValues(frame, otherSlot, values);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SimpleInstance createSimpleInstance(FrameID id,
                                               Collection directTypes,
                                               boolean loadDefaults) {
        Collection<Cls> typesToAdd = new ArrayList<Cls>();
        for (Object o : directTypes) {
            if (o instanceof Cls) {
                Cls otherCls = convertCls((Cls) o);
                if (otherCls != null && !directTypes.contains(otherCls)) {
                    typesToAdd.add(otherCls);
                }
            }
        }
        directTypes.addAll(typesToAdd);
        return super.createSimpleInstance(id, directTypes, loadDefaults);
    }
    
    @Override
    public void addDirectType(Instance instance, Cls type) {
        super.addDirectType(instance, type);
        Cls otherType = convertCls(type);
        if (otherType  != null && !super.getDirectTypes(instance).contains(otherType)) {
            super.addDirectType(instance, otherType);
        }
    }
    
    @Override
    public void removeDirectType(Instance instance, Cls directType) {
        super.removeDirectType(instance, directType);
        Cls otherType = convertCls(directType);
        if (otherType  != null && super.getDirectTypes(instance).contains(otherType)) {
            super.removeDirectType(instance, otherType);
        }
    }

}
