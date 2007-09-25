package edu.stanford.smi.protegex.owl.ui.components.literaltable.tests;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.ui.components.literaltable.LiteralTableModel;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LiteralTableModelTestCase extends AbstractJenaTestCase {

    public void testHasValueRestriction() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Class");
        OWLDatatypeProperty property = owlModel.createOWLDatatypeProperty("property");
        final String hasValue = "Test";
        cls.addSuperclass(owlModel.createOWLHasValue(property, hasValue));
        OWLIndividual individual = cls.createOWLIndividual("Individual");
        LiteralTableModel tableModel = new LiteralTableModel(property);
        tableModel.setSubject(individual);
        assertEquals(1, tableModel.getRowCount());
        assertFalse(tableModel.isCellEditable(0, LiteralTableModel.COL_VALUE));
        assertFalse(tableModel.isCellEditable(0, LiteralTableModel.COL_TYPE));
    }


    public void testRangelessProperty() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Class");
        OWLDatatypeProperty property = owlModel.createOWLDatatypeProperty("property");
        OWLIndividual individual = cls.createOWLIndividual("Individual");
        LiteralTableModel tableModel = new LiteralTableModel(property);
        tableModel.setSubject(individual);
        assertEquals(0, tableModel.getRowCount());
        assertEquals(2, tableModel.getColumnCount());
        assertFalse(tableModel.isStringProperty());
        assertEquals(RDFSDatatype.class, tableModel.getColumnClass(LiteralTableModel.COL_TYPE));
    }


    public void testResourceValues() {
        RDFSNamedClass cls = owlModel.createRDFSNamedClass("Class");
        RDFProperty property = owlModel.createRDFProperty("property");
        RDFResource resource = cls.createInstance("Instance");
        resource.setPropertyValue(property, owlThing);
        LiteralTableModel tableModel = new LiteralTableModel(property);
        tableModel.setSubject(resource);
        tableModel.updateValues();
        assertEquals(1, tableModel.getRowCount());
        assertEquals(owlThing, tableModel.getObject(0));
    }


    public void testStringProperty() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Class");
        OWLDatatypeProperty property = owlModel.createOWLDatatypeProperty("property");
        cls.addSuperclass(owlModel.createOWLAllValuesFrom(property, owlModel.getXSDstring()));
        OWLIndividual individual = cls.createOWLIndividual("Individual");
        LiteralTableModel tableModel = new LiteralTableModel(property);
        tableModel.setSubject(individual);
        assertEquals(0, tableModel.getRowCount());
        assertEquals(2, tableModel.getColumnCount());
        assertTrue(tableModel.isStringProperty());
        assertEquals(String.class, tableModel.getColumnClass(LiteralTableModel.COL_LANG));
        individual.addPropertyValue(property, owlModel.createRDFSLiteral("Value", "Lang"));
        tableModel.updateValues();
        assertEquals(1, tableModel.getRowCount());
        assertEquals("Value", tableModel.getValueAt(0, LiteralTableModel.COL_VALUE));
        assertEquals("Lang", tableModel.getValueAt(0, LiteralTableModel.COL_LANG));
        assertTrue(tableModel.isCellEditable(0, LiteralTableModel.COL_LANG));
    }


    public void testSubPropertyValues() {
        OWLNamedClass cls = owlModel.createOWLNamedClass("Person");
        OWLDatatypeProperty superproperty = owlModel.createOWLDatatypeProperty("hasPrice");
        OWLDatatypeProperty subproperty = owlModel.createOWLDatatypeProperty("hasSpecialPrice");
        subproperty.addSuperproperty(superproperty);
        RDFResource subject = cls.createOWLIndividual("Individual");
        RDFSLiteral value = owlModel.createRDFSLiteral("42", owlModel.getXSDint());
        subject.addPropertyValue(subproperty, value);
        LiteralTableModel tableModel = new LiteralTableModel(superproperty);
        tableModel.setSubject(subject);
        assertEquals(1, tableModel.getRowCount());
        assertEquals(value, tableModel.getObject(0));
        assertFalse(tableModel.isCellEditable(0, LiteralTableModel.COL_VALUE));
        assertFalse(tableModel.isCellEditable(0, LiteralTableModel.COL_TYPE));
    }


    public void testRDFPropertyWithXSDIntRange() {
        RDFSNamedClass cls = owlModel.createRDFSNamedClass("Class");
        RDFResource subject = cls.createRDFIndividual("Individual");
        RDFProperty property = owlModel.createRDFProperty("property");
        property.setRange(owlModel.getXSDint());
        property.setDomain(cls);
        subject.addPropertyValue(property, new Integer(42));
        LiteralTableModel tableModel = new LiteralTableModel(property);
        tableModel.setSubject(subject);
        assertEquals(1, tableModel.getRowCount());
        assertTrue(tableModel.isCellEditable(0, LiteralTableModel.COL_VALUE));
        assertTrue(tableModel.isDeleteEnabled(new int[]{0}));
    }
}
