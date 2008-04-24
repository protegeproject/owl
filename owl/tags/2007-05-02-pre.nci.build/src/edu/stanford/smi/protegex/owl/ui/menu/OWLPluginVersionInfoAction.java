package edu.stanford.smi.protegex.owl.ui.menu;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.resource.OWLText;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLPluginVersionInfoAction extends AbstractOWLModelAction {

    public String getMenubarPath() {
        return "Help";
    }

    public String getName() {
        return "Prot\u00E9g\u00E9-OWL Version Info...";
    }

    public void run(OWLModel owlModel) {
        ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
        OWLText.getName() + " editor, version " + OWLText.getVersion() +
        " (Build " + OWLText.getBuildNumber() + ")\n\n" + "The " + OWLText.getName() +
       	" editor is being developed at Stanford Medical Informatics in collaboration\n" +
        "with the Medical Informatics Group at the University of Manchester.\n" +
        "Some toolbar icons were inspired by those from Eclipse and IntelliJ IDEA.\n\n" +
        "Open source from: http://smi-protege.stanford.edu/repos/protege/owl/trunk\n\n",
        "About Prot\u00E9g\u00E9-OWL");
    }
}
