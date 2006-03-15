package edu.stanford.smi.protegex.owl.jena;

import edu.stanford.smi.protege.util.FileField;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaFilePanel extends JPanel {

    private FileField owlFileField;


    public JenaFilePanel() {
        owlFileField = new FileField("OWL file name",
                                     null, JenaKnowledgeBaseSourcesEditor.EXTENSION,
                                     "Web Ontology Language (OWL) files");
        setLayout(new BorderLayout(8, 8));
        add(BorderLayout.NORTH, owlFileField);
    }


    public String getOWLFilePath() {
        String path = owlFileField.getPath();

        // make sure the filename has an extension
        File file = new File(path);
        String filename = file.getName();
        int extIndex = filename.indexOf('.');
        if (extIndex < 0){
            path = owlFileField.getPath() + "." + JenaKnowledgeBaseSourcesEditor.EXTENSION;
        }
        return path;
    }
}
