package edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner;

import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class SynchronizeReasonerTask extends AbstractReasonerTask {

    private ProtegeReasoner protegeReasoner;


    public SynchronizeReasonerTask(ProtegeReasoner protegeReasoner) {
        super(protegeReasoner);
        this.protegeReasoner = protegeReasoner;
    }


    public int getTaskSize() {
        return 1;
    }


    public void run() throws ProtegeReasonerException {
        TimeDifference td = new TimeDifference();
        setDescription("Synchronizing reasoner");
        setMessage("Synchronizing reasoner...");
        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();
        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Synchronize reasoner",
                null);
        postLogRecord(parentRecord);

        td.markStart();
        setProgressIndeterminate(true);

        doAbortCheck();
        setMessage("Updating reasoner...");

        OWLModel owlModel = protegeReasoner.getOWLModel();
        boolean eventsEnabled = owlModel.setGenerateEventsEnabled(false);
        
        try {
            // Clear the knowledgebase
            clearKnowledgeBase(parentRecord);
            doAbortCheck();
            
            // Transmit the kb to the reasoner
            transmitToReasoner(parentRecord);
            doAbortCheck();			
		} catch (Exception e) {
			postLogRecord(ReasonerLogRecordFactory.getInstance().createErrorMessageLogRecord("Errors at synchronization: " + e.getMessage(), null));
		} finally {
			owlModel.setGenerateEventsEnabled(eventsEnabled);
		}

        setProgressIndeterminate(false);
        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to synchronize = " + td, parentRecord));
        setMessage("Reasoner synchronized");
        setProgress(1);
        setTaskCompleted();
        
   }


    
    protected void clearKnowledgeBase(ReasonerLogRecord parentRecord) throws ProtegeReasonerException {
        TimeDifference td = new TimeDifference();
        td.markStart();
        setMessage("Clearing knowledge base...");
        
        protegeReasoner.reset();
   
        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to clear knowledgebase = " + td, parentRecord));
    }


    protected void transmitToReasoner(ReasonerLogRecord parentRecord) throws ProtegeReasonerException {
        TimeDifference td = new TimeDifference();
        setMessage("Updating reasoner...");
        td.markStart();

        // Send the whole knowledge base to the reasoner

        protegeReasoner.rebind();
        td.markEnd();        
        doAbortCheck(); 
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to update reasoner = " + td, parentRecord));

    }
    
}

