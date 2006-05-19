
package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;

public class JessAssertedIndividualsPanel extends JPanel 
{
  private SWRLJessBridge bridge;
  private AssertedIndividualsModel assertedIndividualsModel;
  private JTable table;

  public JessAssertedIndividualsPanel(SWRLJessBridge bridge) 
  {
    this.bridge = bridge;

    assertedIndividualsModel = new AssertedIndividualsModel();
    table = new JTable(assertedIndividualsModel);

    setLayout(new BorderLayout());

    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());

    add(BorderLayout.CENTER, scrollPane);

  } // JessAssertedIndividualsPanel

  private class AssertedIndividualsModel extends AbstractTableModel
  {
    public int getRowCount() { return bridge.getNumberOfAssertedIndividuals(); }
    public int getColumnCount() { return 1; }
    public String getColumnName(int column) { return "Jess Individual Assertions"; }

    public Object getValueAt(int row, int column) 
    { 
      Object result = null;

      try { result =  bridge.getAssertedIndividualDisplayRepresentation(row); }
      catch (SWRLRuleEngineBridgeException e) { result = e.getMessage(); }

      return result;
    } // getValueAt
    
  } // AssertedIndividualsModel
} // JessAssertedIndividualsPanel
