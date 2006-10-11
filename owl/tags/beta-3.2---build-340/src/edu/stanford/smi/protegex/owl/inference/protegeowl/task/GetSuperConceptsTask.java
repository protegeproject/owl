package edu.stanford.smi.protegex.owl.inference.protegeowl.task;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import org.w3c.dom.Document;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 17, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GetSuperConceptsTask extends AbstractSingleConceptWithConceptCollectionResultTask {

    public GetSuperConceptsTask(OWLClass aClass,
                                ProtegeOWLReasoner protegeOWLReasoner) {
        super("Computing superclasses", aClass, protegeOWLReasoner);
    }


    public void createQuery(Document doc)
            throws DIGReasonerException {
        getTranslator().createDirectSuperConceptsQuery(doc, "q0", getCls());
    }
}

