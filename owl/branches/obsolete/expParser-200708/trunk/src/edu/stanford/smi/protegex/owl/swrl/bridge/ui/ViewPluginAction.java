
package edu.stanford.smi.protegex.owl.swrl.bridge.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.BridgePluginManager;

import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;

import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewPluginAction extends AbstractAction 
{
  private String pluginName;
  private OWLModel owlModel;
  private SWRLTab swrlTab;

  public ViewPluginAction(String pluginName, String tip, Icon icon, SWRLTab swrlTab, OWLModel owlModel) 
  {
    super(tip, icon);
    
    this.pluginName = pluginName;
    this.owlModel = owlModel;
    this.swrlTab = swrlTab;
  } // ViewPluginAction
  
  public void actionPerformed(ActionEvent e) 
  {
    if (BridgePluginManager.isVisible(pluginName)) BridgePluginManager.hidePlugin(pluginName);
    else BridgePluginManager.showPlugin(pluginName, swrlTab, owlModel);
  } // actionPerformed
} // ViewPluginAction
