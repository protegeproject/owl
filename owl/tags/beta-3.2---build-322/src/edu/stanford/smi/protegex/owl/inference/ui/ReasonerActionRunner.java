package edu.stanford.smi.protegex.owl.inference.ui;

import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.inference.dig.exception.DIGReasonerException;
import edu.stanford.smi.protegex.owl.inference.dig.reasoner.DIGReasonerIdentity;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ProtegeOWLReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogRecordFactory;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLogger;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLoggerListener;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.ReasonerLoggerUtil;
import edu.stanford.smi.protegex.owl.inference.protegeowl.log.WarningMessageLogRecord;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskEvent;
import edu.stanford.smi.protegex.owl.inference.protegeowl.task.ReasonerTaskListener;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerPreferences;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;
import edu.stanford.smi.protegex.owl.ui.testing.OWLTestResultsPanel;

/**
 * User: matthewhorridge<br>
 * The Univeristy Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Aug 23, 2004<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The ReasonerActionRunner can be used to exuecute a number
 * of reasoner actions in a separate worker thread.  It will
 * also pop up a dialog with a progress bar and reasoner log.
 */
public class ReasonerActionRunner {
    private static transient Logger log = Log.getLogger(ReasonerActionRunner.class);

    /**
     * The progress dialog that will be poped up
     */
    // private ReasonerProgressModalDialog dlg;

    private boolean showClassifierResults;

    private RunnableReasonerAction runner;

    private boolean journalingEnabled;


    /**
     * Constructs a ReasonerActionRunner.
     *
     * @param runner                The RunnableReasonerAction that specifies
     *                              the code to be executed by this ReasonerActionRunner
     * @param showClassifierResults If <code>true</code> the
     *                              classification results panel will be displayed after
     *                              the reasone actions have been executed.
     */
    public ReasonerActionRunner(RunnableReasonerAction runner,
                                boolean showClassifierResults) {
        this.showClassifierResults = showClassifierResults;
        this.runner = runner;
    }


