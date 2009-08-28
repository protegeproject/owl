
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.*;

/**
 ** The SWRL Rule Engine Bridge provides a mechanism to incorporate rule engines into Protege-OWL to execute SWRL rules. <p>
 **
 ** Detailed documentation for this mechanism can be found <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ">here</a>.
 */
public interface SWRLRuleEngineBridge extends SWRLRuleEngine, SQWRLQueryEngine
{
  void resetRuleEngine() throws SWRLRuleEngineBridgeException;
  void runRuleEngine() throws SWRLRuleEngineBridgeException;

  // The define methods must be implemented by a target rule engine.
  void defineSWRLRule(SWRLRule rule) throws SWRLRuleEngineBridgeException;
  void defineOWLClass(OWLClass owlClass) throws SWRLRuleEngineBridgeException;
  void defineOWLIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException;
  void defineOWLAxiom(OWLAxiom axiom) throws SWRLRuleEngineBridgeException;
  void defineOWLRestriction(OWLRestriction restriction) throws SWRLRuleEngineBridgeException;

  // The infer methods can be used by a terget rule engines to assert axioms that they infer into the bridge.
  void inferOWLPropertyAssertionAxiom(OWLPropertyAssertionAxiom owlPropertyAssertionAxiom) throws SWRLRuleEngineBridgeException;
  void inferOWLIndividual(OWLIndividual owlIndividual, OWLClass owlClass) throws SWRLRuleEngineBridgeException;
  
  // Built-in invocation and argument binding 
  boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, boolean isInConsequent, List<BuiltInArgument> arguments) throws BuiltInException;
  void generateBuiltInBinding(String ruleName, String builtInName, int builtInIndex, List<BuiltInArgument> arguments) throws BuiltInException;
  Set<OWLPropertyAssertionAxiom> getOWLPropertyAssertionAxioms(String individualName, String propertyName) throws BuiltInException;

  // The inject methods can be used by built-ins to inject new axioms into a bridge, which will also reflect them in the underlying engine.
  OWLClass injectOWLAnonymousClass() throws SWRLRuleEngineBridgeException;
  void injectOWLClass(String className) throws SWRLRuleEngineBridgeException;
  void injectOWLClass(String className, String superclassName) throws SWRLRuleEngineBridgeException;
  OWLIndividual injectOWLIndividual() throws SWRLRuleEngineBridgeException;
  void injectOWLIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException;
  OWLIndividual injectOWLIndividual(OWLClass owlClass) throws SWRLRuleEngineBridgeException;
  void injectOWLIndividuals(Set<OWLIndividual> individuals) throws SWRLRuleEngineBridgeException;
  void injectOWLAxiom(OWLAxiom axiom) throws SWRLRuleEngineBridgeException;
  OWLDatatypePropertyAssertionAxiom injectOWLDatatypePropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLDatatypeValue object) 
    throws SWRLRuleEngineBridgeException;
  OWLDatatypePropertyAssertionAxiom injectOWLDatatypePropertyAssertionAxiom(OWLDatatypePropertyAssertionAxiom axiom)
    throws SWRLRuleEngineBridgeException;
  void injectOWLDatatypePropertyAssertionAxioms(Set<OWLDatatypePropertyAssertionAxiom> axioms) throws SWRLRuleEngineBridgeException;
  OWLObjectPropertyAssertionAxiom injectOWLObjectPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLIndividual object) 
    throws SWRLRuleEngineBridgeException;
  OWLObjectPropertyAssertionAxiom injectOWLObjectPropertyAssertionAxiom(OWLObjectPropertyAssertionAxiom axiom)
    throws SWRLRuleEngineBridgeException;
  OWLClassAssertionAxiom injectOWLClassAssertionAxiom(OWLIndividual individual, OWLClass description) throws SWRLRuleEngineBridgeException;
  void injectOWLObjectPropertyAssertionAxioms(Set<OWLObjectPropertyAssertionAxiom> axioms) throws SWRLRuleEngineBridgeException;
  void injectOWLSubClassAxiom(OWLClass subClass, OWLClass superClass) throws SWRLRuleEngineBridgeException;
  void injectOWLClassPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLClass object) throws SWRLRuleEngineBridgeException; 

  boolean isOWLClass(String className);
  boolean isOWLProperty(String propertyName);
  boolean isOWLIndividual(String individualName);
  boolean isOWLIndividualOfClass(String individualName, String className);

  boolean isInjectedOWLAnonymousClass(String className);
  boolean isInjectedOWLClass(String className);
  boolean isInjectedOWLIndividual(String individualName);
  boolean isInjectedOWLAxiom(OWLAxiom axiom);

  // Mapper to non OWL storage formats
  void setMapper(Mapper mapper);
  boolean hasMapper();
  Mapper getMapper();

  OWLModel getOWLModel();

  // Convenience methods to display bridge activity
  int getNumberOfImportedSWRLRules();
  int getNumberOfImportedClasses();
  int getNumberOfImportedIndividuals();
  int getNumberOfImportedAxioms();

  int getNumberOfInferredIndividuals();
  int getNumberOfInferredAxioms();

  int getNumberOfInjectedClasses();
  int getNumberOfInjectedIndividuals();
  int getNumberOfInjectedAxioms();

  // Convenience methods to display the contents of the bridge, including inferred and created knowledge
  Set<SWRLRule> getImportedSWRLRules();

  Set<OWLClass> getImportedClasses();
  Set<OWLIndividual> getImportedIndividuals();

  Set<OWLAxiom> getImportedAxioms();

  Set<OWLIndividual> getInferredIndividuals();
  Set<OWLAxiom> getInferredAxioms();

  Set<OWLClass> getInjectedClasses();
  Set<OWLIndividual> getInjectedIndividuals();
  Set<OWLAxiom> getInjectedAxioms();

} // SWRLRuleEngineBridge
