
package edu.stanford.smi.protegex.owl.swrl.ormap;

import edu.stanford.smi.protegex.owl.swrl.ormap.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;

import java.util.*;

public interface Mapper
{
  boolean isMapped(OWLClass owlClass);
  boolean isMapped(OWLProperty owlProperty);

  Set<OWLIndividual> mapOWLClass(OWLClass owlClass) throws MapperException;
  Set<OWLIndividual> mapOWLClass(OWLClass owlClass, OWLIndividual owlIndividual) throws MapperException;

  Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty) throws MapperException;
  Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty, OWLIndividual subject) throws MapperException;
  Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty, OWLIndividual subject, OWLDatatypeValue value) 
    throws MapperException;

  Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty) throws MapperException;
  Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty, OWLIndividual subject) throws MapperException;
  Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty, OWLIndividual subject, OWLIndividual object) 
    throws MapperException;

  void addMap(OWLClassMap classMap);
  void addMap(OWLObjectPropertyMap objectPropertyMap);
  void addMap(OWLDatatypePropertyMap datatypePropertyMap);
} // Mapper
