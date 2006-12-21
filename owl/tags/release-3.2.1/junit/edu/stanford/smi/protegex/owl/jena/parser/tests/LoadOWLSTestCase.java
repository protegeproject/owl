package edu.stanford.smi.protegex.owl.jena.parser.tests;

import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

import java.net.URI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LoadOWLSTestCase extends AbstractJenaTestCase {

    public void testLoadBravoAir() throws Exception {
        loadTestOntology(new URI("http://www.daml.org/services/owl-s/1.1/BravoAirProcess.owl"));
    }


    public void testLoadCongo() throws Exception {
        loadTestOntology(new URI("http://www.daml.org/services/owl-s/1.1/CongoProfile.owl"));
    }


    public void testLoadOWLSProcess() throws Exception {
        loadTestOntology(new URI("http://www.daml.org/services/owl-s/1.0/Process.owl"));
    }


    public void testLoadOWLSGrouding() throws Exception {
        loadTestOntology(new URI("http://www.daml.org/services/owl-s/1.0/Grounding.owl"));
    }
}
