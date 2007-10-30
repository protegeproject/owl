
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlxml;

import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRuleEngineBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import org.jdom.*;
import org.jdom.output.*;
import org.jdom.input.*;
import org.jdom.filter.*;

import java.io.*;
import java.util.*;

public class XMLMapper
{
  public static final String XMLMappingAlias = "swrlxml:";

  public static final String XMLDocumentMappingOWLClassName = XMLMappingAlias + "XMLDocument";
  public static final String XMLDocumentMappingHasRootElementPropertyName = XMLMappingAlias + "hasRootElement";
  public static final String XMLDocumentMappingHasElementsPropertyName = XMLMappingAlias + "hasElements";

  public static final String XMLElementMappingOWLClassName = XMLMappingAlias + "XMLElement";
  public static final String XMLMappingHasNamePropertyName = XMLMappingAlias + "hasName";
  public static final String XMLMappingHasNamespacePrefixPropertyName = XMLMappingAlias + "hasNamespacePrefix";
  public static final String XMLMappingHasNamespaceURIPropertyName = XMLMappingAlias + "hasNamespaceURI";
  public static final String XMLElementMappingHasMappedIndividualsPropertyName = XMLMappingAlias + "hasMappedIndividuals";
  public static final String XMLElementMappingHasSubElementsPropertyName = XMLMappingAlias + "hasSubElements";
  public static final String XMLElementMappingHasAttributesPropertyName = XMLMappingAlias + "hasAttributes";
  public static final String XMLElementMappingHasContentPropertyName = XMLMappingAlias + "hasContent";

  public static final String XMLAttributeMappingOWLClassName = XMLMappingAlias + "XMLAttribute";
  public static final String XMLAttributeMappingHasValuePropertyName = XMLMappingAlias + "hasValue";

  private OWLProperty rootElementProperty, elementsProperty, subElementsProperty, nameProperty, namespacePrefixProperty, namespaceURIProperty, 
    contentProperty, valueProperty, attributesProperty;

  public XMLMapper()
  {
    rootElementProperty = OWLFactory.createOWLObjectProperty(XMLDocumentMappingHasRootElementPropertyName);
    elementsProperty = OWLFactory.createOWLObjectProperty(XMLDocumentMappingHasElementsPropertyName);
    nameProperty = OWLFactory.createOWLDatatypeProperty(XMLMappingHasNamePropertyName);
    namespacePrefixProperty = OWLFactory.createOWLDatatypeProperty(XMLMappingHasNamespacePrefixPropertyName);
    namespaceURIProperty = OWLFactory.createOWLDatatypeProperty(XMLMappingHasNamespaceURIPropertyName);
    contentProperty = OWLFactory.createOWLDatatypeProperty(XMLElementMappingHasContentPropertyName);
    subElementsProperty = OWLFactory.createOWLObjectProperty(XMLElementMappingHasSubElementsPropertyName);
    valueProperty = OWLFactory.createOWLObjectProperty(XMLAttributeMappingHasValuePropertyName);
    attributesProperty = OWLFactory.createOWLObjectProperty(XMLElementMappingHasAttributesPropertyName);
  } // XMLMapper

