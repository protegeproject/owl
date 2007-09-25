package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.jena.triplestore.JenaTripleStore;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.util.Collections;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultTripleStoreTestCase extends AbstractJenaTestCase {

    public void testDefaultTripleStores() {
        List stores = owlModel.getTripleStoreModel().getTripleStores();
        assertSize(2, stores);
        TripleStore systemStore = (TripleStore) stores.get(0);
        TripleStore userStore = (TripleStore) stores.get(1);
        assertEquals(userStore, owlModel.getTripleStoreModel().getActiveTripleStore());
        owlModel.getTripleStoreModel().setActiveTripleStore(systemStore);
        assertEquals(systemStore, owlModel.getTripleStoreModel().getActiveTripleStore());
        owlModel.getTripleStoreModel().setActiveTripleStore(userStore);
        assertEquals(userStore, owlModel.getTripleStoreModel().getActiveTripleStore());

        NarrowFrameStore nfs = ((JenaTripleStore) systemStore).getNarrowFrameStore();
        nfs.getValues(owlModel.getOWLThingClass(), owlModel.getSlot(Model.Slot.DIRECT_TYPES), null, false);
        RDFProperty aldi = new DefaultRDFProperty(owlModel, new FrameID("frame_1999998"));
        nfs.addValues(aldi, aldi, null, false, Collections.singleton("Value"));
    }


}
