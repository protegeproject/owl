package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLLogicalClassesTestCase extends AbstractTripleStoreTestCase {

    public void testCreateComplementClass1() {
        RDFResource c = new DefaultRDFProperty(owlModel, createFrameID());
        c.setName("test");
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        ts.add(c, owlModel.getRDFProperty(OWLNames.Slot.COMPLEMENT_OF), owlModel.getOWLThingClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        OWLNamedClass namedClass = owlModel.getOWLNamedClass(c.getName());
        Frame f = namedClass.getDefinition();
        assertTrue(f instanceof OWLComplementClass);
        assertEquals(owlModel.getOWLThingClass(), ((OWLComplementClass) f).getComplement());
    }


    public void testCreateComplementClass2() {
        RDFResource c = new DefaultRDFProperty(owlModel, createFrameID());
        String name = "test";
        c.setName(name);
        ts.add(c, owlModel.getRDFProperty(OWLNames.Slot.COMPLEMENT_OF), owlModel.getOWLThingClass());
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();

        OWLNamedClass namedClass = owlModel.getOWLNamedClass(c.getName());
        Frame f = namedClass.getDefinition();
        assertTrue(f instanceof OWLComplementClass);
    }


    public void testCreateIntersectionClass() {
        OWLNamedClass classA = owlModel.createOWLNamedClass("A");
        OWLNamedClass classB = owlModel.createOWLNamedClass("B");

        RDFResource c = new DefaultRDFProperty(owlModel, createFrameID());
        c.setName("test");
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        RDFProperty operandsProperty = owlModel.getOWLIntersectionOfProperty();
        RDFResource nodeA = createRDFResource(null);
        ts.add(c, operandsProperty, nodeA);
        ts.add(nodeA, owlModel.getRDFFirstProperty(), classA);
        RDFResource nodeB = createRDFResource(null);
        ts.add(nodeA, owlModel.getRDFRestProperty(), nodeB);
        ts.add(nodeB, owlModel.getRDFFirstProperty(), classB);
        ts.add(nodeB, owlModel.getRDFRestProperty(), owlModel.getRDFNil());
        owlModel.getTripleStoreModel().endTripleStoreChanges();

        OWLNamedClass namedClass = owlModel.getOWLNamedClass(c.getName());
        Frame f = namedClass.getDefinition();
        assertTrue(f instanceof OWLIntersectionClass);
        OWLIntersectionClass intersectionClass = (OWLIntersectionClass) f;
        assertContains(classA, intersectionClass.getOperands());
        assertContains(classB, intersectionClass.getOperands());
    }


    public void testCreateUnionClass() {
        OWLNamedClass classA = owlModel.createOWLNamedClass("A");
        OWLNamedClass classB = owlModel.createOWLNamedClass("B");

        RDFResource c = new DefaultRDFProperty(owlModel, createFrameID());
        c.setName("test");
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        RDFProperty operandsProperty = owlModel.getOWLUnionOfProperty();
        RDFResource nodeB = createRDFResource(null);
        RDFResource nodeA = createRDFResource(null);
        ts.add(nodeA, owlModel.getRDFRestProperty(), nodeB);
        ts.add(nodeB, owlModel.getRDFFirstProperty(), classB);
        ts.add(nodeB, owlModel.getRDFRestProperty(), owlModel.getRDFNil());
        ts.add(c, operandsProperty, nodeA);
        ts.add(nodeA, owlModel.getRDFFirstProperty(), classA);
        owlModel.getTripleStoreModel().endTripleStoreChanges();

        OWLNamedClass namedClass = owlModel.getOWLNamedClass(c.getName());
        Frame f = namedClass.getDefinition();
        assertTrue(f instanceof OWLUnionClass);
        OWLUnionClass unionClass = (OWLUnionClass) f;
        assertContains(classA, unionClass.getOperands());
        assertContains(classB, unionClass.getOperands());
    }
}
