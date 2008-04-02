package edu.stanford.smi.protegex.owl.inference.ui.action;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.ui.ReasonerActionRunner;
import edu.stanford.smi.protegex.owl.inference.ui.RunnableReasonerAction;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 17, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ComputeIndividualsBelongingToClassAction extends ResourceAction implements RunnableReasonerAction {

    public ComputeIndividualsBelongingToClassAction() {
        super("Compute individuals belonging to class",
                OWLIcons.getComputeIndividualsBelongingToClassIcon(), ActionConstants.ACTION_GROUP);
    }


    public boolean isSuitable(Component component,
                              RDFResource resource) {
        if (resource instanceof RDFSClass) {
            return true;
        }
        else {
            return false;
        }
    }


    public void actionPerformed(ActionEvent e) {
        ReasonerActionRunner runner = new ReasonerActionRunner(this, false);

        runner.execute();
    }


    public void executeReasonerActions(ReasonerTaskListener taskListener) throws DIGReasonerException {
        ProtegeOWLReasoner reasoner;

        reasoner = ReasonerManager.getInstance().getReasoner(getResource().getOWLModel());

        reasoner.getIndividualsBelongingToClass((OWLClass) getResource(), taskListener);
    }
}

