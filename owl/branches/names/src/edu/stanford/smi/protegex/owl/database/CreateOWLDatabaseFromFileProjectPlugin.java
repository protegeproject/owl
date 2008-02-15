package edu.stanford.smi.protegex.owl.database;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.database.creator.OwlDatabaseFromFileCreator;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLDatabaseFromFileProjectPlugin extends CreateOWLDatabaseProjectPlugin {
    
    protected URI ontologyInputSource;

    public CreateOWLDatabaseFromFileProjectPlugin() {
        super("OWL File (.owl or .rdf)");
    }


    protected Project buildNewProject(KnowledgeBaseFactory factory) {
        JenaKnowledgeBaseFactory.useStandalone = false;
        Collection errors = new ArrayList();
        OwlDatabaseFromFileCreator creator = new OwlDatabaseFromFileCreator((OWLDatabaseKnowledgeBaseFactory) factory);
        creator.setDriver(getDriver());
        creator.setURL(getUrl());
        creator.setTable(getTable());
        creator.setUsername(getUsername());
        creator.setPassword(getPassword());
        creator.setOntologySource(getOntologyInputSource().toString());

        Project project = null;
        try {
            project = creator.create(errors);
        }
        catch (IOException ioe) {
            errors.add(ioe);
        }
        handleErrors(errors);
        return project;
    }


    public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return super.canCreateProject(factory, useExistingSources) && useExistingSources;
    }


    public WizardPage createCreateProjectWizardPage(CreateProjectWizard wizard, boolean useExistingSources) {
        return new InitOWLDatabaseFromFileWizardPage(wizard, this);
    }
    
    

    public void setOntologyInputSource(URI uri) {
        this.ontologyInputSource = uri;
    }
    
    public URI getOntologyInputSource() {
        return ontologyInputSource;
    }

}
