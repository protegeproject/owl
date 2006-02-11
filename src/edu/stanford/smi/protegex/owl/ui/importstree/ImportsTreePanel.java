package edu.stanford.smi.protegex.owl.ui.importstree;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ImportsTreePanel extends JPanel {

    private ImportsTree tree;


    public ImportsTreePanel(OWLOntology rootOntology) {
        tree = new ImportsTree(rootOntology);
        OWLLabeledComponent lc = new OWLLabeledComponent("Imported Ontologies", new JScrollPane(tree));
        lc.addHeaderButton(new DownloadImportsAction(tree));
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
        setPreferredSize(new Dimension(500, 300));
    }


    public static void showDialog(OWLModel owlModel) {
        OWLOntology ontology = owlModel.getDefaultOWLOntology();
        ImportsTreePanel panel = new ImportsTreePanel(ontology);
        Component parent = ProtegeUI.getTopLevelContainer(owlModel.getProject());
        ProtegeUI.getModalDialogFactory().showDialog(parent, panel, "owl:imports Between Ontologies",
                ModalDialogFactory.MODE_CLOSE);
    }
}
