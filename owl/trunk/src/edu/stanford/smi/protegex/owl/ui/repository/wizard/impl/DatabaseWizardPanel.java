/**
 * 
 */
package edu.stanford.smi.protegex.owl.ui.repository.wizard.impl;

import java.awt.GridLayout;
import java.net.URI;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.stanford.smi.protege.storage.database.AbstractDatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseProperty;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.impl.DatabaseRepository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryCreatorWizardPanel;

public class DatabaseWizardPanel extends RepositoryCreatorWizardPanel {
    private static final long serialVersionUID = 8313995336416582467L;
    
    private static Logger log = Log.getLogger(DatabaseWizardPanel.class);
    
    private EnumMap<DatabaseProperty, JTextField> textMap 
            = new EnumMap<DatabaseProperty, JTextField>(DatabaseProperty.class);
    private JComboBox tablesBox;
    public String ANY_TABLE_ENTRY = "--All Tables--";
    private Map<String, String> descriptionToTableMap = new HashMap<String, String>();

    private WizardPage wizardPage;
    
    public DatabaseWizardPanel(WizardPage wizardPage,
                       OWLModel owlModel) {
        TextDocumentListener listener = new TextDocumentListener();
        this.wizardPage = wizardPage;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
        tablesBox = new JComboBox();
        add(tablesBox);
        wizardPage.setPageComplete(validateFields());
        tablesBox.setSelectedIndex(0);
    }

    @Override
    public DatabaseRepository createRepository() {
        Object selectedTable = tablesBox.getSelectedItem();
        if (selectedTable == null || selectedTable.equals(ANY_TABLE_ENTRY)) {
            return createAllTablesRepository();
        }
        String table = descriptionToTableMap.get(selectedTable);
        if (table == null) {
            return createAllTablesRepository();
        }
        else {
            return createSingleTableRepository(table);
        }
        
    }
    
    private DatabaseRepository createSingleTableRepository(String table) {
        Level loggingLevel = AbstractDatabaseFrameDb.log.getLevel();
        AbstractDatabaseFrameDb.log.setLevel(Level.WARNING);
        try {
            return new DatabaseRepository(getText(DatabaseProperty.DRIVER_PROPERTY),
                                          getText(DatabaseProperty.URL_PROPERTY),
                                          getText(DatabaseProperty.USERNAME_PROPERTY),
                                          getText(DatabaseProperty.PASSWORD_PROPERTY),
                                          table);
        }
        catch (Throwable t) {
            if (log.isLoggable(Level.FINE))  {
                log.fine("driver = "  + getText(DatabaseProperty.DRIVER_PROPERTY));
                log.fine("url = " + getText(DatabaseProperty.URL_PROPERTY));
                log.fine("username = " + getText(DatabaseProperty.USERNAME_PROPERTY));
                log.fine("password = " + getText(DatabaseProperty.PASSWORD_PROPERTY));
                log.log(Level.FINE, "Create Repository failed", t);
            }
            return null; 
        }
        finally {
            AbstractDatabaseFrameDb.log.setLevel(loggingLevel);
        }
    }
    
    private DatabaseRepository createAllTablesRepository() {
        Level loggingLevel = AbstractDatabaseFrameDb.log.getLevel();
        AbstractDatabaseFrameDb.log.setLevel(Level.WARNING);
        try {
            return new DatabaseRepository(getText(DatabaseProperty.DRIVER_PROPERTY),
                                          getText(DatabaseProperty.URL_PROPERTY),
                                          getText(DatabaseProperty.USERNAME_PROPERTY),
                                          getText(DatabaseProperty.PASSWORD_PROPERTY));
        }
        catch (Throwable t) {
            if (log.isLoggable(Level.FINE))  {
                log.fine("driver = "  + getText(DatabaseProperty.DRIVER_PROPERTY));
                log.fine("url = " + getText(DatabaseProperty.URL_PROPERTY));
                log.fine("username = " + getText(DatabaseProperty.USERNAME_PROPERTY));
                log.fine("password = " + getText(DatabaseProperty.PASSWORD_PROPERTY));
                log.log(Level.FINE, "Create Repository failed", t);
            }
            return null; 
        }
        finally {
            AbstractDatabaseFrameDb.log.setLevel(loggingLevel);
        }
    }
    
    protected String getText(DatabaseProperty property) {
        JTextField text = textMap.get(property);
        return text.getText();
    }
    
    private boolean validateFields() {
        DatabaseRepository rep =  createRepository();
        descriptionToTableMap.clear();
        tablesBox.removeAllItems();
        tablesBox.addItem(ANY_TABLE_ENTRY);
        if (rep != null) {
            for (URI ontology : rep.getOntologies()) {
                String table = rep.getDBTable(ontology);
                String description = new StringBuffer(table)
                                        .append(" (")
                                        .append(ontology.toString())
                                        .append(")")
                                           .toString();
                descriptionToTableMap.put(description, table);
                tablesBox.addItem(description);
            }
        }
        return rep != null;
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