package edu.stanford.smi.protegex.owl.model.impl;

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

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.impl.Util;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.BrowserSlotPattern;
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
import edu.stanford.smi.protege.model.SystemFrames;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.graph.JenaModelFactory;
import edu.stanford.smi.protegex.owl.model.DefaultTaskManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerListener;
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
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSDatatypeFactory;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.TaskManager;
import edu.stanford.smi.protegex.owl.model.XSDNames;
import edu.stanford.smi.protegex.owl.model.XSPNames;
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
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactory;
import edu.stanford.smi.protegex.owl.model.framestore.OWLDeleteSimplificationFrameStore;
import edu.stanford.smi.protegex.owl.model.framestore.OWLFrameFactoryInvocationHandler;
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
import edu.stanford.smi.protegex.owl.model.validator.DefaultPropertyValueValidator;
import edu.stanford.smi.protegex.owl.model.validator.PropertyValueValidator;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;
import edu.stanford.smi.protegex.owl.testing.OWLTest;
import edu.stanford.smi.protegex.owl.testing.OWLTestLibrary;
import edu.stanford.smi.protegex.owl.ui.widget.OWLFormWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLWidgetMapper;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLModel extends DefaultKnowledgeBase
        implements NamespaceManagerListener, OWLModel {

    private static transient Logger log = Log.getLogger(AbstractOWLModel.class);

    private Cls owlAllDifferentClass;

    private Cls owlAnnotationPropertyClass;

    private Cls owlDeprecatedClassClass;

    private Cls owlDeprecatedPropertyClass;

    private Cls owlFunctionalPropertyClass;

    private Cls owlInverseFunctionalPropertyClass;

    private Cls owlSymmetricPropertyClass;

    private Cls owlTransitivePropertyClass;

    private Cls owlAllValuesFromClass;

    private Cls anonymousClassMetaCls;

    private Cls anonymousRootCls;

    private Cls owlDataRangeClass;

    private Cls owlEnumeratedClassClass;

    private Cls rdfExternalResourceClass;

    private Cls rdfListClass;

    private Cls owlOntologyClass;

    private Cls owlClassMetaCls;

    private Cls owlRestrictionClass;

    private Cls owlSomeValuesFromClass;


    private Cls owlHasValueClass;

    private Cls owlMinCardinalityClass;

    private Cls owlMaxCardinalityClass;

    private Cls owlCardinalityClass;

    private Cls owlLogicalClassClass;

    private Cls owlIntersectionClassClass;

    private Cls rdfStatementClass;

    private Cls rdfAltClass;

    private Cls rdfBagClass;

    private Cls rdfsNamedClassClass;

    private Cls rdfsContainerClass;

    private Cls rdfsDatatypeClass;

    private Cls rdfSeqClass;

    private Cls owlUnionClassClass;

    private Cls owlComplementClassClass;

    private Cls owlNamedClassClass;

    private Cls owlNothingClass;

    private Cls rdfsLiteralClass;

    private Cls rdfPropertyClass;

    private Cls owlDatatypePropertyClass;

    private Cls owlObjectPropertyClass;


    private Slot owlAllValuesFromProperty;

    private Slot owlBackwardCompatibleWithProperty;

    private Slot owlCardinalityProperty;

    private Slot owlComplementOfProperty;

    private Slot owlDisjointWithProperty;

    private Slot owlEquivalentClassProperty;

    private Slot owlValuesFromProperty;

    private Slot rdfsDomainProperty;

    private Slot rdfsRangeProperty;

    private Slot rdfsSubClassOfProperty;

    private Slot rdfsSubPropertyOfProperty;


    private Slot protegeClassificationStatusProperty;

    private Slot rdfsCommentProperty;

    private Slot owlDifferentFromProperty;

    private Slot owlDistinctMembersProperty;

    private Slot owlEquivalentPropertyProperty;

    private Slot owlHasValueProperty;

    private Slot owlIncompatibleWithProperty;

    private Slot owlInverseOfProperty;

    private Slot protegeInferredTypeProperty;

    private Slot protegeInferredSubclassesProperty;

    private Slot protegeInferredSuperclassesProperty;

    private Slot owlIntersectionOfProperty;

    private Slot rdfsIsDefinedByProperty;

    private Slot rdfsLabelProperty;

    private Slot owlLogicalOperandsProperty;

    private Slot owlMaxCardinalityProperty;

    private Slot owlMinCardinalityProperty;

    private Slot nameSlot;

    private Slot owlOnPropertyProperty;

    private Slot owlImportsProperty;

    private Slot owlOntologyPrefixesProperty;

    private Slot owlOneOfProperty;

    private Slot owlUnionOfProperty;

    private Slot owlPriorVersionProperty;

    private Slot rdfFirstProperty;

    private Slot rdfObjectProperty;

    private Slot rdfPredicateProperty;

    private Slot rdfRestSlot;

    private Slot rdfSubjectProperty;

    private Slot rdfTypeProperty;

    private Slot rdfValueProperty;

    private Slot rdfsMemberProperty;

    private Slot owlResourceURIProperty;

    private Slot owlSameAsProperty;

    private Slot rdfsSeeAlsoProperty;

    private Slot owlSomeValuesFromProperty;

    private Slot owlVersionInfoProperty;

    private Slot protegeSubclassesDisjointProperty;

    private RDFSDatatype xsdBoolean;

    private RDFSDatatype xsdDouble;

    private RDFSDatatype xsdFloat;

    private RDFSDatatype xsdLong;

    private RDFSDatatype xsdInt;

    private RDFSDatatype xsdShort;

    private RDFSDatatype xsdByte;

    private RDFSDatatype xsdString;

    private RDFSDatatype xsdBase64Binary;

    private RDFSDatatype xsdDate;

    private RDFSDatatype xsdTime;

    private RDFSDatatype xsdDateTime;

    private RDFSDatatype xsdDuration;

    private RDFSDatatype xsdAnyURI;

    private RDFSDatatype xsdDecimal;

    private RDFSDatatype xsdInteger;

    private RDFSDatatype xmlLiteralType;

    private Set floatDatatypes = new HashSet();

    private Set integerDatatypes = new HashSet();

    /**
     * A running id used to create the system FrameIDs
     */
    private int systemID = 9001;

    private boolean inInit = true;

    private com.hp.hpl.jena.rdf.model.Model jenaModel;

    public static final String ANONYMOUS_BASE = "@";

    private boolean bootstrapped = false;

    private OWLClassDisplay owlClassRenderer = OWLClassDisplayFactory.getDefaultDisplay();

    private OWLFrameStore owlFrameStore;

    private OWLProject owlProject;

    private PropertyValueValidator propertyValueValidator = new DefaultPropertyValueValidator();

    public static final String DEFAULT_TODO_PREFIX = "TODO";

    public static final String[] DEFAULT_USED_LANGUAGES = {
            "de",
            "en",
            "es",
            "fr",
            "it",
            "nl",
            "pt",
            "ru"
    };

    private boolean loadDefaults = true;

    private NamespaceManager namespaceManager;

    private Instance rdfNilIndividual;

    private OWLNamedClass owlThingClass;

    private static final String SEARCH_SYNONYMS_KEY = "OWL-SEARCH-SYNONYMS-SLOTS";

    private static final String SEARCH_SYNONYMS_SEPARATOR = ",";

    /**
     * The characters that are valid name parts (in addition to the Java identifier chars)
     */
    private final static String VALID_SYMBOLS = "-."; // "-.+/:";

    private Set defaultAnonymousTypes = new HashSet();

    public static final String DEFAULT_ANNOTATION_PROPERTY_NAME = "annotationProperty";

    public static final String DEFAULT_CLASS_NAME = "Class";

    public static final String DEFAULT_DATATYPE_PROPERTY_NAME = "datatypeProperty";

    public static final String DEFAULT_INDIVIDUAL_NAME = "Individual";

    public static final String DEFAULT_OBJECT_PROPERTY_NAME = "objectProperty";

    public static final String DEFAULT_PROPERTY_NAME = "property";

    private RDFSDatatypeFactory rdfsDatatypeFactory = new DefaultRDFSDatatypeFactory(this);

    private TaskManager taskManager;

    private RepositoryManager repositoryManager;


    public AbstractOWLModel(KnowledgeBaseFactory factory) {
        super(factory);
        resetSystemFrames();
    }

    public AbstractOWLModel(KnowledgeBaseFactory factory,
                            NamespaceManager namespaceManager) {
        super(factory);
        resetSystemFrames();
        initialize(namespaceManager);
    }

    private void resetSystemFrames() {
        MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(this);
        if (mnfs == null) {
            adjustThing();
            initOWLFrameFactoryInvocationHandler();
            String name = getRootCls().getDirectType().getName();
            if (name.equals(Model.Cls.STANDARD_CLASS)) {
                bootstrap();
            }
        }
        else {
            NarrowFrameStore systemFrameStore = mnfs.getSystemFrameStore();
            NarrowFrameStore oldActiveFrameStore = mnfs.setActiveFrameStore(systemFrameStore);
            adjustThing();
            initOWLFrameFactoryInvocationHandler();
            bootstrap();
            mnfs.setActiveFrameStore(oldActiveFrameStore);
        }
    }

    public void initialize(NamespaceManager namespaceManager) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Phase 2 initialization of OWL Model starts");
        }
        this.namespaceManager = namespaceManager;
        namespaceManager.addNamespaceManagerListener(this);
        setGenerateDeletingFrameEventsEnabled(true);

        // resetSystemFrames();

        inInit = false;

        namespaceManager.init(this);

        createDefaultOWLOntology();

        namespaceManager.setModifiable(OWLNames.OWL_PREFIX, false);
        namespaceManager.setModifiable(RDFNames.RDF_PREFIX, false);
        namespaceManager.setModifiable(RDFSNames.RDFS_PREFIX, false);
        namespaceManager.setModifiable(RDFNames.XSD_PREFIX, false);

        setDefaultClsMetaCls(owlNamedClassClass);
        setDefaultSlotMetaCls(owlDatatypePropertyClass);
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

        initOWLFrameStore();

        defaultAnonymousTypes.add(rdfListClass);
        defaultAnonymousTypes.add(owlAllValuesFromClass);
        defaultAnonymousTypes.add(owlSomeValuesFromClass);
        defaultAnonymousTypes.add(owlHasValueClass);
        defaultAnonymousTypes.add(owlMinCardinalityClass);
        defaultAnonymousTypes.add(owlMaxCardinalityClass);
        defaultAnonymousTypes.add(owlCardinalityClass);
        defaultAnonymousTypes.add(owlComplementClassClass);
        defaultAnonymousTypes.add(owlIntersectionClassClass);
        defaultAnonymousTypes.add(owlUnionClassClass);
        defaultAnonymousTypes.add(owlEnumeratedClassClass);
        defaultAnonymousTypes.add(owlAllDifferentClass);
        defaultAnonymousTypes.add(owlDataRangeClass);

        bootstrapped = true;

        taskManager = new DefaultTaskManager();
        taskManager.setProgressDisplay(new NoopProgressDisplay());
        if (super.getProject() != null) {
            setProject(super.getProject());
        }
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
            if (o instanceof String) {
                final String str = (String) o;
                if (DefaultRDFSLiteral.isRawValue(str)) {
                    result.add(new DefaultRDFSLiteral(this, str));
                }
                else {
                    result.add(createRDFSLiteral(o));
                }
            }
            else {
                result.add(o);
            }
        }
        return result;
    }


    public void addResourceListener(ResourceListener listener) {
        if (!(listener instanceof ResourceAdapter)) {
            throw new IllegalArgumentException("Listener must be a ResourceAdapter");
        }
        addInstanceListener(listener);
    }


    public void adjustSystemClasses() {
        final Slot[] thingSlots = new Slot[]{
                nameSlot,
                rdfsLabelProperty,
                rdfsIsDefinedByProperty,
                rdfsSeeAlsoProperty,
                owlVersionInfoProperty,
                owlDifferentFromProperty,
                owlSameAsProperty,
                rdfValueProperty,
                rdfsMemberProperty,
                protegeInferredTypeProperty
        };
        for (int i = 0; i < thingSlots.length; i++) {
            Slot slot = thingSlots[i];
            Collection oldDomain = slot.getDirectDomain();
            if (oldDomain.size() != 1 || !oldDomain.contains(owlThingClass)) {
                for (Iterator it = new ArrayList(oldDomain).iterator(); it.hasNext();) {
                    Cls oldDomainCls = (Cls) it.next();
                    oldDomainCls.removeDirectTemplateSlot(slot);
                }
                ((Cls) owlThingClass).addDirectTemplateSlot(slot);
            }
        }
    }


    public void adjustThing() {
        getRootCls().setName(OWLNames.Cls.THING);
    }


    public abstract void initOWLFrameFactoryInvocationHandler();


    protected void initOWLFrameStore() {
        if (!(getFrameStores().get(0) instanceof OWLFrameStore)) {
            owlFrameStore = new OWLFrameStore(this);
            insertFrameStore(owlFrameStore);
        }
    }
    
    public void adjustClientFrameStores() {
      FrameStoreManager fsm = getFrameStoreManager();
      fsm.setEnabled(owlFrameStore, false);

      FrameStore owlConverter = fsm.getFrameStoreFromClass(OWLFrameFactoryInvocationHandler.class);
      if (owlConverter != null) {
        fsm.setEnabled(owlConverter, false);
      }
      
      FrameStore owlDeleteSimplificationFS = fsm.getFrameStoreFromClass(OWLDeleteSimplificationFrameStore.class);
      if (owlDeleteSimplificationFS != null) {
        fsm.setEnabled(owlDeleteSimplificationFS, false);
      }

    }


    private Cls createSystemCls(String name, Cls superclass) {
        return createSystemCls(name, Collections.singleton(superclass), superclass.getDirectType());
    }


    private Cls createSystemCls(String name, Collection superclasses, Cls type) {
        return createCls(FrameID.createSystem(systemID++), name, superclasses, Collections.singleton(type), false);
    }


    protected SystemFrames createSystemFrames() {
        SystemFrames systemFrames = super.createSystemFrames();
        systemFrames.replaceFrame(new DefaultRDFProperty(this, Model.SlotID.DIRECT_SUPERSLOTS));
        return systemFrames;
    }


    /**
     * Creates the OWL metamodel.
     */
    public void bootstrap() {

        nameSlot = getSlot(Model.Slot.NAME);
        Cls standardCls = getCls(Model.Cls.STANDARD_CLASS);

        anonymousRootCls = createSystemCls(OWLNames.Cls.ANONYMOUS_ROOT, getCls(Model.Cls.SYSTEM_CLASS));

        owlClassMetaCls = createSystemCls(OWLNames.Cls.OWL_CLASS, getCls(Model.Cls.CLASS));
        owlClassMetaCls.setAbstract(true);

        rdfsNamedClassClass = createSystemCls(RDFSNames.Cls.NAMED_CLASS, Arrays.asList(new Cls[]{
                getRootCls(),
                owlClassMetaCls,
                standardCls
        }), standardCls);
        rdfsNamedClassClass.setDirectType(rdfsNamedClassClass);
        owlClassMetaCls.setDirectType(rdfsNamedClassClass);
        owlNamedClassClass = createSystemCls(OWLNames.Cls.NAMED_CLASS, rdfsNamedClassClass);
        owlNamedClassClass.setDirectType(owlNamedClassClass);
        owlNamedClassClass = getCls(OWLNames.Cls.NAMED_CLASS);
        rdfsNamedClassClass.setDirectType(owlNamedClassClass);
        rdfsNamedClassClass = getCls(RDFSNames.Cls.NAMED_CLASS);
        getRootCls().setDirectType(owlNamedClassClass);

        anonymousRootCls.setDirectType(rdfsNamedClassClass);

        anonymousClassMetaCls = createSystemCls(OWLNames.Cls.ANONYMOUS_CLASS,
                                                Collections.singleton(owlClassMetaCls), rdfsNamedClassClass);
        anonymousClassMetaCls.setAbstract(true);
        owlEnumeratedClassClass = createSystemCls(OWLNames.Cls.ENUMERATED_CLASS, anonymousClassMetaCls);

        List slotClassSupers = Arrays.asList(new Cls[]{getRootCls(), getCls(Model.Cls.STANDARD_SLOT)});
        rdfPropertyClass = createSystemCls(RDFNames.Cls.PROPERTY, slotClassSupers, owlNamedClassClass);
        rdfPropertyClass = getCls(RDFNames.Cls.PROPERTY);
        owlDatatypePropertyClass = createSystemCls(OWLNames.Cls.DATATYPE_PROPERTY, rdfPropertyClass);
        owlObjectPropertyClass = createSystemCls(OWLNames.Cls.OBJECT_PROPERTY, rdfPropertyClass);

        rdfsDomainProperty = createSystemSlot(RDFSNames.Slot.DOMAIN, rdfPropertyClass);
        rdfsDomainProperty.setValueType(ValueType.INSTANCE);
        rdfsRangeProperty = createSystemSlot(RDFSNames.Slot.RANGE, rdfPropertyClass);
        rdfsRangeProperty.setValueType(ValueType.INSTANCE);
        rdfPropertyClass.addDirectTemplateSlot(rdfsDomainProperty);
        rdfPropertyClass.addDirectTemplateSlot(rdfsRangeProperty);

        owlInverseOfProperty = getSlot(Model.Slot.INVERSE);
        owlInverseOfProperty.setDirectType(rdfPropertyClass);
        owlInverseOfProperty = getSlot(Model.Slot.INVERSE);   // Re-get
        owlInverseOfProperty.setName(OWLNames.Slot.INVERSE_OF);
        owlInverseOfProperty.setValueType(ValueType.INSTANCE);
        owlInverseOfProperty.setAllowedClses(Collections.singleton(owlObjectPropertyClass));

        rdfsDatatypeClass = createSystemCls(RDFSNames.Cls.DATATYPE,
                                            Collections.singleton(owlThingClass), rdfsNamedClassClass);
        initRDFDatatypes();

        owlAnnotationPropertyClass = createSystemCls(OWLNames.Cls.ANNOTATION_PROPERTY,
                                                     Collections.singleton(rdfPropertyClass), rdfsNamedClassClass);

        initInferredSlots();

        initRestrictionMetaclasses();

        owlLogicalClassClass = createSystemCls(OWLNames.Cls.LOGICAL_CLASS, anonymousClassMetaCls);
        owlComplementClassClass = createSystemCls(OWLNames.Cls.COMPLEMENT_CLASS, owlLogicalClassClass);
        owlIntersectionClassClass = createSystemCls(OWLNames.Cls.INTERSECTION_CLASS, owlLogicalClassClass);
        owlUnionClassClass = createSystemCls(OWLNames.Cls.UNION_CLASS, owlLogicalClassClass);

        owlDifferentFromProperty = createInstanceSlot(OWLNames.Slot.DIFFERENT_FROM, rdfPropertyClass, getRootCls());
        owlDifferentFromProperty.setAllowsMultipleValues(true);
        owlDifferentFromProperty.addOwnSlotValue(rdfsRangeProperty, getRootCls());
        owlSameAsProperty = createInstanceSlot(OWLNames.Slot.SAME_AS, rdfPropertyClass, getRootCls());
        owlSameAsProperty.setAllowsMultipleValues(true);
        owlSameAsProperty.addOwnSlotValue(rdfsRangeProperty, getRootCls());

        owlDisjointWithProperty = createInstanceSlot(OWLNames.Slot.DISJOINT_WITH, rdfPropertyClass, rdfsNamedClassClass);
        owlDisjointWithProperty.setAllowsMultipleValues(true);
        owlClassMetaCls.addDirectTemplateSlot(owlDisjointWithProperty);

        owlComplementOfProperty = createInstanceSlot(OWLNames.Slot.COMPLEMENT_OF, rdfPropertyClass, owlClassMetaCls);
        owlComplementClassClass.addDirectTemplateSlot(owlComplementOfProperty);
        owlIntersectionOfProperty = createInstanceSlot(OWLNames.Slot.INTERSECTION_OF, rdfPropertyClass, rdfListClass);
        owlIntersectionClassClass.addDirectTemplateSlot(owlIntersectionOfProperty);
        owlUnionOfProperty = createInstanceSlot(OWLNames.Slot.UNION_OF, rdfPropertyClass, rdfListClass);
        owlUnionClassClass.addDirectTemplateSlot(owlUnionOfProperty);

        owlEquivalentPropertyProperty = createInstanceSlot(OWLNames.Slot.EQUIVALENT_PROPERTY, rdfPropertyClass, rdfPropertyClass);
        owlEquivalentPropertyProperty.setAllowsMultipleValues(true);
        rdfPropertyClass.addDirectTemplateSlot(owlEquivalentPropertyProperty);
        owlDatatypePropertyClass.setTemplateSlotAllowedClses(owlEquivalentPropertyProperty, Collections.singleton(owlDatatypePropertyClass));
        owlObjectPropertyClass.setTemplateSlotAllowedClses(owlEquivalentPropertyProperty, Collections.singleton(owlObjectPropertyClass));

        // rdfs:subClassOf
        rdfsSubClassOfProperty = createInstanceSlot(RDFSNames.Slot.SUB_CLASS_OF, rdfPropertyClass, owlClassMetaCls);
        rdfsSubClassOfProperty.setAllowsMultipleValues(true);
        rdfsSubClassOfProperty.setAllowedClses(Collections.singleton(rdfsNamedClassClass));
        rdfsSubClassOfProperty.setOwnSlotValue(rdfsRangeProperty, rdfsNamedClassClass);
        rdfsNamedClassClass.addDirectTemplateSlot(rdfsSubClassOfProperty);

        // rdfs:subPropertyOf
        rdfsSubPropertyOfProperty = getSlot(Model.Slot.DIRECT_SUPERSLOTS);
        rdfsSubPropertyOfProperty.setName(RDFSNames.Slot.SUB_PROPERTY_OF);
        rdfsSubPropertyOfProperty.setDirectType(rdfPropertyClass);
        rdfsSubPropertyOfProperty = (RDFProperty) getSlot(RDFSNames.Slot.SUB_PROPERTY_OF);

        // owl:equivalentClass
        owlEquivalentClassProperty = createInstanceSlot(OWLNames.Slot.EQUIVALENT_CLASS, rdfPropertyClass, owlClassMetaCls);
        owlEquivalentClassProperty.setAllowsMultipleValues(true);
        rdfsNamedClassClass.addDirectTemplateSlot(owlEquivalentClassProperty);

        // Annotation properties of :THING
        rdfsLabelProperty = createAnnotationOWLDatatypeProperty(RDFSNames.Slot.LABEL);
        setStringRange(rdfsLabelProperty);
        rdfsIsDefinedByProperty = createAnnotationOWLObjectProperty(RDFSNames.Slot.IS_DEFINED_BY);
        rdfsSeeAlsoProperty = createAnnotationOWLObjectProperty(RDFSNames.Slot.SEE_ALSO);
        owlVersionInfoProperty = createAnnotationOWLDatatypeProperty(OWLNames.Slot.VERSION_INFO);
        setStringRange(owlVersionInfoProperty);
        rdfsCommentProperty = createAnnotationOWLDatatypeProperty(RDFSNames.Slot.COMMENT);
        setStringRange(rdfsCommentProperty);

        rdfValueProperty = createSystemSlot(RDFNames.Slot.VALUE, rdfPropertyClass);
        rdfValueProperty.setAllowsMultipleValues(true);
        rdfValueProperty.setValueType(ValueType.ANY);

        rdfsMemberProperty = createSystemSlot(RDFSNames.Slot.MEMBER, rdfPropertyClass);
        rdfsMemberProperty.setValueType(ValueType.INSTANCE);
        ((Cls) owlThingClass).addDirectTemplateSlot(rdfsMemberProperty);

        adjustSystemClasses();

        Cls directedBinaryRelationCls = getCls(Model.Cls.DIRECTED_BINARY_RELATION);
        directedBinaryRelationCls.addDirectSuperclass(getRootCls());
        directedBinaryRelationCls.setDirectType(owlNamedClassClass);

        initOntologyMetaclass();

        rdfPropertyClass = getCls(RDFNames.Cls.PROPERTY);

        owlFunctionalPropertyClass = createSystemCls(OWLNames.Cls.FUNCTIONAL_PROPERTY,
                                                     Collections.singleton(rdfPropertyClass), rdfsNamedClassClass);
        owlInverseFunctionalPropertyClass = createSystemCls(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY,
                                                            Collections.singleton(rdfPropertyClass), rdfsNamedClassClass);
        owlSymmetricPropertyClass = createSystemCls(OWLNames.Cls.SYMMETRIC_PROPERTY,
                                                    Collections.singleton(rdfPropertyClass), rdfsNamedClassClass);
        owlTransitivePropertyClass = createSystemCls(OWLNames.Cls.TRANSITIVE_PROPERTY,
                                                     Collections.singleton(rdfPropertyClass), rdfsNamedClassClass);

        rdfExternalResourceClass = createSystemCls(RDFNames.Cls.EXTERNAL_RESOURCE,
                                                   Collections.singleton(getRootCls()), rdfsNamedClassClass);
        owlResourceURIProperty = createSystemSlot(OWLNames.Slot.RESOURCE_URI, rdfPropertyClass);
        owlResourceURIProperty.setValueType(ValueType.STRING);
        owlResourceURIProperty.setAllowsMultipleValues(false);
        rdfExternalResourceClass.addDirectTemplateSlot(owlResourceURIProperty);

        owlNothingClass = createSystemCls(OWLNames.Cls.NOTHING, getRootClses(), owlNamedClassClass);
        rdfListClass = createSystemCls(RDFNames.Cls.LIST, getRootClses(), owlNamedClassClass);
        rdfFirstProperty = createSystemSlot(RDFNames.Slot.FIRST, rdfPropertyClass);
        rdfFirstProperty.setValueType(ValueType.ANY);
        rdfFirstProperty.setAllowsMultipleValues(false);
        rdfRestSlot = createSystemSlot(RDFNames.Slot.REST, rdfPropertyClass);
        rdfRestSlot.setAllowsMultipleValues(false);
        rdfRestSlot.setValueType(ValueType.INSTANCE);
        rdfRestSlot.setAllowedClses(Collections.singleton(rdfListClass));
        rdfListClass.addDirectTemplateSlot(rdfFirstProperty);
        rdfListClass.addDirectTemplateSlot(rdfRestSlot);
        rdfNilIndividual = createSystemInstance(RDFNames.Instance.NIL, rdfListClass);

        owlAllDifferentClass = createSystemCls(OWLNames.Cls.ALL_DIFFERENT, Collections.singleton(owlThingClass), rdfsNamedClassClass);
        owlDistinctMembersProperty = createSystemSlot(OWLNames.Slot.DISTINCT_MEMBERS, rdfPropertyClass);
        owlDistinctMembersProperty.setValueType(ValueType.INSTANCE);
        owlDistinctMembersProperty.setAllowedClses(Collections.singleton(rdfListClass));
        owlAllDifferentClass.addDirectTemplateSlot(owlDistinctMembersProperty);

        rdfsLiteralClass = createSystemCls(RDFSNames.Cls.LITERAL, getRootClses(), owlNamedClassClass);

        rdfsContainerClass = createSystemCls(RDFSNames.Cls.CONTAINER, getRootClses(), rdfsNamedClassClass);
        rdfAltClass = createSystemCls(RDFNames.Cls.ALT, rdfsContainerClass);
        rdfBagClass = createSystemCls(RDFNames.Cls.BAG, rdfsContainerClass);
        rdfSeqClass = createSystemCls(RDFNames.Cls.SEQ, rdfsContainerClass);

        rdfObjectProperty = createSystemSlot(RDFNames.Slot.OBJECT, rdfPropertyClass);
        rdfObjectProperty.setValueType(ValueType.INSTANCE);
        rdfPredicateProperty = createSystemSlot(RDFNames.Slot.PREDICATE, rdfPropertyClass);
        rdfPredicateProperty.setValueType(ValueType.INSTANCE);
        rdfSubjectProperty = createSystemSlot(RDFNames.Slot.SUBJECT, rdfPropertyClass);
        rdfSubjectProperty.setValueType(ValueType.INSTANCE);

        rdfTypeProperty = createSystemSlot(RDFNames.Slot.TYPE, rdfPropertyClass);
        rdfTypeProperty.setValueType(ValueType.CLS);
        ((Cls) owlThingClass).addDirectTemplateSlot(rdfTypeProperty);

        rdfStatementClass = createSystemCls(RDFNames.Cls.STATEMENT, getRootClses(), rdfsNamedClassClass);
        rdfStatementClass.addDirectTemplateSlot(rdfObjectProperty);
        rdfStatementClass.addDirectTemplateSlot(rdfPredicateProperty);
        rdfStatementClass.addDirectTemplateSlot(rdfSubjectProperty);

        owlDeprecatedClassClass = createSystemCls(OWLNames.Cls.DEPRECATED_CLASS,
                                                  Collections.singleton(rdfsNamedClassClass), rdfsNamedClassClass);
        owlDeprecatedPropertyClass = createSystemCls(OWLNames.Cls.DEPRECATED_PROPERTY,
                                                     Collections.singleton(rdfPropertyClass), rdfsNamedClassClass);

        owlDataRangeClass = createSystemCls(OWLNames.Cls.DATA_RANGE, getRootClses(), rdfsNamedClassClass);

        owlOneOfProperty = createSystemSlot(OWLNames.Slot.ONE_OF, rdfPropertyClass);
        owlOneOfProperty.setValueType(ValueType.INSTANCE);
        owlOneOfProperty.setAllowedClses(Collections.singleton(rdfListClass));
        owlDataRangeClass.addDirectTemplateSlot(owlOneOfProperty);
        owlEnumeratedClassClass.addDirectTemplateSlot(owlOneOfProperty);

        setAbstract(owlThingClass, false);
        ((Cls) owlThingClass).addOwnSlotValue(rdfTypeProperty, owlNamedClassClass);

        adjustProtegeSystemFrames();

        if (!OWLNames.ClsID.THING.equals(((Cls) owlThingClass).getFrameID()) ||
            !RDFSNames.ClsID.NAMED_CLASS.equals(rdfsNamedClassClass.getFrameID()) ||
            !RDFNames.ClsID.PROPERTY.equals(rdfPropertyClass.getFrameID())) {
            throw new RuntimeException("Fatal Metaclass Error: FrameIDs mismatch.  Perhaps a database rebuild required?");
        }
    }


    private Slot createSystemSlot(String name, Cls type) {
        return createSlot(FrameID.createSystem(systemID++), name, Collections.singleton(type),
                          Collections.EMPTY_LIST, false);
    }


    private void adjustProtegeSystemFrames() {
        getCls(Model.Cls.DIRECTED_BINARY_RELATION).setDirectType(owlNamedClassClass);
        Slot toSlot = getSlot(Model.Slot.FROM);
        Slot fromSlot = getSlot(Model.Slot.TO);
        fromSlot.setDirectType(owlObjectPropertyClass);
        toSlot.setDirectType(owlObjectPropertyClass);

        Cls constraintCls = getCls(Model.Cls.PAL_CONSTRAINT);
        constraintCls.setDirectType(rdfsNamedClassClass);
        getSlot(Model.Slot.PAL_DESCRIPTION).setDirectType(owlDatatypePropertyClass);
        getSlot(Model.Slot.PAL_NAME).setDirectType(owlDatatypePropertyClass);
        getSlot(Model.Slot.PAL_RANGE).setDirectType(owlDatatypePropertyClass);
        getSlot(Model.Slot.PAL_STATEMENT).setDirectType(owlDatatypePropertyClass);
        getSlot(Model.Slot.CONSTRAINTS).setDirectType(owlObjectPropertyClass);
    }


    private void setStringRange(Slot slot) {
        Frame datatype = getFrame("xsd:string");
        slot.setDirectOwnSlotValue(rdfsRangeProperty, datatype);
        slot.setValueType(ValueType.STRING);
    }


    private void initRDFDatatypes() {
        TypeMapper typeMapper = TypeMapper.getInstance();
        Iterator it = typeMapper.listTypes();
        while (it.hasNext()) {
            com.hp.hpl.jena.datatypes.RDFDatatype type = (com.hp.hpl.jena.datatypes.RDFDatatype) it.next();
            String uri = type.getURI();
            if (uri.startsWith(XSDDatatype.XSD)) {
                String name = RDFNames.XSD_PREFIX + ":" + uri.substring(XSDDatatype.XSD.length() + 1);
                createSystemInstance(name, rdfsDatatypeClass);
            }
        }
        createSystemInstance(RDFNames.XML_LITERAL, rdfsDatatypeClass);
        fillDatatypeSet(XMLSchemaDatatypes.floatTypes, floatDatatypes);
        fillDatatypeSet(XMLSchemaDatatypes.integerTypes, integerDatatypes);
    }


    private void fillDatatypeSet(XSDDatatype[] types, Set set) {
        for (int i = 0; i < types.length; i++) {
            XSDDatatype datatype = types[i];
            String name = RDFNames.XSD_PREFIX + ":" + datatype.getURI().substring(XSDDatatype.XSD.length() + 1);
            set.add(getFrame(name));
        }
    }


    private Instance createSystemInstance(String name, Cls type) {
        return createInstance(FrameID.createSystem(systemID++), name, type, false);
    }


    public TaskManager getTaskManager() {
        if (taskManager == null) {
            taskManager = new DefaultTaskManager();
        }
        return taskManager;
    }


    private void initOntologyMetaclass() {
        owlOntologyClass = createSystemCls(OWLNames.Cls.ONTOLOGY,
                                           Collections.singleton(owlThingClass),
                                           owlNamedClassClass);
        owlOntologyPrefixesProperty = createSystemSlot(OWLNames.Slot.ONTOLOGY_PREFIXES, rdfPropertyClass);
        owlOntologyPrefixesProperty.setAllowsMultipleValues(true);
        owlOntologyPrefixesProperty.setValueType(ValueType.STRING);
        owlOntologyClass.addDirectTemplateSlot(owlOntologyPrefixesProperty);
        owlImportsProperty = createSystemSlot(OWLNames.Slot.IMPORTS, rdfPropertyClass);
        owlImportsProperty.setAllowsMultipleValues(true);
        owlOntologyClass.addDirectTemplateSlot(owlImportsProperty);

        owlBackwardCompatibleWithProperty = createAnnotationOWLObjectProperty(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        owlBackwardCompatibleWithProperty.setAllowedClses(Collections.EMPTY_LIST);
        //((Cls) owlThingClass).removeDirectTemplateSlot(owlBackwardCompatibleWithProperty);
        owlOntologyClass.addDirectTemplateSlot(owlBackwardCompatibleWithProperty);
        owlIncompatibleWithProperty = createAnnotationOWLObjectProperty(OWLNames.Slot.INCOMPATIBLE_WITH);
        owlIncompatibleWithProperty.setAllowedClses(Collections.EMPTY_LIST);
        //((Cls) owlThingClass).removeDirectTemplateSlot(owlIncompatibleWithProperty);
        owlOntologyClass.addDirectTemplateSlot(owlIncompatibleWithProperty);
        owlPriorVersionProperty = createAnnotationOWLObjectProperty(OWLNames.Slot.PRIOR_VERSION);
        owlPriorVersionProperty.setAllowedClses(Collections.EMPTY_LIST);
        //((Cls) owlThingClass).removeDirectTemplateSlot(owlPriorVersionProperty);
        owlOntologyClass.addDirectTemplateSlot(owlPriorVersionProperty);
    }


    private void initRestrictionMetaclasses() {
        owlOnPropertyProperty = createSystemSlot(OWLNames.Slot.ON_PROPERTY, rdfPropertyClass);
        owlOnPropertyProperty.setValueType(ValueType.INSTANCE);
        owlOnPropertyProperty.setAllowedClses(Collections.singleton(rdfPropertyClass));

        owlRestrictionClass = createSystemCls(OWLNames.Cls.RESTRICTION, anonymousClassMetaCls);
        owlRestrictionClass.addDirectTemplateSlot(owlOnPropertyProperty);

        Cls metaclass = rdfsNamedClassClass;

        owlAllValuesFromProperty = createSystemSlot(OWLNames.Slot.ALL_VALUES_FROM, rdfPropertyClass);
        owlAllValuesFromClass = createSystemCls(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION,
                                                Collections.singleton(owlRestrictionClass), metaclass);
        owlAllValuesFromClass.addDirectTemplateSlot(owlAllValuesFromProperty);

        owlHasValueProperty = createSystemSlot(OWLNames.Slot.HAS_VALUE, rdfPropertyClass);
        owlHasValueProperty.setAllowsMultipleValues(false);
        owlHasValueClass = createSystemCls(OWLNames.Cls.HAS_VALUE_RESTRICTION,
                                           Collections.singleton(owlRestrictionClass), metaclass);
        owlHasValueClass.addDirectTemplateSlot(owlHasValueProperty);

        final RDFSDatatype cardiRange = getXSDint();
        owlMaxCardinalityProperty = createSystemSlot(OWLNames.Slot.MAX_CARDINALITY, rdfPropertyClass);
        owlMaxCardinalityProperty.setAllowsMultipleValues(false);
        owlMaxCardinalityProperty.setValueType(ValueType.INTEGER);
        owlMaxCardinalityProperty.setDirectOwnSlotValue(rdfsRangeProperty, cardiRange);
        owlMaxCardinalityClass = createSystemCls(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION,
                                                 Collections.singleton(owlRestrictionClass), metaclass);
        owlMaxCardinalityClass.addDirectTemplateSlot(owlMaxCardinalityProperty);

        owlMinCardinalityProperty = createSystemSlot(OWLNames.Slot.MIN_CARDINALITY, rdfPropertyClass);
        owlMinCardinalityProperty.setAllowsMultipleValues(false);
        owlMinCardinalityProperty.setValueType(ValueType.INTEGER);
        owlMinCardinalityProperty.setDirectOwnSlotValue(rdfsRangeProperty, cardiRange);
        owlMinCardinalityClass = createSystemCls(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION,
                                                 Collections.singleton(owlRestrictionClass), metaclass);
        owlMinCardinalityClass.addDirectTemplateSlot(owlMinCardinalityProperty);

        owlCardinalityProperty = createSystemSlot(OWLNames.Slot.CARDINALITY, rdfPropertyClass);
        owlCardinalityProperty.setAllowsMultipleValues(false);
        owlCardinalityProperty.setValueType(ValueType.INTEGER);
        owlCardinalityProperty.setDirectOwnSlotValue(rdfsRangeProperty, cardiRange);
        owlCardinalityClass = createSystemCls(OWLNames.Cls.CARDINALITY_RESTRICTION,
                                              Collections.singleton(owlRestrictionClass), metaclass);
        owlCardinalityClass.addDirectTemplateSlot(owlCardinalityProperty);

        owlValuesFromProperty = createSystemSlot(OWLNames.Slot.VALUES_FROM, rdfPropertyClass);
        owlValuesFromProperty.setAllowsMultipleValues(false);
        owlValuesFromProperty.setValueType(ValueType.INSTANCE);
        owlMaxCardinalityClass.addDirectTemplateSlot(owlValuesFromProperty);
        owlMinCardinalityClass.addDirectTemplateSlot(owlValuesFromProperty);
        owlCardinalityClass.addDirectTemplateSlot(owlValuesFromProperty);

        owlSomeValuesFromProperty = createSystemSlot(OWLNames.Slot.SOME_VALUES_FROM, rdfPropertyClass);
        owlSomeValuesFromClass = createSystemCls(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION,
                                                 Collections.singleton(owlRestrictionClass), metaclass);
        owlSomeValuesFromClass.addDirectTemplateSlot(owlSomeValuesFromProperty);
    }


    private void initInferredSlots() {

        protegeClassificationStatusProperty = createSystemSlot(ProtegeNames.Slot.CLASSIFICATION_STATUS, rdfPropertyClass);
        protegeClassificationStatusProperty.setAllowsMultipleValues(false);
        protegeClassificationStatusProperty.setValueType(ValueType.BOOLEAN);
        rdfsNamedClassClass.addDirectTemplateSlot(protegeClassificationStatusProperty);

        Cls standardCls = getCls(OWLNames.Cls.OWL_CLASS);
        protegeInferredSubclassesProperty = createInstanceSlot(ProtegeNames.Slot.INFERRED_SUBCLASSES, rdfPropertyClass, standardCls);
        protegeInferredSubclassesProperty.setAllowsMultipleValues(true);
        protegeInferredSuperclassesProperty = createInstanceSlot(ProtegeNames.Slot.INFERRED_SUPERCLASSES, rdfPropertyClass, standardCls);
        protegeInferredSuperclassesProperty.setAllowsMultipleValues(true);
        rdfsNamedClassClass.addDirectTemplateSlot(protegeInferredSubclassesProperty);
        rdfsNamedClassClass.addDirectTemplateSlot(protegeInferredSuperclassesProperty);

        protegeInferredTypeProperty = createInstanceSlot(ProtegeNames.Slot.INFERRED_TYPE, rdfPropertyClass, standardCls);
        protegeInferredTypeProperty.setAllowsMultipleValues(true);
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
        getOWLFrameStore().copyFacetValuesIntoNamedClses();
    }


    public String createNewResourceName(String partialLocalName) {
        if (getTripleStoreModel().getActiveTripleStore() != getTripleStoreModel().getTopTripleStore()) {
            String namespace = getTripleStoreModel().getActiveTripleStore().getDefaultNamespace();
            if (namespace != null) {
                String prefix = getNamespaceManager().getPrefix(namespace);
                if (prefix != null && prefix.length() > 0) {
                    return getUniqueFrameName(prefix + ":" + partialLocalName);
                }
            }
        }
        return getUniqueFrameName(partialLocalName);
    }


    public OWLAllDifferent createOWLAllDifferent() {
        return (OWLAllDifferent) createInstance(null, owlAllDifferentClass);
    }


    public OWLAllValuesFrom createOWLAllValuesFrom() {
        Collection parents = CollectionUtilities.createCollection(anonymousRootCls);
        return (OWLAllValuesFrom) createCls(null, parents, owlAllValuesFromClass, false);
    }


    public OWLAllValuesFrom createOWLAllValuesFrom(RDFProperty property, RDFResource filler) {
        OWLAllValuesFrom result = createOWLAllValuesFrom();
        result.setOnProperty(property);
        result.setFiller(filler);
        return result;
    }


    public OWLAllValuesFrom createOWLAllValuesFrom(RDFProperty property, RDFSLiteral[] oneOfValues) {
        OWLAllValuesFrom allRestriction = createOWLAllValuesFrom();
        allRestriction.setOnProperty((RDFProperty) property);
        OWLDataRange dataRange = createOWLDataRange(oneOfValues);
        allRestriction.setFiller(dataRange);
        return allRestriction;
    }


    public OWLDatatypeProperty createAnnotationOWLDatatypeProperty(String name) {
        OWLDatatypeProperty property = createOWLDatatypeProperty(name);
        ((Slot) property).setAllowsMultipleValues(true);
        //getRootCls().addDirectTemplateSlot(property);
        //property.setDomain(getOWLThingClass());
        ((Slot) property).addDirectType(owlAnnotationPropertyClass);
        return property;
    }


    public OWLObjectProperty createAnnotationOWLObjectProperty(String name) {
        OWLObjectProperty property = createOWLObjectProperty(name);
        ((Slot) property).setAllowsMultipleValues(true);
        //getRootCls().addDirectTemplateSlot(property);
        //property.setDomain(getOWLThingClass());
        ((Slot) property).addDirectType(owlAnnotationPropertyClass);
        return property;
    }


    public OWLCardinality createOWLCardinality() {
        Collection parents = CollectionUtilities.createCollection(anonymousRootCls);
        return (OWLCardinality) createCls(null, parents, owlCardinalityClass, false);
    }


    public OWLCardinality createOWLCardinality(RDFProperty property, int value) {
        OWLCardinality cardiRestriction = createOWLCardinality();
        cardiRestriction.setOnProperty((RDFProperty) property);
        cardiRestriction.setCardinality(value);
        return cardiRestriction;
    }


    public OWLCardinality createOWLCardinality(RDFProperty property, int value, RDFSClass qualifier) {
        OWLCardinality owlCardinality = createOWLCardinality(property, value);
        owlCardinality.setValuesFrom(qualifier);
        return owlCardinality;
    }


    public synchronized Cls createCls(FrameID id,
                                      String name,
                                      Collection directSuperclasses,
                                      Collection directTypes,
                                      boolean loadDefaults) {
        if (bootstrapped) {
            if (name == null) {
                if (isDefaultAnonymousType(directTypes)) {
                    name = getNextAnonymousResourceName();
                }
                else {
                    name = createUniqueNewFrameName(DEFAULT_CLASS_NAME);
                }
            }
            // name = getValidNamespaceFrameName(name);
        }
        return super.createCls(id, name, directSuperclasses, directTypes, loadDefaults);
    }


    public OWLComplementClass createOWLComplementClass() {
        Collection parents = CollectionUtilities.createCollection(anonymousRootCls);
        return (OWLComplementClass) createCls(null, parents, owlComplementClassClass, false);
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
        return createOWLDatatypeProperty(name, (OWLNamedClass) owlDatatypePropertyClass);
    }


    public OWLDatatypeProperty createOWLDatatypeProperty(String name, OWLNamedClass metaCls) {
        if (name == null) {
            name = createUniqueNewFrameName("DatatypeProperty");
        }
        OWLDatatypeProperty slot = null;
        if (inInit) {
            slot = (OWLDatatypeProperty) createSystemSlot(name, metaCls);
            ((Cls) owlThingClass).addDirectTemplateSlot(slot);
        }
        else {
            slot = (OWLDatatypeProperty) createSlot(name, metaCls, loadDefaults);
        }
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


    private void createDefaultOWLOntology() {
        if (getDefaultOWLOntology() == null) {
            createDefaultOWLOntologyReally();
        }
    }


    protected void createDefaultOWLOntologyReally() {
        Instance ontology = createInstance(":", owlOntologyClass);
        ontology.setDirectOwnSlotValue(rdfTypeProperty, owlOntologyClass);
        getNamespaceManager().setDefaultNamespace(OWLNamespaceManager.DEFAULT_DEFAULT_NAMESPACE);
        getNamespaceManager().setPrefix(RDF.getURI(), RDFNames.RDF_PREFIX);
        getNamespaceManager().setPrefix(RDFS.getURI(), RDFSNames.RDFS_PREFIX);
        getNamespaceManager().setPrefix(OWL.NS, OWLNames.OWL_PREFIX);
        getNamespaceManager().setPrefix(XSDDatatype.XSD + "#", RDFNames.XSD_PREFIX);
    }


    protected FrameStoreManager createFrameStoreManager() {
        return new OWLFrameStoreManager(this);
    }


    public OWLEnumeratedClass createOWLEnumeratedClass() {
        Collection parents = CollectionUtilities.createCollection(anonymousRootCls);
        return (OWLEnumeratedClass) createCls(null, parents, owlEnumeratedClassClass, false);
    }


    public OWLEnumeratedClass createOWLEnumeratedClass(Collection instances) {
        OWLEnumeratedClass enumerationCls = createOWLEnumeratedClass();
        enumerationCls.setOneOf(instances);
        return enumerationCls;
    }


    /**
     * @deprecated
     */
    public RDFExternalResource createRDFExternalResource(String uri) {
        throw new RuntimeException("The class RDFExternalResource has been replaced with RDFUntypedResource");
    }


    public OWLHasValue createOWLHasValue() {
        Collection parents = CollectionUtilities.createCollection(anonymousRootCls);
        return (OWLHasValue) createCls(null, parents, owlHasValueClass, false);
    }


    public OWLHasValue createOWLHasValue(RDFProperty property, Object value) {
        OWLHasValue restriction = createOWLHasValue();
        restriction.setOnProperty((RDFProperty) property);
        restriction.setHasValue(value);
        return restriction;
    }


    public synchronized Instance createInstance(FrameID id, String name, Collection directTypes, boolean initializeDefaults) {
        if (name == null) {
            if (isDefaultAnonymousType(directTypes)) {
                name = getNextAnonymousResourceName();
            }
            else {
                Cls firstType = (Cls) directTypes.iterator().next();
                if (firstType instanceof RDFSNamedClass) {
                    name = createNewResourceName(((RDFSClass) firstType).getLocalName());
                }
            }
        }
        return super.createInstance(id, name, directTypes, initializeDefaults);
    }


    private Slot createInstanceSlot(String name, Cls directType, Cls allowedCls) {
        Slot slot = createSystemSlot(name, directType);
        slot.setValueType(ValueType.INSTANCE);
        slot.setAllowedClses(CollectionUtilities.createCollection(allowedCls));
        return slot;
    }


    public OWLIntersectionClass createOWLIntersectionClass() {
        Collection parents = CollectionUtilities.createCollection(anonymousRootCls);
        return (OWLIntersectionClass) createCls(null, parents, owlIntersectionClassClass, false);
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
        return (RDFList) rdfListClass.createDirectInstance(null);
    }


    public RDFList createRDFList(Iterator values) {
        return createListInstance(values, rdfListClass);
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
        return (RDFSDatatype) rdfsDatatypeClass.createDirectInstance(name);
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
            currentNode.setRest((RDFList) rdfNilIndividual);
            return li;
        }
    }


    public OWLMaxCardinality createOWLMaxCardinality() {
        Collection parents = Collections.singleton(getAnonymousRootCls());
        return (OWLMaxCardinality) createCls(null, parents, owlMaxCardinalityClass, false);
    }


    public OWLMaxCardinality createOWLMaxCardinality(RDFProperty property, int value) {
        OWLMaxCardinality maxCardiRestriction = createOWLMaxCardinality();
        maxCardiRestriction.setOnProperty((RDFProperty) property);
        maxCardiRestriction.setCardinality(value);
        return maxCardiRestriction;
    }


    public OWLMaxCardinality createOWLMaxCardinality(RDFProperty property, int value, RDFSClass qualifier) {
        OWLMaxCardinality owlMaxCardinality = createOWLMaxCardinality(property, value);
        owlMaxCardinality.setValuesFrom(qualifier);
        return owlMaxCardinality;
    }


    public OWLMinCardinality createOWLMinCardinality() {
        Collection parents = Collections.singleton(anonymousRootCls);
        return (OWLMinCardinality) createCls(null, parents, owlMinCardinalityClass, false);
    }


    public OWLMinCardinality createOWLMinCardinality(RDFProperty property, int value) {
        OWLMinCardinality minCardiRestriction = createOWLMinCardinality();
        minCardiRestriction.setOnProperty((RDFProperty) property);
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
        return (OWLNamedClass) createCls(name, getRootClses(), owlNamedClassClass, loadDefaults);
    }


    public OWLNamedClass createOWLNamedClass(String name, OWLNamedClass metaCls) {
        return (OWLNamedClass) createCls(name, getRootClses(), metaCls, loadDefaults);
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
        return createOWLObjectProperty(name, (OWLNamedClass) owlObjectPropertyClass);
    }


    public OWLObjectProperty createOWLObjectProperty(String name, OWLNamedClass metaCls) {
        if (name == null) {
            name = createUniqueNewFrameName("ObjectProperty");
        }
        OWLObjectProperty result = null;
        if (inInit) {
            result = (OWLObjectProperty) createSystemSlot(name, metaCls);
        }
        else {
            result = (OWLObjectProperty) createSlot(name, metaCls, loadDefaults);
        }
        ((Slot) result).setAllowsMultipleValues(true);
        ((Slot) result).setValueType(ValueType.INSTANCE);
        return result;
    }


    public OWLObjectProperty createOWLObjectProperty(String name, Collection allowedClasses) {
        OWLObjectProperty slot = createOWLObjectProperty(name);
        slot.setUnionRangeClasses(allowedClasses);
        return slot;
    }


    public OWLOntology createOWLOntology(String prefix) {
        return (OWLOntology) createInstance(prefix + ":", owlOntologyClass);
    }


    /**
     * @deprecated
     */
    public OWLOntology createOWLOntology(String name, String uri) {
        String prefix = getNamespaceManager().getPrefix(uri);
        return createOWLOntology(prefix);
    }


    public RDFSNamedClass createRDFSNamedClass(String name) {
        return createRDFSNamedClass(name, true);
    }


    public RDFSNamedClass createRDFSNamedClass(String name, boolean loadDefaults) {
        return (RDFSNamedClass) createCls(name, getRootClses(), rdfsNamedClassClass, loadDefaults);
    }


    public RDFSNamedClass createRDFSNamedClass(String name, Collection parents, RDFSClass rdfType) {
        return (RDFSNamedClass) createCls(name, parents, rdfType);
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
        Instance instance = rdfExternalResourceClass.createDirectInstance(uri);
        return (RDFUntypedResource) instance;
    }


    public RDFProperty createRDFProperty(String name) {
        if (name == null) {
            name = createUniqueNewFrameName("RDFProperty");
        }
        RDFProperty property = (RDFProperty) createSlot(name, rdfPropertyClass, loadDefaults);
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
        return (RDFSNamedClass) createCls(name, Collections.singleton(superclass));
    }


    public RDFSNamedClass createSubclass(String name, Collection superclasses) {
        return (RDFSNamedClass) createCls(name, superclasses);
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom() {
        Collection parents = CollectionUtilities.createCollection(anonymousRootCls);
        return (OWLSomeValuesFrom) createCls(null, parents, owlSomeValuesFromClass, false);
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom(RDFProperty property, RDFResource filler) {
        OWLSomeValuesFrom someRestriction = createOWLSomeValuesFrom();
        someRestriction.setOnProperty((RDFProperty) property);
        someRestriction.setFiller(filler);
        return someRestriction;
    }


    public OWLSomeValuesFrom createOWLSomeValuesFrom(RDFProperty property, RDFSLiteral[] oneOfValues) {
        OWLSomeValuesFrom someRestriction = createOWLSomeValuesFrom();
        someRestriction.setOnProperty((RDFProperty) property);
        OWLDataRange dataRange = createOWLDataRange(oneOfValues);
        someRestriction.setFiller(dataRange);
        return someRestriction;
    }


    public RDFProperty createSubproperty(String name, RDFProperty superProperty) {
        return (RDFProperty) createSlot(name, superProperty.getProtegeType(), Collections.singleton(superProperty), true);
    }


    public boolean endTransaction() {
        return commitTransaction();
    }


    public QueryResults executeSPARQLQuery(String partialQueryText) throws Exception {
        String queryString = SPARQLQueryResults.createPrefixDeclarations(this) + partialQueryText;
        return SPARQLQueryResults.create(this, queryString);
    }


    public OWLUnionClass createOWLUnionClass() {
        Collection parents = CollectionUtilities.createCollection(anonymousRootCls);
        return (OWLUnionClass) createCls(null, parents, owlUnionClassClass, false);
    }


    public OWLUnionClass createOWLUnionClass(Collection clses) {
        OWLUnionClass unionCls = createOWLUnionClass();
        for (Iterator it = clses.iterator(); it.hasNext();) {
            RDFSClass cls = (RDFSClass) it.next();
            unionCls.addOperand(cls);
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


    public void deleteCls(Cls cls) {
        if (cls instanceof OWLAnonymousClass && cls.getDirectSubclassCount() == 1) {
            Cls subCls = (Cls) cls.getDirectSubclasses().iterator().next();
            subCls.removeDirectSuperclass(cls);  // Will call delete again
            return;
        }
        /*if (cls instanceof OWLEnumeratedClass && cls.getDirectInstanceCount() > 0) {
            for (Iterator it = new ArrayList(cls.getDirectInstances()).iterator(); it.hasNext();) {
                Instance instance = (Instance) it.next();
                instance.removeDirectType(cls);
            }
        } */
        super.deleteCls(cls);
    }


    /**
     * Makes sure that the Protege meta ontology is imported in an ontology tag
     * that has rdf:about="".
     */
    public boolean ensureProtegeMetaOntologyImported() {
        OWLOntology owlOntology = getDefaultOWLOntology();
        for (Iterator imports = owlOntology.getImports().iterator(); imports.hasNext();) {
            String im = (String) imports.next();
            if (im.equals(ProtegeNames.FILE)) {
                return false;  // Already there
            }
        }
        owlOntology.addImports(ProtegeNames.FILE);
        ensureProtegePrefixExists();
        return true;
    }


    private void ensureProtegePrefixExists() {
        if (getNamespaceManager().getPrefix(ProtegeNames.NS) == null) {
            String prefix = "protege";
            getNamespaceManager().setPrefix(ProtegeNames.NS, prefix);
            getNamespaceManager().setPrefix(XSPNames.NS, XSPNames.DEFAULT_PREFIX);
        }
    }


    public Collection getOWLAllDifferents() {
        Cls metaCls = getOWLAllDifferentClass();
        return metaCls.getDirectInstances();
    }


    public RDFSNamedClass getOWLAllDifferentClass() {
        return (RDFSNamedClass) owlAllDifferentClass;
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


    /**
     * @deprecated
     */
    public Cls getAnonymousRootCls() {
        return anonymousRootCls;
    }


    public synchronized String getBrowserText(Instance instance) {
        if (instance instanceof RDFResource) {
            if (instance instanceof OWLAnonymousClass) {
                return instance.getBrowserText();
            }
            else if (instance.isDeleted()) {
                return "<deleted>";
            }
            else {
                Cls directType = instance.getDirectType();
                if (getProject() == null) {
                    return getName(instance);
                }
                else if (directType == null) {
                    return getMissingTypeString(instance);
                }
                else {
                    BrowserSlotPattern slotPattern = getProject().getBrowserSlotPattern(instance.getDirectType());
                    if (slotPattern == null) {
                        return getDisplaySlotNotSetString(instance);
                    }
                    else {
                        String value = null;
                        Collection elements = slotPattern.getElements();
                        final Slot slot = slotPattern.getFirstSlot();
                        if (elements.size() == 1 && slot != null &&
                            !slot.equals(nameSlot) &&
                            slot.getValueType() == ValueType.STRING) {
                            String defaultLanguage = getDefaultLanguage();
                            Collection values = null;
                            if (slot instanceof RDFProperty) {
                                values = ((RDFResource) instance).getPropertyValues((RDFProperty) slot);
                            }
                            else {
                                values = instance.getOwnSlotValues(slot);
                            }
                            if (defaultLanguage != null) {
                                Iterator it = values.iterator();
                                while (it.hasNext()) {
                                    Object rawText = it.next();
                                    if (rawText instanceof RDFSLiteral) {
                                        RDFSLiteral literal = (RDFSLiteral) rawText;
                                        if (defaultLanguage.equals(literal.getLanguage())) {
                                            value = literal.getString();
                                            break;
                                        }
                                    }
                                }
                            }
                            if (value == null) {
                                Iterator it = values.iterator();
                                while (it.hasNext()) {
                                    Object rawText = it.next();
                                    if (rawText instanceof RDFSLiteral) {
                                        RDFSLiteral literal = (RDFSLiteral) rawText;
                                        if (literal.getLanguage() == null) {
                                            value = literal.getString();
                                            break;
                                        }
                                    }
                                    else {
                                        value = rawText.toString();
                                        break;
                                    }
                                }
                            }
                        }
                        else {
                            value = slotPattern.getBrowserText(instance);
                        }
                        if (value == null) {
                            value = getDisplaySlotPatternValueNotSetString(instance, slotPattern);
                        }
                        return value;
                    }
                }
            }
        }
        else {
            return super.getBrowserText(instance);
        }
    }


    public Collection getChangedInferredClasses() {
        return getClsesWithClassificationStatus(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED);
    }


    public RDFSNamedClass getCommonSuperclass(Collection classes) {
        Set cs = new HashSet();
        Iterator it = classes.iterator();
        RDFSNamedClass firstClass = (RDFSNamedClass) it.next();
        Collection supers = firstClass.getSuperclasses(true);
        for (Iterator sit = supers.iterator(); sit.hasNext();) {
            RDFSClass superclass = (RDFSClass) sit.next();
            if (superclass instanceof RDFSNamedClass) {
                cs.add(superclass);
            }
        }

        while (it.hasNext()) {
            RDFSNamedClass namedClass = (RDFSNamedClass) it.next();
            Set ss = new HashSet(namedClass.getSuperclasses(true));
            for (Iterator sit = cs.iterator(); sit.hasNext();) {
                RDFSNamedClass c = (RDFSNamedClass) sit.next();
                if (!ss.contains(c)) {
                    sit.remove();
                }
            }
        }

        List copy = new ArrayList(cs);
        for (Iterator cit = copy.iterator(); cit.hasNext();) {
            RDFSNamedClass namedClass = (RDFSNamedClass) cit.next();
            cs.removeAll(namedClass.getSuperclasses(true));
        }
        return (RDFSNamedClass) cs.iterator().next();
    }


    public RDFProperty getProtegeClassificationStatusProperty() {
        return (RDFProperty) protegeClassificationStatusProperty;
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
        return (OWLDatatypeProperty) getSlot(name);
    }


    public OWLNamedClass getOWLDatatypePropertyClass() {
        return (OWLNamedClass) owlDatatypePropertyClass;
    }


    public RDFSNamedClass getOWLDeprecatedClassClass() {
        return (RDFSNamedClass) owlDeprecatedClassClass;
    }


    public String getDefaultLanguage() {
        if (isProtegeMetaOntologyImported()) {
            RDFProperty metaSlot = getRDFProperty(ProtegeNames.getDefaultLanguageSlotName());
            if (metaSlot != null) {
                OWLOntology oi = getDefaultOWLOntology();
                if (oi != null) {
                    String value = (String) oi.getPropertyValue(metaSlot);
                    if (value != null && value.length() > 0) {
                        return value;
                    }
                }
            }
        }
        return null;
    }


    public OWLOntology getDefaultOWLOntology() {
        return (OWLOntology) getFrame(":");
    }


    public Collection getDomainlessProperties() {
        return getRootCls().getDirectTemplateSlots();
    }


    public Set getFloatDatatypes() {
        return floatDatatypes;
    }


    public Set getIntegerDatatypes() {
        return integerDatatypes;
    }


    public RDFProperty getOWLDifferentFromProperty() {
        return (RDFProperty) owlDifferentFromProperty;
    }


    public RDFProperty getOWLDisjointWithProperty() {
        return (RDFProperty) owlDisjointWithProperty;
    }


    public RDFProperty getOWLEquivalentPropertyProperty() {
        return (RDFProperty) owlEquivalentPropertyProperty;
    }


    public RDFProperty getOWLOneOfProperty() {
        return (RDFProperty) owlOneOfProperty;
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
    public RDFExternalResource getRDFExternalResource(String uri) {
        for (Iterator it = getRDFUntypedResourcesClass().getInstances(false).iterator(); it.hasNext();) {
            RDFExternalResource eri = (RDFExternalResource) it.next();
            if (uri.equals(eri.getResourceURI())) {
                return eri;
            }
        }
        return null;
    }


    /**
     * @deprecated
     */
    public RDFSClass getRDFExternalResourceClass() {
        return (RDFSNamedClass) rdfExternalResourceClass;
    }


    public RDFProperty getRDFFirstProperty() {
        return (RDFProperty) rdfFirstProperty;
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


    public RDFSNamedClass getOWLAnnotationPropertyClass() {
        return (RDFSNamedClass) owlAnnotationPropertyClass;
    }


    public OWLClassParser getOWLClassParser() {
        return getOWLClassDisplay().getParser();
    }


    public OWLClassDisplay getOWLClassDisplay() {
        return owlClassRenderer;
    }


    public RDFProperty getProtegeInferredSubclassesProperty() {
        return (RDFProperty) protegeInferredSubclassesProperty;
    }


    public RDFProperty getProtegeInferredSuperclassesProperty() {
        return (RDFProperty) protegeInferredSuperclassesProperty;
    }


    public RDFProperty getRDFSDomainProperty() {
        return (RDFProperty) rdfsDomainProperty;
    }


    public RDFProperty getRDFSIsDefinedByProperty() {
        return (RDFProperty) rdfsIsDefinedByProperty;
    }


    public RDFProperty getRDFSLabelProperty() {
        return (RDFProperty) rdfsLabelProperty;
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


    public static Collection getRDFResources(KnowledgeBase kb, Collection frames) {
        ArrayList result = new ArrayList();
        for (Iterator it = frames.iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (frame instanceof RDFResource) {
                result.add(frame);
            }
        }
        removeProtegeSystemResources(kb, result);
        return result;
    }


    public RDFSNamedClass getRDFListClass() {
        return (RDFSNamedClass) rdfListClass;
    }


    public RDFUntypedResource getRDFUntypedResource(String uri, boolean createOnDemand) {
        RDFUntypedResource r = (RDFUntypedResource) getFrame(uri);
        if (createOnDemand && r == null) {
            r = createRDFUntypedResource(uri);
        }
        return r;
    }


    public RDFSNamedClass getRDFUntypedResourcesClass() {
        return (RDFSNamedClass) rdfExternalResourceClass;
    }


    public Collection getResourceNameMatches(String nameExpression, int maxMatches) {
        Collection frames = getFrameNameMatches(nameExpression, maxMatches);
        return getRDFResources(this, frames);
    }


    public RDFSNamedClass getOWLDataRangeClass() {
        return (RDFSNamedClass) owlDataRangeClass;
    }


    public RDFProperty getOWLIntersectionOfProperty() {
        return (RDFProperty) owlIntersectionOfProperty;
    }


    public OWLNamedClass getOWLNamedClass(String name) {
        return (OWLNamedClass) getCls(name);
    }


    public OWLNamedClass getOWLNamedClassClass() {
        return (OWLNamedClass) owlNamedClassClass;
    }


    public RDFProperty getOWLValuesFromProperty() {
        return (RDFProperty) owlValuesFromProperty;
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


    public RDFList getRDFNil() {
        return (RDFList) rdfNilIndividual;
    }


    public Collection getRDFProperties() {
        return getRDFResources(this, getSlots());
    }


    public RDFResource getRDFResource(String name) {
        return (RDFResource) getFrame(name);
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
        return (OWLIndividual) getFrame(name);
    }


    public OWLNamedClass getOWLNothing() {
        return (OWLNamedClass) owlNothingClass;
    }


    public Collection getOWLRestrictionsOnProperty(RDFProperty property) {
        return getFramesWithValue(owlOnPropertyProperty, null, false, property);
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
        return (OWLObjectProperty) getSlot(name);
    }


    public OWLNamedClass getOWLObjectPropertyClass() {
        return (OWLNamedClass) owlObjectPropertyClass;
    }


    public Collection getOWLOntologies() {
        return owlOntologyClass.getInstances();
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
        String uriString = uri.toString();
        if (!uriString.endsWith("/") && !uriString.endsWith("#")) {
            uriString += "#";
        }
        String prefix = getNamespaceManager().getPrefix(uriString);
        String name = prefix != null ? (prefix + ":") : ":";
        return (RDFResource) getRDFResource(name);
    }

    public OWLNamedClass getOWLOntologyClass() {
        return (OWLNamedClass) owlOntologyClass;
    }


    public Collection getOWLOntologyProperties() {
        return Arrays.asList(new Slot[]{
                owlBackwardCompatibleWithProperty,
                owlIncompatibleWithProperty,
                owlPriorVersionProperty
        });
    }


    /**
     * @deprecated use getRDFSClasses instead
     */
    public Collection getOWLClasses() {
        return getRDFSClasses();
    }

    /*public Collection getOWLClasses() {
       Collection result = new ArrayList();
       for (Iterator it = getClses().iterator(); it.hasNext();) {
           Cls cls = (Cls) it.next();
           if (cls instanceof RDFSClass) {
               result.add(cls);
           }
       }
       return result;
   } */


    public OWLFrameStore getOWLFrameStore() {
        return owlFrameStore;
    }


    public RDFSNamedClass getOWLFunctionalPropertyClass() {
        return (RDFSNamedClass) owlFunctionalPropertyClass;
    }


    public RDFSNamedClass getOWLInverseFunctionalPropertyClass() {
        return (RDFSNamedClass) owlInverseFunctionalPropertyClass;
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
        return (OWLProperty) getSlot(name);
    }


    public Collection getOWLSystemResources() {
        Collection result = new ArrayList(Arrays.asList(getOWLSystemFramesArray()));
        result.addAll(rdfsDatatypeClass.getDirectInstances());
        return result;
    }


    /**
     * Gets the array of OWL System frames.
     * <p/>
     * An important side effect of this method is that it initializes
     * several of the variables used by the AbstractOWLModel.
     */

    protected Frame[] getOWLSystemFramesArray() {
        nameSlot = getSlot(Model.Slot.NAME);
        return new Frame[]{
                owlAllValuesFromClass = getCls(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION),
                anonymousClassMetaCls = getCls(OWLNames.Cls.ANONYMOUS_CLASS),
                anonymousRootCls = getCls(OWLNames.Cls.ANONYMOUS_ROOT),
                owlCardinalityClass = getCls(OWLNames.Cls.CARDINALITY_RESTRICTION),
                owlComplementClassClass = getCls(OWLNames.Cls.COMPLEMENT_CLASS),
                owlEnumeratedClassClass = getCls(OWLNames.Cls.ENUMERATED_CLASS),
                owlHasValueClass = getCls(OWLNames.Cls.HAS_VALUE_RESTRICTION),
                owlIntersectionClassClass = getCls(OWLNames.Cls.INTERSECTION_CLASS),
                owlLogicalClassClass = getCls(OWLNames.Cls.LOGICAL_CLASS),
                owlMaxCardinalityClass = getCls(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION),
                owlMinCardinalityClass = getCls(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION),
                owlOntologyPrefixesProperty = getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES),
                owlRestrictionClass = getCls(OWLNames.Cls.RESTRICTION),
                owlSomeValuesFromClass = getCls(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION),
                owlUnionClassClass = getCls(OWLNames.Cls.UNION_CLASS),
                rdfExternalResourceClass = getCls(RDFNames.Cls.EXTERNAL_RESOURCE),
                owlResourceURIProperty = getSlot(OWLNames.Slot.RESOURCE_URI),

                protegeClassificationStatusProperty = getSlot(ProtegeNames.Slot.CLASSIFICATION_STATUS),
                protegeInferredTypeProperty = getSlot(ProtegeNames.Slot.INFERRED_TYPE),
                protegeInferredSubclassesProperty = getSlot(ProtegeNames.Slot.INFERRED_SUBCLASSES),
                protegeInferredSuperclassesProperty = getSlot(ProtegeNames.Slot.INFERRED_SUPERCLASSES),

                owlAllDifferentClass = getCls(OWLNames.Cls.ALL_DIFFERENT),
                owlAllValuesFromProperty = getSlot(OWLNames.Slot.ALL_VALUES_FROM),
                owlAnnotationPropertyClass = getCls(OWLNames.Cls.ANNOTATION_PROPERTY),
                owlBackwardCompatibleWithProperty = getSlot(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH),
                owlCardinalityProperty = getSlot(OWLNames.Slot.CARDINALITY),
                rdfsCommentProperty = getSlot(RDFSNames.Slot.COMMENT),
                owlComplementOfProperty = getSlot(OWLNames.Slot.COMPLEMENT_OF),
                owlDataRangeClass = getCls(OWLNames.Cls.DATA_RANGE),
                owlDatatypePropertyClass = getCls(OWLNames.Cls.DATATYPE_PROPERTY),
                rdfsDomainProperty = getSlot(RDFSNames.Slot.DOMAIN),
                owlDeprecatedClassClass = getCls(OWLNames.Cls.DEPRECATED_CLASS),
                owlDeprecatedPropertyClass = getCls(OWLNames.Cls.DEPRECATED_PROPERTY),
                owlFunctionalPropertyClass = getCls(OWLNames.Cls.FUNCTIONAL_PROPERTY),
                owlInverseFunctionalPropertyClass = getCls(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY),
                owlSymmetricPropertyClass = getCls(OWLNames.Cls.SYMMETRIC_PROPERTY),
                owlTransitivePropertyClass = getCls(OWLNames.Cls.TRANSITIVE_PROPERTY),
                owlDifferentFromProperty = getSlot(OWLNames.Slot.DIFFERENT_FROM),
                owlDisjointWithProperty = getSlot(OWLNames.Slot.DISJOINT_WITH),
                owlDistinctMembersProperty = getSlot(OWLNames.Slot.DISTINCT_MEMBERS),
                owlEquivalentClassProperty = getSlot(OWLNames.Slot.EQUIVALENT_CLASS),
                owlEquivalentPropertyProperty = getSlot(OWLNames.Slot.EQUIVALENT_PROPERTY),
                rdfFirstProperty = getSlot(RDFNames.Slot.FIRST),
                owlHasValueProperty = getSlot(OWLNames.Slot.HAS_VALUE),
                owlIncompatibleWithProperty = getSlot(OWLNames.Slot.INCOMPATIBLE_WITH),
                owlIntersectionOfProperty = getSlot(OWLNames.Slot.INTERSECTION_OF),
                owlValuesFromProperty = getSlot(OWLNames.Slot.VALUES_FROM),
                rdfsIsDefinedByProperty = getSlot(RDFSNames.Slot.IS_DEFINED_BY),
                rdfsLabelProperty = getSlot(RDFSNames.Slot.LABEL),
                rdfListClass = getCls(RDFNames.Cls.LIST),
                rdfsLiteralClass = getCls(RDFSNames.Cls.LITERAL),
                rdfsSubPropertyOfProperty = getSlot(RDFSNames.Slot.SUB_PROPERTY_OF),
                rdfsSubClassOfProperty = getSlot(RDFSNames.Slot.SUB_CLASS_OF),
                owlInverseOfProperty = getSlot(OWLNames.Slot.INVERSE_OF),
                owlMaxCardinalityProperty = getSlot(OWLNames.Slot.MAX_CARDINALITY),
                owlMinCardinalityProperty = getSlot(OWLNames.Slot.MIN_CARDINALITY),
                owlNamedClassClass = getCls(OWLNames.Cls.NAMED_CLASS),
                rdfNilIndividual = getInstance(RDFNames.Instance.NIL),
                owlNothingClass = getCls(OWLNames.Cls.NOTHING),
                owlObjectPropertyClass = getCls(OWLNames.Cls.OBJECT_PROPERTY),
                owlOnPropertyProperty = getSlot(OWLNames.Slot.ON_PROPERTY),
                owlOntologyClass = getCls(OWLNames.Cls.ONTOLOGY),
                owlImportsProperty = getSlot(OWLNames.Slot.IMPORTS),
                owlClassMetaCls = getCls(OWLNames.Cls.OWL_CLASS),
                owlOneOfProperty = getSlot(OWLNames.Slot.ONE_OF),
                rdfPropertyClass = getCls(RDFNames.Cls.PROPERTY),
                owlPriorVersionProperty = getSlot(OWLNames.Slot.PRIOR_VERSION),
                rdfObjectProperty = getSlot(RDFNames.Slot.OBJECT),
                rdfPredicateProperty = getSlot(RDFNames.Slot.PREDICATE),
                rdfsRangeProperty = getSlot(RDFSNames.Slot.RANGE),
                rdfStatementClass = getCls(RDFNames.Cls.STATEMENT),
                rdfSubjectProperty = getSlot(RDFNames.Slot.SUBJECT),
                rdfTypeProperty = getSlot(RDFNames.Slot.TYPE),
                rdfValueProperty = getSlot(RDFNames.Slot.VALUE),
                rdfsNamedClassClass = getCls(RDFSNames.Cls.NAMED_CLASS),
                rdfAltClass = getCls(RDFNames.Cls.ALT),
                rdfBagClass = getCls(RDFNames.Cls.BAG),
                rdfsContainerClass = getCls(RDFSNames.Cls.CONTAINER),
                rdfsDatatypeClass = getCls(RDFSNames.Cls.DATATYPE),
                rdfsMemberProperty = getSlot(RDFSNames.Slot.MEMBER),
                rdfSeqClass = getCls(RDFNames.Cls.SEQ),
                rdfRestSlot = getSlot(RDFNames.Slot.REST),
                owlSameAsProperty = getSlot(OWLNames.Slot.SAME_AS),
                rdfsSeeAlsoProperty = getSlot(RDFSNames.Slot.SEE_ALSO),
                owlSomeValuesFromProperty = getSlot(OWLNames.Slot.SOME_VALUES_FROM),
                owlUnionOfProperty = getSlot(OWLNames.Slot.UNION_OF),
                owlVersionInfoProperty = getSlot(OWLNames.Slot.VERSION_INFO)
        };
    }


    public OWLNamedClass getOWLThingClass() {
        return (OWLNamedClass) getRootCls();
    }


    public RDFProperty getProtegeAllowedParentProperty() {
        return getRDFProperty(ProtegeNames.PROTEGE_PREFIX + ":" + ProtegeNames.ALLOWED_PARENT);
    }


    public ValueType getOWLValueType(String uri) {
        return XMLSchemaDatatypes.getValueType(uri);
    }


    public RDFIndividual getRDFIndividual(String name) {
        return (RDFIndividual) getFrame(name);
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


    public RDFSNamedClass getRDFSNamedClassClass() {
        return (RDFSNamedClass) rdfsNamedClassClass;
    }


    public RDFSNamedClass getRDFSNamedClass(String name) {
        return (RDFSNamedClass) getCls(name);
    }


    public RDFProperty getRDFProperty(String name) {
        return (RDFProperty) getSlot(name);
    }


    public RDFSNamedClass getRDFPropertyClass() {
        return (RDFSNamedClass) rdfPropertyClass;
    }


    public OWLDatatypeProperty getProtegeReadOnlyProperty() {
        if (isProtegeMetaOntologyImported()) {
            return (OWLDatatypeProperty) getSlot(ProtegeNames.getReadOnlySlotName());
        }
        else {
            return null;
        }
    }


    public RDFSDatatype getRDFSDatatypeByName(String name) {
        return (RDFSDatatype) getFrame(name);
    }


    public RDFSDatatype getRDFSDatatypeByURI(String uri) {
        for (Iterator it = getRDFSDatatypes().iterator(); it.hasNext();) {
            RDFSDatatype datatype = (RDFSDatatype) it.next();
            if (uri.equals(datatype.getURI())) {
                return datatype;
            }
        }
        return null;
    }


    public RDFSDatatype getRDFSDatatypeOfValue(Object valueOrRDFSLiteral) {
        if (valueOrRDFSLiteral instanceof RDFSLiteral) {
            return ((RDFSLiteral) valueOrRDFSLiteral).getDatatype();
        }
        else {
            return DefaultRDFSLiteral.create(this, valueOrRDFSLiteral).getDatatype();
        }
    }


    public Collection getRDFSDatatypes() {
        List results = new ArrayList();
        Iterator it = rdfsDatatypeClass.getDirectInstances().iterator();
        while (it.hasNext()) {
            RDFResource datatype = (RDFResource) it.next();
            if (!datatype.isAnonymous()) {
                results.add(datatype);
            }
        }
        return results;
    }


    public RDFProperty getRDFRestProperty() {
        return (RDFProperty) rdfRestSlot;
    }


    public RDFSDatatype getRDFXMLLiteralType() {
        if (xmlLiteralType == null) {
            xmlLiteralType = getRDFSDatatypeByName(RDFNames.XML_LITERAL);
        }
        return xmlLiteralType;
    }


    public RDFSNamedClass[] getOWLRestrictionMetaclasses() {
        return new RDFSNamedClass[]{// Order may be used for sorting and should be preserved
                (RDFSNamedClass) owlAllValuesFromClass,
                (RDFSNamedClass) owlSomeValuesFromClass,
                (RDFSNamedClass) owlHasValueClass,
                (RDFSNamedClass) owlCardinalityClass,
                (RDFSNamedClass) owlMinCardinalityClass,
                (RDFSNamedClass) owlMaxCardinalityClass
        };
    }


    public synchronized Cls getRootCls() {
        if (owlThingClass == null) {
            owlThingClass = new DefaultOWLNamedClass(this, Model.ClsID.THING);
        }
        return owlThingClass;
    }


    public synchronized Cls getRootSlotMetaCls() {
        return getCls(RDFNames.Cls.PROPERTY);
    }


    public RDFProperty getOWLSameAsProperty() {
        return (RDFProperty) owlSameAsProperty;
    }


    public RDFProperty getOWLUnionOfProperty() {
        return (RDFProperty) owlUnionOfProperty;
    }


    public Cls getRDFSClassMetaClassCls() {
        return rdfsNamedClassClass;
    }


    public Cls getOWLNamedClassMetaClassCls() {
        return owlNamedClassClass;
    }


    public Cls getOWLDatatypePropertyMetaClassCls() {
        return owlDatatypePropertyClass;
    }


    public Cls getOWLObjectPropertyMetaClassCls() {
        return owlObjectPropertyClass;
    }


    public Cls getOWLAllDifferentClassCls() {
        return owlAllDifferentClass;
    }


    public Cls getRDFListCls() {
        return rdfListClass;
    }


    public Cls getOWLOntologyCls() {
        return owlOntologyClass;
    }


    public RDFProperty getProtegeSubclassesDisjointProperty() {
        if (isProtegeMetaOntologyImported()) {
            if (protegeSubclassesDisjointProperty == null) {
                protegeSubclassesDisjointProperty = getSlot(ProtegeNames.getSubclassesDisjointSlotName());
            }
            return (RDFProperty) protegeSubclassesDisjointProperty;
        }
        return null;
    }


    public RDFProperty getRDFTypeProperty() {
        return (RDFProperty) rdfTypeProperty;
    }


    public RDFProperty[] getSystemAnnotationProperties() {
        return new RDFProperty[]{
                (RDFProperty) rdfsSeeAlsoProperty,
                (RDFProperty) rdfsIsDefinedByProperty,
                (RDFProperty) rdfsLabelProperty,
                (RDFProperty) owlVersionInfoProperty,
                (RDFProperty) owlBackwardCompatibleWithProperty,
                (RDFProperty) owlIncompatibleWithProperty,
                (RDFProperty) owlPriorVersionProperty,
                (RDFProperty) rdfsCommentProperty
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


    public String[] getUsedLanguages() {
        if (isProtegeMetaOntologyImported()) {
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
        }
        return DEFAULT_USED_LANGUAGES;
    }


    public Collection getUserDefinedOWLNamedClasses() {
        return getUserDefinedInstances(owlNamedClassClass);
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
        Collection instances = cls.getInstances();
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
        instances.addAll(owlDatatypePropertyClass.getInstances());
        instances.addAll(owlObjectPropertyClass.getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedOWLObjectProperties() {
        List instances = new ArrayList();
        instances.addAll(owlObjectPropertyClass.getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedOWLDatatypeProperties() {
        List instances = new ArrayList();
        instances.addAll(owlDatatypePropertyClass.getInstances());
        return getUserDefinedInstances(instances);
    }


    public Collection getUserDefinedRDFProperties() {
        return getUserDefinedInstances(rdfPropertyClass);
    }


    public Collection getUserDefinedRDFSNamedClasses() {
        return getUserDefinedInstances(rdfsNamedClassClass);
    }


    public String getValueTypeURI(ValueType valueType) {
        return XMLSchemaDatatypes.getValueTypeURI(valueType);
    }


    public Collection getVisibleUserDefinedOWLProperties() {
        List instances = new ArrayList();
        instances.addAll(owlDatatypePropertyClass.getInstances());
        instances.addAll(owlObjectPropertyClass.getInstances());
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
        return getVisibleUserDefinedInstances(rdfPropertyClass);
    }


    public RDFSDatatype getXSDboolean() {
        if (xsdBoolean == null) {
            xsdBoolean = (RDFSDatatype) getFrame(XSDNames.BOOLEAN);
        }
        return xsdBoolean;
    }


    public RDFSDatatype getXSDdouble() {
        if (xsdDouble == null) {
            xsdDouble = (RDFSDatatype) getFrame(XSDNames.DOUBLE);
        }
        return xsdDouble;
    }


    public RDFSDatatype getXSDfloat() {
        if (xsdFloat == null) {
            xsdFloat = (RDFSDatatype) getFrame(XSDNames.FLOAT);
        }
        return xsdFloat;
    }


    public RDFSDatatype getXSDlong() {
        if (xsdLong == null) {
            xsdLong = (RDFSDatatype) getFrame(XSDNames.LONG);
        }
        return xsdLong;
    }


    public RDFSDatatype getXSDint() {
        if (xsdInt == null) {
            xsdInt = (RDFSDatatype) getFrame(XSDNames.INT);
        }
        return xsdInt;
    }


    public RDFSDatatype getXSDshort() {
        if (xsdShort == null) {
            xsdShort = (RDFSDatatype) getFrame(XSDNames.SHORT);
        }
        return xsdShort;
    }


    public RDFSDatatype getXSDbyte() {
        if (xsdByte == null) {
            xsdByte = (RDFSDatatype) getFrame(XSDNames.BYTE);
        }
        return xsdByte;
    }


    public RDFSDatatype getXSDstring() {
        if (xsdString == null) {
            xsdString = (RDFSDatatype) getFrame(XSDNames.STRING);
        }
        return xsdString;
    }


    public RDFSDatatype getXSDbase64Binary() {
        if (xsdBase64Binary == null) {
            xsdBase64Binary = (RDFSDatatype) getFrame(XSDNames.BASE_64_BINARY);
        }
        return xsdBase64Binary;
    }


    public RDFSDatatype getXSDdate() {
        if (xsdDate == null) {
            xsdDate = (RDFSDatatype) getFrame(XSDNames.DATE);
        }
        return xsdDate;
    }


    public RDFSDatatype getXSDtime() {
        if (xsdTime == null) {
            xsdTime = (RDFSDatatype) getFrame(XSDNames.TIME);
        }
        return xsdTime;
    }


    public RDFSDatatype getXSDdateTime() {
        if (xsdDateTime == null) {
            xsdDateTime = (RDFSDatatype) getFrame(XSDNames.DATE_TIME);
        }
        return xsdDateTime;
    }


    public RDFSDatatype getXSDduration() {
        if (xsdDuration == null) {
            xsdDuration = (RDFSDatatype) getFrame(XSDNames.DURATION);
        }
        return xsdDuration;
    }


    public RDFSDatatype getXSDanyURI() {
        if (xsdAnyURI == null) {
            xsdAnyURI = (RDFSDatatype) getFrame(XSDNames.ANY_URI);
        }
        return xsdAnyURI;
    }


    public RDFSDatatype getXSDdecimal() {
        if (xsdDecimal == null) {
            xsdDecimal = (RDFSDatatype) getFrame(XSDNames.DECIMAL);
        }
        return xsdDecimal;
    }


    public RDFSDatatype getXSDinteger() {
        if (xsdInteger == null) {
            xsdInteger = (RDFSDatatype) getFrame(XSDNames.INTEGER);
        }
        return xsdInteger;
    }


    public RDFProperty getOWLVersionInfoProperty() {
        return (RDFProperty) owlVersionInfoProperty;
    }


    public RDFProperty getRDFSRangeProperty() {
        return (RDFProperty) rdfsRangeProperty;
    }


    public RDFProperty getRDFSSubPropertyOfProperty() {
        return (RDFProperty) rdfsSubPropertyOfProperty;
    }


    /**
     * @deprecated
     */
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
    private Boolean protegeMetaOntologyImported = null;
    
    public boolean isProtegeMetaOntologyImported() {
      if (protegeMetaOntologyImported == null) {
        String slotName = ProtegeNames.getSubclassesDisjointSlotName();
        protegeMetaOntologyImported = Boolean.valueOf(getSlot(slotName) != null);
      }
      return protegeMetaOntologyImported;
    }


    public boolean isValidResourceName(String name, RDFResource resource) {
        return isValidFrameName(name, resource);
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


    public synchronized Instance setDirectType(Instance instance, Cls type) {
        if (instance instanceof OWLProperty && type != null) {
            deleteRestrictionsDependingOnPropertyType((OWLProperty) instance, type);
        }
        if (instance instanceof OWLNamedClass && type.equals(rdfsNamedClassClass)) {
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


    public void setProject(Project project) {
        super.setProject(project);
        if (bootstrapped) {

            setOWLProject(new DefaultOWLProject(project));

            project.setPrettyPrintSlotWidgetLabels(false);

            Slot nameSlot = getSlot(Model.Slot.NAME);
            getRootCls().setDirectBrowserSlotPattern(new BrowserSlotPattern(nameSlot));

            project.setDefaultClsWidgetClassName(OWLFormWidget.class.getName());

            project.setWidgetMapper(new OWLWidgetMapper(this));

            if (project.isMultiUserServer()) {
                FrameStoreManager fsm = getFrameStoreManager();
                fsm.removeFrameStore(getOWLFrameStore());
                owlFrameStore = null;
            }

            protegeClassificationStatusProperty.setVisible(false);
            protegeInferredSuperclassesProperty.setVisible(false);
            protegeInferredSubclassesProperty.setVisible(false);
            owlOntologyClass.setVisible(false);
        }
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

    // OWLTestManager stuff -------------------------------------------------

    private final static String AUTO_REPAIR_ENABLED = "TestAutoRepairEnabled";

    private final static String TEST_LIST_NAME = "DisabledTest";

    private final static String TESTGROUP_LIST_NAME = "DisabledTestGroups";


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


    public String createUniqueNewFrameName(String baseName) {
        return getUniqueFrameName(getName() + "_" + baseName);
    }


    // Implements NamespaceManagerListener
    public void defaultNamespaceChanged(String oldValue, String newValue) {
        // No effect: No Frames have to be renamed for this
    }


    public String getResourceNameForURI(String uri) {
        if (uri.lastIndexOf('#') > 0) { // Most likely not an owl:Ontology
            return getFrameNameForURI(uri, true);
        }
        else {   // Special slower handling of potential owl:Ontologies
            String match = uri.endsWith("/") ? uri : uri + "#";
            String prefix = getNamespaceManager().getPrefix(match);
            if (prefix != null) {
                return prefix + ":";
            }
            else if (match.equals(getNamespaceManager().getDefaultNamespace())) {
                return ":";
            }
            else {
                return getFrameNameForURI(uri, true);
            }
        }
    }


    public String getFrameNameForURI(String uri, boolean generatePrefix) {
        String localName = getLocalNameForURI(uri);
        String namespace = getNamespaceForURI(uri);
        final NamespaceManager nsm = getNamespaceManager();
        if (nsm.getDefaultNamespace().equals(namespace)) {
            if (localName == null) {
                return ":";
            }
            else {
                return localName;
            }
        }
        else if (ProtegeNames.NS.equals(namespace)) {
            final String name = ":" + localName; // System frame like :FROM
            if (getFrame(name) != null) {
                return name;
            }
        }
        String prefix = nsm.getPrefix(namespace);
        if (prefix == null) {
            String newPrefix = null;
            int i = 1;
            do {
                newPrefix = "p" + i++;
            }
            while (nsm.getNamespaceForPrefix(newPrefix) != null);
            if (generatePrefix) {
                nsm.setPrefix(namespace, newPrefix);
            }
            prefix = newPrefix;
            // throw new IllegalArgumentException("Unknown prefix for URI " + uri);
        }
        if (localName != null) {
            return prefix + ":" + localName;
        }
        else {
            return prefix + ":";
        }
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
        Instance result = getInstance(text);
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


    public int getRDFSClassCount() {
        return owlClassMetaCls.getInstanceCount() - 4;
    }


    public Collection getRDFSClasses() {
        Collection classes = new ArrayList(owlClassMetaCls.getInstances());
        removeProtegeSystemResources(this, classes);
        return classes;
    }


    public RDFSDatatypeFactory getRDFSDatatypeFactory() {
        return rdfsDatatypeFactory;
    }


    private static void removeProtegeSystemResources(KnowledgeBase kb, Collection frames) {
        if (frames.size() > 0) {
            final Cls dbrClass = kb.getCls(Model.Cls.DIRECTED_BINARY_RELATION);
            //if (kb.getProject() != null && kb.getProject().isHidden(dbrClass)) {
            frames.remove(dbrClass);
            //}
            frames.remove(kb.getCls(Model.Cls.PAL_CONSTRAINT));
            frames.remove(kb.getCls(OWLNames.Cls.ANONYMOUS_ROOT));
            frames.remove(kb.getCls(OWLNames.Cls.OWL_CLASS));
            frames.remove(kb.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES));
            frames.remove(kb.getSlot(OWLNames.Slot.RESOURCE_URI));
            frames.remove(kb.getSlot(Model.Slot.CONSTRAINTS));
        }
    }


    public Collection getResourcesWithPrefix(String prefix) {
        Collection result = new ArrayList();
        for (Iterator it = getFrames().iterator(); it.hasNext();) {
            Frame frame = (Frame) it.next();
            if (frame instanceof RDFResource) {
                String p = ((RDFResource) frame).getNamespacePrefix();
                if (prefix.equals(p)) {
                    result.add(frame);
                }
            }
        }
        return result;
    }


    public String getLocalNameForResourceName(String frameName) {
        int index = frameName.indexOf(':');
        if (index >= 0) {
            return frameName.substring(index + 1);
        }
        else {
            return frameName;
        }
    }


    public String getLocalNameForURI(String uri) {
        int index = Util.splitNamespace(uri);
        if (index == uri.length()) {
            return null;
        }
        return uri.substring(index);
    }


    public NamespaceManager getNamespaceManager() {
        return namespaceManager;
    }


    public String getNamespaceForResourceName(String resourceName) {
        int index = resourceName.indexOf(':');
        if (index > 0) {
            String prefix = resourceName.substring(0, index);
            String namespace = getNamespaceManager().getNamespaceForPrefix(prefix);
            return namespace;
        }
        else {
            return getNamespaceManager().getDefaultNamespace();
        }
    }


    public String getNamespaceForURI(String uri) {
        int index = Util.splitNamespace(uri);
        if (index == uri.length()) {
            return uri;
        }
        return uri.substring(0, index);
    }


    public String getPrefixForResourceName(String frameName) {
        int index = frameName.indexOf(':');
        if (index >= 0) {
            return frameName.substring(0, index);
        }
        else {
            return null;
        }
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


    public String getURIForResourceName(final String name) {
        if (name.indexOf('#') < 0) { // No namespace found
            final NamespaceManager nsm = getNamespaceManager();
            int column = name.indexOf(':');
            if (column == name.length() - 1) {   // ending with ':' -> OWLOntology
                String ns = null;
                if (column == 0) {
                    ns = nsm.getDefaultNamespace();
                }
                else {
                    String prefix = name.substring(0, column);
                    ns = nsm.getNamespaceForPrefix(prefix);
                }
                if (ns.endsWith("#") || ns.endsWith(":")) {
                    return ns.substring(0, ns.length() - 1);
                }
                else {
                    return ns;
                }
            }
            else if (column >= 0) {
                final String localName = name.substring(column + 1);
                if (column == 0) {
                    return ProtegeNames.NS + localName;
                }
                else {
                    final String prefix = name.substring(0, column);
                    final String namespace = nsm.getNamespaceForPrefix(prefix);
                    return namespace + localName;
                }
            }
            else {
                return nsm.getDefaultNamespace() + name;
            }
        }
        else {
            return name;
        }
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


    public OWLDatatypeProperty getRDFSCommentProperty() {
        return (OWLDatatypeProperty) rdfsCommentProperty;
    }


    public String getValidNamespaceFrameName(String suggestedName) {
        return getValidOWLFrameName(this, suggestedName);
    }


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


    public synchronized boolean isSlotMetaCls(Cls cls) {
        return rdfPropertyClass.equals(cls) || hasSuperclass(cls, getRootSlotMetaCls());
    }


    public boolean isValidOWLFrameName(String name) {
        return isValidOWLFrameName(getNamespaceManager(), name);
    }


    public static boolean isValidOWLFrameName(NamespaceManager nsm, String name) {
        if (name == null) {
            return false;
        }
        int index = name.indexOf(':');
        if (index >= 0) {
            if (index == name.length() - 1) {
                return false; // Missing local name
            }
            String prefix = name.substring(0, index);
            String localName = name.substring(index + 1);
            String namespace = nsm.getNamespaceForPrefix(prefix);
            if (namespace == null) {
                return false; // Unknown namespace
            }
            int lastIndex = name.lastIndexOf(':');
            if (index != lastIndex) {
                return false; // Multiple : characters
            }
            return isValidOWLFrameNamePart(localName);
        }
        else {
            return isValidOWLFrameNamePart(name);
        }
    }


    private static boolean isValidOWLFrameNamePart(String name) {
        if (name.length() == 0) {
            return false;
        }
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isJavaIdentifierPart(c) && VALID_SYMBOLS.indexOf(c) < 0) {
                return false;
            }
        }
        return Character.isJavaIdentifierStart(name.charAt(0));
    }


    // Implements NamespaceManagerListener
    public void namespaceChanged(String prefix, String oldValue, String newValue) {
    }


    // Implements NamespaceManagerListener
    public void prefixAdded(String prefix) {
    }


    // Implements NamespaceManagerListener
    public void prefixChanged(String namespace, String oldPrefix, String newPrefix) {
        replacePrefixInInstances(oldPrefix, newPrefix);
    }


    // Implements NamespaceManagerListener
    public void prefixRemoved(String prefix) {
        replacePrefixInInstances(prefix, null);
    }


    /**
     * Replaces the prefix of all NamespaceInstances that have an old prefix.
     *
     * @param oldPrefix the old prefix (to look for)
     * @param newPrefix the new prefix (can be null for no prefix)
     */
    public void replacePrefixInInstances(String oldPrefix, String newPrefix) {
        // Need to go through each triple store
        TripleStoreModel tsm = getTripleStoreModel();
        TripleStore activeTripleStore = tsm.getActiveTripleStore();
        for (Iterator it = tsm.listUserTripleStores(); it.hasNext();) {
            TripleStore curTripleStore = (TripleStore) it.next();
            tsm.setActiveTripleStore(curTripleStore);
            for (Iterator resIt = curTripleStore.listHomeResources(); resIt.hasNext();) {
                RDFResource resource = (RDFResource) resIt.next();
                if (resource.getNamespacePrefix() != null &&
                    resource.getNamespacePrefix().equals(oldPrefix)) {
                    String localName = resource.getLocalName();
                    String newName = localName;
                    if (newPrefix != null && newPrefix.length() > 0) {
                        newName = newPrefix + ":" + localName;
                    }
                    if (getFrame(newName) != null) {
                        newName = getUniqueFrameName(newName);
                    }
                    resource.setName(newName);
                }
            }
        }
        tsm.setActiveTripleStore(activeTripleStore);
    }


    public void resetJenaModel() {
        jenaModel = null;
    }


    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }


    public RDFSNamedClass getRDFSDatatypeClass() {
        return (RDFSNamedClass) rdfsDatatypeClass;
    }


    public Collection getPropertyValueLiterals(RDFResource resource, RDFProperty property) {
        return OWLUtil.getPropertyValueLiterals(resource, property);
    }


    public List getValueLiterals(OWLModel owlModel, List values) {
        return OWLUtil.getLiteralValues(owlModel, values);
    }


    public RDFProperty getRDFSSubClassOfProperty() {
        return (RDFProperty) rdfsSubClassOfProperty;
    }


    public RDFProperty getOWLEquivalentClassProperty() {
        return (RDFProperty) owlEquivalentClassProperty;
    }


    public RDFProperty getOWLInverseOfProperty() {
        return (RDFProperty) owlInverseOfProperty;
    }


    public RDFProperty getOWLDistinctMembersProperty() {
        return (RDFProperty) owlDistinctMembersProperty;
    }
}
