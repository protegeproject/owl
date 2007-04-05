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
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 4, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GetInferredSubClassesAction extends ResourceAction {


    public GetInferredSubClassesAction() {
        super("Get inferred subclasses", OWLIcons.getImageIcon(OWLIcons.GET_INFERRED_SUBCLASSES),
                ActionConstants.ACTION_GROUP);
    }


    public boolean isSuitable(Component component,
                              RDFResource resource) {
        return resource instanceof OWLClass;
    }


    public void actionPerformed(ActionEvent e) {
        final OWLModel owlModel = getResource().getOWLModel();
        final ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().getReasoner(owlModel);
        ReasonerActionRunner runner = new ReasonerActionRunner(new RunnableReasonerAction() {
            public void executeReasonerActions(ReasonerTaskListener taskListener) throws DIGReasonerException {
                reasoner.getSubclasses((OWLClass) getResource(), taskListener);
            }


            public OWLModel getOWLModel() {
                return owlModel;
            }
        }, false);
        runner.execute();
    }
}

