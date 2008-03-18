package edu.stanford.smi.protegex.owl.jena;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.CreateProjectFromFilePlugin;
import edu.stanford.smi.protegex.owl.ProtegeOWL;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateOWLProjectFromFilePlugin implements CreateProjectFromFilePlugin {


    public Project createProject(File file, Collection errors) {
        try {
            JenaKnowledgeBaseFactory.useStandalone = false;
            InputStream is = new FileInputStream(file);
            JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(is);
            // Make sure a URI is used
            owlModel.setProjectFileName(file.toURI().toString());
            return owlModel.getProject();
        }
        catch (Exception ex) {
            errors.add(ex);
            return null;
        }
    }


    public String[] getSuffixes() {
        return new String[]{
                "owl"
        };
    }


    public String getDescription() {
        return "OWL Files";
    }
}
