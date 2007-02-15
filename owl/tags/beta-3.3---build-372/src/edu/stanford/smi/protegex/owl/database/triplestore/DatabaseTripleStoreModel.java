package edu.stanford.smi.protegex.owl.database.triplestore;

import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.AbstractTripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.TripleChangePostProcessor;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DatabaseTripleStoreModel extends AbstractTripleStoreModel {

    private OWLDatabaseModel owlModel;

    private TripleStore systemTripleStore;

    private DatabaseTripleStore userTripleStore;


    public DatabaseTripleStoreModel(OWLDatabaseModel owlModel) {
        super(owlModel);
        this.owlModel = owlModel;
        NarrowFrameStore frameStore = mnfs.getActiveFrameStore();
        userTripleStore = new DatabaseTripleStore(owlModel, this, frameStore);
        NarrowFrameStore systemFrameStore = mnfs.getSystemFrameStore();
        this.systemTripleStore = new DatabaseTripleStore(owlModel, this, systemFrameStore);
        ts.add(systemTripleStore);
        ts.add(userTripleStore);
        if (userTripleStore.getName() == null) {
            userTripleStore.setName("top");
        }
    }

    /**
     * This is a constructor for the case when the model does not have a narrow frame store.
     * <p/>
     * For example, server side OWL models do not have narrow frame stores.
     *
     * @param owlModel
     * @param nfs
     */
    public DatabaseTripleStoreModel(OWLDatabaseModel owlModel, NarrowFrameStore nfs) {
        super(owlModel);
        this.owlModel = owlModel;
        userTripleStore = new DatabaseTripleStore(owlModel, this, nfs);
        systemTripleStore = userTripleStore;
        ts.add(userTripleStore);
        if (userTripleStore.getName() == null) {
            userTripleStore.setName("top");
        }
    }


    public TripleStore createTripleStore(String name) {
        return userTripleStore;
    }


    public void deleteTripleStore(TripleStore tripleStore) {
    }


    public void endTripleStoreChanges() {
        owlModel.flushCache();
        TripleChangePostProcessor.postProcess(owlModel);
    }


    public TripleStore getTripleStoreByDefaultNamespace(String namespace) {
        return userTripleStore;
    }
}
