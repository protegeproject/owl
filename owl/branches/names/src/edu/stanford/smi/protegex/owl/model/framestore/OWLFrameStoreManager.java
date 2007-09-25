package edu.stanford.smi.protegex.owl.model.framestore;

import edu.stanford.smi.protege.model.framestore.DeleteSimplificationFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFrameStoreManager extends FrameStoreManager {

    public OWLFrameStoreManager(OWLModel owlModel) {
        super(owlModel);
    }


    protected FrameStore create(Class clas) {
        if (clas == DeleteSimplificationFrameStore.class) {
            return new OWLDeleteSimplificationFrameStore();
        }
        else {
            return super.create(clas);
        }
    }
}
