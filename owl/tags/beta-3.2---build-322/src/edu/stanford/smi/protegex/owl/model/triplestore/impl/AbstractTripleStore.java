package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.*;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

import java.net.URI;
import java.util.*;

/**
 * A TripleStore that acts as a view on an existing NarrowFrameStore.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractTripleStore implements ProtegeTripleAdder, TripleStore {

    private Map addPropertyValueHandlers = new HashMap();

    private Slot directInstancesSlot;

    private Slot directTypesSlot;

    protected NarrowFrameStore frameStore;

    protected Slot nameSlot;

    protected OWLModel owlModel;

    protected TripleStoreModel tripleStoreModel;


    public AbstractTripleStore(OWLModel owlModel,
                               TripleStoreModel tripleStoreModel,
                               NarrowFrameStore frameStore) {
        this.frameStore = frameStore;
        this.owlModel = owlModel;
        this.tripleStoreModel = tripleStoreModel;

        KnowledgeBase kb = owlModel;
        directTypesSlot = kb.getSlot(Model.Slot.DIRECT_TYPES);
        directInstancesSlot = kb.getSlot(Model.Slot.DIRECT_INSTANCES);
        nameSlot = kb.getSlot(Model.Slot.NAME);

        initAACHandler(OWLNames.Slot.COMPLEMENT_OF, OWLNames.Cls.COMPLEMENT_CLASS, DefaultOWLComplementClass.class);
        initAACHandler(OWLNames.Slot.INTERSECTION_OF, OWLNames.Cls.INTERSECTION_CLASS, DefaultOWLIntersectionClass.class);
        initAACHandler(OWLNames.Slot.UNION_OF, OWLNames.Cls.UNION_CLASS, DefaultOWLUnionClass.class);
        initAACHandler(OWLNames.Slot.CARDINALITY, OWLNames.Cls.CARDINALITY_RESTRICTION, DefaultOWLCardinality.class);
        initAACHandler(OWLNames.Slot.MIN_CARDINALITY, OWLNames.Cls.MIN_CARDINALITY_RESTRICTION, DefaultOWLMinCardinality.class);
        initAACHandler(OWLNames.Slot.MAX_CARDINALITY, OWLNames.Cls.MAX_CARDINALITY_RESTRICTION, DefaultOWLMaxCardinality.class);
        initAACHandler(OWLNames.Slot.ALL_VALUES_FROM, OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION, DefaultOWLAllValuesFrom.class);
        initAACHandler(OWLNames.Slot.SOME_VALUES_FROM, OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION, DefaultOWLSomeValuesFrom.class);
        initAACHandler(OWLNames.Slot.HAS_VALUE, OWLNames.Cls.HAS_VALUE_RESTRICTION, DefaultOWLHasValue.class);

        initHandler(OWLNames.Slot.INVERSE_OF, new AddOWLInverseOfPropertyHandler(this, kb));
        initHandler(RDFSNames.Slot.SUB_CLASS_OF, new AddRDFSSubClassOfPropertyHandler(this, kb));
        initHandler(RDFSNames.Slot.SUB_PROPERTY_OF, new AddRDFSSubPropertyOfPropertyHandler(this, kb));
        initHandler(RDFNames.Slot.TYPE, new AddRDFTypePropertyHandler(this, kb, tripleStoreModel, this));
    }


    public void add(Triple triple) {
        add(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }


    public void add(RDFResource subject, RDFProperty predicate, Object object) {
        addValueFast(subject, predicate, object);
        AddPropertyValueHandler handler = (AddPropertyValueHandler) addPropertyValueHandlers.get(predicate);
        if (handler != null) {
            handler.handleAdd(subject, object);
        }
    }


    public boolean addValue(Instance subject, Slot slot, Object object) {
        final Collection values = getValues(subject, slot);
        if (!values.contains(object)) {
            addValueFast(subject, slot, object);
            return true;
        }
        else {
            return false;
        }
    }


    public void addValueFast(Instance subject, Slot slot, Object object) {
        frameStore.addValues(subject, slot, null, false, Collections.singleton(object));
    }


    public boolean contains(Triple triple) {
        return contains(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }


    public boolean contains(RDFResource subject, RDFProperty predicate, Object object) {
        Collection values = getValues(subject, predicate);
        if (values != null) {
            if (object instanceof RDFSLiteral) {
                values = subject.getOWLModel().asRDFSLiterals(values);
            }
            return values.contains(object);
        }
        else {
            return false;
        }
    }


    public String getDefaultNamespace() {
        return getNamespaceForPrefix("");
    }


    public String getName() {
        return frameStore.getName();
    }


    public RDFResource getHomeResource(String name) {
        Collection values = frameStore.getFrames(nameSlot, null, false, name);
        if (values.isEmpty()) {
            return null;
        }
        else {
            return (RDFResource) values.iterator().next();
        }
    }


    public String getNamespaceForPrefix(String prefix) {
        Instance ontology = TripleStoreUtil.getFirstOntology(owlModel, this);
        if (ontology != null) {
            prefix += ":";
            Slot prefixesSlot = owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
            Collection values = getNarrowFrameStore().getValues(ontology, prefixesSlot, null, false);
            for (Iterator it = values.iterator(); it.hasNext();) {
                String value = (String) it.next();
                if (value.startsWith(prefix)) {
                    return value.substring(prefix.length());
                }
            }
        }
        return null;
    }


    public NarrowFrameStore getNarrowFrameStore() {
        return frameStore;
    }


    public Collection getSlotValues(Instance instance, Slot slot) {
        return tripleStoreModel.getSlotValues(instance, slot);
    }


    protected Collection getValues(Instance instance, Slot slot) {
        return frameStore.getValues(instance, slot, null, false);
    }


    public String getPrefix(String namespace) {
        Instance ontology = TripleStoreUtil.getFirstOntology(owlModel, this);
        if (ontology != null) {
            String suffix = ":" + namespace;
            Slot prefixesSlot = owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
            Collection values = getNarrowFrameStore().getValues(ontology, prefixesSlot, null, false);
            for (Iterator it = values.iterator(); it.hasNext();) {
                String value = (String) it.next();
                if (value.endsWith(suffix)) {
                    return value.substring(0, value.length() - suffix.length());
                }
            }
        }
        return null;
    }


    public Collection getPrefixes() {
        Collection results = new ArrayList();
        Instance ontology = TripleStoreUtil.getFirstOntology(owlModel, this);
        if (ontology != null) {
            Slot prefixesSlot = owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
            Collection values = getNarrowFrameStore().getValues(ontology, prefixesSlot, null, false);
            for (Iterator it = values.iterator(); it.hasNext();) {
                String value = (String) it.next();
                int index = value.indexOf(':');
                results.add(value.substring(0, index));
            }
        }
        return results;
    }


    protected Collection getReferences(Object search) {
        return frameStore.getReferences(search);
    }


    private void initAACHandler(String propertyName, String clsName, Class clazz) {
        final Cls cls = ((KnowledgeBase) owlModel).getCls(clsName);
        initHandler(propertyName,
                new AddAnonymousClassPropertyPropertyHandler(this, cls, clazz, tripleStoreModel));
    }


    private void initHandler(String propertyName, AddPropertyValueHandler handler) {
        addPropertyValueHandlers.put(owlModel.getRDFProperty(propertyName), handler);
    }


    public Iterator listHomeResources() {
        Collection frames = frameStore.getFramesWithAnyValue(nameSlot, null, false);
        Collection results = AbstractOWLModel.getRDFResources(owlModel, frames);
        return results.iterator();
    }


    public Iterator listObjects(RDFResource subject, RDFProperty property) {
        return getValues(subject, property).iterator();
    }


    public Iterator listSubjects(RDFProperty property) {
        Collection frames = frameStore.getFramesWithAnyValue(property, null, false);
        return frames.iterator();
    }


    public Iterator listSubjects(RDFProperty predicate, Object object) {
        if (object instanceof DefaultRDFSLiteral) {
            object = DefaultRDFSLiteral.getPlainValueIfPossible(object);
            if (object instanceof DefaultRDFSLiteral) {
                object = ((DefaultRDFSLiteral) object).getRawValue();
            }
        }
        return frameStore.getFrames(predicate, null, false, object).iterator();
    }


    public Iterator listTriplesWithObject(RDFObject object) {
        Object search = object;
        if (object instanceof DefaultRDFSLiteral) {
            Object plain = ((DefaultRDFSLiteral) object).getPlainValue();
            if (plain == null) {
                search = ((DefaultRDFSLiteral) object).getRawValue();
            }
            else {
                search = plain;
            }
        }
        Collection triples = new ArrayList();
        Collection refs = getReferences(search);
        for (Iterator it = refs.iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (reference.getFrame() instanceof RDFResource &&
                    reference.getSlot() instanceof RDFProperty &&
                    !Model.SlotID.DIRECT_INSTANCES.equals(reference.getSlot().getFrameID())) {
                Triple triple = new DefaultTriple((RDFResource) reference.getFrame(), (RDFProperty) reference.getSlot(), object);
                triples.add(triple);
            }
        }
        return triples.iterator();
    }


    public Iterator listTriplesWithSubject(RDFResource subject) {
        Collection triples = new ArrayList();
        Iterator properties = subject.getOWLModel().listRDFProperties();
        while (properties.hasNext()) {
            RDFProperty property = (RDFProperty) properties.next();
            Iterator it = listObjects(subject, property);
            while (it.hasNext()) {
                Object object = it.next();
                Triple triple = new DefaultTriple(subject, property, object);
                triples.add(triple);
            }
        }
        return triples.iterator();
    }


    public void remove(Triple triple) {
        remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }


    public void remove(RDFResource subject, RDFProperty predicate, Object object) {
        if (predicate.equals(subject.getOWLModel().getRDFTypeProperty())) {
            removeDirectType(subject, object);
        }
        removeValue(subject, predicate, object);
    }


    public void setName(String value) {
        getNarrowFrameStore().setName(value);
    }


    private void removeDirectType(RDFResource subject, Object object) {
        removeValue((Instance) object, directInstancesSlot, subject);
        removeValue(subject, directTypesSlot, object);
    }


    public void removePrefix(String prefix) {
        String namespace = getNamespaceForPrefix(prefix);
        if (namespace != null) {
            Instance ontology = TripleStoreUtil.getFirstOntology(owlModel, this);
            Slot prefixesSlot = owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
            String value = prefix + ":" + namespace;
            getNarrowFrameStore().removeValue(ontology, prefixesSlot, null, false, value);
        }
    }


    protected void removeValue(Instance subject, Slot slot, Object value) {
        frameStore.removeValue(subject, slot, null, false, value);
    }


    public void setDefaultNamespace(String value) {
        setPrefix(value, "");
    }


    public void setDefaultNamespace(URI uri) {
        setDefaultNamespace(uri.toString());
    }


    public void setPrefix(String namespace, String prefix) {
        removePrefix(prefix);
        Instance ontology = TripleStoreUtil.getFirstOntology(owlModel, this);
        Slot prefixesSlot = owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
        String value = prefix + ":" + namespace;
        getNarrowFrameStore().addValues(ontology, prefixesSlot, null, false, Collections.singleton(value));
    }


    public void setPrefix(URI namespace, String prefix) {
        setPrefix(namespace.toString(), prefix);
    }


    public void setRDFResourceName(RDFResource resource, String name) {
        TripleStore home = tripleStoreModel.getHomeTripleStore(resource);
        if (home != null) {
            final NarrowFrameStore nfs = home.getNarrowFrameStore();
            final List values = nfs.getValues(resource, nameSlot, null, false);
            Iterator it = values.iterator();
            if (it.hasNext()) {
                String oldName = (String) it.next();
                nfs.removeValue(resource, nameSlot, null, false, oldName);
            }
        }
        addValueFast(resource, nameSlot, name);
    }


    public void sortPropertyValues(RDFResource resource, RDFProperty property, Comparator comparator) {
        TripleStoreUtil.sortSlotValues(getNarrowFrameStore(), resource, property, comparator);
    }


    public String toString() {
        return "TripleStore(" + getName() + ")";
    }


    public void dump() {
        System.out.println("Triples in " + getName());
        Iterator it = listTriples();
        while (it.hasNext()) {
            System.out.println(" - " + it.next());
        }
    }
}
