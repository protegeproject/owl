package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

import java.net.URI;
import java.util.*;

/**
 * A base class for the two default TripleStoreModel implementations.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractTripleStoreModel implements TripleStoreModel {

    protected MergingNarrowFrameStore mnfs;

    private Slot nameSlot;

    private OWLModel owlModel;

    protected List<TripleStore> ts = new ArrayList<TripleStore>();


    public AbstractTripleStoreModel(OWLModel owlModel) {
        this.nameSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.NAME);
        this.mnfs = MergingNarrowFrameStore.get(owlModel);
        this.owlModel = owlModel;
    }


    public void endTripleStoreChanges() {
        owlModel.flushCache();
        final Collection resources = owlModel.getRDFResources();
        for (Iterator it = resources.iterator(); it.hasNext();) {
            RDFResource resource = (RDFResource) it.next();
            if (resource.isSystem()) {
                it.remove();
            }
        }
        OWLJavaFactoryUpdater.run(owlModel, resources);

        owlModel.setGenerateEventsEnabled(false);
        try {
            TripleChangePostProcessor.postProcess(owlModel);
        }
        finally {
            owlModel.setGenerateEventsEnabled(true);
        }
        owlModel.flushCache();
    }


    public TripleStore getActiveTripleStore() {
        if (mnfs == null && ts.size() == 1) {
            /**
             * Probably a client talking to a server.
             */
            return ts.get(0);
        }
        NarrowFrameStore activeFrameStore = mnfs.getActiveFrameStore();
        String name = activeFrameStore.getName();
        return getTripleStore(name);
    }


    public TripleStore getHomeTripleStore(RDFResource resource) {
        for (Iterator it = ts.iterator(); it.hasNext();) {
            TripleStore tripleStore = (TripleStore) it.next();
            if (tripleStore.getNarrowFrameStore().getValuesCount(resource, nameSlot, null, false) > 0) {
                return tripleStore;
            }
        }
        return null;
    }


    public Collection getPropertyValues(RDFResource resource, RDFProperty property) {
        Collection values = mnfs.getValues(resource, property, null, false);
        return owlModel.getOWLFrameStore().getConvertedValues(values);
    }


    public Collection getSlotValues(Instance instance, Slot slot) {
        return mnfs.getValues(instance, slot, null, false);
    }


    public TripleStore getTripleStore(String name) {
        if (name == null) {
            return (TripleStore) ts.get(1);
        }
        for (Iterator it = ts.iterator(); it.hasNext();) {
            TripleStore tripleStore = (TripleStore) it.next();
            if (name.equals(tripleStore.getName())) {
                return tripleStore;
            }
        }
        return null;
    }


    public TripleStore getTripleStore(int index) {
        return (TripleStore) getTripleStores().get(index);
    }


    public List getTripleStores() {
        return new ArrayList(ts);
    }


    public TripleStore getTopTripleStore() {
        if (mnfs == null && ts.size() == 1) {
            return ts.get(0);
        }
        else {
            return ts.get(1);
        }
    }


    public boolean isActiveTriple(RDFResource subject, RDFProperty predicate, Object object) {
        return getActiveTripleStore().contains(subject, predicate, object);
    }


    public boolean isEditableTriple(RDFResource subject, RDFProperty predicate, Object object) {
        object = DefaultRDFSLiteral.getPlainValueIfPossible(object);
        Iterator it = listUserTripleStores();
        while (it.hasNext()) {
            TripleStore ts = (TripleStore) it.next();
            if (ts.contains(subject, predicate, object)) {
                return isEditableTripleStore(ts);
            }
        }
        return false;
    }


    public boolean isEditableTripleStore(TripleStore tripleStore) {
        int index = ts.indexOf(tripleStore);
        if (index == 0) {
            return false;
        }
        if (index == 1) {
            return true;
        }
        try {
            URI uri = new URI(tripleStore.getName());
            return owlModel.getRepositoryManager().getRepository(uri).isWritable(uri);
            //return owlModel.getURIResolver().isEditableImport(uri);
        }
        catch (Exception ex) {
        }
        return false;
    }


    public Iterator listTriplesWithSubject(RDFResource subject) {
        List result = new ArrayList();
        Iterator it = getTripleStores().iterator();
        while (it.hasNext()) {
            TripleStore ts = (TripleStore) it.next();
            Iterator triples = ts.listTriplesWithSubject(subject);
            while (triples.hasNext()) {
                Triple triple = (Triple) triples.next();
                result.add(triple);
            }
        }
        return result.iterator();
    }


    public Iterator listUserTripleStores() {
        Iterator it = getTripleStores().iterator();
        it.next();
        return it;
    }


    public void replaceJavaObject(RDFResource subject) {
        mnfs.replaceFrame(subject);
    }


    public void setActiveTripleStore(TripleStore tripleStore) {
        if (mnfs.getActiveFrameStore() != tripleStore.getNarrowFrameStore()) {
            mnfs.setActiveFrameStore(tripleStore.getNarrowFrameStore());
        }
    }


    public void setHomeTripleStore(RDFResource resource, TripleStore tripleStore) {
        TripleStore home = getHomeTripleStore(resource);
        if (home != tripleStore) {
            List values = mnfs.getValues(resource, nameSlot, null, false);
            String name = (String) values.get(0);
            home.getNarrowFrameStore().removeValue(resource, nameSlot, null, false, name);
            tripleStore.getNarrowFrameStore().addValues(resource, nameSlot, null, false, Collections.singleton(name));
        }
    }


    public void updateEditableResourceState() {
        Slot nameSlot = ((KnowledgeBase) owlModel).getSlot(Model.Slot.NAME);
        TripleStoreUtil.updateFrameInclusion(mnfs, nameSlot);
    }
}
