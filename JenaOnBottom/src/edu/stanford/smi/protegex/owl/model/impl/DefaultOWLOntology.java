package edu.stanford.smi.protegex.owl.model.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.hp.hpl.jena.rdf.model.Resource;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFExternalResource;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * The default implementation of the OWLObjectProperty interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLOntology extends DefaultRDFIndividual implements OWLOntology {

    public DefaultOWLOntology(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }
    
    public DefaultOWLOntology(KnowledgeBase kb, Resource o) {
      super(kb, o);
    }


    public DefaultOWLOntology() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLOntology(this);
    }


    public void addBackwardCompatibleWith(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        addOwnSlotValue(slot, resource);
    }


    /**
     * Imprtant - for usage see <CODE>OWLOntology</CODE>
     *
     * @param uri
     */
    public void addImports(String uri) {

        TripleStore ts = getOWLModel().getTripleStoreModel().getTripleStore(uri);
        if (ts != null) {
            OWLOntology ont = (OWLOntology) TripleStoreUtil.getFirstOntology(getOWLModel(), ts);
            addImportsHelper(ont);
        }
        else {
            RDFUntypedResource resource = getOWLModel().getRDFUntypedResource(uri, true);
            addImportsHelper(resource);
            // @@TODO we should probably ALWAYS create an owl:Ontology (but not sure if this will have negative side-effects)
        }
    }


    public void addImports(URI uri) {
        addImports(uri.toString());
    }


    public void addImports(RDFExternalResource resource) {
        addImportsHelper(resource);
    }


    public void addImports(RDFUntypedResource resource) {
        addImportsHelper(resource);
    }


    public void addImports(OWLOntology ontology) {
        addImportsHelper(ontology);
    }


    private void addImportsHelper(Object resource) {
        Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.IMPORTS);
        addOwnSlotValue(slot, resource);
    }


    public void addIncompatibleWith(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.INCOMPATIBLE_WITH);
        addOwnSlotValue(slot, resource);
    }


    public void addPriorVersion(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.PRIOR_VERSION);
        addOwnSlotValue(slot, resource);
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof OWLOntology) {
            OWLOntology ontology = (OWLOntology) object;
            return getURI().equals(ontology.getURI());
        }
        return false;
    }


    public Collection getBackwardCompatibleWith() {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        return getOwnSlotValues(slot);
    }


    public String getBrowserText() {
        String uri = getOntologyURI();
        if (uri == null) {
            return "DefaultOntology";
        }
        else {
            return "Ontology(" + uri + ")";
        }
    }


    public Icon getIcon() {
        Icon icon = OWLIcons.getImageIcon("OWLOntology");
        if (!isAssociatedTriplestoreEditable() || !isActive()) {
            icon = OWLIcons.getReadOnlyIcon((ImageIcon) icon,
                                            OWLIcons.RDF_INDIVIDUAL_FRAME);
        }
        return icon;
    }


    public Collection getImports() {
        Collection resources = getImportResources();
        List results = new ArrayList();
        for (Iterator it = resources.iterator(); it.hasNext();) {
            Object o = it.next();
            results.add(((RDFResource) o).getURI());
        }
        return results;
    }


    public Collection getImportResources() {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.IMPORTS);
        return getOwnSlotValues(slot);
    }


    public Collection getIncompatibleWith() {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.INCOMPATIBLE_WITH);
        return getOwnSlotValues(slot);
    }


    public String getOntologyURI() {
        return getURI();
    }


    public Collection getPriorVersions() {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.PRIOR_VERSION);
        return getOwnSlotValues(slot);
    }


    public void removeBackwardCompatibleWith(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.BACKWARD_COMPATIBLE_WITH);
        removeOwnSlotValue(slot, resource);
    }


    public void removeImports(String uri) {
        Object removeValue = null;
        Collection values = getImportResources();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object o = it.next();
            String otherURI = ((RDFResource) o).getURI();
            if (otherURI.equals(uri)) {
                removeValue = o;
                break;
            }
        }
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.IMPORTS);
        removeOwnSlotValue(slot, removeValue);
    }


    public void removeIncompatibleWith(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.INCOMPATIBLE_WITH);
        removeOwnSlotValue(slot, resource);
    }


    public void removePriorVersion(String resource) {
        edu.stanford.smi.protege.model.Slot slot = getKnowledgeBase().getSlot(OWLNames.Slot.PRIOR_VERSION);
        removeOwnSlotValue(slot, resource);
    }

    public boolean isAssociatedTriplestoreEditable() {
        boolean result = false;
        OWLModel owlModel = getOWLModel();
        TripleStore top = owlModel.getTripleStoreModel().getTopTripleStore();
        if (this == TripleStoreUtil.getFirstOntology(owlModel, top)) {
            result = true;
        }
        else {
            RepositoryManager man = owlModel.getRepositoryManager();
            URI ontURI = null;
            try {
                ontURI = new URI(getURI());
                Repository rep = man.getRepository(ontURI);
                if (rep != null) {
                    result = rep.isWritable(ontURI);
                }
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean isActive() {
        TripleStore active = getOWLModel().getTripleStoreModel().getActiveTripleStore();
        return this == TripleStoreUtil.getFirstOntology(getOWLModel(), active);
    }
}
