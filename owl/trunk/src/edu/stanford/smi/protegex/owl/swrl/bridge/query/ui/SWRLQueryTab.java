
package edu.stanford.smi.protegex.owl.swrl.bridge.query.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.ui.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.ui.icons.QueryIcons;

import jess.*;

import javax.swing.*;
import java.awt.*;

public class SWRLQueryTab extends JTabbedPane implements SWRLPluginGUIAdapter
{
  static {
    // Register this plugin with the plugin manager so that the SWRL tab can activate and deactivate it.
    BridgePluginManager.registerPlugin("SWRLQueryTab", "Activate/deactivate SWRLQueryTab", QueryIcons.getQueryIcon(), new SWRLQueryTab());
  } // static

  private SWRLJessBridge bridge;

  public Container getPluginGUI() { return this; }

  public Container createPluginGUI(OWLModel owlModel)
  {
    Rete rete;

    // TODO: eventually pop up window to select an engine from a list.

    try {
      rete = new Rete();
    } catch (NoClassDefFoundError e) {
      return makeErrorWindow("Error loading Jess. Is jess.jar in the Protege-OWL plugins directory?\n" + 
                             "It should be in the ./plugins/edu.stanford.smi.protegex.owl subdirectory of the Protege installation directory.");
    } catch (Exception e) {
      return makeErrorWindow("Error loading Jess: " + e.getMessage());
    } // try

    try {
      bridge = new SWRLJessBridge(owlModel, rete);
    } catch (SWRLRuleEngineBridgeException e) {
      System.err.println("Error initializing the SWRL to Jess bridge: " + e.toString());
      return makeErrorWindow("Error initializing the SWRL to Jess bridge: " + e.toString());
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
