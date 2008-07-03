package edu.stanford.smi.protegex.owl.jena;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBaseSourcesEditor;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.storage.ProtegeSaver;
import edu.stanford.smi.protegex.owl.ui.menu.OWLBackwardsCompatibilityProjectFixups;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * A backend for OWL based on the Jena2 API.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaKnowledgeBaseFactory implements OWLKnowledgeBaseFactory {
    private static final transient Logger log = Log.getLogger(JenaKnowledgeBaseFactory.class);

    public static final String JENA_SYNCHRONIZED = JenaKnowledgeBaseFactory.class.getName() + ".synchronized";

    public static final String OWL_FILE_URI_PROPERTY = "owl_file_name";

    public static final String OWL_FILE_LANGUAGE_PROPERTY = "owl_file_language";

    public final static String[] fileLanguages = {
            FileUtils.langXMLAbbrev,
            FileUtils.langXML,
            FileUtils.langNTriple,
            FileUtils.langN3,
            FileUtils.langTurtle
    };

    final static String[] extensions = {
            "owl",
            "rdf-xml.owl",
            "ntriple.owl",
            "n3.owl",
            "turtle.owl"
    };


    /**
     * A global flag that is used to select the default .pprj file for new project.
     * If you are using the Protege-OWL API in a stand-alone application, then this
     * should be true (default), but the Protege UI will set it to false.
     */
    public static boolean useStandalone = true;
    
    private static List<Repository> repositories = new ArrayList<Repository>();

    public static void addRepository(Repository repository) {
        repositories.add(repository);
    }
    
    public static void clearRepositories() {
        repositories.clear();
    }
    
    public static Collection<Repository> getRepositories() {
        return Collections.unmodifiableCollection(repositories);
    }
    
    
    public KnowledgeBase createKnowledgeBase(Collection errors) {
    	//have to test this in a different way..
    	boolean inUI = ProjectManager.getProjectManager().getMainPanel() != null;
        useStandalone = !inUI;

        ResourceSelectionAction.setActivated(true);
	    JenaOWLModel owlModel = new JenaOWLModel(this);
        owlModel.getRepositoryManager().addDefaultRepositories();
        return owlModel;
    }


    public KnowledgeBaseSourcesEditor createKnowledgeBaseSourcesEditor(String projectURIString, PropertyList sources) {
        if (projectURIString != null && projectURIString.startsWith("http://")) {
            int index = projectURIString.lastIndexOf('/');
            projectURIString = new File(projectURIString.substring(index + 1)).toURI().toString();
        }
        return new JenaKnowledgeBaseSourcesEditor(projectURIString, sources);
    }


    protected URI getFileURI(PropertyList sources, Project project) {
        try {
            String owlURI = getOWLFilePath(sources);
            if (owlURI.startsWith("http://")) {
                return new URI(owlURI);
            }
            else {
                URI projectURI = project.getProjectURI();
                if (projectURI == null) {
                    return new URI(owlURI);
                }
                else {
                    URI projectDirURI = project.getProjectDirectoryURI();
                    URI rel = URIUtilities.relativize(projectDirURI, new URI(owlURI));
                    URI result = projectURI.resolve(rel);
                    return result;
                }
            }
        }
        catch (Exception ex) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
        return null;
    }


    public static String getExtension(String language) {
        for (int i = 0; i < fileLanguages.length; i++) {
            String fileLanguage = fileLanguages[i];
            if (fileLanguage.equals(language)) {
                return extensions[i];
            }
        }
        return null;
    }


    protected static String getOWLFileLanguage(PropertyList sources) {
        String result = sources.getString(OWL_FILE_LANGUAGE_PROPERTY);
        return result == null ? fileLanguages[0] : result;
    }


    public static String getOWLFilePath(PropertyList sources) {
        return sources.getString(OWL_FILE_URI_PROPERTY);
    }


    public String getDescription() {
        return "OWL / RDF Files";
    }


    public String getProjectFilePath() {
        return useStandalone ? "OWL-min.pprj" : "OWL.pprj";
    }


    public void includeKnowledgeBase(KnowledgeBase kb, PropertyList sources, Collection errors) {
        loadKnowledgeBase(kb, sources, errors);
    }


    public boolean isComplete(PropertyList sources) {
        return true;
    }


    public void loadKnowledgeBase(KnowledgeBase kb, PropertyList sources, final Collection errors) {
        final String language = getOWLFileLanguage(sources);
        if (kb instanceof JenaOWLModel) {
	        final JenaOWLModel owlModel = (JenaOWLModel) kb;
            final URI absoluteURI = getFileURI(sources, owlModel.getProject());

            JenaKnowledgeBaseFactory.setOWLFileName(sources, absoluteURI.toString());
            
		    RepositoryFileManager.loadProjectRepositories(owlModel);
		    RepositoryManager repositoryManager = owlModel.getRepositoryManager();
		    for (Repository repository : repositories) {
		        repositoryManager.addProjectRepository(repository);
		    }
		    repositories.clear();
		    
		    try {
		        ProtegeOWLParser parser = new ProtegeOWLParser(owlModel);    
		        parser.run(absoluteURI);
		    }
	        catch (OntologyLoadException t) {
	        	handleException(t, owlModel, absoluteURI, errors);
	        }	     
	        
	        //TODO: Improve this.
	        Collection parseErrors = ProtegeOWLParser.getErrors(); 
	        if (parseErrors != null && parseErrors.size() > 0) {
	            errors.addAll(parseErrors);
	        }
        }
        else {
        	String message = "This plugin can currently only load OWL files into OWL projects";
        	errors.add(new MessageError(message));
        	Log.getLogger().severe(message);
        }
    }


    protected void handleException(Throwable t, JenaOWLModel owlModel, URI absoluteURI, Collection errors) {
        Log.getLogger().log(Level.SEVERE, "Error at loading file "+ absoluteURI, t);
        
        Collection parseErrors = ProtegeOWLParser.getErrors(); 
        if (parseErrors != null && parseErrors.size() > 0) {
            errors.addAll(parseErrors);
        }        
        errors.add(t);
        
        String message = "Errors at loading OWL file from " + absoluteURI + "\n";
        message = message + "\nPlease consider running the file through an RDF or OWL validation service such as:";
        message = message + "\n  - RDF Validator: http://www.w3.org/RDF/Validator";
        message = message + "\n  - OWL Validator: http://phoebus.cs.man.ac.uk:9999/OWL/Validator";
        
        if (owlModel.getNamespaceManager().getPrefix("http://protege.stanford.edu/system#") != null ||
                owlModel.getNamespaceManager().getPrefix("http://protege.stanford.edu/kb#") != null) {
            message = message + "\nThis file seems to have been created with the frame-based Protege RDF Backend. " +
                    "Please try to use the RDF Backend of Protege to open this file and then export it to OWL " +
                    "using Export to Format...";
        }

        errors.add(new MessageError(message));
    }
    
    
    @SuppressWarnings("unchecked")
	public void saveKnowledgeBase(KnowledgeBase kb, PropertyList sources, Collection errors) {
        String language = getOWLFileLanguage(sources);
        if (kb instanceof JenaOWLModel) {
            JenaOWLModel owlModel = (JenaOWLModel) kb;
            OWLBackwardsCompatibilityProjectFixups.insertVersionData(sources);
            URI absoluteURI = getFileURI(sources, owlModel.getProject());
            owlModel.save(absoluteURI, language, errors);
            makeOWLFileNameRelativeIfPossible(owlModel.getProject());
            ApplicationProperties.addProjectToMRUList(owlModel.getProject().getProjectURI());
        }
        else {
            Project newProject = Project.createNewProject(this, errors);
            newProject.setProjectURI(kb.getProject().getProjectURI());
            JenaOWLModel owlModel = (JenaOWLModel) newProject.getKnowledgeBase();
            if (kb instanceof OWLDatabaseModel) {
                OntModel newModel = ((OWLDatabaseModel) kb).getOntModel();
                URI absoluteURI = getFileURI(sources, owlModel.getProject());
                owlModel.save(absoluteURI, language, errors, newModel);
            }
            else {  // Any other Protege format
                // TODO: owlModel.initWithProtegeMetadataOntology(errors);
                try {
                    FactoryUtils.addOntologyToTripleStore(owlModel, owlModel.getTripleStoreModel().getTopTripleStore(), FactoryUtils.generateOntologyURIBase());
                } catch (AlreadyImportedException e) {
                    log.log(Level.WARNING, "Unexpected Exception - Could not set default ontology", e);
                }
                new ProtegeSaver(kb, owlModel).run();
                URI absoluteURI = getFileURI(sources, owlModel.getProject());
                owlModel.save(absoluteURI, language, errors);
            }
        }
    }


    protected void makeOWLFileNameRelativeIfPossible(Project project) {
        String path = getOWLFilePath(project.getSources());
        URI projectDirURI = project.getProjectDirectoryURI();
        if (projectDirURI != null && projectDirURI.toString().startsWith("file:")) {
            try {
                URI fileURI = new URI(path);
                File fileFile = new File(fileURI);
                if (fileFile.isAbsolute()) {
                    File pathFile = new File(projectDirURI);
                    File parentFile = fileFile.getParentFile();
                    if (parentFile.toString().equals(pathFile.toString())) {
                        String rel = fileFile.getName();
                        setOWLFileName(project.getSources(), rel);
                    }
                }
            }
            catch (Exception ex) {
            }
        }
    }


    public static void setOWLFileLanguage(PropertyList sources, String language) {
        sources.setString(OWL_FILE_LANGUAGE_PROPERTY, language);
    }


    public static void setOWLFileName(PropertyList sources, String filePath) {
        if (filePath.indexOf(".") < 0) {
            filePath += ".owl";
        }
        sources.setString(OWL_FILE_URI_PROPERTY, filePath);
    }
    
    public static String getOWLFileName(PropertyList sources) {
        return sources.getString(OWL_FILE_URI_PROPERTY);
    }
}
