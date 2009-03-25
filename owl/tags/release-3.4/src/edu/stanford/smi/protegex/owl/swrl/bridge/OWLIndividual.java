
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.ObjectValue;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.Set;

public interface OWLIndividual extends OWLObject, OWLPropertyValue, IndividualArgument, ObjectValue
{
  String getIndividualName();
  String getPrefixedIndividualName();

  Set<OWLClass> getDefiningClasses();
  Set<OWLClass> getDefiningSuperclasses();
  Set<OWLClass> getDefiningEquivalentClasses();
  Set<OWLClass> getDefiningEquivalentClassSuperclasses();
  Set<OWLIndividual> getSameAsIndividuals();
} // OWLIndividual
