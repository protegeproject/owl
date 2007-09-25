
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;

import java.util.*;

/**
 ** Info object representing an OWL property. 
 */
public class PropertyInfo extends Info implements PropertyArgument, PropertyValue
{
  // There is an equals method defined on this class.
  private String propertyName;
  private IndividualInfo subject;
  private PropertyValueInfo predicate;
  private Set<String> domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames, equivalentPropertyNames;
  
  // Constructor used when creating a PropertyInfo object from an OWL property.
  public PropertyInfo(String propertyName, IndividualInfo subject, PropertyValueInfo predicate, Set<String> domainClassNames, 
                      Set<String> rangeClassNames, Set<String> superPropertyNames, Set<String> subPropertyNames,
                      Set<String> equivalentPropertyNames) 
    throws SWRLRuleEngineBridgeException
  {
    this.propertyName = propertyName;    
    this.subject = subject;
    this.predicate = predicate;
    this.domainClassNames = domainClassNames;
    this.rangeClassNames = rangeClassNames;
    this.superPropertyNames = superPropertyNames;
    this.subPropertyNames = subPropertyNames;
    this.equivalentPropertyNames = equivalentPropertyNames;
  } // PropertyInfo
  
  // Constructor used when creating a PropertyInfo object from an assertion made by a target rule engine or to pass as built-in arguments.
  public PropertyInfo(String propertyName, IndividualInfo subject, PropertyValueInfo predicate) 
  {
    this.propertyName = propertyName;    
    this.subject = subject;
    this.predicate = predicate;

    initialize();
  } // PropertyInfo

  // Constructors used when creating a PropertyInfo object from a property name in a rule.
  public PropertyInfo(OWLProperty property) 
  {
    this.propertyName = property.getName();    
    this.subject = null;
    this.predicate = null;

    initialize();
  } // PropertyInfo

  public PropertyInfo(String propertyName) 
  {
    this.propertyName = propertyName;    
    this.subject = null;
    this.predicate = null;

    initialize();
  } // PropertyInfo
  
  public String getPropertyName() { return propertyName; }
  public IndividualInfo getSubject() { return subject; }
  public PropertyValueInfo getPredicate() { return predicate; }
  public boolean hasSubject() { return subject != null; }
  public boolean hasPredicate() { return predicate != null; }
  public Set<String> getDomainClassNames() { return domainClassNames; }
  public Set<String> getRangeClassNames() { return rangeClassNames; }
  public Set<String> getSuperPropertyNames() { return superPropertyNames; }
  public Set<String> getSubPropertyNames() { return subPropertyNames; }
  public Set<String> getEquivalentPropertyNames() { return equivalentPropertyNames; }
  
  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    IndividualInfo individualInfo;
    OWLProperty property;
    RDFProperty firstSuperProperty = null, firstInverseSuperProperty = null, inverseProperty = null;
    OWLIndividual individual;
    Object object;
    
    if (getPropertyName().equalsIgnoreCase("sameAs")) property = (OWLProperty)owlModel.getOWLSameAsProperty();
    else if (getPropertyName().equalsIgnoreCase("differentFrom")) property = (OWLProperty)owlModel.getOWLDifferentFromProperty();
    else property = owlModel.getOWLProperty(getPropertyName());
    
    if (property == null) throw new InvalidPropertyNameException(getPropertyName());
    
    individualInfo = (IndividualInfo)subject;
    individual = owlModel.getOWLIndividual(individualInfo.getIndividualName());

    if (individual == null) throw new InvalidIndividualNameException(individualInfo.getIndividualName());

    if (property.isObjectProperty()) {
      individualInfo = (IndividualInfo)predicate;
      object = owlModel.getOWLIndividual(individualInfo.getIndividualName());
      if (object == null) throw new InvalidIndividualNameException(individualInfo.getIndividualName());
    } else { // Is a datatype property, so will be held in a LiteralInfo.
      // In Protege-OWL, RDFS literals without a selected language are stored as String objects.
      LiteralInfo literalInfo = (LiteralInfo)predicate;
      if (literalInfo.isString()) object = literalInfo.getString(); // Store strings as String objects, not RDFSLiteral objects.
      object = literalInfo.asRDFSLiteral(owlModel); // Will throw exception if it cannot convert.
    } // if    

    // We have to make sure that any super properties do not already have this value and also that any inverse properties (and their
    // superproperties) do not have this value.

    firstSuperProperty = property.getFirstSuperproperty();
    if (property.isInverseFunctional()) inverseProperty = property.getInverseProperty();
    firstInverseSuperProperty = (inverseProperty == null) ? null : inverseProperty.getFirstSuperproperty();

