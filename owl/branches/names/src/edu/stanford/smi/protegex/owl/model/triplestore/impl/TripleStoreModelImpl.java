package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.util.OWLFrameStoreUtils;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TripleStoreModelImpl implements TripleStoreModel {
    
    protected MergingNarrowFrameStore mnfs;
    
    private Slot nameSlot;
    
    private OWLModel owlModel;
    
    private List<TripleStore> allTripleStores = new ArrayList<TripleStore>();
    
    private Map<NarrowFrameStore, TripleStore> tripleStoreMap = new HashMap<NarrowFrameStore, TripleStore>();
    
    private TripleStore topTripleStore;


    public TripleStoreModelImpl(OWLModel owlModel) {
        this.nameSlot = owlModel.getSystemFrames().getNameSlot();
        this.mnfs = MergingNarrowFrameStore.get(owlModel);
        this.owlModel = owlModel;
        initTripleStores();
    }
    
    public TripleStore createActiveImportedTripleStore(NarrowFrameStore frameStore) {
        String parentName = getActiveTripleStore().getName();
        mnfs.addActiveChildFrameStore(frameStore, parentName);
        TripleStore tripleStore = new TripleStoreImpl(owlModel, frameStore, this);
        allTripleStores.add(tripleStore);
        tripleStoreMap.put(frameStore, tripleStore);
        updateRemoveFrameStores();
        return tripleStore;
    }


    public void deleteTripleStore(TripleStore tripleStore) {
        allTripleStores.remove(tripleStore);
        tripleStoreMap.remove(tripleStore.getNarrowFrameStore());
    }


    public static void ensureActiveTripleStore(RDFResource resource) {
        TripleStoreUtil.ensureActiveTripleStore(resource);
    }


    public TripleStore getTripleStoreByDefaultNamespace(String namespace) {
        for (Iterator<TripleStore> it = allTripleStores.iterator(); it.hasNext();) {
            TripleStore tripleStore = it.next();
            if (namespace.equals(tripleStore.getDefaultNamespace())) {
                return tripleStore;
            }
        }
        return null;
    }


    private void initTripleStores() {
        allTripleStores.clear();
        tripleStoreMap.clear();
        for (NarrowFrameStore nfs : mnfs.getAvailableFrameStores()) {
            TripleStore tripleStore = new TripleStoreImpl(owlModel, nfs, this);
            allTripleStores.add(tripleStore);
            tripleStoreMap.put(nfs, tripleStore);
        }
        updateRemoveFrameStores();
    }


    private void updateRemoveFrameStores() {
        Collection<NarrowFrameStore> allFrameStores = mnfs.getAvailableFrameStores();
        mnfs.setRemoveFrameStores(allFrameStores);
        owlModel.resetJenaModel();
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

        boolean enabled = owlModel.setGenerateEventsEnabled(false);
        try {
            TripleChangePostProcessor.postProcess(owlModel);
        }
        finally {
            owlModel.setGenerateEventsEnabled(enabled);
        }
        owlModel.flushCache();
    }
    
    public TripleStore getActiveTripleStore() {
        if (mnfs == null) {
            /**
             * Probably a client talking to a server.
             */
            return allTripleStores.get(allTripleStores.size() - 1);
        }
        NarrowFrameStore activeFrameStore = mnfs.getActiveFrameStore();
        return tripleStoreMap.get(activeFrameStore);
    }


    public TripleStore getHomeTripleStore(RDFResource resource) {
        for (Iterator<TripleStore> it = allTripleStores.iterator(); it.hasNext();) {
            TripleStore tripleStore = it.next();
            if (tripleStore.getNarrowFrameStore().getValuesCount(resource, nameSlot, null, false) > 0) {
                return tripleStore;
            }
        }
        return null;
    }


    public Collection getPropertyValues(RDFResource resource, RDFProperty property) {
        Collection values = mnfs.getValues(resource, property, null, false);
        return OWLFrameStoreUtils.convertValueListToRDFLiterals(owlModel, values);
    }


    public Collection getSlotValues(Instance instance, Slot slot) {
        return mnfs.getValues(instance, slot, null, false);
    }


    public TripleStore getTripleStore(String name) {
        if (name == null) {
            return getActiveTripleStore();
        }
        for (TripleStore tripleStore : allTripleStores) {
            if (name.equals(tripleStore.getName())) {
                return tripleStore;
            }
        }
        return null;
    }

    public TripleStore getSystemTripleStore() {
        return allTripleStores.get(0);
    }


    public List<TripleStore> getTripleStores() {
        return new ArrayList<TripleStore>(allTripleStores);
    }


    public TripleStore getTopTripleStore() {
        return topTripleStore;
    }
    
    public void setTopTripleStore(TripleStore tripleStore) {
        topTripleStore = tripleStore;
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
        if (tripleStore.equals(getSystemTripleStore())) {
            return false;
        }
        else if (tripleStore.equals(getTopTripleStore())) {
            return true;
        }
        else {
            try {
                URI uri = new URI(tripleStore.getName());
                return owlModel.getRepositoryManager().getRepository(uri).isWritable(uri);
                //return owlModel.getURIResolver().isEditableImport(uri);
            }
            catch (Exception ex) {
                return false;
            }
        }
    }


    public Iterator<Triple> listTriplesWithSubject(RDFResource subject) {
        List<Triple> result = new ArrayList<Triple>();
        Iterator<TripleStore> it = getTripleStores().iterator();
        while (it.hasNext()) {
            TripleStore ts = it.next();
            Iterator<Triple> triples = ts.listTriplesWithSubject(subject);
            while (triples.hasNext()) {
                Triple triple = triples.next();
                result.add(triple);
            }
        }
        return result.iterator();
    }
    
    
    public Iterator<RDFResource> listSubjects(RDFProperty property) {       
         Set<RDFResource> result = new HashSet<RDFResource>();
         
         Iterator<TripleStore> it = getTripleStores().iterator();
         while (it.hasNext()) {
             TripleStore ts = it.next();    
             Iterator subjects = ts.listSubjects(property);
             while (subjects.hasNext()) {
                 Frame frame = (Frame) subjects.next();
                 if (frame instanceof RDFResource) {
                    result.add((RDFResource) frame);
                 }
             }
         }
         return result.iterator();
    }


    public Iterator listUserTripleStores() {
        //TT: This has to be checked whether it is working right.
        if (mnfs == null && allTripleStores.size() == 1) {
            /**
             * Probably a client talking to a server and server is using database mode.
             */
            return CollectionUtilities.createCollection(allTripleStores.get(0)).iterator();
        }
        
        Iterator it = getTripleStores().iterator();
        it.next(); // drop the system triple store.
        return it;
    }


    public void replaceJavaObject(RDFResource subject) {
        mnfs.replaceFrame(subject);
    }


    public void setActiveTripleStore(TripleStore tripleStore) {
        if (mnfs == null && allTripleStores.size() == 1) {
            /**
             * Probably a client talking to a server and server is using database mode.
             * When we will support database inclusion, we should fix this implementation
             */
            return;
        }
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
        TripleStoreUtil.updateFrameInclusion(mnfs, nameSlot);
    }
    
    public void dispose() {
        for (TripleStore tripleStore : allTripleStores) {
            tripleStore.dispose();
        }
        
        if (mnfs != null) {
            mnfs.close();
        }
        
        allTripleStores.clear();
        tripleStoreMap.clear();
        allTripleStores = null;
        tripleStoreMap = null;
    }
    
    
    
}
