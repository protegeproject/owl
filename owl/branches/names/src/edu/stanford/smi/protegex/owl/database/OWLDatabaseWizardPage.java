package edu.stanford.smi.protegex.owl.database;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.JTextField;

import edu.stanford.smi.protege.storage.database.DatabaseWizardPage;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Wizard;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDatabaseWizardPage extends DatabaseWizardPage {

    private OWLDatabasePlugin plugin;
    
    private JTextField ontologyName;


    public OWLDatabaseWizardPage(Wizard wizard, OWLDatabasePlugin plugin) {
        super(wizard, plugin);
        this.plugin = plugin;
    }
    
    @Override
    protected void layoutComponents(Box panel) {
        if (!getFromExistingSources()) {
            ontologyName = ComponentFactory.createTextField("Enter the ontology name as a URL");
            ontologyName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent event) {
                    updateSetPageComplete();
                }
            });
            panel.add(new LabeledComponent("Ontology Name", ontologyName));
        }
        super.layoutComponents(panel);
    }
    
    @Override
    protected boolean isComplete() {
        if (getFromExistingSources()) {
            return super.isComplete();  
        }
        return super.isComplete() && isValidURL();
    }
    
    private boolean isValidURL() {
        try {
            new URL(ontologyName.getText());
            return true;
        }
        catch (MalformedURLException e) {
            setErrorText("Need valid URL for ontology name: " + e);
            return false;
        }
    }
    
    @Override
    public void onFinish() {
        if (!getFromExistingSources()) {
            plugin.setOntologyName(ontologyName.getText());
        }
        super.onFinish();
    }
    
    public boolean getFromExistingSources() {
        return false;
    }
}
