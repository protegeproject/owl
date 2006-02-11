package edu.stanford.smi.protegex.owl.jena.parser.tests;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LoadNamespacesTestCase extends AbstractJenaTestCase {

    public void testDefaultNamespaces() throws Exception {
        Collection oldOntologies = owlModel.getOWLOntologies();
        assertSize(1, oldOntologies);
        OWLModel newModel = reload(owlModel);
        Collection ontologies = newModel.getOWLOntologies();
        assertSize(1, ontologies);
        assertContains(newModel.getDefaultOWLOntology(), ontologies);
        NamespaceManager nsm = newModel.getNamespaceManager();
        assertEquals(OWLNamespaceManager.DEFAULT_DEFAULT_NAMESPACE, nsm.getDefaultNamespace());
        assertEquals(OWL.getURI(), nsm.getNamespaceForPrefix("owl"));
        assertEquals(RDF.getURI(), nsm.getNamespaceForPrefix("rdf"));
        assertEquals(RDFS.getURI(), nsm.getNamespaceForPrefix("rdfs"));
    }


    public void testMissingPrefixForImport() throws Exception {
        loadRemoteOntology("importTravelNoPrefix.owl");
    }


    public void testMissingPrefix() throws Exception {
        loadRemoteOntology("concepts.owl");
        assertNotNull(owlModel.getDefaultOWLOntology());
    }
}
