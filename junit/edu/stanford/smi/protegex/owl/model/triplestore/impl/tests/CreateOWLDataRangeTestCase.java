package edu.stanford.smi.protegex.owl.model.triplestore.impl.tests;

import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLDataRangeTestCase extends AbstractTripleStoreTestCase {


    public void testCreateDataRange() {
        RDFResource dataRange = createRDFResource(null);
        ts.add(dataRange, owlModel.getRDFTypeProperty(), owlModel.getOWLDataRangeClass());
        RDFResource firstNode = createRDFResource(null);
        ts.add(dataRange, owlModel.getOWLOneOfProperty(), firstNode);
        RDFResource secondNode = createRDFResource(null);
        ts.add(firstNode, owlModel.getRDFFirstProperty(), "A");
        ts.add(firstNode, owlModel.getRDFRestProperty(), secondNode);
        ts.add(secondNode, owlModel.getRDFFirstProperty(), "B");
        ts.add(secondNode, owlModel.getRDFRestProperty(), owlModel.getRDFNil());
        owlModel.getTripleStoreModel().endTripleStoreChanges();
        RDFResource newDataRange = owlModel.getRDFResource(dataRange.getName());
        assertTrue(newDataRange instanceof OWLDataRange);
        RDFResource newFirstNode = owlModel.getRDFResource(firstNode.getName());
        assertTrue(newFirstNode instanceof RDFList);
    }
}
