package edu.stanford.smi.protegex.owl.ui.repository.wizard.impl;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.stanford.smi.protege.storage.database.DatabaseProperty;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.impl.DatabaseRepository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryCreatorWizardPanel;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryCreatorWizardPlugin;


public class DatabaseRepositoryCreatorWizardPlugin implements
        RepositoryCreatorWizardPlugin {
    private static transient final Logger log = Log.getLogger(DatabaseRepositoryCreatorWizardPlugin.class);
    
    public String getName() {
        return "Database Repository";
    }
    
    public String getDescription() {
        return "Create a repository representing ontologies found in a database";
    }
    
    public boolean isSuitable(OWLModel model) {
        return true;
    }

    public RepositoryCreatorWizardPanel createRepositoryCreatorWizardPanel(WizardPage wizardPage,
                                                                           OWLModel owlModel) {
        return new WizardPanel(wizardPage, owlModel);
    }
    
    private class WizardPanel extends RepositoryCreatorWizardPanel {
        private static final long serialVersionUID = 8313995336416582467L;
        
        private EnumMap<DatabaseProperty, JTextField> textMap 
                = new EnumMap<DatabaseProperty, JTextField>(DatabaseProperty.class);
        private WizardPage wizardPage;
        
        public WizardPanel(WizardPage wizardPage,
                           OWLModel owlModel) {
            TextDocumentListener listener = new TextDocumentListener();
            this.wizardPage = wizardPage;
            setLayout(new GridLayout(DatabaseRepository.DATABASE_FIELDS.length, 1));
            for (DatabaseProperty field : DatabaseRepository.DATABASE_FIELDS) {
                JTextField text;
                if (field == DatabaseProperty.PASSWORD_PROPERTY) {
                    text = ComponentFactory.createPasswordField();
                }
                else {
                    text = ComponentFactory.createTextField();
                }
                text.setText(DatabaseProperty.getProperty(field));
                textMap.put(field, text);
                LabeledComponent component = new LabeledComponent(field.getTitle(), text);
                text.getDocument().addDocumentListener(listener);
                add(component);
            }
            wizardPage.setPageComplete(validateFields());
        }

        @Override
        public Repository createRepository() {
            try {
                return new DatabaseRepository(getText(DatabaseProperty.DRIVER_PROPERTY),
                                              getText(DatabaseProperty.URL_PROPERTY),
                                              getText(DatabaseProperty.USERNAME_PROPERTY),
                                              getText(DatabaseProperty.PASSWORD_PROPERTY));
            }
            catch (SQLException e) {
                if (log.isLoggable(Level.FINE))  {
                    log.fine("driver = "  + getText(DatabaseProperty.DRIVER_PROPERTY));
                    log.fine("url = " + getText(DatabaseProperty.URL_PROPERTY));
                    log.fine("username = " + getText(DatabaseProperty.USERNAME_PROPERTY));
                    log.fine("password = " + getText(DatabaseProperty.PASSWORD_PROPERTY));
                    log.log(Level.FINE, "Create Repository failed", e);
                }
                return null;
            }
            catch (ClassNotFoundException e) {
                if (log.isLoggable(Level.FINE))  {
                    log.fine("driver = "  + getText(DatabaseProperty.DRIVER_PROPERTY));
                    log.fine("url = " + getText(DatabaseProperty.URL_PROPERTY));
                    log.fine("username = " + getText(DatabaseProperty.USERNAME_PROPERTY));
                    log.fine("password = " + getText(DatabaseProperty.PASSWORD_PROPERTY));
                    log.log(Level.FINE, "Create Repository failed", e);
                }
                return null; 
            }
        }
        
        private String getText(DatabaseProperty property) {
            JTextField text = textMap.get(property);
            return text.getText();
        }
        
        private boolean validateFields() {
            return createRepository() != null;
        }
        
        private class TextDocumentListener implements DocumentListener {
            public void insertUpdate(DocumentEvent e) {
                wizardPage.setPageComplete(validateFields());
            }


            public void removeUpdate(DocumentEvent e) {
                wizardPage.setPageComplete(validateFields());
            }


            public void changedUpdate(DocumentEvent e) {
                wizardPage.setPageComplete(validateFields());
            }
        }

    }

}
