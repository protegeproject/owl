package edu.stanford.smi.protegex.owl.jena.creator.tests;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOntologiesTestCase extends AbstractJenaCreatorTestCase {

    public void testDefaultOntology() {
        OntModel newModel = runJenaCreator();
        assertSize(1, newModel.listOntologies());
        Ontology ontology = (Ontology) newModel.listOntologies().next();
        assertEquals(newModel.getNsPrefixURI(""), ontology.getURI() + "#");
    }


    public void testOntologyProperty() {
        String priorVersion = "http://old.de#";
        owlModel.getDefaultOWLOntology().addPriorVersion(priorVersion);
        OntModel newModel = runJenaCreator();
        assertSize(1, newModel.listOntologies());
        Ontology ontology = (Ontology) newModel.listOntologies().next();
        Literal value = (Literal) ontology.getPropertyValue(OWL.priorVersion);
        assertEquals(priorVersion, value.getString());
    }


    public void testOntologyWithNiceNamespace() {
        owlModel.createOWLNamedClass("Cls");
        String namespace = "http://aldi.de/ont";
        owlModel.getNamespaceManager().setDefaultNamespace(namespace + "#");
        OntModel newModel = runJenaCreator();
        assertSize(1, newModel.listOntologies());
        Ontology ontology = (Ontology) newModel.listOntologies().next();
        assertEquals(namespace, ontology.getURI());
    }


    public void testOntologyWithUglyNamespace() {
        owlModel.getDefaultOWLOntology().addImports("http://www.owl-ontologies.com/travel.owl");
        owlModel.createOWLNamedClass("Cls");
        String namespace = "http://aldi.de/ont/";
        owlModel.getNamespaceManager().setDefaultNamespace(namespace);
        OntModel newModel = runJenaCreator();
        assertSize(2, newModel.listOntologies());
        Ontology ontology = (Ontology) newModel.getOntology(namespace);
        assertEquals(namespace, ontology.getURI());
        assertSize(1, ontology.listImports());
    }
}
