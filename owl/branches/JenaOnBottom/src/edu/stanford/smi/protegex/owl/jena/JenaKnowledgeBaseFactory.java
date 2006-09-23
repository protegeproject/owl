package edu.stanford.smi.protegex.owl.jena;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.model.ClientInitializerKnowledgeBaseFactory;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBaseSourcesEditor;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.FrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.database.triplestore.DatabaseTripleStoreModel;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.jena.triplestore.JenaTripleStoreModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;
import edu.stanford.smi.protegex.owl.resource.OWLText;
import edu.stanford.smi.protegex.owl.storage.OWLKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.storage.ProtegeSaver;
import edu.stanford.smi.protegex.owl.ui.ProgressDisplayDialog;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * A backend for OWL based on the Jena2 API.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaKnowledgeBaseFactory implements OWLKnowledgeBaseFactory, ClientInitializerKnowledgeBaseFactory {

    public static final String JENA_SYNCHRONIZED = JenaKnowledgeBaseFactory.class.getName() + ".synchronized";

    public static final String OWL_FILE_URI_PROPERTY = "owl_file_name";

    public static final String OWL_FILE_LANGUAGE_PROPERTY = "owl_file_language";

    public static final String OWL_BUILD_PROPERTY = "owl_build";

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


    public KnowledgeBase createKnowledgeBase(Collection errors) {
	    ProtegeOWLParser.inUI = Application.getWelcomeDialog() != null;
        useStandalone = ProtegeOWLParser.inUI == false;
        OWLNamespaceManager namespaceManager = new OWLNamespaceManager();
        ResourceSelectionAction.setActivated(true);
	    JenaOWLModel owlModel = new JenaOWLModel(this, namespaceManager);
        owlModel.getRepositoryManager().addDefaultRepositories();
	    if(ProtegeOWLParser.inUI) {
		    owlModel.getTaskManager().setProgressDisplay(new ProgressDisplayDialog());
	    }
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
                    //return new File(owlURI).toURI();
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
		    loadRepositories(owlModel, absoluteURI);
			owlModel.load(absoluteURI, language, errors);

        }
        else {
            errors.add("This plugin can currently only load OWL files into OWL projects");
        }
    }


    private void loadRepositories(OWLModel owlModel, URI uri) {
        String owlFilePath = uri.getPath();
        File f = new File(owlFilePath);
        // Load any project repositories
        RepositoryFileManager man = new RepositoryFileManager(owlModel);
        man.loadProjectRepositories();
    }


    public void saveKnowledgeBase(KnowledgeBase kb, PropertyList sources, Collection errors) {
        String language = getOWLFileLanguage(sources);
        if (kb instanceof JenaOWLModel) {
            JenaOWLModel owlModel = (JenaOWLModel) kb;
            sources.setInteger(OWL_BUILD_PROPERTY, OWLText.getBuildNumber());
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
                new ProtegeSaver(kb, owlModel).run();
                URI absoluteURI = getFileURI(sources, owlModel.getProject());
                owlModel.save(absoluteURI, language, errors);
            }
        }
    }


    protected void makeOWLFileNameRelativeIfPossible(Project project) {
        String path = getOWLFilePath(project.getSources());
        URI projectDirURI = project.getProjectDirectoryURI();
        if (projectDirURI.toString().startsWith("file:")) {
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
    
    public void initializeClientKnowledgeBase(FrameStore fs, 
                                              NarrowFrameStore nfs,
                                              KnowledgeBase kb) { 
      if (kb instanceof OWLModel) {
        JenaOWLModel owlModel = (JenaOWLModel) kb;
        JenaTripleStoreModel tsm = new JenaTripleStoreModel(owlModel,nfs);
        owlModel.setTripleStoreModel(tsm);
      }
    }
}
