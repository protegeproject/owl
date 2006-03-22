package edu.stanford.smi.protegex.owl.jena.parser.tests;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;
import edu.stanford.smi.protegex.owl.ui.search.finder.DefaultClassFind;
import edu.stanford.smi.protegex.owl.ui.search.finder.Find;
import edu.stanford.smi.protegex.owl.ui.search.finder.SearchAdapter;

import java.io.File;
import java.net.URI;
import java.util.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class LoadNamespacesTestCase extends AbstractJenaTestCase {

    private URI tempSavedFileURI = new File(ProtegeOWL.getPluginFolder(), "temp.owl").toURI();

    private static final String TEST_1 = "http://www.co-ode.org/ontology/test1.owl#";
    private static final String TEST_2 = "http://www.co-ode.org/ontology/test2.owl#";

    private static final String TEST_1_PREFIX = "test1";
    private static final String TEST_2_PREFIX = "test2";
    private static final String AUTO_1_PREFIX = "p1";
    private static final String AUTO_2_PREFIX = "p2";

    private boolean searching = true;

    public void testLoadOntologyWithSameDefaultNSAndXMLBase() throws Exception {
        loadRemoteOntology("namespaces/nsAndBaseSame.owl");

        Map expectedNamespaces = new HashMap();
        expectedNamespaces.put("Class_1", TEST_1);
        expectedNamespaces.put("Class_2", TEST_1);
        expectedNamespaces.put("Class_3", TEST_2);

        Map expectedPrefixes = new HashMap();
        expectedPrefixes.put("Class_1", null);
        expectedPrefixes.put("Class_2", null);
        expectedPrefixes.put("Class_3", TEST_2_PREFIX);

        assertOntologyIsAsExpected(expectedNamespaces, expectedPrefixes);
        assertEquals("http://www.co-ode.org/ontology/test1.owl",
                     owlModel.getDefaultOWLOntology().getURI());

        owlModel.save(tempSavedFileURI);
        System.out.println("saved to " + tempSavedFileURI);

        loadTestOntology(tempSavedFileURI);

        assertOntologyIsAsExpected(expectedNamespaces, expectedPrefixes);
        assertEquals("http://www.co-ode.org/ontology/test1.owl",
                     owlModel.getDefaultOWLOntology().getURI());
    }

    public void testLoadOntologyWithDifferentDefaultNSAndXMLBase() throws Exception {
        loadRemoteOntology("namespaces/nsAndBaseDiff.owl");

        Map expectedNamespaces = new HashMap();
        expectedNamespaces.put("Class_1", TEST_1);
        expectedNamespaces.put("Class_2", TEST_1);
        expectedNamespaces.put("Class_3", TEST_2);

        Map expectedPrefixes = new HashMap();
        expectedPrefixes.put("Class_1", AUTO_1_PREFIX);
        expectedPrefixes.put("Class_2", AUTO_1_PREFIX);
        expectedPrefixes.put("Class_3", TEST_2_PREFIX);

        assertOntologyIsAsExpected(expectedNamespaces, expectedPrefixes);

        owlModel.save(tempSavedFileURI);
        System.out.println("saved to " + tempSavedFileURI);

        loadTestOntology(tempSavedFileURI);

        assertOntologyIsAsExpected(expectedNamespaces, expectedPrefixes);
    }

    public void testLoadOntologyWithNoDefaultNSOrXMLBase() throws Exception {
        loadRemoteOntology("namespaces/noDefaultNSNoBase.owl");

        Map expectedNamespaces = new HashMap();
        expectedNamespaces.put("Class_1", TEST_1);
        expectedNamespaces.put("Class_2", TEST_1);
        expectedNamespaces.put("Class_3", TEST_2);

        Map expectedPrefixes = new HashMap();
        expectedPrefixes.put("Class_1", AUTO_1_PREFIX);
        expectedPrefixes.put("Class_2", AUTO_1_PREFIX);
        expectedPrefixes.put("Class_3", TEST_2_PREFIX);

        assertOntologyIsAsExpected(expectedNamespaces, expectedPrefixes);
        assertEquals("http://www.co-ode.org/ontology/test1.owl",
                     owlModel.getDefaultOWLOntology().getURI());

        owlModel.save(tempSavedFileURI);
        System.out.println("saved to " + tempSavedFileURI);

        loadTestOntology(tempSavedFileURI);

        assertOntologyIsAsExpected(expectedNamespaces, expectedPrefixes);
    }


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

    private void assertOntologyIsAsExpected(Map expectedNamespaces, Map expectedPrefixes) {
        Find find = new DefaultClassFind(owlModel, Find.ENDS_WITH);
        find.addResultListener(new SearchAdapter() {
            public void searchCompleteEvent(int numResults, Find source) {
                searching = false;
            }
        });

        for (Iterator i = expectedNamespaces.keySet().iterator(); i.hasNext();) {
            String current = (String) i.next();
            System.out.println("current = " + current);
            searching = true;
            find.startSearch(current);
            while (searching) {
                Thread.yield();
            }

            Set results = find.getResultResources();
            assertTrue(results.size() > 0);
            OWLNamedClass c = (OWLNamedClass) CollectionUtilities.getFirstItem(results);
            System.out.println("current URI = " + c.getURI());
            assertNotNull(c);
            assertEquals(expectedNamespaces.get(current), c.getNamespace());
            assertEquals(expectedPrefixes.get(current), c.getNamespacePrefix());
        }

        assertNotNull(owlModel.getDefaultOWLOntology());
    }
}
