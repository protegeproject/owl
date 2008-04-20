package edu.stanford.smi.protegex.owl.ui.subsumption;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * A JPanel hosting a table which displays the changed classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ChangedClassesPanel extends ResultsPanel {

    private Action assertAction = new AbstractAction("Assert selected change(s)",
            OWLIcons.getAssertChangeIcon()) {
        public void actionPerformed(ActionEvent e) {
            assertSelectedChanges();
        }
    };

    private static Map panels = new HashMap();

    private Action showAction = new AbstractAction("Show selected class in hierarchies",
            OWLIcons.getViewIcon()) {
        public void actionPerformed(ActionEvent e) {
            showSelectedCls();
        }
    };

    private ChangedClassesTableModel tableModel;

    private JTable table;

    public static final String TAB_NAME = "Classification Results";


    public ChangedClassesPanel(OWLModel owlModel) {
        super(owlModel);
        tableModel = new ChangedClassesTableModel(owlModel);
        table = new JTable(tableModel);
        table.setRowHeight(getFontMetrics(getFont()).getHeight());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(false);
        table.setRowMargin(0);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showSelectedCls();
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateActions();
            }
        });
        table.setDefaultRenderer(Frame.class, new ResourceRenderer());
        table.getColumnModel().getColumn(ChangedClassesTableModel.COL_CLS).setPreferredWidth(180);
        table.getColumnModel().getColumn(ChangedClassesTableModel.COL_TEXT).setPreferredWidth(250);
        JScrollPane scrollPane = new JScrollPane(table);
        JViewport viewPort = scrollPane.getViewport();
        viewPort.setBackground(table.getBackground());
        // addButton(showAction);
        addButton(assertAction);
        updateActions();
        setCenterComponent(scrollPane);
    }


    private void assertSelectedChanges() {
        int[] rows = table.getSelectedRows();
        tableModel.assertChanges(rows);
    }


    public boolean contains(Cls cls) {
        return tableModel.contains(cls);
    }


    public void dispose() {
        tableModel.dispose();
    }


    /**
     * Should be called to clean up any dangling (memory-expensive) references
     * to OWLModels when a project is closed.
     *
     * @param owlModel the OWLModel to delete references to
     */
    public static void dispose(OWLModel owlModel) {
        ChangedClassesPanel panel = (ChangedClassesPanel) panels.get(owlModel);
        if (panel != null) {
            panels.remove(owlModel);
            panel.dispose();
        }
    }


    public static ChangedClassesPanel get(OWLModel owlModel) {
        ChangedClassesPanel panel = (ChangedClassesPanel) panels.get(owlModel);
        if (panel == null) {
            panel = new ChangedClassesPanel(owlModel);
            panels.put(owlModel, panel);
        }
        return panel;
    }


    public int getChangeCount() {
        return tableModel.getRowCount();
    }


    public String getChangeText(Cls cls) {
        return tableModel.getChangeText(cls);
    }


    public Icon getIcon() {
        return OWLIcons.getImageIcon("ChangedClasses");
    }


    public String getTabName() {
        return TAB_NAME;
    }


    ChangedClassesTableModel getTableModel() {
        return tableModel;
    }


    public void refresh() {
        tableModel.refill();
    }


    private void showSelectedCls() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Cls cls = tableModel.getCls(row);
            if (cls instanceof RDFSClass) {
                showHostResource((RDFSClass) cls);
            }
        }
    }


    private void updateActions() {
        int[] rows = table.getSelectedRows();
        showAction.setEnabled(rows.length == 1);
        if (rows.length > 0) {
            for (int i = 0; i < rows.length; i++) {
                int row = rows[i];
                Cls cls = tableModel.getCls(row);
                if (!OWLUtil.isInconsistent(cls) && cls.isEditable()) {
                    assertAction.setEnabled(true);
                    return;
                }
            }
        }
        assertAction.setEnabled(false);
    }
}
