package edu.stanford.smi.protegex.owl.inference.protegeowl.tests;

import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 1, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * @prowl.junit.dig
 */
public class SatisfiabilityTestCase extends AbstractProtegeOwlTestCase {


    public void testOWLThingSatisfiableQuery() {
        try {
            ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().createReasoner(owlModel);
            assertTrue(reasoner.isSatisfiable(owlModel.getOWLThingClass(), null));
        }
        catch (DIGReasonerException e) {
            fail(e.getMessage());
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void testOWLNothingSatisfiableQuery() {
        try {
            ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().createReasoner(owlModel);
            assertFalse(reasoner.isSatisfiable(owlModel.getOWLNothing(), null));
        }
        catch (DIGReasonerException e) {
            fail(e.getMessage());
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void testConceptSatisfiabilityQuery() {
        try {
            OWLNamedClass clsA = owlModel.createOWLNamedClass("A");
            OWLNamedClass clsB = owlModel.createOWLNamedClass("B");
            OWLNamedClass clsC = owlModel.createOWLNamedClass("C");
            clsC.addSuperclass(clsA);
            clsC.addSuperclass(clsB);
            ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().createReasoner(owlModel);
            assertTrue(reasoner.isSatisfiable(clsC, null));
            clsA.addDisjointClass(clsB);
            assertFalse(reasoner.isSatisfiable(clsC, null));
        }
        catch (DIGReasonerException e) {
            fail(e.getMessage());
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void testIntersectionSatisfiableQuery() {
        try {
            OWLNamedClass clsA = owlModel.createOWLNamedClass("A");
            OWLNamedClass clsB = owlModel.createOWLNamedClass("B");
            OWLNamedClass clsC = owlModel.createOWLNamedClass("C");
            ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().createReasoner(owlModel);
            OWLClass [] clses = new OWLClass []{clsA, clsB, clsC};
            assertTrue(reasoner.isIntersectionSatisfiable(clses, null));
            clsA.addDisjointClass(clsB);
            assertFalse(reasoner.isIntersectionSatisfiable(clses, null));
        }
        catch (DIGReasonerException e) {
            fail(e.getMessage());
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


}