  public Document xmlDocumentMapping2Document(SWRLRuleEngineBridge bridge) throws XMLMapperException
  {

    edu.stanford.smi.protegex.owl.model.OWLNamedClass xmlDocumentMappingClass;
    edu.stanford.smi.protegex.owl.model.OWLIndividual xmlDocumentMapping, xmlRootElementMapping = null;
    Object propertyValue;
    Document doc = new Document();

    try {
      xmlDocumentMappingClass = SWRLOWLUtil.getClass(bridge.getOWLModel(), XMLDocumentMappingOWLClassName);
      xmlDocumentMapping = SWRLOWLUtil.getIndividual(bridge.getOWLModel(), xmlDocumentMappingClass, true, 1);
      propertyValue = SWRLOWLUtil.getObjectPropertyValue(bridge.getOWLModel(), xmlDocumentMapping, 
                                                                 XMLDocumentMappingHasRootElementPropertyName, false);

      if (propertyValue == null) throw new XMLMapperException("no document root element specified");
      else if (propertyValue instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)
        xmlRootElementMapping = (edu.stanford.smi.protegex.owl.model.OWLIndividual)propertyValue;
      else throw new XMLMapperException("invalid document root element '" + propertyValue + "'");

      xmlElementMapping2Element(doc, bridge, xmlRootElementMapping, null);
    } catch (SWRLOWLUtilException e) {
      throw new XMLMapperException("error mapping OWL XML ontology to Document: " + e.getMessage());
    } // try

    return doc;
  } // xmlDocumentMapping2Document

