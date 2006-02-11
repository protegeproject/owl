package edu.stanford.smi.protegex.owl.storage.tests;

import com.hp.hpl.jena.ontology.OntProperty;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.test.APITestCase;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.storage.ProtegeSaver;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeSaverTestCase extends APITestCase {

    private Project project;


    private JenaOWLModel runCopier() {
        Collection errors = new ArrayList();
        project = new Project(null, errors);
        final JenaKnowledgeBaseFactory factory = new JenaKnowledgeBaseFactory();
        project.setKnowledgeBaseFactory(factory);
        project.createDomainKnowledgeBase(factory, errors, false);
        JenaOWLModel target = (JenaOWLModel) project.getKnowledgeBase();
        new ProtegeSaver(getDomainKB(), target).run();
        return target;
    }


    public void testDocumentation() {
        KnowledgeBase kb = getDomainKB();
        Cls cls = kb.createCls("Cls", kb.getRootClses());
        cls.setDocumentation("Test");

        JenaOWLModel target = runCopier();

        OWLNamedClass newCls = target.getOWLNamedClass(cls.getName());
        assertEquals(0, ((Cls) newCls).getDirectOwnSlotValues(target.getSlot(Model.Slot.DOCUMENTATION)).size());
        assertEquals(1, newCls.getPropertyValues(target.getRDFSCommentProperty()).size());
        assertEquals("Test", newCls.getPropertyValue(target.getRDFSCommentProperty()));
    }


    public void testInverseSlots() {
        KnowledgeBase kb = getDomainKB();
        Slot aSlot = kb.createSlot("A");
        aSlot.setValueType(ValueType.INSTANCE);
        Slot bSlot = kb.createSlot("B");
        bSlot.setValueType(ValueType.INSTANCE);
        aSlot.setInverseSlot(bSlot);

        JenaOWLModel target = runCopier();

        OWLObjectProperty newASlot = (OWLObjectProperty) target.getSlot(aSlot.getName());
        OWLObjectProperty newBSlot = (OWLObjectProperty) target.getSlot(bSlot.getName());
        assertNotNull(newASlot.getInverseProperty());
        assertEquals(newASlot.getInverseProperty(), newBSlot);
        OntProperty aProperty = target.getOntModel().getOntProperty(newASlot.getURI());
        OntProperty bProperty = target.getOntModel().getOntProperty(newBSlot.getURI());
        assertEquals(1, Jena.set(aProperty.listInverseOf()).size());
        assertEquals(aProperty, bProperty.getInverseOf());
        assertEquals(1, Jena.set(bProperty.listInverseOf()).size());
        assertEquals(bProperty, aProperty.getInverseOf());
    }


    public void testDatatypeRange() {
        KnowledgeBase kb = getDomainKB();
        Slot aSlot = kb.createSlot("A");
        aSlot.setValueType(ValueType.INTEGER);

        JenaOWLModel target = runCopier();
        OWLDatatypeProperty property = target.getOWLDatatypeProperty(aSlot.getName());
        assertEquals(target.getXSDint(), property.getRange());
    }


    public void testOWLFunctionalProperty() {
        KnowledgeBase kb = getDomainKB();
        Slot slot = kb.createSlot("slot");
        slot.setAllowsMultipleValues(false);

        JenaOWLModel target = runCopier();
        RDFProperty property = target.getRDFProperty(slot.getName());
        assertTrue(property.isFunctional());
    }


    public void testRDFSDomainEmpty() {
        KnowledgeBase kb = getDomainKB();
        Slot slot = kb.createSlot("slot");

        JenaOWLModel target = runCopier();
        RDFProperty property = target.getRDFProperty(slot.getName());
        assertTrue(property.getUnionDomain().isEmpty());
    }


    public void testRDFSDomainOneClass() {
        KnowledgeBase kb = getDomainKB();
        Cls cls = kb.createCls("Class", kb.getRootClses());
        Slot slot = kb.createSlot("slot");
        cls.addDirectTemplateSlot(slot);

        JenaOWLModel target = runCopier();
        RDFProperty property = target.getRDFProperty(slot.getName());
        assertEquals(1, property.getUnionDomain().size());
        assertEquals(property.getDomain(false).getName(), cls.getName());
    }
}
