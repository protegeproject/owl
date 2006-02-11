package edu.stanford.smi.protegex.owl.ui.testing;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.testing.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLTestAction extends AbstractAction {

    protected OWLModel owlModel;

    protected OWLTestManager testManager;


    public AbstractOWLTestAction() {
    }


    public AbstractOWLTestAction(OWLModel owlModel,
                                 OWLTestManager testManager,
                                 String text,
                                 Icon icon) {
        super(text, icon);
        this.owlModel = owlModel;
        this.testManager = testManager;
    }


    public void actionPerformed(ActionEvent e) {
        OWLTest[] tests = getOWLTests();
        if (tests.length == 0) {
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                    "There are no tests activated.");
        }
        else {
            if (!OWLUI.isConfirmationNeeded(owlModel) ||
                    OWLUI.isConfirmed(owlModel, owlModel.getRDFResourceCount() > OWLUI.getConfirmationThreshold(owlModel))) {
                performAction(tests);
            }
        }
    }


    private void performAction(final OWLTest[] tests) {
        Task task = new AbstractTask("Running Tests", true, owlModel.getTaskManager()) {
            public void runTask() throws Exception {
                List results = run(tests, this);
                if (results.isEmpty()) {
                    showAllTestsPassedMessage(tests.length);
                }
                showResults(results, owlModel);
            }
        };
	    try {
		    owlModel.getTaskManager().run(task);
	    }
	    catch(Exception e) {
		    e.printStackTrace();
	    }
    }


    protected OWLTest[] getOWLTests() {
        return testManager.getOWLTests();
    }


    protected OWLTestResultsPanel getTestResultsPanel(OWLModel owlModel, List results) {
        return new OWLTestResultsPanel(owlModel, results, null, true);
    }


    protected abstract List run(OWLTest[] tests, Task task);


    protected void runOWLClsTest(List results, RDFSClassTest test) {
        final Collection owlSystemFrames = ((AbstractOWLModel) owlModel).getOWLSystemResources();
        for (Iterator it = owlModel.getRDFSClasses().iterator(); it.hasNext();) {
            RDFSClass rdfsClass = (RDFSClass) it.next();
            if (!owlSystemFrames.contains(rdfsClass)) {
                results.addAll(test.test(rdfsClass));
            }
        }
    }


    protected void runOWLInstanceTest(List results, RDFResourceTest test) {
        final Collection owlSystemFrames = ((AbstractOWLModel) owlModel).getOWLSystemResources();
        for (Iterator it = owlModel.getRDFSClasses().iterator(); it.hasNext();) {
            RDFSClass rdfsClass = (RDFSClass) it.next();
            if (!owlSystemFrames.contains(rdfsClass)) {
                results.addAll(test.test(rdfsClass));
            }
        }
        for (Iterator it = owlModel.getUserDefinedOWLProperties().iterator(); it.hasNext();) {
            OWLProperty owlProperty = (OWLProperty) it.next();
            results.addAll(test.test(owlProperty));
        }
        for (Iterator it = owlModel.getOWLIndividuals().iterator(); it.hasNext();) {
            RDFResource RDFResource = (RDFResource) it.next();
            results.addAll(test.test(RDFResource));
        }
    }


    protected void runOWLModelTest(List results, OWLModelTest test) {
        results.addAll(test.test(owlModel));
    }


    protected void runOWLPropertyTest(List results, RDFPropertyTest test) {
        for (Iterator it = owlModel.getUserDefinedOWLProperties().iterator(); it.hasNext();) {
            OWLProperty owlProperty = (OWLProperty) it.next();
            results.addAll(test.test(owlProperty));
        }
    }


    protected void showAllTestsPassedMessage(int count) {
        // JOptionPane.showMessageDialog(null, "All " + count + " tests passed successfully.");
    }


    public void showResults(List results, OWLModel owlModel) {
        OWLTestResultsPanel panel = getTestResultsPanel(owlModel, results);
        ResultsPanelManager.addResultsPanel(owlModel, panel, true);
    }
}
