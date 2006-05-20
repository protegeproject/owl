
package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;

public class JessAssertedPropertiesPanel extends JPanel 
{
  private SWRLJessBridge bridge;
  private AssertedPropertiesModel assertedPropertiesModel;
  private JTable table;

  public JessAssertedPropertiesPanel(SWRLJessBridge bridge) 
  {
    this.bridge = bridge;

    assertedPropertiesModel = new AssertedPropertiesModel();
    table = new JTable(assertedPropertiesModel);

    setLayout(new BorderLayout());

    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());

    add(BorderLayout.CENTER, scrollPane);

  } // JessAssertedPropertiesPanel

  public void update() { assertedPropertiesModel.fireTableStructureChanged(); }

  private class AssertedPropertiesModel extends AbstractTableModel
  {
    public int getRowCount() { return bridge.getNumberOfAssertedProperties(); }
    public int getColumnCount() { return 1; }
    public String getColumnName(int column) { return "Jess Property Assertions"; }

    public Object getValueAt(int row, int column) 
    { 
      Object result = null;

      try { result =  bridge.getAssertedPropertyDisplayRepresentation(row); }
      catch (SWRLRuleEngineBridgeException e) { result = e.getMessage(); }

      return result;
  } // getValueAt

  } // AssertedPropertiesModel

} // JessAssertedPropertiesPanel
