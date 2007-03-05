
package edu.stanford.smi.protegex.owl.swrl.bridge.query.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.query.SWRLBuiltInLibraryImpl;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;

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
  private SWRLQueryResultModel swrlQueryResultModel;
  private JTable table;

  public SWRLQueryResultPanel(SWRLRuleEngineBridge bridge, String ruleName) 
  {
    this.ruleName = ruleName;
    this.bridge = bridge;
    
    swrlQueryResultModel = new SWRLQueryResultModel();
    table = new JTable(swrlQueryResultModel);
    
    setLayout(new BorderLayout());
    
    JPanel buttonsPanel = new JPanel(new FlowLayout());
    
    JButton runRuleButton = createButton("Run rule", "Execute the SWRL rule", new RunRuleActionListener());
    buttonsPanel.add(runRuleButton);
    
    JButton closeTabButton = createButton("Close", "Close the tab for this rule", new CloseTabActionListener());
    buttonsPanel.add(closeTabButton);
    
    JButton saveRuleButton = createButton("Save...", "Save the result", new SaveRuleActionListener());
    buttonsPanel.add(saveRuleButton);
    
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
        System.err.println("Exception rerunning bridge: " + e.getMessage());
      } // try
      validate();
    } // ActionPerformed
  } // RunRuleActionListener
  
  private class CloseTabActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event) 
    {
    } // ActionPerformed
  } // CloseTabActionListener
  
  private class SaveRuleActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event) 
    {
    } // ActionPerformed
  } // SaveRuleActionListener
  
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
        count = result.getNumberOfRows(); 
      } catch (ResultException e) {
        System.err.println("Exception getting row count: " + e);
      } // try

      return count;
    } // getRowCount
    
    public int getColumnCount() 
    {
      Result result = null;
      int count = 0;
      
      try {
        result = bridge.getQueryResult(ruleName);
        count = result.getNumberOfColumns(); 
      } catch (ResultException e) {
        System.err.println("Exception getting column count: " + e);
      } // try

      return count;
    } // getColumnCount
    
    public String getColumnName(int columnIndex) 
    {
      Result result = null;
      String columnName = null;
      
      try {
        result = bridge.getQueryResult(ruleName);
        columnName = result.getColumnName(columnIndex); 
      } catch (ResultException e) {
        System.err.println("Exception getting column name: " + e);
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
        value =  result.getValue(column, row);
      } catch (ResultException e) { 
        System.err.println("Exception getting value: " + e);
        value = e.getMessage(); 
      } // try
      
      return value;
    } // getValueAt
    
  } // SWRLQueryResultModel

} // SWRLQueryResultPanel
