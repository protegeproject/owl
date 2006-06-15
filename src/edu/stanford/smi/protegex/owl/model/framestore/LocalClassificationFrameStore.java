package edu.stanford.smi.protegex.owl.model.framestore;

import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.EventDispatchFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreAdapter;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;

import java.util.*;

/**
 * A FrameStore that intercepts any access to those slots that store the
 * classification results and redirects them to values stored in local maps.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LocalClassificationFrameStore extends FrameStoreAdapter {

    private OWLModel owlModel;

    /**
     * Slot -> instancesMap
     */
    private Map<Slot, Map<Frame, List>> slotsMap = new HashMap<Slot, Map<Frame, List>>();


    public LocalClassificationFrameStore(OWLModel owlModel) {
        this.owlModel = owlModel;
        slotsMap.put(owlModel.getRDFProperty(ProtegeNames.Slot.CLASSIFICATION_STATUS), new HashMap<Frame, List>());
        slotsMap.put(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_TYPE), new HashMap<Frame, List>());
        slotsMap.put(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_SUBCLASSES), new HashMap<Frame, List>());
        slotsMap.put(owlModel.getRDFProperty(ProtegeNames.Slot.INFERRED_SUPERCLASSES), new HashMap<Frame, List>());
    }


    public void deleteCls(Cls cls) {
        for (Iterator<Slot> it = slotsMap.keySet().iterator(); it.hasNext();) {
            Slot slot = it.next();
            Map<Frame, List> instancesMap = slotsMap.get(slot);
            instancesMap.remove(cls);
            for (Iterator<Frame> jt = new ArrayList<Frame>(instancesMap.keySet()).iterator(); 
                 jt.hasNext();) {
                Frame frame = jt.next();
                Collection values = instancesMap.get(frame);
                if (values.contains(cls)) {
                    List newValues = new ArrayList(values);
                    newValues.remove(cls);
                    frame.setDirectOwnSlotValues(slot, newValues);
                }
            }
        }
        super.deleteCls(cls);
    }


    private void dispatchEvent(Frame frame, Slot slot) {
        EventDispatchFrameStore e = getEventDispatchFrameStore();
        Collection ls = new ArrayList(e.getListeners(FrameListener.class, frame));
        for (Iterator it = ls.iterator(); it.hasNext();) {
            FrameListener listener = (FrameListener) it.next();
            listener.ownSlotValueChanged(new FrameEvent(frame, FrameEvent.OWN_SLOT_VALUE_CHANGED, slot));
        }
    }


    public List getDirectOwnSlotValues(Frame frame, Slot slot) {
        final Map<Frame,List> instancesMap = slotsMap.get(slot);
        if (instancesMap != null) {
            final List values = instancesMap.get(frame);
            if (values == null) {
                return Collections.EMPTY_LIST;
            }
            else {
                return values;
            }
        }
        else {
            return super.getDirectOwnSlotValues(frame, slot);
        }
    }


    public int getDirectOwnSlotValuesCount(Frame frame, Slot slot) {
        final Map<Frame, List> instancesMap = slotsMap.get(slot);
        if (instancesMap != null) {
            return getDirectOwnSlotValues(frame, slot).size();
        }
        else {
            return super.getDirectOwnSlotValuesCount(frame, slot);
        }
    }


    private EventDispatchFrameStore getEventDispatchFrameStore() {
        for (Iterator it = ((KnowledgeBase) owlModel).getFrameStores().iterator(); it.hasNext();) {
            FrameStore frameStore = (FrameStore) it.next();
            if (frameStore instanceof EventDispatchFrameStore) {
                return (EventDispatchFrameStore) frameStore;
            }
        }
        return null;
    }


    public Set getFramesWithDirectOwnSlotValue(Slot slot, Object value) {
        final Map<Frame, List> instancesMap = slotsMap.get(slot);
        if (instancesMap != null) {
            final Set<Frame> result = new HashSet<Frame>();
            for (Iterator<Frame> it = instancesMap.keySet().iterator(); it.hasNext();) {
                final Frame key = it.next();
                final Collection values = instancesMap.get(key);
                if (values != null && values.contains(value)) {
                    result.add(key);
                }
            }
            return result;
        }
        else {
            return super.getFramesWithDirectOwnSlotValue(slot, value);
        }
    }


    public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {
        final Map<Frame, List> instancesMap = slotsMap.get(slot);
        if (instancesMap != null) {
            if (values.isEmpty()) {
                instancesMap.remove(frame);
            }
            else {
                instancesMap.put(frame, new ArrayList(values));
            }
            // dispatchEvent(frame, slot);
        }
        else {
            super.setDirectOwnSlotValues(frame, slot, values);
        }
    }
}
