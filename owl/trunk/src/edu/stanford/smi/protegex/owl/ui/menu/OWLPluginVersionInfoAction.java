package edu.stanford.smi.protegex.owl.ui.menu;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
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
        return "Protege-OWL Version Info...";
    }


    public void run(OWLModel owlModel) {
        ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                "Protege-OWL Plugin Version " + OWLNames.VERSION + " (Build " + OWLNames.BUILD + ")\n\n" +
                        "The OWL Plugin is being developed at Stanford Medical Informatics in collaboration\n" +
                        "with the Medical Informatics Group at the University of Manchester.\n" +
                        "Some toolbar icons were inspired by those from Eclipse and IntelliJ IDEA.\n\n" +
                        "Open source from: http://protege.stanford.edu/plugins/owl", "About...");
    }
}
