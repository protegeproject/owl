
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
   ** Write knowledge inferred by rule engine back to OWL.
   */
  void writeInferredKnowledge2OWL() throws SWRLRuleEngineException;

  /**
   **  Clear all knowledge from rule engine, deleted asserted knowledge from the bridge, and leave imported bridge knowledge intact.
   */
  void reset() throws SWRLRuleEngineException;

  int getNumberOfInferredIndividuals();
  int getNumberOfInferredPropertyAssertionAxioms();

  SWRLRule getRule(String ruleName) throws InvalidRuleNameException;

  Set<OWLIndividual> getInferredIndividuals();
  Set<OWLPropertyAssertionAxiom> getInferredPropertyAssertionAxioms();
} // SWRLRuleEngine
