
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;

public interface Mapper
{
  boolean isMapped(OWLClass owlClass);
  boolean isMapped(OWLProperty owlProperty);

  void open() throws MapperException;
  void close() throws MapperException;

  Set<OWLIndividual> mapOWLClass(OWLClass owlClass) throws MapperException;
  Set<OWLIndividual> mapOWLClass(OWLClass owlClass, OWLIndividual owlIndividual) throws MapperException;

  Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty) throws MapperException;
  Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty, OWLIndividual subject) throws MapperException;
  Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty, OWLDatatypeValue value) throws MapperException;
  Set<OWLDatatypePropertyAssertionAxiom> mapOWLDatatypeProperty(OWLProperty owlProperty, OWLIndividual subject, OWLDatatypeValue value) 
    throws MapperException;

  Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty) throws MapperException;
  Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty, OWLIndividual subject) throws MapperException;
  Set<OWLObjectPropertyAssertionAxiom> mapOWLObjectProperty(OWLProperty owlProperty, OWLIndividual subject, OWLIndividual object) 
    throws MapperException;
} // Mapper
