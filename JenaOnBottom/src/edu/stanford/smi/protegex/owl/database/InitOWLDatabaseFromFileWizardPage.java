package edu.stanford.smi.protegex.owl.database;

import edu.stanford.smi.protege.util.URIField;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class InitOWLDatabaseFromFileWizardPage extends WizardPage {

    private OWLDatabasePlugin plugin;

    private URIField uriField;


    public InitOWLDatabaseFromFileWizardPage(Wizard wizard, OWLDatabasePlugin plugin) {
        super("Specify Ontology File", wizard);
        this.plugin = plugin;
        uriField = new URIField("URI of OWL file to populate the new database with",
                null, ".owl", "OWL Files");
        uriField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setPageComplete(true);
            }
        });
        add(BorderLayout.CENTER, uriField);
        add(BorderLayout.SOUTH,
                OWLUI.createHelpPanel("This allows you to use the Protege-OWL parser to load OWL/RDF files directly into a new database.  In theory, this parser should be able to handle files of arbitrary size, because it loads the file triple-by-triple.", "Create OWL Database from a File"));
        setPageComplete(false);
    }


    public WizardPage getNextPage() {
        return new OWLDatabaseWizardPage(getWizard(), plugin, true);
    }


    public void onFinish() {
        plugin.setOntologyFileURI(uriField.getAbsoluteURI());
    }
}
