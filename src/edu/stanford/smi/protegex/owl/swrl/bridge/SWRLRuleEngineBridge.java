
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;

/**
 ** The SWRL Rule Engine Bridge defines the interface seen by a implementation of a SWRL rule engine.
 **
 ** Detailed documentation for this mechanism can be found <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public interface SWRLRuleEngineBridge extends SWRLRuleEngine, SQWRLQueryEngine
{
  // The infer methods can be used by a target rule engines to assert axioms that they infer into the bridge.
  void inferOWLPropertyAssertionAxiom(OWLPropertyAssertionAxiom owlPropertyAssertionAxiom) throws SWRLRuleEngineBridgeException;
  void inferOWLIndividual(OWLIndividual owlIndividual, OWLClass owlClass) throws SWRLRuleEngineBridgeException;
  
  // Built-in invocation and argument binding - called by target rule engine
  boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, boolean isInConsequent, List<BuiltInArgument> arguments) 
    throws SWRLRuleEngineBridgeException;

  boolean isInjectedOWLAnonymousClass(String className);
  boolean isInjectedOWLClass(String className);
  boolean isInjectedOWLIndividual(String individualName);
  boolean isInjectedOWLAxiom(OWLAxiom axiom);

  OWLModel getOWLModel(); // TODO: This Protege-OWL API dependency should be removed.
} // SWRLRuleEngineBridge
