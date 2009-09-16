
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLResult;

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
  // engine. Eventually subsume all these methods into injectOWLAxiom.
  OWLClass injectOWLAnonymousClass() throws SWRLBuiltInBridgeException;
  void injectOWLClass(String className) throws SWRLBuiltInBridgeException;
  void injectOWLClass(String className, String superclassName) throws SWRLBuiltInBridgeException;
  OWLIndividual injectOWLIndividual() throws SWRLBuiltInBridgeException;
  void injectOWLIndividual(OWLIndividual owlIndividual) throws SWRLBuiltInBridgeException;
  OWLIndividual injectOWLIndividual(OWLClass owlClass) throws SWRLBuiltInBridgeException;
  void injectOWLIndividuals(Set<OWLIndividual> individuals) throws SWRLBuiltInBridgeException;
  void injectOWLAxiom(OWLAxiom axiom) throws SWRLBuiltInBridgeException;

  boolean isOWLClass(String className);
  boolean isOWLProperty(String propertyName);
  boolean isOWLIndividual(String individualName);
  boolean isOWLIndividualOfClass(String individualName, String className);

  Set<OWLPropertyAssertionAxiom> getOWLPropertyAssertionAxioms(String individualName, String propertyName) throws SWRLBuiltInBridgeException;

  // Mapper to non OWL storage formats
  void setMapper(Mapper mapper);
  boolean hasMapper();
  Mapper getMapper();

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

  OWLModel getOWLModel(); // TODO: This Protege-OWL API dependency should be removed.
} // SWRLBuiltInBridge
