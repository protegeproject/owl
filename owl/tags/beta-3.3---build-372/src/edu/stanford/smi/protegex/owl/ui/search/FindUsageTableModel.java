package edu.stanford.smi.protegex.owl.ui.search;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * A TableModel displaying the results of a "find usage" run.
 * This has two columns - one for the anonymous class, and one for the named host class.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FindUsageTableModel extends AbstractTableModel
        implements Disposable, FindUsageTableModelColumns {

    private List items;

    private OWLModel owlModel;

    private ModelListener listener = new ModelAdapter() {
        public void classDeleted(RDFSClass cls) {
            deleteItemsWith(cls);
        }


        public void individualDeleted(RDFResource resource) {
            deleteItemsWith(resource);
        }


        public void propertyDeleted(RDFProperty property) {
            deleteItemsWith(property);
        }
    };

    private int sortColumn = COL_HOST;


    public FindUsageTableModel(OWLModel owlModel, Collection items) {
        this.items = new ArrayList(items);
        this.owlModel = owlModel;
        sort();
        owlModel.addModelListener(listener);
    }


    private void deleteItemsWith(Frame cls) {
        for (int i = items.size() - 1; i >= 0; i--) {
            FindUsageTableItem item = getItem(i);
            if (item.contains(cls)) {
                items.remove(i);
                fireTableRowsDeleted(i, i);
            }
        }
    }


    public void dispose() {
        owlModel.removeModelListener(listener);
    }


    public Class getColumnClass(int column) {
        if (column == COL_HOST) {
            return RDFResource.class;
        }
        else if (column == COL_USAGE) {
            return RDFResource.class;
        }
        else if (column == COL_TYPE) {
            return Icon.class;
        }
        return null;
    }


    public int getColumnCount() {
        return COL_COUNT;
    }


    public String getColumnName(int column) {
        String str = "";
        if (column == COL_HOST) {
            str = "Resource";
        }
        else if (column == COL_USAGE) {
            str = "Expression";
        }
        else if (column == COL_TYPE) {
            str = "Type";
        }
        if (str != null && column == sortColumn) {
            str = "[" + str + "]";
        }
        return str;
    }


    RDFResource getHost(int row) {
        return getItem(row).host;
    }


    private FindUsageTableItem getItem(int row) {
        return (FindUsageTableItem) items.get(row);
    }


    public int getRowCount() {
        return items.size();
    }


    public RDFResource getUsage(int rowIndex) {
        return (RDFResource) getValueAt(rowIndex, COL_USAGE);
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == COL_USAGE) {
            return getItem(rowIndex).usage;
        }
        else if (columnIndex == COL_HOST) {
            return getHost(rowIndex);
        }
        else if (columnIndex == COL_TYPE) {
            return getItem(rowIndex).getIcon();
        }
        return null;
    }


    public void setItems(Collection items) {
        this.items = new ArrayList(items);
        fireTableDataChanged();
    }


    public void setSortColumn(int column) {
        sortColumn = column;
        sort();
        fireTableStructureChanged();
    }


    private void sort() {
        Collections.sort(items, new Comparator() {
            public int compare(Object o1, Object o2) {
                FindUsageTableItem item1 = (FindUsageTableItem) o1;
                FindUsageTableItem item2 = (FindUsageTableItem) o2;
                if (sortColumn == COL_HOST) {
                    return item1.host.getBrowserText().compareTo(item2.host.getBrowserText());
                }
                else if (sortColumn == COL_TYPE) {
                    return new Integer(item1.type).compareTo(new Integer(item2.type));
                }
                else if (sortColumn == COL_USAGE) {
                    return item1.usage.getBrowserText().compareTo(item2.usage.getBrowserText());
                }
                return 0;
            }
        });
    }
}
