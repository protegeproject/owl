
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;

/**
 ** Info object representing an OWL property. 
 */
public class PropertyInfo extends Info implements Argument
{
  // There is an equals method defined on this class.
  private String propertyName;
  private Argument subject, predicate;
  private Set<String> domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames;
  
  // Constructor used when creating a PropertyInfo object from an OWL property.
  public PropertyInfo(String propertyName, Argument subject, Argument predicate, Set<String> domainClassNames, 
                      Set<String> rangeClassNames, Set<String> superPropertyNames, Set<String> subPropertyNames) 
    throws SWRLRuleEngineBridgeException
  {
    this.propertyName = propertyName;    
    this.subject = subject;
    this.predicate = predicate;
    initialize();
  } // PropertyInfo
  
  // Constructor used when creating a PropertyInfo object from an assertion made by a target rule engine or to pass as built-in arguments.
  public PropertyInfo(String propertyName, Argument subject, Argument predicate) 
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
  public Argument getSubject() { return subject; }
  public Argument getPredicate() { return predicate; }
  public boolean hasSubject() { return subject != null; }
  public boolean hasPredicate() { return predicate != null; }
  public Set<String> getDomainClassNames() { return domainClassNames; }
  public Set<String> getRangeClassNames() { return rangeClassNames; }
  public Set<String> getSuperPropertyNames() { return superPropertyNames; }
  public Set<String> getSubPropertyNames() { return subPropertyNames; }
  
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
      if (literalInfo.isString()) object = literalInfo.getString(); // Store strings as String objects, not RDFSLiteral objects.
      object = literalInfo.asRDFSLiteral(owlModel); // Will throw exception if it cannot convert.
    } // if    

    if (!individual.hasPropertyValue(property, object, true)) individual.addPropertyValue(property, object);
  } // writeAssertedProperty2OWL

  public String toString()
  {
    return "Property(name: " + getPropertyName() + ", subject: " + subject + ", predicate: " + predicate + ", domainClassNames: " + 
      domainClassNames + ", rangeClassNames: " + rangeClassNames + ", superPropertyNames: " + superPropertyNames + ")";
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
  // TODO: This is incredibly inefficient. Need to use method in the OWLModel to get individuals with a particular property.

  public static List<PropertyInfo> buildPropertyInfoList(OWLModel owlModel, String propertyName) throws SWRLRuleEngineBridgeException
  {
    OWLProperty property;
    PropertyInfo propertyInfo;
    List<PropertyInfo> propertyInfoList = new ArrayList<PropertyInfo>();
    Set<String> domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames;
    Argument subject, predicate;

    property = owlModel.getOWLProperty(propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);
    
    domainClassNames = SWRLOWLUtil.rdfResources2Names(property.getUnionDomain());
    rangeClassNames = SWRLOWLUtil.rdfResources2Names(property.getUnionRangeClasses());
    superPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getSuperproperties(true));
    subPropertyNames = SWRLOWLUtil.rdfResources2Names(property.getSubproperties(true));

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
                RDFResource resource = (RDFResource)individualValuesIterator.next();
                if (resource instanceof OWLIndividual) {
                  OWLIndividual rangeIndividual = (OWLIndividual)resource;
                  subject = new IndividualInfo(domainIndividual.getName());
                  predicate = new IndividualInfo(rangeIndividual.getName());
                  propertyInfo = new PropertyInfo(propertyName, subject, predicate, domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames);
                  propertyInfoList.add(propertyInfo);
                } else {
                  System.err.println("Unknown property value resource: " + resource);
                } // if
              } // while
            } else { // DatatypeProperty
              Iterator literalsIterator = domainIndividual.getPropertyValueLiterals(property).iterator();
              while (literalsIterator.hasNext()) {
                RDFSLiteral literal = (RDFSLiteral)literalsIterator.next();
                subject = new IndividualInfo(domainIndividual.getName());
                predicate = new LiteralInfo(owlModel, literal);
                propertyInfo = new PropertyInfo(propertyName, subject, predicate, domainClassNames, rangeClassNames, superPropertyNames, subPropertyNames);
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
    this.domainClassNames = new HashSet<String>();
    this.rangeClassNames = new HashSet<String>();
    this.superPropertyNames = new HashSet<String>();
    this.subPropertyNames = new HashSet<String>();
  } // initialize

} // PropertyInfo
