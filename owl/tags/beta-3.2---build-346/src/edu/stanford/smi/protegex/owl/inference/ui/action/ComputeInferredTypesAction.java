package edu.stanford.smi.protegex.owl.inference.ui.action;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.ui.ReasonerActionRunner;
import edu.stanford.smi.protegex.owl.inference.ui.RunnableReasonerAction;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 28, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ComputeInferredTypesAction extends AbstractOWLModelAction {

    public String getIconFileName() {
        return OWLIcons.CLASSIFY_INDIVIDUAL;
    }


    public String getMenubarPath() {
        return OWL_MENU + PATH_SEPARATOR + ActionConstants.ACTION_GROUP;
    }


    public String getName() {
        return "Compute inferred types...";
    }


    public String getToolbarPath() {
        return ActionConstants.ACTION_GROUP;
    }


    public void run(final OWLModel owlModel) {
        ReasonerActionRunner runner = new ReasonerActionRunner(new RunnableReasonerAction() {
            public void executeReasonerActions(ReasonerTaskListener taskListener) throws DIGReasonerException {
                ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().getReasoner(owlModel);
                reasoner.computeInferredIndividualTypes(taskListener);
            }


            public OWLModel getOWLModel() {
                return owlModel;
            }
        }, false);

        runner.execute();
    }
}

