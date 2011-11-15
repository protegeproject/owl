
package edu.stanford.smi.protegex.owl.swrl.portability;

/**
 *  TODO: This class is not yet faithful representation of an owl:someValuesFrom restriction and does not align with the OWLAPI.
 */
public interface OWLSomeValuesFromReference extends OWLRestrictionReference
{
  OWLClassReference getSomeValuesFrom();
}
