package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateRDFSDomainTestCase extends AbstractTripleStoreTestCase {


    public void testDefaultDomain() {
        RDFResource propertyFrame = createRDFResource("property");
        ts.add(propertyFrame, owlModel.getRDFTypeProperty(), owlModel.getRDFPropertyClass());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        RDFProperty property = owlModel.getRDFProperty(propertyFrame.getName());
        assertSize(1, ((Slot) property).getDirectDomain());
        assertContains(owlThing, ((Slot) property).getDirectDomain());
    }


    public void testDefaultDomainOfSubProperty() {
        RDFResource superpropertyFrame = createRDFResource("superproperty");
        RDFResource subpropertyFrame = createRDFResource("subproperty");
        ts.add(superpropertyFrame, owlModel.getRDFTypeProperty(), owlModel.getRDFPropertyClass());
        ts.add(subpropertyFrame, owlModel.getRDFTypeProperty(), owlModel.getRDFPropertyClass());
        ts.add(subpropertyFrame, owlModel.getRDFSSubPropertyOfProperty(), superpropertyFrame);
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        Slot subproperty = owlModel.getRDFProperty(subpropertyFrame.getName());
        assertSize(0, subproperty.getDirectDomain());
        Slot superproperty = owlModel.getRDFProperty(superpropertyFrame.getName());
        assertSize(1, superproperty.getDirectDomain());
        assertContains(owlThing, ((Slot) superproperty).getDirectDomain());
    }


    public void testSimpleDomain() {
        RDFSNamedClass cls = owlModel.createRDFSNamedClass("Class");
        RDFResource propertyFrame = createRDFResource("property");
        ts.add(propertyFrame, owlModel.getRDFTypeProperty(), owlModel.getRDFPropertyClass());
        ts.add(propertyFrame, owlModel.getRDFSDomainProperty(), cls);
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        RDFProperty property = owlModel.getRDFProperty(propertyFrame.getName());
        assertSize(1, ((Slot) property).getDirectDomain());
        assertContains(cls, ((Slot) property).getDirectDomain());
        assertSize(1, ((Cls) cls).getDirectTemplateSlots());
        assertContains(property, ((Cls) cls).getDirectTemplateSlots());
    }


    public void testMultipleDomains() {
        RDFSNamedClass clsA = owlModel.createRDFSNamedClass("A");
        RDFSNamedClass clsB = owlModel.createRDFSNamedClass("B");
        RDFResource propertyR = createRDFResource("property");
        ts.add(propertyR, owlModel.getRDFTypeProperty(), owlModel.getRDFPropertyClass());
        ts.add(propertyR, owlModel.getRDFSDomainProperty(), clsA);
        ts.add(propertyR, owlModel.getRDFSDomainProperty(), clsB);
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        Slot slot = owlModel.getSlot(propertyR.getName());
        assertSize(1, slot.getDirectDomain());
        assertContains(clsA, slot.getDirectDomain());
    }


    public void testUnionDomains() {
        RDFSNamedClass clsA = owlModel.createRDFSNamedClass("A");
        RDFSNamedClass clsB = owlModel.createRDFSNamedClass("B");
        OWLUnionClass unionClass = owlModel.createOWLUnionClass();
        unionClass.addOperand(clsA);
        unionClass.addOperand(clsB);
        RDFResource propertyR = createRDFResource("property");
        ts.add(propertyR, owlModel.getRDFTypeProperty(), owlModel.getRDFPropertyClass());
        ts.add(propertyR, owlModel.getRDFSDomainProperty(), unionClass);
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        Slot slot = owlModel.getSlot(propertyR.getName());
        assertSize(2, slot.getDirectDomain());
        assertContains(clsA, slot.getDirectDomain());
        assertContains(clsB, slot.getDirectDomain());
    }
}
