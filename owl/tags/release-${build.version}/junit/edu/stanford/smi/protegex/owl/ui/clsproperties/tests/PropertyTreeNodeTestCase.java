package edu.stanford.smi.protegex.owl.ui.clsproperties.tests;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.ui.clsproperties.PropertyTreeNode;
import edu.stanford.smi.protegex.owl.ui.clsproperties.RestrictionTreeNode;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyTreeNodeTestCase extends AbstractJenaTestCase {

    public void testDuplicateRestriction() {
        RDFProperty property = owlModel.createOWLObjectProperty("property");
        OWLNamedClass baseClass = owlModel.createOWLNamedClass("Base");
        OWLNamedClass middleClass = (OWLNamedClass) owlModel.createSubclass("Middle", baseClass);
        OWLNamedClass leafClass = (OWLNamedClass) owlModel.createSubclass("Leaf", middleClass);
        baseClass.addSuperclass(owlModel.createOWLSomeValuesFrom(property, baseClass));
        middleClass.addSuperclass(owlModel.createOWLSomeValuesFrom(property, baseClass));
        PropertyTreeNode node = new PropertyTreeNode(null, leafClass, property, false);
        assertEquals(1, node.getChildCount());
        RestrictionTreeNode childNode = (RestrictionTreeNode) node.getRestrictionTreeNode(0);
        assertTrue(childNode.isInherited());
        assertEquals(baseClass.getName(), childNode.getFillerText());
        assertEquals(middleClass, childNode.getInheritedFromClass());
    }


    public void testOWLSomeValuesFromNonRedundant() {
        RDFProperty property = owlModel.createOWLObjectProperty("property");
        OWLNamedClass baseClass = owlModel.createOWLNamedClass("Base");
        RDFSNamedClass otherFiller = owlModel.getRDFSNamedClassClass();
        baseClass.addSuperclass(owlModel.createOWLSomeValuesFrom(property, otherFiller));
        OWLNamedClass cls = owlModel.createOWLNamedSubclass("Class", baseClass);
        cls.addSuperclass(owlModel.createOWLSomeValuesFrom(property, cls));
        PropertyTreeNode node = new PropertyTreeNode(null, cls, property, false);
        assertEquals(2, node.getChildCount());
    }


    public void testOWLSomeValuesFromRedundant() {
        RDFProperty property = owlModel.createOWLObjectProperty("property");
        OWLNamedClass baseClass = owlModel.createOWLNamedClass("Base");
        baseClass.addSuperclass(owlModel.createOWLSomeValuesFrom(property, baseClass));
        OWLNamedClass cls = owlModel.createOWLNamedSubclass("Class", baseClass);
        cls.addSuperclass(owlModel.createOWLSomeValuesFrom(property, cls));
        PropertyTreeNode node = new PropertyTreeNode(null, cls, property, false);
        assertEquals(1, node.getChildCount());
        RestrictionTreeNode childNode = (RestrictionTreeNode) node.getRestrictionTreeNode(0);
        assertFalse(childNode.isInherited());
        assertEquals(cls.getName(), childNode.getFillerText());
    }


    public void testDuplicateQCR1() {
        OWLNamedClass qualifier = owlModel.createOWLNamedClass("A");
        RDFProperty property = owlModel.createOWLObjectProperty("property");
        OWLNamedClass restrictedClass = owlModel.createOWLNamedClass("Class");
        restrictedClass.addSuperclass(owlModel.createOWLMinCardinality(property, 1, qualifier));
        restrictedClass.addSuperclass(owlModel.createOWLMinCardinality(property, 2));
        PropertyTreeNode node = new PropertyTreeNode(null, restrictedClass, property, false);
        assertEquals(2, node.getChildCount());
    }


    public void testDuplicateQCR2() {
        OWLNamedClass qualifier = owlModel.createOWLNamedClass("A");
        RDFProperty property = owlModel.createOWLObjectProperty("property");
        OWLNamedClass restrictedClass = owlModel.createOWLNamedClass("Class");
        restrictedClass.addSuperclass(owlModel.createOWLMinCardinality(property, 2));
        restrictedClass.addSuperclass(owlModel.createOWLMinCardinality(property, 1, qualifier));
        PropertyTreeNode node = new PropertyTreeNode(null, restrictedClass, property, false);
        assertEquals(2, node.getChildCount());
    }


    public void testDuplicateQCR3() {
        OWLNamedClass qualifier = owlModel.createOWLNamedClass("A");
        RDFProperty property = owlModel.createOWLObjectProperty("property");
        OWLNamedClass restrictedClass = owlModel.createOWLNamedClass("Class");
        restrictedClass.addSuperclass(owlModel.createOWLMinCardinality(property, 1, qualifier));
        restrictedClass.addSuperclass(owlModel.createOWLMinCardinality(property, 2, qualifier));
        PropertyTreeNode node = new PropertyTreeNode(null, restrictedClass, property, false);
        assertEquals(2, node.getChildCount());
    }
}
