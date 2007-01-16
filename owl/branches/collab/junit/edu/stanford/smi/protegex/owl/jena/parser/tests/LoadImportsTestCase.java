package edu.stanford.smi.protegex.owl.jena.parser.tests;

import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LoadImportsTestCase extends AbstractJenaTestCase {

    public void testLoadImports() throws Exception {
        loadRemoteOntology("import-demo.owl");
        assertEquals("http://protege.stanford.edu/plugins/owl/owl-library/import-demo.owl#",
                     owlModel.getNamespaceManager().getDefaultNamespace());
        OWLNamedClass riqClass = owlModel.getOWLNamedClass("RottnestIslandQuokka");
        assertNotNull(riqClass);
        assertTrue(riqClass.isEditable());
        OWLNamedClass quokkaClass = owlModel.getOWLNamedClass("koala:Quokka");
        assertNotNull(quokkaClass);
        assertTrue(quokkaClass.isIncluded());
        Collection ontologies = owlModel.getCls(OWLNames.Cls.ONTOLOGY).getDirectInstances();
        assertSize(2, ontologies);
        assertContains(owlModel.getDefaultOWLOntology(), ontologies);
        Collection imports = owlModel.getDefaultOWLOntology().getImports();
        for (Iterator it = imports.iterator(); it.hasNext();) {
            Object o = it.next();
            assertTrue(o instanceof String);
        }
        URI koalaURI = new URI("http://protege.stanford.edu/plugins/owl/owl-library/koala.owl");
        RDFResource oi = owlModel.getOWLOntologyByURI(koalaURI);
        assertContains(oi, ontologies);
        assertEquals("koala:", oi.getName());
        assertTrue(oi.isIncluded());
        OWLNamedClass koalaCls = owlModel.getOWLNamedClass("koala:Koala");
        assertNotNull(koalaCls);
    }


    public void testLoadUglyImport() throws Exception {
        loadRemoteOntology("uglyImport.owl");
        assertEquals("http://aldi.de/ont/", owlModel.getNamespaceManager().getDefaultNamespace());
        OWLOntology oi = owlModel.getDefaultOWLOntology();
        assertSize(1, oi.getImports());
    }


    public void testLoadImportTravel() throws Exception {
        loadRemoteOntology("importTravel.owl");
        OWLNamedClass ratingClass = owlModel.getOWLNamedClass("travel:AccommodationRating");
        assertNotNull(ratingClass);
        final RDFSClass definition = ratingClass.getDefinition();
        assertTrue(definition instanceof OWLEnumeratedClass);
        OWLEnumeratedClass enumeratedClass = (OWLEnumeratedClass) definition;
        RDFResource type = enumeratedClass.getRDFType();
        assertEquals(OWLNames.Cls.NAMED_CLASS, type.getName());
    }


    public void testLoadIncrementalImport() throws Exception {
        URI uri = new URI(getRemoteOntologyRoot() + "travel.owl");
        String namespace = uri.toString() + "#";
        owlModel.getNamespaceManager().setPrefix(namespace, "travel");
        ProtegeOWLParser.addImport(owlModel, uri);
        owlModel.getDefaultOWLOntology().addImports(uri.toString());
    }


    public void testLoadIncrementalImportOWLS() throws Exception {
        URI uri = new URI("http://www.daml.org/services/owl-s/1.1/Profile.owl");
        String namespace = uri.toString() + "#";
        owlModel.getNamespaceManager().setPrefix(namespace, "profile");
        ProtegeOWLParser.addImport(owlModel, uri);
        owlModel.getDefaultOWLOntology().addImports(uri.toString());
    }


    public void testLoadIncrementalImportOWLS2() throws Exception {
        {
            URI uri = new URI("http://www.daml.org/services/owl-s/1.1/Process.owl");
            String namespace = uri.toString() + "#";
            owlModel.getNamespaceManager().setPrefix(namespace, "process");
            ProtegeOWLParser.addImport(owlModel, uri);
            owlModel.getDefaultOWLOntology().addImports(uri.toString());
        }
        {
            URI uri = new URI("http://www.daml.org/services/owl-s/1.1/Grounding.owl");
            ProtegeOWLParser.addImport(owlModel, uri);
            owlModel.getDefaultOWLOntology().addImports(uri.toString());
        }
    }


    public void testLoadOWLSIndividual() throws Exception {
        loadRemoteOntology("CompositeProcess.owl");
        RDFResource individual = owlModel.getRDFResource("MyIndividual");
        assertTrue(individual instanceof OWLIndividual);
    }


    public void testLoadIncrementalImportOfProtege() throws Exception {
        URI uri = new URI(ProtegeNames.FILE);
        String namespace = uri.toString() + "#";
        owlModel.getNamespaceManager().setPrefix(namespace, "protege");
        ProtegeOWLParser.addImport(owlModel, uri);
        owlModel.getDefaultOWLOntology().addImports(uri.toString());
        assertNull(owlModel.getOWLNamedClass("protege:PAL-CONSTRAINT"));
    }
}
