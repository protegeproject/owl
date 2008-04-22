
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;

import java.util.*;

/**
 ** Class representing an OWL property
 */
public abstract class OWLPropertyImpl extends BuiltInArgumentImpl implements OWLProperty
{
  // There is an equals method defined on this class.
  private String propertyName;
  private Set<String> domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames, 
    equivalentPropertyNames, equivalentPropertySuperPropertyNames;
  
  public OWLPropertyImpl(OWLModel owlModel, String propertyName) throws OWLFactoryException
  {
    edu.stanford.smi.protegex.owl.model.OWLProperty property;

    this.propertyName = propertyName;

    property = SWRLOWLUtil.getOWLProperty(owlModel, propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);

    domainClassNames = SWRLOWLUtil.rdfResources2Names(property.getUnionDomain(true));
    rangeClassNames = SWRLOWLUtil.rdfResources2Names(property.getUnionRangeClasses());
    superPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getSuperproperties(true));
    subPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getSubproperties(true));
    equivalentPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getEquivalentProperties());
    equivalentPropertySuperPropertyNames = new HashSet<String>();

    for (String equivalentPropertyName : equivalentPropertyNames) {
      RDFProperty equivalentProperty = SWRLOWLUtil.getOWLProperty(owlModel, equivalentPropertyName);
      Iterator equivalentPropertySuperPropertiesIterator = equivalentProperty.getSuperproperties(true).iterator();
      while (equivalentPropertySuperPropertiesIterator.hasNext()) {
        RDFProperty equivalentPropertySuperProperty = (RDFProperty)equivalentPropertySuperPropertiesIterator.next();
        equivalentPropertySuperPropertyNames.add(equivalentPropertySuperProperty.getName());
      } /// while
    } // for
  } // OWLPropertyImpl

  // Constructor used when creating a OWLPropertyImpl object to pass as a built-in argument
  public OWLPropertyImpl(String propertyName) 
  {
    this.propertyName = propertyName;
    initialize();
  } // OWLPropertyImpl
  
  public String getPropertyName() { return propertyName; }
  public Set<String> getDomainClassNames() { return domainClassNames; }
  public Set<String> getRangeClassNames() { return rangeClassNames; }
  public Set<String> getSuperPropertyNames() { return superPropertyNames; }
  public Set<String> getSubPropertyNames() { return subPropertyNames; }
  public Set<String> getEquivalentPropertyNames() { return equivalentPropertyNames; }
  public Set<String> getEquivalentPropertySuperPropertyNames() { return equivalentPropertySuperPropertyNames; }
  
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
      (equivalentPropertyNames == impl.equivalentPropertyNames || (equivalentPropertyNames != null && equivalentPropertyNames.equals(impl.equivalentPropertyNames))) &&
      (equivalentPropertySuperPropertyNames == impl.equivalentPropertySuperPropertyNames || (equivalentPropertySuperPropertyNames != null && equivalentPropertySuperPropertyNames.equals(impl.equivalentPropertySuperPropertyNames)));
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
    hash = hash + (null == equivalentPropertySuperPropertyNames ? 0 : equivalentPropertySuperPropertyNames.hashCode());
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
    edu.stanford.smi.protegex.owl.model.OWLProperty property = SWRLOWLUtil.getOWLProperty(owlModel, propertyName);
    OWLPropertyAssertionAxiom axiom;
    RDFResource subject;

    if (property == null) throw new InvalidPropertyNameException(propertyName);

    TripleStoreModel tsm = owlModel.getTripleStoreModel();
    Iterator<RDFResource> it = tsm.listSubjects(property);
    while (it.hasNext()) {
      subject = it.next();
      if (!(subject instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)) continue; // Deal only with OWL individuals (could return metaclass, for example)
      edu.stanford.smi.protegex.owl.model.OWLIndividual subjectIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)subject;
      
      for (Object object : subject.getPropertyValues(property)) {
        
        if (property.hasObjectRange()) { // Object property
          if (object instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
            edu.stanford.smi.protegex.owl.model.OWLIndividual objectIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)object;
            OWLIndividual subjectOWLIndividual = OWLFactory.createOWLIndividual(owlModel, subjectIndividual.getName());
            OWLIndividual objectOWLIndividual = OWLFactory.createOWLIndividual(owlModel, objectIndividual.getName());
            axiom = OWLFactory.createOWLObjectPropertyAssertionAxiom(subjectOWLIndividual, OWLFactory.createOWLObjectProperty(owlModel, propertyName), objectOWLIndividual);
            propertyAssertions.add(axiom);
          } else if (object instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) { // This will be OWL Full
            edu.stanford.smi.protegex.owl.model.OWLNamedClass objectClass = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)object;
            OWLIndividual subjectOWLIndividual = OWLFactory.createOWLIndividual(owlModel, subjectIndividual.getName());
            OWLClass objectOWLClass = OWLFactory.createOWLClass(owlModel, objectClass.getName());
            axiom = OWLFactory.createOWLClassPropertyAssertionAxiom(subjectOWLIndividual, OWLFactory.createOWLObjectProperty(owlModel, propertyName), objectOWLClass);
            propertyAssertions.add(axiom);
          } else if (object instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) { // This will be OWL Full
            edu.stanford.smi.protegex.owl.model.OWLProperty objectProperty = (edu.stanford.smi.protegex.owl.model.OWLProperty)object;
            OWLIndividual subjectOWLIndividual = OWLFactory.createOWLIndividual(owlModel, subjectIndividual.getName());
            OWLProperty objectOWLProperty;
            if (objectProperty.isObjectProperty()) objectOWLProperty = OWLFactory.createOWLObjectProperty(owlModel, objectProperty.getName());
            else objectOWLProperty = OWLFactory.createOWLDatatypeProperty(owlModel, objectProperty.getName());
            axiom = OWLFactory.createOWLPropertyPropertyAssertionAxiom(subjectOWLIndividual, OWLFactory.createOWLObjectProperty(owlModel, propertyName), objectOWLProperty);
            propertyAssertions.add(axiom);                
          } // if
        } else { // DatatypeProperty
          OWLIndividual subjectOWLIndividual = OWLFactory.createOWLIndividual(owlModel, subjectIndividual.getName());
          RDFSLiteral literal = owlModel.asRDFSLiteral(object);
          OWLDatatypeValue objectOWLDatatypeValue = OWLFactory.createOWLDatatypeValue(owlModel, literal);
          axiom = OWLFactory.createOWLDatatypePropertyAssertionAxiom(subjectOWLIndividual, OWLFactory.createOWLDatatypeProperty(owlModel, propertyName), objectOWLDatatypeValue);
          propertyAssertions.add(axiom);
        } // if
      } // for
    } // while
      
    return propertyAssertions;
  } // buildOWLPropertyAssertionAxioms

  private void initialize()
  {
    domainClassNames = new HashSet<String>();
    rangeClassNames = new HashSet<String>();
    superPropertyNames = new HashSet<String>();
    subPropertyNames = new HashSet<String>();
    equivalentPropertyNames = new HashSet<String>();
    equivalentPropertySuperPropertyNames = new HashSet<String>();
  } // initialize

} // OWLPropertyImpl
