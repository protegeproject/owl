package edu.stanford.smi.protegex.owl.ui.components.triples;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditor;
import edu.stanford.smi.protegex.owl.ui.editors.PropertyValueEditorManager;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.metadata.AnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.metadata.DateAnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.metadata.DateTimeAnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.metadata.TimeAnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A PropertyWidget to edit the values of properties in a table.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractTriplesComponent extends AbstractPropertyValuesComponent implements Disposable {

    private Icon icon;

    private OWLModel owlModel;

    private static Set plugins = new HashSet();

    private TriplesTable table;

    private TriplesTableModel tableModel;

    private Action viewValueAction;


    public AbstractTriplesComponent(RDFProperty predicate) {
        this(predicate, "Triples", OWLIcons.getImageIcon(OWLIcons.TRIPLE));
    }


    public AbstractTriplesComponent(RDFProperty predicate, String label, Icon icon) {
        super(predicate);

        this.icon = icon;

        addPlugin(new DateAnnotationsWidgetPlugin());   // Ugly, but static {} block has class loader problem
        addPlugin(new DateTimeAnnotationsWidgetPlugin());
        addPlugin(new TimeAnnotationsWidgetPlugin());

        this.owlModel = getOWLModel();

        viewValueAction = new AbstractAction("View selected value", OWLIcons.getViewIcon()) {
            public void actionPerformed(ActionEvent e) {
                viewValue();
            }
        };
        viewValueAction.setEnabled(false);

        tableModel = createTableModel();
        table = createTable(owlModel.getProject());

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                handleSelectionChanged();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleTableDoubleClick();
                }
            }
        });

        // forces any current editing to stop if the window closes
        addContainerListener(new ContainerAdapter(){
            public void componentRemoved(ContainerEvent e) {
                stopCellEditing();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getViewport().setBackground(table.getBackground());
        LabeledComponent lc = new OWLLabeledComponent(label, scrollPane, true, true);
        if (icon != null) {
            lc.setHeaderIcon(icon);
        }
        WidgetUtilities.addViewButton(lc, viewValueAction);
        addButtons(lc);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
    }


    protected abstract void addButtons(LabeledComponent lc);


    public static void addPlugin(AnnotationsWidgetPlugin plugin) {
        for (Iterator it = plugins.iterator(); it.hasNext();) {
            AnnotationsWidgetPlugin p = (AnnotationsWidgetPlugin) it.next();
            if (p.getClass() == plugin.getClass()) {
                return;
            }
        }
        plugins.add(plugin);
    }


    protected TriplesTable createTable(Project project) {
        return new TriplesTable(project, tableModel, "property");
    }


    protected TriplesTableModel createTableModel() {
        return new TriplesTableModel(null);
    }


    public void dispose() {
        tableModel.dispose();
    }


    public Icon getIcon() {
        return icon;
    }


    public TriplesTable getTable() {
        return table;
    }


    public TriplesTableModel getTableModel() {
        return tableModel;
    }


    protected void handleSelectionChanged() {
        updateActions();
    }


    private void handleTableDoubleClick() {
        int selIndex = table.getSelectedRow();
        if (selIndex >= 0 && selIndex < tableModel.getRowCount() && viewValueAction.isEnabled()) {
            viewValue();
        }
    }


    public static Iterator plugins() {
        return plugins.iterator();
    }


    public static void removePlugin(AnnotationsWidgetPlugin plugin) {
        plugins.remove(plugin);
    }


    public void setSubject(RDFResource subject) {
        super.setSubject(subject);
        if(table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        tableModel.setSubject(subject);
    }


    protected void updateActions() {
        final int row = table.getSelectedRow();
        RDFProperty property = row >= 0 ? tableModel.getPredicate(row) : null;
        if (row >= 0) {
            Object value = tableModel.getValue(row);
            viewValueAction.setEnabled(true);
        }
        else {
            viewValueAction.setEnabled(false);
        }
    }


    public void valuesChanged() {
        tableModel.updateValues();
    }


    private void viewValue() {
        final int row = table.getSelectedRow();
        viewValue(row);
    }


    private void viewValue(final int row) {
        Object value = tableModel.getValue(row);
        if (value instanceof RDFResource) {
            ProtegeUI.show((RDFResource) value);
        }
        else {
            RDFProperty property = tableModel.getPredicate(row);
            RDFResource subject = tableModel.getSubject();
            PropertyValueEditor editor = PropertyValueEditorManager.getEditor(subject, property, value);
            if(editor != null) {
                Object newValue = editor.editValue(this, subject, property, value);
                if(newValue != null) {
                    tableModel.setValue(newValue, row);
                    return;
                }
                else {
                    return;
                }
            }

            for (Iterator it = plugins.iterator(); it.hasNext();) {
                AnnotationsWidgetPlugin plugin = (AnnotationsWidgetPlugin) it.next();
                if (plugin.canEdit(subject, property, value)) {
                    Object newValue = plugin.editValue(null, subject, property, value);
                    if (newValue != null) {
                        tableModel.setValue(newValue, row);
                    }
                    return;
                }
            }
            String name = property.getBrowserText();
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            JTextArea textArea = new JTextArea(value.toString());
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEnabled(tableModel.isCellEditable(row, TriplesTableModel.COL_VALUE));
            Component comp = new JScrollPane(textArea);
            LabeledComponent lc = new LabeledComponent(name, comp);
            lc.setPreferredSize(new Dimension(400, 400));
            int r = ProtegeUI.getModalDialogFactory().showDialog(this, lc, "Edit annotation", ModalDialogFactory.MODE_OK_CANCEL);
            if (r == ModalDialogFactory.OPTION_OK) {
                String newValue = textArea.getText();
                tableModel.setValue(newValue, row);
            }
        }
    }

    private boolean stopCellEditing() {
        try {
            int column = table.getEditingColumn();
            if (column > -1) {
                TableCellEditor cellEditor = table.getColumnModel().getColumn(column).getCellEditor();
                if (cellEditor == null) {
                    cellEditor = table.getDefaultEditor(table.getColumnClass(column));
                }
                if (cellEditor != null) {
                    cellEditor.stopCellEditing();
                }
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
}
