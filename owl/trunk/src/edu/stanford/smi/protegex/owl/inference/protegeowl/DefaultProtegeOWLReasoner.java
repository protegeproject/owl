package edu.stanford.smi.protegex.owl.inference.protegeowl;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGError;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasoner;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DefaultDIGReasoner;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.logger.DIGLogger;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.logger.DIGLoggerListener;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ErrorMessageLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogger;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.*;
import edu.stanford.smi.protegex.owl.inference.util.TimeDifference;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.*;

import java.util.Collection;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jun 14, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class DefaultProtegeOWLReasoner implements ProtegeOWLReasoner {


    private DIGReasoner reasoner;

    private OWLModel model;

    private boolean synchronizeReasoner = true;

    private boolean autoSynchReasoner = true;

    private String kbURI;

    private ProjectListener projectListener = new ProjectAdapter() {
        public void projectClosed(ProjectEvent event) {
            // If the reasoner still contains a model then release it
            if (kbURI != null) {
                try {
                    reasoner.releaseKnowledgeBase(kbURI);
                    kbURI = null;
                    // Remove listeners from model
                    removeListeners();
                    model.getProject().removeProjectListener(projectListener);
                    model = null;
                }
                catch (DIGReasonerException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /* A ModelListener so we can listen to model
	events and determine whether or not we need to sync the
	reasoner. */
    private ModelListener modelListener = new ModelAdapter() {
        public void classCreated(RDFSClass cls) {
            reactToKnowledgeBaseChange();
        }


        public void classDeleted(RDFSClass cls) {
            reactToKnowledgeBaseChange();
        }


        public void propertyCreated(RDFProperty property) {
            reactToKnowledgeBaseChange();
        }


        public void propertyDeleted(RDFProperty property) {
            reactToKnowledgeBaseChange();
        }


        public void individualCreated(RDFResource resource) {
            reactToKnowledgeBaseChange();
        }


        public void individualDeleted(RDFResource resource) {
            reactToKnowledgeBaseChange();
        }
    };

    private ResourceAdapter resourceAdapter = new ResourceAdapter() {
        public void typeAdded(RDFResource resource,
                              RDFSClass type) {
            reactToKnowledgeBaseChange();
        }


        public void typeRemoved(RDFResource resource,
                                RDFSClass type) {
            reactToKnowledgeBaseChange();
        }
    };

    private PropertyListener propertyListener = new PropertyAdapter() {
        public void subpropertyAdded(RDFProperty property,
                                     RDFProperty subproperty) {
            reactToKnowledgeBaseChange();
        }


        public void subpropertyRemoved(RDFProperty property,
                                       RDFProperty subproperty) {
            reactToKnowledgeBaseChange();
        }


        public void superpropertyAdded(RDFProperty property,
                                       RDFProperty superproperty) {
            reactToKnowledgeBaseChange();
        }


        public void superpropertyRemoved(RDFProperty property,
                                         RDFProperty superproperty) {
            reactToKnowledgeBaseChange();
        }


        public void unionDomainClassAdded(RDFProperty property,
                                          RDFSClass rdfsClass) {
            reactToKnowledgeBaseChange();
        }


        public void unionDomainClassRemoved(RDFProperty property,
                                            RDFSClass rdfsClass) {
            reactToKnowledgeBaseChange();
        }
    };

    private ClassListener classListener = new ClassAdapter() {
        public void addedToUnionDomainOf(RDFSClass cls,
                                         RDFProperty property) {
            reactToKnowledgeBaseChange();
        }


        public void instanceAdded(RDFSClass cls,
                                  RDFResource instance) {
            reactToKnowledgeBaseChange();
        }


        public void instanceRemoved(RDFSClass cls,
                                    RDFResource instance) {
            reactToKnowledgeBaseChange();
        }


        public void removedFromUnionDomainOf(RDFSClass cls,
                                             RDFProperty property) {
            reactToKnowledgeBaseChange();
        }


        public void subclassAdded(RDFSClass cls,
                                  RDFSClass subclass) {
            reactToKnowledgeBaseChange();
        }


        public void subclassRemoved(RDFSClass cls,
                                    RDFSClass subclass) {
            reactToKnowledgeBaseChange();
        }


        public void superclassAdded(RDFSClass cls,
                                    RDFSClass superclass) {
            reactToKnowledgeBaseChange();
        }


        public void superclassRemoved(RDFSClass cls,
                                      RDFSClass superclass) {
            reactToKnowledgeBaseChange();
        }
    };

    private PropertyValueListener propertyValueListener = new PropertyValueAdapter() {
        public void nameChanged(RDFResource resource,
                                String oldName) {
            reactToKnowledgeBaseChange();
        }


        public void propertyValueChanged(RDFResource resource,
                                         RDFProperty property,
                                         Collection oldValues) {
            reactToKnowledgeBaseChange();
        }
    };

    private DIGLoggerListener digLoggerListener = new DIGLoggerListener() {
        public void errorLogged(DIGError error) {
            RDFResource cause = model.getOWLNamedClass(error.getID());
            ReasonerLogger.getInstance().postLogRecord(new ErrorMessageLogRecord(cause, error.getMessage(), null));
        }
    };


    public DefaultProtegeOWLReasoner(OWLModel kb) {
        this.reasoner = new DefaultDIGReasoner();
        DIGLogger.getInstance(this.reasoner).addListener(digLoggerListener);
        setKnowledgeBase(kb);
        kb.getProject().addProjectListener(projectListener);
    }


    /**
     * Gets the DIGReasoner that this reasoner uses
     * to talk to the external DIG Reasoner Process
     */
    public DIGReasoner getDIGReasoner() {
        return reasoner;
    }


    protected void setKnowledgeBase(OWLModel kb) {
        if (this.model != null) {
            removeListeners();
        }
        this.model = kb;
        forceReasonerReSynchronization();
        addListeners();
    }


    private void removeListeners() {
        model.removeModelListener(modelListener);
        model.removeResourceListener(resourceAdapter);
        model.removePropertyListener(propertyListener);
        model.removeClassListener(classListener);
        model.removePropertyValueListener(propertyValueListener);
    }


    private void addListeners() {
        model.addModelListener(modelListener);
        model.addResourceListener(resourceAdapter);
        model.addPropertyListener(propertyListener);
        model.addClassListener(classListener);
        model.addPropertyValueListener(propertyValueListener);
    }


    public void setURL(String url) {
        try {
            if (reasoner.getReasonerURL().equals(url) == false) {
                if (kbURI != null) {
                    reasoner.releaseKnowledgeBase(kbURI);
                    kbURI = null;
                    forceReasonerReSynchronization();
                }
                reasoner.setReasonerURL(url);
            }
        }
        catch (DIGReasonerException e) {
            e.printStackTrace();
        }
    }


    public String getURL() {
        return reasoner.getReasonerURL();
    }


    public boolean isConnected() {
        try {
            reasoner.getIdentity();
            return true;
        }

        catch (DIGReasonerException ex) {
            return false;
        }
    }


    private void reactToKnowledgeBaseChange() {
        if (autoSynchReasoner) {
            forceReasonerReSynchronization();
        }
    }


    public void forceReasonerReSynchronization() {
        synchronizeReasoner = true;
    }


    public void setAutoSynchronizationEnabled(boolean b) {
        autoSynchReasoner = b;
    }


    public boolean isAutoSynchronizationEnabled() {
        return autoSynchReasoner;
    }


    public String getReasonerKnowledgeBaseURI() {
        return kbURI;
    }


    protected void synchronizeReasoner(ReasonerTaskListener taskListener) throws DIGReasonerException {
        // Render the ontology into DIG and send it to the external
        // dig reasoner
        if (synchronizeReasoner == true || kbURI == null) {
            SynchronizeReasonerTask synchronizeReasonerTask = new SynchronizeReasonerTask(this);
            if (taskListener != null) {
                synchronizeReasonerTask.addTaskListener(taskListener);
            }

            if (kbURI == null) {
                kbURI = reasoner.createKnowledgeBase();
            }

            synchronizeReasonerTask.run();

            if (taskListener != null) {
                synchronizeReasonerTask.removeTaskListener(taskListener);
            }

            synchronizeReasoner = false;
        }
    }


    public DIGReasonerIdentity getIdentity() {
        DIGReasonerIdentity id = null;

        try {
            id = reasoner.getIdentity();
        }
        catch (DIGReasonerException e) {
            System.out.println(e.getMessage());
	        //e.printStackTrace();
        }

        return id;
    }


    public OWLModel getKnowledgeBase() {
        return model;
    }


    public void classifyTaxonomy(ReasonerTaskListener taskListener) throws DIGReasonerException {
        ClassifyTaxonomyTask task = new ClassifyTaxonomyTask(this);

        performTask(task, taskListener);
    }


    public void computeInferredHierarchy(ReasonerTaskListener taskListener) throws DIGReasonerException {
        UpdateInferredHierarchyTask task = new UpdateInferredHierarchyTask(this);

        performTask(task, taskListener);
    }


    public void computeEquivalentConcepts(ReasonerTaskListener taskListener) throws DIGReasonerException {
        UpdateEquivalentClassesTask task = new UpdateEquivalentClassesTask(this);

        performTask(task, taskListener);
    }


    public void computeInconsistentConcepts(ReasonerTaskListener taskListener) throws DIGReasonerException {
        UpdateInconsistentClassesTask task = new UpdateInconsistentClassesTask(this);
        performTask(task, taskListener);
    }


    public void computeInferredIndividualTypes(ReasonerTaskListener taskListener) throws DIGReasonerException {
        UpdateInferredTypesTask task = new UpdateInferredTypesTask(this);

        performTask(task, taskListener);
    }


    public boolean isSatisfiable(OWLClass aClass,
                                 ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetConceptSatisfiableTask task = new GetConceptSatisfiableTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();

    }


    public boolean isIntersectionSatisfiable(OWLClass[] clses,
                                             ReasonerTaskListener taskListener) throws DIGReasonerException {
        IsConceptIntersectionSatisfiableTask task = new IsConceptIntersectionSatisfiableTask(clses, this);
        performTask(task, taskListener);

        return task.getResult();
    }


    public boolean isSubsumedBy(OWLClass cls1,
                                OWLClass cls2,
                                ReasonerTaskListener taskListener) throws DIGReasonerException {
        IsSubsumedByTask task = new IsSubsumedByTask(this, cls1,
                cls2);

        performTask(task, taskListener);

        return task.getResult();
    }


    public boolean isDisjointTo(OWLClass cls1,
                                OWLClass cls2,
                                ReasonerTaskListener taskListener) throws DIGReasonerException {
        IsDisjointToTask task = new IsDisjointToTask(this, cls1,
                cls2);

        performTask(task, taskListener);

        return task.getResult();
    }


    public int getSubsumptionRelationship(OWLClass cls1,
                                          OWLClass cls2,
                                          ReasonerTaskListener taskListener)
            throws DIGReasonerException {
        GetSubsumptionRelationshipTask task = new GetSubsumptionRelationshipTask(this, cls1, cls2);
        performTask(task, taskListener);
        return task.getResult();
    }


    public Collection getSuperclasses(OWLClass aClass,
                                      ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetSuperConceptsTask task = new GetSuperConceptsTask(aClass, this);

        performTask(task, taskListener);

        return task.getResult();
    }


    public Collection getSuperclassesOfIntersection(OWLClass[] clses,
                                                    ReasonerTaskListener taskListener)
            throws DIGReasonerException {
        GetConceptIntersectionSuperclassesTask task = new GetConceptIntersectionSuperclassesTask(clses,
                this);
        performTask(task, taskListener);

        return task.getResult();
    }


    public Collection getSubclasses(OWLClass aClass,
                                    ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetSubConceptsTask task = new GetSubConceptsTask(aClass, this);
        performTask(task, taskListener);

        return task.getResult();
    }


    public Collection getAncestorClasses(OWLClass aClass,
                                         ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetAncestorConceptsTask task = new GetAncestorConceptsTask(aClass, this);
        performTask(task, taskListener);

        return task.getResult();
    }


    public Collection getDescendantClasses(OWLClass aClass,
                                           ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetDescendantConceptsTask task = new GetDescendantConceptsTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }


    public Collection getEquivalentClasses(OWLClass aClass,
                                           ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetEquivalentConceptsTask task = new GetEquivalentConceptsTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }


    public Collection getIndividualsBelongingToClass(OWLClass aClass,
                                                     ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetIndividualsBelongingToConceptTask task = new GetIndividualsBelongingToConceptTask(aClass, this);
        performTask(task, taskListener);
        return task.getResult();
    }


    public Collection getIndividualTypes(OWLIndividual individual,
                                         ReasonerTaskListener taskListener) throws DIGReasonerException {
        GetIndividualInferredTypesTask task = new GetIndividualInferredTypesTask(individual, this);
        performTask(task, taskListener);
        return task.getResult();
    }


    /**
     * Executes the specified task after synchronizing the reasoner if necessary.
     * The task listener (if not <code>null</code>)  is automatically registered
     * with the task before task execution, and the unregistered after task
     * execution.
     *
     * @param task         The task to be executed.
     * @param taskListener The listener to be registered with the task.  May be
     *                     <code>null</code> if no listener should be registered.
     */
    public void performTask(ReasonerTask task,
                            ReasonerTaskListener taskListener) throws DIGReasonerException {
        TimeDifference td = new TimeDifference();

        td.markStart();

        final ReasonerTaskListener tskLsnr = taskListener;

        ReasonerTaskAdapter taskAdapter;

        if (taskListener != null) {
            taskAdapter = new ReasonerTaskAdapter() {
                // Don't override task completed


                public void addedToTask(ReasonerTaskEvent event) {
                    tskLsnr.addedToTask(event);
                }


                public void progressChanged(ReasonerTaskEvent event) {
                    tskLsnr.progressChanged(event);
                }


                public void progressIndeterminateChanged(ReasonerTaskEvent event) {
                    tskLsnr.progressIndeterminateChanged(event);
                }


                public void descriptionChanged(ReasonerTaskEvent event) {
                    tskLsnr.descriptionChanged(event);
                }


                public void messageChanged(ReasonerTaskEvent event) {
                    tskLsnr.messageChanged(event);
                }


                public void taskFailed(ReasonerTaskEvent event) {
                    tskLsnr.taskFailed(event);
                }
            };
        }
        else {
            taskAdapter = new ReasonerTaskAdapter();
        }

        synchronizeReasoner(taskAdapter);

        if (taskListener != null) {
            task.addTaskListener(taskListener);
        }

        try {
            task.run();
        }
        catch (DIGReasonerException e) {
            String oldKbURI = kbURI;

            // Flag that the knowledgebase need recreating
            kbURI = null;

            // Attempt to release the old model.  This
            // may result in exceptions, but we have
            // already flagged that the knowledgebase
            // needs recreating
            reasoner.releaseKnowledgeBase(oldKbURI);

            synchronizeReasoner = true;

            throw e;
        }
        finally {
            if (taskListener != null) {
                task.removeTaskListener(taskListener);
            }

        }

        td.markEnd();

        ReasonerLogger.getInstance().postLogRecord(ReasonerLogRecordFactory.getInstance().createInformationMessageLogRecord("Total time: " + td, null));
    }
}

