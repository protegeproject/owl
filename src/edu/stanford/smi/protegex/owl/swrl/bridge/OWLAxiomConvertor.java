package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.TargetSWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDeclarationAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDifferentIndividualsAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSameIndividualAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSubClassAxiomReference;

/**
 * Interface that defines method that must be implemented by a target rule engine to natively representing OWL axioms.
 * 
 * A default implementation that does most of the heavy lifting can be found in the DefaultOWLAxiomConvertor class.
 */
public interface OWLAxiomConvertor
{
	void generateOWLAxiom(OWLAxiomReference axiom) throws TargetSWRLRuleEngineException;
	
	void generateOWLDeclarationAxiom(OWLDeclarationAxiomReference axiom) throws TargetSWRLRuleEngineException; 
  void generateOWLDataPropertyAssertionAxiom(OWLDataPropertyAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException;
  void generateOWLObjectPropertyAssertionAxiom(OWLObjectPropertyAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException;
  void generateOWLSameIndividualAxiom(OWLSameIndividualAxiomReference axiom)	throws TargetSWRLRuleEngineException;
  void generateOWLDifferentIndividualsAxiom(OWLDifferentIndividualsAxiomReference axiom)	throws TargetSWRLRuleEngineException;
  void generateOWLClassAssertionAxiom(OWLClassAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException;
  void generateOWLSubclassAxiom(OWLSubClassAxiomReference axiom) throws TargetSWRLRuleEngineException;
  
  void generateOWLClassPropertyAssertionAxiom(OWLClassPropertyAssertionAxiomReference axiom)	throws TargetSWRLRuleEngineException;
  void generateOWLPropertyPropertyAssertionAxiom(OWLPropertyPropertyAssertionAxiomReference axiom) throws TargetSWRLRuleEngineException;
} 
