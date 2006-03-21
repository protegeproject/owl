package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.*;

/**
 * Checks the name, current browser slot and any synonym slots allocated
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
public class ResultsViewModelFind extends AbstractTableModel
        implements Find, ListModel, TableModel {

    protected List orderedKeys; // used for sorting alphabetically

    private HashMap map;

    private List listeners = new ArrayList(1);

    private Find findAlg;

    public ResultsViewModelFind(OWLModel owlModel, int type) {

        // wrap the finder, but allow isValidFrameToSearch() to be overloaded
        findAlg = new ThreadedFind(owlModel, type) {
            protected boolean isValidFrameToSearch(Frame f) {
                return super.isValidFrameToSearch(f) &&
                       ResultsViewModelFind.this.isValidFrameToSearch(f);
            }
        };

        findAlg.addResultListener(new SearchAdapter() {
            public void searchEvent(Find source) {
                fireDataChanged();
            }
        });
    }

    /////////////////////////// ListModel interface implementation

    public int getSize() {
        if (orderedKeys != null) {
            return orderedKeys.size();
        }
        else {
            return 0;
        }
    }


    public synchronized Object getElementAt(int index) {
        return orderedKeys.get(index);
    }


    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }


    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    /////////////////////////// TableModel interface implementation

    public int getRowCount() {
        return getSize();
    }


    public int getColumnCount() {
        return FindResult.NUM_COLUMNS;
    }


    public String getColumnName(int column) {
        return FindResult.getColumnName(column);
    }


    public synchronized Object getValueAt(int rowIndex, int columnIndex) {
        if (orderedKeys.size() > rowIndex) {
            Object key = orderedKeys.get(rowIndex);
            FindResult item = (FindResult) map.get(key);
            return item.get(columnIndex);
        }
        else {
            return null;
        }
    }


    private synchronized void fireDataChanged() {
        map = new HashMap(getResults());
        orderedKeys = new LinkedList(map.keySet());
        //Collections.sort(orderedKeys);

        // notify list listeners
        ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED,
                                            0, getSize());
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ((ListDataListener) i.next()).contentsChanged(e);
        }

        // notify table listeners
        this.fireTableDataChanged();
    }

    /**
     * Delegates to internal find
     *
     * @param s
     */
    public void startSearch(String s) {
        findAlg.startSearch(s);
    }

    public void startSearch(String s, int searchType) {
        findAlg.startSearch(s, searchType);
    }

    public void cancelSearch() {
        findAlg.cancelSearch();
    }

    public Map getResults() {
        return findAlg.getResults();
    }

    public Set getResultResources() {
        return findAlg.getResultResources();
    }

    public int getResultCount() {
        return findAlg.getResultCount();
    }

    public String getLastSearch() {
        return findAlg.getLastSearch();
    }

    public String getSummaryText() {
        return findAlg.getSummaryText();
    }

    public String getDescription() {
        return findAlg.getDescription();
    }

    public OWLModel getModel() {
        return findAlg.getModel();
    }

    public int getNumSearchProperties() {
        return findAlg.getNumSearchProperties();
    }

    public int getSearchType() {
        return findAlg.getSearchType();
    }

    public void addResultListener(SearchListener l) {
        findAlg.addResultListener(l);
    }

    public boolean removeResultListener(SearchListener l) {
        return findAlg.removeResultListener(l);
    }

    protected boolean isValidFrameToSearch(Frame f) {
        return true;
    }
}
