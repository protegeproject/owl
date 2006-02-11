package edu.stanford.smi.protegex.owl.database;

import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.AbstractCreateProjectPlugin;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.WizardPage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Ray Fergerson  <fergerson@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLDatabaseProjectPlugin extends
        AbstractCreateProjectPlugin implements OWLDatabasePlugin {

    private String driver;

    private String table;

    private String username;

    private String password;

    protected URI ontologyFileURI;

    private String url;


    public CreateOWLDatabaseProjectPlugin() {
        this("OWL Database");
    }


    public CreateOWLDatabaseProjectPlugin(String name) {
        super(name);
    }


    public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return factory.getClass() == OWLDatabaseKnowledgeBaseFactory.class;
    }


    public Project createNewProject(KnowledgeBaseFactory factory) {
        Collection errors = new ArrayList();
        Project project = super.createNewProject(factory);
        initializeSources(project.getSources());
        try {
            File tempProjectFile = File.createTempFile("protege", "temp");
            project.setProjectFilePath(tempProjectFile.getPath());
            project.save(errors);
            if(errors.isEmpty()) {
                project = Project.loadProjectFromFile(tempProjectFile.getPath(), errors);
            }
            handleErrors(errors);
            project.setProjectFilePath(null);
            tempProjectFile.delete();
        }
        catch (IOException e) {
            Log.getLogger().severe(Log.toString(e));
        }
        return project;
    }


    public WizardPage createCreateProjectWizardPage(CreateProjectWizard wizard,
                                                    boolean useExistingSources) {
        return new OWLDatabaseWizardPage(wizard, this, useExistingSources);
    }


    protected void initializeSources(PropertyList sources) {
        DatabaseKnowledgeBaseFactory.setSources(sources, driver, url, table, username, password);
    }


    public void setDriver(String driver) {
        this.driver = driver;
    }


    public void setOntologyFileURI(URI uri) {
        this.ontologyFileURI = uri;
    }


    public void setTable(String table) {
        this.table = table;
    }


    public void setURL(String url) {
        this.url = url;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }
}