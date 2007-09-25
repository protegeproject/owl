package edu.stanford.smi.protegex.owl.inference.protegeowl.task;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 19, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ResetInferredHierarchyTask extends AbstractReasonerTask {

    private ProtegeOWLReasoner protegeOWLReasoner;


    public ResetInferredHierarchyTask(ProtegeOWLReasoner protegeOWLReasoner) {
        super(protegeOWLReasoner);
        this.protegeOWLReasoner = protegeOWLReasoner;
    }


    public int getTaskSize() {
        return 1;
    }


    public void run() throws DIGReasonerException {
        setDescription("Resetting inferred hierarchy");
        setMessage("Clearing...");
        setProgressIndeterminate(true);

        OWLModel kb = protegeOWLReasoner.getKnowledgeBase();
        kb.setGenerateEventsEnabled(false);
        kb.beginTransaction("Reset inferred hierarchy");
        OWLUtil.resetComputedSuperclasses(kb);
        kb.endTransaction();
        kb.setGenerateEventsEnabled(true);

        setProgressIndeterminate(false);
        setTaskCompleted();
        setProgress(1);
    }
}

