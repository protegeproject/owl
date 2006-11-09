
// This interface must be implemented by a target rule system to provide a mechanism to create screen real estate for its GUI, which will be
// displayed in the SWRLTab when the rule engine is activated.

package edu.stanford.smi.protegex.owl.swrl.bridge.ui;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import javax.swing.*;
import java.awt.*;

public interface SWRLRuleEngineGUIAdapter
{
  Container createRuleEngineGUI(OWLModel owlModel);
  Container getRuleEngineGUI();
} // SWRLRuleEngineGUIAdapter
