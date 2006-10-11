
package edu.stanford.smi.protegex.owl.swrl.bridge.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineManager;

import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;

import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ViewRuleEngineAction extends AbstractAction 
{
  private String ruleEngineName;
  private OWLModel owlModel;
  private SWRLTab swrlTab;

  public ViewRuleEngineAction(String ruleEngineName, String tip, Icon icon, SWRLTab swrlTab, OWLModel owlModel) 
  {
    super(tip, icon);
    
    this.ruleEngineName = ruleEngineName;
    this.owlModel = owlModel;
    this.swrlTab = swrlTab;
  } // ViewRuleEngineAction
  
  public void actionPerformed(ActionEvent e) 
  {
    if (SWRLRuleEngineManager.isVisible(ruleEngineName)) SWRLRuleEngineManager.hideRuleEngine(ruleEngineName);
    else SWRLRuleEngineManager.showRuleEngine(ruleEngineName, swrlTab, owlModel);
  } // actionPerformed
} // ViewRuleEngineAction
