package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.*;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * Checks the name, current browser slot and any synonym slots allocated
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
public class DefaultFind extends AbstractTableModel implements Find {

    private static final int MAX_MATCHES = -1;

    protected OWLModel owlModel;
    protected Map results;
    protected List orderedKeys; // used for sorting alphabetically
    protected String string;
    private List listeners = new ArrayList(1);
    private int searchType;


    public DefaultFind(OWLModel owlModel, int type) {
        this.owlModel = owlModel;
        this.searchType = type;
    }

    public void startSearch(String s) {
        startSearch(s, this.searchType);
    }

    public void startSearch(String s, int searchType) {
        string = s;
        results = new HashMap();
        this.searchType = searchType;

        if ((s != null) && (s.length() > 0)) {
            List searchProps = getSearchProperties();

            for (Iterator i = searchProps.iterator(); i.hasNext();) {
                results.putAll(searchOnSlot((Slot) i.next(), s, null, searchType));
            }
            String lang = owlModel.getDefaultLanguage();
            if (lang != null) {
                for (Iterator i = searchProps.iterator(); i.hasNext();) {
                    Slot slot = (Slot) i.next();
                    if (!slot.equals(owlModel.getNameSlot())) {
                        results.putAll(searchOnSlot(slot, s, lang, searchType));
                    }
                }
            }

            fireDataChanged();
        }
    }


    /**
     * by default automatically search the name and the current browser slot
     * as well as the search synonym slots (if set)
     */
    protected List getSearchProperties() {
        List searchProps = new ArrayList();

        Collection synonymProps = owlModel.getSearchSynonymProperties();
        searchProps.addAll(synonymProps);

        searchProps.add(owlModel.getNameSlot());

        Slot bs = owlModel.getOWLNamedClassClass().getBrowserSlotPattern().getFirstSlot();
        if ((!bs.equals(owlModel.getNameSlot())) && (!synonymProps.contains(bs))) {
            searchProps.add(bs);
        }

        return searchProps;
    }


    public Map getResults() {
        return results;
    }


    public String getLastSearch() {
        return string;
    }


    public String getSummaryText() {
        return "Search for \"" + string + "\" : (" + results.size() + " matches)";
    }


    public String getDescription() {
        return "Find Resource";
    }


    public OWLModel getModel() {
        return owlModel;
    }


    public int getNumSearchProperties() {
        return getSearchProperties().size();
    }


    protected Map searchOnSlot(Slot searchProp, String searchStr,
                               String lang, int searchType) {
        Map results = new HashMap();

        Collection frames = null;

        String actualSearchString = searchStr;

        switch(searchType){
            case STARTS_WITH:
                actualSearchString += "*"; // no break
            case EXACTLY_MATCHES:
                if (lang != null) {
                    actualSearchString = "~#" + lang + " " + actualSearchString;
                }
                break;

            case CONTAINS: actualSearchString += "*"; // no break
            case ENDS_WITH: actualSearchString = "*" + actualSearchString; break;
        }

        frames = owlModel.getMatchingFrames(searchProp, null, false, actualSearchString, MAX_MATCHES);

        if (frames != null) {
            for (Iterator j = frames.iterator(); j.hasNext();) {
                Frame f = (Frame) j.next();
                if (isValidFrameToSearch(f)) {
                    RDFResource res = (RDFResource) f;
                    FindResult item = FindResult.createFindResult(res, searchProp, searchStr);
                    if (item != null) {
                        results.put(res, item);
                    }
                }
            }
        }

        return results;
    }


    /**
     * Only look at classes, properties and individuals
     *
     * @param f a frame
     * @return true if the given frame should be included in the results
     */
    protected boolean isValidFrameToSearch(Frame f) {
        Class fclass = f.getClass();
        return (OWLNamedClass.class.isAssignableFrom(fclass)) ||
               (OWLProperty.class.isAssignableFrom(fclass)) ||
               (OWLIndividual.class.isAssignableFrom(fclass));
    }


    /////////////////////////// ListModel interface implementation

    public int getSize() {
        return results.size();
    }


    public Object getElementAt(int index) {
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
        return results.size();
    }


    public int getColumnCount() {
        return FindResult.NUM_COLUMNS;
    }


    public String getColumnName(int column) {
        return FindResult.getColumnName(column);
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        Object key = orderedKeys.get(rowIndex);
        FindResult item = (FindResult) results.get(key);
        return item.get(columnIndex);
    }


    private void fireDataChanged() {
        orderedKeys = new LinkedList(results.keySet());
        Collections.sort(orderedKeys);

        ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED,
                                            0, results.size());
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ((ListDataListener) i.next()).contentsChanged(e);
        }
        this.fireTableDataChanged();
    }

    public int getSearchType() {
        return searchType;
    }
}
