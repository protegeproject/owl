package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLLogicalClassesTestCase extends AbstractTripleStoreTestCase {
    
    private int frameCounter = 0;
    public FrameID createFrameID() {
        return new FrameID("OWLTestEntity" + frameCounter++);
    }

    public void testCreateComplementClass1() {
        RDFResource c = new DefaultRDFProperty(owlModel, createFrameID());
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        ts.add(c, owlModel.getRDFProperty(OWLNames.Slot.COMPLEMENT_OF), owlModel.getOWLThingClass());
        OWLNamedClass namedClass = owlModel.getOWLNamedClass(c.getName());
        Frame f = namedClass.getDefinition();
        assertTrue(f instanceof OWLComplementClass);
        assertEquals(owlModel.getOWLThingClass(), ((OWLComplementClass) f).getComplement());
    }


    public void testCreateComplementClass2() {
        RDFResource c = new DefaultRDFProperty(owlModel, createFrameID());
        ts.add(c, owlModel.getRDFProperty(OWLNames.Slot.COMPLEMENT_OF), owlModel.getOWLThingClass());
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());

        OWLNamedClass namedClass = owlModel.getOWLNamedClass(c.getName());
        Frame f = namedClass.getDefinition();
        assertTrue(f instanceof OWLComplementClass);
    }


    public void testCreateIntersectionClass() {
        OWLNamedClass classA = owlModel.createOWLNamedClass("A");
        OWLNamedClass classB = owlModel.createOWLNamedClass("B");

        RDFResource c = new DefaultRDFProperty(owlModel, createFrameID());
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        RDFProperty operandsProperty = owlModel.getOWLIntersectionOfProperty();
        RDFResource nodeA = createRDFResource(null);
        ts.add(c, operandsProperty, nodeA);
        ts.add(nodeA, owlModel.getRDFFirstProperty(), classA);
        RDFResource nodeB = createRDFResource(null);
        ts.add(nodeA, owlModel.getRDFRestProperty(), nodeB);
        ts.add(nodeB, owlModel.getRDFFirstProperty(), classB);
        ts.add(nodeB, owlModel.getRDFRestProperty(), owlModel.getRDFNil());

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
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        RDFProperty operandsProperty = owlModel.getOWLUnionOfProperty();
        RDFResource nodeB = createRDFResource(null);
        RDFResource nodeA = createRDFResource(null);
        ts.add(nodeA, owlModel.getRDFRestProperty(), nodeB);
        ts.add(nodeB, owlModel.getRDFFirstProperty(), classB);
        ts.add(nodeB, owlModel.getRDFRestProperty(), owlModel.getRDFNil());
        ts.add(c, operandsProperty, nodeA);
        ts.add(nodeA, owlModel.getRDFFirstProperty(), classA);

        OWLNamedClass namedClass = owlModel.getOWLNamedClass(c.getName());
        Frame f = namedClass.getDefinition();
        assertTrue(f instanceof OWLUnionClass);
        OWLUnionClass unionClass = (OWLUnionClass) f;
        assertContains(classA, unionClass.getOperands());
        assertContains(classB, unionClass.getOperands());
    }
}
