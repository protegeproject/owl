package edu.stanford.smi.protegex.owl.ui.testing;

import edu.stanford.smi.protegex.owl.model.Task;
import edu.stanford.smi.protegex.owl.model.TaskManager;
import edu.stanford.smi.protegex.owl.testing.*;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTestAction extends AbstractOWLModelTestAction {

    public String getIconFileName() {
        return OWLIcons.TEST;
    }


    public String getName() {
        return "Run ontology tests...";
    }


    protected List run(OWLTest[] tests, Task task) {
        TaskManager taskManager = owlModel.getTaskManager();
        List results = new ArrayList();
        double count = tests.length * 4;
        int j = 0;
        for (int i = 0; i < tests.length && !task.isCancelled(); i++) {
            try {
                OWLTest test = tests[i];
                String className = test.getClass().getName();
                int index = className.lastIndexOf('.');
                taskManager.setMessage(task, className.substring(index + 1));
                taskManager.setProgress(task, (int) (j++ / count * 100));
                if (test instanceof OWLModelTest) {
                    runOWLModelTest(results, (OWLModelTest) test);
                }
                taskManager.setProgress(task, (int) (j++ / count * 100));
                if (test instanceof RDFSClassTest) {
                    runOWLClsTest(results, (RDFSClassTest) test);
                }
                taskManager.setProgress(task, (int) (j++ / count * 100));
                if (test instanceof RDFPropertyTest) {
                    runOWLPropertyTest(results, (RDFPropertyTest) test);
                }
                taskManager.setProgress(task, (int) (j++ / count * 100));
                if (test instanceof RDFResourceTest) {
                    runOWLInstanceTest(results, (RDFResourceTest) test);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return results;
    }
}
