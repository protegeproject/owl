package edu.stanford.smi.protegex.owl.jena;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.AbstractCreateProjectPlugin;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.XSPNames;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.model.util.XSDVisibility;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.ui.SWRLProjectPlugin;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchClassDefinitionResourceDisplayPlugin;
import edu.stanford.smi.protegex.owl.ui.jena.OWLFilesWizardPage;
import edu.stanford.smi.protegex.owl.ui.menu.OWLMenuProjectPlugin;
import edu.stanford.smi.protegex.owl.ui.metadatatab.OntologyURIWizardPage;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfileSelectionWizardPage;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFilesCreateProjectPlugin extends AbstractCreateProjectPlugin implements OWLFilesPlugin {
	
    private static transient Logger log = Log.getLogger(OWLFilesCreateProjectPlugin.class);

    private Class defaultClassView;

    private String defaultNamespace;

    private Collection imports = new ArrayList();

    private Map importPrefixes = new HashMap();

    private String fileURI;

    private String lang;

    private String profileURI;


    public OWLFilesCreateProjectPlugin() {
        super("OWL Files");
        JenaKnowledgeBaseFactory.useStandalone = false;
    }

    
    private void addViewSettings(PropertyList sources) {
        String typeName = null;
        if (defaultClassView == null) {
            typeName = SwitchClassDefinitionResourceDisplayPlugin.getDefaultClassView();
        }
        else {
            typeName = defaultClassView.getName();
        }
        SwitchClassDefinitionResourceDisplayPlugin.setClassesView(sources, typeName);
        SwitchClassDefinitionResourceDisplayPlugin.setDefaultClassesView(typeName);
        if (profileURI != null) {
            ProfilesManager.setProfile(sources, profileURI);
        }
    }


    protected Project buildNewProject(KnowledgeBaseFactory factory) {        
        Project project = buildNewProject2(factory);
        if (project != null) {
            OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
            OWLMenuProjectPlugin.makeHiddenClsesWithSubclassesVisible(owlModel);
            if (SWRLProjectPlugin.isSWRLImported(owlModel)) {
                SWRLProjectPlugin.adjustGUI(project);
            }
            XSDVisibility.updateVisibility(owlModel);
        }        
        return project;
    }


    private Project buildNewProject2(KnowledgeBaseFactory factory) {
        Collection errors = new ArrayList();
        Project project = Project.createNewProject(factory, errors);
        initialize(project);
        URI uri = getBuildProjectURI();
        if (uri != null) {
            project.setProjectURI(uri);
        }
        project.createDomainKnowledgeBase(factory, errors, true);
        handleErrors(errors);
        return project;
    }


    public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return factory.getClass() == JenaKnowledgeBaseFactory.class;
    }


    public WizardPage createCreateProjectWizardPage(CreateProjectWizard wizard, boolean useExistingSources) {
        ProfileSelectionWizardPage.isBuild = useExistingSources;
        if (useExistingSources) {
            return new OWLFilesWizardPage(wizard, this);
        }
        else {
            return new OntologyURIWizardPage(wizard, this);
        }
    }


    protected Project createNewProject(KnowledgeBaseFactory factory) {
    	Collection errors = new ArrayList();
    	Project project = Project.createNewProject(factory, errors);
    	OWLModel owlModel = (OWLModel) project.getKnowledgeBase();
    	
    	TripleStoreModel tripleStoreModel = owlModel.getTripleStoreModel();
    	tripleStoreModel.setTopTripleStore(tripleStoreModel.getActiveTripleStore());
    	
    	if (defaultNamespace == null) {
    		defaultNamespace = ProtegeNames.DEFAULT_DEFAULT_BASE;
    	}
    	owlModel.getNamespaceManager().setDefaultNamespace(defaultNamespace);
    	String defaultOntologyName = defaultNamespace;
    	if (defaultOntologyName.endsWith("#")) {
    		defaultOntologyName = defaultOntologyName.substring(0, defaultOntologyName.length() - 1);
    	}    	
    	owlModel.getSystemFrames().getOwlOntologyClass().createInstance(defaultOntologyName);
    	
    	TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
    	activeTripleStore.setOriginalXMLBase(defaultOntologyName);
    	activeTripleStore.setName(defaultOntologyName);
    	owlModel.resetOntologyCache();
    	
    	addViewSettings(project.getSources());    	
    	OWLMenuProjectPlugin.makeHiddenClsesWithSubclassesVisible(owlModel);
    	
    	return project;
    }


    protected URI getBuildProjectURI() {
        if (fileURI != null) {
            if (fileURI.startsWith("file:")) {
                int index = fileURI.lastIndexOf('.');
                if (index > 0) {
                    String uri = FileUtilities.replaceExtension(fileURI, ".pprj");
                    try {
                        return new URI(uri);
                    }
                    catch (Exception ex) {
                      Log.emptyCatchBlock(ex);
                    }
                }
            }
        }
        return super.getBuildProjectURI();
    }


    protected void initializeSources(PropertyList sources) {
        JenaKnowledgeBaseFactory.setOWLFileName(sources, fileURI);
        JenaKnowledgeBaseFactory.setOWLFileLanguage(sources, lang);
        addViewSettings(sources);
    }


    public void setFile(String fileURI) {
        this.fileURI = fileURI;
    }


    public void setLanguage(String lang) {
        this.lang = lang;
    }


    public void setDefaultClassView(Class typeClass) {
        this.defaultClassView = typeClass;
        SwitchClassDefinitionResourceDisplayPlugin.setDefaultClassesView(typeClass.getName());
    }


    public void setDefaultNamespace(String namespace) {
        this.defaultNamespace = namespace;
    }


    public void setProfile(String profileURI) {
        this.profileURI = profileURI;
    }
    
    public void addImport(String uri, String prefix) {
    }
}
