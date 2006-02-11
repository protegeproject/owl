package edu.stanford.smi.protegex.owl.javacode;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JavaCodeGeneratorAction extends AbstractOWLModelAction {

    public static final String GROUP = "Java";


    public String getMenubarPath() {
        return CODE_MENU + PATH_SEPARATOR + GROUP;
    }


    public String getName() {
        return "Generate Protege-OWL Java Code...";
    }


    public void run(OWLModel owlModel) {
        EditableJavaCodeGeneratorOptions options = new ProjectBasedJavaCodeGeneratorOptions(owlModel);
        JavaCodeGeneratorPanel panel = new JavaCodeGeneratorPanel(options);
        if (ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(owlModel.getProject()), panel,
                getName(), ModalDialogFactory.MODE_OK_CANCEL) == ModalDialogFactory.OPTION_OK) {
            panel.ok();
            JavaCodeGenerator creator = new JavaCodeGenerator(owlModel, options);
            try {
                creator.createAll();
                ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, "Java code successfully generated.");
            }
            catch (Exception ex) {
                ex.printStackTrace();
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                        "Could not create Java code:\n" + ex);
            }
        }
    }
}
