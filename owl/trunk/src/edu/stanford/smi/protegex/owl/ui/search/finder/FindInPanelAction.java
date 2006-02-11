package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;

import javax.swing.*;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
public class FindInPanelAction extends AbstractFindAction {

    public FindInPanelAction(Find find, Icon icon, HostResourceDisplay hrd, boolean allowSave) {
        super(find, icon, hrd, allowSave);
    }


    protected void showResults(AbstractFindResultsView view) {
        RetainFindResultsPanel resultsPanel = new RetainFindResultsPanel(find, view);
        ResultsPanelManager.addResultsPanel(find.getModel(), resultsPanel, true);
    }
}
