package edu.stanford.smi.protegex.owl.database.triplestore;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.ReferenceImpl;
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

    private OWLFrameFactoryInvocationHandler handler;

    private OWLJavaFactory javaFactory;


    public DatabaseTripleStore(OWLDatabaseModel owlModel,
                               TripleStoreModel tripleStoreModel,
                               NarrowFrameStore frameStore) {
        super(owlModel, tripleStoreModel, frameStore);
        javaFactory = new OWLJavaFactory(owlModel);
        handler = new OWLFrameFactoryInvocationHandler();
    }


    public boolean addValue(Instance subject, Slot slot, Object object) {
        addValueFast(subject, slot, object);
        return true;
    }


    public RDFResource getHomeResource(String name) {
        Collection values = frameStore.getFrames(nameSlot, null, false, name);
        if (values.isEmpty()) {
            return null;
        }
        else {
            Instance instance = (Instance) values.iterator().next();
            return getRDFResource(instance);
        }
    }


    private RDFResource getRDFResource(Instance instance) {
        if (instance instanceof RDFResource) {
            return (RDFResource) instance;
        }
        Collection directTypes = instance.getDirectTypes();
        Instance in = null;
        if (directTypes.isEmpty()) {
            return new DefaultRDFProperty(owlModel, instance.getFrameID());
        }
        else {
            boolean allClasses = true;
            for (Iterator it = directTypes.iterator(); it.hasNext();) {
                if (!(it.next() instanceof Cls)) {
                    allClasses = false;
                }
            }
            if (allClasses) {
                if (instance instanceof Cls) {
                    in = javaFactory.createCls(instance.getFrameID(), directTypes);
                }
                else if (instance instanceof Slot) {
                    in = javaFactory.createSlot(instance.getFrameID(), directTypes);
                }
                else {
                    in = javaFactory.createSimpleInstance(instance.getFrameID(), directTypes);
                }
            }
        }
        if (!(in instanceof RDFResource)) {
            return new DefaultRDFProperty(owlModel, instance.getFrameID());
        }
        else {
            return (RDFResource) in;
        }
    }


    protected Collection getReferences(Object search) {
        Collection s = super.getReferences(search);
        if (s.isEmpty()) {
            return s;
        }
        else {
            for (Iterator it = s.iterator(); it.hasNext();) {
                ReferenceImpl reference = (ReferenceImpl) it.next();
                Frame newFrame = handler.convertInstance((Instance) reference.getFrame());
                Slot newSlot = (Slot) handler.convertInstance(reference.getSlot());
                reference.set(newFrame, newSlot, reference.getFacet(), reference.isTemplate());
            }
            return s;
        }
    }


    public String getName() {
        return getNarrowFrameStore().getName();
    }


    protected Collection getValues(Instance instance, Slot slot) {
        return wrap(super.getValues(instance, slot));
    }


    public Iterator listObjects(RDFResource subject, RDFProperty property) {
        return wrap(super.listObjects(subject, property));
    }


    public Iterator listSubjects(RDFProperty property) {
        return wrap(super.listSubjects(property));
    }


    public Iterator listTriples() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    public Iterator wrap(Iterator it) {
        List results = new ArrayList();
        while (it.hasNext()) {
            Instance next = (Instance) it.next();
            if (next instanceof RDFResource) {
                results.add(next);
            }
            else {
                RDFResource resource = getRDFResource(next);
                results.add(resource);
            }
        }
        return results.iterator();
    }


    public Collection wrap(Collection c) {
        return handler.convertCollection(c);
    }
}
