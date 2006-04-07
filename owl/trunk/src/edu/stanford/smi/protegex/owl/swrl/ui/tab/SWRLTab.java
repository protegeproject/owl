package edu.stanford.smi.protegex.owl.swrl.ui.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JLabel;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.ui.SWRLProjectPlugin;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.swrl.ui.table.SWRLTablePanel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

/**
 * A tab widget displaying all SWRL rules in the current ontology.
 *
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLTab extends AbstractTabWidget {

    private SWRLTablePanel panel;


    private void activateSWRL() {
        JenaOWLModel owlModel = (JenaOWLModel) getKnowledgeBase();
        try {
	        ImportHelper importHelper = new ImportHelper(owlModel);
	        importHelper.addImport(new URI(SWRLNames.SWRL_IMPORT));
	        importHelper.addImport(new URI(SWRLNames.SWRLB_IMPORT));
	        importHelper.importOntologies();
	        owlModel.getNamespaceManager().setPrefix(SWRLNames.SWRL_NAMESPACE, SWRLNames.SWRL_PREFIX);
            owlModel.getNamespaceManager().setPrefix(SWRLNames.SWRLB_NAMESPACE, SWRLNames.SWRLB_PREFIX);
        }
        catch (Exception ex) {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                    "Could not activate SWRL support:\n" + ex +
                            ".\nYour project might be in an inconsistent state now.");
            Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
        }
        SWRLProjectPlugin.adjustWidgets(getProject());
        ProtegeUI.reloadUI(getProject());
    }

    public void initialize() {
        setLabel("SWRL Rules");
        setIcon(SWRLIcons.getImpsIcon());
        setLayout(new BorderLayout());
        if (getKnowledgeBase() instanceof OWLModel) {
            OWLModel owlModel = (OWLModel) getKnowledgeBase();
            if (SWRLProjectPlugin.isSWRLImported(owlModel)) {
                panel = new SWRLTablePanel(owlModel, null);
                add(BorderLayout.CENTER, panel);
            }
            else {
                setLayout(new FlowLayout());
                add(new JLabel("Your ontology needs to import the SWRL ontology (" +
                        SWRLNames.SWRL_NAMESPACE + ")."));
                JButton activateButton = new JButton("Activate SWRL...");
                activateButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        activateSWRL();
                    }
                });
                add(activateButton);
                if (!(owlModel instanceof JenaOWLModel)) {
                    activateButton.setEnabled(false);
                }
            }
        }
        else {
            add(BorderLayout.CENTER, new JLabel("This tab can only be used with OWL projects."));
        }
    }


    public static boolean isSuitable(Project p, Collection errors) {
        if (p.getKnowledgeBase() instanceof OWLModel) {
            return true;
        }
        else {
            errors.add("This tab can only be used with OWL projects.");
            return false;
        }
    }

}
