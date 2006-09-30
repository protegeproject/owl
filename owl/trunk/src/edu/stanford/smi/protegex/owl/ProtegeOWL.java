package edu.stanford.smi.protegex.owl;

import com.hp.hpl.jena.util.FileUtils;
import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.util.RepositoryFileManager;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A singleton that provides several generic services such as creating
 * new OWLModels.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeOWL {

    /**
     * The name of the OWL Plugin folder (subfolder of the plugins directory)
     */
    public static String PLUGIN_FOLDER = "edu.stanford.smi.protegex.owl";


    /**
     * Creates a new, empty JenaOWLModel.
     *
     * @return a new OWLModel
     */
    public static JenaOWLModel createJenaOWLModel() {
		final JenaKnowledgeBaseFactory factory = new JenaKnowledgeBaseFactory();
		Collection errors = new ArrayList();
		Project project = Project.createNewProject(factory, errors);
		// TODO TT: I commented out the following lines, they are duplicate with
		// the createNewProject call. They should be removed in the release, if
		// all tests pass.
		// project.setKnowledgeBaseFactory(factory);
		// project.createDomainKnowledgeBase(factory, errors, false);
		return (JenaOWLModel) project.getKnowledgeBase();
	}
   
    
    public static JenaOWLModel createJenaOWLModelFromInputStream(InputStream is) throws Exception {    	
        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();
        owlModel.load(is, FileUtils.langXMLAbbrev);
        return owlModel;
    }


    public static JenaOWLModel createJenaOWLModelFromReader(Reader reader) throws Exception {
        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();
        owlModel.load(reader, FileUtils.langXMLAbbrev);
        return owlModel;
    }


    public static JenaOWLModel createJenaOWLModelFromURI(String uri) throws Exception {
        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModel();
        
        Project project = owlModel.getProject();
        if (project != null) {
        	JenaKnowledgeBaseFactory.setOWLFileName(project.getSources(),uri);
        }
               
        URI urii = URIUtilities.createURI(uri);
        loadRepositories(owlModel, urii);
        owlModel.load(new URI(uri), FileUtils.langXMLAbbrev);
        return owlModel;
    }

    //TODO: TT - This code is duplicated with the JenaKnowledgeBaseFactory.
    // Check whether it can be reused.
    private static void loadRepositories(OWLModel owlModel, URI uri) {
        String owlFilePath = uri.getPath();
        File f = new File(owlFilePath);
        // Load any project repositories
        RepositoryFileManager man = new RepositoryFileManager(owlModel);
        man.loadProjectRepositories();
    }
    

    /**
     * Gets the plugin folder, which is the subfolder plugins/edu.stanford.smi.protegex.owl
     * of the application's starting directory.
     * @return the plugin folder
     */
    public static File getPluginFolder() {
        return new File(new File(ApplicationProperties.getApplicationDirectory(),
                    "plugins"),
                    PLUGIN_FOLDER);
    }


    /**
     * Initializes a Project so that it points to an existing OWL file.
     * This is typically used in conjunction with the <CODE>createJenaOWLModel()</CODE>
     * methods to create and save a new project.  The resulting project can then be loaded
     * into a Protege UI using the main method.
     *
     * @param project
     * @param owlFilePath
     * @param language
     */
    public static void initProject(Project project, String owlFilePath, String language) {
        JenaKnowledgeBaseFactory.setOWLFileLanguage(project.getSources(), language);
        JenaKnowledgeBaseFactory.setOWLFileName(project.getSources(), owlFilePath);
    }


    /**
     * Starts the Protege UI, optionally with a given Project file.
     *
     * @param args the first String may be the name of a .pprj file
     */
    public static void main(String[] args) {
        Application.main(args);
    }
}
