package edu.stanford.smi.protegex.owl.ui.importstree;

import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ImportsTree extends JTree {

    private OWLOntology ontology;


    public ImportsTree(OWLOntology rootOntology) {
        super(new ImportsTreeRoot(rootOntology));
        this.ontology = rootOntology;
        setCellRenderer(new ResourceRenderer());
        setRootVisible(false);
        expandRow(0);
    }


    private void addResources(Set result, ImportsTreeNode node) {
        Object value = node.getUserObject();
        if (!result.contains(value)) {
            result.add(value);
            for (int i = 0; i < node.getChildCount(); i++) {
                ImportsTreeNode childNode = (ImportsTreeNode) node.getChildAt(i);
                addResources(result, childNode);
            }
        }
    }


    public OWLOntology getRootOntology() {
        return ontology;
    }


    /**
     * Gets the currently selected resources.
     *
     * @return a Set of RDFResources
     */
    public Set getSelectedResources() {
        Set result = new HashSet();
        TreePath[] paths = getSelectionPaths();
        if (paths != null && paths.length > 0) {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                ImportsTreeNode node = (ImportsTreeNode) path.getLastPathComponent();
                result.add(node.getUserObject());
            }
        }
        else {
            LazyTreeRoot root = (LazyTreeRoot) getModel().getRoot();
            addResources(result, (ImportsTreeNode) root.getChildAt(0));
        }
        return result;
    }
}
