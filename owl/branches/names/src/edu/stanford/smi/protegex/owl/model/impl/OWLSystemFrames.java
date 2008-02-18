package edu.stanford.smi.protegex.owl.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameFactory;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.MaximumCardinalityConstraint;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.RoleConstraint;
import edu.stanford.smi.protege.model.SimpleInstance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.SystemFrames;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.model.ValueTypeConstraint;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.model.XSDNames;

/*
 * This class is very repetitious - it essentially represents the owl metadata
 * ontology written in Java.  For this rea  son I have declared and
 * defined and asserted facts about classes and slots in the order
 * that they appear in the (non-OWL) Protege classes and slots tab.
 */


public abstract class OWLSystemFrames extends SystemFrames {
    protected OWLModel owlModel;

    /*
     * Declarations of meta class entities.
     * 
     *  Cls declarations in the order that they appear in the Clses tab.
     */
    private OWLNamedClass owlThingClass;
    private RDFSNamedClass owlClassMetaCls;
    private OWLNamedClass rdfsNamedClassClass;
    private OWLNamedClass owlNamedClassClass;
    private RDFSNamedClass owlDeprecatedClassClass;
    private OWLNamedClass anonymousClassMetaCls;
    private RDFSNamedClass owlEnumeratedClassClass;
    private RDFSNamedClass owlRestrictionClass;
    private RDFSNamedClass owlAllValuesFromClass;
    private RDFSNamedClass owlHasValueClass;
    private RDFSNamedClass owlMaxCardinalityClass;
    private RDFSNamedClass owlMinCardinalityClass;
    private RDFSNamedClass owlCardinalityClass;
    private RDFSNamedClass owlSomeValuesFromClass;
    private RDFSNamedClass owlLogicalClassClass;
    private RDFSNamedClass owlComplementClassClass;
    private RDFSNamedClass owlIntersectionClassClass;
    private RDFSNamedClass owlUnionClassClass;
    private OWLNamedClass rdfPropertyClass;
    private OWLNamedClass  owlDatatypePropertyClass;
    private OWLNamedClass  owlObjectPropertyClass;
    private OWLNamedClass owlInverseFunctionalPropertyClass;
    private OWLNamedClass owlSymmetricPropertyClass;
    private OWLNamedClass owlTransitivePropertyClass;
    private OWLNamedClass owlAnnotationPropertyClass;
    private OWLNamedClass owlFunctionalPropertyClass;
    private RDFSNamedClass owlDeprecatedPropertyClass;
    private RDFSNamedClass rdfsDatatypeClass;
    private OWLNamedClass owlOntologyClass;
    private OWLNamedClass owlNothingClass;
    private RDFSNamedClass rdfListClass;
    private RDFSNamedClass owlAllDifferentClass;
    private RDFSNamedClass rdfsLiteralClass;
    private RDFSNamedClass rdfsContainerClass;
    private RDFSNamedClass rdfAltClass;
    private RDFSNamedClass rdfBagClass;
    private RDFSNamedClass rdfSeqClass;
    private RDFSNamedClass rdfStatementClass;
    private RDFSNamedClass owlDataRangeClass;
    private RDFSNamedClass anonymousRootCls;
    private RDFSNamedClass rdfExternalResourceClass;
    private RDFSNamedClass owlOntologyPointerClass;

    /*
     * Slot declarations in the order that they appear in the Slot tab.
     */
    private RDFProperty owlAllValuesFromProperty;
    private RDFProperty owlBackwardCompatibleWithProperty;
    private RDFProperty owlCardinalityProperty;
    private RDFProperty owlComplementOfProperty;
    private RDFProperty owlDifferentFromProperty;
    private RDFProperty owlDisjointWithProperty;
    private RDFProperty owlDistinctMembersProperty;
    private RDFProperty owlEquivalentClassProperty;
    private RDFProperty owlEquivalentPropertyProperty;
    private RDFProperty owlHasValueProperty;
    private RDFProperty owlImportsProperty;
    private RDFProperty owlIncompatibleWithProperty;
    private RDFProperty owlIntersectionOfProperty;
    private RDFProperty owlInverseOfProperty;
    private RDFProperty owlMaxCardinalityProperty;
    private RDFProperty owlMinCardinalityProperty;
    private RDFProperty owlOneOfProperty;
    private RDFProperty owlOnPropertyProperty;
    private RDFProperty owlPriorVersionProperty;
    private RDFProperty owlSameAsProperty;
    private RDFProperty owlSomeValuesFromProperty;
    private RDFProperty owlUnionOfProperty;
    private RDFProperty owlValuesFromProperty;
    private RDFProperty owlVersionInfoProperty;
    private RDFProperty protegeClassificationStatusProperty;
    private RDFProperty protegeInferredSubclassesProperty;
    private RDFProperty protegeInferredSuperclassesProperty;
    private RDFProperty protegeInferredTypeProperty;
    private RDFProperty rdfFirstProperty;
    private RDFProperty rdfObjectProperty;
    private RDFProperty rdfPredicateProperty;
    private RDFProperty rdfRestProperty;
    private RDFProperty rdfSubjectProperty;
    private RDFProperty rdfTypeProperty;
    private RDFProperty rdfValueProperty;
    private OWLDatatypeProperty rdfsCommentProperty;
    private RDFProperty rdfsDomainProperty;
    private RDFProperty rdfsIsDefinedByProperty;
    private RDFProperty rdfsLabelProperty;
    private RDFProperty rdfsMemberProperty;
    private RDFProperty rdfsRangeProperty;
    private RDFProperty rdfsSeeAlsoProperty;
    private RDFProperty rdfsSubClassOfProperty;
    private RDFProperty rdfsSubPropertyOf;
    private RDFProperty owlOntologyPrefixesProperty;
    private RDFProperty owlResourceURIProperty;
    private RDFProperty owlOntologyPointerProperty;
    
    /* 
     * Instance Declarations
     */
    private RDFList rdfNil;
    private Set<RDFSDatatype> rdfDatatypes = new HashSet<RDFSDatatype>();   
    private Set<RDFSDatatype> floatDatatypes = new HashSet<RDFSDatatype>();
    private Set<RDFSDatatype> integerDatatypes = new HashSet<RDFSDatatype>();
	
    public OWLSystemFrames(OWLModel owlModel) {
        super(owlModel);
        this.owlModel = owlModel;
        createOWLMetaModel();
    }
	
    /* ******************************************************************************
     * Creating the model elements - nothing is asserted about these elements
     *                               until they are added to the frame store
     */
	
    /*
     * first some utilities for the creation.
     */
	
    protected OWLNamedClass createOWLNamedClass(String name) {
        FrameID id = new FrameID(name);
        OWLNamedClass cls = new DefaultOWLNamedClass(owlModel, id);
        addFrame(id, cls);
        return cls;
    }

    private void replaceFrameWithOWLNamedClass(String name) {
        FrameID id = new FrameID(name);
        OWLNamedClass cls = new DefaultOWLNamedClass(owlModel, id);
        replaceFrame(id, cls);
    }
    
    protected RDFSNamedClass createRDFSNamedClass(String name) {
        FrameID id = new FrameID(name);
        RDFSNamedClass cls = new DefaultRDFSNamedClass(owlModel, id);
        addFrame(id, cls);
        return cls;
    }
    
    private void replaceFrameWithRDFSNamedClass(String name) {
        FrameID id = new FrameID(name);
        RDFSNamedClass cls = new DefaultRDFSNamedClass(owlModel, id);
        replaceFrame(id, cls);
    }

    protected RDFProperty createRDFProperty(String name) {
        FrameID id = new FrameID(name);
        RDFProperty property = new DefaultRDFProperty(owlModel, id);
        addFrame(id, property);
        return property;
    }
    
