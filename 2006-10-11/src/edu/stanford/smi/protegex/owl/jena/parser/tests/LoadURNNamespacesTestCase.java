package edu.stanford.smi.protegex.owl.jena.parser.tests;

import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LoadURNNamespacesTestCase extends AbstractJenaTestCase {

    public void testLoadOntologyMetadata() throws Exception {
        loadRemoteOntology("alix/java.owl");
        for (Iterator it = owlModel.getCls(OWLNames.Cls.ONTOLOGY).getDirectInstances().iterator(); it.hasNext();) {
            OWLOntology owlOntology = (OWLOntology) it.next();
            System.out.println("- " + owlOntology.getBrowserText());
        }
        OWLOntology oi = owlModel.getDefaultOWLOntology();
        assertSize(1, oi.getVersionInfo());
    }


    public void testLoadURN() throws Exception {
        loadRemoteOntology("urn.owl");
        final String uri = "urn:lsid:lsid.ibm.com:predicates";
        assertEquals(uri + ":", owlModel.getNamespaceManager().getDefaultNamespace());
        assertEquals(uri, owlModel.getDefaultOWLOntology().getURI());
        assertSize(1, owlModel.getOWLOntologies());
    }
}
