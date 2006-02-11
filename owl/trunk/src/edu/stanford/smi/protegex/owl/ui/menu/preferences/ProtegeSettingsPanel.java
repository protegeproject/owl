package edu.stanford.smi.protegex.owl.ui.menu.preferences;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * A JComponent that allows to specify Protege specific features in an OWLModel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProtegeSettingsPanel extends JComponent {

    private JCheckBox importProtegeOntologyCheckBox;

    private OWLModel owlModel;

    private JCheckBox userDefinedDatatypesCheckBox;

    public final static String USER_DEFINED_DATATYPES = "edu.stanford.smi.protegex.owl.userDefinedDatatypes";


    ProtegeSettingsPanel(final JenaOWLModel owlModel) {
        this.owlModel = owlModel;

        importProtegeOntologyCheckBox = new JCheckBox("Import Protege metadata ontology");
        importProtegeOntologyCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (importProtegeOntologyCheckBox.isSelected()) {
                    enableProtegeOntology();
                }
                else {
                    disableProtegeOntology();
                }
            }
        });
        importProtegeOntologyCheckBox.setSelected(owlModel.getDefaultOWLOntology().getImports().contains(ProtegeNames.FILE));

        userDefinedDatatypesCheckBox = new JCheckBox("Support user-defined XML Schema datatypes (numeric ranges)");
        userDefinedDatatypesCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUserDefinedDatatypesSupported(owlModel, userDefinedDatatypesCheckBox.isSelected());
                ProtegeUI.reloadUI(owlModel);
            }
        });
        updateUserDefinedDatatypesCheckBox();

        setBorder(BorderFactory.createTitledBorder("Protege Features"));
        setLayout(new GridLayout(2, 1));
        add(importProtegeOntologyCheckBox);
        add(userDefinedDatatypesCheckBox);
    }


    private void disableProtegeOntology() {
        if(owlModel.isProtegeMetaOntologyImported() == true) {
            owlModel.getDefaultOWLOntology().removeImports(ProtegeNames.FILE);
            userDefinedDatatypesCheckBox.setEnabled(false);
        }
    }


    private void enableProtegeOntology() {
        if(owlModel.isProtegeMetaOntologyImported() == false) {
            ImportHelper importHelper = new ImportHelper((JenaOWLModel)owlModel);
            try {
                URI uri = new URI(ProtegeNames.FILE);
                importHelper.addImport(uri);
                importHelper.importOntologies();
                owlModel.getNamespaceManager().setPrefix(ProtegeNames.NS, ProtegeNames.PREFIX);
                userDefinedDatatypesCheckBox.setEnabled(true);
            } catch (Exception e) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel, e.getMessage());
            }

        }
    }


    public static boolean isUserDefinedDatatypesSupported(OWLModel owlModel) {
        return Boolean.TRUE.equals(owlModel.getOWLProject().getSettingsMap().getBoolean(USER_DEFINED_DATATYPES));
    }


    public static void setUserDefinedDatatypesSupported(OWLModel owlModel, boolean value) {
        owlModel.getOWLProject().getSettingsMap().setBoolean(USER_DEFINED_DATATYPES, Boolean.valueOf(value));
    }


    private void updateUserDefinedDatatypesCheckBox() {
        userDefinedDatatypesCheckBox.setEnabled(importProtegeOntologyCheckBox.isSelected());
        userDefinedDatatypesCheckBox.setSelected(ProtegeSettingsPanel.isUserDefinedDatatypesSupported(owlModel));
    }
}
