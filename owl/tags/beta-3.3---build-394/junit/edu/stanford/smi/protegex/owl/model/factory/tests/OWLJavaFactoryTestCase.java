package edu.stanford.smi.protegex.owl.model.factory.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.*;
import edu.stanford.smi.protegex.owl.tests.AbstractOWLModelTestCase;

import java.util.Collection;
import java.util.Collections;

/**
 * Various tests which verify whether the DefaultOWLFrameFactory generates the correct Java
 * classes for given Protege classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLJavaFactoryTestCase extends AbstractOWLModelTestCase {

    private final static String TEST_CLASS_NAME = "TestCls";


    public void testCacheAfterChangedType() {
        final String name = "slot";
        OWLObjectProperty objectSlot = owlModel.createOWLObjectProperty(name);
        objectSlot.setProtegeType(owlModel.getOWLDatatypePropertyClass());
        OWLDatatypeProperty datatypeSlot = (OWLDatatypeProperty) owlModel.getRDFProperty(name);
    }


    private void testCreate(String metaClsName, Class interFace, Class defaultClass) {
        KnowledgeBase kb = owlModel;
        Cls metaCls = kb.getCls(metaClsName);
        kb.createCls(TEST_CLASS_NAME, Collections.singleton(owlModel.getOWLThingClass()), metaCls);
        Cls cls = kb.getCls(TEST_CLASS_NAME);
        assertNotNull(cls);
        assertTrue(interFace.isInstance(cls));
        assertTrue(defaultClass.isInstance(cls));
        // owlModel.deleteCls(cls);
    }


    public void testCreateAllDifferentInstance() {
        OWLAllDifferent adi = owlModel.createOWLAllDifferent();
        assertEquals(DefaultOWLAllDifferent.class, adi.getClass());
    }


    public void testCreateAllRestriction() {
        testCreate(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION, OWLAllValuesFrom.class, DefaultOWLAllValuesFrom.class);
    }


    public void testCreateHasValueRestriction() {
        testCreate(OWLNames.Cls.HAS_VALUE_RESTRICTION, OWLHasValue.class, DefaultOWLHasValue.class);
    }


    public void testCreateOWLDataRange() {
        RDFSClass dataRangeClass = owlModel.getOWLDataRangeClass();
        assertTrue(dataRangeClass.createInstance("test") instanceof OWLDataRange);
    }


    public void testCreateRDFDatatype() {
        assertTrue(owlModel.getRDFResource("xsd:int") instanceof RDFSDatatype);
    }


    public void testCreateRDFSCls() {
        testCreate(RDFSNames.Cls.NAMED_CLASS, RDFSNamedClass.class, DefaultRDFSNamedClass.class);
    }


    public void testCreateRDFSlot() {
        assertTrue(owlModel.createRDFProperty("slot") instanceof RDFProperty);
    }


    public void testCreateRDFSNamedMetaclassInstance() {
        RDFSNamedClass metaclass = owlModel.createRDFSNamedSubclass("Metaclass", owlModel.getRDFSNamedClassClass());
        Frame frame = metaclass.createInstance("test");
        assertEquals(DefaultRDFSNamedClass.class, frame.getClass());
    }


    public void testCreateOWLNamedClass() {
        testCreate(OWLNames.Cls.NAMED_CLASS, OWLNamedClass.class, DefaultOWLNamedClass.class);
    }


    public void testCreateListInstance() {
        RDFList list = owlModel.createRDFList(Collections.EMPTY_LIST.iterator());
        assertEquals(DefaultRDFList.class, list.getClass());
    }


    public void testCreateOntologyInstance() {
        owlModel.getNamespaceManager().setPrefix("http://aldi.de#", "test");
        OWLOntology owlOntology = owlModel.createOWLOntology("test");
        assertEquals(DefaultOWLOntology.class, owlOntology.getClass());
    }


    public void testRDFSClsMetaClsIsOWLCls() {
        Cls cls = ((KnowledgeBase) owlModel).getCls(RDFSNames.Cls.NAMED_CLASS);
        assertTrue(cls instanceof RDFSClass);
    }


    public void testRDFPropertyMetaclass() {
        OWLNamedClass c1 = (OWLNamedClass) owlModel.getRDFSNamedClass(RDFNames.Cls.PROPERTY);
        OWLNamedClass c2 = (OWLNamedClass) owlModel.getRDFResource(RDFNames.Cls.PROPERTY);
    }


    public void testCreateRDFUntypedResource() {
        RDFUntypedResource eri = owlModel.createRDFUntypedResource("http://aldi.de");
        assertEquals(DefaultRDFUntypedResource.class, eri.getClass());
    }


    public void testCreateSomeRestriction() {
        testCreate(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION, OWLSomeValuesFrom.class, DefaultOWLSomeValuesFrom.class);
    }


    public void testThingIsNamedCls() {
        assertTrue(owlModel.getOWLThingClass() instanceof OWLNamedClass);
    }


    public void testRDFIndividual() {
        RDFSNamedClass cls = owlModel.createRDFSNamedClass("Class");
        RDFIndividual individual = cls.createRDFIndividual("Test");
        assertFalse(individual instanceof OWLIndividual);
    }


    public void testOWLIndividual() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Class");
        OWLIndividual individual = cls.createOWLIndividual("Test");
        assertTrue(individual instanceof OWLIndividual);
    }


    public void testOWLDeprecatedClass() {
        RDFSNamedClass c = owlModel.getOWLDeprecatedClassClass();
        RDFResource resource = c.createInstance("MyClass");
        assertTrue(resource instanceof RDFSNamedClass);
    }


    public void testMixedClass() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Person");
        RDFResource instance = cls.createInstance("Instance");
        assertTrue(instance instanceof OWLIndividual);
        instance.addProtegeType(owlModel.getOWLNamedClassClass());
        final Collection types = instance.getProtegeTypes();
        assertEquals(2, types.size());
        assertTrue(types.contains(cls));
        assertTrue(types.contains(owlModel.getOWLNamedClassClass()));
        instance = owlModel.getRDFResource(instance.getName());
        assertTrue(instance instanceof OWLNamedClass);
    }
}
