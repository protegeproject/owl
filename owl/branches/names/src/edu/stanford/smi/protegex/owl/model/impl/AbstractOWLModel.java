package edu.stanford.smi.protegex.owl.model.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.FrameNameValidator;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.server.framestore.background.ServerCacheStateMachine;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.graph.JenaModelFactory;
import edu.stanford.smi.protegex.owl.jena.parser.UnresolvedImportHandler;
import edu.stanford.smi.protegex.owl.model.DefaultTaskManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFExternalResource;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSDatatypeFactory;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.TaskManager;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classdisplay.OWLClassDisplayFactory;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyListener;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.model.event.ResourceAdapter;
import edu.stanford.smi.protegex.owl.model.event.ResourceListener;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.framestore.FacetUpdateFrameStore;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameStore;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameStoreManager;
import edu.stanford.smi.protegex.owl.model.project.DefaultOWLProject;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.model.project.SettingsMap;
import edu.stanford.smi.protegex.owl.model.query.QueryResults;
import edu.stanford.smi.protegex.owl.model.query.SPARQLQueryResults;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTriple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.DefaultTuple;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.TripleStoreModelImpl;
import edu.stanford.smi.protegex.owl.model.validator.DefaultPropertyValueValidator;
import edu.stanford.smi.protegex.owl.model.validator.PropertyValueValidator;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;
import edu.stanford.smi.protegex.owl.server.OwlStateMachine;
import edu.stanford.smi.protegex.owl.server.triplestore.ClientTripleStoreModel;
import edu.stanford.smi.protegex.owl.swrl.SWRLSystemFrames;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;
import edu.stanford.smi.protegex.owl.testing.OWLTest;
import edu.stanford.smi.protegex.owl.testing.OWLTestLibrary;
import edu.stanford.smi.protegex.owl.ui.repository.UnresolvedImportUIHandler;
import edu.stanford.smi.protegex.owl.ui.widget.OWLFormWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLWidgetMapper;
import edu.stanford.smi.protegex.owl.util.OWLBrowserSlotPattern;


