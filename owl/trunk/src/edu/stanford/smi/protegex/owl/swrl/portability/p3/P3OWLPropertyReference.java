
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.HashSet;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;

/**
 * Class representing an OWL property
 */
public abstract class P3OWLPropertyReference implements OWLPropertyReference
{
	// There is an equals method defined on this class.
	private String propertyURI;
	private Set<OWLClassReference> domainClasses, rangeClasses;
	private Set<OWLPropertyReference> superProperties, subProperties, equivalentProperties;

	public P3OWLPropertyReference(String propertyURI)
	{
		this.propertyURI = propertyURI;
		initialize();
	}

	public void addDomainClass(OWLClassReference domainClass)
	{
		this.domainClasses.add(domainClass);
	}

	public void addRangeClass(OWLClassReference rangeClass)
	{
		this.rangeClasses.add(rangeClass);
	}

	public void addSuperProperty(OWLPropertyReference superProperty)
	{
		this.superProperties.add(superProperty);
	}

	public void addSubProperty(OWLPropertyReference subProperty)
	{
		this.subProperties.add(subProperty);
	}

	public void addEquivalentProperty(OWLPropertyReference equivalentProperty)
	{
		this.equivalentProperties.add(equivalentProperty);
	}

	public String getURI()
	{
		return propertyURI;
	}

	public Set<OWLClassReference> getDomainClasses()
	{
		return domainClasses;
	}

	public Set<OWLClassReference> getRangeClasses()
	{
		return rangeClasses;
	}

	public Set<OWLPropertyReference> getSuperProperties()
	{
		return superProperties;
	}

	public Set<OWLPropertyReference> getSubProperties()
	{
		return subProperties;
	}

	public Set<OWLPropertyReference> getEquivalentProperties()
	{
		return equivalentProperties;
	}

	public Set<OWLPropertyReference> getTypes()
	{
		Set<OWLPropertyReference> types = new HashSet<OWLPropertyReference>(superProperties);
		types.add(this);

		return types;
	}

	public String toString()
	{
		return getURI();
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLPropertyReference impl = (P3OWLPropertyReference)obj;
		return (getURI() == impl.getURI() || (getURI() != null && getURI().equals(impl.getURI())));
	}

	public int hashCode()
	{
		int hash = 767;

		hash = hash + (null == getURI() ? 0 : getURI().hashCode());

		return hash;
	}

	private void initialize()
	{
		domainClasses = new HashSet<OWLClassReference>();
		rangeClasses = new HashSet<OWLClassReference>();
		superProperties = new HashSet<OWLPropertyReference>();
		subProperties = new HashSet<OWLPropertyReference>();
		equivalentProperties = new HashSet<OWLPropertyReference>();
	}

}
