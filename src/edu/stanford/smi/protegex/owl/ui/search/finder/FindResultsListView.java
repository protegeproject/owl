package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         19-Oct-2005
 */
public class FindResultsListView extends AbstractFindResultsView {

    private SelectableList list;

    protected FindResultsListView(Find find, HostResourceDisplay hrd) {
        super(hrd);

        setLayout(new BorderLayout(6, 6));

        list = new SelectableList();
        list.setModel(find);

        list.setCellRenderer(ResourceRenderer.createInstance());

        add(list, BorderLayout.CENTER);
    }

    public RDFResource getSelectedResource() {
        return (RDFResource) CollectionUtilities.getFirstItem(list.getSelection());
    }

//    public void setResults(Map results) {
//        if ((results != null) && (results.size() > 0)) {
//            list.setListData(results.keySet().toArray());
//            list.setSelectionInterval(0, 0);
//        }
//        else {
//            list.setListData(new Object[]{});
//        }
//    }

    public void addMouseListener(MouseListener l) {
        list.addMouseListener(l);
    }

    public void addKeyListener(KeyListener l) {
        list.addKeyListener(l);
    }

    public void requestFocus() {
        list.requestFocus();
        list.setSelectedIndex(0);
    }
}
