
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyAssertionAxiomReference;

/**
 *  This is an OWL Full entity.
 * 
 */
public interface OWLPropertyPropertyAssertionAxiomReference extends OWLPropertyAssertionAxiomReference
{
  OWLPropertyReference getObject();
} 
