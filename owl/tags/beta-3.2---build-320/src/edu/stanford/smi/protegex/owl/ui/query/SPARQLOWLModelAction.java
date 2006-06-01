package edu.stanford.smi.protegex.owl.ui.query;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SPARQLOWLModelAction extends AbstractOWLModelAction {

    public final static String GROUP = "Query";


    public String getIconFileName() {
        return OWLIcons.SPARQL;
    }


    public String getMenubarPath() {
        return OWL_MENU + PATH_SEPARATOR + GROUP;
    }


    public String getName() {
        return "Open SPARQL Query panel...";
    }


    public boolean isSuitable(OWLModel owlModel) {
        return owlModel instanceof JenaOWLModel;
    }


    public void run(OWLModel owlModel) {
        show(owlModel, true);
    }


    public static SPARQLResultsPanel show(OWLModel owlModel, boolean withQueryPanel) {
        SPARQLResultsPanel resultsPanel = (SPARQLResultsPanel) ResultsPanelManager.getResultsPanelByName(owlModel, SPARQLResultsPanel.NAME);
        if (resultsPanel == null) {
            resultsPanel = new SPARQLResultsPanel(owlModel, withQueryPanel);
            ResultsPanelManager.addResultsPanel(owlModel, resultsPanel, false);
        }
        else {
            ResultsPanelManager.setSelectedResultsPanel(owlModel, resultsPanel);
        }
        return resultsPanel;
    }
}
