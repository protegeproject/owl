package edu.stanford.smi.protegex.owl.ui.subsumption;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.cls.HiddenClassTreeRoot;
import edu.stanford.smi.protegex.owl.ui.cls.Hierarchy;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         23-Feb-2006
 */
public class HiddenClassesPanel extends SubsumptionTreePanel {

    private Cls root;

    public HiddenClassesPanel(Cls root) {
        super(new HiddenClassTreeRoot(root),
              root.getKnowledgeBase().getSlot(Model.Slot.DIRECT_SUPERCLASSES),
              false);
        this.root = root;
    }

    public Hierarchy createClone() {
        return new HiddenClassesPanel(root);
    }

    public String getTitle() {
        return "Hidden Classes";
    }

    public void navigateToResource(RDFResource resource) {
        if (resource instanceof RDFSClass && !resource.isVisible()) {
            setSelectedClass((RDFSClass) resource);
        }
    }
}
