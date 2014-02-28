package edu.stanford.smi.protegex.owl.misc;

import java.io.File;
import java.util.logging.Logger;

import junit.framework.TestCase;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.protege2jena.Protege2Jena;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;


public class TestRefactor extends TestCase {
    public static final Logger LOGGER = Log.getLogger(TestRefactor.class);
    public static final String NEW_NS = "http://www.co-ode.org/ontologies/pizza/junit-pizza.owl";
    
    public void testRefactorNoErrors() throws Exception {
        File f = createRefactoredPizza(openPizza());
        testRefactoredPizza(f);
    }
    
    private void testRefactoredPizza(File f) throws OntologyLoadException {
        OWLModel model = ProtegeOWL.createJenaOWLModelFromURI(f.toURI().toString());
        assertEquals(NEW_NS + "#", model.getNamespaceManager().getDefaultNamespace());
        assertEquals(NEW_NS, model.getTripleStoreModel().getTopTripleStore().getOriginalXMLBase());
        assertEquals(NEW_NS, model.getDefaultOWLOntology().getName());
    }
    
    private File createRefactoredPizza(OWLModel model) throws Exception {
        OWLUtil.renameOntology(model, model.getDefaultOWLOntology(), NEW_NS);
        TripleStore ts = model.getTripleStoreModel().getTopTripleStore();
        ts.setOriginalXMLBase(NEW_NS);
        model.getNamespaceManager().setDefaultNamespace(NEW_NS + "#");
        File out = File.createTempFile("Junit", ".owl");
        Protege2Jena.saveAll(model, out.toURI(), FileUtils.langXMLAbbrev);
        LOGGER.info("Saved file to " + out);
        return out;
    }
    
    private OWLModel openPizza() throws OntologyLoadException {
        return ProtegeOWL.createJenaOWLModelFromURI(new File("junit/projects/pizza.owl").toURI().toString());
    }
}
