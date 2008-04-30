package edu.stanford.smi.protegex.owl.ui.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.test.APITestCase;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.database.creator.OwlDatabaseFromFileCreator;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromReaderCreator;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromStreamCreator;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromUriCreator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFolderRepository;
import edu.stanford.smi.protegex.owl.tests.AbstractJenaTestCase;

public class RepositoriesAtCreation extends APITestCase {
	private Repository repository = new LocalFolderRepository(new File("examples"));
	private URI ontologyUri = AbstractJenaTestCase.getRemoteOntologyURI("importTravelNoPrefix.owl");
	private String sampleClass = "http://www.owl-ontologies.com/travel.owl#Sightseeing";
	private String actualOntologyName = "http://www.owl-ontologies.com/unnamed.owl";
	
	@SuppressWarnings("unchecked")
	public void testBaseline() {  // the travel.owl import is not found without a good repository.
		try {
	        Collection errors = new ArrayList();
	        OwlProjectFromUriCreator creator = new OwlProjectFromUriCreator();
	        creator.setOntologyUri(ontologyUri.toString());
	        OWLModel owlModel = (JenaOWLModel) creator.create(errors).getKnowledgeBase();
	        assertEquals(0, errors.size());
			assertEquals(1, owlModel.getOWLOntologyClass().getInstanceCount(false));
			assertNull(owlModel.getOWLNamedClass(sampleClass));
			assertEquals(actualOntologyName, owlModel.getDefaultOWLOntology().getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testStreamingDatabase() {
		for (DBType dbt : DBType.values()) {
			setDBType(dbt);
			if (!dbConfigured()) {
				continue;
			}
	        Collection errors = new ArrayList();
			OwlDatabaseFromFileCreator creator = new OwlDatabaseFromFileCreator();
			creator.addRepository(repository);
			creator.setDriver(APITestCase.getDBProperty(APITestCase.JUNIT_DB_DRIVER_PROPERTY));
			creator.setURL(APITestCase.getDBProperty(APITestCase.JUNIT_DB_URL_PROPERTY));
			creator.setTable(getDBProperty(APITestCase.JUNIT_DB_TABLE_PROPERTY));
			creator.setUsername(APITestCase.getDBProperty(APITestCase.JUNIT_DB_USER_PROPERTY));
			creator.setPassword(APITestCase.getDBProperty(APITestCase.JUNIT_DB_PASSWORD_PROPERTY));
			creator.setOntologySource(ontologyUri.toString());
			OWLModel owlModel;
			try  {
				Project p = creator.create(errors);
				owlModel = (OWLModel) p.getKnowledgeBase();
			}
			catch (IOException e) {
				fail("Exception caught creating owl model from database " + e);
				return;
			}
			assertEquals(0, errors.size());
			checkForSuccess(owlModel);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testReader() {
		OwlProjectFromReaderCreator creator = new OwlProjectFromReaderCreator();
		OWLModel owlModel;
		Collection errors = new ArrayList();
		creator.addRepository(repository);
		try {
			creator.setReader(makeReader());
			owlModel = (OWLModel) creator.create(errors).getKnowledgeBase();
		} catch (IOException e) {
			fail("Exception caught creating owl model from file reader" + e);
			return;
		}
        assertEquals(0, errors.size());
		checkForSuccess(owlModel);
	}
	
	@SuppressWarnings("unchecked")
	public void testStream() {
		OwlProjectFromStreamCreator creator = new OwlProjectFromStreamCreator();
		OWLModel owlModel;
		Collection errors = new ArrayList();
		creator.addRepository(repository);
		try {
			creator.setStream(makeInputStream());
			owlModel = (OWLModel) creator.create(errors).getKnowledgeBase();
		} catch (IOException e) {
			fail("Exception caught creating owl model from file reader" + e);
			return;
		}
        assertEquals(0, errors.size());
		checkForSuccess(owlModel);
	}
	
	@SuppressWarnings("deprecation")
    public void testLoadUri() {
	    try {
	        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();
	        owlModel.getRepositoryManager().addProjectRepository(repository);
	        owlModel.load(ontologyUri, FileUtils.langXMLAbbrev);
	        checkForSuccess(owlModel);
	    }
	    catch (IOException e) {
	        fail("Exception caught " + e);
	    }
	}
	
    @SuppressWarnings("deprecation")
    public void testLoadInputStream() {
        try {
            JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();
            owlModel.getRepositoryManager().addProjectRepository(repository);
            owlModel.load(makeInputStream(), FileUtils.langXMLAbbrev);
            checkForSuccess(owlModel);
        }
        catch (IOException e) {
            fail("Exception caught " + e);
        }
    }
    
    @SuppressWarnings("deprecation")
    public void testLoadReader() {
        try {
            JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();
            owlModel.getRepositoryManager().addProjectRepository(repository);
            owlModel.load(makeReader(), FileUtils.langXMLAbbrev);
            checkForSuccess(owlModel);
        }
        catch (IOException e) {
            fail("Exception caught " + e);
        }
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    public void testLoadUriWithErrors() {
        try {
            Collection errors = new ArrayList();
            JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();
            owlModel.getRepositoryManager().addProjectRepository(repository);
            owlModel.load(ontologyUri, FileUtils.langXMLAbbrev, errors);
            assertEquals(0, errors.size());
            checkForSuccess(owlModel);
        }
        catch (IOException e) {
            fail("Exception caught " + e);
        }
    }
	
	private void checkForSuccess(OWLModel owlModel) {
		assertEquals(2, owlModel.getOWLOntologyClass().getInstanceCount(false));
		assertNotNull(owlModel.getOWLNamedClass(sampleClass));
		assertEquals(actualOntologyName, owlModel.getDefaultOWLOntology().getName());
	}
	
	private InputStream makeInputStream() throws IOException {
	    return ontologyUri.toURL().openStream();
	}
	
	private Reader makeReader() throws IOException {
        InputStream inputStream = ontologyUri.toURL().openStream();
        return new InputStreamReader(inputStream);
	}
}
