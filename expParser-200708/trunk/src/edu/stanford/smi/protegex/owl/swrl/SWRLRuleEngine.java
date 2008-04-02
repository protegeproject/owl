
package edu.stanford.smi.protegex.owl.swrl;

import edu.stanford.smi.protegex.owl.swrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.Result;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.ResultException;

import java.util.*;


/**
 ** This inferface defines the methods that must be provided by a SWRL rule engine.
 **
 */
public interface SWRLRuleEngine
{
  /**
   ** Load rules and knowledge from OWL into bridge, send them to a rule engine, run the rule engine, and write any inferred knowledge back
   ** to OWL.
   */
  void infer() throws SWRLRuleEngineException;

  /**
   ** Load rules and knowledge from OWL into bridge. All existing bridge rules and knowledge will first be cleared and the associated rule
   ** engine will be reset.
   */
  void importSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineException;

  /**
   ** Load rules from a particular rule group and associated knowledge from OWL into bridge. All existing bridge rules and knowledge will
   ** first be cleared and the associated rule engine will be reset.
   */
  void importSWRLRulesAndOWLKnowledge(String ruleGroupName) throws SWRLRuleEngineException;

  /**
   ** Load rules from all the named rule groups and associated knowledge from OWL into bridge. All existing bridge rules and knowledge will
   ** first be cleared and the associated rule engine will be reset.
   */
  void importSWRLRulesAndOWLKnowledge(Set<String> ruleGroupNames) throws SWRLRuleEngineException;

  /**
   ** Run the rule engine.
   */
  void run() throws SWRLRuleEngineException;

  /**
   ** Send rules and knowledge stored in bridge to a rule engine.
   */
  void exportSWRLRulesAndOWLKnowledge() throws SWRLRuleEngineException;

  /**
   ** Send knowledge (excluding SWRL rules) stored in bridge to a rule engine.
   */
  void exportOWLKnowledge() throws SWRLRuleEngineException;

  /**
   ** Write knowledge inferred by rule engine back to OWL.
   */
  void writeAssertedIndividualsAndProperties2OWL() throws SWRLRuleEngineException;

  /**
   **  Clear all knowledge from rule engine, deleted asserted knowledge from the bridge, and leave imported bridge knowledge intact.
   */
  void resetRuleEngine() throws SWRLRuleEngineException;

  /**
   **  Get the results from a rule containing query built-ins. Null is retured if there are no results or if the query subsystem is not
   **  activated.
   */
  Result getQueryResult(String ruleName) throws ResultException;

} // SWRLRuleEngine
