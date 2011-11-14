
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDeclarationAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLEntityReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyReference;

public class P3OWLDeclarationAxiomReference implements OWLDeclarationAxiomReference
{
	private OWLEntityReference owlEntity;

	public P3OWLDeclarationAxiomReference(OWLEntityReference owlEntity)
	{
		this.owlEntity = owlEntity;
	}

	public OWLEntityReference getEntity()
	{
		return owlEntity;
	}

	public String toString()
	{
		String uri = owlEntity.getURI();

		if (owlEntity instanceof OWLClassReference)
			return "owl:Class(" + uri + ")";
		else if (owlEntity instanceof OWLNamedIndividualReference)
			return "owl:Individual(" + uri + ")";
		else if (owlEntity instanceof OWLObjectPropertyReference)
			return "owl:ObjectProperty(" + uri + ")";
		else if (owlEntity instanceof OWLDataPropertyReference)
			return "owl:DataProperty(" + uri + ")";
		else
			return "UNKNOWN_DECLARATION_TYPE(" + uri + ")";
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLDeclarationAxiomReference impl = (P3OWLDeclarationAxiomReference)obj;
		return (super.equals((P3OWLDeclarationAxiomReference)impl) && (owlEntity != null && impl.owlEntity != null && owlEntity.equals(impl.owlEntity)));
	}

	public int hashCode()
	{
		int hash = 42;
		hash = hash + super.hashCode();
		hash = hash + (null == owlEntity ? 0 : owlEntity.hashCode());
		return hash;
	}

}
