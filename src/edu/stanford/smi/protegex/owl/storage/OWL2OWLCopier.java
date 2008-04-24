package edu.stanford.smi.protegex.owl.storage;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;

import java.util.Iterator;

/**
 * A KnowledgeBaseCopier that can be used to generate a Jena OntModel from an
 * existing OWLModel (especially from a database).
 * Basically, the OntModel is populated by the JenaUpdater while the frames are
 * copied from the source to the target OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWL2OWLCopier extends KnowledgeBaseCopier {

    private OWLModel source;

    private OWLModel target;


    public OWL2OWLCopier(OWLModel source, OWLModel target) {
        super(source, target);
        this.source = source;
        this.target = target;
    }


    protected void createClses() {
        // Overloaded to make sure that named classes are created first
        for (Iterator it = source.getUserDefinedOWLNamedClasses().iterator(); it.hasNext();) {
            OWLNamedClass oldNamedCls = (OWLNamedClass) it.next();
            getNewCls(oldNamedCls);
        }
        super.createClses();  // Remaining ones (if any)
    }


    protected void createFacetOverrides(Cls oldCls) {
        if (oldCls instanceof OWLRestriction) {  // Only do facet overrides for restrictions
            super.createFacetOverrides(oldCls);
        }
    }


    protected Instance getNewInstance(Instance oldInstance) {
        if (oldInstance instanceof OWLOntology) {
            if (oldInstance.equals(source.getDefaultOWLOntology())) {
                return target.getDefaultOWLOntology();
            }
        }
        return super.getNewInstance(oldInstance);
    }


    /**
     * Makes sure that Restrictions are immediately initialized by their facet
     * overrides, so that the corresponding OntClass can be generated.
     *
     * @param oldInstance
     */
    protected void setInitialOwnSlotValues(Instance oldInstance) {
        if (oldInstance instanceof OWLRestriction) {
            OWLRestriction oldRestriction = (OWLRestriction) oldInstance;
            OWLRestriction newRestriction = (OWLRestriction) getNewInstance(oldRestriction);
            createFacetOverrides(oldRestriction);
            log("+ Initialized OWLRestriction " + newRestriction.getBrowserText());
        }
    }


    /**
     * Overloaded to capture special handling of namespace prefixes: Currently they
     * need to be changed through the NamespaceManager - setOwnSlotValues is not enough.
     *
     * @param newFrame
     * @param oldInstance
     * @param oldSlot
     */
    protected void setOwnSlotValues(Frame newFrame, Instance oldInstance, Slot oldSlot) {
        if (newFrame instanceof OWLOntology &&
                oldSlot.getName().equals(OWLNames.Slot.ONTOLOGY_PREFIXES) &&
                newFrame.equals(target.getDefaultOWLOntology())) {
            OWLOntology oi = (OWLOntology) newFrame;
            for (Iterator it = source.getNamespaceManager().getPrefixes().iterator(); it.hasNext();) {
                String prefix = (String) it.next();
                String namespace = source.getNamespaceManager().getNamespaceForPrefix(prefix);
                target.getNamespaceManager().setPrefix(namespace, prefix);
            }
            target.getNamespaceManager().setDefaultNamespace(source.getNamespaceManager().getDefaultNamespace());
        }
        else {
            super.setOwnSlotValues(newFrame, oldInstance, oldSlot);
        }
    }
}