    protected OWLDatatypeProperty createOWLDatatypeProperty(String name) {
    	FrameID id = new FrameID(name);
    	OWLDatatypeProperty property = new DefaultOWLDatatypeProperty(owlModel, id);
    	addFrame(id, property);
    	return property;
    }
    
    private void replaceFrameWithOWLDatatypeProperty(String name) {
    	FrameID id = new FrameID(name);
    	OWLDatatypeProperty property = new DefaultOWLDatatypeProperty(owlModel, id);
    	replaceFrame(id, property);
    }
    
    protected OWLObjectProperty createOWLObjectProperty(String name) {
    	FrameID id = new FrameID(name);
    	OWLObjectProperty property = new DefaultOWLObjectProperty(owlModel, id);
    	addFrame(id, property);
    	return property;
    }
    
    private void replaceFrameWithOWLObjectProperty(String name) {
    	FrameID id = new FrameID(name);
    	OWLObjectProperty property = new DefaultOWLObjectProperty(owlModel, id);
    	replaceFrame(id, property);
    }
    
    protected RDFSDatatype createRDFSDatatype(String name) {
        FrameID id = new FrameID(name);
        RDFSDatatype frame = new DefaultRDFSDatatype(owlModel, id);
        addFrame(id, frame);
        rdfDatatypes.add(frame);
        return frame;
    }
    
    /*
     * Now the work of actually creating the model.
     */
	
    private void createOWLMetaModel() {
        createOWLClses();
        createOWLSlots();
        createOWLInstances();
        replaceProtegeFrames();
        removeUnusedProtegeFrames();
    }
	