    if (!((firstSuperProperty != null && individual.hasPropertyValue(firstSuperProperty, object, true)) ||
          (firstInverseSuperProperty != null && ((OWLIndividual)object).hasPropertyValue(firstInverseSuperProperty, individual, true)) ||
          individual.hasPropertyValue(property, object, true)))
      individual.addPropertyValue(property, object);    
  } // writeAssertedProperty2OWL

  public String toString() { return getPropertyName(); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    PropertyInfo info = (PropertyInfo)obj;
    return (getPropertyName() == info.getPropertyName() || (getPropertyName() != null && getPropertyName().equals(info.getPropertyName()))) && 
      (subject == info.subject || (subject != null && subject.equals(info.subject))) &&
      (predicate == info.predicate || (predicate != null && predicate.equals(info.predicate))) &&
      (domainClassNames == info.domainClassNames || (domainClassNames != null && domainClassNames.equals(info.domainClassNames))) &&
      (rangeClassNames == info.rangeClassNames || (rangeClassNames != null && rangeClassNames.equals(info.rangeClassNames))) &&
      (equivalentPropertyNames == info.equivalentPropertyNames || (equivalentPropertyNames != null && equivalentPropertyNames.equals(info.equivalentPropertyNames)));
  } // equals

  public int hashCode()
  {
    int hash = 767;
    hash = hash + (null == getPropertyName() ? 0 : getPropertyName().hashCode());
    hash = hash + (null == subject ? 0 : subject.hashCode());
    hash = hash + (null == predicate ? 0 : predicate.hashCode());
    hash = hash + (null == domainClassNames ? 0 : domainClassNames.hashCode());
    hash = hash + (null == rangeClassNames ? 0 : rangeClassNames.hashCode());
    hash = hash + (null == equivalentPropertyNames ? 0 : equivalentPropertyNames.hashCode());
    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return propertyName.compareTo(((PropertyInfo)o).getPropertyName());
  } // compareTo

  // Utility method to create a collection of PropertyInfo objects for every subject/predicate combination for a particular OWL property.
  // TODO: This is incredibly inefficient. 

  public static List<PropertyInfo> buildPropertyInfoList(OWLModel owlModel, String propertyName) throws SWRLRuleEngineBridgeException
  {
    OWLProperty property;
    PropertyInfo propertyInfo;
    List<PropertyInfo> propertyInfoList = new ArrayList<PropertyInfo>();
    Set<String> domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames, equivalentPropertyNames;
    IndividualInfo subject;
    PropertyValueInfo predicate;

    property = owlModel.getOWLProperty(propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);

    domainClassNames = SWRLOWLUtil.rdfResources2Names(property.getUnionDomain(true));
    rangeClassNames = SWRLOWLUtil.rdfResources2Names(property.getUnionRangeClasses());
    superPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getSuperproperties(true));
    subPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getSubproperties(true));
    equivalentPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getEquivalentProperties());


    Iterator domainsIterator = property.getUnionDomain(true).iterator();
    while (domainsIterator.hasNext()) {
      Object object1 = domainsIterator.next();
      if (!(object1 instanceof RDFSClass)) continue; // Should only return RDFResource object, but...
      RDFSClass rdfsClass = (RDFSClass)object1;
        
      Iterator individualsIterator = rdfsClass.getInstances(true).iterator();
      while (individualsIterator.hasNext()) {
        Object object2 = individualsIterator.next();          
        if (!(object2 instanceof OWLIndividual)) continue; // Deal only with OWL individuals (could return metaclass, for example)
        OWLIndividual domainIndividual = (OWLIndividual)object2;
        
        if (domainIndividual.hasPropertyValue(property)) {
          
          if (property.hasObjectRange()) {
            Iterator individualValuesIterator = domainIndividual.getPropertyValues(property).iterator();
            while (individualValuesIterator.hasNext()) {
              RDFResource resource = (RDFResource)individualValuesIterator.next();
              if (resource instanceof OWLIndividual) {
                OWLIndividual rangeIndividual = (OWLIndividual)resource;
                subject = new IndividualInfo(domainIndividual.getName());
                predicate = new IndividualInfo(rangeIndividual.getName());
                propertyInfo = new PropertyInfo(propertyName, subject, predicate, domainClassNames, rangeClassNames, 
                                                superPropertyNames, subPropertyNames, equivalentPropertyNames);
                propertyInfoList.add(propertyInfo);
              } else {
                //System.err.println("Unknown property value resource: " + resource); // TODO: Orphan resources in OWL file. Ignore?
              } // if
            } // while
          } else { // DatatypeProperty
            Iterator literalsIterator = domainIndividual.getPropertyValueLiterals(property).iterator();
            while (literalsIterator.hasNext()) {
              RDFSLiteral literal = (RDFSLiteral)literalsIterator.next();
              subject = new IndividualInfo(domainIndividual.getName());
              predicate = new LiteralInfo(owlModel, literal);
              propertyInfo = new PropertyInfo(propertyName, subject, predicate, domainClassNames, rangeClassNames, 
                                              superPropertyNames, subPropertyNames, equivalentPropertyNames);
              propertyInfoList.add(propertyInfo);
            } // while
          } // if
        } // if
      } // while
    } // while
    
    return propertyInfoList;
  } // buildPropertyInfoList

  private void initialize()
  {
    domainClassNames = new HashSet<String>();
    rangeClassNames = new HashSet<String>();
    superPropertyNames = new HashSet<String>();
    subPropertyNames = new HashSet<String>();
    equivalentPropertyNames = new HashSet<String>();
  } // initialize

} // PropertyInfo
