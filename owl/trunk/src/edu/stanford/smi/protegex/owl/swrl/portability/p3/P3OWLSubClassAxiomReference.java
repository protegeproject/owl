
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSubClassAxiomReference;

public class P3OWLSubClassAxiomReference implements OWLSubClassAxiomReference
{
	private OWLClassReference subClass, superClass;

	public P3OWLSubClassAxiomReference(OWLClassReference subClass, OWLClassReference superClass)
	{
		this.subClass = subClass;
		this.superClass = superClass;
	}

	public OWLClassReference getSubClass()
	{
		return subClass;
	}

	public OWLClassReference getSuperClass()
	{
		return superClass;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLSubClassAxiomReference impl = (P3OWLSubClassAxiomReference)obj;
		return (super.equals((P3OWLSubClassAxiomReference)impl) && (subClass != null && impl.subClass != null && subClass.equals(impl.subClass)) && (superClass != null
				&& impl.superClass != null && superClass.equals(impl.superClass)));
	}

	public int hashCode()
	{
		int hash = 49;
		hash = hash + super.hashCode();
		hash = hash + (null == subClass ? 0 : subClass.hashCode());
		hash = hash + (null == superClass ? 0 : superClass.hashCode());
		return hash;
	}

	public String toString()
	{
		return "" + getSubClass() + " subclass of " + getSuperClass() + "";
	}
}
