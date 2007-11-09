
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.SWRLRuleEngine;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLQueryEngine;
import edu.stanford.smi.protegex.owl.swrl.ormap.Mapper;
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

  void defineRule(SWRLRule rule) throws SWRLRuleEngineBridgeException;
  void defineClass(OWLClass owlClass) throws SWRLRuleEngineBridgeException;
  void defineIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException;
  void defineAxiom(OWLAxiom axiom) throws SWRLRuleEngineBridgeException;

  // The infer methods can be used by rule engines to assert axioms that they infer into the bridge
  void inferPropertyAssertionAxiom(OWLPropertyAssertionAxiom owlPropertyAssertionAxiom) throws SWRLRuleEngineBridgeException;
  void inferIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException;
  
  // The create methods can be used by built-ins to assert new axioms into a bridge and also reflect them in the underlying engine
  OWLIndividual createOWLIndividual() throws SWRLRuleEngineBridgeException;
  void createOWLIndividual(OWLIndividual owlIndividual) throws SWRLRuleEngineBridgeException;
  OWLIndividual createOWLIndividual(OWLClass owlClass) throws SWRLRuleEngineBridgeException;
  void createOWLIndividuals(Set<OWLIndividual> individuals) throws SWRLRuleEngineBridgeException;

  OWLDatatypePropertyAssertionAxiom createOWLDatatypePropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLDatatypeValue object) 
    throws SWRLRuleEngineBridgeException;
  OWLDatatypePropertyAssertionAxiom createOWLDatatypePropertyAssertionAxiom(OWLDatatypePropertyAssertionAxiom axiom)
    throws SWRLRuleEngineBridgeException;
  void createOWLDatatypePropertyAssertionAxioms(Set<OWLDatatypePropertyAssertionAxiom> axioms) throws SWRLRuleEngineBridgeException;
  OWLObjectPropertyAssertionAxiom createOWLObjectPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLIndividual object) 
    throws SWRLRuleEngineBridgeException;
 OWLObjectPropertyAssertionAxiom createOWLObjectPropertyAssertionAxiom(OWLObjectPropertyAssertionAxiom axiom)
    throws SWRLRuleEngineBridgeException;
  void createOWLObjectPropertyAssertionAxioms(Set<OWLObjectPropertyAssertionAxiom> axioms) throws SWRLRuleEngineBridgeException;

  boolean isCreatedIndividual(String individualName);
  boolean isCreatedPropertyAssertionAxiom(OWLPropertyAssertionAxiom axiom);

  // Built-in invocation and argument binding 
  boolean invokeSWRLBuiltIn(String ruleName, String builtInName, int builtInIndex, List<BuiltInArgument> arguments) throws BuiltInException;
  void generateBuiltInBinding(String ruleName, String builtInName, int builtInIndex, List<BuiltInArgument> arguments) throws BuiltInException;

  // Mapper to non OWL storage formats
  void setMapper(Mapper mapper);
  boolean hasMapper();
  Mapper getMapper();

  OWLModel getOWLModel();

  // Convenience methods to display bridge activity
  int getNumberOfImportedSWRLRules();
  int getNumberOfImportedClasses();
  int getNumberOfImportedIndividuals();
  int getNumberOfImportedPropertyAssertionAxioms();
  int getNumberOfImportedAxioms();
  int getNumberOfInferredIndividuals();
  int getNumberOfInferredPropertyAssertionAxioms();
  int getNumberOfCreatedIndividuals();
  int getNumberOfCreatedPropertyAssertionAxioms();

  // Convenience methods to display the contents of the bridge, including inferred and created knowledge
  Set<SWRLRule> getImportedSWRLRules();

  Set<OWLClass> getImportedClasses();
  Set<OWLIndividual> getImportedIndividuals();

  Set<OWLPropertyAssertionAxiom> getImportedPropertyAssertionAxioms();
  Set<OWLAxiom> getImportedAxioms();

  Set<OWLIndividual> getInferredIndividuals();
  Set<OWLPropertyAssertionAxiom> getInferredPropertyAssertionAxioms();

  Set<OWLIndividual> getCreatedIndividuals();
  Set<OWLPropertyAssertionAxiom> getCreatedPropertyAssertionAxioms();

} // SWRLRuleEngineBridge
