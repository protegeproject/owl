
// This class provides mechanisms for rule systems to get screen real estate in the SWRL Tab.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.ui.SWRLRuleEngineGUIAdapter;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import java.awt.*;

public class SWRLRuleEngineManager
{
  private static HashMap registeredRuleEngines = new HashMap();
  private static String visibleRuleEngineName = "";

  static {

    try { // Hack until we can do a proper class load wit the manifest
      Class.forName("edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.SWRLJessTab");
    } catch (ClassNotFoundException e) {
      System.err.println("Could not find Jess Tab class");
    } // 
  } // static

  // Called by each rule system as it is loaded to inform the adapter of  presence.
  public static void registerRuleEngine(String ruleEngineName, String toolTip, Icon icon, SWRLRuleEngineGUIAdapter guiAdapter)
  {
    if (registeredRuleEngines.containsKey(ruleEngineName)) registeredRuleEngines.remove(ruleEngineName);

    registeredRuleEngines.put(ruleEngineName, new RuleEngineRegistrationInfo(ruleEngineName, toolTip, icon, guiAdapter));

    System.err.println("Registering " + ruleEngineName + " rule engine with SWRL bridge.");
  } // registerRuleEngine

  public static Collection getRegisteredRuleEngines() 
  { 
    return registeredRuleEngines.values(); 
  } // getRegisteredRuleEngines

  public static void unregisterEngine(String ruleEngineName)
  {
    if (registeredRuleEngines.containsKey(ruleEngineName)) {
      if (isVisible(ruleEngineName)) hideRuleEngine(ruleEngineName);
      registeredRuleEngines.remove(ruleEngineName);
    } // if
  } // unregisterEngine

  public static boolean isVisible(String ruleEngineName) 
  { 
    return ruleEngineName.equals(visibleRuleEngineName);
  } // isVisible

  public static void showRuleEngine(String ruleEngineName, SWRLTab swrlTab, OWLModel owlModel)
  {
    if (!ruleEngineName.equals(visibleRuleEngineName)) {

      hideRuleEngine(visibleRuleEngineName);
      
      if (registeredRuleEngines.containsKey(ruleEngineName)) {
        RuleEngineRegistrationInfo info = (RuleEngineRegistrationInfo)registeredRuleEngines.get(ruleEngineName);
        Container ruleEngineGUI;
        
        ruleEngineGUI = info.getGUIAdapter().createRuleEngineGUI(owlModel);

        if (ruleEngineGUI != null) {
          swrlTab.add(ruleEngineGUI);
          swrlTab.validate();
          swrlTab.setVisible(true);
          visibleRuleEngineName = ruleEngineName;
        } else {
          makeTextPanel(swrlTab, "Unable to activate the " + ruleEngineName + " rule engine.");
        } // if
      } // if
    } // if
  } // showRuleEngine

  public static void hideRuleEngine(String ruleEngineName)
  {
    if (ruleEngineName.equals(visibleRuleEngineName)) {
      if (registeredRuleEngines.containsKey(ruleEngineName)) {
        RuleEngineRegistrationInfo info = (RuleEngineRegistrationInfo)registeredRuleEngines.get(ruleEngineName);
        Container swrlTab = info.getGUIAdapter().getRuleEngineGUI().getParent();
        info.getGUIAdapter().getRuleEngineGUI().setVisible(false);
        swrlTab.remove(info.getGUIAdapter().getRuleEngineGUI());
        swrlTab.validate(); swrlTab.setVisible(true);
        visibleRuleEngineName = "";
      } // if
    } // if
  } // hideRuleEngine
  
  public static class RuleEngineRegistrationInfo
  {
    private String ruleEngineName;
    private String toolTip;
    private SWRLRuleEngineGUIAdapter guiAdapter;
    private Icon icon;

    public RuleEngineRegistrationInfo(String ruleEngineName, String toolTip, Icon icon, SWRLRuleEngineGUIAdapter guiAdapter)
    {
      this.ruleEngineName = ruleEngineName;
      this.toolTip = toolTip;
      this.guiAdapter = guiAdapter;
      this.icon = icon;
    } // RuleEngineRegistrationInfo

    public String getRuleEngineName() { return ruleEngineName; }
    public String getToolTip() { return toolTip; }
    public SWRLRuleEngineGUIAdapter getGUIAdapter() { return guiAdapter; }
    public Icon getIcon() { return icon; }
      
  } // RuleEngineRegistrationInfo

  private static void makeTextPanel(SWRLTab swrlTab, String text) 
  { 
    JPanel panel = new JPanel(false); 
    JLabel filler = new JLabel(text); 
    filler.setHorizontalAlignment(JLabel.CENTER); 
    panel.setLayout(new GridLayout(1, 1)); 
    panel.add(filler); 

    swrlTab.add(panel);
  }  // makeTextPanel

    
} // SWRLRuleEngineManager
