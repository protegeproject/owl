
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSomeValuesFromReference;

public class P3OWLSomeValuesFromReference extends P3OWLRestrictionReference implements OWLSomeValuesFromReference
{
	private OWLClassReference someValuesFrom;

	public P3OWLSomeValuesFromReference(OWLClassReference owlClass, OWLPropertyReference onProperty, OWLClassReference someValuesFrom)
	{
		super(owlClass, onProperty);
		this.someValuesFrom = someValuesFrom;
	}

	public OWLClassReference getSomeValuesFrom()
	{
		return someValuesFrom;
	}

	public String toString()
	{
		return "someValuesFrom(" + asOWLClass().getURI() + ", " + getProperty().getURI() + ", " + getSomeValuesFrom().getURI() + ")";
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLSomeValuesFromReference impl = (P3OWLSomeValuesFromReference)obj;
		return (asOWLClass().getURI() == impl.asOWLClass().getURI() || (asOWLClass().getURI() != null && asOWLClass().getURI().equals(impl.asOWLClass().getURI())))
				&& (getProperty().getURI() == impl.getProperty().getURI() || (getProperty().getURI() != null && getProperty().getURI().equals(
						impl.getProperty().getURI())))
				&& (getSomeValuesFrom().getURI() == impl.getSomeValuesFrom().getURI() || (getSomeValuesFrom().getURI() != null && getSomeValuesFrom().getURI().equals(
						impl.getSomeValuesFrom().getURI())));
	}

	public int hashCode()
	{
		int hash = 232;

		hash = hash + (null == asOWLClass().getURI() ? 0 : asOWLClass().getURI().hashCode());
		hash = hash + (null == getProperty().getURI() ? 0 : getProperty().getURI().hashCode());
		hash = hash + (null == getSomeValuesFrom().getURI() ? 0 : getSomeValuesFrom().getURI().hashCode());

		return hash;
	}

}
