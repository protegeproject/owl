/**
 * 
 */
package edu.stanford.smi.protegex.owl.inference.dig.tests;

import edu.stanford.smi.protegex.owl.tests.AbstractDIGReasonerTestCase;

import java.net.URI;

/**
 * @author rouquett
 * @prowl.junit.dig
 */
public class SimpleConsistencyTestCase extends AbstractDIGReasonerTestCase {
    

    public void testDisjointSubclassInconsistency() throws Exception {
        if (!reasonerInitialized()) {
          return;
        }
        loadTestOntology(new URI("file:./junit/projects/inconsistent.owl"));
        computeAndCheckInconsistentConcepts();
    }
}
