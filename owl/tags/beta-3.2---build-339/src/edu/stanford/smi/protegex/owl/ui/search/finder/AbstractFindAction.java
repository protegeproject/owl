package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
abstract class AbstractFindAction extends AbstractAction implements FindAction {

    private JTextComponent searchBox;

    protected ResultsViewModelFind find;

    protected boolean allowSave;

    private HostResourceDisplay hrd;

    public AbstractFindAction(ResultsViewModelFind find, Icon icon) {
        this(find, icon, null);
    }

    public AbstractFindAction(ResultsViewModelFind find, Icon icon, HostResourceDisplay hrd) {
        this(find, icon, hrd, false);
    }

    public AbstractFindAction(ResultsViewModelFind find, Icon icon, HostResourceDisplay hrd, boolean allowSave) {
        super(find.getDescription(), icon);
        this.find = find;
        this.hrd = hrd;
        this.allowSave = allowSave;
    }

    public void setTextBox(JTextComponent textBox) {
        this.searchBox = textBox;
    }

    public void actionPerformed(ActionEvent e) {

        String txt = "";

        if (searchBox != null) {
            txt = searchBox.getText();
        }

        find.startSearch(txt);
//        Map results = find.getResults();

        // for a single result, just go and select the resource
//        if ((results != null) && (results.size() == 1)) {
//            RDFResource result = (RDFResource) results.keySet().iterator().next();
//            OWLUI.selectResource(result, hrd);
//        }
//        else {
        // determine whether to show a table or a simple list
        if (find.getNumSearchProperties() > 1) {
            showResults(new FindResultsTableView(find, hrd));
        }
        else {
            showResults(new FindResultsListView(find, hrd));
        }
//        }

    }

    protected abstract void showResults(AbstractFindResultsView view);
}
