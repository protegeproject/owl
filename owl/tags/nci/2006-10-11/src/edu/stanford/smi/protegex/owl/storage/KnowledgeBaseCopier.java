package edu.stanford.smi.protegex.owl.storage;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protegex.owl.model.OWLNames;

import java.util.*;

/**
 * A utility class that copies all frames from a source KnowledgeBase into a target KnowledgeBase.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class KnowledgeBaseCopier {

    private KnowledgeBase source;

    private KnowledgeBase target;

    private Set doneSlots = new HashSet();

    /**
     * A Hashtable from old frames to new Frames.  This is used to find frames that have been
     * renamed on the fly while being created in the target knowledge base.  When the direct
     * name match fails then this Hashtable is consulted.
     */
    private Hashtable frameMap = new Hashtable();

    private Map todoSlots = new HashMap();


    public KnowledgeBaseCopier(KnowledgeBase source, KnowledgeBase target) {

        this.source = source;
        this.target = target;

        doneSlots.add(Model.Slot.DIRECT_DOMAIN);
        doneSlots.add(Model.Slot.DIRECT_SUBCLASSES);
        doneSlots.add(Model.Slot.DIRECT_SUBSLOTS);
        doneSlots.add(Model.Slot.DIRECT_SUPERCLASSES);
        doneSlots.add(Model.Slot.DIRECT_SUPERSLOTS);
        doneSlots.add(Model.Slot.DIRECT_INSTANCES);
        doneSlots.add(Model.Slot.DIRECT_TEMPLATE_SLOTS);
        doneSlots.add(Model.Slot.DIRECT_TYPES);
        doneSlots.add(Model.Slot.MAXIMUM_CARDINALITY);
        doneSlots.add(Model.Slot.MINIMUM_CARDINALITY);
        doneSlots.add(Model.Slot.NAME);
        doneSlots.add(Model.Slot.VALUE_TYPE);
    }


    public void run() {
        log("Creating Classes...");
        createClses();
        log("Creating Facets...");
        createFacets();
        createSlots();
        createInstances();
        createFacetOverrides();
        setOwnSlotValues();
        for (Iterator it = todoSlots.keySet().iterator(); it.hasNext();) {
            Slot oldSlot = (Slot) it.next();
            Slot newSlot = (Slot) todoSlots.get(oldSlot);
            setValueType(oldSlot, newSlot);
        }
    }


    protected void addExtraDirectTypes(Instance oldInstance, Instance newInstance) {
        Collection directTypes = oldInstance.getDirectTypes();
        Iterator it = directTypes.iterator();
        it.next(); // Ignore first type
        while (it.hasNext()) {
            Cls oldType = (Cls) it.next();
            Cls newType = getNewCls(oldType);
            if (!(newInstance.hasDirectType(newType))) {  // Ignore duplicates due to bugs
                log("+ Adding direct type " + newType.getBrowserText() +
                        " to " + newInstance.getBrowserText());
                newInstance.addDirectType(newType);
            }
        }
    }


    protected Collection cloneValues(Collection oldValues) {
        Collection newValues = new ArrayList();
        for (Iterator vit = oldValues.iterator(); vit.hasNext();) {
            Object oldValue = vit.next();
            if (oldValue instanceof Instance) {
                Instance newInstance = getNewInstance((Instance) oldValue);
                if (newInstance != null) {
                    newValues.add(newInstance);
                }
            }
            else {
                newValues.add(oldValue);
            }
        }
        return newValues;
    }


    protected Cls createCls(String clsName, Cls metaCls) {
        log("+ Creating Class " + clsName + " of type " + metaCls);
        return target.createCls(clsName, target.getRootClses(), metaCls);
    }


    protected void createClses() {
        for (Iterator it = source.getClses().iterator(); it.hasNext();) {
            Cls oldCls = (Cls) it.next();
            getNewCls(oldCls);
        }
    }


    private void createFacetOverrides() {
        for (Iterator it = source.getClses().iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls.isEditable() || (cls.isIncluded() && !cls.isSystem())) {
                createFacetOverrides(cls);
            }
        }
    }


    protected void createFacetOverrides(Cls oldCls) {
        for (Iterator it = oldCls.getTemplateSlots().iterator(); it.hasNext();) {
            Slot slot = (Slot) it.next();
            if (oldCls.hasDirectlyOverriddenTemplateSlot(slot)) {
                createFacetOverrides(oldCls, slot);
            }
        }
    }


    private void createFacetOverride(Cls oldCls, Slot oldSlot, Facet oldFacet) {
        Cls newCls = getNewCls(oldCls);
        Slot newSlot = getNewSlot(oldSlot);

        log("* Making facet override of " + oldSlot.getBrowserText() + "."
                + oldFacet.getBrowserText() + " at Cls " + oldCls.getBrowserText() +
                ", new: " + newCls.getBrowserText());

        if (oldFacet.getName().equals(Model.Facet.VALUE_TYPE)) {
            ValueType valueType = oldCls.getTemplateSlotValueType(oldSlot);
            newCls.setTemplateSlotValueType(newSlot, valueType);
            if (valueType == ValueType.INSTANCE) {
                Collection oldClses = oldCls.getTemplateSlotAllowedClses(oldSlot);
                Collection newClses = cloneValues(oldClses);
                newCls.setTemplateSlotAllowedClses(newSlot, newClses);
            }
            else if (valueType == ValueType.CLS) {
                Collection oldClses = oldCls.getTemplateSlotAllowedParents(oldSlot);
                Collection newClses = cloneValues(oldClses);
                newCls.setTemplateSlotAllowedParents(newSlot, newClses);
            }
            else if (valueType == ValueType.SYMBOL) {
                Collection oldValues = oldCls.getTemplateSlotAllowedValues(oldSlot);
                Collection newValues = cloneValues(oldValues);
                newCls.setTemplateSlotAllowedValues(newSlot, newValues);
            }
        }
        else {
            Collection oldValues = oldCls.getTemplateFacetValues(oldSlot, oldFacet);
            Collection newValues = cloneValues(oldValues);
            Facet newFacet = getNewFacet(oldFacet);
            newCls.setTemplateFacetValues(newSlot, newFacet, newValues);
        }
    }


    private void createFacetOverrides(Cls oldCls, Slot oldSlot) {
        for (Iterator it = source.getFacets().iterator(); it.hasNext();) {
            Facet oldFacet = (Facet) it.next();
            if (oldCls.hasDirectlyOverriddenTemplateFacet(oldSlot, oldFacet)) {
                createFacetOverride(oldCls, oldSlot, oldFacet);
            }
        }
    }


    private void createFacets() {
        for (Iterator it = source.getFacets().iterator(); it.hasNext();) {
            Facet oldFacet = (Facet) it.next();
            getNewFacet(oldFacet);
        }
    }


    protected Instance createInstance(final String name, Cls newType) {
        return target.createInstance(name, newType);
    }


    private void createInstances() {
        for (Iterator it = source.getInstances().iterator(); it.hasNext();) {
            Instance oldInstance = (Instance) it.next();
            if (oldInstance.getDirectType() != null &&
                    !oldInstance.getDirectType().isSystem()) { //!(oldInstance instanceof Slot) && !(oldInstance instanceof Cls)) {
                getNewInstance(oldInstance);
            }
        }
    }


    protected Slot createSlot(String slotName, ValueType valueType) {
        return target.createSlot(slotName);
    }


    private void createSlots() {
        for (Iterator it = source.getSlots().iterator(); it.hasNext();) {
            Slot oldSlot = (Slot) it.next();
            getNewSlot(oldSlot);
        }
    }


    protected Cls getNewCls(Cls oldCls) {
        Cls newCls = (Cls) frameMap.get(oldCls);
        if (newCls == null) {
            newCls = target.getCls(oldCls.getName());
        }
        else {
            newCls = target.getCls(newCls.getName()); // Re-get it if Java type has changed
        }
        if (newCls == null) {
            Cls newType = getNewCls(oldCls.getDirectType());
            if (newType == null) {
                System.err.println("ERROR: No type for " + oldCls);
                newType = oldCls.getKnowledgeBase().getCls(OWLNames.Cls.NAMED_CLASS);
                oldCls.setDirectType(newType);
            }
            newCls = createCls(oldCls.getName(), newType);
            registerFrame(oldCls, newCls);
            addExtraDirectTypes(oldCls, newCls);
            for (Iterator it = oldCls.getDirectSuperclasses().iterator(); it.hasNext();) {
                Cls oldSuperCls = (Cls) it.next();
                Cls newSuperCls = getNewCls(oldSuperCls);
                if (!newCls.hasDirectSuperclass(newSuperCls)) {
                    newCls.addDirectSuperclass(newSuperCls);
                    log("  + Adding " + newSuperCls.getName() + " to superclasses of " + newCls.getName());
                }
            }
            if (!oldCls.hasDirectSuperclass(source.getRootCls())) {
                newCls.removeDirectSuperclass(target.getRootCls());
            }
            for (Iterator it = oldCls.getDirectTemplateSlots().iterator(); it.hasNext();) {
                Slot oldSlot = (Slot) it.next();
                Slot newSlot = getNewSlot(oldSlot);
                log("+ Added template slot " + newSlot.getBrowserText() + " to " + newCls.getBrowserText());
                newCls.addDirectTemplateSlot(newSlot);
            }
            setInitialOwnSlotValues(oldCls);
        }
        return newCls;
    }


    private Facet getNewFacet(Facet oldFacet) {
        Facet newFacet = (Facet) frameMap.get(oldFacet);
        if (newFacet == null) {
            newFacet = target.getFacet(oldFacet.getName());
        }
        if (newFacet == null) {
            newFacet = target.createFacet(oldFacet.getName());
            registerFrame(oldFacet, newFacet);
            addExtraDirectTypes(oldFacet, newFacet);
            setInitialOwnSlotValues(oldFacet);
        }
        return newFacet;
    }


    protected Instance getNewInstance(Instance oldInstance) {
        if (oldInstance instanceof Cls) {
            return getNewCls((Cls) oldInstance);
        }
        else if (oldInstance instanceof Slot) {
            return getNewSlot((Slot) oldInstance);
        }
        else if (oldInstance instanceof Facet) {
            return getNewFacet((Facet) oldInstance);
        }
        else {
            Instance newInstance = (Instance) frameMap.get(oldInstance);
            final String name = oldInstance.getName();
            if (newInstance == null) {
                newInstance = target.getInstance(name);
            }
            if (newInstance == null) {
                Cls oldType = oldInstance.getDirectType();
                if (oldType == null) {
                    System.err.println("Warning: Instance " + oldInstance.getName() + " has no direct type [Ignored]");
                    return null;
                }
                Cls newType = getNewCls(oldType);
                newInstance = createInstance(name, newType);
                registerFrame(oldInstance, newInstance);
                addExtraDirectTypes(oldInstance, newInstance);
                setInitialOwnSlotValues(oldInstance);
            }
            return newInstance;
        }
    }


    protected Slot getNewSlot(Slot oldSlot) {
        Slot newSlot = (Slot) frameMap.get(oldSlot);
        if (newSlot == null) {
            String slotName = oldSlot.getName();
            newSlot = target.getSlot(slotName);
        }
        if (newSlot == null) {
            newSlot = createSlot(oldSlot.getName(), oldSlot.getValueType());
            registerFrame(oldSlot, newSlot);
            addExtraDirectTypes(oldSlot, newSlot);
            // setValueType(oldSlot, newSlot);
            todoSlots.put(oldSlot, newSlot);
            setDirectType(oldSlot, newSlot);
            newSlot.setMinimumCardinality(oldSlot.getMinimumCardinality());
            newSlot.setMaximumCardinality(oldSlot.getMaximumCardinality());
            for (Iterator it = oldSlot.getDirectSuperslots().iterator(); it.hasNext();) {
                Slot oldSuperSlot = (Slot) it.next();
                Slot newSuperSlot = getNewSlot(oldSuperSlot);
                if (!newSlot.getDirectSuperslots().contains(newSuperSlot)) {
                    newSlot.addDirectSuperslot(newSuperSlot);
                }
            }
            setInitialOwnSlotValues(oldSlot);
        }
        return newSlot;
    }


    protected void log(String str) {
        // System.out.println("[KnowledgeBaseCopier] " + str);
    }


    private void registerFrame(Frame oldFrame, Frame newFrame) {
        String oldName = oldFrame.getName();
        String newName = newFrame.getName();
        if (!oldName.equals(newName)) {
            frameMap.put(oldFrame, newFrame);
        }
    }


    protected void setDirectType(Instance oldFrame, Instance newFrame) {
        Cls oldType = oldFrame.getDirectType();
        Cls newType = getNewCls(oldType);
        if (!newType.equals(newFrame.getDirectType())) {
            newFrame.setDirectType(newType);
        }
    }


    /**
     * Can be overloaded to force the assignment of selected own slot values
     * immediately after the instance has been created.
     * The default implementation is empty.
     *
     * @param instance the instance to initialize the own slot values
     */
    protected void setInitialOwnSlotValues(Instance instance) {
        // Do nothing - can be overloaded
    }


    private void setOwnSlotValues() {
        for (Iterator it = source.getInstances().iterator(); it.hasNext();) {
            Instance oldInstance = (Instance) it.next();
            if (!oldInstance.isSystem()) { // && !oldInstance.getDirectType().isSystem()) {
                if (oldInstance.isEditable() || oldInstance.isIncluded()) {
                    setOwnSlotValues(oldInstance);
                }
            }
        }
    }


    private void setOwnSlotValues(Instance oldInstance) {
        Frame newFrame = getNewInstance(oldInstance);
        if (newFrame != null) {
            log("+ Setting own slot values of " + oldInstance.getBrowserText() +
                    " (new: " + newFrame.getBrowserText() + ")");
            for (Iterator slots = oldInstance.getOwnSlots().iterator(); slots.hasNext();) {
                Slot oldSlot = (Slot) slots.next();
                if (!doneSlots.contains(oldSlot.getName())) {
                    setOwnSlotValues(newFrame, oldInstance, oldSlot);
                }
            }
        }
    }


    protected void setOwnSlotValues(Frame newFrame, Instance oldInstance, Slot oldSlot) {
        Slot newSlot = getNewSlot(oldSlot);
        Collection values = oldInstance.getOwnSlotValues(oldSlot);
        if (values.size() > 0) {
            log(" - " + oldSlot + " (" + values.size() + " values)");
            Collection clones = cloneValues(values);
            if (!clones.equals(newFrame.getOwnSlotValues(newSlot))) {
                newFrame.setOwnSlotValues(newSlot, clones);
            }
        }
    }


    protected void setValueType(Slot oldSlot, Slot newSlot) {
        newSlot.setValueType(oldSlot.getValueType());
        if (oldSlot.getValueType() == ValueType.INSTANCE) {
            Collection newAllowedClses = cloneValues(oldSlot.getAllowedClses());
            newSlot.setAllowedClses(newAllowedClses);
        }
        else if (oldSlot.getValueType() == ValueType.CLS) {
            Collection newAllowedParents = cloneValues(oldSlot.getAllowedParents());
            newSlot.setAllowedParents(newAllowedParents);
        }
        else if (oldSlot.getValueType() == ValueType.SYMBOL) {
            Collection newAllowedValues = oldSlot.getAllowedValues();
            newSlot.setAllowedValues(newAllowedValues);
        }
    }
}
