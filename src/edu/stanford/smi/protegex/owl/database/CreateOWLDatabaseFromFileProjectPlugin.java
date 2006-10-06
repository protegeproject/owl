package edu.stanford.smi.protegex.owl.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.plugin.CreateProjectWizard;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLDatabaseFromFileProjectPlugin extends CreateOWLDatabaseProjectPlugin {

    public CreateOWLDatabaseFromFileProjectPlugin() {
        super("OWL File (.owl or .rdf)");
    }


    protected Project buildNewProject(KnowledgeBaseFactory factory) {
        JenaKnowledgeBaseFactory.useStandalone = false;
        Collection errors = new ArrayList();
        Project project = super.createNewProject(factory);
        initializeSources(project.getSources());
        try {
            File tempProjectFile = File.createTempFile("protege", "temp");
            project.setProjectFilePath(tempProjectFile.getPath());
            project.save(errors);
            project = Project.loadProjectFromFile(tempProjectFile.getPath(), errors);

            OWLDatabaseModel owlModel = (OWLDatabaseModel) project.getKnowledgeBase();
            updateTripleStores(owlModel);
            ProtegeOWLParser parser = new ProtegeOWLParser(owlModel, false);
            try {
                parser.run(ontologyFileURI);
            }
            catch (Exception ex) {
            	String message = "Could not load OWL file into database";
                Log.getLogger().severe(message);
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                errors.add(new MessageError(ex, message));
            }
            owlModel.resetTripleStoreModel();
            handleErrors(errors);
            project.setProjectFilePath(null);
            tempProjectFile.delete();
            updateTripleStores(owlModel);

            // Sorting doesn't seem to work yet
            //System.out.println("Sorting subclasses...");
            //OWLUtil.sortSubclasses(owlModel);
            //System.out.println("... done");
        }
        catch (IOException e) {
            Log.getLogger().severe(Log.toString(e));
        }
        return project;
    }


    private void updateTripleStores(OWLDatabaseModel owlModel) {
        owlModel.resetTripleStoreModel();
        TripleStore topTripleStore = owlModel.getTripleStoreModel().getTopTripleStore();
        owlModel.getTripleStoreModel().setActiveTripleStore(topTripleStore);
        MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(owlModel);
        mnfs.setTopFrameStore(mnfs.getActiveFrameStore().getName());
    }


    public boolean canCreateProject(KnowledgeBaseFactory factory, boolean useExistingSources) {
        return super.canCreateProject(factory, useExistingSources) && useExistingSources;
    }


    public WizardPage createCreateProjectWizardPage(CreateProjectWizard wizard, boolean useExistingSources) {
        return new InitOWLDatabaseFromFileWizardPage(wizard, this);
    }
}
