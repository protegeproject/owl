package edu.stanford.smi.protegex.owl.ui.importstree;

import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ImportsTreeNode extends LazyTreeNode {

    public ImportsTreeNode(LazyTreeNode parent, RDFResource resource) {
        super(parent, resource);
    }


    protected LazyTreeNode createNode(Object o) {
        return new ImportsTreeNode(this, (RDFResource) o);
    }


    protected int getChildObjectCount() {
        RDFResource resource = getResource();
        if (resource instanceof OWLOntology) {
            OWLOntology ontology = (OWLOntology) resource;
            return ontology.getImports().size();
        }
        else {
            return 0;
        }
    }


    protected Collection getChildObjects() {
        RDFResource resource = getResource();
        if (resource instanceof OWLOntology) {
            OWLOntology ontology = (OWLOntology) resource;
            return ontology.getImportResources();
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }


    protected Comparator getComparator() {
        return null;
    }


    private RDFResource getResource() {
        return (RDFResource) getUserObject();
    }
}
