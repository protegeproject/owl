
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.ObjectValue;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.Set;

public interface OWLIndividual extends OWLPropertyValue, IndividualArgument, ObjectValue
{
  String getIndividualName();

  // TODO: temporary
  Set<String> getDefiningClassNames();
  Set<String> getDefiningSuperClassNames();
  Set<String> getDefiningEquivalentClassNames();
  Set<String> getSameAsIndividualNames();
  void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException;
} // OWLIndividual
