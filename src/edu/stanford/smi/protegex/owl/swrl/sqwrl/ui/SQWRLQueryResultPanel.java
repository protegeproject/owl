
package edu.stanford.smi.protegex.owl.swrl.sqwrl.ui;

import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SQWRLQueryResultPanel extends JPanel 
{
  private String queryName;
  private JTable table;
  private SQWRLQueryEngine queryEngine;
  private SQWRLResult result = null;
  private SQWRLQueryControlPanel controlPanel;
  private SQWRLQueryResultModel swrlQueryResultModel;
  private static File currentDirectory = null;

  public SQWRLQueryResultPanel(SQWRLQueryEngine queryEngine, String queryName, SQWRLResult result, SQWRLQueryControlPanel controlPanel) 
  {
    this.queryEngine = queryEngine;
    this.queryName = queryName;
    this.result = result;
    this.controlPanel = controlPanel;
    
    swrlQueryResultModel = new SQWRLQueryResultModel();
    table = new JTable(swrlQueryResultModel);
    
    setLayout(new BorderLayout());
    
    JPanel buttonsPanel = new JPanel(new FlowLayout());
    
    JButton saveResultButton = createButton("Save as CSV...", "Save the result as a CSV file...", new SaveResultActionListener());
    buttonsPanel.add(saveResultButton);

    JButton runQueriesButton = createButton("Rerun", "Rerun all the SQWRL queries", new RunQueriesActionListener());
    buttonsPanel.add(runQueriesButton);
    
    JButton closeTabButton = createButton("Close", "Close the tab for this query", new CloseTabActionListener());
    buttonsPanel.add(closeTabButton);
        
    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());
    
    add(BorderLayout.CENTER, scrollPane);
    add(BorderLayout.SOUTH, buttonsPanel);
  } // SQWRLQueryResultPanel
  
  public void validate() { swrlQueryResultModel.fireTableStructureChanged(); super.validate(); }
  
  private class RunQueriesActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event) 
    {
      result = null;

      try {
        queryEngine.runSQWRLQueries();

        result = queryEngine.getSQWRLResult(queryName);

        if (result == null || result.getNumberOfRows() == 0) {
          controlPanel.appendText("No result returned for SQWRL query '" + queryName + "' - closing tab.\n");
          controlPanel.removeResultPanel(queryName);
        } else validate();
      } catch (InvalidQueryNameException e) {
        controlPanel.appendText("Invalid query name '" + queryName + "'.\n");
      } catch (SQWRLException e) {
        controlPanel.appendText("Exception running SQWRL queries: " + e.getMessage() + "\n");
      } // try
     
      if (result == null) {
        controlPanel.removeAllPanels();
        controlPanel.appendText("Closing all result tabs.\n");
      } // if
    }
  } // RunQueriesActionListener
  
  private class CloseTabActionListener implements ActionListener
  {
    public void actionPerformed(ActionEvent event) 
    {
      controlPanel.removeResultPanel(queryName);
      controlPanel.appendText("'" + queryName + "' tab closed.\n");
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
      int numberOfColumns;
      
      try {
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          selectedFile = chooser.getSelectedFile();
          currentDirectory = chooser.getCurrentDirectory();
          writer = new FileWriter(selectedFile);
          result = queryEngine.getSQWRLResult(queryName);

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
            controlPanel.appendText("Sucessfully saved results of query '" + queryName + "' to CSV file '" + selectedFile.getPath() + "'.\n");
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

  private class SQWRLQueryResultModel extends AbstractTableModel
  {
    public int getRowCount() 
    { 
      int count = 0;
      
      try {
        count = (result == null) ? 0 : result.getNumberOfRows(); 
      } catch (SQWRLException e) {}

      return count;
    } // getRowCount
    
    public int getColumnCount() 
    {
      int count = 0;
      
      try {
        count = (result == null) ? 0 : result.getNumberOfColumns(); 
      } catch (SQWRLException e) {}

      return count;
    } // getColumnCount
    
    public String getColumnName(int columnIndex) 
    {
      String columnName = "";

      try {
        columnName = (result == null) ? "" : result.getColumnName(columnIndex); 
      } catch (SQWRLException e) {}
      
      return columnName;
    } // getColumnName

    public Object getValueAt(int row, int column) 
    { 
      Object value = null;
      
      try { 
        value = (result == null) ? null : result.getValue(column, row);
      } catch (SQWRLException e) {}

      return value;
    } // getValueAt
    
  } // SQWRLQueryResultModel

} // SQWRLQueryResultPanel
