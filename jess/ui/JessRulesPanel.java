
package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;

public class JessRulesPanel extends JPanel 
{
  private SWRLJessBridge bridge;
  private RulesModel rulesModel;
  private JTable table;

  public JessRulesPanel(SWRLJessBridge bridge) 
  {
    this.bridge = bridge;

    rulesModel = new RulesModel();
    table = new JTable(rulesModel);

    setLayout(new BorderLayout());

    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());

    add(BorderLayout.CENTER, scrollPane);

  } // JessRulesPanel

  private class RulesModel extends AbstractTableModel
  {
    public int getRowCount() { return bridge.getNumberOfImportedSWRLRules(); }
    public int getColumnCount() { return 1; }
    public String getColumnName(int column) { return "Jess Rules"; }

    public Object getValueAt(int row, int column) 
    { 
      Object result = null;

      try { result =  bridge.getImportedRuleDisplayRepresentation(row); }
      catch (SWRLRuleEngineBridgeException e) { result = e.getMessage(); }

      return result;
    } // getValueAt

  } // RulesModel

} // JessRulesPanel
