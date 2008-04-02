package edu.stanford.smi.protegex.owl.ui.testing;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * @deprecated use OWLSettingsPanel directly (currently within PreferencesPanel)
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLTestSettingsAction extends AbstractOWLModelAction {

    public String getIconFileName() {
        return OWLIcons.TEST_SETTINGS;
    }


    public String getMenubarPath() {
        return OWL_MENU + PATH_SEPARATOR + "Testing";
    }


    public String getName() {
        return "Test settings...";
    }


    public void run(OWLModel owlModel) {
        OWLTestSettingsPanel.showOWLTestSettingsDialog(owlModel);
    }
}
