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


    public TripleStore createTripleStore(String name) {
        return userTripleStore;
    }


    public void deleteTripleStore(TripleStore tripleStore) {
    }


    public void endTripleStoreChanges() {
        owlModel.flushCache();
        owlModel.updateProtegeMetaOntologyImported();
        TripleChangePostProcessor.postProcess(owlModel);
    }


    public TripleStore getTripleStoreByDefaultNamespace(String namespace) {
        return userTripleStore;
    }
}
