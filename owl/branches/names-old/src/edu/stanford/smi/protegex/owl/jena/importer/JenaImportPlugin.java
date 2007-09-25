package edu.stanford.smi.protegex.owl.jena.importer;

import com.hp.hpl.jena.util.FileUtils;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.ImportPlugin;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.jena.JenaFilePanel;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaImportPlugin implements ImportPlugin {

    public void dispose() {
    }


    public String getName() {
        return "OWL File";
    }


    public Project handleImportRequest() {
        Project result = null;
        JenaFilePanel panel = new JenaFilePanel();
        int rval = ProtegeUI.getModalDialogFactory().showDialog(null, panel, "OWL File to Import", ModalDialogFactory.MODE_OK_CANCEL);
        if (rval == ModalDialogFactory.OPTION_OK) {
            String filePath = panel.getOWLFilePath();
            WaitCursor cursor = new WaitCursor(ProjectManager.getProjectManager().getMainPanel());
            try {
                result = importProject(new File(filePath).toURI());
            }
            finally {
                cursor.hide();
            }
        }
        return result;
    }


    private Project importProject(URI uri) {
        Collection errors = new ArrayList();
        JenaKnowledgeBaseFactory jenaFactory = new JenaKnowledgeBaseFactory();
        Project owlProject = Project.createNewProject(jenaFactory, errors);
        JenaOWLModel owlModel = (JenaOWLModel) owlProject.getKnowledgeBase();
        owlModel.load(uri, FileUtils.langXMLAbbrev, errors);
        if (errors.isEmpty()) {
            Project project = Project.createNewProject(null, new ArrayList());
            KnowledgeBase kb = project.getKnowledgeBase();
            new OWLImporter(owlModel, kb);
            return project;
        }
        else {
            return null;
        }
    }
}