    /**
     * Executes the reasoner actions that are specified in the
     * RunnableReasonerAction's <code>executeReasonerActions</code>
     * method.  This will execute the actions is a worker thread
     * and pop up a progress dialog.
     */
    public void execute() {

        // Disable journaling whilst the classification takes place.
        // For very large ontologies, such as the NCI ontology, the journaling
        // file bloats with all the classification events.
        final Project project = runner.getOWLModel().getProject();
        if (project != null) {
            journalingEnabled = project.isJournalingEnabled();
        }

        // The reasoner that we will use
        final ProtegeOWLReasoner reasoner = ReasonerManager.getInstance().getReasoner(runner.getOWLModel());

        // Ensure that the reasoner URL is correct
        String prefURL = ReasonerPreferences.getInstance().getReasonerURL();
        String actualURL = reasoner.getDIGReasoner().getReasonerURL();
        if (actualURL.equals(prefURL) == false) {
            reasoner.setURL(prefURL);
        }

        // We want to obtain the identity of the reasoner
        // so that we can display its name to the user
        final DIGReasonerIdentity id = reasoner.getIdentity();

        // Create a new progress dialog
        Component appWindow = ProtegeUI.getTopLevelContainer(project);
        Frame appFrame = null;
        if (appWindow instanceof Frame) {
            appFrame = (Frame) appWindow;
        }

        String dlgTitle;
        if (id != null) {
            dlgTitle = "Connected to " + id.getName() + " " + id.getVersion();
        }
        else {
            dlgTitle = "Could not obtain reasoner identity";
        }

        final ReasonerProgressModalDialog dlg = new ReasonerProgressModalDialog(appFrame, dlgTitle);

        // Listen to the reasoner logger so we can present log messages
        // to the user.
        ReasonerLoggerListenerBridge logLoggerListenerBridge = new ReasonerLoggerListenerBridge(dlg);

        // Add the listener to the reasoner logger
        ReasonerLogger.getInstance().addListener(logLoggerListenerBridge);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (showClassifierResults) {
                    // Close the inferred hierarchy
                    showInferredHierarchy(false);
                }
            }
        });


        Runnable r = new Runnable() {
            public void run() {
                // Display the progress dialog
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        dlg.show();
                    }
                });

                try {
                    // Might need to setup the warnings as test results
                    final ArrayList warnings = new ArrayList();
                    boolean showWarningResultsPanel = ReasonerPreferences.getInstance().isShowWarningResultsPanel();
                    ReasonerLoggerListener loggerListener = null;
                    ReasonerLogger logger = ReasonerLogger.getInstance();
                    if (showWarningResultsPanel) {
                        loggerListener = new ReasonerLoggerListener() {
                            public void logRecordPosted(ReasonerLogRecord logRecord) {
                                if (logRecord instanceof WarningMessageLogRecord) {
                                    warnings.add(ReasonerLoggerUtil.convertToOWLTestResult((WarningMessageLogRecord) logRecord));
                                }
                            }
                        };
                        logger.addListener(loggerListener);
                    }

                    // The important part! Execute the reasoner actions
                    runner.executeReasonerActions(new ReasonerTaskListenerBridge(dlg));

                    // Set journaling to its original state.
                    if (project != null) {
                        project.setJournalingEnabled(journalingEnabled);
                    }

                    // Check if we need to display the warnings as test results
                    if (showWarningResultsPanel && warnings.size() > 0) {
                        logger.removeListener(loggerListener);
                        loggerListener = null;
                        OWLTestResultsPanel testResultsPanel = new ReasonerWarningPanel(runner.getOWLModel(),
                                warnings);
                        ResultsPanelManager.addResultsPanel(runner.getOWLModel(), testResultsPanel, true);
                    }

                    // Might need to show the classifier results panel
                    if (showClassifierResults == true) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                showInferredHierarchy(true);
                            }
                        });
                    }

                }
                catch (DIGReasonerException e1) {
                    final DIGReasonerException ex = e1;

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            // Signal that the dialog can be closed
                            dlg.setOKButtonEnabled(true);
                            dlg.setProgressIndeterminate(false);
                            // Post an error log record
                            ReasonerLogger logger = ReasonerLogger.getInstance();
                            ReasonerLogRecordFactory factory = ReasonerLogRecordFactory.getInstance();
                            ReasonerLogRecord log = factory.createDIGReasonerExceptionLogRecord(ex, null);
                            logger.postLogRecord(log);
                        }
                    });


                    // The reasoner may have disabled the kb events.
                    // Check that they are enabled - if not enable them
                    if (runner.getOWLModel().getGenerateEventsEnabled() == false) {
                        runner.getOWLModel().setGenerateEventsEnabled(true);
                    }
                    // Ensure that journaling is correct
                    if (project != null) {
                        project.setJournalingEnabled(journalingEnabled);
                    }
                }


            }
        };

        // Execute the whole thing
        try {
            Thread t = new Thread(r);
            t.start();
        }
        catch (Exception e) {
            dlg.setOKButtonEnabled(true);
            dlg.setProgressIndeterminate(false);
            Log.emptyCatchBlock(e);
        }
    }


    /**
     * Displays the inferred hierarchy and the classifier results
     * panel.
     *
     * @param visible
     */
    protected void showInferredHierarchy(boolean visible) {

        ProjectView projectView = ProtegeUI.getProjectView(runner.getOWLModel().getProject());
        OWLClassesTab classesTab = (OWLClassesTab) projectView.getTabByClassName(OWLClassesTab.class.getName());
        if (classesTab != null) {
            if (visible == true) {
                ReasonerPreferences reasonerPreferences = ReasonerPreferences.getInstance();
                if (reasonerPreferences.isAutomaticallyShowInferredHierarchy()) {
                    classesTab.setInferredClsesVisible(true);
                }
                if (reasonerPreferences.isShowReasonerResultsPanel()) {
                    classesTab.refreshChangedClses();
                    classesTab.requestFocusInWindow();
                }
            }
            else {
                classesTab.setInferredClsesVisible(false);
            }
        }
    }


    private class ReasonerLoggerListenerBridge implements ReasonerLoggerListener {

        private ReasonerProgressModalDialog dlg;


        public ReasonerLoggerListenerBridge(ReasonerProgressModalDialog dlg) {
            this.dlg = dlg;
        }


        public void logRecordPosted(final ReasonerLogRecord logRecord) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dlg.postLogRecord(logRecord);
                }
            });
        }
    }


    private class ReasonerTaskListenerBridge implements ReasonerTaskListener {

        private ReasonerProgressModalDialog dlg;


        public ReasonerTaskListenerBridge(ReasonerProgressModalDialog dlg) {
            this.dlg = dlg;
        }


        public void addedToTask(final ReasonerTaskEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int taskSize = event.getSource().getTaskSize();
                    dlg.setTask(event.getSource());
                    dlg.setProgressIndeterminate(false);
                    dlg.setProgressBarMaxValue(taskSize);
                    dlg.setCancelButtonEnabled(true);
                    dlg.setOKButtonEnabled(false);
                }
            });
        }


        public void progressChanged(final ReasonerTaskEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int progress = event.getSource().getProgress();
                    dlg.setProgress(progress);
                }
            });

        }


        public void progressIndeterminateChanged(final ReasonerTaskEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    boolean b = event.getSource().isProgressIndeterminate();
                    dlg.setProgressIndeterminate(b);
                }
            });
        }


        public void descriptionChanged(final ReasonerTaskEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String description = event.getSource().getDescription();
                    dlg.setDescription(description);
                }
            });
        }


        public void messageChanged(final ReasonerTaskEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String message = event.getSource().getMessage();
                    dlg.setMessage(message);
                }
            });
        }


        public void taskFailed(ReasonerTaskEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dlg.setOKButtonEnabled(true);
                    dlg.setCancelButtonEnabled(false);
                    dlg.setProgressIndeterminate(false);
                }
            });

        }


        public void taskCompleted(ReasonerTaskEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    dlg.setOKButtonEnabled(true);
                    dlg.setCancelButtonEnabled(false);
                }
            });
        }
    }
}

