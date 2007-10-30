
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;

import java.util.*;
import java.io.Serializable;

/**
 ** Class representing an OWL property
 */
public abstract class OWLPropertyImpl extends BuiltInArgumentImpl implements OWLProperty, Serializable
{
  // There is an equals method defined on this class.
  private String propertyName;
  private Set<String> domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames, equivalentPropertyNames;
  
  // Constructor used when creating a OWLPropertyImpl object from an OWL property
  public OWLPropertyImpl(String propertyName, Set<String> domainClassNames, 
                         Set<String> rangeClassNames, Set<String> superPropertyNames, Set<String> subPropertyNames,
                         Set<String> equivalentPropertyNames) 
  {
    this.propertyName = propertyName;    
    this.domainClassNames = domainClassNames;
    this.rangeClassNames = rangeClassNames;
    this.superPropertyNames = superPropertyNames;
    this.subPropertyNames = subPropertyNames;
    this.equivalentPropertyNames = equivalentPropertyNames;
  } // OWLPropertyImpl
  
  public OWLPropertyImpl(String propertyName) 
  {
    initialize(propertyName);
  } // OWLPropertyImpl

  public OWLPropertyImpl() // For serialization
  {
    initialize("");
  } // OWLPropertyImpl
  
  public String getPropertyName() { return propertyName; }
  public Set<String> getDomainClassNames() { return domainClassNames; }
  public Set<String> getRangeClassNames() { return rangeClassNames; }
  public Set<String> getSuperPropertyNames() { return superPropertyNames; }
  public Set<String> getSubPropertyNames() { return subPropertyNames; }
  public Set<String> getEquivalentPropertyNames() { return equivalentPropertyNames; }
  
  public String getRepresentation() { return getPropertyName(); }

  public String toString() { return getPropertyName(); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLPropertyImpl impl = (OWLPropertyImpl)obj;
    return (getPropertyName() == impl.getPropertyName() || (getPropertyName() != null && getPropertyName().equals(impl.getPropertyName()))) && 
      (domainClassNames == impl.domainClassNames || (domainClassNames != null && domainClassNames.equals(impl.domainClassNames))) &&
      (rangeClassNames == impl.rangeClassNames || (rangeClassNames != null && rangeClassNames.equals(impl.rangeClassNames))) &&
      (subPropertyNames == impl.subPropertyNames || (subPropertyNames != null && subPropertyNames.equals(impl.subPropertyNames))) &&
      (superPropertyNames == impl.superPropertyNames || (superPropertyNames != null && superPropertyNames.equals(impl.superPropertyNames))) &&
      (equivalentPropertyNames == impl.equivalentPropertyNames || (equivalentPropertyNames != null && equivalentPropertyNames.equals(impl.equivalentPropertyNames)));
  } // equals

  public int hashCode()
  {
    int hash = 767;
    hash = hash + (null == getPropertyName() ? 0 : getPropertyName().hashCode());
    hash = hash + (null == domainClassNames ? 0 : domainClassNames.hashCode());
    hash = hash + (null == rangeClassNames ? 0 : rangeClassNames.hashCode());
    hash = hash + (null == subPropertyNames ? 0 : subPropertyNames.hashCode());
    hash = hash + (null == superPropertyNames ? 0 : superPropertyNames.hashCode());
    hash = hash + (null == equivalentPropertyNames ? 0 : equivalentPropertyNames.hashCode());
    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return propertyName.compareTo(((OWLPropertyImpl)o).getPropertyName());
  } // compareTo

  // Utility method to create a collection of OWLProperty objects for every subject/predicate combination for a particular OWL property.
  // TODO: This is incredibly inefficient. 

  public static Set<OWLPropertyAssertionAxiom> buildOWLPropertyAssertionAxioms(OWLModel owlModel, String propertyName) throws OWLFactoryException, DatatypeConversionException
  {
    Set<OWLPropertyAssertionAxiom> propertyAssertions = new HashSet<OWLPropertyAssertionAxiom>();
    Set<String> domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames, equivalentPropertyNames;
    edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyName);

    if (property == null) throw new InvalidPropertyNameException(propertyName);

    domainClassNames = SWRLOWLUtil.rdfResources2Names(property.getUnionDomain(true));
    rangeClassNames = SWRLOWLUtil.rdfResources2Names(property.getUnionRangeClasses());
    superPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getSuperproperties(true));
    subPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getSubproperties(true));
    equivalentPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getEquivalentProperties());

    Iterator domainsIterator = property.getUnionDomain(true).iterator();
    while (domainsIterator.hasNext()) {
      Object domain = domainsIterator.next();
      if (!(domain instanceof RDFSClass)) continue; // Should only return RDFResource object, but...
      RDFSClass rdfsClass = (RDFSClass)domain;
        
      Iterator individualsIterator = rdfsClass.getInstances(true).iterator();
      while (individualsIterator.hasNext()) {
        Object object2 = individualsIterator.next();          
        if (!(object2 instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)) continue; // Deal only with OWL individuals (could return metaclass, for example)
        edu.stanford.smi.protegex.owl.model.OWLIndividual domainIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object2;
        
        if (domainIndividual.hasPropertyValue(property)) {
          
          if (property.hasObjectRange()) {
            Iterator individualValuesIterator = domainIndividual.getPropertyValues(property).iterator();
            while (individualValuesIterator.hasNext()) {
              RDFResource resource = (RDFResource)individualValuesIterator.next();
              if (resource instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
                edu.stanford.smi.protegex.owl.model.OWLIndividual rangeIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)resource;
                OWLIndividual subject = OWLFactory.createOWLIndividual(domainIndividual.getName());
                OWLIndividual object = OWLFactory.createOWLIndividual(rangeIndividual.getName());
                OWLPropertyAssertionAxiom axiom = OWLFactory.createOWLObjectPropertyAssertionAxiom(subject, OWLFactory.createOWLObjectProperty(propertyName), object);
                propertyAssertions.add(axiom);
              } else {
                //System.err.println("Unknown property value resource: " + resource); // TODO: Orphan resources in OWL file. Ignore?
              } // if
            } // while
          } else { // DatatypeProperty
            Iterator literalsIterator = domainIndividual.getPropertyValueLiterals(property).iterator();
            while (literalsIterator.hasNext()) {
              RDFSLiteral literal = (RDFSLiteral)literalsIterator.next();
              OWLIndividual subject = OWLFactory.createOWLIndividual(domainIndividual.getName());
              OWLDatatypeValue object = OWLFactory.createOWLDatatypeValue(owlModel, literal);
              OWLPropertyAssertionAxiom axiom = OWLFactory.createOWLDatatypePropertyAssertionAxiom(subject, OWLFactory.createOWLDatatypeProperty(propertyName), object);
              propertyAssertions.add(axiom);
            } // while
          } // if
        } // if
      } // while
    } // while
    
    return propertyAssertions;
  } // buildOWLPropertyAssertionAxioms

  private void initialize(String propertyName)
  {
    this.propertyName = propertyName;

    domainClassNames = new HashSet<String>();
    rangeClassNames = new HashSet<String>();
    superPropertyNames = new HashSet<String>();
    subPropertyNames = new HashSet<String>();
    equivalentPropertyNames = new HashSet<String>();
  } // initialize

} // OWLPropertyImpl
