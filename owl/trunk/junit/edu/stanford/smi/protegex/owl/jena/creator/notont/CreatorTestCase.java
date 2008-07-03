package edu.stanford.smi.protegex.owl.jena.creator.notont;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromUriCreator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;


public class CreatorTestCase extends TestCase {
    private Logger log = Log.getLogger(CreatorTestCase.class);
    
    public static String IMPORTING_BASE = "http://www.tigraworld.com/protege/Importing.owl";
    public static String IMPORTED_BASE  = "http://www.tigraworld.com/protege/Imported.owl";
    
    private Collection errors = new ArrayList();
    
    public void testUriCreatorRepositoryBaseline() throws OntologyLoadException {
        File ontology = new File("junit/projects/creator/Importing.owl");
        OwlProjectFromUriCreator creator = new OwlProjectFromUriCreator();
        creator.setOntologyUri(ontology.toURI().toString());
        creator.create(errors);
        handleErrors();
        OWLModel owlModel = creator.getOwlModel();
        
        assertNotNull(owlModel.getOWLNamedClass("A"));
        assertNull(owlModel.getOWLNamedClass("imported:X"));
        
        assertEquals(1, owlModel.getDefaultOWLOntology().getImports().size());
        assertEquals(IMPORTED_BASE, owlModel.getDefaultOWLOntology().getImports().iterator().next());
        assertEquals(2, owlModel.getTripleStoreModel().getTripleStores().size());
        assertNull(owlModel.getTripleStoreModel().getTripleStore(IMPORTED_BASE));
        
        checkBase(owlModel, IMPORTING_BASE);
    }

    public void testUriCreatorRepository() throws OntologyLoadException {
        File ontology = new File("junit/projects/creator/Importing.owl");
        OwlProjectFromUriCreator creator = new OwlProjectFromUriCreator();
        creator.setOntologyUri(ontology.toURI().toString());
        creator.addRepository(new LocalFolderRepository(new File("junit/projects/creator")));
        creator.create(errors);
        handleErrors();
        OWLModel owlModel = creator.getOwlModel();
        
        assertNotNull(owlModel.getOWLNamedClass("A"));
        assertNotNull(owlModel.getOWLNamedClass("imported:X"));
        
        assertEquals(1, owlModel.getDefaultOWLOntology().getImports().size());
        assertEquals(IMPORTED_BASE, owlModel.getDefaultOWLOntology().getImports().iterator().next());
        assertEquals(3, owlModel.getTripleStoreModel().getTripleStores().size());
        assertNotNull(owlModel.getTripleStoreModel().getTripleStore(IMPORTED_BASE));
        
        checkBase(owlModel, IMPORTING_BASE);
    }
    
    private void checkBase(OWLModel owlModel, String expected) {
        TripleStore topTripleStore = owlModel.getTripleStoreModel().getTopTripleStore();
        assertEquals(expected + "#", owlModel.getNamespaceManager().getDefaultNamespace()); 
        assertEquals(expected + "#", topTripleStore.getNamespaceManager().getDefaultNamespace());
        assertEquals(expected, topTripleStore.getOriginalXMLBase());
        assertEquals(expected, owlModel.getDefaultOWLOntology().getName());
    }
    
    private void handleErrors() {
        if (errors.isEmpty()) {
            return;
        }
        for (Object o : errors) {
            if (o instanceof Throwable) {
                log.log(Level.SEVERE, "exception caught", ((Throwable) o));
            }
            else {
                log.severe("Error = " + o);
            }
        }
        errors.clear();
        fail();
    }
    
}