  private void xmlElementMapping2Element(Document doc, SWRLRuleEngineBridge bridge, edu.stanford.smi.protegex.owl.model.OWLIndividual xmlElementMapping, Element parentElement) 
    throws XMLMapperException, SWRLOWLUtilException
  {
    String elementName = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getOWLModel(), xmlElementMapping, XMLMappingHasNamePropertyName, true);
    String elementNamespacePrefix = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getOWLModel(), xmlElementMapping, XMLMappingHasNamespacePrefixPropertyName, false, "");
    String elementNamespaceURI = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getOWLModel(), xmlElementMapping, XMLMappingHasNamespaceURIPropertyName, false, "");
    Set<Object> attributes = SWRLOWLUtil.getObjectPropertyValues(bridge.getOWLModel(), xmlElementMapping, XMLElementMappingHasAttributesPropertyName, false);
    Set<Object> subElements = SWRLOWLUtil.getObjectPropertyValues(bridge.getOWLModel(), xmlElementMapping, XMLElementMappingHasSubElementsPropertyName, false);
    String content = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getOWLModel(), xmlElementMapping, XMLElementMappingHasContentPropertyName, false, "");
    Element element = createElement(doc, parentElement, elementName);

    element.setNamespace(Namespace.getNamespace(elementNamespacePrefix, elementNamespaceURI));

    if (content != null) element.addContent(content);
      
    for (Object value : subElements) {
      if (value instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
        edu.stanford.smi.protegex.owl.model.OWLIndividual xmlSubElementMapping = (edu.stanford.smi.protegex.owl.model.OWLIndividual)value;
        xmlElementMapping2Element(doc, bridge, xmlSubElementMapping, element);
      } //if
    } // for

    for (Object value : attributes) {
      if (value instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
        edu.stanford.smi.protegex.owl.model.OWLIndividual xmlAttributeMapping = (edu.stanford.smi.protegex.owl.model.OWLIndividual)value;
        xmlAttributeMapping2Attribute(doc, bridge, xmlAttributeMapping, element);
      } // if
  } // for
  } // xmlElementMapping2Element

  private void xmlAttributeMapping2Attribute(Document doc, SWRLRuleEngineBridge bridge, edu.stanford.smi.protegex.owl.model.OWLIndividual xmlAttributeMapping, Element element) 
    throws XMLMapperException, SWRLOWLUtilException
  {
    String attributeName = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getOWLModel(), xmlAttributeMapping, XMLMappingHasNamePropertyName, true);
    String attributeValue = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getOWLModel(), xmlAttributeMapping, XMLAttributeMappingHasValuePropertyName, true);
    String attributeNamespacePrefix = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getOWLModel(), xmlAttributeMapping, XMLMappingHasNamespacePrefixPropertyName, false, "");
    String attributeNamespaceURI = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getOWLModel(), xmlAttributeMapping, XMLMappingHasNamespaceURIPropertyName, false, "");

    setAttribute(element, attributeName, attributeValue, attributeNamespacePrefix, attributeNamespaceURI);
  } // xmlAttributeMapping2Attribute

  public OWLIndividual document2XMLDocumentMapping(Document doc, SWRLRuleEngineBridge bridge) throws XMLMapperException
  {
    Element rootElement = doc.getRootElement();
    OWLIndividual xmlDocumentMapping;

    if (isSchema(rootElement)) throw new XMLMapperException("not expecting 'schema' root element");

    try {
      xmlDocumentMapping = bridge.createOWLIndividual(OWLFactory.createOWLClass(XMLDocumentMappingOWLClassName));
      element2XMLElementMapping(doc, bridge, xmlDocumentMapping, null, rootElement);
    } catch (SWRLRuleEngineBridgeException e) {
      throw new XMLMapperException("error mapping Document to OWL XML ontology: " + e.getMessage());
    } // try

    return xmlDocumentMapping;
  } // document2XMLDocumentMapping

  private void element2XMLElementMapping(Document doc, SWRLRuleEngineBridge bridge, OWLIndividual xmlDocumentMapping, 
                                         OWLIndividual parentElementMapping, Element element) throws XMLMapperException, SWRLRuleEngineBridgeException
  {
    OWLIndividual elementMapping = bridge.createOWLIndividual(OWLFactory.createOWLClass(XMLElementMappingOWLClassName));
    String elementName = element.getName();
    String elementNamespacePrefix = element.getNamespace().getPrefix();
    String elementNamespaceURI = element.getNamespace().getURI();
    Filter textFilter = new ContentFilter(ContentFilter.TEXT);
    String content = "";

    if (parentElementMapping == null)
      bridge.createOWLObjectPropertyAssertionAxiom(xmlDocumentMapping, rootElementProperty, elementMapping);
    else 
      bridge.createOWLObjectPropertyAssertionAxiom(parentElementMapping, subElementsProperty, elementMapping);

    bridge.createOWLObjectPropertyAssertionAxiom(xmlDocumentMapping, elementsProperty, elementMapping);

    bridge.createOWLDatatypePropertyAssertionAxiom(elementMapping, nameProperty, OWLFactory.createOWLDatatypeValue(elementName));

    bridge.createOWLDatatypePropertyAssertionAxiom(elementMapping, namespacePrefixProperty, 
                                                   OWLFactory.createOWLDatatypeValue(elementNamespacePrefix));

    bridge.createOWLDatatypePropertyAssertionAxiom(elementMapping, namespaceURIProperty, 
                                                   OWLFactory.createOWLDatatypeValue(elementNamespaceURI));
 
    for (Object o : element.getContent(textFilter)) {
      Text text = (Text)o;
      content += text.getValue();
    } // for
    
    bridge.createOWLDatatypePropertyAssertionAxiom(elementMapping, contentProperty, OWLFactory.createOWLDatatypeValue(content));

    for (Attribute attribute : getAttributes(element)) attribute2XMLAttributeMapping(doc, bridge, elementMapping, attribute);

    for (Element subElement : getSubElements(element)) element2XMLElementMapping(doc, bridge, xmlDocumentMapping, elementMapping, subElement);
  } // element2XMLElementMapping

  private void attribute2XMLAttributeMapping(Document doc, SWRLRuleEngineBridge bridge, OWLIndividual elementMapping, Attribute attribute) 
    throws XMLMapperException, SWRLRuleEngineBridgeException
  {
    OWLIndividual attributeMapping = bridge.createOWLIndividual(OWLFactory.createOWLClass(XMLAttributeMappingOWLClassName));
    String attributeName = attribute.getName();
    String attributeValue = attribute.getValue();
    String attributeNamespacePrefix = attribute.getNamespace().getPrefix();
    String attributeNamespaceURI = attribute.getNamespace().getURI();
    
    bridge.createOWLDatatypePropertyAssertionAxiom(attributeMapping, nameProperty, OWLFactory.createOWLDatatypeValue(attributeName));

    bridge.createOWLDatatypePropertyAssertionAxiom(attributeMapping, valueProperty, OWLFactory.createOWLDatatypeValue(attributeValue));

    bridge.createOWLDatatypePropertyAssertionAxiom(attributeMapping, namespacePrefixProperty,
                                                   OWLFactory.createOWLDatatypeValue(attributeNamespacePrefix));

    bridge.createOWLDatatypePropertyAssertionAxiom(attributeMapping, namespaceURIProperty,
                                                   OWLFactory.createOWLDatatypeValue(attributeNamespaceURI));

    bridge.createOWLObjectPropertyAssertionAxiom(elementMapping, attributesProperty, attributeMapping);

  } // attribute2XMLAttributeMapping

  private Element createElement(Document doc, Element parentElement, String elementName)
  {
    Element element = new Element(elementName);

    if (parentElement == null) doc.setRootElement(element);
    else parentElement.addContent(element);

    return element;
  } // createElement

  private void setAttribute(Element element, String attributeName, String attributeValue, String namespacePrefix, String namespaceURI)
  {
    Attribute attribute = new Attribute(attributeName, attributeValue, Namespace.getNamespace(namespacePrefix, namespaceURI));

    element.setAttribute(attribute);
  } // setAttribute

  private List<Attribute> getAttributes(Element element) throws XMLMapperException 
  {
    return new ArrayList<Attribute>(element.getAttributes()); 
  } // getAttributes

  private List<Element> getSubElements(Element element) throws XMLMapperException 
  { 
    List<Element> result = new ArrayList<Element>();

    for (Object o : element.getChildren()) if (o instanceof Element) result.add((Element)o);

    return result;
  } // getSubElements

  // Methods below this point are not used yet - they are for future support of more complex XML and XML Schema documents.

  private boolean isSchema(Element element) { return hasName(element, "schema"); }
  private boolean isElement(Element element) { return hasName(element, "element"); }
  private boolean isAll(Element element) { return hasName(element, "all"); }
  private boolean isComplexType(Element element) { return hasName(element, "complexType"); }
  private boolean isSequence(Element element) { return hasName(element, "sequence"); }
  private boolean isGroup(Element element) { return hasName(element, "group"); }
  private boolean isAttributeGroup(Element element) { return hasName(element, "attributeGroup"); }
  private boolean isChoice(Element element) { return hasName(element, "choice"); }
  private boolean isAny(Element element) { return hasName(element, "any"); }
  private boolean isAnyAttribute(Element element) { return hasName(element, "anyAttribute"); }
  private boolean isAttribute(Element element) { return hasName(element, "attribute"); }
  private boolean isComplexContent(Element element) { return hasName(element, "complexContent"); }
  private boolean isSimpleContent(Element element) { return hasName(element, "simpleContent"); }
  private boolean isSimpleContext(Element element) { return hasName(element, "simpleContext"); }
  private boolean isSimpleType(Element element) { return hasName(element, "simpleType"); }
  private boolean isRefElement(Element element) { return isElement(element) && hasRefAttribute(element); }

  private String getNameAttribute(Element element) throws XMLMapperException { return getNameAttributeValue(element); }
  private String getReafAttribute(Element element) throws XMLMapperException { return getRefAttributeValue(element); }
  private String getTypeAttribute(Element element) throws XMLMapperException { return getTypeAttributeValue(element); }
  private String getUseAttribute(Element element) throws XMLMapperException { return getUseAttributeValue(element); }
  private String getMinOccursAttribute(Element element) throws XMLMapperException { return getMinOccursAttributeValue(element); }
  private String getMaxOccursAttribute(Element element) throws XMLMapperException { return getMaxOccursAttributeValue(element); }

  private boolean hasNameAttribute(Element element) { return hasAttribute(element, "name"); }
  private boolean hasValueAttribute(Element element) { return hasAttribute(element, "value"); }
  private boolean hasBaseAttribute(Element element) { return hasAttribute(element, "base"); }
  private boolean hasMixedAttribute(Element element) { return hasAttribute(element, "mixed"); }
  private boolean hasTypeAttribute(Element element) { return hasAttribute(element, "type"); }
  private boolean hasDefaultAttribute(Element element) { return hasAttribute(element, "default"); }
  private boolean hasFixedAttribute(Element element) { return hasAttribute(element, "fixed"); }
  private boolean hasRefAttribute(Element element) { return hasAttribute(element, "ref"); }
  private boolean hasUseAttribute(Element element) { return hasAttribute(element, "use"); }
  private boolean hasMaxOccursAttribute(Element element) { return hasAttribute(element, "maxOccurs"); }
  private boolean hasMinOccursAttribute(Element element) { return hasAttribute(element, "minOccurs"); }

  private String getNameAttributeValue(Element element) throws XMLMapperException { return getAttributeValue(element, "name"); }
  private String getValueAttributeValue(Element element) throws XMLMapperException  { return getAttributeValue(element, "value"); }
  private String getMixedAttributeValue(Element element) throws XMLMapperException  { return getAttributeValue(element, "mixed"); }
  private String getBaseAttributeValue(Element element) throws XMLMapperException  { return getAttributeValue(element, "base"); }
  private String getTypeAttributeValue(Element element) throws XMLMapperException  { return getAttributeValue(element, "type"); }
  private String getDefaultAttributeValue(Element element) throws XMLMapperException  { return getAttributeValue(element, "default"); }
  private String getFixedAttributeValue(Element element) throws XMLMapperException  { return getAttributeValue(element, "fixed"); }
  private String getRefAttributeValue(Element element) throws XMLMapperException { return getAttributeValue(element, "ref"); }
  private String getUseAttributeValue(Element element) throws XMLMapperException { return getAttributeValue(element, "use"); }
  private String getMaxOccursAttributeValue(Element element) throws XMLMapperException { return getAttributeValue(element, "maxOccurs"); }
  private String getMinOccursAttributeValue(Element element) throws XMLMapperException { return getAttributeValue(element, "minOccurs"); }

  private Element getComplexTypeChild(Element element) throws XMLMapperException
  {
    if (!hasComplexTypeChild(element))
      throw new XMLMapperException("expecting complexType child for element '" + getNameAttributeValue(element) + "'");
    return element.getChild("complexType", element.getNamespace());
  } // getComplexTypeChild

  private int count(List l, Object o) 
  {
    int result = 0, i = l.indexOf(o);
    
    if (i != -1)
      for (; i < l.size(); i++) 
        if (l.get(i) == o) result++;

    return result;
  } // count

  private boolean hasAttribute(Element element, String attributeName) 
  { 
    return element.getAttributeValue(attributeName) != null;
  } // hasAttribute
  
  private String getAttributeValue(Element element, String attributeName) throws XMLMapperException
  {
    if (!hasAttribute(element, attributeName))
      throw new XMLMapperException("no '" + attributeName + "' attribute found in element '" + element.getName() + "'");

    return element.getAttributeValue(attributeName);
  } // getAttributeValue

  private Element getFirstChild(Element element) throws XMLMapperException
  {
    if (element.getChildren() == null) 
      throw new XMLMapperException("getFirstChild called on non-parent element '" + getNameAttributeValue(element) + "'");

    return (Element)element.getChildren().get(0);
  } // getFirstChild
  private boolean hasName(Element element, String name) { return element.getName() != null && element.getName().equals(name); }

  private boolean hasChildren(Element element) { return element.getChildren() != null; }

  private boolean hasComplexTypeChild(Element element) 
  { 
    return isElement(element) && element.getChild("complexType", element.getNamespace()) != null; 
  } // hasComplexTypeChild

  private boolean hasSimpleTypeChild(Element element) 
  { 
    return isElement(element) && element.getChild("simpleTypeChild", element.getNamespace()) != null; 
  } // hasSimpleTypeChild

} // XMLMapper
