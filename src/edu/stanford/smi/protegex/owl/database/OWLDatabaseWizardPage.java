package edu.stanford.smi.protegex.owl.database;

import edu.stanford.smi.protege.storage.database.DatabaseWizardPage;
import edu.stanford.smi.protege.util.Wizard;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseWizardPage extends DatabaseWizardPage {

    private boolean fromExistingSources;

    private OWLDatabasePlugin plugin;


    public OWLDatabaseWizardPage(Wizard wizard, OWLDatabasePlugin plugin, boolean fromExistingSources) {
        super(wizard, plugin);
        this.fromExistingSources = fromExistingSources;
        this.plugin = plugin;
    }
}
