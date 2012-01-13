
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.MapperException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;

public interface Mapper
{
  boolean isMapped(OWLClassReference owlClass);
  boolean isMapped(OWLPropertyReference owlProperty);

  void open() throws MapperException;
  void close() throws MapperException;

  Set<OWLNamedIndividualReference> mapOWLClass(OWLClassReference owlClass) throws MapperException;
  Set<OWLNamedIndividualReference> mapOWLClass(OWLClassReference owlClass, OWLNamedIndividualReference owlIndividual) throws MapperException;

  Set<OWLDataPropertyAssertionAxiomReference> mapOWLDataProperty(OWLPropertyReference owlProperty) throws MapperException;
  Set<OWLDataPropertyAssertionAxiomReference> mapOWLDataProperty(OWLPropertyReference owlProperty, OWLNamedIndividualReference subject) throws MapperException;
  Set<OWLDataPropertyAssertionAxiomReference> mapOWLDataProperty(OWLPropertyReference owlProperty, OWLDataValue value) throws MapperException;
  Set<OWLDataPropertyAssertionAxiomReference> mapOWLDataProperty(OWLPropertyReference owlProperty, OWLNamedIndividualReference subject, OWLDataValue value) 
    throws MapperException;

  Set<OWLObjectPropertyAssertionAxiomReference> mapOWLObjectProperty(OWLPropertyReference owlProperty) throws MapperException;
  Set<OWLObjectPropertyAssertionAxiomReference> mapOWLObjectProperty(OWLPropertyReference owlProperty, OWLNamedIndividualReference subject) throws MapperException;
  Set<OWLObjectPropertyAssertionAxiomReference> mapOWLObjectProperty(OWLPropertyReference owlProperty, OWLNamedIndividualReference subject, OWLNamedIndividualReference object) 
    throws MapperException;
} // Mapper
