
package edu.stanford.smi.protegex.owl.swrl.bridge.query.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.query.SWRLBuiltInLibraryImpl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SWRLQueryResultPanel extends JPanel 
{
  private String ruleName;
  private SWRLRuleEngineBridge bridge;
  private SWRLQueryControlPanel controlPanel;
  private SWRLQueryResultModel swrlQueryResultModel;
  private JTable table;

  public SWRLQueryResultPanel(SWRLRuleEngineBridge bridge, SWRLQueryControlPanel controlPanel, String ruleName) 
  {
    this.ruleName = ruleName;
    this.controlPanel = controlPanel;
    this.bridge = bridge;
    
    swrlQueryResultModel = new SWRLQueryResultModel();
    table = new JTable(swrlQueryResultModel);
    
    setLayout(new BorderLayout());
    
    JPanel buttonsPanel = new JPanel(new FlowLayout());
    
    JButton runRuleButton = createButton("Run rule", "Execute the SWRL rule", new RunRuleActionListener());
    buttonsPanel.add(runRuleButton);
    
    JButton closeTabButton = createButton("Close", "Close the tab for this rule", new CloseTabActionListener());
    buttonsPanel.add(closeTabButton);
    
    JButton saveResultButton = createButton("Save...", "Save the result", new SaveResultActionListener());
    buttonsPanel.add(saveResultButton);
    
    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());
    
    add(BorderLayout.CENTER, scrollPane);
    add(BorderLayout.SOUTH, buttonsPanel);
  } // SWRLQueryResultPanel
  
  public void validate() { swrlQueryResultModel.fireTableStructureChanged(); super.validate(); }
  
  private class RunRuleActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event) 
    {
      try {
        bridge.resetBridge();
        bridge.importSWRLRulesAndOWLKnowledge();
        bridge.exportSWRLRulesAndOWLKnowledge();
        bridge.runRuleEngine();
      } catch (SWRLRuleEngineBridgeException e) {
        controlPanel.appendText("Exception running rules: " + e.getMessage() + "\n");
      } // try
      validate();
    } // ActionPerformed
  } // RunRuleActionListener
  
  private class CloseTabActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event) 
    {
      controlPanel.removeResultPanel(ruleName);
      controlPanel.appendText("'" + ruleName + "' tab closed.\n");
    } // ActionPerformed
  } // CloseTabActionListener
  
  private class SaveResultActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event) 
    {
    } // ActionPerformed
  } // SaveResultActionListener
  
  private JButton createButton(String text, String toolTipText, ActionListener listener)
  {
    JButton button = new JButton(text);
    button.setToolTipText(toolTipText);
    button.setPreferredSize(new Dimension(160, 30));
    button.addActionListener(listener);

    return button;
  } // createButton

  private class SWRLQueryResultModel extends AbstractTableModel
  {
    public int getRowCount() 
    { 
      Result result = null;
      int count = 0;
      
      try {
        result = bridge.getQueryResult(ruleName);
        count = (result == null) ? 0 : result.getNumberOfRows(); 
      } catch (ResultException e) {
        controlPanel.appendText("Exception getting row count in model: " + e + "\n");
      } // try

      return count;
    } // getRowCount
    
    public int getColumnCount() 
    {
      Result result = null;
      int count = 0;
      
      try {
        result = bridge.getQueryResult(ruleName);
        count = (result == null) ? 0 : result.getNumberOfColumns(); 
      } catch (ResultException e) {
        controlPanel.appendText("Exception getting column count in model: " + e + "\n");
      } // try

      return count;
    } // getColumnCount
    
    public String getColumnName(int columnIndex) 
    {
      Result result = null;
      String columnName = null;
      
      try {
        result = bridge.getQueryResult(ruleName);
        columnName = (result == null) ? "" : result.getColumnName(columnIndex); 
      } catch (ResultException e) {
        controlPanel.appendText("Exception getting column name in model: " + e + "\n");
        columnName = "Exception: " + e.getMessage();
      } // try
      return columnName;
    } // getColumnName

    public Object getValueAt(int row, int column) 
    { 
      Result result = null;
      Object value = null;
      
      try { 
        result = bridge.getQueryResult(ruleName);
        value = (result == null) ? null : result.getValue(column, row);
      } catch (ResultException e) { 
        controlPanel.appendText("Exception getting value in model: " + e + "\n");
        value = e.getMessage(); 
      } // try
      
      return value;
    } // getValueAt
    
  } // SWRLQueryResultModel

} // SWRLQueryResultPanel
