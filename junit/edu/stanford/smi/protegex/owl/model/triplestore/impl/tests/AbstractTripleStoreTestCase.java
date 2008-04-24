package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractTripleStoreTestCase extends AbstractJenaTestCase {

    private int frameIDCount = 19999;

    protected RDFProperty rdfTypeProperty;

    protected TripleStore ts;


    protected RDFResource createAnonymousResource() {
        return createRDFResource(null);
    }


    protected RDFResource createRDFResource(String name) {
        RDFResource frame = new DefaultRDFProperty(owlModel, createFrameID());
        if (name == null) {
            name = owlModel.getNextAnonymousResourceName();
        }
        owlModel.getOWLFrameStore().setFrameName(frame, name);
        return frame;
    }


    protected FrameID createFrameID() {
        return FrameID.createLocal(frameIDCount++);
    }


    protected void setUp() throws Exception {
        super.setUp();
        ts = owlModel.getTripleStoreModel().getActiveTripleStore();
        rdfTypeProperty = owlModel.getRDFTypeProperty();
    }
}
