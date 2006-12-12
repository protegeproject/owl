
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidPropertyNameException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidIndividualNameException;

import java.util.*;

/*
** Info object representing an OWL property. 
*/
public class PropertyInfo extends Info
{
  // There is an equals method defined on this class.
  private String propertyName;
  private Argument subject, predicate;
  private Set<String> domainClassNames, rangeClassNames;
  
  // Constructor used when creating a PropertyInfo object from an OWL property.
  public PropertyInfo(String propertyName, Argument subject, Argument predicate, 
		      Set<String> domainClassNames, Set<String> rangeClassNames) 
    throws SWRLRuleEngineBridgeException
  {
    this.propertyName = propertyName;    
    this.subject= subject;
    this.predicate = predicate;
    this.domainClassNames = domainClassNames;
    this.rangeClassNames = rangeClassNames;
  } // PropertyInfo
  
  // Constructor used when creating a PropertyInfo object from an assertion made by a target rule engine. 
  public PropertyInfo(String propertyName, Argument subject, Argument predicate) throws SWRLRuleEngineBridgeException
  {
    this.propertyName = propertyName;    

    this.subject = subject;
    this.predicate = predicate;
  } // PropertyInfo
  
  public String getPropertyName() { return propertyName; }
  public Argument getSubject() { return subject; }
  public Argument getPredicate() { return predicate; }
  public Set<String> getDomainClassNames() { return domainClassNames; }
  public Set<String> getRangeClassNames() { return rangeClassNames; }
  
  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    IndividualInfo individualInfo;
    OWLProperty property;
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
      // In Protege-OWL RDFS literals without a selected language are stored as String objects.
      LiteralInfo literalInfo = (LiteralInfo)predicate;
      if (literalInfo.isString()) object = literalInfo.getString();
      else object = owlModel.asRDFSLiteral(literalInfo.getString());
    } // if    

    if (!individual.hasPropertyValue(property, object, true)) individual.addPropertyValue(property, object);
  } // writeAssertedProperty2OWL

  public String toString()
  {
    return "Property(name: " + getPropertyName() + ", subject: " + subject + ", predicate: " + predicate + ", domainClassNames: " + 
      domainClassNames + ", rangeClassNames: " + rangeClassNames + ")";
  } // toString

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    PropertyInfo info = (PropertyInfo)obj;
    return (getPropertyName() == info.getPropertyName() || (getPropertyName() != null && getPropertyName().equals(info.getPropertyName()))) && 
      (subject == info.subject || (subject != null && subject.equals(info.subject))) &&
      (predicate == info.predicate || (predicate != null && predicate.equals(info.predicate))) &&
      (domainClassNames == info.domainClassNames || (domainClassNames != null && domainClassNames.equals(info.domainClassNames))) &&
      (rangeClassNames == info.rangeClassNames || (rangeClassNames != null && rangeClassNames.equals(info.rangeClassNames)));
  } // equals

  public int hashCode()
  {
    int hash = 767;
    hash = hash + (null == getPropertyName() ? 0 : getPropertyName().hashCode());
    hash = hash + (null == subject ? 0 : subject.hashCode());
    hash = hash + (null == predicate ? 0 : predicate.hashCode());
    hash = hash + (null == domainClassNames ? 0 : domainClassNames.hashCode());
    hash = hash + (null == rangeClassNames ? 0 : rangeClassNames.hashCode());
    return hash;
  } // hashCode

  // Utility method to create a collection of PropertyInfo objects for every subject/predicate combination for a particular OWL property.
  // TODO: This is incredibly inefficient. Need to add a method to the OWLModel to get individuals with a particular property.

  public static List<PropertyInfo> buildPropertyInfoList(OWLModel owlModel, String propertyName) throws SWRLRuleEngineBridgeException
  {
    RDFProperty property;
    PropertyInfo propertyInfo;
    List<PropertyInfo> propertyInfoList = new ArrayList<PropertyInfo>();
    Set<String> domainClassNames, rangeClassNames;
    Argument subject, predicate;

    property = owlModel.getRDFProperty(propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);
    
    domainClassNames = rdfResources2Names(property.getUnionDomain());
    rangeClassNames = rdfResources2Names(property.getUnionRangeClasses());

    Iterator domainsIterator = property.getUnionDomain().iterator();
    while (domainsIterator.hasNext()) {
      RDFSClass rdfsClass = (RDFSClass)domainsIterator.next();
        
      Iterator individualsIterator = rdfsClass.getInstances(true).iterator();
      while (individualsIterator.hasNext()) {
        Object object = individualsIterator.next();
        
        if (!(object instanceof OWLIndividual)) continue; // Deal only with OWL individuals (could return metaclass, for example)
        OWLIndividual domainIndividual = (OWLIndividual)object;
        
          if (domainIndividual.hasPropertyValue(property)) {
            if (property.hasObjectRange()) {
              Iterator individualValuesIterator = domainIndividual.getPropertyValues(property).iterator();
              while (individualValuesIterator.hasNext()) {
                OWLIndividual rangeIndividual = (OWLIndividual)individualValuesIterator.next();
                subject = new IndividualInfo(domainIndividual.getName());
                predicate = new IndividualInfo(rangeIndividual.getName());
                propertyInfo = new PropertyInfo(propertyName, subject, predicate, domainClassNames, rangeClassNames);
                propertyInfoList.add(propertyInfo);
              } // while
            } else { // DatatypeProperty
              Iterator literalsIterator = domainIndividual.getPropertyValueLiterals(property).iterator();
              while (literalsIterator.hasNext()) {
                RDFSLiteral literal = (RDFSLiteral)literalsIterator.next();
                subject = new IndividualInfo(domainIndividual.getName());
                predicate = new LiteralInfo(owlModel, literal);
                propertyInfo = new PropertyInfo(propertyName, subject, predicate, domainClassNames, rangeClassNames);
                propertyInfoList.add(propertyInfo);
              } // while
            } // if
          } // if
      } // while
    } // while
    
    return propertyInfoList;
  } // buildPropertyInfoList

} // PropertyInfo
