
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.PropertyValue;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.Set;

/**
 ** Interface representing an instance of an OWL property
 */
public interface OWLProperty extends PropertyValue, AtomArgument // AtomArgument is for SWRL Full
{
  String getPropertyName();

  Set<String> getDomainClassNames();
  Set<String> getRangeClassNames();
  Set<String> getSuperPropertyNames();
  Set<String> getSubPropertyNames();
  Set<String> getEquivalentPropertyNames();
  Set<String> getEquivalentPropertySuperPropertyNames();
} // OWLProperty
