
package edu.stanford.smi.protegex.owl.swrl.ormap;

import edu.stanford.smi.protegex.owl.swrl.ormap.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;

import java.util.*;

public interface Mapper
{
  boolean isMappedClass(OWLClass owlClass);
  boolean isMappedProperty(OWLProperty owlProperty);

  Set<OWLIndividual> getMappedIndividuals(OWLClass owlClass) throws MapperException;
  Set<OWLPropertyAssertionAxiom> getMappedProperties(OWLProperty owlProperty) throws MapperException;
} // Mapper
