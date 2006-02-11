package edu.stanford.smi.protegex.owl.ui.metadatatab;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.ui.metadatatab.imports.ImportsPanel;
import edu.stanford.smi.protegex.owl.ui.metadatatab.prefixes.PrefixesPanel;

import javax.swing.*;
import java.awt.*;

/**
 * A JPanel that allows users to view and edit the default namespace, the prefixes
 * and the imports of an OWLOntology.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLNamespacesPanel extends JPanel implements Disposable {

    private ImportsPanel importsPanel;

    private PrefixesPanel prefixesPanel;


    public OWLNamespacesPanel(OWLOntology ontology) {
        importsPanel = new ImportsPanel(ontology);
        prefixesPanel = new PrefixesPanel(ontology);
        importsPanel.setPrefixesPanel(prefixesPanel);
        prefixesPanel.setImportsPanel(importsPanel);
        setLayout(new BorderLayout(0, 10));
        add(BorderLayout.NORTH, new LabeledComponent("Default Namespace",
                prefixesPanel.getDefaultNamespaceField()));
        JSplitPane centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerPanel.setBorder(null);
        centerPanel.add(prefixesPanel);
        centerPanel.add(importsPanel);
        centerPanel.setResizeWeight(0.5);
        centerPanel.setDividerLocation(0.5);
        add(BorderLayout.CENTER, centerPanel);
    }


    public void dispose() {
        importsPanel.dispose();
        prefixesPanel.dispose();
    }


	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		importsPanel.setEnabled(enabled);
		prefixesPanel.setEnabled(enabled);
	}
}
