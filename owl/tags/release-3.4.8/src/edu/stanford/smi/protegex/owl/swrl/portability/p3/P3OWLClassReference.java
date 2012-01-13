
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.HashSet;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;

public class P3OWLClassReference implements OWLClassReference
{
	// equals() method defined in this class.
	private String classURI;
	private Set<OWLClassReference> superClasses, subClasses, equivalentClasses;

	// Constructor used when creating a OWLClass object to pass as a built-in argument
	public P3OWLClassReference(String classURI)
	{
		initialize(classURI);
	}

	public void addSuperClass(OWLClassReference superclass)
	{
		superClasses.add(superclass);
	}

	public void addSubClass(OWLClassReference subClass)
	{
		subClasses.add(subClass);
	}

	public void addEquivalentClass(OWLClassReference equivalentClass)
	{
		equivalentClasses.add(equivalentClass);
	}

	public String getURI()
	{
		return classURI;
	}

	public Set<OWLClassReference> getSuperClasses()
	{
		return superClasses;
	}

	public Set<OWLClassReference> getSubClasses()
	{
		return subClasses;
	}

	public Set<OWLClassReference> getEquivalentClasses()
	{
		return equivalentClasses;
	}

	public boolean isNamedClass()
	{
		return true;
	}

	public String toString()
	{
		return getURI();
	}

	// We consider classes to be equal if they have the same name.
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLClassReference impl = (P3OWLClassReference)obj;
		return (getURI() == impl.getURI() || (getURI() != null && getURI().equals(impl.getURI())));
	}

	public int hashCode()
	{
		int hash = 12;

		hash = hash + (null == getURI() ? 0 : getURI().hashCode());

		return hash;
	}

	private void initialize(String classURI)
	{
		this.classURI = classURI;
		superClasses = new HashSet<OWLClassReference>();
		subClasses = new HashSet<OWLClassReference>();
		equivalentClasses = new HashSet<OWLClassReference>();
	}
}
