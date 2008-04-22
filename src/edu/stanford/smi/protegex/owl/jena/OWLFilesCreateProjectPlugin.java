package edu.stanford.smi.protegex.owl.jena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.AmalgamatedIOException;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.AbstractCreateProjectPlugin;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.jena.creator.NewOwlProjectCreator;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromUriCreator;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchClassDefinitionResourceDisplayPlugin;
import edu.stanford.smi.protegex.owl.ui.jena.OWLFilesWizardPage;
import edu.stanford.smi.protegex.owl.ui.metadatatab.OntologyURIWizardPage;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfileSelectionWizardPage;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLFilesCreateProjectPlugin extends AbstractCreateProjectPlugin implements OWLFilesPlugin {
	
    private static transient Logger log = Log.getLogger(OWLFilesCreateProjectPlugin.class);

    private Class defaultClassView;

    private String ontologyName;

    private String fileURI;

    private String lang;

    private String profileURI;


    public OWLFilesCreateProjectPlugin() {
        super("OWL Files");
        JenaKnowledgeBaseFactory.useStandalone = false;
    }

    protected Project buildNewProject(KnowledgeBaseFactory factory) {   
        Collection errors = new ArrayList();
        OwlProjectFromUriCreator creator = new OwlProjectFromUriCreator((JenaKnowledgeBaseFactory) factory);
        creator.setOntologyUri(fileURI);
        creator.setLang(lang);
        creator.setDefaultClassView(defaultClassView);
        creator.setProfileURI(profileURI);
        try {
            return creator.create(errors);
        }
        catch (IOException ioe) {
            errors.add(ioe);
            return null;
        }
        finally {
            handleErrors(errors);
        }
        
    }

    public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return factory instanceof JenaKnowledgeBaseFactory;
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
        NewOwlProjectCreator creator = new NewOwlProjectCreator((JenaKnowledgeBaseFactory) factory);
        creator.setOntologyName(ontologyName);
        creator.setDefaultClassView(defaultClassView);
        creator.setProfileURI(profileURI);
        try {
            return creator.create(errors);
        }
        catch (AmalgamatedIOException ioe) {
            errors.addAll(ioe.getErrorList());
            return null;
        }
        catch (IOException ioe) {
            errors.add(ioe);
            return null;
        }
        finally {
            handleErrors(errors);
        }
    }
 



    /*
     * ---------------------------------------------------------------------------
     * setters and getters
     */


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


    public void setOntologyName(String ontologyName) {
        this.ontologyName = ontologyName;
    }


    public void setProfile(String profileURI) {
        this.profileURI = profileURI;
    }
    
    public void addImport(String uri, String prefix) {
    }
}
