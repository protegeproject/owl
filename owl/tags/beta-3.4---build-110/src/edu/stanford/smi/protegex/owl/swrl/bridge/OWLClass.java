
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.ClassValue;

import java.util.Set;

// A named OWL class

public interface OWLClass extends ClassArgument, ClassValue
{
  String getClassName();

  Set<String> getDirectSuperClassNames();
  Set<String> getDirectSubClassNames();
  Set<String> getEquivalentClassNames();
} // OWLClass
