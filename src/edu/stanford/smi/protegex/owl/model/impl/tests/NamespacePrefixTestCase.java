package edu.stanford.smi.protegex.owl.model.impl.tests;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.util.Collection;

/**
 * Test cases for the Protege-only part of the namespace support.
 *
 * @author Holger Knublauch
 */
public class NamespacePrefixTestCase extends AbstractJenaTestCase {

    public void testDefaultOntologyName() throws Exception {
        OWLOntology ontology = owlModel.getDefaultOWLOntology();
        assertEquals(":", ontology.getName());
        assertEquals(OWLNamespaceManager.DEFAULT_DEFAULT_BASE, ontology.getURI());
    }


    public void testOtherOntologyName() {
        String namespaceBase = "http://aldi.de";
        String namespace = namespaceBase + "#";
        String prefix = "aldi";
        owlModel.getNamespaceManager().setPrefix(namespace, prefix);
        OWLOntology ontology = owlModel.createOWLOntology(prefix + ":", namespace);
        assertEquals(namespaceBase, ontology.getURI());
    }


    public void testDefaultPrefix() {
        edu.stanford.smi.protege.model.Slot prefixesSlot = owlModel.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES);
        NamespaceManager nsm = owlModel.getNamespaceManager();
        assertEquals(OWLNamespaceManager.DEFAULT_DEFAULT_NAMESPACE, nsm.getDefaultNamespace());

        final String CLS_NAME = "Person";
        OWLNamedClass cls = owlModel.createOWLNamedClass(CLS_NAME);
        assertEquals(nsm.getDefaultNamespace(), cls.getNamespace());
        assertEquals(nsm.getDefaultNamespace() + CLS_NAME, cls.getURI());
        assertEquals(CLS_NAME, cls.getLocalName());
        assertEquals(CLS_NAME, cls.getBrowserText());

        Collection values = ((Instance) owlModel.getDefaultOWLOntology()).getDirectOwnSlotValues(prefixesSlot);
        assertEquals(5, values.size());
    }


    public void testChangePrefix() {
        edu.stanford.smi.protege.model.Slot prefixesSlot = owlModel.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES);
        NamespaceManager nsm = owlModel.getNamespaceManager();
        assertEquals(OWLNamespaceManager.DEFAULT_DEFAULT_NAMESPACE, nsm.getDefaultNamespace());

        String NAMESPACE = "http://aldi.de#";
        String ALDI = "aldi";
        nsm.setPrefix(NAMESPACE, ALDI);
        Collection values = ((Instance) owlModel.getDefaultOWLOntology()).getDirectOwnSlotValues(prefixesSlot);
        assertEquals(6, values.size());
        assertTrue(values.contains(ALDI + ":" + NAMESPACE));

        String LIDL = "lidl";
        nsm.setPrefix(NAMESPACE, LIDL);
        values = ((Instance) owlModel.getDefaultOWLOntology()).getDirectOwnSlotValues(prefixesSlot);
        assertEquals(6, values.size());
        assertFalse(values.contains(ALDI + ":" + NAMESPACE));
        assertTrue(values.contains(LIDL + ":" + NAMESPACE));
    }


    public void testOntologyInstanceValues() {
        edu.stanford.smi.protege.model.Slot prefixesSlot = owlModel.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES);
        NamespaceManager nsm = owlModel.getNamespaceManager();
        assertEquals(OWLNamespaceManager.DEFAULT_DEFAULT_NAMESPACE, nsm.getDefaultNamespace());

        final String CLS_NAME = "Person";
        OWLNamedClass cls = owlModel.createOWLNamedClass(CLS_NAME);

        String NAMESPACE = "http://aldi.de#";
        String ALDI = "aldi";
        nsm.setPrefix(NAMESPACE, ALDI);
        Collection values = ((Instance) owlModel.getDefaultOWLOntology()).getDirectOwnSlotValues(prefixesSlot);
        assertEquals(6, values.size());
        assertTrue(values.contains(ALDI + ":" + NAMESPACE));

        OWLNamedClass otherCls = owlModel.createOWLNamedClass(ALDI + ":" + CLS_NAME);
        assertEquals(CLS_NAME, otherCls.getLocalName());
        assertEquals(NAMESPACE, otherCls.getNamespace());

        String LIDL = "lidl";
        nsm.setPrefix(NAMESPACE, LIDL);
        values = ((Instance) owlModel.getDefaultOWLOntology()).getDirectOwnSlotValues(prefixesSlot);
        assertEquals(6, values.size());
        assertTrue(values.contains(LIDL + ":" + NAMESPACE));
    }

    /*public void testWrayJohnson() throws Exception {
        String uri = "http://protege.stanford.edu/plugins/owl/owl-library/koala.owl";
        URI tempFileURI = File.createTempFile("WrayJohnson", ".owl").toURI();
        OWLOntology ont = owlModel.getDefaultOWLOntology();
        ont.addImports(uri);
        owlModel.save(tempFileURI, FileUtils.langXMLAbbrev, new Vector());
        owlModel.load(tempFileURI, FileUtils.langXMLAbbrev, new Vector());
        String namespace = uri + "#";
        NamespaceManager mgr = owlModel.getNamespaceManager();
        mgr.setDefaultNamespace(namespace);
        String importPrefix = mgr.getPrefix(namespace);
        mgr.removePrefix(importPrefix);
        OWLNamedClass Koala = owlModel.getOWLNamedClass("Koala");
        OWLNamedClass DryEucalyptForest = owlModel.getOWLNamedClass("DryEucalyptForest");
        OWLIndividual female = owlModel.getOWLIndividual("female");
        OWLObjectProperty hasGender = owlModel.getOWLObjectProperty("hasGender");
        OWLObjectProperty hasHabitat = owlModel.getOWLObjectProperty("hasHabitat");
        OWLIndividual Kime = Koala.createOWLIndividual("Kime");
        OWLIndividual FingalValley = DryEucalyptForest.createOWLIndividual("Fingal Valley");
        Kime.setPropertyValue(hasGender, female);
        Kime.setPropertyValue(hasHabitat, FingalValley);
        owlModel.save(tempFileURI, FileUtils.langXMLAbbrev, new Vector());
    }*/
}
