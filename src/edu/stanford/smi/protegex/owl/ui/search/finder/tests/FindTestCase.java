package edu.stanford.smi.protegex.owl.ui.search.finder.tests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.ui.search.finder.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Jan-2006
 */
public class FindTestCase extends AbstractJenaTestCase {

    boolean complete = false;
    ThreadedFind findAlg;

    public void testThreadedSpeed() {
        loadPizza();

        Cls metaCls = owlModel.getOWLNamedClassMetaClassCls();
        metaCls.setDirectBrowserSlot(owlModel.getRDFProperty(RDFSNames.Slot.LABEL));
        findAlg = new ThreadedFind(owlModel, Find.CONTAINS);

        findAlg.addResultListener(new SearchAdapter() {
            public void resultsUpdatedEvent(int numResults, Find source) {
                System.out.println("updated: " + Thread.currentThread());
                System.out.println(" count = " + findAlg.getResultCount());
            }

            public void searchCompleteEvent(int numResults, Find source) {
                System.out.println("complete: " + Thread.currentThread());
                complete = true;
            }

            public void searchCancelledEvent(Find source) {
                System.out.println("cancelled: " + Thread.currentThread());
                complete = true;
            }

            public void searchStartedEvent(Find source) {
                System.out.println("started: " + Thread.currentThread());
            }
        });

        System.out.println("Starting timer");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1; i++) {
            findAlg.startSearch("p");
            while (!complete) {
                Thread.yield();
            }
            System.out.print("WOW");
        }

        long timeTaken = System.currentTimeMillis() - startTime;
        System.out.println("time = " + timeTaken);
    }

    public void testFindDoesNotPickUpLanguageTags() {
        loadPizza();
        Cls metaCls = owlModel.getOWLNamedClassMetaClassCls();
        metaCls.setDirectBrowserSlot(owlModel.getRDFProperty(RDFSNames.Slot.LABEL));
        Find findAlg = new DefaultClassFind(owlModel, Find.CONTAINS);

        findAlg.startSearch("p");
        Map results = findAlg.getResults();

        System.out.println("results = " + results);
        assertTrue(results.size() > 0);
        for (Iterator i = results.values().iterator(); i.hasNext();) {
            FindResult result = (FindResult) i.next();
            assertNotNull(result.getMatchValue());
            assertTrue(Pattern.matches(".*[pP].*", result.getMatchValue()));
            assertTrue(result.getMatchingResource() instanceof OWLNamedClass);
        }
    }

    private void loadPizza() {
        try {
            URI pizza = new URI("http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl");
            try {
                owlModel.load(pizza, null);
            }
            catch (Exception e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
        }
        catch (URISyntaxException e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }
}
