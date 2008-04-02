package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLEquivalentClassTestCase extends AbstractTripleStoreTestCase {


    public void testDefaultSuperclassDespiteRestriction() {
        OWLObjectProperty property = owlModel.createOWLObjectProperty("property");
        String name = "Class";
        RDFResource c = createRDFResource(name);
        RDFResource restriction = createRDFResource(null);
        ts.add(c, owlModel.getOWLEquivalentClassProperty(), restriction);
        ts.add(restriction, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        ts.add(restriction, owlModel.getRDFProperty(OWLNames.Slot.CARDINALITY), new Integer(1));
        ts.add(restriction, owlModel.getRDFProperty(OWLNames.Slot.ON_PROPERTY), property);
        ts.add(c, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        Cls cls = owlModel.getOWLNamedClass(name);
        assertSize(2, cls.getDirectSuperclasses());
        assertContains(owlThing, cls.getDirectSuperclasses());
        assertContains(restriction, cls.getDirectSuperclasses());
        Cls r = owlModel.getCls(restriction.getName());
        assertSize(1, r.getDirectSuperclasses());
        assertContains(cls, r.getDirectSuperclasses());
    }


    public void testNamedEquivalentClass() {
        RDFResource classA = createRDFResource("classA");
        RDFResource classB = createRDFResource("classB");
        ts.add(classA, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        ts.add(classB, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass());
        ts.add(classA, owlModel.getOWLEquivalentClassProperty(), classB);
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        Cls clsA = owlModel.getOWLNamedClass(classA.getName());
        Cls clsB = owlModel.getOWLNamedClass(classB.getName());
        assertSize(2, clsA.getDirectSuperclasses());
        assertContains(owlThing, clsA.getDirectSuperclasses());
        assertContains(clsB, clsA.getDirectSuperclasses());
        assertSize(2, clsB.getDirectSuperclasses());
        assertContains(owlThing, clsB.getDirectSuperclasses());
        assertContains(clsA, clsB.getDirectSuperclasses());
    }


    public void testMakeNamedClassAnonymous() {
        RDFResource namedClassR = createRDFResource("class");
        ts.add(namedClassR, rdfTypeProperty, owlModel.getOWLNamedClassClass());
        ts.add(namedClassR, owlModel.getRDFProperty(OWLNames.Slot.COMPLEMENT_OF), owlThing);
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        OWLNamedClass namedClass = owlModel.getOWLNamedClass(namedClassR.getName());
        assertSize(1, namedClass.getEquivalentClasses());
        OWLComplementClass complementClass = (OWLComplementClass) namedClass.getEquivalentClasses().iterator().next();
        assertEquals(owlThing, complementClass.getComplement());
    }
}
