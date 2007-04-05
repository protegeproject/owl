package edu.stanford.smi.protegex.owl.ui.metadatatab.prefixes;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.NamespaceManagerListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.OWLNamespaceManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * A TableModel for editing prefixes in an OWLModel.
 * The resulting table has two columns, one for prefixes and one for the namespace.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PrefixesTableModel extends AbstractTableModel implements Disposable {


    private NamespaceManagerListener listener = new NamespaceManagerListener() {

        public void defaultNamespaceChanged(String oldValue, String newValue) {
            // Do nothing
        }


        public void namespaceChanged(String prefix, String oldValue, String newValue) {
            int row = getPrefixRow(prefix);
            fireTableCellUpdated(row, COL_NAMESPACE);
        }


        public void prefixAdded(String prefix) {
            int index = prefixes.size();
            prefixes.add(prefix);
            fireTableRowsInserted(index, index);
        }


        public void prefixChanged(String namespace, String oldPrefix, String newPrefix) {
            int row = getPrefixRow(oldPrefix);
            if (row >= 0) {
                prefixes.remove(row);
                prefixes.add(row, newPrefix);
                fireTableCellUpdated(row, COL_PREFIX);
            }
        }


        public void prefixRemoved(String prefix) {
            int row = getPrefixRow(prefix);
            if (row >= 0) {
                prefixes.remove(row);
                fireTableRowsDeleted(row, row);
            }
        }
    };

    private OWLOntology ontology;

    public final static int COL_PREFIX = 0;

    public final static int COL_NAMESPACE = 1;

    public final static int COL_COUNT = 2;

    /**
     * A list of prefixes (one String value for each row)
     */
    private List prefixes = new ArrayList();


    public PrefixesTableModel(OWLOntology ontology) {
        this.ontology = ontology;
        getNamespaceManager().addNamespaceManagerListener(listener);
        fill();
    }


    public void dispose() {
        getNamespaceManager().removeNamespaceManagerListener(listener);
    }


    private void fill() {
        prefixes = new ArrayList(getNamespaceManager().getPrefixes());
    }


    public int getColumnCount() {
        return COL_COUNT;
    }


    public Class getColumnClass(int columnIndex) {
        return String.class;
    }


    public String getColumnName(int column) {
        if (column == COL_PREFIX) {
            return "Prefix";
        }
        else if (column == COL_NAMESPACE) {
            return "Namespace";
        }
        else {
            return null;
        }
    }


    public String getNamespace(int rowIndex) {
        String prefix = getPrefix(rowIndex);
        return getNamespaceManager().getNamespaceForPrefix(prefix);
    }


    NamespaceManager getNamespaceManager() {
        return ontology.getOWLModel().getNamespaceManager();
    }


    public String getPrefix(int row) {
        return (String) prefixes.get(row);
    }


    int getPrefixRow(String prefix) {
        return prefixes.indexOf(prefix);
    }


    public int getRowCount() {
        return prefixes.size();
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == COL_PREFIX) {
            return getPrefix(rowIndex);
        }
        else if (columnIndex == COL_NAMESPACE) {
            return getNamespace(rowIndex);
        }
        else {
            return null;
        }
    }


    public boolean isCellEditable(int rowIndex, int columnIndex) {
        String prefix = getPrefix(rowIndex);
        return isPrefixEditable(prefix);
    }


    public boolean isNamespaceEditable(String namespace) {
        String prefix = getNamespaceManager().getPrefix(namespace);
        return isPrefixEditable(prefix);
    }


    boolean isPrefixEditable(String prefix) {
        return getNamespaceManager().isModifiable(prefix);
    }


    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        NamespaceManager nsm = getNamespaceManager();
        if (columnIndex == COL_PREFIX) {
            String value = (String) aValue;
            if (nsm.getNamespaceForPrefix(value) == null) {
                if (OWLNamespaceManager.isValidPrefix(value)) {
                    String namespace = getNamespace(rowIndex);
                    setPrefixOfNamespace(namespace, value);
                }
            }
        }
        else if (columnIndex == COL_NAMESPACE) {
            String value = (String) aValue;
            if (nsm.getPrefix(value) == null && !nsm.getDefaultNamespace().equals(value)) {
                if (Jena.isNamespaceWithSeparator(value)) {
                    String prefix = getPrefix(rowIndex);
                    setNamespaceOfPrefix(prefix, value);
                }
            }
        }
    }


    private void setNamespaceOfPrefix(String prefix, String value) {
        OWLModel owlModel = ontology.getOWLModel();
        try {
            owlModel.beginTransaction("Change namespace of " + prefix + " to " + value);
            owlModel.getNamespaceManager().setPrefix(value, prefix);
        }
        catch (Exception ex) {
            OWLUI.handleError(owlModel, ex);
        }
        finally {
            owlModel.endTransaction();
        }
    }


    private void setPrefixOfNamespace(String namespace, String value) {
        OWLModel owlModel = ontology.getOWLModel();
        try {
            owlModel.beginTransaction("Change prefix of " + namespace + " to " + value);
            owlModel.getNamespaceManager().setPrefix(namespace, value);
        }
        catch (Exception ex) {
            OWLUI.handleError(owlModel, ex);
        }
        finally {
            owlModel.endTransaction();
        }
    }
}
