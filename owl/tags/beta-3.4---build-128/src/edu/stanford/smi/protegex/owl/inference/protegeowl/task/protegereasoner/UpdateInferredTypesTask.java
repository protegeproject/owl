package edu.stanford.smi.protegex.owl.inference.protegeowl.task.protegereasoner;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerUtil;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;


public class UpdateInferredTypesTask extends AbstractReasonerTask {

    private ProtegeReasoner protegeReasoner;
    
    //private Map<OWLIndividual, Collection<OWLClass>> individuals2inferredTypes = new HashMap<OWLIndividual, Collection<OWLClass>>();
    

    public UpdateInferredTypesTask(ProtegeReasoner protegeReasoner) {
        super(protegeReasoner);
        this.protegeReasoner = protegeReasoner;
    }


    /**
     * Gets the size of the task.  When the progress
     * reaches this size, the task should be complete.
     */
    public int getTaskSize() {
        return ReasonerUtil.getInstance().getIndividuals(protegeReasoner.getOWLModel()).size();
    }


    /**
     * Executes the task.
     *
     * @throws edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException
     *
     */
    public void run() throws ProtegeReasonerException {
        OWLModel kb = protegeReasoner.getOWLModel();
       
        ReasonerLogRecordFactory logRecordFactory = ReasonerLogRecordFactory.getInstance();

        ReasonerLogRecord parentRecord = logRecordFactory.createInformationMessageLogRecord("Computing inferred types", null);

        postLogRecord(parentRecord);
        setDescription("Computing inferred types");
      
        // Query the reasoner

        setMessage("Querying reasoner and updating Protege-OWL...");
        
        TimeDifference td = new TimeDifference();
               
        td.markStart();

        // Disable the events as we may not be updating protege
        // from the event dispatch thread
        boolean eventsEnabled = kb.setGenerateEventsEnabled(false);        
        try {
	        kb.beginTransaction("Compute and update inferred types");
	
	        Slot inferredTypesSlot = kb.getRDFProperty(ProtegeNames.Slot.INFERRED_TYPE);
	        Slot classificationStatusSlot = ((AbstractOWLModel) kb).getProtegeClassificationStatusProperty();

	        Collection allIndividuals = ReasonerUtil.getInstance().getIndividuals(kb);
	        
	        for (Iterator iterator = allIndividuals.iterator(); iterator.hasNext();) {
	        	OWLIndividual curInd = (OWLIndividual) iterator.next();
	        	// Check the inferred types and asserted types
	        	// if there is a mismatch between the two then
	        	// mark the classification status of the individual
	        	// as changed. (MH - 15/09/04)
	        	        	
	        	Collection<OWLClass> inferredTypes = protegeReasoner.getIndividualDirectTypes(curInd);
	        	
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

	        	setProgress(getProgress() + 1);
	        	doAbortCheck();
	        }
	        kb.commitTransaction();
        }
        catch (ProtegeReasonerException e) {
        	kb.rollbackTransaction();
        	throw e;
        }
        catch (Exception e) {
        	kb.rollbackTransaction();
        	
        	RuntimeException re = new RuntimeException();
        	re.initCause(e);
        	throw re;
		} finally{
			kb.setGenerateEventsEnabled(eventsEnabled);
		}

        td.markEnd();
        postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Time to update Protege-OWL = " + td, parentRecord));
        setTaskCompleted();
        
        //System.out.println(individuals2inferredTypes);

    }
}

