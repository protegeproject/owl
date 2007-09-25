package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protegex.owl.model.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLEnumeratedClassTestCase extends AbstractTripleStoreTestCase {

    public void testCreateOWLEnumeratedClass() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Class");
        OWLIndividual a = cls.createOWLIndividual("a");
        OWLIndividual b = cls.createOWLIndividual("b");
        RDFResource enumR = createRDFResource(null);
        ts.add(enumR, rdfTypeProperty, owlModel.getOWLNamedClassClass());
        RDFResource first = createRDFResource(null);
        RDFResource second = createRDFResource(null);
        ts.add(enumR, owlModel.getOWLOneOfProperty(), first);
        ts.add(first, owlModel.getRDFFirstProperty(), a);
        ts.add(first, owlModel.getRDFRestProperty(), second);
        ts.add(second, owlModel.getRDFFirstProperty(), b);
        ts.add(second, owlModel.getRDFRestProperty(), owlModel.getRDFNil());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        RDFSClass enumClass = (RDFSClass) owlModel.getRDFResource(enumR.getName());
        assertTrue(enumClass instanceof OWLEnumeratedClass);
        OWLEnumeratedClass enumeratedClass = (OWLEnumeratedClass) enumClass;
        assertSize(2, enumeratedClass.getOneOf());
        assertContains(a, enumeratedClass.getOneOf());
        assertContains(b, enumeratedClass.getOneOf());
    }
}
