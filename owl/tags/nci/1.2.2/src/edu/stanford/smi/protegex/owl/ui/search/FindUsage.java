package edu.stanford.smi.protegex.owl.ui.search;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FindUsage {

    public static Collection getItems(RDFResource searchInstance) {    	    	
        OWLModel owlModel = searchInstance.getOWLModel();
        
        Set ignoreProperties = getIgnoreProperties(owlModel);
        Collection anons = searchInstance.getReferringAnonymousClasses();
        Collection items = new ArrayList();
        
        if (searchInstance instanceof RDFProperty && (((RDFProperty)searchInstance).isAnnotationProperty())) {        	
        	return getAnnotationPropertiesReferences((RDFProperty)searchInstance);
        }
        
        Slot superClsesSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUPERCLASSES);
        RDFProperty disjointWithProperty = owlModel.getRDFProperty(OWLNames.Slot.DISJOINT_WITH);
        Slot rangeSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.VALUE_TYPE);
        if (!owlModel.getOWLThingClass().equals(searchInstance)) {
            for (Iterator it = ((KnowledgeBase) owlModel).getReferences(searchInstance, 10000).iterator(); it.hasNext();) {
                Reference ref = (Reference) it.next();
                if (disjointWithProperty.equals(ref.getSlot())) {
                    items.add(new DefaultFindUsageTableItem(FindUsageTableItem.DISJOINT_CLASS,
                            (RDFResource) ref.getFrame(), searchInstance));
                }
                else if (rangeSlot.equals(ref.getSlot())) {
                    items.add(new DefaultFindUsageTableItem(FindUsageTableItem.RANGE,
                            (RDFResource) ref.getFrame(), searchInstance));
                }
            }
        }
        Set used = new HashSet();
        for (Iterator it = anons.iterator(); it.hasNext();) {
            OWLAnonymousClass cls = (OWLAnonymousClass) it.next();
            OWLAnonymousClass rootCls = cls.getExpressionRoot();
            if (searchInstance instanceof OWLNamedClass && ((OWLNamedClass) searchInstance).getEquivalentClasses().contains(rootCls)) {
                continue;
            }
            if (used.contains(rootCls)) {
                continue;
            }
            used.add(rootCls);
            Collection refs = ((KnowledgeBase) owlModel).getReferences(rootCls, 100000);
            for (Iterator rit = refs.iterator(); rit.hasNext();) {
                Reference reference = (Reference) rit.next();
                if (reference.getFrame() instanceof RDFResource) {
                    RDFResource host = (RDFResource) reference.getFrame();
                    if (superClsesSlot.equals(reference.getSlot())) {
                        if (host instanceof OWLNamedClass && ((OWLNamedClass) host).getPureSuperclasses().contains(rootCls)) {
                            items.add(new DefaultFindUsageTableItem(FindUsageTableItem.SUPERCLASS,
                                    host, rootCls));
                        }
                        else {
                            items.add(new DefaultFindUsageTableItem(FindUsageTableItem.EQUIVALENT_CLASS,
                                    host, rootCls));
                        }
                    }
                    else if (disjointWithProperty.equals(reference.getSlot())) {
                        items.add(new DefaultFindUsageTableItem(FindUsageTableItem.DISJOINT_CLASS,
                                host, rootCls));
                    }
                    else if (rangeSlot.equals(reference.getSlot())) {
                        items.add(new DefaultFindUsageTableItem(FindUsageTableItem.RANGE,
                                host, rootCls));
                    }
                }
            }
        }

        Collection refs = ((KnowledgeBase) owlModel).getReferences(searchInstance, 1000);
        for (Iterator it = refs.iterator(); it.hasNext();) {
            Reference ref = (Reference) it.next();
            if (ref.getSlot() instanceof RDFProperty && ref.getFrame() instanceof RDFResource) {
                RDFProperty property = (RDFProperty) ref.getSlot();
                if (!ignoreProperties.contains(property)) {
                    RDFResource host = (RDFResource) ref.getFrame();
                    if (!host.isAnonymous()) {
                        items.add(new DefaultFindUsageTableItem(FindUsageTableItem.VALUE,
                                host, property));
                    }
                }
            }
        }

        return items;
    }


    private static Collection getAnnotationPropertiesReferences(RDFProperty searchProperty) {
    	Collection items = new ArrayList();
    	
    	OWLModel owlModel = searchProperty.getOWLModel();
    	Collection<Frame> framesWithValue = owlModel.getHeadFrameStore().getFramesWithAnyDirectOwnSlotValue(searchProperty);
    	
    	for (Frame frame : framesWithValue) {
    		Collection slotValues = frame.getOwnSlotValues(searchProperty);
    		for (Iterator iter = slotValues.iterator(); iter.hasNext();) {
				Object value = (Object) iter.next();
				items.add(new DefaultFindUsageTableItem(FindUsageTableItem.PROPERTY_VALUE, (RDFResource) frame, value));
			}
		}
    	
    	return items;
	}


	private static Set getIgnoreProperties(OWLModel owlModel) {
        Set result = new HashSet();
        result.add(owlModel.getRDFTypeProperty());
        result.add(owlModel.getRDFSRangeProperty());
        result.add(owlModel.getRDFSDomainProperty());
        result.add(owlModel.getOWLDisjointWithProperty());
        result.add(owlModel.getRDFSSubClassOfProperty());
        result.add(owlModel.getOWLEquivalentClassProperty());
        return result;
    }
}