/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLModel extends DefaultKnowledgeBase
        implements OWLModel {

    private static transient Logger log = Log.getLogger(AbstractOWLModel.class);
    
    /**
     * If the value of this property key is true in protege.properties, 
     * the default mechanism of the underlying frame model will be used
     * at class, property and individual creation time. 
     * It false, the defaults mechanism will not be used, which might bring
     * performance improvements. 
     * If false, the default mechanism will be disabled. 
     * The default value of this property is false. 
     */
    public static final String OWL_MODEL_INIT_DEFAULTS_AT_CREATION = "owlmodel.init.defaults";

    public static final String OWL_MODEL_EXPAND_SHORT_NAME_IN_METHODS = "owlmodel.expand.short.name.in.methods";

    private static final String SEARCH_SYNONYMS_KEY = "OWL-SEARCH-SYNONYMS-SLOTS";

    private static final String SEARCH_SYNONYMS_SEPARATOR = ",";

    public static final String DEFAULT_TODO_PREFIX = "TODO";

    public static final String[] DEFAULT_USED_LANGUAGES = {"de", "en", "es", "fr", "it", "nl", "pt", "ru" };
    
    public static final String ANONYMOUS_BASE = "@";
    
    public static final String DEFAULT_ANNOTATION_PROPERTY_NAME = "annotationProperty";

    public static final String DEFAULT_CLASS_NAME = "Class";

    public static final String DEFAULT_DATATYPE_PROPERTY_NAME = "datatypeProperty";

    public static final String DEFAULT_INDIVIDUAL_NAME = "Individual";

    public static final String DEFAULT_OBJECT_PROPERTY_NAME = "objectProperty";

    public static final String DEFAULT_PROPERTY_NAME = "property";
          
    /**
     * The characters that are valid name parts (in addition to the Java identifier chars)
     */
    private final static String VALID_SYMBOLS = "-."; // "-.+/:";
    
    private final static String AUTO_REPAIR_ENABLED = "TestAutoRepairEnabled";

    private final static String TEST_LIST_NAME = "DisabledTest";

    private final static String TESTGROUP_LIST_NAME = "DisabledTestGroups";
    
    
    private boolean loadDefaults = false;
    
    private boolean expandShortNameInMethods = false;
    
    
    private static UnresolvedImportHandler unresolvedImportHandler = new UnresolvedImportUIHandler();
    
    private com.hp.hpl.jena.rdf.model.Model jenaModel;
    

    private OWLClassDisplay owlClassRenderer = OWLClassDisplayFactory.getDefaultDisplay();

    private OWLProject owlProject;

    private PropertyValueValidator propertyValueValidator = new DefaultPropertyValueValidator();


    private Set<Cls> defaultAnonymousTypes = new HashSet<Cls>();

    private RDFSDatatypeFactory rdfsDatatypeFactory = new DefaultRDFSDatatypeFactory(this);

    private TaskManager taskManager;

    private RepositoryManager repositoryManager;

    /**
     * The top OWL ontology is the top level ontology from the model.
     * E.g. in file mode, it is the ontology that is loaded from an URI
     * or an input stream and that may import other ontologies.
     * The top OWL ontology is stored in the top triple store.
     * The top OWL ontology is not dependent on the active ontology.
     */
    private OWLOntology topOWLOntology;
    
    private Slot protegeSubclassesDisjointProperty;
    
    private TripleStoreModel tripleStoreModel;


    
    public AbstractOWLModel(KnowledgeBaseFactory factory) {
        super(factory);

        setFrameFactory(new SWRLJavaFactory(this));
        
        initializeLoadDefaults();
        initializeExpandShortNamesInMethods();

        initialize();
    }

    protected void initializeLoadDefaults() {
    	loadDefaults = ApplicationProperties.getBooleanProperty(OWL_MODEL_INIT_DEFAULTS_AT_CREATION, false);
    }

    
    protected void initializeExpandShortNamesInMethods() {
    	expandShortNameInMethods = ApplicationProperties.getBooleanProperty(OWL_MODEL_EXPAND_SHORT_NAME_IN_METHODS, false);
    }

    
    public void initialize() {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Phase 2 initialization of OWL Model starts");
        }

        boolean eventEnabled = setGenerateEventsEnabled(false);

        setGenerateDeletingFrameEventsEnabled(true);

        setDefaultClsMetaCls(getOWLNamedClassClass());
        setDefaultSlotMetaCls(getOWLDatatypePropertyClass());
        
        //TODO - change name validation
        setFrameNameValidator(new FrameNameValidator() {


            public String getErrorMessage(String name, Frame frame) {
                if (frame instanceof RDFUntypedResource) {
                    return "The name \"" + name + "\" is not a valid URI.";
                }
                else {
                    String validName = getValidNamespaceFrameName(name);
                    return "The name \"" + name + "\" is not a valid OWL identifier.\n" +
                    "You might want to use \"" + validName + "\" instead.";
                }
            }


            public boolean isValid(String name, Frame frame) {
                if (frame != null && !frame.isEditable()) {
                    return true;
                }
                else if (frame instanceof RDFUntypedResource) {
                    if (name.equals(name.trim()) && name.length() > 4) {
                        try {
                            new URI(name);
                            return true;
                        }
                        catch (Exception ex) {
                        }
                    }
                    return false;
                }
                else {
                    NamespaceManager nsm = getNamespaceManager();
                    return AbstractOWLModel.isValidOWLFrameName(nsm, name);
                }
            }
        });

        defaultAnonymousTypes.add(getRDFListClass());
        defaultAnonymousTypes.add(getOWLAllValuesFromClass());
        defaultAnonymousTypes.add(getOWLSomeValuesFromClass());
        defaultAnonymousTypes.add(getOWLHasValueClass());
        defaultAnonymousTypes.add(getOWLMinCardinalityClass());
        defaultAnonymousTypes.add(getOWLMaxCardinalityClass());
        defaultAnonymousTypes.add(getOWLCardinalityClass());
        defaultAnonymousTypes.add(getOWLComplementClassClass());
        defaultAnonymousTypes.add(getOWLIntersectionClassClass());
        defaultAnonymousTypes.add(getOWLUnionClassClass());
        defaultAnonymousTypes.add(getOWLEnumeratedClassClass());
        defaultAnonymousTypes.add(getOWLAllDifferentClass());
        defaultAnonymousTypes.add(getOWLDataRangeClass());

        taskManager = new DefaultTaskManager();
        taskManager.setProgressDisplay(new NoopProgressDisplay());
        
        // getFrameStoreManager().setOwlFrameStoresEnabled(true);

        setGenerateEventsEnabled(eventEnabled);

    }
    

    public void addImport(URI ontologyName) throws IOException {
        TripleStoreModel tripleStoreModel = getTripleStoreModel();
        for (TripleStore tripleStore : tripleStoreModel.getTripleStores()) {
            if (tripleStore.getIOAddresses().contains(ontologyName.toString())) {
                return;
            }
        }
        Repository rep = getRepository(getTripleStoreModel().getActiveTripleStore(), ontologyName);
        if(rep != null) {
            try {
                tripleStoreModel.setViewActiveOnly(true);
                TripleStore importedTripleStore = rep.addImport(this, ontologyName);
                importedTripleStore.addIOAddress(ontologyName.toString());
            }
            finally {
                tripleStoreModel.setViewActiveOnly(false);
            }

        }
    }

    private Repository getRepository(TripleStore tripleStore,
                                            URI ontologyName) {
        RepositoryManager rm = getRepositoryManager();
        // Get the repository (ask the system to create a HTTP repository if necessary)
        Repository rep = rm.getRepository(ontologyName, true);
        if(rep == null) {
            rep = unresolvedImportHandler.handleUnresolvableImport(this, tripleStore, ontologyName);
            if(rep != null) {
                rm.addProjectRepository(0, rep);
            }
        }
        return rep;
    }

    public void addClassListener(ClassListener listener) {
        if (!(listener instanceof ClassAdapter)) {
            throw new IllegalArgumentException("Listener must be a ClassAdapter");
        }
        addClsListener(listener);
    }


    public void addModelListener(ModelListener listener) {
        if (!(listener instanceof ModelAdapter)) {
            throw new IllegalArgumentException("Listener must be a ModelAdapter");
        }
        addKnowledgeBaseListener(listener);
    }


    public void addPropertyListener(PropertyListener listener) {
        if (!(listener instanceof PropertyAdapter)) {
            throw new IllegalArgumentException("Listener must be a PropertyAdapter");
        }
        addSlotListener(listener);
    }


    public void addPropertyValueListener(PropertyValueListener listener) {
        if (!(listener instanceof PropertyValueAdapter)) {
            throw new IllegalArgumentException("Listener must be a PropertyValueAdapter");
        }
        addFrameListener(listener);
    }


    public RDFSLiteral asRDFSLiteral(Object value) {
        if (value == null) {
            return null;
        }
        else if (value instanceof RDFSLiteral) {
            return (RDFSLiteral) value;
        }
        else {
            return createRDFSLiteral(value);
        }
    }


    public RDFObject asRDFObject(Object object) {
        if (object == null) {
            return null;
        }
        else if (object instanceof RDFExternalResource) {
            return null;
        }
        else if (object instanceof RDFResource) {
            return (RDFObject) object;
        }
        else {
            return createRDFSLiteral(object);
        }
    }


    public List asRDFSLiterals(Collection values) {
        List result = new LinkedList();
        for (Iterator it = values.iterator(); it.hasNext();) {
        	Object o = it.next();
        	result.add(asRDFSLiteral(o));  
        }
        return result;
    }


    public void addResourceListener(ResourceListener listener) {
        if (!(listener instanceof ResourceAdapter)) {
            throw new IllegalArgumentException("Listener must be a ResourceAdapter");
        }
        addInstanceListener(listener);
    }


    public abstract void initOWLFrameFactoryInvocationHandler();

    
    @Override
    protected SWRLJavaFactory createFrameFactory() {
        return new SWRLJavaFactory(this);
    }

    @Override
    protected SWRLSystemFrames createSystemFrames() {
        return new SWRLSystemFrames(this);
    }
    
    @Override
    public synchronized SWRLSystemFrames getSystemFrames() {
        return (SWRLSystemFrames) super.getSystemFrames();
    }


    public TaskManager getTaskManager() {
        if (taskManager == null) {
            taskManager = new DefaultTaskManager();
        }
        return taskManager;
    }

    public void removeClassListener(ClassListener listener) {
        removeClsListener(listener);
    }


    public void removeModelListener(ModelListener listener) {
        removeKnowledgeBaseListener(listener);
    }


    public void removePropertyListener(PropertyListener listener) {
        removeSlotListener(listener);
    }


    public void removePropertyValueListener(PropertyValueListener listener) {
        removeFrameListener(listener);
    }


    public void removeResourceListener(ResourceListener listener) {
        removeInstanceListener(listener);
    }


    public void setOWLClassDisplay(OWLClassDisplay renderer) {
        assert renderer != null;
        this.owlClassRenderer = renderer;
    }


    public OWLJavaFactory getOWLJavaFactory() {
        return (OWLJavaFactory) super.getFrameFactory();
    }


    public void setOWLJavaFactory(OWLJavaFactory factory) {
        super.setFrameFactory(factory);
    }


    public void setOWLProject(OWLProject owlProject) {
        this.owlProject = owlProject;
    }


    public PropertyValueValidator getPropertyValueValidator() {
        return propertyValueValidator;
    }


    public boolean isValidPropertyValue(RDFResource subject, RDFProperty predicate, Object value) {
        if (getPropertyValueValidator() == null) {
            return true;
        }
        else {
            return getPropertyValueValidator().isValidPropertyValue(subject, predicate, value);
        }
    }


    public void setPropertyValueValidator(PropertyValueValidator validator) {
        this.propertyValueValidator = validator;
    }


    /**
     * Copies all facet values of restriction superclasses into their named subclasses
     * and thus makes sure that both values are synchronized.
     * This method must be called after a file has been loaded.
     */
    public void copyFacetValuesIntoNamedClses() {
      FacetUpdateFrameStore facetUpdateFrameStore = getFrameStoreManager().getFacetUpdateFrameStore();
      if (FrameStoreManager.isEnabled(facetUpdateFrameStore)) {
          facetUpdateFrameStore.copyFacetValuesIntoNamedClses();
      }
    }


    public String createNewResourceName(String partialLocalName) {
    	TripleStore activeTripleStore = getTripleStoreModel().getActiveTripleStore(); 
    	String activeNamespace = activeTripleStore.getDefaultNamespace();
    	    	
    	if (activeNamespace == null) { //TODO TT - how to handle this?
    		activeNamespace = activeTripleStore.getName() + "#";
    	}

    	return getUniqueFrameName(activeNamespace + partialLocalName);
    }


    public OWLAllDifferent createOWLAllDifferent() {
        return (OWLAllDifferent) createInstance(null, getOWLAllDifferentClass());
    }


    public OWLAllValuesFrom createOWLAllValuesFrom() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLAllValuesFrom) createCls(null, parents, getOWLAllValuesFromClass(), false);
    }


    public OWLAllValuesFrom createOWLAllValuesFrom(RDFProperty property, RDFResource filler) {
        OWLAllValuesFrom result = createOWLAllValuesFrom();
        result.setOnProperty(property);
        result.setFiller(filler);
        return result;
    }


    public OWLAllValuesFrom createOWLAllValuesFrom(RDFProperty property, RDFSLiteral[] oneOfValues) {
        OWLAllValuesFrom allRestriction = createOWLAllValuesFrom();
        allRestriction.setOnProperty(property);
        OWLDataRange dataRange = createOWLDataRange(oneOfValues);
        allRestriction.setFiller(dataRange);
        return allRestriction;
    }
    
    
    public RDFProperty createAnnotationProperty(String name) {
    	if (name == null) {
    		name = createUniqueNewFrameName(DEFAULT_ANNOTATION_PROPERTY_NAME);
    	}

    	RDFProperty annotationProperty = createRDFProperty(name);
    	annotationProperty.setProtegeType(getOWLAnnotationPropertyClass());    	

    	return annotationProperty;
    }


    public OWLDatatypeProperty createAnnotationOWLDatatypeProperty(String name) {
        OWLDatatypeProperty property = createOWLDatatypeProperty(name);
        ((Slot) property).setAllowsMultipleValues(true);
        ((Slot) property).addDirectType(getOWLAnnotationPropertyClass());
        return property;
    }


    public OWLObjectProperty createAnnotationOWLObjectProperty(String name) {
        OWLObjectProperty property = createOWLObjectProperty(name);
        ((Slot) property).setAllowsMultipleValues(true);
        ((Slot) property).addDirectType(getOWLAnnotationPropertyClass());
        return property;
    }


    public OWLCardinality createOWLCardinality() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLCardinality) createCls(null, parents, getOWLCardinalityClass(), false);
    }


    public OWLCardinality createOWLCardinality(RDFProperty property, int value) {
        OWLCardinality cardiRestriction = createOWLCardinality();
        cardiRestriction.setOnProperty(property);
        cardiRestriction.setCardinality(value);
        return cardiRestriction;
    }


    public OWLCardinality createOWLCardinality(RDFProperty property, int value, RDFSClass qualifier) {
        OWLCardinality owlCardinality = createOWLCardinality(property, value);
        owlCardinality.setValuesFrom(qualifier);
        return owlCardinality;
    }

    @Override
    public synchronized Cls createCls(FrameID id,
                                      Collection directSuperclasses,
                                      Collection directTypes,
                                      boolean loadDefaults) {
        if (id == null || id.getName() == null) {
            if (isDefaultAnonymousType(directTypes)) {
                id = new FrameID(getNextAnonymousResourceName());
            }
            else {
                id = new FrameID(createUniqueNewFrameName(DEFAULT_CLASS_NAME));
            }
        }

        return super.createCls(id, directSuperclasses, directTypes, loadDefaults);
    }


    public OWLComplementClass createOWLComplementClass() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLComplementClass) createCls(null, parents, getOWLComplementClassClass(), false);
    }


    public OWLComplementClass createOWLComplementClass(RDFSClass complement) {
        OWLComplementClass complementCls = createOWLComplementClass();
        complementCls.setComplement(complement);
        return complementCls;
    }


    public OWLDataRange createOWLDataRange() {
        return (OWLDataRange) getOWLDataRangeClass().createAnonymousInstance();
    }


    public OWLDataRange createOWLDataRange(RDFSLiteral[] values) {
        OWLDataRange dataRange = createOWLDataRange();
        RDFList list = createRDFList(Arrays.asList(values).iterator());
        dataRange.setPropertyValue(getOWLOneOfProperty(), list);
        return dataRange;
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name) {
        return createOWLDatatypeProperty(name, getOWLDatatypePropertyClass());
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name, OWLNamedClass metaCls) {
        if (name == null) {
            name = createUniqueNewFrameName(DEFAULT_DATATYPE_PROPERTY_NAME);
        }
        OWLDatatypeProperty slot = (OWLDatatypeProperty) createSlot(getInternalFullName(name), metaCls, loadDefaults);
   
        ((Slot) slot).setAllowsMultipleValues(true);
        ((Slot) slot).setValueType(ValueType.ANY);
        return slot;
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name, RDFSDatatype datatype) {
        OWLDatatypeProperty property = createOWLDatatypeProperty(name);
        ((Slot) property).setAllowsMultipleValues(true);
        property.setRange(datatype);
        return property;
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name, RDFSLiteral[] dataRangeLiterals) {
        OWLDatatypeProperty property = createOWLDatatypeProperty(name);
        OWLDataRange range = createOWLDataRange(dataRangeLiterals);
        property.setRange(range);
        return property;
    }

    
    @Override
	protected FrameStoreManager createFrameStoreManager() {
        return new OWLFrameStoreManager(this);
    }
    
    @Override
    public OWLFrameStoreManager getFrameStoreManager() {
        return (OWLFrameStoreManager) super.getFrameStoreManager();
    }


    public OWLEnumeratedClass createOWLEnumeratedClass() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLEnumeratedClass) createCls(null, parents, getOWLEnumeratedClassClass(), false);
    }


    public OWLEnumeratedClass createOWLEnumeratedClass(Collection instances) {
        OWLEnumeratedClass enumerationCls = createOWLEnumeratedClass();
        enumerationCls.setOneOf(instances);
        return enumerationCls;
    }


    /**
     * @deprecated
     */
    @Deprecated
	public RDFExternalResource createRDFExternalResource(String uri) {
        throw new RuntimeException("The class RDFExternalResource has been replaced with RDFUntypedResource");
    }


    public OWLHasValue createOWLHasValue() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLHasValue) createCls(null, parents, getOWLHasValueClass(), false);
    }


    public OWLHasValue createOWLHasValue(RDFProperty property, Object value) {
        OWLHasValue restriction = createOWLHasValue();
        restriction.setOnProperty(property);
        restriction.setHasValue(value);
        return restriction;
    }

    @Override
    public synchronized Instance createInstance(String name, Cls directType) {    	
    	// TT: should we ignore the loadDefaults for non-system classes?    
    	return createInstance(new FrameID(name), CollectionUtilities.createCollection(directType), loadDefaults);
    }

    @Override
    public synchronized Instance createInstance(String name, Collection directTypes) {    
         // TT: should we ignore the loadDefaults for non-system classes?
         return createInstance(new FrameID(getInternalFullName(name)), directTypes, loadDefaults);
    }
    
    @Override
    public synchronized Instance createInstance(FrameID id, Collection directTypes, boolean initializeDefaults) {
        if (id == null || id.getName() == null) {
            if (isDefaultAnonymousType(directTypes)) {
                id = new FrameID(getNextAnonymousResourceName());
            }
            else {
                Cls firstType = (Cls) directTypes.iterator().next();
                if (firstType instanceof RDFSNamedClass) {
                    id = new FrameID(createNewResourceName(((RDFSClass) firstType).getLocalName()));
                }
            }
        }
        return super.createInstance(id,directTypes, initializeDefaults);
    }


    public OWLIntersectionClass createOWLIntersectionClass() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLIntersectionClass) createCls(null, parents, getOWLIntersectionClassClass(), false);
    }


    public OWLIntersectionClass createOWLIntersectionClass(Collection clses) {
        OWLIntersectionClass intersectionCls = createOWLIntersectionClass();
        for (Iterator it = clses.iterator(); it.hasNext();) {
            RDFSClass cls = (RDFSClass) it.next();
            intersectionCls.addOperand(cls);
        }
        return intersectionCls;
    }


    public RDFList createRDFList() {
        return (RDFList) getRDFListClass().createDirectInstance(null);
    }


    public RDFList createRDFList(Iterator values) {
        return createListInstance(values, getRDFListClass());
    }


    public RDFSClass createRDFSClassFromExpression(String parsableExpression) {
        try {
            OWLClassParser parser = getOWLClassDisplay().getParser();
            return parser.parseClass(this, parsableExpression);
        }
        catch (Exception ex) {
            // Don't print Exception but return null
            return null;
        }
    }


    public RDFSDatatype createRDFSDatatype(String name) {
        return (RDFSDatatype) getRDFSDatatypeClass().createDirectInstance(name);
    }


    private RDFList createListInstance(Iterator values, Cls listCls) {
        if (!values.hasNext()) {
            return getRDFNil();
        }
        else {
            RDFList li = (RDFList) listCls.createDirectInstance(null);
            RDFList currentNode = li;
            while (values.hasNext()) {
                Object value = values.next();
                currentNode.setFirst(value);
                if (values.hasNext()) {
                    RDFList rest = (RDFList) listCls.createDirectInstance(null);
                    currentNode.setRest(rest);
                    currentNode = rest;
                }
            }
            currentNode.setRest(getRDFNil());
            return li;
        }
    }


    public OWLMaxCardinality createOWLMaxCardinality() {
        Collection parents = Collections.singleton(getAnonymousRootCls());
        return (OWLMaxCardinality) createCls(null, parents, getOWLMaxCardinalityClass(), false);
    }


    public OWLMaxCardinality createOWLMaxCardinality(RDFProperty property, int value) {
        OWLMaxCardinality maxCardiRestriction = createOWLMaxCardinality();
        maxCardiRestriction.setOnProperty(property);
        maxCardiRestriction.setCardinality(value);
        return maxCardiRestriction;
    }


    public OWLMaxCardinality createOWLMaxCardinality(RDFProperty property, int value, RDFSClass qualifier) {
        OWLMaxCardinality owlMaxCardinality = createOWLMaxCardinality(property, value);
        owlMaxCardinality.setValuesFrom(qualifier);
        return owlMaxCardinality;
    }


    public OWLMinCardinality createOWLMinCardinality() {
        Collection parents = Collections.singleton(getAnonymousRootCls());
        return (OWLMinCardinality) createCls(null, parents, getOWLMinCardinalityClass(), false);
    }


    public OWLMinCardinality createOWLMinCardinality(RDFProperty property, int value) {
        OWLMinCardinality minCardiRestriction = createOWLMinCardinality();
        minCardiRestriction.setOnProperty(property);
        minCardiRestriction.setCardinality(value);
        return minCardiRestriction;
    }


    public OWLMinCardinality createOWLMinCardinality(RDFProperty property, int value, RDFSClass qualifier) {
        OWLMinCardinality owlMinCardinality = createOWLMinCardinality(property, value);
        owlMinCardinality.setValuesFrom(qualifier);
        return owlMinCardinality;
    }


    public OWLNamedClass createOWLNamedClass(String name) {
        return createOWLNamedClass(name, loadDefaults);
    }


    public OWLNamedClass createOWLNamedClass(String name, boolean loadDefaults) {
        return (OWLNamedClass) createCls(getInternalFullName(name), getRootClses(), getOWLNamedClassClass(), loadDefaults);
    }


    public OWLNamedClass createOWLNamedClass(String name, OWLNamedClass metaCls) {
        return (OWLNamedClass) createCls(getInternalFullName(name), getRootClses(), metaCls, loadDefaults);
    }


    public OWLNamedClass createOWLNamedSubclass(String name, OWLNamedClass superclass) {
        OWLNamedClass result = createOWLNamedClass(name);
        if (!superclass.equals(getOWLThingClass())) {
            result.addSuperclass(superclass);
            result.removeSuperclass(getOWLThingClass());
        }
        return result;
    }


    public OWLObjectProperty createOWLObjectProperty(String name) {
        return createOWLObjectProperty(name, getOWLObjectPropertyClass());
    }


    public OWLObjectProperty createOWLObjectProperty(String name, OWLNamedClass metaCls) {
        if (name == null) {
            name = createUniqueNewFrameName(DEFAULT_OBJECT_PROPERTY_NAME);
        }
        OWLObjectProperty result = (OWLObjectProperty) createSlot(getInternalFullName(name), metaCls, loadDefaults);
    
        ((Slot) result).setAllowsMultipleValues(true);
        ((Slot) result).setValueType(ValueType.INSTANCE);
        return result;
    }


    public OWLObjectProperty createOWLObjectProperty(String name, Collection allowedClasses) {
        OWLObjectProperty slot = createOWLObjectProperty(name);
        slot.setUnionRangeClasses(allowedClasses);
        return slot;
    }


    public OWLOntology createOWLOntology(String uri) throws AlreadyImportedException {
        if (getDefaultOWLOntology() == null) {
            FactoryUtils.addOntologyToTripleStore(this, tripleStoreModel.getTopTripleStore(), uri);
        }
        else {
            OWLUtil.renameOntology(this, getDefaultOWLOntology(), uri);
        }
        return getDefaultOWLOntology();
    }


    /**
     * @deprecated
     */
    @Deprecated
	public OWLOntology createOWLOntology(String name, String uri) throws AlreadyImportedException {
        //String prefix = getNamespaceManager().getPrefix(uri);
        return createOWLOntology(uri);
    }


    public RDFSNamedClass createRDFSNamedClass(String name) {
        return createRDFSNamedClass(name, true);
    }


    public RDFSNamedClass createRDFSNamedClass(String name, boolean loadDefaults) {
        return (RDFSNamedClass) createCls(getInternalFullName(name), getRootClses(), getRDFSNamedClassClass(), loadDefaults);
    }


    public RDFSNamedClass createRDFSNamedClass(String name, Collection parents, RDFSClass rdfType) {
        return (RDFSNamedClass) createCls(getInternalFullName(name), parents, rdfType);
    }


    public RDFSNamedClass createRDFSNamedSubclass(String name, RDFSNamedClass superclass) {
        RDFSNamedClass result = createRDFSNamedClass(name);
        if (!superclass.equals(getOWLThingClass())) {
            result.addSuperclass(superclass);
            result.removeSuperclass(getOWLThingClass());
        }
        return result;
    }


    public RDFUntypedResource createRDFUntypedResource(String uri) {
        Instance instance = getRDFExternalResourceClass().createDirectInstance(uri);
        return (RDFUntypedResource) instance;
    }


    public RDFProperty createRDFProperty(String name) {
        if (name == null) {
            name = createUniqueNewFrameName(DEFAULT_PROPERTY_NAME);
        }
        RDFProperty property = (RDFProperty) createSlot(getInternalFullName(name), getRDFPropertyClass(), loadDefaults);
        ((Slot) property).setValueType(ValueType.ANY);
        ((Slot) property).setAllowsMultipleValues(true);
        return property;
    }


    public Triple createTriple(RDFResource subject, RDFProperty predicate, Object object) {
        return new DefaultTriple(subject, predicate, object);
    }


    public Set getAllImports() {
        Set imports = new HashSet();
        for (Iterator it = getOWLOntologies().iterator(); it.hasNext();) {
            Object curImport = it.next();
            OWLOntology ontology = (OWLOntology) curImport;
            for (Iterator imp = ontology.getImports().iterator(); imp.hasNext();) {
                Object impo = imp.next();
                if (impo instanceof RDFResource) {
                    imports.add(((RDFResource) impo).getURI());
                }
                else if (impo instanceof String) {
                    imports.add(impo);
                }
            }
        }
        return imports;
    }


    public RDFSNamedClass createSubclass(String name, RDFSNamedClass superclass) {
        return (RDFSNamedClass) createCls(getInternalFullName(name), Collections.singleton(superclass));
    }


    public RDFSNamedClass createSubclass(String name, Collection superclasses) {
        return (RDFSNamedClass) createCls(getInternalFullName(name), superclasses);
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLSomeValuesFrom) createCls(null, parents, getOWLSomeValuesFromClass(), false);
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom(RDFProperty property, RDFResource filler) {
        OWLSomeValuesFrom someRestriction = createOWLSomeValuesFrom();
        someRestriction.setOnProperty(property);
        someRestriction.setFiller(filler);
        return someRestriction;
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom(RDFProperty property, RDFSLiteral[] oneOfValues) {
        OWLSomeValuesFrom someRestriction = createOWLSomeValuesFrom();
        someRestriction.setOnProperty(property);
        OWLDataRange dataRange = createOWLDataRange(oneOfValues);
        someRestriction.setFiller(dataRange);
        return someRestriction;
    }


    public RDFProperty createSubproperty(String name, RDFProperty superProperty) {
        return (RDFProperty) createSlot(getInternalFullName(name), superProperty.getProtegeType(), Collections.singleton(superProperty), true);
    }


    @Override
	public boolean endTransaction() {
        return commitTransaction();
    }


    public QueryResults executeSPARQLQuery(String partialQueryText) throws Exception {
        String queryString = SPARQLQueryResults.createPrefixDeclarations(this) + partialQueryText;
        return SPARQLQueryResults.create(this, queryString);
    }


    public OWLUnionClass createOWLUnionClass() {
        Collection parents = CollectionUtilities.createCollection(getAnonymousRootCls());
        return (OWLUnionClass) createCls(null, parents, getOWLUnionClassClass(), false);
    }


    public OWLUnionClass createOWLUnionClass(Collection clses) {
        OWLUnionClass unionCls = createOWLUnionClass();
        for (Iterator it = clses.iterator(); it.hasNext();) {
        	Cls cls = (Cls) it.next();
        	if (cls instanceof RDFSClass) {
        		unionCls.addOperand((RDFSClass)cls);
        	} else {
        		Log.getLogger().warning(cls + " is not an RDFSClass. It will not be added to the OWLUnionClass.");
        	}            
        }
        return unionCls;
    }


    public RDFSLiteral createRDFSLiteral(Object value) {
        return DefaultRDFSLiteral.create(this, value);
    }


    public RDFSLiteral createRDFSLiteral(String lexicalValue, RDFSDatatype datatype) {
        return DefaultRDFSLiteral.create(this, lexicalValue, datatype);
    }


    public RDFSLiteral createRDFSLiteral(String value, String language) {
        return DefaultRDFSLiteral.create(this, value, language);
    }


    public Object createRDFSLiteralOrString(String value, String language) {
        if (language != null) {
            language = language.trim();
            if (language.length() > 0) {
                return createRDFSLiteral(value, language);
            }
        }
        return value;
    }


    /**
     * Deletes all Frames that are not system classes (including OWL system classes).
     */
    public static void deleteAllFrames(OWLModel owlModel) {
        KnowledgeBase kb = owlModel;
        for (Iterator it = kb.getInstances().iterator(); it.hasNext();) {
            Instance instance = (Instance) it.next();
            if (!(instance instanceof Cls) &&
                (instance.isEditable() || instance.isIncluded()) && !instance.isSystem()) {
                kb.deleteFrame(instance);
            }
        }

        Collection metaClasses = new ArrayList();
        for (Iterator it = owlModel.getUserDefinedOWLNamedClasses().iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if ((cls.isEditable() || cls.isIncluded()) && !cls.isSystem()) {
                if (cls.isMetaCls()) {
                    metaClasses.add(cls);
                }
            }
        }
        for (Iterator it = metaClasses.iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            for (Iterator ins = cls.getInstances().iterator(); ins.hasNext();) {
                Frame frame = (Frame) ins.next();
                kb.deleteFrame(frame);
            }
        }
        for (Iterator it = owlModel.getUserDefinedOWLNamedClasses().iterator(); it.hasNext();) {
            Cls cls = (Cls) it.next();
            if (cls.getDirectType() != null && (cls.isEditable() || cls.isIncluded()) && !cls.isSystem()) {
                kb.deleteFrame(cls);
            }
        }
    }


    @Override
	public void deleteCls(Cls cls) {
    	getHeadFrameStore().deleteCls(cls);
    	cls.markDeleted(true);    	
    }


    public Collection getOWLAllDifferents() {
        Cls metaCls = getOWLAllDifferentClass();
        return metaCls.getDirectInstances();
    }


    public Collection getOWLAnnotationProperties() {
        Collection result = new ArrayList();
        for (Iterator it = getSlots().iterator(); it.hasNext();) {
            Slot slot = (Slot) it.next();
            if (slot instanceof RDFProperty && ((RDFProperty) slot).isAnnotationProperty()) {
                result.add(slot);
            }
        }
        return result;
    }


    @Override
	public synchronized String getBrowserText(Instance instance) {
    	
    	if (!(instance instanceof RDFResource))
    		return super.getBrowserText(instance);

    	if (instance.isDeleted()) 
            return "<deleted>";
    	
    	if (instance instanceof OWLAnonymousClass)
    		return instance.getBrowserText();
    	
    	if (getProject() == null)
             return getName(instance);   
            
       	Cls directType = instance.getDirectType();

       	if (directType == null)
        	return getMissingTypeString(instance);
          
//      remove this after you implemented the Namespace
		return NamespaceUtil.getPrefixedName(this, instance.getName());
        /*
       	
       	BrowserSlotPattern slotPattern = null;
       	       	
       	try {
       		slotPattern = (OWLBrowserSlotPattern) getProject().getBrowserSlotPattern(directType);
       	} catch (ClassCastException e) {
       		try {
			slotPattern = OWLUI.fixBrowserSlotPattern(getProject(), directType);			
			//Log.getLogger().warning("Non-OWL browser slot for: " + directType + " ... Convert it to OWLBrowserSlotPattern");
       		} catch (Exception ex) {
       			Log.getLogger().log(Level.WARNING, "Error at getting browser slot pattern for " + instance, ex);
       		}
		} catch(Exception e) {
       		slotPattern = getProject().getBrowserSlotPattern(directType);
       		Log.getLogger().log(Level.WARNING, "Unknown error at getting the browser slot for: " + directType, e);
       	}
                        
         if (slotPattern == null)
        	 return getDisplaySlotNotSetString(instance);
         
         String value = slotPattern.getBrowserText(instance);
         if (value == null) {
             value = getDisplaySlotPatternValueNotSetString(instance, slotPattern);
         }
         
         return value;
         */
	}

    
 
    public Collection getChangedInferredClasses() {
        return getClsesWithClassificationStatus(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED);
    }

    /**
     * Gets the most specific common named superclasses of a given collection of named classes.
     *
     * @param classes the RDFSNamedClasses to get the superclasses of (at least one)
     * @return the most specific common superclasses of all classes, e.g. owl:Thing
     */
    public Set<RDFSNamedClass> getCommonSuperclasses(Collection<RDFSNamedClass> classes) {
        Set<RDFSNamedClass> commonSupers = new HashSet<RDFSNamedClass>();
        boolean firstTime = true;
        for (RDFSNamedClass inputClass : classes) {
            if (firstTime) {
                Collection firstInputClassSupers = inputClass.getSuperclasses(true);
                for (Object aFirstSuper  : firstInputClassSupers) {
                    if (aFirstSuper instanceof RDFSNamedClass) {
                        commonSupers.add((RDFSNamedClass) aFirstSuper);
                    }
                }
                firstTime = false;
            }
            else {
                commonSupers.retainAll(inputClass.getSuperclasses(true));
            }
        }

        // the mysterious if check is to ensure no problems in the case getCommonSuperclasses({ A }) where
        //   A \subseteq B \subseteq C \subseteq B
        // We don't want to remove both B and C but either one will do for keeping.
        List<RDFSNamedClass> commonSupersCopy = new ArrayList<RDFSNamedClass>(commonSupers);
        for (RDFSNamedClass namedClass : commonSupersCopy) {
            if (commonSupers.contains(namedClass)) {
                Collection superClasses = namedClass.getSuperclasses(true);
                if (superClasses.contains(namedClass)) {
                    superClasses = new HashSet(superClasses);
                    superClasses.remove(namedClass);
                }
                commonSupers.removeAll(superClasses);
            }
        }
        return commonSupers;
    }
    
    /**
     * Chooses a most specific common named superclass of a given collection of named classes.
     *
     * @param classes the RDFSNamedClasses to get the superclass of (at least one)
     * @return the most specific common superclass of all classes, e.g. owl:Thing
     */
    public RDFSNamedClass getCommonSuperclass(Collection<RDFSNamedClass> classes) {
        return getCommonSuperclasses(classes).iterator().next();
    }


    private Collection getClsesWithClassificationStatus(int status) {
        final Slot slot = getProtegeClassificationStatusProperty();
        Collection matches = getFramesWithValue(slot, null, false,
                                                new Integer(status));
        List result = new ArrayList();
        for (Iterator it = matches.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (frame instanceof OWLNamedClass) {
                result.add(frame);
            }
        }
        return result;
    }


    public OWLDatatypeProperty getOWLDatatypeProperty(String name) {    	
        return (OWLDatatypeProperty) getSlot(getInternalFullName(name));
    }

    public String getDefaultLanguage() {        
        RDFProperty metaSlot = getRDFProperty(ProtegeNames.getDefaultLanguageSlotName());
        if (metaSlot != null) {
            OWLOntology oi = getDefaultOWLOntology();
            if (oi != null) {
                Object value = oi.getPropertyValue(metaSlot);                
                if (value instanceof String) {
                	String stringValue = (String) value;
                	if (stringValue != null && stringValue.length() > 0) {
                		return stringValue;
                	}
                }
            }
        }
        return null;
    }
   
    public void resetOntologyCache() {
        topOWLOntology = null;
    }


    public OWLOntology getDefaultOWLOntology() {
        if (topOWLOntology == null) {
            topOWLOntology= getTripleStoreModel().getTopTripleStore().getOWLOntology();
        }
        return topOWLOntology;
    }


    public Collection getDomainlessProperties() {
        return getRootCls().getDirectTemplateSlots();
    }


    public OWLProject getOWLProject() {
        return owlProject;
    }


    public RepositoryManager getRepositoryManager() {
        if (repositoryManager == null) {
            repositoryManager = new RepositoryManager(this);
            RepositoryFileManager man = new RepositoryFileManager(this);
            man.loadGlobalRepositories();
        }
        return repositoryManager;
    }


    /**
     * @deprecated
     */
    @Deprecated
	public RDFExternalResource getRDFExternalResource(String uri) {
    	return (RDFExternalResource) getRDFResource(uri);      
    }



    public Collection getInconsistentClasses() {
        return getOWLNothing().getInferredSubclasses();
    }


    public com.hp.hpl.jena.rdf.model.Model getJenaModel() {
        if (jenaModel == null) {
            jenaModel = JenaModelFactory.createModel(this);
        }
        return jenaModel;
    }



    public OWLClassParser getOWLClassParser() {
        return getOWLClassDisplay().getParser();
    }


    public OWLClassDisplay getOWLClassDisplay() {
        return owlClassRenderer;
    }


    public int getRDFResourceCount() {
        return getFrameCount() - 54; //63;
    }


    public Collection getRDFResources() {
        Collection frames = getFrames();
        return getRDFResources(this, frames);
    }


    public Collection getRDFResourcesWithPropertyValue(RDFProperty property, Object value) {
        Collection frames = getFramesWithValue(property, null, false, value);
        return getRDFResources(this, frames);
    }


    public static Collection<RDFResource> getRDFResources(KnowledgeBase kb, Collection<? extends Frame> frames) {
        ArrayList<RDFResource> result = new ArrayList<RDFResource>();
        for (Iterator<? extends Frame> it = frames.iterator(); it.hasNext();) {
            Frame frame = it.next();
            if (frame instanceof RDFResource) {
                result.add((RDFResource) frame);
            }
        }
        removeProtegeSystemResources(kb, result);
        return result;
    }


    public RDFUntypedResource getRDFUntypedResource(String uri, boolean createOnDemand) {
        RDFUntypedResource r = (RDFUntypedResource) getFrame(uri);
        if (createOnDemand && r == null) {
            r = createRDFUntypedResource(uri);
        }
        return r;
    }


    public Collection<RDFResource> getResourceNameMatches(String nameExpression, int maxMatches) {
        Collection frames = getFrameNameMatches(nameExpression, maxMatches);
        return getRDFResources(this, frames);
    }


    public OWLNamedClass getOWLNamedClass(String name) {
        return (OWLNamedClass) getCls(getInternalFullName(name));
    }


    public Collection getMatchingResources(RDFProperty property, String matchString, int maxMatches) {
        Collection frames = getMatchingFrames(property, null, false, matchString, maxMatches);
        return getRDFResources(this, frames);
    }


    private int anonCount = 1;
   
    public String getNextAnonymousResourceName() {
        for (; ;) {
            String name = ANONYMOUS_BASE + anonCount;
            if (getFrame(name) == null) {
                return name;
            }
            anonCount++;
        }
    }


    public Collection getRDFProperties() {
        return getRDFResources(this, getSlots());
    }


    public RDFResource getRDFResource(String name) {
        return (RDFResource) getFrame(getInternalFullName(name));
    }


    public RDFResource getRDFResourceAs(String name, Class javaInterface) {
        RDFResource resource = getRDFResource(name);
        if (resource != null) {
            return resource.as(javaInterface);
        }
        else {
            return null;
        }
    }


    public OWLIndividual getOWLIndividual(String name) {
        return (OWLIndividual) getFrame(getInternalFullName(name));
    }


    public Collection getOWLRestrictionsOnProperty(RDFProperty property) {
        return getFramesWithValue(getOWLOnPropertyProperty(), null, false, property);
    }


    public Collection getSearchSynonymProperties() {
        Collection results = new HashSet();
        String syns = getOWLProject().getSettingsMap().getString(SEARCH_SYNONYMS_KEY);
        if (syns != null) {
            String[] ss = syns.split(SEARCH_SYNONYMS_SEPARATOR);
            for (int i = 0; i < ss.length; i++) {
                String s = ss[i];
                Frame frame = getFrame(s);
                if (frame instanceof Slot) {
                    results.add(frame);
                }
            }
        }
        return results;
    }


    public OWLObjectProperty getOWLObjectProperty(String name) {
        return (OWLObjectProperty) getSlot(getInternalFullName(name));
    }


    public Collection getOWLOntologies() {
        return getOWLOntologyClass().getInstances();
    }


    public OWLOntology getOWLOntologyByURI(String uri) {
        OWLOntology ont = null;
        try {
            URI properURI = new URI(uri);
            ont = (OWLOntology) getOWLOntologyByURI(properURI);
        }
        catch (URISyntaxException e) {
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
        return ont;
    }


    public RDFResource getOWLOntologyByURI(URI uri) {
        return getRDFResource(uri.toString());
    }


    public Collection getOWLOntologyProperties() {
        return Arrays.asList(new Slot[]{
                getOWLBackwardCompatibleWithProperty(),
                getOWLIncompatibleWithProperty(),
                getOWLPriorVersionProperty()
        });
    }


    /**
     * @deprecated use getRDFSClasses instead
     */
    @Deprecated
    public Collection getOWLClasses() {
        return getRDFSClasses();
    }


    public OWLFrameStore getOWLFrameStore() {
        return getFrameStoreManager().getOWLFrameStore();
    }


    public Collection getOWLIndividuals() {
        return getOWLIndividuals(false);
    }


    public Collection getOWLIndividuals(boolean onlyVisibleClasses) {
        Collection result = getRDFIndividuals(listOWLNamedClasses(), onlyVisibleClasses);
        Iterator it = listOWLAnonymousClasses();
        while (it.hasNext()) {
            OWLAnonymousClass c = (OWLAnonymousClass) it.next();
            Collection instances = c.getInstances(false);
            for (Iterator is = instances.iterator(); is.hasNext();) {
                Instance instance = (Instance) is.next();
                result.add(instance);
            }
        }
        return result;
    }


    public OWLProperty getOWLProperty(String name) {
        return (OWLProperty) getSlot(getInternalFullName(name));
    }


    public Collection getOWLSystemResources() {
        Collection result = new ArrayList(Arrays.asList(getOWLSystemFramesArray()));
        result.addAll(getRDFSDatatypeClass().getDirectInstances());
        return result;
    }


    /**
     * Gets the array of OWL System frames.
     * <p/>
     * An important side effect of this method is that it initializes
     * several of the variables used by the AbstractOWLModel.
     */

    protected Frame[] getOWLSystemFramesArray() {
        Collection<Frame> frameSet = getSystemFrames().getFrames();
        Set<Frame> owlFrameSet = new HashSet<Frame>();
        for (Frame frame : frameSet) {
            if (frame instanceof RDFResource) {
                owlFrameSet.add(frame);
            }
        }
        return owlFrameSet.toArray(new Frame[owlFrameSet.size()]);
    }



    public RDFProperty getProtegeAllowedParentProperty() {
        return getRDFProperty(ProtegeNames.PROTEGE_OWL_NAMESPACE + ProtegeNames.ALLOWED_PARENT);
    }


    public ValueType getOWLValueType(String uri) {
        return XMLSchemaDatatypes.getValueType(uri);
    }


    public RDFIndividual getRDFIndividual(String name) {
        return (RDFIndividual) getFrame(getInternalFullName(name));
    }


    public Collection getRDFIndividuals() {
        return getRDFIndividuals(false);
    }


    public Collection getRDFIndividuals(boolean onlyVisibleClasses) {
        return getRDFIndividuals(listRDFSNamedClasses(), onlyVisibleClasses);
    }


    private Collection getRDFIndividuals(Iterator it, boolean onlyVisibleClasses) {
        Collection result = new HashSet();
        while (it.hasNext()) {
            RDFSNamedClass c = (RDFSNamedClass) it.next();
            if (c.isVisible() || !onlyVisibleClasses) {
                Collection instances = c.getInstances(false);
                for (Iterator is = instances.iterator(); is.hasNext();) {
                    Instance instance = (Instance) is.next();
                    if (instance instanceof RDFIndividual &&
                        !(instance instanceof OWLOntology) &&
                        !(instance instanceof RDFList) &&
                        !(instance instanceof OWLAllDifferent)) {
                        result.add(instance);
                    }
                }
            }
        }
        return result;
    }


    public RDFSNamedClass getRDFSNamedClass(String name) {
        return (RDFSNamedClass) getCls(getInternalFullName(name));
    }


    public RDFProperty getRDFProperty(String name) {
        return (RDFProperty) getSlot(getInternalFullName(name));
    }


    public OWLDatatypeProperty getProtegeReadOnlyProperty() {   
    	return (OWLDatatypeProperty) getSlot(ProtegeNames.getReadOnlySlotName());   
    }


    public RDFSDatatype getRDFSDatatypeByName(String name) {    	
        return getRDFSDatatypeByURI(getInternalFullName(name));
    }

    public RDFSDatatype getRDFSDatatypeByURI(String uri) {
    	 return uri == null ? null : (RDFSDatatype) getFrame(uri);
    }


    public RDFSDatatype getRDFSDatatypeOfValue(Object valueOrRDFSLiteral) {
        if (valueOrRDFSLiteral instanceof RDFSLiteral) {
            return ((RDFSLiteral) valueOrRDFSLiteral).getDatatype();
        }
        else {
            return DefaultRDFSLiteral.create(this, valueOrRDFSLiteral).getDatatype();
        }
    }


    public Collection<RDFResource> getRDFSDatatypes() {
        List<RDFResource> results = new ArrayList<RDFResource>();
        Iterator it = getRDFSDatatypeClass().getDirectInstances().iterator();
        while (it.hasNext()) {
            RDFResource datatype = (RDFResource) it.next();
            if (!datatype.isAnonymous()) {
                results.add(datatype);
            }
        }
        return results;
    }

    public RDFSNamedClass[] getOWLRestrictionMetaclasses() {
        return new RDFSNamedClass[]{// Order may be used for sorting and should be preserved
                getOWLAllValuesFromClass(),
                getOWLSomeValuesFromClass(),
                getOWLHasValueClass(),
                getOWLCardinalityClass(),
                getOWLMinCardinalityClass(),
                getOWLMaxCardinalityClass()
        };
    }


    public RDFProperty[] getSystemAnnotationProperties() {
        return new RDFProperty[]{
                getRDFSSeeAlsoProperty(),
                getRDFSIsDefinedByProperty(),
                getRDFSLabelProperty(),
                getOWLVersionInfoProperty(),
                getOWLBackwardCompatibleWithProperty(),
                getOWLIncompatibleWithProperty(),
                getOWLPriorVersionProperty(),
                getRDFSCommentProperty()
        };
    }


    public String getTodoAnnotationPrefix() {
        RDFProperty metaSlot = getRDFProperty(ProtegeNames.getTodoPrefixSlotName());
        if (metaSlot != null) {
            OWLOntology oi = getDefaultOWLOntology();
            if (oi != null) {
                String value = (String) oi.getPropertyValue(metaSlot);
                if (value != null) {
                    return value;
                }
            }
        }
        return DEFAULT_TODO_PREFIX;
    }


    public OWLDatatypeProperty getTodoAnnotationProperty() {
        RDFProperty metaSlot = getRDFProperty(ProtegeNames.getTodoPropertySlotName());
        if (metaSlot != null) {
            OWLOntology oi = getDefaultOWLOntology();
            if (oi != null) {
                Object slot = oi.getPropertyValue(metaSlot);
                if (slot instanceof OWLDatatypeProperty) {
                    return (OWLDatatypeProperty) slot;
                }
            }
        }
        return (OWLDatatypeProperty) getSlot(OWLNames.Slot.VERSION_INFO);
    }
    
    public TripleStoreModel getTripleStoreModel() {
        if (tripleStoreModel == null) {
            tripleStoreModel = new TripleStoreModelImpl(this);
        }
        return tripleStoreModel;
    }


    public String[] getUsedLanguages() {      
        RDFProperty metaSlot = getRDFProperty(ProtegeNames.getUsedLanguagesSlotName());
        if (metaSlot != null) {
            OWLOntology oi = getDefaultOWLOntology();
            if (oi != null) {
                Collection values = oi.getPropertyValues(metaSlot);
                if (values.size() > 0) {
                    return (String[]) values.toArray(new String[0]);
                }
            }
        }      
        return DEFAULT_USED_LANGUAGES;
    }


    public Collection getUserDefinedOWLNamedClasses() {
        return getUserDefinedInstances(getOWLNamedClassClass());
    }


    public Collection getUserDefinedRDFIndividuals(boolean onlyVisibleClasses) {
        List result = new ArrayList();
        Iterator it = getRDFIndividuals(onlyVisibleClasses).iterator();
        while (it.hasNext()) {
            RDFResource resource = (RDFResource) it.next();
            if (!resource.isSystem()) {
                result.add(resource);
            }
        }
        return result;
    }


    private Collection getUserDefinedInstances(Cls cls) {
        Collection<Instance> instances = cls.getInstances();
        return getUserDefinedInstances(instances);
    }


    private Collection getUserDefinedInstances(Collection instances) {
        List results = new ArrayList();
        for (Iterator it = instances.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (!frame.isSystem()) {
                results.add(frame);
            }
        }
        return results;
    }


    public Collection getUserDefinedOWLProperties() {
        List instances = new ArrayList();
        instances.addAll(getOWLDatatypePropertyClass().getInstances());
        instances.addAll(getOWLObjectPropertyClass().getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedOWLObjectProperties() {
        List instances = new ArrayList();
        instances.addAll(getOWLObjectPropertyClass().getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedOWLDatatypeProperties() {
        List instances = new ArrayList();
        instances.addAll(getOWLDatatypePropertyClass().getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedRDFProperties() {
        return getUserDefinedInstances(getRDFPropertyClass());
    }


    public Collection getUserDefinedRDFSNamedClasses() {
        return getUserDefinedInstances(getRDFSNamedClassClass());
    }


    public String getValueTypeURI(ValueType valueType) {
        return XMLSchemaDatatypes.getValueTypeURI(valueType);
    }


    public Collection getVisibleUserDefinedOWLProperties() {
        List instances = new ArrayList();
        instances.addAll(getOWLDatatypePropertyClass().getInstances());
        instances.addAll(getOWLObjectPropertyClass().getInstances());
        return getVisibleUserDefinedInstances(instances);
    }


    private Collection getVisibleUserDefinedInstances(Cls cls) {
        Collection instances = cls.getInstances();
        return getVisibleUserDefinedInstances(instances);
    }


    private Collection getVisibleUserDefinedInstances(Collection instances) {
        List results = new ArrayList();
        for (Iterator it = instances.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (!frame.isSystem() && frame.isVisible()) {
                results.add(frame);
            }
        }
        return results;
    }


    public Collection getVisibleUserDefinedRDFProperties() {
        // TODO: Maybe remove protege:slots
        return getVisibleUserDefinedInstances(getRDFPropertyClass());
    }


    /**
     * @deprecated
     */
    @Deprecated
	public boolean isAnonymousResource(RDFResource resource) {
        return resource.isAnonymous();
    }


    public boolean isAnonymousResourceName(String name) {
        return name.charAt(0) == '@';    // name.startsWith(ANONYMOUS_BASE); but faster
    }


    public boolean isDefaultAnonymousType(Cls type) {
        return defaultAnonymousTypes.contains(type);
    }


    public boolean isDefaultAnonymousType(Collection types) {
        for (Iterator it = types.iterator(); it.hasNext();) {
            Cls type = (Cls) it.next();
            if (isDefaultAnonymousType(type)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @deprecated
     */
    @Deprecated
	public boolean isOWLSystemFrame(Frame frame) {
        return getOWLSystemResources().contains(frame);
    }


    /*
     * This fix is incomplete.  It should be completed before being merged back into the
     * head.  The intention is that we do not continually as the knowledge base whether there
     * is a frame with a particular name.  This can be expensive in the case that there is no
     * such frame because caching the result is difficult.
     * 
     * The problem with this fix is that the stored result is not updated when the protege 
     * ontology is imported.
     */
     /*
      * TT: The fix should be easy, but it needs to be tested. We can keep the flag from below 
      * and when we import in the ImportHelper, we set this flag, if necessary.
      * We should do this fix soon.
      */
    		
    // private Boolean protegeMetaOntologyImported = null;
    
   
    public boolean isProtegeMetaOntologyImported() {
    	//TT: this should be reimplemented in a more efficient way
    	//Should use a listener on the ontology imports and a class field
    	
    	//TT: Nice implementation, but I don't know how efficient it is.    	
    	/*
        for (Iterator iter = getAllImports().iterator(); iter.hasNext();) {
			String uriImport = (String) iter.next();
			if (uriImport.equals(ProtegeNames.FILE))	
				return true;
		}
    	return false;
    	*/
        String slotName = ProtegeNames.getSubclassesDisjointSlotName();
        return getSlot(slotName) != null;
    }


    public boolean isTrueInstance(Instance instance) {
        return !(instance instanceof Slot) &&
               !(instance instanceof Cls) &&
               !(instance instanceof Facet) &&
               !(instance instanceof OWLAllDifferent) &&
               !(instance instanceof RDFList) &&
               !(instance instanceof OWLOntology);
    }


    public Iterator listOWLAnonymousClasses() {
        Collection result = new ArrayList();
        for (Iterator it = getCls(OWLNames.Cls.ANONYMOUS_CLASS).getInstances().iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof OWLAnonymousClass) {
                result.add(o);
            }
        }
        return result.iterator();
    }


    public Iterator listOWLNamedClasses() {
        Collection result = new ArrayList();
        for (Iterator it = getOWLNamedClassClass().getInstances(true).iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof OWLNamedClass) {
                result.add(o);
            }
        }
        return result.iterator();
    }


    public Iterator listRDFProperties() {
        return getRDFProperties().iterator();
    }


    public Iterator listRDFSNamedClasses() {
        Collection result = new ArrayList();
        for (Iterator it = getRDFSNamedClassClass().getInstances(true).iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFSNamedClass) {
                result.add(o);
            }
        }
        return result.iterator();
    }


    public Iterator listReferences(Object object, int maxResults) {
        if (object instanceof DefaultRDFSLiteral) {
            object = ((DefaultRDFSLiteral) object).getRawValue();
        }
        Iterator refs = getReferences(object, maxResults).iterator();
        List results = new ArrayList();
        while (refs.hasNext()) {
            Reference ref = (Reference) refs.next();
            if (ref.getFrame() instanceof RDFResource && ref.getSlot() instanceof RDFProperty) {
                results.add(new DefaultTuple((RDFResource) ref.getFrame(), (RDFProperty) ref.getSlot()));
            }
        }
        return results.iterator();
    }


    public Iterator listSubjects(RDFProperty property) {
        return getHeadFrameStore().getFramesWithAnyDirectOwnSlotValue(property).iterator();
    }


    @Override
	public synchronized Instance setDirectType(Instance instance, Cls type) {
        if (instance instanceof OWLProperty && type != null) {
            deleteRestrictionsDependingOnPropertyType((OWLProperty) instance, type);
        }
        if (instance instanceof OWLNamedClass && type.equals(getRDFSNamedClassClass())) {
            deleteAnonymousSuperclasses((OWLNamedClass) instance);
        }
        return super.setDirectType(instance, type);
    }


    private void deleteAnonymousSuperclasses(OWLNamedClass namedOWLClass) {
        Collection oldNamedParents = namedOWLClass.getNamedSuperclasses();
        Iterator it = new ArrayList(namedOWLClass.getSuperclasses(false)).iterator();
        while (it.hasNext()) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof OWLAnonymousClass) {
                superCls.delete();
            }
        }
        for (Iterator sit = oldNamedParents.iterator(); sit.hasNext();) {
            RDFSClass superCls = (RDFSClass) sit.next();
            if (!namedOWLClass.isSubclassOf(superCls)) {
                namedOWLClass.addSuperclass(superCls);
            }
        }
    }


    private void deleteRestrictionsDependingOnPropertyType(OWLProperty owlProperty, Cls type) {
        Cls metaClass = null;
        if (owlProperty instanceof OWLDatatypeProperty) {
            metaClass = getOWLDatatypePropertyClass();
            //newType = ValueType.INSTANCE;
        }
        else {
            metaClass = getOWLObjectPropertyClass();
            //newType = ValueType.ANY;
        }
        if (!type.hasSuperclass(metaClass)) {
            getOWLFrameStore().deleteQuantifierRestrictions(owlProperty);
            //if (!type.equals(getRDFPropertyClass())) {
            // owlProperty.setValueType(newType);
            //}
        }
    }


    public void setLoadDefaults(boolean value) {
        loadDefaults = value;
    }


    @Override
    public void setProject(Project project) {
        super.setProject(project);
        setOWLProject(new DefaultOWLProject(project));

        project.setPrettyPrintSlotWidgetLabels(false);

        Slot nameSlot = getSlot(Model.Slot.NAME);                       
  
        getRootCls().setDirectBrowserSlotPattern(new OWLBrowserSlotPattern(nameSlot));

        project.setDefaultClsWidgetClassName(OWLFormWidget.class.getName());

        project.setWidgetMapper(new OWLWidgetMapper(this));

        getProtegeClassificationStatusProperty().setVisible(false);
        getProtegeInferredSuperclassesProperty().setVisible(false);
        getProtegeInferredSubclassesProperty().setVisible(false);
        getOWLOntologyClass().setVisible(false);
    }
    

    public void setSearchSynonymProperties(Collection slots) {
        if (slots.isEmpty()) {
            getOWLProject().getSettingsMap().setString(SEARCH_SYNONYMS_KEY, null);
        }
        else {
            String str = "";
            for (Iterator it = slots.iterator(); it.hasNext();) {
                Slot slot = (Slot) it.next();
                str += slot.getName();
                if (it.hasNext()) {
                    str += SEARCH_SYNONYMS_SEPARATOR;
                }
            }
            getOWLProject().getSettingsMap().setString(SEARCH_SYNONYMS_KEY, str);
        }
    }


    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    
    public RDFResource getRDFResourceByBrowserText(String text) {
        for (Iterator it = getInstances().iterator(); it.hasNext();) {
            Instance instance = (Instance) it.next();
            if (instance instanceof RDFResource && instance.getBrowserText().equals(text)) {
                return (RDFResource) getInstance(instance.getName());
            }
        }
        return null;
    }


    public RDFResource getRDFResourceByNameOrBrowserText(String text) {
        Instance result = getInstance(getInternalFullName(text));
        if (result instanceof RDFResource) {
            return (RDFResource) result;
        }
        result = getRDFResourceByBrowserText(text);
        if (result instanceof RDFResource) {
            return (RDFResource) result;
        }
        else {
            return null;
        }
    }


    //TODO: Where is this number coming from?
    public int getRDFSClassCount() {
        return getRDFSNamedClassClass().getInstanceCount() - 4;
    }


    public Collection getRDFSClasses() {
        Collection classes = new ArrayList(getOWLClassMetaCls().getInstances());
        removeProtegeSystemResources(this, classes);
        return classes;
    }


    public RDFSDatatypeFactory getRDFSDatatypeFactory() {
        return rdfsDatatypeFactory;
    }


    private static void removeProtegeSystemResources(KnowledgeBase kb, Collection frames) {
        if (frames.size() > 0) {
            final Cls dbrClass = kb.getCls(Model.Cls.DIRECTED_BINARY_RELATION);            
            frames.remove(dbrClass);
            
            frames.remove(((OWLModel) kb).getSystemFrames().getOwlOntologyPointerClass());
            
            frames.remove(kb.getSystemFrames().getPalConstraintCls());
            frames.remove(kb.getCls(OWLNames.Cls.ANONYMOUS_ROOT));
            frames.remove(kb.getCls(OWLNames.Cls.OWL_CLASS));
            frames.remove(kb.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES));            
            frames.remove(kb.getSlot(OWLNames.Slot.RESOURCE_URI));
            frames.remove(kb.getSlot(Model.Slot.CONSTRAINTS));
            frames.remove(kb.getSlot(OWLNames.Slot.OWL_ONTOLOGY_POINTER_PROPERTY));
        }
    }


    public Collection getResourcesWithPrefix(String prefix) {
        Collection result = new ArrayList();
        
        String namespace = getNamespaceManager().getNamespaceForPrefix(prefix);
        
        if (namespace == null) {
        	return result;
        }
                
        result = getMatchingFrames(getNameSlot(), null, false, namespace + "*", -1);          
      
        return getRDFResources(this, result);
    }


    public List getVisibleResources(Iterator iterator) {
        List result = new ArrayList();
        while (iterator.hasNext()) {
            RDFResource resource = (RDFResource) iterator.next();
            if (resource.isVisible()) {
                result.add(resource);
            }
        }
        return result;
    }


    @Override
	public synchronized boolean isSlotMetaCls(Cls cls) {
        return getRDFPropertyClass().equals(cls) || hasSuperclass(cls, getRootSlotMetaCls());
    }
 

    public void resetJenaModel() {
        jenaModel = null;
    }


    public Collection getPropertyValueLiterals(RDFResource resource, RDFProperty property) {
        final List values = new ArrayList(OWLUtil.getPropertyValues(resource, property, false));
        if (!values.isEmpty()) {
            return getValueLiterals(values);
        }
        else {
            return values;
        }
    }

    
    public List getValueLiterals(List values) {
        List result = new ArrayList();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object o = it.next();
            if (o instanceof RDFSLiteral) {
                result.add(o);
            }
            else {
                result.add(createRDFSLiteral(o));
            }
        }
        return result;
    }


    
    @Override
	public void setDirectOwnSlotValues(Frame frame, Slot slot, Collection values) {

        final int valueCount = values.size();
        if (valueCount > 0) {
            for (Iterator it = values.iterator(); it.hasNext();) {
                Object o = it.next();
                if (o instanceof RDFSLiteral) {
                    values = convertRDFSLiteralsToInternalFormat(values);
                    break;
                }
            }
        }
        super.setDirectOwnSlotValues(frame, slot, values);
    }
    
    private List convertRDFSLiteralsToInternalFormat(Collection values) {
      final List result = new LinkedList();
      for (Iterator it = values.iterator(); it.hasNext();) {
          final Object o = it.next();
          if (o instanceof RDFSLiteral) {
              final DefaultRDFSLiteral literal = (DefaultRDFSLiteral) o;
              final Object optimized = literal.getPlainValue();
              if (optimized != null) {
                  result.add(optimized);
              }
              else {
                  result.add(literal.getRawValue());
              }
          }
          else {
              result.add(o);
          }
      }
      return result;
    }

    
    /*
     * OWLTest methods
     */

    public void addOWLTest(OWLTest test) {
        getOWLTestsSettingsMap().remove(test.getClass().getName());
    }


    public OWLTest[] getOWLTests() {
        Class[] classes = OWLTestLibrary.getOWLTestClasses();
        Collection tests = new ArrayList();
        Set disabledTestClasses = new HashSet();
        Iterator it = getOWLTestsSettingsMap().listKeys();
        while (it.hasNext()) {
            disabledTestClasses.add(it.next());
        }
        for (int i = 0; i < classes.length; i++) {
            Class c = classes[i];
            if (!disabledTestClasses.contains(c.getName())) {
                tests.add(OWLTestLibrary.getOWLTest(c));
            }
        }
        return (OWLTest[]) tests.toArray(new OWLTest[0]);
    }


    private SettingsMap getOWLTestGroupsSettingsMap() {
        return getOWLProject().getSettingsMap().getSettingsMap(TESTGROUP_LIST_NAME);
    }


    private SettingsMap getOWLTestsSettingsMap() {
        return getOWLProject().getSettingsMap().getSettingsMap(TEST_LIST_NAME);
    }


    public boolean isAutoRepairEnabled() {
        return !Boolean.FALSE.equals(getOWLProject().getSettingsMap().getBoolean(AUTO_REPAIR_ENABLED));
    }


    public boolean isOWLTestGroupEnabled(String groupName) {
        return getOWLTestGroupsSettingsMap().getBoolean(groupName) == null;
    }


    public void removeOWLTest(OWLTest test) {
        final String key = test.getClass().getName();
        getOWLTestsSettingsMap().setBoolean(key, true);
    }


    public void setAutoRepairEnabled(boolean value) {
        getOWLProject().getSettingsMap().setBoolean(AUTO_REPAIR_ENABLED, Boolean.valueOf(value));
    }


    public void setOWLTestGroupEnabled(String groupName, boolean value) {
        if (value) {
            getOWLTestGroupsSettingsMap().remove(groupName);
            Class[] classes = OWLTestLibrary.getOWLTestClasses();
            for (int i = 0; i < classes.length; i++) {
                Class c = classes[i];
                OWLTest test = OWLTestLibrary.getOWLTest(c);
                if (groupName.equals(test.getGroup())) {
                    addOWLTest(test);
                }
            }
        }
        else {
            getOWLTestGroupsSettingsMap().setBoolean(groupName, true);
            OWLTest[] tests = getOWLTests();
            for (int i = 0; i < tests.length; i++) {
                OWLTest test = tests[i];
                if (groupName.equals(test.getGroup())) {
                    removeOWLTest(test);
                }
            }
        }
    }

    /*
     * End OWLTest methods
     */
    
    

    
    /*
     * Client and Server related methods
     */
    
    @Override
    public ServerCacheStateMachine getCacheMachine() {
        return new OwlStateMachine(getHeadFrameStore(), this);
    }
    
    @Override
    public void setCacheMachine(ServerCacheStateMachine machine) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    protected void adjustForClient() {
        super.adjustForClient();
        OWLFrameStoreManager fsm = getFrameStoreManager();
        fsm.setOwlFrameStoresEnabled(false);
        tripleStoreModel=new ClientTripleStoreModel(this);
    }

    
    /*
     * Utilities for naming methods
     */

    
    public boolean isValidResourceName(String name, RDFResource resource) {
        return isValidFrameName(name, resource);
    }
    
    public String createUniqueNewFrameName(String baseName) {
    	return createNewResourceName(baseName);
        //return getUniqueFrameName(getName() + "_" + baseName);
    }


    public String getResourceNameForURI(String uri) {
    	return uri;
    }
    
    public boolean isValidOWLFrameName(String name) {
        return isValidOWLFrameName(getNamespaceManager(), name);
    }

    public static boolean isValidOWLFrameName(NamespaceManager nsm, String name) {
    	//TODO TT - add maybe extra checks; for now it should be fine
    	return URIUtilities.isValidURI(name);
    }
    
    public String getValidNamespaceFrameName(String suggestedName) {
        return getValidOWLFrameName(this, suggestedName);
    }

    //TODO: change this
    public static String getValidOWLFrameName(AbstractOWLModel kb, String suggestedName) {
        Assert.assertNotNull(suggestedName);
        String name = suggestedName;
        if (name.startsWith(":")) {
            name = "_" + name.substring(1);
        }
        int first = name.indexOf(':') + 1;
        for (int i = 0; i < name.length(); i++) {
            if (i != first - 1) {
                char c = name.charAt(i);
                if (!Character.isJavaIdentifierPart(c) && VALID_SYMBOLS.indexOf(c) < 0) {
                    name = name.replace(c, '_');
                }
            }
        }
        if (name.length() == first) {
            name = "_" + name;
        }
        else if (!Character.isJavaIdentifierStart(name.charAt(first))) {
            int x = first == 0 ? first : first - 1;
            name = name.substring(0, x) + "_" + name.substring(first);
        }
        if (!name.equals(suggestedName)) {
            if (kb != null && kb.getFrame(name) != null) {
                suggestedName = kb.getUniqueFrameName(name);
            }
            else {
                suggestedName = name;
            }
        }
        return suggestedName;
    }
    

    public String getLocalNameForURI(String uri) {
    	return NamespaceUtil.getLocalName(uri);
    }


    public NamespaceManager getNamespaceManager() {
    	//TODO change this with an aggregation of prefixes
        return getTripleStoreModel().getActiveTripleStore().getNamespaceManager();
    }

    public String getNamespaceForURI(String uri) {
    	return NamespaceUtil.getNameSpace(uri);
    }


    public String getPrefixForResourceName(String name) {
    	return NamespaceUtil.getPrefixForResourceName(this, getInternalFullName(name));
    }


    private int lastGen = 1;

    public String getUniqueFrameName(String name) {
        String baseName = name;
        int i = lastGen;
        do {
            name = baseName + "_" + i++;
        }
        while (getFrame(name) != null);
        lastGen = i;
        return name;
    }
 

    public String getURIForResourceName(String name) {
    	return getInternalFullName(name);
    }

    
    /**
     * @deprecated Use {@link #getNamespaceForURI(String)}
     */
    @Deprecated
    public String getNamespaceForResourceName(String resourceName) {
       return getNamespaceForURI(resourceName);
    }

    /** 
     * @deprecated
     */
    @Deprecated
    public String getFrameNameForURI(String uri, boolean generatePrefix) {
    	return uri;
    }

    /**
     * @deprecated Use {@link #getLocalNameForURI(String)}
     */    
    @Deprecated
    public String getLocalNameForResourceName(String frameName) {
    	return getLocalNameForURI(frameName);
    }

      
    
    /*
     * Getter methods for the OWL entities
     * These essentially come from the system frames but there is some issues with the naming.
     */
   
    
    public RDFSNamedClass getRDFUntypedResourcesClass() {
        return getRDFExternalResourceClass();
    }

    public OWLNamedClass getOWLDatatypePropertyMetaClassCls() {
        return getOWLDatatypePropertyClass();
    }

    public OWLNamedClass getOWLObjectPropertyMetaClassCls() {
        return getOWLObjectPropertyClass();
    }
    
    public RDFSNamedClass getOWLAllDifferentClassCls() {
        return getOWLAllDifferentClass();
    }
    
    public OWLNamedClass getOWLNamedClassMetaClassCls() {
        return getOWLNamedClassClass();
    }
    
    public OWLNamedClass getOWLOntologyCls() {
        return getOWLOntologyClass();
    }
    
    public RDFSNamedClass getRDFListCls() {
        return getRDFListClass();
    }
    
    public OWLNamedClass getRDFSClassMetaClassCls() {
        return getRDFSNamedClassClass();
    }
    
    public OWLNamedClass getOWLNothing() {
        return getOWLNothingClass();
    }
    
    public RDFSNamedClass getOWLSomeValuesFromRestrictionClass() {
        return getOWLSomeValuesFromClass();
    }
    
    public RDFProperty getRDFSSubPropertyOfProperty() {
        return getRDFSSubPropertyOf();
    }
    
    /*
     * Getters retrieved from system frames.  Note that the case is slightly
     * different
     *    Owl -> OWL
     *    Rdf -> RDF
     *    Rdfs -> RDFS
     */

    /**
     * @return the owlThingClass
     */
    public OWLNamedClass getOWLThingClass() {
        return getSystemFrames().getOwlThingClass();
    }

    /**
     * @return the owlClassMetaCls
     */
    public RDFSNamedClass getOWLClassMetaCls() {
        return getSystemFrames().getOwlClassMetaCls();
    }

    /**
     * @return the rdfsNamedClassClass
     */
    public OWLNamedClass getRDFSNamedClassClass() {
        return getSystemFrames().getRdfsNamedClassClass();
    }

    /**
     * @return the owlNamedClassClass
     */
    public OWLNamedClass getOWLNamedClassClass() {
        return getSystemFrames().getOwlNamedClassClass();
    }

    /**
     * @return the owlDeprecatedClassClass
     */
    public RDFSNamedClass getOWLDeprecatedClassClass() {
        return getSystemFrames().getOwlDeprecatedClassClass();
    }

    /**
     * @return the anonymousClassMetaCls
     */
    public OWLNamedClass getAnonymousClassMetaCls() {
        return getSystemFrames().getAnonymousClassMetaCls();
    }

    /**
     * @return the owlEnumeratedClassClass
     */
    public RDFSNamedClass getOWLEnumeratedClassClass() {
        return getSystemFrames().getOwlEnumeratedClassClass();
    }

    /**
     * @return the owlRestrictionClass
     */
    public RDFSNamedClass getOWLRestrictionClass() {
        return getSystemFrames().getOwlRestrictionClass();
    }

    /**
     * @return the owlAllValuesFromClass
     */
    public RDFSNamedClass getOWLAllValuesFromClass() {
        return getSystemFrames().getOwlAllValuesFromClass();
    }

    /**
     * @return the owlHasValueClass
     */
    public RDFSNamedClass getOWLHasValueClass() {
        return getSystemFrames().getOwlHasValueClass();
    }

    /**
     * @return the owlMaxCardinalityClass
     */
    public RDFSNamedClass getOWLMaxCardinalityClass() {
        return getSystemFrames().getOwlMaxCardinalityClass();
    }

    /**
     * @return the owlMinCardinalityClass
     */
    public RDFSNamedClass getOWLMinCardinalityClass() {
        return getSystemFrames().getOwlMinCardinalityClass();
    }

    /**
     * @return the owlCardinalityClass
     */
    public RDFSNamedClass getOWLCardinalityClass() {
        return getSystemFrames().getOwlCardinalityClass();
    }

    /**
     * @return the owlSomeValuesFromClass
     */
    public RDFSNamedClass getOWLSomeValuesFromClass() {
        return getSystemFrames().getOwlSomeValuesFromClass();
    }

    /**
     * @return the owlLogicalClassClass
     */
    public RDFSNamedClass getOWLLogicalClassClass() {
        return getSystemFrames().getOwlLogicalClassClass();
    }

    /**
     * @return the owlComplementClassClass
     */
    public RDFSNamedClass getOWLComplementClassClass() {
        return getSystemFrames().getOwlComplementClassClass();
    }

    /**
     * @return the owlIntersectionClassClass
     */
    public RDFSNamedClass getOWLIntersectionClassClass() {
        return getSystemFrames().getOwlIntersectionClassClass();
    }

    /**
     * @return the owlUnionClassClass
     */
    public RDFSNamedClass getOWLUnionClassClass() {
        return getSystemFrames().getOwlUnionClassClass();
    }

    /**
     * @return the rdfPropertyClass
     */
    public OWLNamedClass getRDFPropertyClass() {
        return getSystemFrames().getRdfPropertyClass();
    }

    /**
     * @return the owlDatatypePropertyClass
     */
    public OWLNamedClass getOWLDatatypePropertyClass() {
        return getSystemFrames().getOwlDatatypePropertyClass();
    }

    /**
     * @return the owlObjectPropertyClass
     */
    public OWLNamedClass getOWLObjectPropertyClass() {
        return getSystemFrames().getOwlObjectPropertyClass();
    }

    /**
     * @return the owlInverseFunctionalPropertyClass
     */
    public OWLNamedClass getOWLInverseFunctionalPropertyClass() {
        return getSystemFrames().getOwlInverseFunctionalPropertyClass();
    }

    /**
     * @return the owlSymmetricPropertyClass
     */
    public OWLNamedClass getOWLSymmetricPropertyClass() {
        return getSystemFrames().getOwlSymmetricPropertyClass();
    }

    /**
     * @return the owlTransitivePropertyClass
     */
    public OWLNamedClass getOWLTransitivePropertyClass() {
        return getSystemFrames().getOwlTransitivePropertyClass();
    }

    /**
     * @return the owlAnnotationPropertyClass
     */
    public OWLNamedClass getOWLAnnotationPropertyClass() {
        return getSystemFrames().getOwlAnnotationPropertyClass();
    }

    /**
     * @return the owlFunctionalPropertyClass
     */
    public OWLNamedClass getOWLFunctionalPropertyClass() {
        return getSystemFrames().getOwlFunctionalPropertyClass();
    }

    /**
     * @return the owlDeprecatedPropertyClass
     */
    public RDFSNamedClass getOWLDeprecatedPropertyClass() {
        return getSystemFrames().getOwlDeprecatedPropertyClass();
    }

    /**
     * @return the rdfsDatatypeClass
     */
    public RDFSNamedClass getRDFSDatatypeClass() {
        return getSystemFrames().getRdfsDatatypeClass();
    }

    /**
     * @return the owlOntologyClass
     */
    public OWLNamedClass getOWLOntologyClass() {
        return getSystemFrames().getOwlOntologyClass();
    }

    /**
     * @return the owlNothingClass
     */
    public OWLNamedClass getOWLNothingClass() {
        return getSystemFrames().getOwlNothingClass();
    }

    /**
     * @return the rdfListClass
     */
    public RDFSNamedClass getRDFListClass() {
        return getSystemFrames().getRdfListClass();
    }

    /**
     * @return the owlAllDifferentClass
     */
    public RDFSNamedClass getOWLAllDifferentClass() {
        return getSystemFrames().getOwlAllDifferentClass();
    }

    /**
     * @return the rdfsLiteralClass
     */
    public RDFSNamedClass getRDFSLiteralClass() {
        return getSystemFrames().getRdfsLiteralClass();
    }

    /**
     * @return the rdfsContainerClass
     */
    public RDFSNamedClass getRDFSContainerClass() {
        return getSystemFrames().getRdfsContainerClass();
    }

    /**
     * @return the rdfAltClass
     */
    public RDFSNamedClass getRDFAltClass() {
        return getSystemFrames().getRdfAltClass();
    }

    /**
     * @return the rdfBagClass
     */
    public RDFSNamedClass getRDFBagClass() {
        return getSystemFrames().getRdfBagClass();
    }

    /**
     * @return the rdfSeqClass
     */
    public RDFSNamedClass getRDFSeqClass() {
        return getSystemFrames().getRdfSeqClass();
    }

    /**
     * @return the rdfStatementClass
     */
    public RDFSNamedClass getRDFStatementClass() {
        return getSystemFrames().getRdfStatementClass();
    }

    /**
     * @return the owlDataRangeClass
     */
    public RDFSNamedClass getOWLDataRangeClass() {
        return getSystemFrames().getOwlDataRangeClass();
    }

    /**
     * @return the anonymousRootCls
     */
    public RDFSNamedClass getAnonymousRootCls() {
        return getSystemFrames().getAnonymousRootCls();
    }

    /**
     * @return the rdfExternalResourceClass
     */
    public RDFSNamedClass getRDFExternalResourceClass() {
        return getSystemFrames().getRdfExternalResourceClass();
    }

    /**
     * @return the topOWLOntologyClass
     */
    public RDFSNamedClass getTopOWLOntologyClass() {
        return getSystemFrames().getOwlOntologyPointerClass();
    }

    /**
     * @return the owlAllValuesFromProperty
     */
    public RDFProperty getOWLAllValuesFromProperty() {
        return getSystemFrames().getOwlAllValuesFromProperty();
    }

    /**
     * @return the owlBackwardCompatibleWithProperty
     */
    public RDFProperty getOWLBackwardCompatibleWithProperty() {
        return getSystemFrames().getOwlBackwardCompatibleWithProperty();
    }

    /**
     * @return the owlCardinalityProperty
     */
    public RDFProperty getOWLCardinalityProperty() {
        return getSystemFrames().getOwlCardinalityProperty();
    }

    /**
     * @return the owlComplementOfProperty
     */
    public RDFProperty getOWLComplementOfProperty() {
        return getSystemFrames().getOwlComplementOfProperty();
    }

    /**
     * @return the owlDifferentFromProperty
     */
    public RDFProperty getOWLDifferentFromProperty() {
        return getSystemFrames().getOwlDifferentFromProperty();
    }

    /**
     * @return the owlDisjointWithProperty
     */
    public RDFProperty getOWLDisjointWithProperty() {
        return getSystemFrames().getOwlDisjointWithProperty();
    }

    /**
     * @return the owlDistinctMembersProperty
     */
    public RDFProperty getOWLDistinctMembersProperty() {
        return getSystemFrames().getOwlDistinctMembersProperty();
    }

    /**
     * @return the owlEquivalentClassProperty
     */
    public RDFProperty getOWLEquivalentClassProperty() {
        return getSystemFrames().getOwlEquivalentClassProperty();
    }

    /**
     * @return the owlEquivalentPropertyProperty
     */
    public RDFProperty getOWLEquivalentPropertyProperty() {
        return getSystemFrames().getOwlEquivalentPropertyProperty();
    }

    /**
     * @return the owlHasValueProperty
     */
    public RDFProperty getOWLHasValueProperty() {
        return getSystemFrames().getOwlHasValueProperty();
    }

    /**
     * @return the owlImportsProperty
     */
    public RDFProperty getOWLImportsProperty() {
        return getSystemFrames().getOwlImportsProperty();
    }

    /**
     * @return the owlIncompatibleWithProperty
     */
    public RDFProperty getOWLIncompatibleWithProperty() {
        return getSystemFrames().getOwlIncompatibleWithProperty();
    }

    /**
     * @return the owlIntersectionOfProperty
     */
    public RDFProperty getOWLIntersectionOfProperty() {
        return getSystemFrames().getOwlIntersectionOfProperty();
    }

    /**
     * @return the owlInverseOfProperty
     */
    public RDFProperty getOWLInverseOfProperty() {
        return getSystemFrames().getOwlInverseOfProperty();
    }

    /**
     * @return the owlMaxCardinalityProperty
     */
    public RDFProperty getOWLMaxCardinalityProperty() {
        return getSystemFrames().getOwlMaxCardinalityProperty();
    }

    /**
     * @return the owlMinCardinalityProperty
     */
    public RDFProperty getOWLMinCardinalityProperty() {
        return getSystemFrames().getOwlMinCardinalityProperty();
    }

    /**
     * @return the owlOneOfProperty
     */
    public RDFProperty getOWLOneOfProperty() {
        return getSystemFrames().getOwlOneOfProperty();
    }

    /**
     * @return the owlOnPropertyProperty
     */
    public RDFProperty getOWLOnPropertyProperty() {
        return getSystemFrames().getOwlOnPropertyProperty();
    }

    /**
     * @return the owlPriorVersionProperty
     */
    public RDFProperty getOWLPriorVersionProperty() {
        return getSystemFrames().getOwlPriorVersionProperty();
    }

    /**
     * @return the owlSameAsProperty
     */
    public RDFProperty getOWLSameAsProperty() {
        return getSystemFrames().getOwlSameAsProperty();
    }

    /**
     * @return the owlSomeValuesFromProperty
     */
    public RDFProperty getOWLSomeValuesFromProperty() {
        return getSystemFrames().getOwlSomeValuesFromProperty();
    }

    /**
     * @return the owlUnionOfProperty
     */
    public RDFProperty getOWLUnionOfProperty() {
        return getSystemFrames().getOwlUnionOfProperty();
    }

    /**
     * @return the owlValuesFromProperty
     */
    public RDFProperty getOWLValuesFromProperty() {
        return getSystemFrames().getOwlValuesFromProperty();
    }

    /**
     * @return the owlVersionInfoProperty
     */
    public RDFProperty getOWLVersionInfoProperty() {
        return getSystemFrames().getOwlVersionInfoProperty();
    }

    /**
     * @return the protegeClassificationStatusProperty
     */
    public RDFProperty getProtegeClassificationStatusProperty() {
        return getSystemFrames().getProtegeClassificationStatusProperty();
    }

    /**
     * @return the protegeInferredSubclassesProperty
     */
    public RDFProperty getProtegeInferredSubclassesProperty() {
        return getSystemFrames().getProtegeInferredSubclassesProperty();
    }

    /**
     * @return the protegeInferredSuperclassesProperty
     */
    public RDFProperty getProtegeInferredSuperclassesProperty() {
        return getSystemFrames().getProtegeInferredSuperclassesProperty();
    }

    /**
     * @return the protegeInferredTypeProperty
     */
    public RDFProperty getProtegeInferredTypeProperty() {
        return getSystemFrames().getProtegeInferredTypeProperty();
    }

    /**
     * @return the rdfFirstProperty
     */
    public RDFProperty getRDFFirstProperty() {
        return getSystemFrames().getRdfFirstProperty();
    }

    /**
     * @return the rdfObjectProperty
     */
    public RDFProperty getRDFObjectProperty() {
        return getSystemFrames().getRdfObjectProperty();
    }

    /**
     * @return the rdfPredicateProperty
     */
    public RDFProperty getRDFPredicateProperty() {
        return getSystemFrames().getRdfPredicateProperty();
    }

    /**
     * @return the rdfRestSlot
     */
    public RDFProperty getRDFRestProperty() {
        return getSystemFrames().getRdfRestProperty();
    }

    /**
     * @return the rdfSubjectProperty
     */
    public RDFProperty getRDFSubjectProperty() {
        return getSystemFrames().getRdfSubjectProperty();
    }

    /**
     * @return the rdfTypeProperty
     */
    public RDFProperty getRDFTypeProperty() {
        return getSystemFrames().getRdfTypeProperty();
    }

    /**
     * @return the rdfValueProperty
     */
    public RDFProperty getRDFValueProperty() {
        return getSystemFrames().getRdfValueProperty();
    }

    /**
     * @return the rdfsCommentProperty
     */
    public OWLDatatypeProperty getRDFSCommentProperty() {
        return getSystemFrames().getRdfsCommentProperty();
    }

    /**
     * @return the rdfsDomainProperty
     */
    public RDFProperty getRDFSDomainProperty() {
        return getSystemFrames().getRdfsDomainProperty();
    }

    /**
     * @return the rdfsIsDefinedByProperty
     */
    public RDFProperty getRDFSIsDefinedByProperty() {
        return getSystemFrames().getRdfsIsDefinedByProperty();
    }

    /**
     * @return the rdfsLabelProperty
     */
    public RDFProperty getRDFSLabelProperty() {
        return getSystemFrames().getRdfsLabelProperty();
    }

    /**
     * @return the rdfsMemberProperty
     */
    public RDFProperty getRDFSMemberProperty() {
        return getSystemFrames().getRdfsMemberProperty();
    }

    /**
     * @return the rdfsRangeProperty
     */
    public RDFProperty getRDFSRangeProperty() {
        return getSystemFrames().getRdfsRangeProperty();
    }

    /**
     * @return the rdfsSeeAlsoProperty
     */
    public RDFProperty getRDFSSeeAlsoProperty() {
        return getSystemFrames().getRdfsSeeAlsoProperty();
    }

    /**
     * @return the rdfsSubClassOfProperty
     */
    public RDFProperty getRDFSSubClassOfProperty() {
        return getSystemFrames().getRdfsSubClassOfProperty();
    }

    /**
     * @return the rdfsSubPropertyOf
     */
    public RDFProperty getRDFSSubPropertyOf() {
        return getSystemFrames().getRdfsSubPropertyOf();
    }

    /**
     * @return the owlOntologyPrefixesProperty
     */
    public RDFProperty getOWLOntologyPrefixesProperty() {
        return getSystemFrames().getOwlOntologyPrefixesProperty();
    }

    /**
     * @return the owlResourceURIProperty
     */
    public RDFProperty getOWLResourceURIProperty() {
        return getSystemFrames().getOwlResourceURIProperty();
    }

    /**
     * @return the topOWLOntologyURISlot
     */
    public RDFProperty getTopOWLOntologyURISlot() {
        return getSystemFrames().getOwlOntologyPointerProperty();
    }

    /**
     * @return the rdfNilIndividual
     */
    public RDFList getRDFNil() {
        return getSystemFrames().getRdfNil();
    }

    /**
     * @return the rdfDatatypes
     */
    public Set<RDFSDatatype> getRDFDatatypes() {
        return getSystemFrames().getRdfDatatypes();
    }

    /**
     * @return the floatDatatypes
     */
    public Set<RDFSDatatype> getFloatDatatypes() {
        return getSystemFrames().getFloatDatatypes();
    }

    /**
     * @return the integerDatatypes
     */
    public Set<RDFSDatatype> getIntegerDatatypes() {
        return getSystemFrames().getIntegerDatatypes();
    }
    
    /*
     * Getters for the important datatypes.  We don't need all 43...
     */
    public RDFSDatatype getXSDboolean() {
        return getSystemFrames().getXsdBoolean();
    }

    public RDFSDatatype getXSDdouble() {
        return getSystemFrames().getXsdDouble();
    }

    public RDFSDatatype getXSDfloat() {
        return getSystemFrames().getXsdFloat();
    }

    public RDFSDatatype getXSDlong() {
        return getSystemFrames().getXsdLong();
    }

    public RDFSDatatype getXSDint() {
        return getSystemFrames().getXsdInt();
    }

    public RDFSDatatype getXSDshort() {
        return getSystemFrames().getXsdShort();
    }

    public RDFSDatatype getXSDbyte() {
        return getSystemFrames().getXsdByte();
    }

    public RDFSDatatype getXSDstring() {
        return getSystemFrames().getXsdString();
    }

    public RDFSDatatype getXSDbase64Binary() {
        return getSystemFrames().getXsdBase64Binary();
    }

    public RDFSDatatype getXSDdate() {
        return getSystemFrames().getXsdDate();
    }

    public RDFSDatatype getXSDtime() {
        return getSystemFrames().getXsdTime();
    }

    public RDFSDatatype getXSDdateTime() {
        return getSystemFrames().getXsdDateTime();
    }

    public RDFSDatatype getXSDduration() {
        return getSystemFrames().getXsdDuration();
    }

    public RDFSDatatype getXSDanyURI() {
        return getSystemFrames().getXsdAnyURI();
    }

    public RDFSDatatype getXSDdecimal() {
        return getSystemFrames().getXsdDecimal();
    }

    public RDFSDatatype getXSDinteger() {
        return getSystemFrames().getXsdInteger();
    }

    public RDFSDatatype getRDFXMLLiteralType() {
        return getSystemFrames().getXmlLiteralType();
    }

    /*
     * End getter methods for OWL entities
     */
    
	public boolean isExpandShortNameInMethods() {
		return expandShortNameInMethods;
	}

	public void setExpandShortNameInMethods(boolean expandShortNameInMethods) {
		this.expandShortNameInMethods = expandShortNameInMethods;
	}

	
    public RDFProperty getProtegeSubclassesDisjointProperty() {
        //TT: is it safe to cache this value? What if an import is added/removed? 
        if (protegeSubclassesDisjointProperty == null) {
            protegeSubclassesDisjointProperty = getSlot(ProtegeNames.getSubclassesDisjointSlotName());
        }
        return (RDFProperty) protegeSubclassesDisjointProperty;
    }

	
	
    /*
     * Get the full name of a resource.  We have to handle the short name instead of full URIs
     * used in the create & getter methods. This is a backwards compatibility extension.
     * All the create & get methods will check the expandShortNameInMethods flag, which is default false.
     */   
    protected String getInternalFullName(String name) {
    	if (name == null) {
    		return null;
    	}
    	if (expandShortNameInMethods && !isAbsoluteURI(name)) {
    		name = NamespaceUtil.getFullName(this, name);
    	}
    	
    	return name;
    }

    protected boolean isAbsoluteURI(String uriString) {
        if (!uriString.contains(":")) {
            return false;
        }
        
        URI uri = null;
        
        try {
        	uri = new URI(uriString);
		} catch (URISyntaxException e) {			
			//do nothing - uri will be null. Hopefully this won't be the case
		}
    	
    	if (uri == null || !uri.isAbsolute()) {
    		return false;
    	}
    	
    	if (uri.isOpaque()) {
    		String scheme = uri.getScheme();
    		return getNamespaceManager().getNamespaceForPrefix(scheme) == null ? true : false;
    	}
    	
    	return true;
    }
    
    @Override
    public synchronized void dispose() {    	
    	super.dispose();
    	jenaModel = null;
    	owlProject = null;
    	
    	if (tripleStoreModel != null) {
    		tripleStoreModel.dispose();
    		tripleStoreModel = null;
    	}
    	
    	repositoryManager = null;
    	taskManager = null;
    }
	
}
