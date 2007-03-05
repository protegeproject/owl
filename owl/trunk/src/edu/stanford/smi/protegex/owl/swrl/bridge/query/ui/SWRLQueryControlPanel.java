
package edu.stanford.smi.protegex.owl.swrl.bridge.query.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;

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
  private SWRLJessBridge bridge;
  private HashMap<String, SWRLQueryResultPanel> resultPanels;

  private static int MaximumOpenResultPanels = 8;

  public SWRLQueryControlPanel(SWRLJessBridge bridge) 
  {
    JTextArea textArea;
    JPanel panel;
    JButton button;

    this.bridge = bridge;

    resultPanels = new HashMap();

    setLayout(new BorderLayout());

    textArea = createTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(900, 300));

    add(BorderLayout.CENTER, scrollPane);
    
    panel = new JPanel(new FlowLayout());

    button = createButton("Run", "Run the selected SWRL rule", new RunRuleActionListener(bridge, textArea, this));
    panel.add(button);

    add(BorderLayout.SOUTH, panel);

    textArea.append("SWRLQueryTab\n\n");
    textArea.append("See http://protege.cim3.net/cgi-bin/wiki.pl?SWRLQueryTab for documentation.\n\n");
    textArea.append("Select a rule with query built-ins from the list above and press the Run button.\n");
    textArea.append("If the rule generates a result, the result will appear in a new tab.\n");
  } // SWRLQueryControlPanel

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
    textArea.setEditable(false);
    return textArea;
  } // createTextArea

  private class ListenerBase
  {
    protected SWRLJessBridge bridge;
    protected JTextArea textArea;
    protected SWRLQueryControlPanel controlPanel;

    public ListenerBase(SWRLJessBridge bridge, JTextArea textArea, SWRLQueryControlPanel controlPanel)
    {
      this.bridge = bridge;
      this.textArea = textArea;
      this.controlPanel = controlPanel;
    } // ListenerBase
  } // ListenerBase

  private class RunRuleActionListener extends ListenerBase implements ActionListener
  {
    public RunRuleActionListener(SWRLJessBridge bridge, JTextArea textArea, SWRLQueryControlPanel controlPanel) 
    { super(bridge, textArea, controlPanel); }
    
    public void actionPerformed(ActionEvent event) 
    {
      SWRLQueryResultPanel resultPanel;
      String ruleName = "";
      QueryLibrary queryLibrary = null;
      Result result = null;
      
      if (resultPanels.size() == MaximumOpenResultPanels) {
        textArea.append("A maximum of " + MaximumOpenResultPanels + " may be open at once.\n");
        textArea.append("Please close an existing panel to display a panel for this rule.\n");
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
            if (result == null) textArea.append("Rule '" + ruleName + "' does not have any query built-ins or did not generate any result.\n");
            else {
              if (result.getNumberOfRows() == 0) {
                textArea.append("No results for query '" + ruleName + "'.\n");
                // TODO: kill or update tab it is exists
              } else textArea.append("See tab to review results of query '" + ruleName + "'.\n");
              if  (resultPanels.containsKey(ruleName)) resultPanel = resultPanels.get(ruleName);
              else {
                resultPanel = new SWRLQueryResultPanel(bridge, ruleName);
                resultPanels.put(ruleName, resultPanel);
                ((JTabbedPane)getParent()).addTab(ruleName, SWRLIcons.getImpsIcon(), resultPanel, "Result Panel for rule '" + ruleName + "'");
              } // if
              resultPanel.validate();
              controlPanel.getParent().validate();
            } // else
          } // else
	} catch (SWRLRuleEngineBridgeException e) {
          textArea.append("Exception getting result for rule '" + ruleName + "': " + e.getMessage() + "\n");
	} // try
      } // if
    } // ActionPerformed
  } // RunRuleActionListener
} // SWRLQueryControlPanel




