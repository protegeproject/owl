
package edu.stanford.smi.protegex.owl.swrl.bridge.query.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.query.SWRLBuiltInLibraryImpl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SWRLQueryResultPanel extends JPanel 
{
  private String ruleName;
  private SWRLRuleEngineBridge bridge;
  private SWRLQueryControlPanel controlPanel;
  private SWRLQueryResultModel swrlQueryResultModel;
  private JTable table;
  private static File currentDirectory = null;

  public SWRLQueryResultPanel(SWRLRuleEngineBridge bridge, SWRLQueryControlPanel controlPanel, String ruleName) 
  {
    this.ruleName = ruleName;
    this.controlPanel = controlPanel;
    this.bridge = bridge;
    
    swrlQueryResultModel = new SWRLQueryResultModel();
    table = new JTable(swrlQueryResultModel);
    
    setLayout(new BorderLayout());
    
    JPanel buttonsPanel = new JPanel(new FlowLayout());
    
    JButton saveResultButton = createButton("Save as CSV...", "Save the result as a CSV file...", new SaveResultActionListener());
    buttonsPanel.add(saveResultButton);

    JButton runRulesButton = createButton("Rerun", "Rerun all the SWRL rules", new RunRulesActionListener());
    buttonsPanel.add(runRulesButton);
    
    JButton closeTabButton = createButton("Close", "Close the tab for this rule", new CloseTabActionListener());
    buttonsPanel.add(closeTabButton);
        
    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());
    
    add(BorderLayout.CENTER, scrollPane);
    add(BorderLayout.SOUTH, buttonsPanel);
  } // SWRLQueryResultPanel
  
  public void validate() { swrlQueryResultModel.fireTableStructureChanged(); super.validate(); }
  
  private class RunRulesActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event) 
    {
      Result result;

      try {
        bridge.resetBridge();
        bridge.importSWRLRulesAndOWLKnowledge();
        bridge.exportSWRLRulesAndOWLKnowledge();
        bridge.runRuleEngine();

        result = bridge.getQueryResult(ruleName);

        if (result == null || result.getNumberOfRows() == 0) {
          controlPanel.appendText("No result returned for rule '" + ruleName + "' - closing tab.\n");
          controlPanel.removeResultPanel(ruleName);
        } else validate();
      } catch (SWRLRuleEngineBridgeException e) {
        controlPanel.appendText("All results panels closed - exception running rules: " + e.getMessage() + "\n");
        controlPanel.removeAllPanels();
      } // try
    } // ActionPerformed
  } // RunRulesActionListener
  
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
    private JFileChooser chooser;

    public SaveResultActionListener()
    {
      chooser = new JFileChooser();
      chooser.setCurrentDirectory(null);
    } // SaveResultActionListener

    public void actionPerformed(ActionEvent event) 
    {
      saveResults();
    } // ActionPerformed

    private void saveResults() 
    {
      int returnValue = chooser.showOpenDialog(controlPanel);
      File selectedFile = null;
      FileWriter writer = null;
      Result result = null;
      int numberOfColumns;
      
      try {
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          selectedFile = chooser.getSelectedFile();
          currentDirectory = chooser.getCurrentDirectory();
          writer = new FileWriter(selectedFile);
          result = bridge.getQueryResult(ruleName);

          if (result != null) {
            numberOfColumns = result.getNumberOfColumns();
            
            for (int i = 0; i < numberOfColumns; i++) {
              if (i != 0) writer.write(", ");
              writer.write(result.getColumnName(i));
            } // for
            writer.write("\n");
            
            while (result.hasNext()) {
              for (int i = 0; i < numberOfColumns; i++) {
                ResultValue value = result.getValue(i);
                if (i != 0) writer.write(", ");
                if (value instanceof DatatypeValue && ((DatatypeValue)value).isString()) writer.write("\"" + value + "\"");
                else writer.write("" + value);
              } // for
              writer.write("\n");
              result.next();
            } // while
            result.reset();
            writer.close();
            controlPanel.appendText("Sucessfully saved results of rule '" + ruleName + "' to CSV file '" + selectedFile.getPath() + "'.\n");
          } // if
        } // if
      } catch (Throwable e) {
        JOptionPane.showMessageDialog(null, "Error saving file '" + selectedFile.getPath() + "': " + e.getMessage(), "Error saving file",
                                      JOptionPane.ERROR_MESSAGE);
        // TODO: findbugs - stream not closed on all paths
      } // try
    } // saveResults
    
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
