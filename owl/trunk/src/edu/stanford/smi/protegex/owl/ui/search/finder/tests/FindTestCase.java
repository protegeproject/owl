package edu.stanford.smi.protegex.owl.ui.search.finder.tests;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.ui.search.finder.DefaultClassFind;
import edu.stanford.smi.protegex.owl.ui.search.finder.Find;
import edu.stanford.smi.protegex.owl.ui.search.finder.FindResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         27-Jan-2006
 */
public class FindTestCase extends AbstractJenaTestCase {

    public void testFindDoesNotPickUpLanguageTags(){
        loadPizza();
        Cls metaCls = owlModel.getOWLNamedClassMetaClassCls();
        metaCls.setDirectBrowserSlot(owlModel.getRDFProperty(RDFSNames.Slot.LABEL));
        Find findAlg = new DefaultClassFind(owlModel, Find.CONTAINS);
        findAlg.startSearch("p");

        Map results = findAlg.getResults();
        System.out.println("results = " + results);
        assertTrue(results.size() > 0);
        for (Iterator i = results.values().iterator(); i.hasNext();){
            FindResult result = (FindResult)i.next();
            assertNotNull(result.getMatchValue());
            assertTrue(Pattern.matches(".*[pP].*", result.getMatchValue()));
            assertTrue(result.getMatchingResource() instanceof OWLNamedClass);
        }
    }

    private void loadPizza(){
        try {
            URI pizza = new URI("http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl");
            try {
                owlModel.load(pizza, null);
            }
            catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