    private void createOWLClses() {
        owlThingClass = createOWLNamedClass(OWLNames.Cls.THING);
        owlClassMetaCls = createRDFSNamedClass(OWLNames.Cls.OWL_CLASS);
        rdfsNamedClassClass = createOWLNamedClass(RDFSNames.Cls.NAMED_CLASS);
        owlNamedClassClass = createOWLNamedClass(OWLNames.Cls.NAMED_CLASS);
        owlDeprecatedClassClass = createRDFSNamedClass(OWLNames.Cls.DEPRECATED_CLASS);
        anonymousClassMetaCls = createOWLNamedClass(OWLNames.Cls.ANONYMOUS_CLASS);
        owlEnumeratedClassClass = createRDFSNamedClass(OWLNames.Cls.ENUMERATED_CLASS);
        owlRestrictionClass = createRDFSNamedClass(OWLNames.Cls.RESTRICTION);
        owlAllValuesFromClass = createRDFSNamedClass(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION);
        owlHasValueClass = createRDFSNamedClass(OWLNames.Cls.HAS_VALUE_RESTRICTION);
        owlMaxCardinalityClass = createRDFSNamedClass(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION);
        owlMinCardinalityClass = createRDFSNamedClass(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION);
        owlCardinalityClass = createRDFSNamedClass(OWLNames.Cls.CARDINALITY_RESTRICTION);
        owlSomeValuesFromClass = createRDFSNamedClass(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
        owlLogicalClassClass = createRDFSNamedClass(OWLNames.Cls.LOGICAL_CLASS);
        owlComplementClassClass = createRDFSNamedClass(OWLNames.Cls.COMPLEMENT_CLASS);
        owlIntersectionClassClass = createRDFSNamedClass(OWLNames.Cls.INTERSECTION_CLASS);
        owlUnionClassClass = createRDFSNamedClass(OWLNames.Cls.UNION_CLASS);
        rdfPropertyClass = createOWLNamedClass(RDFNames.Cls.PROPERTY);
        owlDatatypePropertyClass = createOWLNamedClass(OWLNames.Cls.DATATYPE_PROPERTY);
        owlObjectPropertyClass = createOWLNamedClass(OWLNames.Cls.OBJECT_PROPERTY);
        owlInverseFunctionalPropertyClass = createOWLNamedClass(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY);
        owlSymmetricPropertyClass = createOWLNamedClass(OWLNames.Cls.SYMMETRIC_PROPERTY);
        owlTransitivePropertyClass = createOWLNamedClass(OWLNames.Cls.TRANSITIVE_PROPERTY);
        owlAnnotationPropertyClass = createOWLNamedClass(OWLNames.Cls.ANNOTATION_PROPERTY);
        owlFunctionalPropertyClass = createOWLNamedClass(OWLNames.Cls.FUNCTIONAL_PROPERTY);
        owlDeprecatedPropertyClass = createRDFSNamedClass(OWLNames.Cls.DEPRECATED_PROPERTY);
        rdfsDatatypeClass = createRDFSNamedClass(RDFSNames.Cls.DATATYPE);
        owlOntologyClass = createOWLNamedClass(OWLNames.Cls.ONTOLOGY);
        owlNothingClass = createOWLNamedClass(OWLNames.Cls.NOTHING);
        rdfListClass = createRDFSNamedClass(RDFNames.Cls.LIST);
        owlAllDifferentClass = createRDFSNamedClass(OWLNames.Cls.ALL_DIFFERENT);
        rdfsLiteralClass = createRDFSNamedClass(RDFSNames.Cls.LITERAL);
        rdfsContainerClass = createRDFSNamedClass(RDFSNames.Cls.CONTAINER);
        rdfAltClass = createRDFSNamedClass(RDFNames.Cls.ALT);
        rdfBagClass = createRDFSNamedClass(RDFNames.Cls.BAG);
        rdfSeqClass = createRDFSNamedClass(RDFNames.Cls.SEQ);
        rdfStatementClass = createRDFSNamedClass(RDFNames.Cls.STATEMENT);
        owlDataRangeClass = createRDFSNamedClass(OWLNames.Cls.DATA_RANGE);
        anonymousRootCls= createRDFSNamedClass(OWLNames.Cls.ANONYMOUS_ROOT);
        rdfExternalResourceClass= createRDFSNamedClass(RDFNames.Cls.EXTERNAL_RESOURCE);
        owlOntologyPointerClass = createRDFSNamedClass(OWLNames.Cls.OWL_ONTOLOGY_POINTER_CLASS);
    }
	
    private void createOWLSlots() {
        owlAllValuesFromProperty = createRDFProperty(OWLNames.Slot.ALL_VALUES_FROM);
        owlBackwardCompatibleWithProperty = createRDFProperty(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        owlCardinalityProperty = createRDFProperty(OWLNames.Slot.CARDINALITY);
        owlComplementOfProperty = createRDFProperty(OWLNames.Slot.COMPLEMENT_OF);
        owlDifferentFromProperty = createRDFProperty(OWLNames.Slot.DIFFERENT_FROM);
        owlDisjointWithProperty = createRDFProperty(OWLNames.Slot.DISJOINT_WITH);
        owlDistinctMembersProperty = createRDFProperty(OWLNames.Slot.DISTINCT_MEMBERS);
        owlEquivalentClassProperty = createRDFProperty(OWLNames.Slot.EQUIVALENT_CLASS);
        owlEquivalentPropertyProperty = createRDFProperty(OWLNames.Slot.EQUIVALENT_PROPERTY);
        owlHasValueProperty = createRDFProperty(OWLNames.Slot.HAS_VALUE);
        owlImportsProperty = createRDFProperty(OWLNames.Slot.IMPORTS);
        owlIncompatibleWithProperty = createRDFProperty(OWLNames.Slot.INCOMPATIBLE_WITH);
        owlIntersectionOfProperty = createRDFProperty(OWLNames.Slot.INTERSECTION_OF);
        owlInverseOfProperty = createRDFProperty(OWLNames.Slot.INVERSE_OF);
        owlMaxCardinalityProperty = createRDFProperty(OWLNames.Slot.MAX_CARDINALITY);
        owlMinCardinalityProperty = createRDFProperty(OWLNames.Slot.MIN_CARDINALITY);
        owlOneOfProperty = createRDFProperty(OWLNames.Slot.ONE_OF);
        owlOnPropertyProperty = createRDFProperty(OWLNames.Slot.ON_PROPERTY);
        owlPriorVersionProperty = createRDFProperty(OWLNames.Slot.PRIOR_VERSION);
        owlSameAsProperty = createRDFProperty(OWLNames.Slot.SAME_AS);
        owlSomeValuesFromProperty = createRDFProperty(OWLNames.Slot.SOME_VALUES_FROM);
        owlUnionOfProperty = createRDFProperty(OWLNames.Slot.UNION_OF);
        owlValuesFromProperty = createRDFProperty(OWLNames.Slot.VALUES_FROM);
        owlVersionInfoProperty = createRDFProperty(OWLNames.Slot.VERSION_INFO);
        protegeClassificationStatusProperty = createRDFProperty(ProtegeNames.Slot.CLASSIFICATION_STATUS);
        protegeInferredSubclassesProperty = createRDFProperty(ProtegeNames.Slot.INFERRED_SUBCLASSES);
        protegeInferredSuperclassesProperty = createRDFProperty(ProtegeNames.Slot.INFERRED_SUPERCLASSES);
        protegeInferredTypeProperty = createRDFProperty(ProtegeNames.Slot.INFERRED_TYPE);
        rdfFirstProperty = createRDFProperty(RDFNames.Slot.FIRST);
        rdfObjectProperty = createRDFProperty(RDFNames.Slot.OBJECT);
        rdfPredicateProperty = createRDFProperty(RDFNames.Slot.PREDICATE);
        rdfRestProperty = createRDFProperty(RDFNames.Slot.REST);
        rdfSubjectProperty = createRDFProperty(RDFNames.Slot.SUBJECT);
        rdfTypeProperty = createRDFProperty(RDFNames.Slot.TYPE);
        rdfValueProperty = createRDFProperty(RDFNames.Slot.VALUE);
        rdfsCommentProperty = createOWLDatatypeProperty(RDFSNames.Slot.COMMENT);
        rdfsDomainProperty = createRDFProperty(RDFSNames.Slot.DOMAIN);
        rdfsIsDefinedByProperty = createRDFProperty(RDFSNames.Slot.IS_DEFINED_BY);
        rdfsLabelProperty = createRDFProperty(RDFSNames.Slot.LABEL);
        rdfsMemberProperty = createRDFProperty(RDFSNames.Slot.MEMBER);
        rdfsRangeProperty = createRDFProperty(RDFSNames.Slot.RANGE);
        rdfsSeeAlsoProperty = createRDFProperty(RDFSNames.Slot.SEE_ALSO);
        rdfsSubClassOfProperty = createRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
        rdfsSubPropertyOf  = createRDFProperty(RDFSNames.Slot.SUB_PROPERTY_OF);    
        owlOntologyPrefixesProperty = createRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
        owlResourceURIProperty= createRDFProperty(OWLNames.Slot.RESOURCE_URI);
        owlOntologyPointerProperty = createRDFProperty(OWLNames.Slot.OWL_ONTOLOGY_POINTER_PROPERTY);
    }
    
    private void createOWLInstances() {
        FrameID nilFrameId = new FrameID(RDFNames.Instance.NIL);
        rdfNil = new DefaultRDFList(owlModel, nilFrameId);
        addFrame(nilFrameId, rdfNil);
        createRDFSDatatypes();
    }
    
    
    @SuppressWarnings("unchecked")
    private void createRDFSDatatypes() {
        createRDFSDatatype(RDFNames.XML_LITERAL);
        TypeMapper typeMapper = TypeMapper.getInstance();
        Iterator it = typeMapper.listTypes();
        while (it.hasNext()) {
            com.hp.hpl.jena.datatypes.RDFDatatype type = (com.hp.hpl.jena.datatypes.RDFDatatype) it.next();
            String uri = type.getURI();
            if (uri.startsWith(XSDDatatype.XSD)) {
               String name = uri.toString();
               createRDFSDatatype(name);
            }
        }
        fillDatatypeSet(XMLSchemaDatatypes.floatTypes, floatDatatypes);
        fillDatatypeSet(XMLSchemaDatatypes.integerTypes, integerDatatypes);
    }


    private void fillDatatypeSet(XSDDatatype[] types, Set<RDFSDatatype> set) {
        for (int i = 0; i < types.length; i++) {
            XSDDatatype datatype = types[i];
            String name = datatype.getURI();
            set.add((RDFSDatatype) getFrame(new FrameID(name)));
        }
    }
    
    /*
     * replaceProtegeFrames and removeUnusedProtegeFrames are slightly different.  The first  is 
     * used in the case that the protege core name is being kept but the java type for that object 
     * has changed.  The second one is used in conjunction with the modified Systems calls section 
     * just below it in the case that the protege core frame is being replaced with a frame with
     * a different name and a different java type.
     */
    
    private void replaceProtegeFrames() {
       	replaceFrameWithRDFSNamedClass(Model.Cls.PAL_CONSTRAINT);
    	replaceFrameWithOWLNamedClass(Model.Cls.DIRECTED_BINARY_RELATION);
    	replaceFrameWithOWLObjectProperty(Model.Slot.FROM);
    	replaceFrameWithOWLObjectProperty(Model.Slot.TO);
    	replaceFrameWithOWLDatatypeProperty(Model.Slot.PAL_DESCRIPTION);
    	replaceFrameWithOWLDatatypeProperty(Model.Slot.PAL_NAME);
    	replaceFrameWithOWLDatatypeProperty(Model.Slot.PAL_STATEMENT); 	
    	replaceFrameWithOWLObjectProperty(Model.Slot.CONSTRAINTS);
    }
	
    /*
     * Each call in here corresponds to a changed modified System getter in the section
     * just below.
     */
	private void removeUnusedProtegeFrames() {
	    removeFrame(super.getRootCls().getFrameID());
	    removeFrame(super.getDirectSuperslotsSlot().getFrameID());
	    removeFrame(super.getInverseSlotSlot().getFrameID());
	}

	
	
    /* **********************************************************************
     * Modified SystemFrames calls.
     */
    @Override
	public OWLNamedClass getRootCls() {
        return owlThingClass;
    }

    @Override
	public RDFProperty getDirectSuperslotsSlot() {
        return rdfsSubPropertyOf;
    }
	
    @Override
	public RDFProperty getInverseSlotSlot() {
        return owlInverseOfProperty;
    }

    /* **************************************************************************
     * Tell the Frame Store
     */
    
    @Override
    public void addSystemFrames(FrameStore fs) {
        FrameFactory oldFrameFactory = ((KnowledgeBase) owlModel).getFrameFactory();
        try {
            ((KnowledgeBase) owlModel).setFrameFactory(new NonSwizzlingFactory());
            super.addSystemFrames(fs);
            OWLSystemFramesAssertions asserter = new OWLSystemFramesAssertions(fs);
            asserter.addClassHierarchy();
            asserter.addInstances();
            asserter.addClassAssertions();
            asserter.addSlotAssertions();
        }
        finally {
            ((KnowledgeBase) owlModel).setFrameFactory(oldFrameFactory);
        }
    }

    
    protected class OWLSystemFramesAssertions extends SystemFramesAsserter {
    	private RDFSDatatype xsdInt;
    	private RDFSDatatype xsdString;
    	private Collection<Cls> annotationObjectPropertyTypes = new HashSet<Cls>();
    	private Collection<Cls> annotationDatatypePropertyTypes = new HashSet<Cls>();
    	
    	
    	public OWLSystemFramesAssertions(FrameStore fs) {
    	    super(fs);
            xsdInt = (RDFSDatatype) getFrame(new FrameID(XSDNames.INT));
            xsdString = (RDFSDatatype) getFrame(new FrameID(XSDNames.STRING));
            
            annotationObjectPropertyTypes.add(owlAnnotationPropertyClass);
            annotationObjectPropertyTypes.add(owlObjectPropertyClass);
            
            annotationDatatypePropertyTypes.add(owlAnnotationPropertyClass);
            annotationDatatypePropertyTypes.add(owlDatatypePropertyClass);
    	}
    	

        
        /* **************************************************
         * OWL Assertions
         */

        /*
         * Proper indentation is important in this function (and the indents must reflect the code).
         * Try to avoid using tabs so it looks the same for everybody.  Hopefully compilers don't have
         * trouble with very long statements.
         */
        public void addClassHierarchy() {
            fs.addDirectSuperclass(owlClassMetaCls, getStandardClsMetaCls());
            assertTypeAndSubclasses(owlClassMetaCls, rdfsNamedClassClass, new Cls[] {
                    assertTypeAndSubclasses(rdfsNamedClassClass, owlNamedClassClass, new Cls[] {
                            assertTypeAndSubclasses(owlNamedClassClass,      owlNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(owlDeprecatedClassClass, rdfsNamedClassClass, new Cls[] { })
                        }),
                    assertTypeAndSubclasses(anonymousClassMetaCls, rdfsNamedClassClass, new Cls[] {
                            assertTypeAndSubclasses(owlEnumeratedClassClass, rdfsNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(owlRestrictionClass,     rdfsNamedClassClass, new Cls[] {
                                    assertTypeAndSubclasses(owlAllValuesFromClass,  rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlHasValueClass,       rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlMaxCardinalityClass, rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlMinCardinalityClass, rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlCardinalityClass,    rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlSomeValuesFromClass, rdfsNamedClassClass, new Cls[] {})
                                }),
                            assertTypeAndSubclasses(owlLogicalClassClass,  rdfsNamedClassClass, new Cls[] { 
                                    assertTypeAndSubclasses(owlComplementClassClass,   rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlIntersectionClassClass, rdfsNamedClassClass, new Cls[] {}),
                                    assertTypeAndSubclasses(owlUnionClassClass,        rdfsNamedClassClass, new Cls[] {})
                                })
                        })
                });
            fs.addDirectSuperclass(rdfPropertyClass, getStandardSlotMetaCls());
            assertTypeAndSubclasses(rdfPropertyClass, owlNamedClassClass, new Cls[] {
                    assertTypeAndSubclasses(owlDatatypePropertyClass,   owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlObjectPropertyClass,     owlNamedClassClass, new Cls[] {
                            assertTypeAndSubclasses(owlInverseFunctionalPropertyClass, owlNamedClassClass, new Cls[] {}),
                            assertTypeAndSubclasses(owlSymmetricPropertyClass,         owlNamedClassClass, new Cls[] {}),
                            assertTypeAndSubclasses(owlTransitivePropertyClass,        owlNamedClassClass, new Cls[] {})
                        }),
                    assertTypeAndSubclasses(owlAnnotationPropertyClass, owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlFunctionalPropertyClass, owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlDeprecatedPropertyClass, rdfsNamedClassClass, new Cls[] {})
                });
            assertTypeAndSubclasses(owlThingClass, owlNamedClassClass, new Cls[] {
                    rdfsNamedClassClass,
                    rdfPropertyClass,
                    assertTypeAndSubclasses(rdfsDatatypeClass,    rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlOntologyClass,     owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlNothingClass,      owlNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(rdfListClass,         rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlAllDifferentClass, rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(rdfsLiteralClass,     rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(rdfsContainerClass,   rdfsNamedClassClass, new Cls[] {
                            assertTypeAndSubclasses(rdfAltClass, rdfsNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(rdfBagClass, rdfsNamedClassClass, new Cls[] { }),
                            assertTypeAndSubclasses(rdfSeqClass, rdfsNamedClassClass, new Cls[] { })
                        }),
                    assertTypeAndSubclasses(rdfStatementClass,        rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlDataRangeClass,        rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(anonymousRootCls,         rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(rdfExternalResourceClass, rdfsNamedClassClass, new Cls[] {}),
                    assertTypeAndSubclasses(owlOntologyPointerClass,      rdfsNamedClassClass, new Cls[] {}),
                    getDirectedBinaryRelationCls()
                });
        }
     
        private void addClassAssertions() {
            fs.setDirectOwnSlotValues(owlClassMetaCls, getRoleSlot(), 
                                      Collections.singleton(RoleConstraint.CONCRETE));
            assertTemplateSlots(owlThingClass, 
                                new Slot[] {
                                    owlDifferentFromProperty,
                                    owlSameAsProperty,
                                    owlVersionInfoProperty,
                                    rdfsIsDefinedByProperty,
                                    rdfsLabelProperty,
                                    rdfsSeeAlsoProperty,
                                    rdfsMemberProperty,
                                    rdfTypeProperty,
                                    rdfValueProperty
                                });
                   
            fs.setDirectOwnSlotValues(owlClassMetaCls, getRoleSlot(), 
                                      Collections.singleton(RoleConstraint.ABSTRACT));
            assertTemplateSlot(owlClassMetaCls, owlDisjointWithProperty);
            
            assertTemplateSlots(rdfsNamedClassClass, new Slot[] {
                    rdfsSubClassOfProperty, owlEquivalentClassProperty,
                    protegeClassificationStatusProperty, 
                    protegeInferredSubclassesProperty, 
                    protegeInferredSuperclassesProperty });
              
            assertTemplateSlot(owlEnumeratedClassClass, owlOneOfProperty);

            assertTemplateSlot(owlRestrictionClass, owlOnPropertyProperty);      

            assertTemplateSlot(owlAllValuesFromClass, owlAllValuesFromProperty);

            assertTemplateSlot(owlHasValueClass, owlHasValueProperty);
            
            assertTemplateSlots(owlMaxCardinalityClass, new Slot[] {owlMaxCardinalityProperty, owlValuesFromProperty});
            
            assertTemplateSlots(owlMinCardinalityClass, new Slot[] {owlMinCardinalityProperty, owlValuesFromProperty});

            assertTemplateSlots(owlCardinalityClass, new Slot[] {owlCardinalityProperty, owlValuesFromProperty});
            
            assertTemplateSlot(owlSomeValuesFromClass, owlSomeValuesFromProperty);
            
            assertTemplateSlot(owlComplementClassClass, owlComplementOfProperty);
            assertTemplateSlot(owlIntersectionClassClass, owlIntersectionOfProperty);
            assertTemplateSlot(owlUnionClassClass, owlUnionOfProperty);

            assertTemplateSlots(rdfPropertyClass, 
                                new Slot[] {owlEquivalentPropertyProperty, rdfsDomainProperty, rdfsRangeProperty});

            fs.setDirectTemplateFacetValues(owlDatatypePropertyClass, owlEquivalentPropertyProperty, 
                                            getValueTypeFacet(), Collections.singleton(owlDatatypePropertyClass));
            
            fs.setDirectTemplateFacetValues(owlObjectPropertyClass, owlEquivalentPropertyProperty, getValueTypeFacet(),
                                            ValueTypeConstraint.getValues(ValueType.INSTANCE, Collections.singleton(owlObjectPropertyClass)));
            

            assertTemplateSlots(owlOntologyClass, 
                               new Slot[] {
                                   owlIncompatibleWithProperty,
                                   owlOntologyPrefixesProperty, 
                                   owlImportsProperty,
                                   owlBackwardCompatibleWithProperty,
                                   owlPriorVersionProperty
                               });
            
            assertTemplateSlots(rdfListClass, 
                                new Slot[]{ 
                                    rdfFirstProperty,
                                    rdfRestProperty
                                });
            
            assertTemplateSlot(owlAllDifferentClass, owlDistinctMembersProperty);
            
            assertTemplateSlots(rdfStatementClass, 
                               new Slot[] {
                                   rdfObjectProperty,
                                   rdfPredicateProperty,
                                   rdfSubjectProperty
                               });


            assertTemplateSlot(owlDataRangeClass, owlOneOfProperty);

            assertTemplateSlot(rdfExternalResourceClass, owlResourceURIProperty);

            assertTemplateSlot(owlOntologyPointerClass, owlOntologyPointerProperty);
            
            assertTypeAndName(getDirectedBinaryRelationCls(), owlNamedClassClass);
            
            assertTypeAndName(getPalConstraintCls(), rdfsNamedClassClass);
        }

        private void addSlotAssertions() {
            assertTypeAndName(owlAllValuesFromProperty, rdfPropertyClass);
            
            assertTypeAndName(owlBackwardCompatibleWithProperty, annotationObjectPropertyTypes);
            assertValueType(owlBackwardCompatibleWithProperty, ValueType.INSTANCE);
            
            assertTypeAndName(owlCardinalityProperty, rdfPropertyClass);
            assertFunctional(owlCardinalityProperty);
            assertValueType(owlCardinalityProperty, ValueType.INTEGER);
            fs.setDirectOwnSlotValues(owlCardinalityProperty, rdfsRangeProperty, Collections.singleton(xsdInt));
            
            assertTypeAndName(owlComplementOfProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlComplementOfProperty, owlClassMetaCls);
            
            assertTypeAndName(owlDifferentFromProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlDifferentFromProperty, owlThingClass);

            fs.setDirectOwnSlotValues(owlDifferentFromProperty, rdfsRangeProperty, 
                                      Collections.singleton(owlThingClass));

            assertTypeAndName(owlDisjointWithProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlDisjointWithProperty, rdfsNamedClassClass);
            
            assertTypeAndName(owlDistinctMembersProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlDistinctMembersProperty, rdfListClass);
            
            assertTypeAndName(owlEquivalentClassProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlDistinctMembersProperty, owlClassMetaCls);
            
            assertTypeAndName(owlEquivalentPropertyProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlEquivalentPropertyProperty, rdfPropertyClass);
            fs.setDirectOwnSlotValues(owlEquivalentPropertyProperty, rdfsRangeProperty, 
            						  Collections.singleton(rdfPropertyClass));
            
            assertTypeAndName(owlHasValueProperty, rdfPropertyClass);
            assertFunctional(owlHasValueProperty);
            
            assertTypeAndName(owlImportsProperty, rdfPropertyClass);
            
            assertTypeAndName(owlIncompatibleWithProperty, annotationObjectPropertyTypes);
            assertValueType(owlIncompatibleWithProperty, ValueType.INSTANCE);
            
            assertTypeAndName(owlIntersectionOfProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlIntersectionOfProperty, rdfListClass);
            
            assertTypeAndName(owlInverseOfProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlInverseOfProperty, owlObjectPropertyClass);
            
            assertTypeAndName(owlMaxCardinalityProperty, rdfPropertyClass);
            assertValueType(owlMaxCardinalityProperty, ValueType.INTEGER);
            assertFunctional(owlMaxCardinalityProperty);
            fs.setDirectOwnSlotValues(owlMaxCardinalityProperty, rdfsRangeProperty, Collections.singleton(xsdInt));
            
            assertTypeAndName(owlMinCardinalityProperty, rdfPropertyClass);
            assertValueType(owlMinCardinalityProperty, ValueType.INTEGER);
            assertFunctional(owlMinCardinalityProperty);
            fs.setDirectOwnSlotValues(owlMinCardinalityProperty, rdfsRangeProperty, Collections.singleton(xsdInt));

            assertTypeAndName(owlOneOfProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlOneOfProperty, rdfListClass);
            
            assertTypeAndName(owlOnPropertyProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlOnPropertyProperty, rdfPropertyClass);
            
            assertTypeAndName(owlPriorVersionProperty, annotationObjectPropertyTypes);
            assertValueType(owlPriorVersionProperty, ValueType.INSTANCE);
            
            assertTypeAndName(owlSameAsProperty, rdfPropertyClass);
            assertValueType(owlSameAsProperty, ValueType.INSTANCE);
            fs.setDirectOwnSlotValues(owlSameAsProperty, rdfsRangeProperty, Collections.singleton(owlThingClass));
            
            assertTypeAndName(owlSomeValuesFromProperty, rdfPropertyClass);      
            
            assertTypeAndName(owlUnionOfProperty, rdfPropertyClass);
            assertValueTypeFromClass(owlUnionOfProperty, rdfListClass);
            
            assertTypeAndName(owlValuesFromProperty, rdfPropertyClass);
            assertValueType(owlValuesFromProperty, ValueType.INSTANCE);
            fs.setDirectOwnSlotValues(owlValuesFromProperty, rdfsRangeProperty, Collections.singleton(owlThingClass));
            assertFunctional(owlValuesFromProperty);
            
            assertTypeAndName(owlVersionInfoProperty, annotationObjectPropertyTypes);
            assertValueType(owlVersionInfoProperty, ValueType.STRING);
            fs.setDirectOwnSlotValues(owlVersionInfoProperty, rdfsRangeProperty, Collections.singleton(xsdInt));
            
            assertTypeAndName(protegeClassificationStatusProperty, rdfPropertyClass);      
            assertFunctional(protegeClassificationStatusProperty);
            assertValueType(owlVersionInfoProperty, ValueType.BOOLEAN);
            
            assertTypeAndName(protegeInferredSubclassesProperty, rdfPropertyClass);      
            assertValueTypeFromClass(protegeInferredSubclassesProperty, getStandardClsMetaCls());
            
            assertTypeAndName(protegeInferredSuperclassesProperty, rdfPropertyClass);      
            assertValueTypeFromClass(protegeInferredSuperclassesProperty, getStandardClsMetaCls());
            
            assertTypeAndName(protegeInferredTypeProperty, rdfPropertyClass);      
            assertValueTypeFromClass(protegeInferredTypeProperty, getStandardClsMetaCls());
            
            assertTypeAndName(rdfFirstProperty, rdfPropertyClass); 
            assertFunctional(rdfFirstProperty);
            
            assertTypeAndName(rdfObjectProperty, rdfPropertyClass);      
            assertValueType(rdfObjectProperty, ValueType.INSTANCE);
            
            assertTypeAndName(rdfPredicateProperty, rdfPropertyClass);      
            assertValueType(rdfPredicateProperty, ValueType.INSTANCE);
            
            assertTypeAndName(rdfRestProperty, rdfPropertyClass);  
            assertFunctional(rdfRestProperty);
            assertValueTypeFromClass(rdfRestProperty, rdfListClass);
            
            assertTypeAndName(rdfSubjectProperty, rdfPropertyClass);  
            assertValueType(rdfSubjectProperty, ValueType.INSTANCE);
            
            assertTypeAndName(rdfTypeProperty, rdfPropertyClass);
            assertValueType(rdfTypeProperty, ValueType.CLS);
            
            assertTypeAndName(rdfValueProperty, rdfPropertyClass);  
            
            assertTypeAndName(rdfsCommentProperty, annotationDatatypePropertyTypes);
            assertValueType(rdfsCommentProperty, ValueType.STRING);
            fs.setDirectOwnSlotValues(rdfsCommentProperty, rdfsRangeProperty, Collections.singleton(xsdString));
            
            assertTypeAndName(rdfsDomainProperty, rdfPropertyClass);  
            assertValueType(rdfsCommentProperty, ValueType.INSTANCE);
            
            assertTypeAndName(rdfsIsDefinedByProperty, annotationObjectPropertyTypes);
            
            assertTypeAndName(rdfsLabelProperty, annotationObjectPropertyTypes);
            assertValueType(rdfsLabelProperty, ValueType.STRING);
            fs.setDirectOwnSlotValues(rdfsLabelProperty, rdfsRangeProperty, Collections.singleton(xsdString));
            
            assertTypeAndName(rdfsMemberProperty, rdfPropertyClass);
            assertValueType(rdfsMemberProperty, ValueType.INSTANCE);
            
            assertTypeAndName(rdfsRangeProperty, rdfPropertyClass);  
            assertValueType(rdfsRangeProperty, ValueType.INSTANCE);
            
            assertTypeAndName(rdfsSeeAlsoProperty, annotationObjectPropertyTypes);

            assertTypeAndName(rdfsSubClassOfProperty, rdfPropertyClass);  
            assertValueTypeFromClass(rdfsSubClassOfProperty, rdfsNamedClassClass);
            fs.setDirectOwnSlotValues(rdfsSubClassOfProperty, rdfsRangeProperty, 
                                      Collections.singleton(rdfsNamedClassClass));
            
            assertTypeAndName(owlOntologyPrefixesProperty, rdfPropertyClass);
            assertValueType(owlOntologyPrefixesProperty, ValueType.STRING);

            assertTypeAndName(owlOntologyPrefixesProperty, rdfPropertyClass);
            assertValueType(owlOntologyPrefixesProperty, ValueType.STRING);
            assertFunctional(owlOntologyPrefixesProperty);

            assertTypeAndName(owlOntologyPointerProperty, rdfPropertyClass);
            assertFunctional(owlOntologyPointerProperty);
            assertValueTypeFromClass(owlOntologyPointerProperty, owlOntologyClass);
            
            assertTypeAndName(getFromSlot(), owlObjectPropertyClass);        
            assertTypeAndName(getToSlot(), owlObjectPropertyClass);
            assertTypeAndName(getFromSlot(), owlObjectPropertyClass);
            assertTypeAndName(getPalDescriptionSlot(), owlDatatypePropertyClass);
            assertTypeAndName(getPalNameSlot(), owlDatatypePropertyClass);
            assertTypeAndName(getPalStatementSlot(), owlDatatypePropertyClass);
            assertTypeAndName(getConstraintsSlot(), owlObjectPropertyClass);
        }
        

        
        private void addInstances() {
            assertTypeAndName(rdfNil, rdfListClass);
            for (Instance datatype : rdfDatatypes) {
                assertTypeAndName(datatype, rdfsDatatypeClass);
            } 
        }
    }
    
    public class SystemFramesAsserter {
        protected FrameStore fs;
        
        public SystemFramesAsserter(FrameStore fs) {
            this.fs = fs;
        }
        
        /* ***********************************************************
         * Utilities
         */
        
        protected Cls assertTypeAndSubclasses(Cls cls, Cls type, Cls[] subclasses) {
            assertTypeAndName(cls, type);
            for (Cls subclass : subclasses) {
                fs.addDirectSuperclass(subclass, cls);
            }
            return cls;
        }
        
        protected void assertTemplateSlots(Cls cls, Slot [] slots) {
            for (Slot slot : slots) {
                assertTemplateSlot(cls, slot);
            }
        }
        
        protected void assertTemplateSlot(Cls cls, Slot slot) {
            fs.addDirectTemplateSlot(cls, slot);
        }
        
        protected void assertFunctional(Slot slot) {
            fs.setDirectOwnSlotValues(slot, getMaximumCardinalitySlot(),
                                      MaximumCardinalityConstraint.getValues(false));
        }
        
        protected void assertTypeAndName(Instance frame, Cls type) {
            assertTypeAndName(frame, Collections.singleton(type));
        }

        protected void assertValueType(Slot slot, ValueType vt) {
            fs.setDirectOwnSlotValues(slot, getValueTypeSlot(),
                                      ValueTypeConstraint.getValues(vt));
        }
        
        protected void assertValueTypeFromClass(Slot slot, Cls cls) {
            fs.setDirectOwnSlotValues(slot, getValueTypeSlot(),
                                      ValueTypeConstraint.getValues(ValueType.INSTANCE, 
                                                                    Collections.singleton(cls)));
        }
        
        protected void assertTypeAndName(Frame frame, Collection<Cls> types) {
            OWLSystemFrames.this.assertTypeAndName(fs, frame, types);
        }
        
    }
    
    private class NonSwizzlingFactory implements FrameFactory {

        public void addJavaPackage(String packageName) {
            ;
        }

        public Cls createCls(FrameID id, Collection directTypes) {
            return (Cls) getFrame(id);
        }

        public Facet createFacet(FrameID id, Collection directTypes) {
            return (Facet) getFrame(id);
        }

        public Frame createFrameFromClassId(int javaClassId, FrameID id) {
            return getFrame(id);
        }

        public SimpleInstance createSimpleInstance(FrameID id,
                                                   Collection directTypes) {
            return (SimpleInstance) getFrame(id);
        }

        public Slot createSlot(FrameID id, Collection directTypes) {
            return (Slot) getFrame(id);
        }

        public Collection getClsJavaClassIds() {
            throw new UnsupportedOperationException();
        }

        public Collection getFacetJavaClassIds() {
            throw new UnsupportedOperationException();
        }

        public int getJavaClassId(Frame value) {
            throw new UnsupportedOperationException();
        }

        public Collection getSimpleInstanceJavaClassIds() {
            throw new UnsupportedOperationException();
        }

        public Collection getSlotJavaClassIds() {
            throw new UnsupportedOperationException();
        }

        public boolean isCorrectJavaImplementationClass(FrameID id,
                                                        Collection directTypes,
                                                        Class clas) {
            return true;
        }

        public void removeJavaPackage(String packageName) {
            throw new UnsupportedOperationException();
        }

        public Frame rename(Frame original, String name) {
            throw new UnsupportedOperationException();
        }
        
    }
    
    /*
     * ----------------------------------------------------------------------
     * The getters - automatically generated by eclipse.
     */
    
    /*
     * Getters for the important datatypes.  We don't need all 43...
     */
    public RDFSDatatype getXsdBoolean() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.BOOLEAN));
    }

    public RDFSDatatype getXsdDouble() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DOUBLE));
    }

    public RDFSDatatype getXsdFloat() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.FLOAT));
    }

    public RDFSDatatype getXsdLong() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.LONG));
    }

    public RDFSDatatype getXsdInt() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.INT));
    }

    public RDFSDatatype getXsdShort() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.SHORT));
    }

    public RDFSDatatype getXsdByte() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.BYTE));
    }

    public RDFSDatatype getXsdString() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.STRING));
    }

    public RDFSDatatype getXsdBase64Binary() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.BASE_64_BINARY));
    }

    public RDFSDatatype getXsdDate() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DATE));
    }

    public RDFSDatatype getXsdTime() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.TIME));
    }

    public RDFSDatatype getXsdDateTime() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DATE_TIME));
    }

    public RDFSDatatype getXsdDuration() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DURATION));
    }

    public RDFSDatatype getXsdAnyURI() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.ANY_URI));
    }

    public RDFSDatatype getXsdDecimal() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.DECIMAL));
    }

    public RDFSDatatype getXsdInteger() {
        return (RDFSDatatype) getFrame(new FrameID(XSDNames.INTEGER));
    }

    public RDFSDatatype getXmlLiteralType() {
        return (RDFSDatatype) getFrame(new FrameID(RDFNames.XML_LITERAL));
    }

    
    /*
     * Getters for all the important system frames.  IDE's can generate this for you.
     */

    /**
     * @return the owlThingClass
     */
    public OWLNamedClass getOwlThingClass() {
        return owlThingClass;
    }

    /**
     * @return the owlClassMetaCls
     */
    public RDFSNamedClass getOwlClassMetaCls() {
        return owlClassMetaCls;
    }

    /**
     * @return the rdfsNamedClassClass
     */
    public OWLNamedClass getRdfsNamedClassClass() {
        return rdfsNamedClassClass;
    }

    /**
     * @return the owlNamedClassClass
     */
    public OWLNamedClass getOwlNamedClassClass() {
        return owlNamedClassClass;
    }

    /**
     * @return the owlDeprecatedClassClass
     */
    public RDFSNamedClass getOwlDeprecatedClassClass() {
        return owlDeprecatedClassClass;
    }

    /**
     * @return the anonymousClassMetaCls
     */
    public OWLNamedClass getAnonymousClassMetaCls() {
        return anonymousClassMetaCls;
    }

    /**
     * @return the owlEnumeratedClassClass
     */
    public RDFSNamedClass getOwlEnumeratedClassClass() {
        return owlEnumeratedClassClass;
    }

    /**
     * @return the owlRestrictionClass
     */
    public RDFSNamedClass getOwlRestrictionClass() {
        return owlRestrictionClass;
    }

    /**
     * @return the owlAllValuesFromClass
     */
    public RDFSNamedClass getOwlAllValuesFromClass() {
        return owlAllValuesFromClass;
    }

    /**
     * @return the owlHasValueClass
     */
    public RDFSNamedClass getOwlHasValueClass() {
        return owlHasValueClass;
    }

    /**
     * @return the owlMaxCardinalityClass
     */
    public RDFSNamedClass getOwlMaxCardinalityClass() {
        return owlMaxCardinalityClass;
    }

    /**
     * @return the owlMinCardinalityClass
     */
    public RDFSNamedClass getOwlMinCardinalityClass() {
        return owlMinCardinalityClass;
    }

    /**
     * @return the owlCardinalityClass
     */
    public RDFSNamedClass getOwlCardinalityClass() {
        return owlCardinalityClass;
    }

    /**
     * @return the owlSomeValuesFromClass
     */
    public RDFSNamedClass getOwlSomeValuesFromClass() {
        return owlSomeValuesFromClass;
    }

    /**
     * @return the owlLogicalClassClass
     */
    public RDFSNamedClass getOwlLogicalClassClass() {
        return owlLogicalClassClass;
    }

    /**
     * @return the owlComplementClassClass
     */
    public RDFSNamedClass getOwlComplementClassClass() {
        return owlComplementClassClass;
    }

    /**
     * @return the owlIntersectionClassClass
     */
    public RDFSNamedClass getOwlIntersectionClassClass() {
        return owlIntersectionClassClass;
    }

    /**
     * @return the owlUnionClassClass
     */
    public RDFSNamedClass getOwlUnionClassClass() {
        return owlUnionClassClass;
    }

    /**
     * @return the rdfPropertyClass
     */
    public OWLNamedClass getRdfPropertyClass() {
        return rdfPropertyClass;
    }

    /**
     * @return the owlDatatypePropertyClass
     */
    public OWLNamedClass getOwlDatatypePropertyClass() {
        return owlDatatypePropertyClass;
    }

    /**
     * @return the owlObjectPropertyClass
     */
    public OWLNamedClass getOwlObjectPropertyClass() {
        return owlObjectPropertyClass;
    }

    /**
     * @return the owlInverseFunctionalPropertyClass
     */
    public OWLNamedClass getOwlInverseFunctionalPropertyClass() {
        return owlInverseFunctionalPropertyClass;
    }

    /**
     * @return the owlSymmetricPropertyClass
     */
    public OWLNamedClass getOwlSymmetricPropertyClass() {
        return owlSymmetricPropertyClass;
    }

    /**
     * @return the owlTransitivePropertyClass
     */
    public OWLNamedClass getOwlTransitivePropertyClass() {
        return owlTransitivePropertyClass;
    }

    /**
     * @return the owlAnnotationPropertyClass
     */
    public OWLNamedClass getOwlAnnotationPropertyClass() {
        return owlAnnotationPropertyClass;
    }

    /**
     * @return the owlFunctionalPropertyClass
     */
    public OWLNamedClass getOwlFunctionalPropertyClass() {
        return owlFunctionalPropertyClass;
    }

    /**
     * @return the owlDeprecatedPropertyClass
     */
    public RDFSNamedClass getOwlDeprecatedPropertyClass() {
        return owlDeprecatedPropertyClass;
    }

    /**
     * @return the rdfsDatatypeClass
     */
    public RDFSNamedClass getRdfsDatatypeClass() {
        return rdfsDatatypeClass;
    }

    /**
     * @return the owlOntologyClass
     */
    public OWLNamedClass getOwlOntologyClass() {
        return owlOntologyClass;
    }

    /**
     * @return the owlNothingClass
     */
    public OWLNamedClass getOwlNothingClass() {
        return owlNothingClass;
    }

    /**
     * @return the rdfListClass
     */
    public RDFSNamedClass getRdfListClass() {
        return rdfListClass;
    }

    /**
     * @return the owlAllDifferentClass
     */
    public RDFSNamedClass getOwlAllDifferentClass() {
        return owlAllDifferentClass;
    }

    /**
     * @return the rdfsLiteralClass
     */
    public RDFSNamedClass getRdfsLiteralClass() {
        return rdfsLiteralClass;
    }

    /**
     * @return the rdfsContainerClass
     */
    public RDFSNamedClass getRdfsContainerClass() {
        return rdfsContainerClass;
    }

    /**
     * @return the rdfAltClass
     */
    public RDFSNamedClass getRdfAltClass() {
        return rdfAltClass;
    }

    /**
     * @return the rdfBagClass
     */
    public RDFSNamedClass getRdfBagClass() {
        return rdfBagClass;
    }

    /**
     * @return the rdfSeqClass
     */
    public RDFSNamedClass getRdfSeqClass() {
        return rdfSeqClass;
    }

    /**
     * @return the rdfStatementClass
     */
    public RDFSNamedClass getRdfStatementClass() {
        return rdfStatementClass;
    }

    /**
     * @return the owlDataRangeClass
     */
    public RDFSNamedClass getOwlDataRangeClass() {
        return owlDataRangeClass;
    }

    /**
     * @return the anonymousRootCls
     */
    public RDFSNamedClass getAnonymousRootCls() {
        return anonymousRootCls;
    }

    /**
     * @return the rdfExternalResourceClass
     */
    public RDFSNamedClass getRdfExternalResourceClass() {
        return rdfExternalResourceClass;
    }

    /**
     * @return the topOWLOntologyClass
     */
    public RDFSNamedClass getOwlOntologyPointerClass() {
        return owlOntologyPointerClass;
    }

    /**
     * @return the owlAllValuesFromProperty
     */
    public RDFProperty getOwlAllValuesFromProperty() {
        return owlAllValuesFromProperty;
    }

    /**
     * @return the owlBackwardCompatibleWithProperty
     */
    public RDFProperty getOwlBackwardCompatibleWithProperty() {
        return owlBackwardCompatibleWithProperty;
    }

    /**
     * @return the owlCardinalityProperty
     */
    public RDFProperty getOwlCardinalityProperty() {
        return owlCardinalityProperty;
    }

    /**
     * @return the owlComplementOfProperty
     */
    public RDFProperty getOwlComplementOfProperty() {
        return owlComplementOfProperty;
    }

    /**
     * @return the owlDifferentFromProperty
     */
    public RDFProperty getOwlDifferentFromProperty() {
        return owlDifferentFromProperty;
    }

    /**
     * @return the owlDisjointWithProperty
     */
    public RDFProperty getOwlDisjointWithProperty() {
        return owlDisjointWithProperty;
    }

    /**
     * @return the owlDistinctMembersProperty
     */
    public RDFProperty getOwlDistinctMembersProperty() {
        return owlDistinctMembersProperty;
    }

    /**
     * @return the owlEquivalentClassProperty
     */
    public RDFProperty getOwlEquivalentClassProperty() {
        return owlEquivalentClassProperty;
    }

    /**
     * @return the owlEquivalentPropertyProperty
     */
    public RDFProperty getOwlEquivalentPropertyProperty() {
        return owlEquivalentPropertyProperty;
    }

    /**
     * @return the owlHasValueProperty
     */
    public RDFProperty getOwlHasValueProperty() {
        return owlHasValueProperty;
    }

    /**
     * @return the owlImportsProperty
     */
    public RDFProperty getOwlImportsProperty() {
        return owlImportsProperty;
    }

    /**
     * @return the owlIncompatibleWithProperty
     */
    public RDFProperty getOwlIncompatibleWithProperty() {
        return owlIncompatibleWithProperty;
    }

    /**
     * @return the owlIntersectionOfProperty
     */
    public RDFProperty getOwlIntersectionOfProperty() {
        return owlIntersectionOfProperty;
    }

    /**
     * @return the owlInverseOfProperty
     */
    public RDFProperty getOwlInverseOfProperty() {
        return owlInverseOfProperty;
    }

    /**
     * @return the owlMaxCardinalityProperty
     */
    public RDFProperty getOwlMaxCardinalityProperty() {
        return owlMaxCardinalityProperty;
    }

    /**
     * @return the owlMinCardinalityProperty
     */
    public RDFProperty getOwlMinCardinalityProperty() {
        return owlMinCardinalityProperty;
    }

    /**
     * @return the owlOneOfProperty
     */
    public RDFProperty getOwlOneOfProperty() {
        return owlOneOfProperty;
    }

    /**
     * @return the owlOnPropertyProperty
     */
    public RDFProperty getOwlOnPropertyProperty() {
        return owlOnPropertyProperty;
    }

    /**
     * @return the owlPriorVersionProperty
     */
    public RDFProperty getOwlPriorVersionProperty() {
        return owlPriorVersionProperty;
    }

    /**
     * @return the owlSameAsProperty
     */
    public RDFProperty getOwlSameAsProperty() {
        return owlSameAsProperty;
    }

    /**
     * @return the owlSomeValuesFromProperty
     */
    public RDFProperty getOwlSomeValuesFromProperty() {
        return owlSomeValuesFromProperty;
    }

    /**
     * @return the owlUnionOfProperty
     */
    public RDFProperty getOwlUnionOfProperty() {
        return owlUnionOfProperty;
    }

    /**
     * @return the owlValuesFromProperty
     */
    public RDFProperty getOwlValuesFromProperty() {
        return owlValuesFromProperty;
    }

    /**
     * @return the owlVersionInfoProperty
     */
    public RDFProperty getOwlVersionInfoProperty() {
        return owlVersionInfoProperty;
    }

    /**
     * @return the protegeClassificationStatusProperty
     */
    public RDFProperty getProtegeClassificationStatusProperty() {
        return protegeClassificationStatusProperty;
    }

    /**
     * @return the protegeInferredSubclassesProperty
     */
    public RDFProperty getProtegeInferredSubclassesProperty() {
        return protegeInferredSubclassesProperty;
    }

    /**
     * @return the protegeInferredSuperclassesProperty
     */
    public RDFProperty getProtegeInferredSuperclassesProperty() {
        return protegeInferredSuperclassesProperty;
    }

    /**
     * @return the protegeInferredTypeProperty
     */
    public RDFProperty getProtegeInferredTypeProperty() {
        return protegeInferredTypeProperty;
    }

    /**
     * @return the rdfFirstProperty
     */
    public RDFProperty getRdfFirstProperty() {
        return rdfFirstProperty;
    }

    /**
     * @return the rdfObjectProperty
     */
    public RDFProperty getRdfObjectProperty() {
        return rdfObjectProperty;
    }

    /**
     * @return the rdfPredicateProperty
     */
    public RDFProperty getRdfPredicateProperty() {
        return rdfPredicateProperty;
    }

    /**
     * @return the rdfRestProperty
     */
    public RDFProperty getRdfRestProperty() {
        return rdfRestProperty;
    }

    /**
     * @return the rdfSubjectProperty
     */
    public RDFProperty getRdfSubjectProperty() {
        return rdfSubjectProperty;
    }

    /**
     * @return the rdfTypeProperty
     */
    public RDFProperty getRdfTypeProperty() {
        return rdfTypeProperty;
    }

    /**
     * @return the rdfValueProperty
     */
    public RDFProperty getRdfValueProperty() {
        return rdfValueProperty;
    }

    /**
     * @return the rdfsCommentProperty
     */
    public OWLDatatypeProperty getRdfsCommentProperty() {
        return rdfsCommentProperty;
    }

    /**
     * @return the rdfsDomainProperty
     */
    public RDFProperty getRdfsDomainProperty() {
        return rdfsDomainProperty;
    }

    /**
     * @return the rdfsIsDefinedByProperty
     */
    public RDFProperty getRdfsIsDefinedByProperty() {
        return rdfsIsDefinedByProperty;
    }

    /**
     * @return the rdfsLabelProperty
     */
    public RDFProperty getRdfsLabelProperty() {
        return rdfsLabelProperty;
    }

    /**
     * @return the rdfsMemberProperty
     */
    public RDFProperty getRdfsMemberProperty() {
        return rdfsMemberProperty;
    }

    /**
     * @return the rdfsRangeProperty
     */
    public RDFProperty getRdfsRangeProperty() {
        return rdfsRangeProperty;
    }

    /**
     * @return the rdfsSeeAlsoProperty
     */
    public RDFProperty getRdfsSeeAlsoProperty() {
        return rdfsSeeAlsoProperty;
    }

    /**
     * @return the rdfsSubClassOfProperty
     */
    public RDFProperty getRdfsSubClassOfProperty() {
        return rdfsSubClassOfProperty;
    }

    /**
     * @return the rdfsSubPropertyOf
     */
    public RDFProperty getRdfsSubPropertyOf() {
        return rdfsSubPropertyOf;
    }

    /**
     * @return the owlOntologyPrefixesProperty
     */
    public RDFProperty getOwlOntologyPrefixesProperty() {
        return owlOntologyPrefixesProperty;
    }

    /**
     * @return the owlResourceURIProperty
     */
    public RDFProperty getOwlResourceURIProperty() {
        return owlResourceURIProperty;
    }

    /**
     * @return the topOWLOntologyURISlot
     */
    public RDFProperty getOwlOntologyPointerProperty() {
        return owlOntologyPointerProperty;
    }

    /**
     * @return the rdfNil
     */
    public RDFList getRdfNil() {
        return rdfNil;
    }

    /**
     * @return the rdfDatatypes
     */
    public Set<RDFSDatatype> getRdfDatatypes() {
        return rdfDatatypes;
    }

    /**
     * @return the floatDatatypes
     */
    public Set<RDFSDatatype> getFloatDatatypes() {
        return floatDatatypes;
    }

    /**
     * @return the integerDatatypes
     */
    public Set<RDFSDatatype> getIntegerDatatypes() {
        return integerDatatypes;
    }
}
