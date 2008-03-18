package edu.stanford.smi.protegex.owl.inference.protegeowl.tests;

import java.util.Collection;
import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 2, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * @prowl.junit.dig
 */
public class IndividualTestCase extends AbstractProtegeOwlTestCase {

  
    public void testTypesQuery() {
        try {
            OWLNamedClass clsA = owlModel.createOWLNamedClass("A");
            OWLNamedClass clsB = owlModel.createOWLNamedClass("B");
            OWLIndividual indA = clsA.createOWLIndividual("iA");
            OWLIndividual indThing = owlModel.getOWLThingClass().createOWLIndividual("iThing");
            indA.addRDFType(clsB);
            ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().createReasoner(owlModel);
            Collection iATypes = reasoner.getIndividualTypes(indA, null);
            assertTrue(iATypes.contains(clsA));
            assertTrue(iATypes.contains(clsB));
            Collection thingTypes = reasoner.getIndividualTypes(indThing, null);
            assertTrue(thingTypes.contains(owlModel.getOWLThingClass()));
        }
        catch (DIGReasonerException e) {
            fail(e.getMessage());
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public void testIndividualsBelongingToClassQuery() {
        try {
            OWLNamedClass clsA = owlModel.createOWLNamedClass("A");
            OWLNamedClass clsB = owlModel.createOWLNamedClass("B");
            OWLIndividual indA = clsA.createOWLIndividual("iA");
            OWLIndividual indThing = owlModel.getOWLThingClass().createOWLIndividual("iThing");
            indA.addRDFType(clsB);
            ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().createReasoner(owlModel);
            assertTrue(reasoner.getIndividualsBelongingToClass(clsA, null).contains(indA));
            assertTrue(reasoner.getIndividualsBelongingToClass(clsB, null).contains(indA));
            assertTrue(reasoner.getIndividualsBelongingToClass(owlModel.getOWLThingClass(), null).contains(indThing));
        }
        catch (DIGReasonerException e) {
            fail(e.getMessage());
            Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }
}

