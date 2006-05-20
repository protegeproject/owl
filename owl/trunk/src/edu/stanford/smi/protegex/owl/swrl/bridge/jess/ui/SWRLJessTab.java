

// TODO: the six imported/asserted subpanel classes should be replaced by one using generics.
// TODO: rather than returning null on failure from createRuleEngineGUI, throw and exception.

package edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.ui.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.icons.JessIcons;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;

import jess.*;

import javax.swing.*;
import java.awt.*;

public class SWRLJessTab extends JTabbedPane implements SWRLRuleEngineGUIAdapter
{
  static {

    // Register this engine with the rule engine UI adapter so that the SWRL tab can activate and deactivate it.
    SWRLRuleEngineManager.registerRuleEngine("Jess", "Activate/deactivate Jess rule engine", JessIcons.getJessIcon(), new SWRLJessTab());
  } // static

  private SWRLJessBridge bridge;

  public Container getRuleEngineGUI() { return this; }

  public Container createRuleEngineGUI(OWLModel owlModel)
  {
    Rete rete;

    try {
      rete = new Rete();
    } catch (NoClassDefFoundError e) {
      System.err.println("Error loading Jess. Is the Jess JAR in the Protege-OWL plugin directory?");
      makeTextPanel("Error loading Jess. Is the Jess JAR in the Protege-OWL plugin directory?");
      return null;
    } // try

    try {
      bridge = new SWRLJessBridge(owlModel, rete);
    } catch (SWRLRuleEngineBridgeException e) {
      System.err.println("Error initializing the SWRL to Jess bridge: " + e.toString());
      makeTextPanel("Error initializing the SWRL to Jess bridge: " + e.toString());
      return null;
    } // try

    removeAll();

    JessControlPanel controlPanel = new JessControlPanel(bridge);
    addTab("Jess Control", getImpsIcon(), controlPanel, "Jess Control Panel");

    JessRulesPanel rulesPanel = new JessRulesPanel(bridge);
    addTab("Jess Rules", getImpsIcon(), rulesPanel, "Jess Rules Panel");

    JessImportedClassesPanel importedClassesPanel = new JessImportedClassesPanel(bridge);
    addTab("Imported Jess Classes", getImpsIcon(), importedClassesPanel, "Imported Jess Classes Panel");

    JessImportedPropertiesPanel importedPropertiesPanel = new JessImportedPropertiesPanel(bridge);
    addTab("Imported Jess Properties", getImpsIcon(), importedPropertiesPanel, "Imported Jess Properties Panel");

    JessImportedIndividualsPanel importedIndividualsPanel = new JessImportedIndividualsPanel(bridge);
    addTab("Imported Jess Individuals", getImpsIcon(), importedIndividualsPanel, "Imported Jess Individuals Panel");

    JessAssertedIndividualsPanel assertedIndividualsPanel = new JessAssertedIndividualsPanel(bridge);
    addTab("Asserted Jess Individuals", getImpsIcon(), assertedIndividualsPanel, "Asserted Jess Individuals Panel");

    JessAssertedPropertiesPanel assertedPropertiesPanel = new JessAssertedPropertiesPanel(bridge);
    addTab("Asserted Jess Properties", getImpsIcon(), assertedPropertiesPanel, "Asserted Jess Properties Panel");

    return this;

  } // createRuleEngineGUI

  private void makeTextPanel(String text) 
  { 
    JPanel panel = new JPanel(false); 
    JLabel filler = new JLabel(text); 
    filler.setHorizontalAlignment(JLabel.CENTER); 
    panel.setLayout(new GridLayout(1, 1)); 
    panel.add(filler); 

    add(panel);
  }  // makeTextPanel

  private Icon getImpsIcon() { return SWRLIcons.getImpsIcon(); }

} // SWRLJessTab
