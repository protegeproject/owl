
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLBuiltInBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.Set;
import java.util.List;

/**
 ** The SWRL Built-in Bridge defines the methods seen by built-in implementations at run time. Ideally, built-in implementations should only
 ** use this interface to operate on the active ontology. However, some specialized libraries (e.g., abox and tbox) require direct access to
 ** the active ontology so will use the provided getOWLModel method.
 **
 ** Detailed documentation for the SWRl rule engine bridge mechanism can be found <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public interface SWRLBuiltInBridge
{
  // The inject methods can be used by built-ins to inject new axioms into a bridge, which will also reflect them in the underlying
  // engine. Eventually collapse all inject methods into injectOWLAxiom.
  void injectOWLAxiom(OWLAxiom axiom) throws SWRLBuiltInBridgeException;

  OWLClass injectOWLClass() throws SWRLBuiltInBridgeException;
  void injectOWLClass(String className) throws SWRLBuiltInBridgeException;
  OWLIndividual injectOWLIndividual() throws SWRLBuiltInBridgeException;
  void injectOWLIndividual(OWLIndividual owlIndividual) throws SWRLBuiltInBridgeException;
  OWLIndividual injectOWLIndividualOfClass(OWLClass owlClass) throws SWRLBuiltInBridgeException;

  boolean isOWLClass(String className);
  boolean isOWLObjectProperty(String propertyName);
  boolean isOWLDataProperty(String propertyName);
  boolean isOWLIndividual(String individualName);
  boolean isOWLIndividualOfClass(String individualName, String className);
  Set<OWLIndividual> getOWLIndividuals();

  Set<OWLPropertyAssertionAxiom> getOWLPropertyAssertionAxioms(String individualName, String propertyName) throws SWRLBuiltInBridgeException;

  /**
   * Invoke a SWRL built-in. Should not be called by built-ins in general; provided (perhaps temporarily) for specialized invocation
   * purposes only (e.g., swrlx:invokeSWRLBuiltIn).
   */
  boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, boolean isInConsequent, List<BuiltInArgument> arguments) 
    throws SWRLRuleEngineBridgeException;

  /**
   * Method to provide access to SWRL rules from a built-in.  Should not be called by built-ins in general; provided (temporarily) for the
   * use of the SQWRL built-in library.
   */
  SWRLRule getSWRLRule(String ruleName) throws SWRLBuiltInBridgeException;

  OWLDataFactory getOWLDataFactory();
  OWLModel getOWLModel(); // TODO: Protege-OWL dependency - remove
} // SWRLBuiltInBridge
