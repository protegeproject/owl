package edu.stanford.smi.protegex.owl.inference.protegeowl.task;

import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGQueryResponse;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 17, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class GetIndividualInferredTypesTask extends AbstractReasonerTask implements CollectionResultReasonerTask {

    private OWLIndividual individual;

    private ProtegeOWLReasoner protegeOWLReasoner;

    private HashSet types;


    public GetIndividualInferredTypesTask(OWLIndividual individual,
                                          ProtegeOWLReasoner protegeOWLReasoner) {
        super(protegeOWLReasoner);
        this.individual = individual;
        this.protegeOWLReasoner = protegeOWLReasoner;

        types = new HashSet();
    }


    public int getTaskSize() {
        return 1;
    }


    public void run()
            throws DIGReasonerException {
        OWLModel kb = protegeOWLReasoner.getKnowledgeBase();

        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();

        setDescription("Getting types for individual");
        setProgress(0);
        setMessage("Building reasoner query...");

        Document doc = getTranslator().createAsksDocument(protegeOWLReasoner.getReasonerKnowledgeBaseURI());
        getTranslator().createIndividualTypesQuery(doc, "q0", individual);

        setMessage("Querying reasoner...");

        Document responseDoc = protegeOWLReasoner.getDIGReasoner().performRequest(doc);

        Iterator it = getTranslator().getDIGQueryResponseIterator(kb, responseDoc);

        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Inferred types for: " + individual.getBrowserText(), null);
        postLogRecord(parentRecord);

        while (it.hasNext()) {
            final DIGQueryResponse curResponse = (DIGQueryResponse) it.next();

            types.addAll(curResponse.getConcepts());
        }

        Iterator typesIt = types.iterator();

        while (typesIt.hasNext()) {
            final RDFSClass curClass = (RDFSClass) typesIt.next();

            postLogRecord(logRecordFactory.createOWLInstanceLogRecord(curClass, parentRecord));
        }

        setMessage("Finished");

        setTaskCompleted();
    }


    public Collection getResult() {
        return types;
    }
}

