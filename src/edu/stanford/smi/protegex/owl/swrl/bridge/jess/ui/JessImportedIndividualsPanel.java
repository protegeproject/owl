
package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;

public class JessImportedIndividualsPanel extends JPanel 
{
  private SWRLJessBridge bridge;
  private ImportedIndividualsModel importedIndividualsModel;
  private JTable table;

  public JessImportedIndividualsPanel(SWRLJessBridge bridge) 
  {
    this.bridge = bridge;

    importedIndividualsModel = new ImportedIndividualsModel();
    table = new JTable(importedIndividualsModel);

    setLayout(new BorderLayout());
    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());

    add(BorderLayout.CENTER, scrollPane);

  } // JessImportedIndividualsPanel

  private class ImportedIndividualsModel extends AbstractTableModel
  {
    public int getRowCount() { return bridge.getNumberOfImportedIndividuals(); }
    public int getColumnCount() { return 1; }
    public String getColumnName(int column) { return "Jess Individual Definitions"; }

    public Object getValueAt(int row, int column) 
    { 
      Object result = null;

      try { result =  bridge.getImportedIndividualDisplayRepresentation(row); }
      catch (SWRLRuleEngineBridgeException e) { result = e.getMessage(); }

      return result;
    } // getValueAt

  } // ImportedIndividualsModel

} // JessImportedIndividualsPanel
