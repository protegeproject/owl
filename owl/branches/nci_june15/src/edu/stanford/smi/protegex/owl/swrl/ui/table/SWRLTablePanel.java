package edu.stanford.smi.protegex.owl.swrl.ui.table;

import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineManager;
import edu.stanford.smi.protegex.owl.swrl.bridge.ui.ViewRuleEngineAction;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.ui.actions.EditRuleAction;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * A JPanel consisting of a SWRLTable and buttons to create
 * and delete rules.
 */
public class SWRLTablePanel extends JPanel implements Disposable {

    private EditRuleAction editRuleAction;

    private SWRLTable table;

    private SWRLTableModel tableModel;


  // Called when the table panel is in a results panel.
  public SWRLTablePanel(OWLModel owlModel, RDFResource resource) 
  {
    initialize(owlModel, resource);
  } // SWRLTablePanel

  // Called when the table panel is within the SWRLTab.
  public SWRLTablePanel(OWLModel owlModel, RDFResource resource, SWRLTab swrlTab) 
  {
    LabeledComponent lc = initialize(owlModel, resource);

    // Iterate through all registered rule engine and add an enable button for each one.
    Iterator iterator = SWRLRuleEngineManager.getRegisteredRuleEngines().iterator();
    while (iterator.hasNext()) {
      SWRLRuleEngineManager.RuleEngineRegistrationInfo info = (SWRLRuleEngineManager.RuleEngineRegistrationInfo)iterator.next();
      lc.addHeaderButton(new ViewRuleEngineAction(info.getRuleEngineName(), info.getToolTip(), info.getIcon(), swrlTab, owlModel));
      add(BorderLayout.CENTER, lc);
    } // while
  } // SWRLTablePanel

  public void dispose() {
    table.dispose();
  }

  private LabeledComponent initialize(OWLModel owlModel, RDFResource RDFResource) {
    
    tableModel = RDFResource == null ?
      new SWRLTableModel(owlModel) :
      new SWRLTableModel(RDFResource);
    table = new SWRLTable(tableModel, owlModel);
    
    JScrollPane scrollPane = new JScrollPane(table);
    JViewport viewPort = scrollPane.getViewport();
    viewPort.setBackground(table.getBackground());
    
    LabeledComponent lc = new OWLLabeledComponent("SWRL Rules", scrollPane);
    lc.addHeaderButton(new ViewRuleAction(table));
    lc.addHeaderButton(new CreateRuleAction(table, owlModel));
    lc.addHeaderButton(new CloneRuleAction(table, owlModel));
    lc.addHeaderButton(new DeleteRuleAction(table));
    
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, lc);

    return lc;
  } // initialize

}
