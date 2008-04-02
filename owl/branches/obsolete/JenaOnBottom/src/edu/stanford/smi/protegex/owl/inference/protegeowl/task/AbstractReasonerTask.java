package edu.stanford.smi.protegex.owl.inference.protegeowl.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGTranslator;
import edu.stanford.smi.protegex.owl.inference.dig.translator.DIGTranslatorFactory;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogger;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 13, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A partial implementation of <code>ReasonerTask</code> that
 * implements several useful methods to set the task state and
 * simultaneously notify listeners of state changes.
 */
public abstract class AbstractReasonerTask implements ReasonerTask {
    private static transient Logger log = Log.getLogger(AbstractReasonerTask.class);

    private ArrayList listeners;

    private int progress;

    private boolean progressIndeterminate;

    private String description;

    private String message;

    private ReasonerTaskEvent evt = new ReasonerTaskEvent(this);

    private boolean abortTask;

    private DIGTranslator translator;


    public AbstractReasonerTask(ProtegeOWLReasoner protegeOWLReasoner) {
        abortTask = false;
        listeners = new ArrayList();
        translator = DIGTranslatorFactory.getInstance().createTranslator();
        try {
            DIGReasonerIdentity id = protegeOWLReasoner.getDIGReasoner().getIdentity();
            translator.setReasonerIdentity(id);
        }
        catch (DIGReasonerException e) {
          Log.emptyCatchBlock(e);
        }
    }


    /**
     * Gets the translator that should be used to convert
     * Protege-OWL to DIG and vice versa
     */
    protected DIGTranslator getTranslator() {
        return translator;
    }


    /**
     * Sets the progress of the task, simultaneously notifying
     * listeners that the progress has changed.
     *
     * @param progress The new progress value
     */
    protected void setProgress(int progress) {
        this.progress = progress;

        fireProgressChangedEvent();
    }


    /**
     * Sets the progress as indeterminate, notifying
     * listeners of the change.
     *
     * @param b <code>true</code> if the task progress
     *          cannot be determined, <code>false</code> if the
     *          task progress can be determined.
     */
    protected void setProgressIndeterminate(boolean b) {
        progressIndeterminate = b;

        fireProgressIndeterminateChnaged();
    }


    public int getProgress() {
        return progress;
    }


    public boolean isProgressIndeterminate() {
        return progressIndeterminate;
    }


    public String getDescription() {
        return description;
    }


    /**
     * Sets the high level task description, notifiying
     * listeners of the change in description.
     *
     * @param description The new task description.
     */
    protected void setDescription(String description) {
        this.description = description;

        fireDescriptionChangedEvent();
    }


    /**
     * Sets the task message, notifying any listeners
     * of the change in message.
     *
     * @param message The new message.
     */
    protected void setMessage(String message) {
        this.message = message;

        fireMessageChangedEvent();
    }


    public String getMessage() {
        return message;
    }


    /**
     * Sets the task as having a 'complete' status,
     * notifying listeners that the task is complete.
     */
    protected void setTaskCompleted() {
        fireTaskCompletedEvent();
    }


    /**
     * Sets the task as having a 'failed' status,
     * notifying listeners that the task failed.
     */
    protected void setTaskFailed() {
        fireTaskFailedEvent();
    }


    /**
     * Adds a listener.
     */
    public void addTaskListener(ReasonerTaskListener lsnr) {
        Iterator it = listeners.iterator();
        // Check that the listener has not been already added
        while (it.hasNext()) {
            final WeakReference wr = (WeakReference) it.next();
            if (wr.get().equals(lsnr)) {
                return;
            }
        }
        // If we have reached here then the listener
        // has not been added
        lsnr.addedToTask(new ReasonerTaskEvent(this));
        listeners.add(new WeakReference(lsnr));
    }


    /**
     * Removes a previously added listener.
     */
    public void removeTaskListener(ReasonerTaskListener lsnr) {
        Iterator it = listeners.iterator();
        while (it.hasNext()) {
            final WeakReference wr = (WeakReference) it.next();
            if (wr.get() != null) {
                if (wr.get().equals(lsnr)) {
                    wr.clear();
                    it.remove();
                }
            }
        }
    }


    /**
     * Informs registered listeners of a change in task progress
     */
    protected void fireProgressChangedEvent() {
        Iterator it = new ArrayList(listeners).iterator();
        while (it.hasNext()) {
            final ReasonerTaskListener lsnr = (ReasonerTaskListener) ((WeakReference) it.next()).get();
            if (lsnr != null) {
                lsnr.progressChanged(evt);
            }
        }
    }


    /**
     * Informs registered listeners of a change in the
     * state of whether the task progress can be determined
     * or not.
     */
    protected void fireProgressIndeterminateChnaged() {
        Iterator it = new ArrayList(listeners).iterator();
        while (it.hasNext()) {
            final ReasonerTaskListener lsnr = (ReasonerTaskListener) ((WeakReference) it.next()).get();
            if (lsnr != null) {
                lsnr.progressIndeterminateChanged(evt);
            }
        }
    }


    /**
     * Informs registered listeners of a change in task description.
     */
    protected void fireDescriptionChangedEvent() {
        Iterator it = new ArrayList(listeners).iterator();

        while (it.hasNext()) {
            final ReasonerTaskListener lsnr = (ReasonerTaskListener) ((WeakReference) it.next()).get();

            if (lsnr != null) {
                lsnr.descriptionChanged(evt);
            }
        }
    }


    /**
     * Informs registered listeners of a change in task message
     */
    protected void fireMessageChangedEvent() {
        Iterator it = new ArrayList(listeners).iterator();

        while (it.hasNext()) {
            final ReasonerTaskListener lsnr = (ReasonerTaskListener) ((WeakReference) it.next()).get();

            if (lsnr != null) {
                lsnr.messageChanged(evt);
            }
        }
    }


    /**
     * Informs registered listeners that the task failed.
     */
    protected void fireTaskFailedEvent() {
        Iterator it = new ArrayList(listeners).iterator();

        while (it.hasNext()) {
            final ReasonerTaskListener lsnr = (ReasonerTaskListener) ((WeakReference) it.next()).get();

            if (lsnr != null) {
                lsnr.taskFailed(evt);
            }
        }
    }


    /**
     * Informs registered listeners that the task was completed.
     */
    protected void fireTaskCompletedEvent() {
        Iterator it = new ArrayList(listeners).iterator();

        while (it.hasNext()) {
            final ReasonerTaskListener lsnr = (ReasonerTaskListener) ((WeakReference) it.next()).get();

            if (lsnr != null) {
                lsnr.taskCompleted(evt);
            }
        }
    }


    protected void postLogRecord(ReasonerLogRecord logRecord) {
        ReasonerLogger.getInstance().postLogRecord(logRecord);
    }


    public void setRequestAbort() {
        abortTask = true;
    }


    public boolean isRequestAbort() {
        return abortTask;
    }


    protected void doAbortCheck() throws DIGReasonerException {
        if (isRequestAbort()) {
            setProgressIndeterminate(false);
            setTaskFailed();
            throw new DIGReasonerException("Task aborted");
        }
    }
}
