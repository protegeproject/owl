package edu.stanford.smi.protegex.owl.server.triplestore;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Localizable;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

public class ClientTripleStoreModel implements TripleStoreModel {

    public TripleStore createActiveImportedTripleStore(
                                                       NarrowFrameStore frameStore) {
        // TODO Auto-generated method stub
        return null;
    }

    public void deleteTripleStore(TripleStore tripleStore) {
        // TODO Auto-generated method stub

    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public TripleStore getActiveTripleStore() {
        // TODO Auto-generated method stub
        return null;
    }

    public TripleStore getHomeTripleStore(RDFResource resource) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection getPropertyValues(RDFResource resource,
                                        RDFProperty property) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection getSlotValues(Instance instance, Slot slot) {
        // TODO Auto-generated method stub
        return null;
    }

    public TripleStore getSystemTripleStore() {
        // TODO Auto-generated method stub
        return null;
    }

    public TripleStore getTopTripleStore() {
        // TODO Auto-generated method stub
        return null;
    }

    public TripleStore getTripleStore(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public TripleStore getTripleStoreByDefaultNamespace(String namespace) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<TripleStore> getTripleStores() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isActiveTriple(RDFResource subject, RDFProperty predicate,
                                  Object object) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEditableTriple(RDFResource subject, RDFProperty predicate,
                                    Object object) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEditableTripleStore(TripleStore ts) {
        // TODO Auto-generated method stub
        return false;
    }

    public Iterator<RDFResource> listSubjects(RDFProperty property) {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator<Triple> listTriplesWithSubject(RDFResource subject) {
        // TODO Auto-generated method stub
        return null;
    }

    public Iterator<TripleStore> listUserTripleStores() {
        // TODO Auto-generated method stub
        return null;
    }

    public void replaceJavaObject(RDFResource subject) {
        // TODO Auto-generated method stub

    }

    public void setActiveTripleStore(TripleStore tripleStore) {
        // TODO Auto-generated method stub

    }

    public void setHomeTripleStore(RDFResource resource, TripleStore tripleStore) {
        // TODO Auto-generated method stub

    }

    public void setTopTripleStore(TripleStore tripleStore) {
        // TODO Auto-generated method stub

    }

    public void setViewActiveOnly(boolean viewActiveOnly) {
        // TODO Auto-generated method stub

    }

    public void updateEditableResourceState() {
        // TODO Auto-generated method stub

    }
    
    public class Package implements Localizable, Serializable {
        private static final long serialVersionUID = 6422510439112012894L;

        public void localize(KnowledgeBase kb) {
            // TODO Auto-generated method stub
            
        }
        
    }

}
