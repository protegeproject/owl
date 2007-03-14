
package edu.stanford.smi.protegex.owl.swrl.bridge.query.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.ui.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.ui.icons.QueryIcons;

import javax.swing.*;
import java.awt.*;

public class SWRLQueryTab extends JTabbedPane implements SWRLPluginGUIAdapter
{
  static {
    // Register this plugin with the plugin manager so that the SWRL tab can activate and deactivate it.
    BridgePluginManager.registerPlugin("SWRLQueryTab", "Activate/deactivate SWRLQueryTab", QueryIcons.getQueryIcon(), new SWRLQueryTab());
  } // static

  private SWRLRuleEngineBridge bridge;

  public Container getPluginGUI() { return this; }

  public Container createPluginGUI(OWLModel owlModel)
  {
    // TODO: Eventually pop up window to select an engine from a list.

    try {
      bridge = RuleEngineFactory.createRuleEngine(owlModel);
    } catch (SWRLRuleEngineBridgeException e) {
      System.err.println(e.toString());
      return makeErrorWindow(e.toString());
    } // try

    removeAll();

    SWRLQueryControlPanel controlPanel = new SWRLQueryControlPanel(bridge);
    addTab("SWRLQueryTab", QueryIcons.getQueryIcon(), controlPanel, "Control Panel");

    return this;
  } // createPluginGUI

  private Container makeErrorWindow(String text) 
  { 
    removeAll();
    JPanel panel = new JPanel(false); 
    JLabel filler = new JLabel(text); 
    filler.setHorizontalAlignment(JLabel.CENTER); 
    panel.setLayout(new GridLayout(1, 1)); 
    panel.add(filler); 
    
    add(panel);

    return this;
  }  // makeErrorWindow
} // SWRLQueryTab
