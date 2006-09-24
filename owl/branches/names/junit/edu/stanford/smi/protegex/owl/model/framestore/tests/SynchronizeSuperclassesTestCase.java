package edu.stanford.smi.protegex.owl.model.framestore.tests;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SynchronizeSuperclassesTestCase extends AbstractJenaTestCase {

    public void testDefaultSuperclass() {
        RDFProperty subClassOfProperty = owlModel.getRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
        RDFSNamedClass cls = owlModel.createRDFSNamedClass("Class");
        assertEquals(1, cls.getSuperclassCount());
        assertContains(owlThing, cls.getSuperclasses(false));
        assertSize(1, cls.getPropertyValues(subClassOfProperty));
        assertContains(owlThing, cls.getPropertyValues(subClassOfProperty));
    }


    public void testChangingSuperclass() {
        RDFProperty subClassOfProperty = owlModel.getRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
        RDFSNamedClass superclass = owlModel.createRDFSNamedClass("Superclass");
        RDFSNamedClass subclass = owlModel.createRDFSNamedClass("Subclass");
        subclass.addSuperclass(superclass);
        assertSize(2, subclass.getPropertyValues(subClassOfProperty));
        assertContains(owlThing, subclass.getPropertyValues(subClassOfProperty));
        assertContains(superclass, subclass.getPropertyValues(subClassOfProperty));
        subclass.removeSuperclass(owlThing);
        assertSize(1, subclass.getPropertyValues(subClassOfProperty));
        assertContains(superclass, subclass.getPropertyValues(subClassOfProperty));
    }


    public void testCreateSuperclass() {
        RDFProperty subClassOfProperty = owlModel.getRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
        RDFSNamedClass superclass = owlModel.createRDFSNamedClass("Superclass");
        RDFSNamedClass subclass = owlModel.createRDFSNamedSubclass("Subclass", superclass);
        assertSize(1, subclass.getPropertyValues(subClassOfProperty));
        assertContains(superclass, subclass.getPropertyValues(subClassOfProperty));
    }


    public void testAnonymousSuperclassToImportedClass() throws Exception {
        RDFProperty subClassOfProperty = owlModel.getRDFProperty(RDFSNames.Slot.SUB_CLASS_OF);
        loadRemoteOntology("importTravel.owl");
        OWLNamedClass c = owlModel.getOWLNamedClass("travel:Activity");
        assertNotNull(c);
        assertSize(0, c.getPropertyValues(subClassOfProperty));
        OWLAnonymousClass anon = owlModel.createOWLComplementClass(c);
        c.addSuperclass(anon);
        Collection supers = c.getPropertyValues(subClassOfProperty);
        assertSize(2, supers);
        assertContains(owlThing, supers);
        assertContains(anon, supers);
        TripleStore topTS = owlModel.getTripleStoreModel().getTopTripleStore();
        TripleStore importedTS = owlModel.getTripleStoreModel().getTripleStore(2);
        assertTrue(topTS.contains(c, subClassOfProperty, anon));
        assertTrue(importedTS.contains(c, subClassOfProperty, owlThing));
    }


    public void testOWLEquivalentClassWithNamedClass() {
        OWLNamedClass a = owlModel.createOWLNamedClass("A");
        OWLNamedClass b = owlModel.createOWLNamedClass("B");
        a.addEquivalentClass(b);
        assertContains(b, a.getEquivalentClasses());
        assertContains(b, a.getPropertyValues(owlModel.getOWLEquivalentClassProperty()));
    }


    public void testSuperclassOfRDFSNamedClass() {

        final RDFProperty rdfsSubClassOfProperty = owlModel.getRDFSSubClassOfProperty();
        RDFSNamedClass subclass = owlModel.createRDFSNamedClass("Sub");
        RDFSNamedClass superclass = owlModel.createRDFSNamedClass("Super");

        subclass.addSuperclass(superclass);

        assertSize(2, subclass.getSuperclasses(false));
        assertContains(superclass, subclass.getSuperclasses(false));
        assertContains(owlThing, subclass.getSuperclasses(false));

        assertSize(2, subclass.getPropertyValues(rdfsSubClassOfProperty));
        assertContains(superclass, subclass.getPropertyValues(rdfsSubClassOfProperty));
        assertContains(owlThing, subclass.getPropertyValues(rdfsSubClassOfProperty));

        subclass.removeSuperclass(superclass);

        assertSize(1, subclass.getSuperclasses(false));
        assertContains(owlThing, subclass.getSuperclasses(false));

        assertSize(1, subclass.getPropertyValues(rdfsSubClassOfProperty));
        assertContains(owlThing, subclass.getPropertyValues(rdfsSubClassOfProperty));
    }
}
