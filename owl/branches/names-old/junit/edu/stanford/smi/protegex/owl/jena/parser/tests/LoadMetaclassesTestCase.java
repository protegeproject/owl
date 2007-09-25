package edu.stanford.smi.protegex.owl.jena.parser.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LoadMetaclassesTestCase extends AbstractJenaTestCase {

    public void testLoadMetaclassInstances() throws Exception {
        loadRemoteOntology("metaclasses.owl");
        OWLNamedClass a = owlModel.getOWLNamedClass("A");
        assertSize(1, a.getSuperclasses(false));
        assertContains(owlModel.getOWLNamedClassClass(), a.getSuperclasses(false));
        OWLNamedClass b = owlModel.getOWLNamedClass("B");
        assertSize(1, b.getSuperclasses(false));
        assertContains(a, b.getSuperclasses(false));

        OWLNamedClass instanceClsA = owlModel.getOWLNamedClass("InstanceClsA");
        assertSize(1, instanceClsA.getProtegeTypes());
        assertContains(a, instanceClsA.getProtegeTypes());

        OWLNamedClass instanceClsB = owlModel.getOWLNamedClass("InstanceClsB");
        assertSize(1, instanceClsB.getProtegeTypes());
        assertContains(b, instanceClsB.getProtegeTypes());
    }


    public void testLoadRDFMetaclass() throws Exception {
        loadRemoteOntology("rdfmetaclassbug.owl");
        RDFSNamedClass aClass = owlModel.getRDFSNamedClass("Class_01");
        assertNotNull(aClass);
        ((Cls) aClass).setDirectOwnSlotValue(owlModel.getSlot(RDFSNames.Slot.COMMENT), "comment");
    }


    public void testLoadMyGeneric() throws Exception {
        loadRemoteOntology("mygeneric.owl");
    }


    public void testLoadRecursiveMetaclass() throws Exception {
        OWLNamedClass oldClass = owlModel.createOWLNamedSubclass("Metaclass", owlModel.getOWLNamedClassClass());
        oldClass.setRDFType(oldClass);
        JenaOWLModel newModel = reload(owlModel);
        OWLNamedClass newClass = newModel.getOWLNamedClass(oldClass.getName());
        assertSize(1, newClass.getRDFTypes());
        assertEquals(newClass, newClass.getRDFType());
    }
}
