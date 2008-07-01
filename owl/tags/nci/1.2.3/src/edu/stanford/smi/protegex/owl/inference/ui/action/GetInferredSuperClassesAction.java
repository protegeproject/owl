package edu.stanford.smi.protegex.owl.inference.ui.action;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.ui.ReasonerActionRunner;
import edu.stanford.smi.protegex.owl.inference.ui.RunnableReasonerAction;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
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
 * Date: Sep 8, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GetInferredSuperClassesAction extends ResourceAction {

    public GetInferredSuperClassesAction() {
        super("Get inferred super classes",
                OWLIcons.getImageIcon(OWLIcons.GET_INFERRED_SUPERCLASSES), ActionConstants.ACTION_GROUP);
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        if (resource instanceof RDFSClass) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        final OWLModel kb = getResource().getOWLModel();

        final ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().getReasoner(kb);

        ReasonerActionRunner runner = new ReasonerActionRunner(new RunnableReasonerAction() {
            public void executeReasonerActions(ReasonerTaskListener taskListener) throws DIGReasonerException {
                reasoner.getSuperclasses((OWLClass) getResource(), taskListener);
            }


            public OWLModel getOWLModel() {
                return kb;
            }
        }, false);

        runner.execute();
    }
}

