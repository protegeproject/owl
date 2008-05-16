
package edu.stanford.smi.protegex.owl.swrl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;

import java.util.*;

/**
 ** This interface defines the methods that must be provided by a SWRL rule engine.
 **
 */
public interface SWRLRuleEngine
{
  /**
   ** Load rules and knowledge from OWL into bridge, send them to a rule engine, run the rule engine, and write any inferred knowledge back
   ** to OWL.
   */
  void infer() throws SWRLRuleEngineException;
  void infer(String ruleGroupName) throws SWRLRuleEngineException;
  void infer(Set<String> ruleGroupNames) throws SWRLRuleEngineException;

  /**
   ** Load rules and knowledge from OWL into bridge. All existing bridge rules and knowledge will first be cleared and the associated rule
   ** engine will be reset.
   */
  void importSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineException;
  void importSWRLRulesAndOWLKnowledge(String ruleGroupName) throws SWRLRuleEngineException;
  void importSWRLRulesAndOWLKnowledge(Set<String> ruleGroupNames) throws SWRLRuleEngineException;

  /**
   ** Run the rule engine.
   */
  void run() throws SWRLRuleEngineException;

  /**
   ** Write knowledge inferred by rule engine back to OWL.
   */
  void writeInferredKnowledge2OWL() throws SWRLRuleEngineException;

  /**
   **  Clear all knowledge from rule engine, deleted asserted knowledge from the bridge, and leave imported bridge knowledge intact.
   */
  void reset() throws SWRLRuleEngineException;

  SWRLRule getRule(String ruleName) throws InvalidRuleNameException;

  int getNumberOfInferredIndividuals();
  int getNumberOfInferredAxioms();

  Set<OWLIndividual> getInferredIndividuals();
  Set<OWLAxiom> getInferredAxioms();
} // SWRLRuleEngine
