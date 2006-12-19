
// TODO: Jess rule engine and SWRL query tab are loaded explicitly here. We need a discovery mechanism using the manifest.
// TODO: Should this be in the ui subpackage?

/*
** This class provides mechanisms for plugins to register themselves and to get screen real estate in the SWRLTab.
*/
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.ui.SWRLPluginGUIAdapter;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.event.*;

import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Container;

public class BridgePluginManager
{
  private static HashMap<String, PluginRegistrationInfo> registeredPlugins = new HashMap<String, PluginRegistrationInfo>();
  private static String visiblePluginName = "";
  private static String selectedRuleName = "";
  
  private static ProjectListener projectListener = new ProjectAdapter() 
  {
    public void projectClosed(ProjectEvent event) 
    { 
      Project project = (Project)event.getSource();
      project.removeProjectListener(projectListener);
      visiblePluginName = "";
      selectedRuleName = "";
    } // projectClosed
  }; 
  
  static {

    try { // TODO:  Hack until we can do a proper class load with the manifest
      Class.forName("edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.SWRLJessTab");
    } catch (ClassNotFoundException e) {
      System.err.println("SWRLJessTab load failed: Could not find the edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.SWRLJessTab class.");
    } // 

    try { // TODO:  Hack until we can do a proper class load with the manifest
      Class.forName("edu.stanford.smi.protegex.owl.swrl.bridge.query.ui.SWRLQueryTab");
    } catch (ClassNotFoundException e) {
      // TODO: commented out until SWRLQueryTab is offically provided with the Protege-OWL distribution.
      //System.err.println("SWRLQueryTab load failed: Could not find the edu.stanford.smi.protegex.owl.swrl.bridge.query.ui.SWRLQueryTab class.");
    } // 
  } // static

  public static String getSelectedRuleName() { return selectedRuleName; }
  public static boolean hasSelectedRule() { return !selectedRuleName.equals(""); }
  public static void setSelectedRuleName(String ruleName) { selectedRuleName = ruleName; }

  // Called by each plugin as it is loaded to inform the adapter of its presence.
  public static void registerPlugin(String pluginName, String toolTip, Icon icon, SWRLPluginGUIAdapter guiAdapter)
  {
    if (registeredPlugins.containsKey(pluginName)) registeredPlugins.remove(pluginName);

    registeredPlugins.put(pluginName, new PluginRegistrationInfo(pluginName, toolTip, icon, guiAdapter));

    System.err.println("Registering " + pluginName + " with SWRL bridge.");
  } // registerPlugin

  public static Collection<PluginRegistrationInfo> getRegisteredPlugins() { return registeredPlugins.values(); }

  public static void unregisterPlugin(String pluginName)
  {
    if (registeredPlugins.containsKey(pluginName)) {
      if (isVisible(pluginName)) hidePlugin(pluginName, true);
      registeredPlugins.remove(pluginName);
    } // if
  } // unregisterEngine

  public static boolean isVisible(String pluginName) 
  { 
    return !visiblePluginName.equals("") && pluginName.equals(visiblePluginName);
  } // isVisible

  public static void showPlugin(String pluginName, SWRLTab swrlTab, OWLModel owlModel)
  {
    if (!isVisible(pluginName)) {
      if (hidePlugin(visiblePluginName)) { // Hide may fail if user does not confirm it.
      
        if (registeredPlugins.containsKey(pluginName)) {
          PluginRegistrationInfo info = (PluginRegistrationInfo)registeredPlugins.get(pluginName);
          Container pluginGUI = info.getGUIAdapter().createPluginGUI(owlModel);

          info.setOWLModel(owlModel); // Set the owlModel so that we can deregister ourselves on deactivation.
          
          if (pluginGUI != null) {
            swrlTab.setVisible(false); 
            swrlTab.setLayout(new GridLayout(2, 1));          
            swrlTab.add(pluginGUI);
            swrlTab.setVisible(true);
            visiblePluginName = pluginName;
          } else  makeTextPanel(swrlTab, "Unable to activate the " + pluginName + " plugin.");
        } // if
      } // if
    } // if
    owlModel.getProject().addProjectListener(projectListener);
  } // showPlugin

  public static void hideVisiblePlugin() { hidePlugin(visiblePluginName, true); }

  public static boolean hidePlugin(String pluginName) { return hidePlugin(pluginName, false); }

  private static boolean hidePlugin(String pluginName, boolean force)
  {
    if (isVisible(pluginName)) {

      if (!force && (JOptionPane.showConfirmDialog(null, "Do you really want to disable the " + pluginName + " plugin?", "Disable " + pluginName,
                                                   JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)) return false;

      if (registeredPlugins.containsKey(pluginName)) {
        PluginRegistrationInfo info = registeredPlugins.get(pluginName);
        Container pluginGUI = info.getGUIAdapter().getPluginGUI();
        SWRLTab swrlTab = (SWRLTab)pluginGUI.getParent();
        swrlTab.setVisible(false); 
        swrlTab.remove(pluginGUI);
        swrlTab.reconfigure();
        swrlTab.setVisible(true);
        visiblePluginName = "";

        if (info.hasOWLModel()) info.getOWLModel().getProject().removeProjectListener(projectListener); 
      } // if
    } // if
    return true;
  } // hidePlugin
  
  public static class PluginRegistrationInfo
  {
    private String pluginName;
    private String toolTip;
    private SWRLPluginGUIAdapter guiAdapter;
    private Icon icon;
    private OWLModel owlModel;

    public PluginRegistrationInfo(String pluginName, String toolTip, Icon icon, SWRLPluginGUIAdapter guiAdapter)
    {
      this.pluginName = pluginName;
      this.toolTip = toolTip;
      this.guiAdapter = guiAdapter;
      this.icon = icon;
      owlModel = null; // An OWL model is supplied when a GUI associated with the plugin is activated.
    } // PluginRegistrationInfo

    public void setOWLModel(OWLModel owlModel) { this.owlModel = owlModel; }

    public String getPluginName() { return pluginName; }
    public String getToolTip() { return toolTip; }
    public SWRLPluginGUIAdapter getGUIAdapter() { return guiAdapter; }
    public Icon getIcon() { return icon; }
    public OWLModel getOWLModel() { return owlModel; } 
    public boolean hasOWLModel() { return owlModel != null; }
      
  } // PluginRegistrationInfo

  private static void makeTextPanel(SWRLTab swrlTab, String text) 
  { 
    JPanel panel = new JPanel(false); 
    JLabel filler = new JLabel(text); 
    filler.setHorizontalAlignment(JLabel.CENTER); 
    panel.setLayout(new GridLayout(1, 1)); 
    panel.add(filler); 

    swrlTab.add(panel);
  }  // makeTextPanel
    
} // BridgePluginManager
