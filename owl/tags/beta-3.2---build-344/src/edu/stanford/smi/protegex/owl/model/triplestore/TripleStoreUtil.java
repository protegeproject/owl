package edu.stanford.smi.protegex.owl.model.triplestore;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TripleStoreUtil {


    public static void addToTripleStore(OWLModel owlModel, TripleStore tripleStore, RDFResource subject, RDFProperty predicate, Object object) {
        TripleStore oldTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
        if (oldTripleStore != tripleStore) {
            owlModel.getTripleStoreModel().setActiveTripleStore(tripleStore);
            subject.addPropertyValue(predicate, object);
            owlModel.getTripleStoreModel().setActiveTripleStore(oldTripleStore);
        }
        else {
            subject.addPropertyValue(predicate, object);
        }
    }


    public static TripleStore ensureActiveTripleStore(RDFResource resource) {
        OWLModel owlModel = resource.getOWLModel();
        final TripleStoreModel tsm = owlModel.getTripleStoreModel();
        if (tsm != null) {
            TripleStore tripleStore = tsm.getHomeTripleStore(resource);
            tsm.setActiveTripleStore(tripleStore);
            return tripleStore;
        }
        else {
            return null;
        }
    }


    public static RDFResource getFirstOntology(OWLModel owlModel, TripleStore tripleStore) {
        RDFSNamedClass owlOntologyClass = owlModel.getOWLOntologyClass();
        Iterator ontologies = tripleStore.listSubjects(owlModel.getRDFTypeProperty(), owlOntologyClass);
        if (ontologies.hasNext()) {
            Frame next = (Frame) ontologies.next();
            if (next instanceof RDFResource) {
                return (RDFResource) next;
            }
            else {
                return new DefaultOWLOntology(owlModel, next.getFrameID());
            }
        }
        else {
            return null;
        }
    }


    public static TripleStore getTripleStoreOf(RDFResource subject, Slot slot, Object object) {

        // Temporary hard-coding of database limitation
        if (subject.getOWLModel() instanceof OWLDatabaseModel) {
            return subject.getOWLModel().getTripleStoreModel().getTopTripleStore();
        }

        if (object instanceof RDFSLiteral) {
            object = ((DefaultRDFSLiteral) object).getRawValue();
        }
        OWLModel owlModel = subject.getOWLModel();
        Iterator it = owlModel.getTripleStoreModel().getTripleStores().iterator();
        while (it.hasNext()) {
            TripleStore ts = (TripleStore) it.next();
            if (ts.getNarrowFrameStore().getValues(subject, slot, null, false).contains(object)) {
                return ts;
            }
        }
        return null;
    }


    /**
     * Gets all Triples that are contained "within" a resource definition block.
     * This includes all property values, recursively into nested anonymous classes and RDFLists.
     * The traversal stops at named resources.
     *
     * @param subject the class to start with
     * @return an Iterator of Triple objects
     */
    public static Iterator listDependingTriples(RDFResource subject) {
        List result = new ArrayList();
        getDependingTriples(subject, result);
        return result.iterator();
    }


    private static void getDependingTriples(RDFResource subject, List result) {
        OWLModel owlModel = subject.getOWLModel();
        Iterator it = owlModel.getTripleStoreModel().listTriplesWithSubject(subject);
        while (it.hasNext()) {
            Triple triple = (Triple) it.next();
            result.add(triple);
            if (triple.getObject() instanceof RDFResource) {
                RDFResource object = (RDFResource) triple.getObject();
                if (object.isAnonymous()) {
                    getDependingTriples(object, result);
                }
                else {
                    RDFResource type = object.getRDFType();
                    if (type != null) {
                        Triple typeTriple = new DefaultTriple(object, owlModel.getRDFTypeProperty(), type);
                        result.add(typeTriple);
                    }
                }
            }
        }
    }


    public static void moveResources(Collection resources, TripleStore sourceTS, TripleStore targetTS) {
        if (!resources.isEmpty()) {
            OWLModel owlModel = ((RDFResource) resources.iterator().next()).getOWLModel();
            NarrowFrameStore sourceFS = sourceTS.getNarrowFrameStore();
            NarrowFrameStore targetFS = targetTS.getNarrowFrameStore();
            Collection slots = ((KnowledgeBase) owlModel).getSlots();
            for (Iterator it = resources.iterator(); it.hasNext();) {
                RDFResource resource = (RDFResource) it.next();
                moveResource(resource, slots, sourceFS, targetFS);
            }
            owlModel.flushCache();
        }
    }


    private static void moveResource(Instance instance, Collection slots, NarrowFrameStore sourceFS, NarrowFrameStore targetFS) {
        for (Iterator it = slots.iterator(); it.hasNext();) {
            Slot slot = (Slot) it.next();
            Collection values = sourceFS.getValues(instance, slot, null, false);
            for (Iterator vs = values.iterator(); vs.hasNext();) {
                Object value = vs.next();
                sourceFS.removeValue(instance, slot, null, false, value);
            }
            targetFS.addValues(instance, slot, null, false, values);
        }
    }


    public static void replaceTriple(final Triple oldTriple, final Triple newTriple) {
        OWLModel owlModel = oldTriple.getSubject().getOWLModel();
        TripleStore ts = getTripleStoreOf(oldTriple.getSubject(), oldTriple.getPredicate(), oldTriple.getObject());
        runInTripleStore(owlModel, ts, new Runnable() {
            public void run() {
                oldTriple.getSubject().removePropertyValue(oldTriple.getPredicate(), oldTriple.getObject());
                newTriple.getSubject().addPropertyValue(newTriple.getPredicate(), newTriple.getObject());
            }
        });
    }


    public static void runInHomeTripleStoreOf(RDFResource resource, Runnable runnable) {
        OWLModel owlModel = resource.getOWLModel();
        TripleStore homeTS = owlModel.getTripleStoreModel().getHomeTripleStore(resource);
        runInTripleStore(owlModel, homeTS, runnable);
    }


    public static void runInTripleStore(OWLModel owlModel, TripleStore ts, Runnable runnable) {
        TripleStoreModel tsm = owlModel.getTripleStoreModel();
        TripleStore oldActiveTS = tsm.getActiveTripleStore();
        if (oldActiveTS != ts && ts != null) {
            tsm.setActiveTripleStore(ts);
            runnable.run();
            tsm.setActiveTripleStore(oldActiveTS);
        }
        else {
            runnable.run();
        }
    }


    public static void sortSlotValues(NarrowFrameStore narrowFrameStore, Instance instance, Slot slot, Comparator comparator) {
        final List oldValues = narrowFrameStore.getValues(instance, slot, null, false);
        if (!oldValues.isEmpty()) {
            final List values = new ArrayList(oldValues);
            Collections.sort(values, comparator);
            narrowFrameStore.setValues(instance, slot, null, false, values);
        }
    }


    public static void sortSubclasses(OWLModel owlModel) {
        final Slot subclassesSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_SUBCLASSES);
        Collection namedClasses = owlModel.getUserDefinedRDFSNamedClasses();
        for (Iterator it = namedClasses.iterator(); it.hasNext();) {
            RDFSNamedClass namedClass = (RDFSNamedClass) it.next();
            TripleStore homeTripleStore = owlModel.getTripleStoreModel().getHomeTripleStore(namedClass);
            NarrowFrameStore nfs = homeTripleStore.getNarrowFrameStore();
            sortSlotValues(nfs, namedClass, subclassesSlot, new FrameComparator());
        }

        OWLNamedClass owlThingClass = owlModel.getOWLThingClass();
        Iterator tripleStores = owlModel.getTripleStoreModel().listUserTripleStores();
        while (tripleStores.hasNext()) {
            TripleStore tripleStore = (TripleStore) tripleStores.next();
            NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
            sortSlotValues(nfs, owlThingClass, subclassesSlot, new FrameComparator());
        }

        owlModel.flushCache();
    }


    public static void switchTripleStore(OWLModel owlModel, TripleStore tripleStore) {
        final TripleStoreModel tsm = owlModel.getTripleStoreModel();
        if (tsm.getActiveTripleStore() != tripleStore) {
            WaitCursor waitCursor = new WaitCursor(ProtegeUI.getTopLevelContainer(owlModel.getProject()));
            tsm.setActiveTripleStore(tripleStore);
            tsm.updateEditableResourceState();
            ProtegeUI.reloadUI(owlModel.getProject());
            waitCursor.hide();
        }
    }


    /**
     * Updates the "isIncluded()" flag for all Frames, so that only those Frames that have their
     * :NAME in the active FrameStore are not included.
     *
     * @param mnfs the MergingNarrowFrameStore
     */
    public static void updateFrameInclusion(MergingNarrowFrameStore mnfs, Slot nameSlot) {
        NarrowFrameStore activeFrameStore = mnfs.getActiveFrameStore();
        updateFrameInclusion(activeFrameStore, nameSlot, false);
        NarrowFrameStore systemFrameStore = mnfs.getSystemFrameStore();
        Iterator frameStores = mnfs.getAllFrameStores().iterator();
        while (frameStores.hasNext()) {
            NarrowFrameStore frameStore = (NarrowFrameStore) frameStores.next();
            if (frameStore != systemFrameStore && frameStore != activeFrameStore) {
                updateFrameInclusion(frameStore, nameSlot, true);
            }
        }
    }


    private static void updateFrameInclusion(NarrowFrameStore frameStore, Slot nameSlot, boolean included) {
        for (Iterator it = frameStore.getFrames().iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (!frame.isSystem()) {
                if (frameStore.getValuesCount(frame, nameSlot, null, false) > 0) {
                    frame.setIncluded(included);
                    frame.setEditable(!included);
                    // System.out.println("- " + frame.getName() + ": " + included);
                }
            }
        }
    }
}
