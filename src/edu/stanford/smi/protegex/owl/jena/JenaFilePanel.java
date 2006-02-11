package edu.stanford.smi.protegex.owl.jena;

import edu.stanford.smi.protege.util.FileField;

import javax.swing.*;
import java.awt.*;

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
        return owlFileField.getPath();
    }
}
