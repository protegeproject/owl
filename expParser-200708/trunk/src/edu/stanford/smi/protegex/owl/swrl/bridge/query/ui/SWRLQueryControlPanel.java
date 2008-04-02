
package edu.stanford.smi.protegex.owl.swrl.bridge.query.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.query.SWRLBuiltInLibraryImpl;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class SWRLQueryControlPanel extends JPanel 
{
  private SWRLRuleEngineBridge bridge;
  private HashMap<String, SWRLQueryResultPanel> resultPanels;
  private JTextArea textArea;
  private static int MaximumOpenResultPanels = 8;

  public SWRLQueryControlPanel(SWRLRuleEngineBridge bridge) 
  {
    JPanel panel;
    JButton button;

    this.bridge = bridge;

    resultPanels = new HashMap<String, SWRLQueryResultPanel>();

    setLayout(new BorderLayout());

    textArea = createTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(900, 300));

    add(BorderLayout.CENTER, scrollPane);
    
    panel = new JPanel(new FlowLayout());

    button = createButton("Run Rules", "Run all SWRL rules", new RunRuleActionListener(textArea, this));
    panel.add(button);

    add(BorderLayout.SOUTH, panel);

    textArea.append("SWRLQueryTab\n\n");
    textArea.append("See http://protege.cim3.net/cgi-bin/wiki.pl?SWRLQueryTab for documentation.\n\n");
    textArea.append("Executing rules in this tab does not modify the ontology.\n\n");
    textArea.append("Select a rule with query built-ins from the list above and press the 'Run Rules' button.\n");
    textArea.append("If the selected rule generates a result, the result will appear in a new tab.\n\n");
  } // SWRLQueryControlPanel

  public void appendText(String text)
  {
    textArea.append(text);
  } // appendText

  public void removeResultPanel(String ruleName)
  {
    if (resultPanels.containsKey(ruleName)) {
      SWRLQueryResultPanel resultPanel = resultPanels.get(ruleName);
      resultPanels.remove(ruleName);
      ((JTabbedPane)getParent()).remove(resultPanel);
      ((JTabbedPane)getParent()).setSelectedIndex(0);
    } // if
  } // if

  public void removeAllPanels()
  {
    for (SWRLQueryResultPanel resultPanel : resultPanels.values()) ((JTabbedPane)getParent()).remove(resultPanel);
    resultPanels = new HashMap<String, SWRLQueryResultPanel>();
  } // if

  private JButton createButton(String text, String toolTipText, ActionListener listener)
  {
    JButton button = new JButton(text);

    button.setToolTipText(toolTipText);
    button.setPreferredSize(new Dimension(160, 30));
    button.addActionListener(listener);

    return button;
  } // createButton

  private JLabel createLabel(String text)
  {
    JLabel label = new JLabel(text);

    label.setPreferredSize(new Dimension(160, 30));

    return label;
  } // createLabel

  private JTextArea createTextArea()
  {
    JTextArea textArea = new JTextArea(10, 80);
    textArea.setLineWrap(true);
    textArea.setBackground(Color.WHITE);
    textArea.setEditable(false);
    return textArea;
  } // createTextArea

  private class ListenerBase
  {
    protected JTextArea textArea;
    protected SWRLQueryControlPanel controlPanel;

    public ListenerBase(JTextArea textArea, SWRLQueryControlPanel controlPanel)
    {
      this.textArea = textArea;
      this.controlPanel = controlPanel;
    } // ListenerBase
  } // ListenerBase

  private class RunRuleActionListener extends ListenerBase implements ActionListener
  {
    public RunRuleActionListener(JTextArea textArea, SWRLQueryControlPanel controlPanel) 
    { super(textArea, controlPanel); }
    
    public void actionPerformed(ActionEvent event) 
    {
      SWRLQueryResultPanel resultPanel;
      String ruleName = "";
      QueryLibrary queryLibrary = null;
      Result result = null;
      
      if (resultPanels.size() == MaximumOpenResultPanels) {
        textArea.append("A maximum of " + MaximumOpenResultPanels + " result tabs may be open at once. ");
        textArea.append("Please close an existing tab to display results for the selected rule.\n");
      } else {
	try {
          bridge.resetBridge();
          bridge.importSWRLRulesAndOWLKnowledge();
          bridge.exportSWRLRulesAndOWLKnowledge();
          bridge.runRuleEngine();
        
          ruleName = BridgePluginManager.getSelectedRuleName();
          
          if (ruleName == null || ruleName.equals("")) textArea.append("No rule selected.\n");
          else {
            result = bridge.getQueryResult(ruleName);
            if (result == null || result.getNumberOfRows() == 0) {
              textArea.append("Rule '" + ruleName + "' did not generate any result.\n");
              if  (resultPanels.containsKey(ruleName)) {
                resultPanel = resultPanels.get(ruleName);
                resultPanels.remove(resultPanel);
                ((JTabbedPane)getParent()).remove(resultPanel);
              } // if
            } else { // A result was returned
              textArea.append("See the '" + ruleName + "' tab to review results of the query.\n");
              
              if  (resultPanels.containsKey(ruleName)) resultPanel = resultPanels.get(ruleName); // Existing tab found
              else { // Create new tab
                resultPanel = new SWRLQueryResultPanel(bridge, controlPanel, ruleName);
                resultPanels.put(ruleName, resultPanel);
                ((JTabbedPane)getParent()).addTab(ruleName, SWRLIcons.getImpsIcon(), resultPanel, "Result Panel for rule '" + ruleName + "'");
              } // if
              resultPanel.validate();
              controlPanel.getParent().validate();
            } // if
          } // if
	} catch (SWRLRuleEngineBridgeException e) {
          if (ruleName.equals("")) textArea.append("Exception running rules:" + e.getMessage() + "\n");
          else textArea.append("Exception when running rule '" + ruleName + "': " + e.getMessage() + "\n");
	} // try
      } // if
    } // ActionPerformed
  } // RunRuleActionListener
} // SWRLQueryControlPanel




