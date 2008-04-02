
// Info object representing an OWL property. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidPropertyNameException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidIndividualNameException;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class PropertyInfo extends Info
{
  private Argument subject, predicate;
  private List domainClassNames, rangeClassNames;
  
  // Constructor used when creating a PropertyInfo object from an OWL property.
  public PropertyInfo(String propertyName, Argument subject, Argument predicate, List domainClassNames, List rangeClassNames) 
    throws SWRLRuleEngineBridgeException
  {
    super(propertyName);
    
    this.subject= subject;
    this.predicate = predicate;
    this.domainClassNames = domainClassNames;
    this.rangeClassNames = rangeClassNames;
  } // PropertyInfo
  
  // Constructor used when creating a PropertyInfo object from an assertion made by a target rule engine. 
  public PropertyInfo(String propertyName, Argument subject, Argument predicate) throws SWRLRuleEngineBridgeException
  {
    super(propertyName);

    this.subject= subject;
    this.predicate = predicate;
  } // PropertyInfo
  
  public Argument getSubject() { return subject; }
  public Argument getPredicate() { return predicate; }
  public List getDomainClassNames() { return domainClassNames; }
  public List getRangeClassNames() { return rangeClassNames; }
  
  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    OWLProperty property;
    OWLIndividual individual;
    Object object;
    
    if (getName().equalsIgnoreCase("sameAs")) property = (OWLProperty)owlModel.getOWLSameAsProperty();
    else if (getName().equalsIgnoreCase("differentFrom")) property = (OWLProperty)owlModel.getOWLDifferentFromProperty();
    else property = owlModel.getOWLProperty(getName());
    
    if (property == null) throw new InvalidPropertyNameException(getName());
    
    individual = owlModel.getOWLIndividual(subject.getName());
    if (individual == null) throw new InvalidIndividualNameException(subject.getName());
    
    if (property.isObjectProperty()) {
      object = owlModel.getOWLIndividual(predicate.getName());
      if (object == null) throw new InvalidIndividualNameException(predicate.getName());
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
    return "Property(name: " + getName() + ", subject: " + subject + ", predicate: " + predicate + ", domainClassNames: " + 
      domainClassNames + ", rangeClassNames: " + rangeClassNames + ")";
  } // toString

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    PropertyInfo info = (PropertyInfo)obj;
    return (getName() == info.getName() || (getName() != null && getName().equals(info.getName()))) && 
      (subject == info.subject || (subject != null && subject.equals(info.subject))) &&
      (predicate == info.predicate || (predicate != null && predicate.equals(info.predicate))) &&
      (domainClassNames == info.domainClassNames || (domainClassNames != null && domainClassNames.equals(info.domainClassNames))) &&
      (rangeClassNames == info.rangeClassNames || (rangeClassNames != null && rangeClassNames.equals(info.rangeClassNames)));
  } // equals

  public int hashCode()
  {
    int hash = 767;
    hash = hash + (null == getName() ? 0 : getName().hashCode());
    hash = hash + (null == subject ? 0 : subject.hashCode());
    hash = hash + (null == predicate ? 0 : predicate.hashCode());
    hash = hash + (null == domainClassNames ? 0 : domainClassNames.hashCode());
    hash = hash + (null == rangeClassNames ? 0 : rangeClassNames.hashCode());
    return hash;
  } // hashCode

  // Utility method to create a list of PropertyInfo objects for every subject/predicate combination for a particular OWL property.
  // TODO: This is incredibly inefficient. Need to add a method to the OWLModel to get individuals with a particular property.

  public static List buildPropertyInfoList(OWLModel owlModel, String propertyName) throws SWRLRuleEngineBridgeException
  {
    RDFProperty property;
    PropertyInfo propertyInfo;
    List propertyInfoList = new ArrayList();
    List domainClassNames, rangeClassNames;
    Argument subject, predicate;

    property = owlModel.getRDFProperty(propertyName);
    if (property == null) throw new InvalidPropertyNameException(propertyName);
    
    domainClassNames = rdfResources2NamesList(property.getUnionDomain());
    rangeClassNames = rdfResources2NamesList(property.getUnionRangeClasses());

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
