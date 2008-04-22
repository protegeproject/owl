package edu.stanford.smi.protegex.owl.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

/**
 * A KnowledgeBaseCopier that can be used to generate a Jena OntModel from an
 * existing OWLModel (especially from a database).
 * Basically, the OntModel is populated by the JenaUpdater while the frames are
 * copied from the source to the target OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWL2OWLCopier extends KnowledgeBaseCopier {
    public static final transient Logger log = Log.getLogger(OWL2OWLCopier.class);

    private OWLModel source;

    private OWLModel target;


    public OWL2OWLCopier(OWLModel source, OWLModel target) {
        super(source, target);
        this.source = source;
        this.target = target;
        
        doneSlots.add(RDFNames.Slot.TYPE);
        doneSlots.add(OWLNames.Slot.ONTOLOGY_PREFIXES);
    }
    
    @Override
    public void run() {
        copyNamespaces();
        super.run();
    }

    private void copyNamespaces() {
        OWLUtil.renameOntology(target, target.getDefaultOWLOntology(), source.getDefaultOWLOntology().getName());
        NamespaceManager sourceNames = source.getNamespaceManager();
        NamespaceManager targetNames = target.getNamespaceManager();
        for (String prefix : new ArrayList<String>(targetNames.getPrefixes())) {
            if (targetNames.isModifiable(prefix)) {
                targetNames.removePrefix(prefix);
            }
        }
        for (String prefix : sourceNames.getPrefixes()) {
            if (targetNames.isModifiable(prefix)) {
                targetNames.setPrefix(sourceNames.getNamespaceForPrefix(prefix), prefix);
            }
        }
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
            if (log.isLoggable(Level.FINE)) {
                log.fine("+ Initialized OWLRestriction " + newRestriction.getBrowserText());
            }
        }
    }
}
