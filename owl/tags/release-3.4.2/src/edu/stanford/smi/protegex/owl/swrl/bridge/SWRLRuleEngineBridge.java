
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;

/**
 ** The SWRL Rule Engine Bridge defines the interface seen by an implementation of a SWRL rule engine. The implementation used this
 ** interface primarily to infer axioms and to invoke builtins.
 **
 ** Detailed documentation for this mechanism can be found <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public interface SWRLRuleEngineBridge extends SWRLRuleEngine, SQWRLQueryEngine
{
  // The infer method can be used by a target rule engines to assert axioms that they infer into the bridge.
  void inferOWLAxiom(OWLAxiom axiom) throws SWRLRuleEngineBridgeException;
  
  // Built-in invocation and argument binding - called by target rule engine
  boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, boolean isInConsequent, List<BuiltInArgument> arguments) 
    throws SWRLRuleEngineBridgeException;

  boolean isOWLClass(String classURI);
  boolean isOWLObjectProperty(String propertyURI);
  boolean isOWLDataProperty(String propertyURI);
  boolean isOWLIndividual(String individualURI);

  OWLFactory getOWLFactory();
  OWLModel getOWLModel(); // TODO: Protege-OWL dependency - remove
} // SWRLRuleEngineBridge
