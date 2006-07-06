
// TODO: Jess is loaded explicitly here. We need a discovery mechanism using the manifest.
// TODO: should this be in the ui subpackage?

// This class provides mechanisms for rule engines to register themselves and to get screen real estate in the SWRL Tab.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.ui.SWRLRuleEngineGUIAdapter;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.event.*;

import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import java.awt.*;

public class SWRLRuleEngineManager
{
  private static HashMap registeredRuleEngines = new HashMap();
  private static String visibleRuleEngineName = "";

  private static ProjectListener projectListener = new ProjectAdapter() 
  {
    public void projectClosed(ProjectEvent event) 
    { 
      Project project = (Project)event.getSource();
      project.removeProjectListener(projectListener);
      visibleRuleEngineName = "";
    } // projectClosed
    }; 

  static {

    try { // TODO:  Hack until we can do a proper class load with the manifest
      Class.forName("edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.SWRLJessTab");
    } catch (ClassNotFoundException e) {
      System.err.println("Could not find the edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.SWRLJessTab class");
    } // 
  } // static

  // Called by each rule system as it is loaded to inform the adapter of its presence.
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
      if (isVisible(ruleEngineName)) hideRuleEngine(ruleEngineName, true);
      registeredRuleEngines.remove(ruleEngineName);
    } // if
  } // unregisterEngine

  public static boolean isVisible(String ruleEngineName) 
  { 
    return !visibleRuleEngineName.equals("") && ruleEngineName.equals(visibleRuleEngineName);
  } // isVisible

  public static void showRuleEngine(String ruleEngineName, SWRLTab swrlTab, OWLModel owlModel)
  {
    owlModel.getProject().addProjectListener(projectListener);

    if (!isVisible(ruleEngineName)) {

      if (hideRuleEngine(visibleRuleEngineName)) { // Hide may fail if user does not confirm it.
      
        if (registeredRuleEngines.containsKey(ruleEngineName)) {
          
          RuleEngineRegistrationInfo info = (RuleEngineRegistrationInfo)registeredRuleEngines.get(ruleEngineName);
          Container ruleEngineGUI;
          
          ruleEngineGUI = info.getGUIAdapter().createRuleEngineGUI(owlModel);
          
          if (ruleEngineGUI != null) {
            swrlTab.add(ruleEngineGUI);
            swrlTab.setVisible(false); swrlTab.setVisible(true); // Who knows?
            visibleRuleEngineName = ruleEngineName;
          } else  makeTextPanel(swrlTab, "Unable to activate the " + ruleEngineName + " rule engine.");
        } // if
      } // if
    } // if
  } // showRuleEngine

  public static void hideVisibleRuleEngine()
  {
    hideRuleEngine(visibleRuleEngineName, true);
  } // hideVisibleRuleEngine

  public static boolean hideRuleEngine(String ruleEngineName)
  {
    return hideRuleEngine(ruleEngineName, false);
  } // hideRuleEngine

  private static boolean hideRuleEngine(String ruleEngineName, boolean force)
  {
    if (isVisible(ruleEngineName)) {

      if (!force && (JOptionPane.showConfirmDialog(null, "Do you really want to disable the " + ruleEngineName + " rule engine?", "Disable " + ruleEngineName,
                                                   JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)) return false;

      if (registeredRuleEngines.containsKey(ruleEngineName)) {
        RuleEngineRegistrationInfo info = (RuleEngineRegistrationInfo)registeredRuleEngines.get(ruleEngineName);
        Container ruleEngineGUI = info.getGUIAdapter().getRuleEngineGUI();
        Container swrlTab = ruleEngineGUI.getParent();
        swrlTab.remove(ruleEngineGUI);
        swrlTab.setVisible(false); swrlTab.setVisible(true); // Who knows?
        visibleRuleEngineName = "";
      } // if
    } // if
    return true;
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
