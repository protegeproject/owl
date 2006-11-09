package edu.stanford.smi.protegex.owl.jena.export;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.ExportPlugin;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.jena.JenaFilePanel;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.storage.ProtegeSaver;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaExportPlugin implements ExportPlugin {

    public void dispose() {
    }


    public String getName() {
        return "OWL";
    }


    public void handleExportRequest(Project project) {
        if (project.getKnowledgeBase() instanceof JenaOWLModel) {
            ProtegeUI.getModalDialogFactory().showMessageDialog((OWLModel) project.getKnowledgeBase(),
                    "This project is already an OWL Files project.");
        }
        else {
            JenaFilePanel panel = new JenaFilePanel();
            int rval = ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(project),
                    panel, "OWL File to Export", ModalDialogFactory.MODE_OK_CANCEL);
            if (rval == ModalDialogFactory.OPTION_OK) {
                String filePath = panel.getOWLFilePath();
                WaitCursor cursor = new WaitCursor(ProjectManager.getProjectManager().getMainPanel());
                try {
                    exportProject(project.getKnowledgeBase(), filePath);
                }
                catch (Exception ex) {
                    Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                    ProtegeUI.getModalDialogFactory().showErrorMessageDialog(panel,
                            "Export failed. Please see console for details.\n" + ex);
                }
                finally {
                    cursor.hide();
                }
            }
        }
    }


    private void exportProject(KnowledgeBase kb, String filePath) {
        Collection errors = new ArrayList();
        JenaKnowledgeBaseFactory factory = new JenaKnowledgeBaseFactory();
        Project newProject = Project.createNewProject(factory, errors);
        URI fileURI = new File(filePath).toURI();
        newProject.setProjectURI(fileURI);
        JenaOWLModel owlModel = (JenaOWLModel) newProject.getKnowledgeBase();
        if (kb instanceof OWLDatabaseModel) {
            OntModel newModel = ((OWLDatabaseModel) kb).getOntModel();
            owlModel.save(fileURI, FileUtils.langXML, errors, newModel);
        }
        else {  // Any other Protege format
            // TODO: owlModel.initWithProtegeMetadataOntology(errors);
            new ProtegeSaver(kb, owlModel).run();
            owlModel.save(fileURI, FileUtils.langXMLAbbrev, errors);
        }
        if (errors.size() == 0) {
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                    "Project has been exported to " + filePath);
        }
        else {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                    "Export Failed: " + errors.iterator().next());
        }
    }
}
