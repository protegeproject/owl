
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.HashSet;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;

/**
 * Class representing an OWL individual. 
 */
public class OWLIndividualImpl implements OWLNamedIndividualReference
{
  // NOTE: equals() method defined in this class

  private String individualURI;  
  private Set<OWLClassReference> definingClasses;
  private Set<OWLNamedIndividualReference> sameAsIndividuals, differentFromIndividuals;

  public OWLIndividualImpl(String individualURI)
  {
    initialize(individualURI);
  } 

  public String getURI() { return individualURI; }
  public Set<OWLClassReference> getTypes() { return definingClasses; }
  
  public void addType(OWLClassReference definingClass) { definingClasses.add(definingClass); }
  
  public void addSameAsIndividual(OWLNamedIndividualReference sameAsIndividual) { sameAsIndividuals.add(sameAsIndividual); }
  public void addDifferentFromIndividual(OWLNamedIndividualReference differentFromIndividual) { differentFromIndividuals.add(differentFromIndividual); }

  public Set<OWLNamedIndividualReference> getSameIndividuals() { return sameAsIndividuals; }
  public Set<OWLNamedIndividualReference> getDifferentIndividuals() { return differentFromIndividuals; }

  public boolean hasType(String classURI) 
  {
    for (OWLClassReference owlClass : definingClasses) if (owlClass.getURI().equals(classURI)) return true;

    return false;
  } 

  public String toString() { return getURI(); }


  public int compareTo(Object o)
  {
    return individualURI.compareTo(((OWLIndividualImpl)o).getURI());
  }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLIndividualImpl impl = (OWLIndividualImpl)obj;
    return (getURI() == impl.getURI() || (getURI() != null && getURI().equals(impl.getURI())));
  }

  public int hashCode()
  {
    int hash = 76;
  
    hash = hash + (null == getURI() ? 0 : getURI().hashCode());
  
    return hash;
  }

  private void initialize(String individualURI)
  {
    this.individualURI = individualURI;

    definingClasses = new HashSet<OWLClassReference>();
    sameAsIndividuals = new HashSet<OWLNamedIndividualReference>();
    differentFromIndividuals = new HashSet<OWLNamedIndividualReference>();
  } 

} 
