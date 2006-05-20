
package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;

public class JessImportedPropertiesPanel extends JPanel 
{
  private SWRLJessBridge bridge;
  private ImportedPropertiesModel importedPropertiesModel;
  private JTable table;

  public JessImportedPropertiesPanel(SWRLJessBridge bridge) 
  {
    this.bridge = bridge;

    importedPropertiesModel = new ImportedPropertiesModel();
    table = new JTable(importedPropertiesModel);

    setLayout(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());

    add(BorderLayout.CENTER, scrollPane);
  } // JessImportedPropertiesPanel

  private class ImportedPropertiesModel extends AbstractTableModel
  {
    public int getRowCount() { return bridge.getNumberOfImportedProperties(); }
    public int getColumnCount() { return 1; }
    public String getColumnName(int column) { return "Jess Property Definitions"; }

    public Object getValueAt(int row, int column) 
    { 
      Object result = null;

      try { result =  bridge.getImportedPropertyDisplayRepresentation(row); }
      catch (SWRLRuleEngineBridgeException e) { result = e.getMessage(); }

      return result;
    } // getValueAt
  } // ImportedPropertiesModel

} // JessImportedPropertiesPanel
