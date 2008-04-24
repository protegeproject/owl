package edu.stanford.smi.protegex.owl.database.triplestore;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.ReferenceImpl;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameFactoryInvocationHandler;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.AbstractTripleStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DatabaseTripleStore extends AbstractTripleStore {

    private OWLJavaFactory javaFactory;

    public DatabaseTripleStore(OWLDatabaseModel owlModel,
                               TripleStoreModel tripleStoreModel,
                               NarrowFrameStore frameStore) {
        super(owlModel, tripleStoreModel, frameStore);
        javaFactory = owlModel.getOWLJavaFactory();

		if (!(javaFactory instanceof OWLJavaFactory)) {
			Log.getLogger().warning("Adjusting the Java factory to OWLJavaFactory");
			javaFactory = new OWLJavaFactory(owlModel);
		}
    }

    public String getName() {
        return getNarrowFrameStore().getName();
    }

    public Iterator listTriples() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
