
package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;

public class JessImportedClassesPanel extends JPanel 
{
  private SWRLJessBridge bridge;
  private ImportedClassesModel importedClassesModel;
  private JTable table;

  public JessImportedClassesPanel(SWRLJessBridge bridge) 
  {
    this.bridge = bridge;

    importedClassesModel = new ImportedClassesModel();
    table = new JTable(importedClassesModel);

    setLayout(new BorderLayout());

    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());

    add(BorderLayout.CENTER, scrollPane);

  } // JessImportedClassesPanel

  private class ImportedClassesModel extends AbstractTableModel
  {
    public int getRowCount() { return bridge.getNumberOfImportedClasses(); }
    public int getColumnCount() { return 1; }
    public String getColumnName(int column) { return "Jess Class Definitions"; }

    public Object getValueAt(int row, int column) 
    { 
      Object result = null;

      try { result =  bridge.getImportedClassDisplayRepresentation(row); }
      catch (SWRLRuleEngineBridgeException e) { result = e.getMessage(); }

      return result;
    } // getValueAt
  } // ImportedClassesModel

} // JessImportedClassesPanel
