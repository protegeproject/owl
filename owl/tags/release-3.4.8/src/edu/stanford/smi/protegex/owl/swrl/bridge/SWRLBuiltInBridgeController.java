package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLBuiltInBridgeException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;

/*
 * This interface defines methods required by a built-in bridge controller. 
 */
public interface SWRLBuiltInBridgeController 
{
	void reset() throws SWRLBuiltInBridgeException;
	
  int getNumberOfInjectedOWLClassDeclarations();
  int getNumberOfInjectedOWLIndividualDeclarations() ;
  int getNumberOfInjectedOWLAxioms();

  boolean isInjectedOWLClass(String classURI);
  boolean isInjectedOWLIndividual(String individualURI);
  boolean isInjectedOWLAxiom(OWLAxiomReference axiom);

  Set<OWLClassReference> getInjectedOWLClassDeclarations();
  Set<OWLNamedIndividualReference> getInjectedOWLIndividualDeclarations();
  Set<OWLAxiomReference> getInjectedOWLAxioms();
}
