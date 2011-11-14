
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDeclarationAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLBuiltInAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLRuleReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.SQWRLResultImpl;

/*
 * This interface defines a processor that imports SWRL rules and SQWRL queries from an active ontology and determines the OWL axioms
 * necessary to process those rules or queries. These axioms can then be transferred to a target rule engine for processing. 
 * An implementation of this class will attempt to ensure that only necessary axioms are imported.     
 */
public interface OWLAxiomProcessor 
{ 
	void reset();
	
	void processSWRLRules() throws SWRLRuleEngineException;
	void processSQWRLQuery(String queryName) throws SWRLRuleEngineException;
	
	int getNumberOfReferencedSWRLRules();
	int getNumberOfReferencedOWLAxioms();
  int getNumberOfReferencedOWLClassDeclarationAxioms();
	int getNumberOfReferencedOWLPropertyDeclarationAxioms();
  int getNumberOfReferencedOWLIndividualDeclarationAxioms();
  
  Set<SWRLRuleReference> getReferencedSWRLRules();
  Set<OWLDeclarationAxiomReference> getReferencedOWLDeclarationAxioms();
  Set<OWLDeclarationAxiomReference> getReferencedOWLClassDeclarationsAxioms();
  Set<OWLDeclarationAxiomReference> getReferencedOWLPropertyDeclarationAxioms();
  Set<OWLDeclarationAxiomReference> getReferencedOWLIndividualDeclarationAxioms();
  Set<OWLAxiomReference> getReferencedOWLAxioms();

  boolean isReferencedOWLClass(String uri);
  boolean isReferencedOWLIndividual(String uri);
  boolean isReferencedOWLObjectProperty(String uri);
  boolean isReferencedOWLDataProperty(String uri);
	
	Set<String> getReferencedOWLClassURIs();
	Set<String> getReferencedOWLPropertyURIs();
	Set<String> getReferencedOWLIndividualURIs();
	
	Set<String> getReferencedOWLClassURIs(SWRLRuleReference ruleOrQuery);
	Set<String> getReferencedOWLPropertyURIs(SWRLRuleReference ruleOrQuery);
	Set<String> getReferencedOWLIndividualURIs(SWRLRuleReference ruleOrQuery);

  SWRLRuleReference getSWRLRule(String ruleURI) throws SWRLRuleEngineException;

  boolean isSQWRLQuery(String uri);
  SWRLRuleReference getSQWRLQuery(String queryURI) throws SQWRLException;
  Set<SWRLRuleReference> getSQWRLQueries() throws SQWRLException;
  Set<String> getSQWRLQueryNames() throws SQWRLException;

  List<SWRLBuiltInAtomReference> getBuiltInAtomsFromHead(SWRLRuleReference ruleOrQuery);
  List<SWRLBuiltInAtomReference> getBuiltInAtomsFromHead(SWRLRuleReference ruleOrQuery, Set<String> builtInNames);
  List<SWRLBuiltInAtomReference> getBuiltInAtomsFromBody(SWRLRuleReference ruleOrQuery);
  List<SWRLBuiltInAtomReference> getBuiltInAtomsFromBody(SWRLRuleReference ruleOrQuery, Set<String> builtInNames);

  SQWRLResultImpl getSQWRLResult(String uri) throws SQWRLException;
  SQWRLResultImpl getSQWRLUnpreparedResult(String uri) throws SQWRLException;
  List<SWRLAtomReference> getSQWRLPhase1BodyAtoms(SWRLRuleReference query);
  List<SWRLAtomReference> getSQWRLPhase2BodyAtoms(SWRLRuleReference query);
  boolean usesSQWRLCollections(SWRLRuleReference query);
  
  String getRuleGroupName(String uri);
  void setRuleGroupName(String uri, String ruleGroupName);

  boolean isEnabled(String uri);
  void setEnabled(String uri, boolean isEnabled);
}
