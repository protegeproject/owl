package edu.stanford.smi.protegex.owl.server.triplestore;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Localizable;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.server.narrowframestore.RemoteClientInvocationHandler;
import edu.stanford.smi.protege.server.narrowframestore.RemoteServerNarrowFrameStore;
import edu.stanford.smi.protege.server.narrowframestore.ServerNarrowFrameStore;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.TripleStoreImpl;

public class ClientTripleStoreModel implements TripleStoreModel {
    private  OWLModel owlModel;
    private List<TripleStore> tripleStores = new ArrayList<TripleStore>();
    
    public ClientTripleStoreModel(OWLModel owlModel) {
        this.owlModel = owlModel;
        initialize();
    }
    
    private void initialize() {
        initialize(getRemoteSession());
    }
    
    private RemoteSession getRemoteSession() {
        RemoteClientFrameStore frameStore = owlModel.getFrameStoreManager().getFrameStoreFromClass(RemoteClientFrameStore.class);
        return frameStore.getSession();
    }
    
    private void initialize(RemoteSession  session) {
        Package p = (Package) new GetPackage(owlModel).execute();
        for (int i = 0; i < p.getFrameStores().size(); i++) {
            RemoteServerNarrowFrameStore remoteNarrowFrameStore = p.getFrameStores().get(i);
            NamespaceManager namespaceManager = p.getNamespaceManagers().get(i);
            
            NarrowFrameStore narrowFrameStore = new RemoteClientInvocationHandler(owlModel, remoteNarrowFrameStore, session).getNarrowFrameStore();
            TripleStore tripleStore = new TripleStoreImpl(owlModel, narrowFrameStore ,this, namespaceManager);
            tripleStores.add(tripleStore);
        }
    }
    

    public TripleStore createActiveImportedTripleStore(
                                                       NarrowFrameStore frameStore) {
        throw new UnsupportedOperationException();
    }

    public void deleteTripleStore(TripleStore tripleStore) {
        throw new UnsupportedOperationException();
    }

    public void dispose() {
        ;
    }

    public TripleStore getActiveTripleStore() {
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
    
    public class GetPackage extends ProtegeJob {
        private static final long serialVersionUID = 6836555738304528191L;

        public GetPackage(OWLModel owlModel) {
            super(owlModel);
        }
        
        @Override
        public OWLModel getKnowledgeBase() {
            return (OWLModel) super.getKnowledgeBase();
        }
        
        @Override
        public Object run() throws ProtegeException {
            try {
                List<RemoteServerNarrowFrameStore> frameStores = new ArrayList<RemoteServerNarrowFrameStore>();
                List<NamespaceManager> namespaceManagers = new ArrayList<NamespaceManager>();
                String activeTripleStore;
                
                TripleStoreModel tripleStoreModel = getKnowledgeBase().getTripleStoreModel();
                activeTripleStore = tripleStoreModel.getActiveTripleStore().getName();
                for (TripleStore tripleStore : tripleStoreModel.getTripleStores()) {
                    NarrowFrameStore nfs = tripleStore.getNarrowFrameStore();
                    RemoteServerNarrowFrameStore remoteNarrowFrameStore = new ServerNarrowFrameStore(nfs, getKnowledgeBase(), getKnowledgeBase());
                    remoteNarrowFrameStore = (RemoteServerNarrowFrameStore) UnicastRemoteObject.exportObject(remoteNarrowFrameStore);
 
                    frameStores.add(remoteNarrowFrameStore);
                    namespaceManagers.add(tripleStore.getNamespaceManager());
                }
                return new Package(frameStores, namespaceManagers, activeTripleStore);
            }
            catch (RemoteException re) {
                throw new ProtegeException(re);
            }
        }
    }
    
    public class Package implements Localizable, Serializable {
        private static final long serialVersionUID = 6422510439112012894L;
        
        private List<RemoteServerNarrowFrameStore> frameStores;
        private List<NamespaceManager> namespaceManagers;
        private String activeTripleStore;

        
        
        public Package(List<RemoteServerNarrowFrameStore> frameStores,
                       List<NamespaceManager> namespaceManagers,
                       String activeTripleStore) {
            this.frameStores = frameStores;
            this.namespaceManagers = namespaceManagers;
            this.activeTripleStore = activeTripleStore;
        }

        public List<RemoteServerNarrowFrameStore> getFrameStores() {
            return frameStores;
        }



        public List<NamespaceManager> getNamespaceManagers() {
            return namespaceManagers;
        }



        public void localize(KnowledgeBase kb) {
            for (NamespaceManager ns : namespaceManagers) {
                LocalizeUtils.localize(ns, kb);
            }
        }
        
    }

}
