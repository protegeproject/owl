package edu.stanford.smi.protegex.owl.model.triplestore.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.InMemoryFrameDb;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.Record;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLComplementClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLHasValue;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLUnionClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;

/**
 * A TripleStore that acts as a view on an existing NarrowFrameStore.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TripleStoreImpl implements TripleStore, ProtegeTripleAdder {
    private static transient final Logger log = Log.getLogger(TripleStoreImpl.class);
    
    private NamespaceManager namespaceManager;

    private Map<RDFProperty, AddPropertyValueHandler> addPropertyValueHandlers = new HashMap<RDFProperty, AddPropertyValueHandler>();

    private Slot directInstancesSlot;

    private Slot directTypesSlot;

    protected NarrowFrameStore frameStore;

    protected Slot nameSlot;

    protected OWLModel owlModel;

    protected TripleStoreModel tripleStoreModel;
    
    protected String originalXMLBase;

    private Collection<URI> ioAddresses = new ArrayList<URI>();
    
    private String name;
    
    public TripleStoreImpl(OWLModel owlModel, NarrowFrameStore frameStore, TripleStoreModel tripleStoreModel) {
        this.frameStore = frameStore;
        this.owlModel = owlModel;
        this.tripleStoreModel = tripleStoreModel;
        
        name = frameStore.getName();
        
        initializeNamespaceManager();

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
    
    private void initializeNamespaceManager() {
        namespaceManager = new OWLNamespaceManager(owlModel);
    }
    
    public NamespaceManager getNamespaceManager() {
        return namespaceManager;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TripleStoreImpl) {
            return frameStore.getName().equals(((TripleStoreImpl) obj).frameStore.getName());
        }
        else {
            return false;
        }
    }


    public Iterator listTriples() {
	    // TODO: This could be optimised so that a custom Iterator is used.
        KnowledgeBase kb = owlModel;
        Collection<Slot> ignoreProperties = new HashSet<Slot>();
        ignoreProperties.add(owlModel.getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES));
        ignoreProperties.add(owlModel.getRDFProperty(OWLNames.Slot.OWL_ONTOLOGY_POINTER_PROPERTY));
        ignoreProperties.add(kb.getSlot(Model.Slot.DIRECT_INSTANCES));
        ignoreProperties.add(kb.getSlot(Model.Slot.DIRECT_TYPES));
        ignoreProperties.add(kb.getSlot(ProtegeNames.Slot.CLASSIFICATION_STATUS));
        ignoreProperties.add(kb.getSlot(ProtegeNames.Slot.INFERRED_SUBCLASSES));
        ignoreProperties.add(kb.getSlot(ProtegeNames.Slot.INFERRED_SUPERCLASSES));
        ignoreProperties.add(kb.getSlot(ProtegeNames.Slot.INFERRED_TYPE));
        List triples = new ArrayList();
        Collection records = ((InMemoryFrameDb) frameStore).getRecords();
        for (Iterator it = records.iterator(); it.hasNext();) {
            Record record = (Record) it.next();
            Frame subject = record.getFrame();
            if (subject instanceof RDFResource) {
                Slot predicate = record.getSlot();
                if (log.isLoggable(Level.FINE)) {
                    log.fine("listTriples -- " + subject.getName() + " . " + predicate.getName());
                }
                if (predicate instanceof RDFProperty) {
                    if (record.getFacet() == null && !record.isTemplate() && !ignoreProperties.contains(predicate)) {
                        List values = record.getValues();
                        for (Iterator vit = values.iterator(); vit.hasNext();) {
                            Object object = vit.next();
                            if (object instanceof String && DefaultRDFSLiteral.isRawValue((String) object)) {
                                object = new DefaultRDFSLiteral(owlModel, (String) object);
                            }
                            Triple triple = new DefaultTriple((RDFResource) subject, (RDFProperty) predicate, object);
                            triples.add(triple);
                        }
                    }
                }
            }
        }
        return triples.iterator();
    }
    
    @Override
    public int hashCode() {
    	return 0;    	
    }
    

    public void add(Triple triple) {
        add(triple.getSubject(), triple.getPredicate(), triple.getObject());
    }


    public void add(RDFResource subject, RDFProperty predicate, Object object) {
        addValueFast(subject, predicate, object);
        AddPropertyValueHandler handler = addPropertyValueHandlers.get(predicate);
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

    public RDFResource getHomeResource(String name) {
        Collection values = frameStore.getFrames(nameSlot, null, false, name);
        if (values.isEmpty()) {
            return null;
        }
        else {
            return (RDFResource) values.iterator().next();
        }
    }
    
    public OWLOntology getOWLOntology() {
        String ontologyName = getName();
        if (ontologyName != null) {
            return (OWLOntology) ((KnowledgeBase) owlModel).getFrame(ontologyName);
        }
        else {
            return null;
        }
    }
    
    public String getName() {
        return name;
    }





    public String getNamespaceForPrefix(String prefix) {
    	return getNamespaceManager().getNamespaceForPrefix(prefix);
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
    	return getNamespaceManager().getPrefix(namespace);
    }


    public Collection<String> getPrefixes() {
    	return getNamespaceManager().getPrefixes();
    }


    protected Collection<Reference> getReferences(Object search) {
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


    public Iterator<RDFResource> listHomeResources() {
        Collection frames = frameStore.getFramesWithAnyValue(nameSlot, null, false);
        Collection<RDFResource> results = AbstractOWLModel.getRDFResources(owlModel, frames);
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


    public Iterator<Triple> listTriplesWithObject(RDFObject object) {
        Cls rdfproperty = ((KnowledgeBase) owlModel).getCls(RDFNames.Cls.PROPERTY);
        OWLJavaFactory factory = owlModel.getOWLJavaFactory();
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
        Collection<Triple> triples = new ArrayList<Triple>();
        Collection<Reference> refs = getReferences(search);
        for (Iterator it = refs.iterator(); it.hasNext();) {
            Reference reference = (Reference) it.next();
            if (reference.getFrame() instanceof RDFResource &&
                    reference.getSlot().hasType(rdfproperty) &&
                    !Model.SlotID.DIRECT_INSTANCES.equals(reference.getSlot().getFrameID())) {
                RDFProperty property = (RDFProperty) factory.createSlot(reference.getSlot().getFrameID(), 
                                                                        reference.getSlot().getDirectTypes());
                Triple triple = new DefaultTriple((RDFResource) reference.getFrame(), property, object);
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
        name = value;
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
    	getNamespaceManager().setPrefix(namespace, prefix);
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


    public String getOriginalXMLBase() {    
        return originalXMLBase;
    }
    
    public void setOriginalXMLBase(String xmlBase) {
        originalXMLBase = xmlBase;      
    }
    
    
    @Override
    public String toString() {
        return "TripleStore(" + getName() + ")";
    }


    public void dump(Level level) {
        if (log.isLoggable(level)) {
            log.log(level, "Triples in " + getName());
            Iterator it = listTriples();
            while (it.hasNext()) {
                log.log(level, " - " + it.next());
            }
        }
    }
    
    public void dispose() {
        addPropertyValueHandlers.clear();
        addPropertyValueHandlers = null;
        
    }

    public void addIOAddress(URI uri) {
        if (!ioAddresses.contains(uri)) {
            ioAddresses.add(uri);
        }
    }

    public Collection<URI> getIOAddresses() {
        return Collections.unmodifiableCollection(ioAddresses);
    }

    public void removeIOAddress(URI uri) {
        ioAddresses.remove(uri);
    }
    
}
