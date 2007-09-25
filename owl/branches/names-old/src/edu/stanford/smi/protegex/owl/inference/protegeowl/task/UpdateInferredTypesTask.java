package edu.stanford.smi.protegex.owl.inference.protegeowl.task;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGQueryResponse;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFIndividual;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import org.w3c.dom.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 28, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UpdateInferredTypesTask extends AbstractReasonerTask {

    private ProtegeOWLReasoner protegeOWLReasoner;


    public UpdateInferredTypesTask(ProtegeOWLReasoner protegeOWLReasoner) {
        super(protegeOWLReasoner);
        this.protegeOWLReasoner = protegeOWLReasoner;
    }


    /**
     * Gets the size of the task.  When the progress
     * reaches this size, the task should be complete.
     */
    public int getTaskSize() {
        return ReasonerUtil.getInstance().getIndividuals(protegeOWLReasoner.getKnowledgeBase()).size();
    }


    /**
     * Executes the task.
     *
     * @throws edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException
     *
     */
    public void run() throws DIGReasonerException {
        OWLModel kb = protegeOWLReasoner.getKnowledgeBase();

        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();

        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Computing inferred types",
                null);

        postLogRecord(parentRecord);
        setDescription("Computing inferred types");
        setMessage("Building reasoner query");

        // Build the types query
        TimeDifference td = new TimeDifference();
        td.markStart();
        Document doc = getTranslator().createAsksDocument(protegeOWLReasoner.getReasonerKnowledgeBaseURI());
        Iterator individualsIt = ReasonerUtil.getInstance().getIndividuals(kb).iterator();
        while (individualsIt.hasNext()) {
            final RDFIndividual curInd = (RDFIndividual) individualsIt.next();

            getTranslator().createIndividualTypesQuery(doc, curInd.getName(), curInd);
        }
        td.markEnd();

        // Log time
        postLogRecord(logRecordFactory.createInformationMessageLogRecord("Time to build query = " + td,
                parentRecord));

        // Query the reasoner

        setMessage("Querying reasoner...");

        td.markStart();

        Document responseDoc = protegeOWLReasoner.getDIGReasoner().performRequest(doc);


        td.markEnd();

        // Log time
        postLogRecord(logRecordFactory.createInformationMessageLogRecord("Time to query reasoner = " + td,
                parentRecord));

        // Update Protge-OWL
        setMessage("Updating Protege-OWL...");
        td.markStart();

        // Disable the events as we may not be updating protege
        // from the event dispatch thread
        kb.setGenerateEventsEnabled(false);
        kb.beginTransaction("Compute and update inferred types");
        Iterator responseIt = getTranslator().getDIGQueryResponseIterator(kb, responseDoc);


        Slot inferredTypesSlot = kb.getRDFProperty(ProtegeNames.Slot.INFERRED_TYPE);
        Slot classificationStatusSlot = ((AbstractOWLModel) kb).getProtegeClassificationStatusProperty();

        while (responseIt.hasNext()) {
            final DIGQueryResponse curResponse = (DIGQueryResponse) responseIt.next();
            final RDFIndividual curInd = kb.getRDFIndividual(curResponse.getID());

            if (curInd != null) {
                // Check the inferred types and asserted types
                // if there is a mismatch between the two then
                // mark the classification status of the individual
                // as changed. (MH - 15/09/04)
                final Collection inferredTypes = curResponse.getConcepts();
                if (inferredTypes.size() == 0) {
                    inferredTypes.add(curInd.getOWLModel().getOWLThingClass());
                }
                final Collection assertedTypes = curInd.getProtegeTypes();
                KnowledgeBase k = kb;
                k.setOwnSlotValues(curInd, inferredTypesSlot, inferredTypes);

                if (inferredTypes.containsAll(assertedTypes) == false &&
                        assertedTypes.containsAll(inferredTypes) == false) {
                    k.setOwnSlotValues(curInd, classificationStatusSlot, Collections.singleton(new Integer(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_CHANGED)));
                }
                else {
                    k.setOwnSlotValues(curInd, classificationStatusSlot, Collections.singleton(new Integer(OWLNames.CLASSIFICATION_STATUS_CONSISTENT_AND_UNCHANGED)));
                }
            }

            setProgress(getProgress() + 1);
            doAbortCheck();
        }
        kb.endTransaction();
        kb.setGenerateEventsEnabled(true);

        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to update Protege-OWL = " + td,
                parentRecord));
        setTaskCompleted();

    }
}

