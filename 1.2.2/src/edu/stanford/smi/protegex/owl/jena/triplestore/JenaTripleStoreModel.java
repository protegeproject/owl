package edu.stanford.smi.protegex.owl.jena.triplestore;

import edu.stanford.smi.protege.model.framestore.InMemoryFrameDb;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.AbstractTripleStoreModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaTripleStoreModel extends AbstractTripleStoreModel {

    private JenaOWLModel owlModel;


    public JenaTripleStoreModel(JenaOWLModel owlModel) {
        super(owlModel);
        this.owlModel = owlModel;
        initTripleStores();
    }


    public TripleStore createTripleStore(String name) {
        NarrowFrameStore frameStore = new InMemoryFrameDb(name);
        String parentName = getActiveTripleStore().getName();
        mnfs.addActiveChildFrameStore(frameStore, parentName);
        TripleStore tripleStore = new JenaTripleStore(owlModel, frameStore, this);
        ts.add(tripleStore);
        updateRemoveFrameStores();
        return tripleStore;
    }


    public void deleteTripleStore(TripleStore tripleStore) {
        ts.remove(tripleStore);
        // TODO! mnfs.delete(tripleStore.getNarrowFrameStore());
    }


    public static void ensureActiveTripleStore(RDFResource resource) {
        TripleStoreUtil.ensureActiveTripleStore(resource);
    }


    public TripleStore getTripleStoreByDefaultNamespace(String namespace) {
        for (Iterator it = ts.iterator(); it.hasNext();) {
            TripleStore tripleStore = (TripleStore) it.next();
            if (namespace.equals(tripleStore.getDefaultNamespace())) {
                return tripleStore;
            }
        }
        return null;
    }


    private void initTripleStores() {
        ts = new ArrayList();
        NarrowFrameStore[] ss = (NarrowFrameStore[]) mnfs.getAvailableFrameStores().toArray(new NarrowFrameStore[0]);
        for (int i = 0; i < ss.length; i++) {
            NarrowFrameStore s = ss[i];
            ts.add(new JenaTripleStore(owlModel, s, this));
        }
        updateRemoveFrameStores();
    }


    private void updateRemoveFrameStores() {
        Collection allFrameStores = mnfs.getAvailableFrameStores();
        mnfs.setRemoveFrameStores(allFrameStores);
        owlModel.resetJenaModel();
    }
}
