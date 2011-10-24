package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlxml;

import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.Filter;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataValueFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLBuiltInBridge;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLBuiltInBridgeException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;
import edu.stanford.smi.protegex.owl.swrl.owlapi.OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.owlapi.OWLNamedIndividual;
import edu.stanford.smi.protegex.owl.swrl.owlapi.OWLProperty;
import edu.stanford.smi.protegex.owl.swrl.owlapi.impl.OWLDataFactoryImpl;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;

public class XMLBridgeMapper
{
	public static final String SWRLXMLAlias = "swrlxml:";
	public static final String SWRLXMLNamespace = "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlxml.owl#";

	public static final String XMLDocumentOWLClassName = SWRLXMLNamespace + "XMLDocument";
	public static final String XMLDocumentHasRootElementPropertyName = SWRLXMLNamespace + "hasRootElement";
	public static final String XMLDocumentHasElementsPropertyName = SWRLXMLNamespace + "hasElements";

	public static final String XMLElementOWLClassName = SWRLXMLNamespace + "XMLElement";
	public static final String XMLHasNamePropertyName = SWRLXMLNamespace + "hasName";
	public static final String XMLHasNamespacePrefixPropertyName = SWRLXMLNamespace + "hasNamespacePrefix";
	public static final String XMLHasNamespaceURIPropertyName = SWRLXMLNamespace + "hasNamespaceURI";
	public static final String XMLElementHasMappedIndividualsPropertyName = SWRLXMLNamespace + "hasMappedIndividuals";
	public static final String XMLElementHasSubElementsPropertyName = SWRLXMLNamespace + "hasSubElements";
	public static final String XMLElementHasAttributesPropertyName = SWRLXMLNamespace + "hasAttributes";
	public static final String XMLElementHasContentPropertyName = SWRLXMLNamespace + "hasContent";

	public static final String XMLAttributeOWLClassName = SWRLXMLNamespace + "XMLAttribute";
	public static final String XMLAttributeHasValuePropertyName = SWRLXMLNamespace + "hasValue";

	private OWLProperty rootElementProperty, elementsProperty, subElementsProperty, nameProperty, namespacePrefixProperty, namespaceURIProperty, contentProperty,
			valueProperty, attributesProperty;

	private OWLDataFactory owlDataFactory;
	private OWLDataValueFactory owlDataValueFactory;

	private XMLProcessor xmlProcessor;

	public XMLBridgeMapper()
	{
		this.owlDataFactory = new OWLDataFactoryImpl();
		this.owlDataValueFactory = OWLDataValueFactory.create();
		this.xmlProcessor = new XMLProcessor();

		rootElementProperty = owlDataFactory.getOWLObjectProperty(XMLDocumentHasRootElementPropertyName);
		elementsProperty = owlDataFactory.getOWLObjectProperty(XMLDocumentHasElementsPropertyName);
		nameProperty = owlDataFactory.getOWLDataProperty(XMLHasNamePropertyName);
		namespacePrefixProperty = owlDataFactory.getOWLDataProperty(XMLHasNamespacePrefixPropertyName);
		namespaceURIProperty = owlDataFactory.getOWLDataProperty(XMLHasNamespaceURIPropertyName);
		contentProperty = owlDataFactory.getOWLDataProperty(XMLElementHasContentPropertyName);
		subElementsProperty = owlDataFactory.getOWLObjectProperty(XMLElementHasSubElementsPropertyName);
		valueProperty = owlDataFactory.getOWLDataProperty(XMLAttributeHasValuePropertyName);
		attributesProperty = owlDataFactory.getOWLObjectProperty(XMLElementHasAttributesPropertyName);
	}

	public Document owlDocument2Document(SWRLBuiltInBridge bridge) throws XMLBridgeMapperException
	{

		edu.stanford.smi.protegex.owl.model.OWLNamedClass owlDocumentClass;
		edu.stanford.smi.protegex.owl.model.OWLIndividual owlDocumentIndividual, owlRootElementIndividual = null;
		edu.stanford.smi.protegex.owl.model.RDFResource propertyValue;
		Document doc = new Document();

		try {
			owlDocumentClass = SWRLOWLUtil.getNamedClass(bridge.getActiveOntology().getOWLModel(), XMLDocumentOWLClassName);
			owlDocumentIndividual = SWRLOWLUtil.getOWLIndividual(bridge.getActiveOntology().getOWLModel(), owlDocumentClass, true, 1);
			propertyValue = SWRLOWLUtil.getObjectPropertyValue(bridge.getActiveOntology().getOWLModel(), owlDocumentIndividual,
					XMLDocumentHasRootElementPropertyName, false);

			if (propertyValue == null)
				throw new XMLBridgeMapperException("no document root element specified");
			else if (propertyValue instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)
				owlRootElementIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)propertyValue;
			else
				throw new XMLBridgeMapperException("invalid document root element '" + propertyValue + "'");

			owlElement2Element(doc, bridge, owlRootElementIndividual, null);
		} catch (SWRLOWLUtilException e) {
			throw new XMLBridgeMapperException("error mapping OWL XML ontology to Document: " + e.getMessage());
		}

		return doc;
	}

	public OWLNamedIndividual document2OWLDocument(Document doc, SWRLBuiltInBridge bridge) throws XMLBridgeMapperException
	{
		Element rootElement = doc.getRootElement();
		OWLNamedIndividual owlDocumentIndividual;

		if (xmlProcessor.isSchema(rootElement))
			throw new XMLBridgeMapperException("not expecting 'schema' root element");

		try {
			owlDocumentIndividual = bridge.injectOWLIndividualDeclaration(owlDataFactory.getOWLClass(XMLDocumentOWLClassName));
			element2OWLElement(doc, bridge, owlDocumentIndividual, null, rootElement);
		} catch (SWRLBuiltInBridgeException e) {
			throw new XMLBridgeMapperException("bridge error mapping Document to OWL XML ontology: " + e.getMessage());
		} catch (OWLFactoryException e) {
			throw new XMLBridgeMapperException("OWL factory error mapping Document to OWL XML ontology: " + e.getMessage());
		}

		return owlDocumentIndividual;
	}

	private void owlElement2Element(Document doc, SWRLBuiltInBridge bridge, edu.stanford.smi.protegex.owl.model.OWLIndividual owlElementIndividal,
																	Element parentElement) throws XMLBridgeMapperException, SWRLOWLUtilException
	{
		String elementName = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getActiveOntology().getOWLModel(), owlElementIndividal, XMLHasNamePropertyName,
				true);
		String elementNamespacePrefix = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getActiveOntology().getOWLModel(), owlElementIndividal,
				XMLHasNamespacePrefixPropertyName, false, "");
		String elementNamespaceURI = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getActiveOntology().getOWLModel(), owlElementIndividal,
				XMLHasNamespaceURIPropertyName, false, "");
		Set<edu.stanford.smi.protegex.owl.model.RDFResource> attributes = SWRLOWLUtil.getObjectPropertyValues(bridge.getActiveOntology().getOWLModel(),
				owlElementIndividal, XMLElementHasAttributesPropertyName, false);
		Set<edu.stanford.smi.protegex.owl.model.RDFResource> subElements = SWRLOWLUtil.getObjectPropertyValues(bridge.getActiveOntology().getOWLModel(),
				owlElementIndividal, XMLElementHasSubElementsPropertyName, false);
		String content = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getActiveOntology().getOWLModel(), owlElementIndividal,
				XMLElementHasContentPropertyName, false, "");
		Element element = xmlProcessor.createElement(doc, parentElement, elementName);

		element.setNamespace(Namespace.getNamespace(elementNamespacePrefix, elementNamespaceURI));

		if (content != null)
			element.addContent(content);

		for (edu.stanford.smi.protegex.owl.model.RDFResource value : subElements) {
			if (value instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
				edu.stanford.smi.protegex.owl.model.OWLIndividual owlSubElementIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)value;
				owlElement2Element(doc, bridge, owlSubElementIndividual, element);
			}
		}

		for (edu.stanford.smi.protegex.owl.model.RDFResource value : attributes) {
			if (value instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
				edu.stanford.smi.protegex.owl.model.OWLIndividual owlAttributeIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)value;
				owlAttribute2Attribute(doc, bridge, owlAttributeIndividual, element);
			}
		}
	}

	private void owlAttribute2Attribute(Document doc, SWRLBuiltInBridge bridge, edu.stanford.smi.protegex.owl.model.OWLIndividual owlAttributeIndividual,
																			Element element) throws XMLBridgeMapperException, SWRLOWLUtilException
	{
		String attributeName = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getActiveOntology().getOWLModel(), owlAttributeIndividual,
				XMLHasNamePropertyName, true);
		String attributeValue = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getActiveOntology().getOWLModel(), owlAttributeIndividual,
				XMLAttributeHasValuePropertyName, true);
		String attributeNamespacePrefix = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getActiveOntology().getOWLModel(), owlAttributeIndividual,
				XMLHasNamespacePrefixPropertyName, false, "");
		String attributeNamespaceURI = SWRLOWLUtil.getDatavaluedPropertyValueAsString(bridge.getActiveOntology().getOWLModel(), owlAttributeIndividual,
				XMLHasNamespaceURIPropertyName, false, "");

		xmlProcessor.setAttribute(element, attributeName, attributeValue, attributeNamespacePrefix, attributeNamespaceURI);
	}

	private void element2OWLElement(Document doc, SWRLBuiltInBridge bridge, OWLNamedIndividual owlDocumentIndividual,
																	OWLNamedIndividual parentOWLElementIndividual, Element element)
			throws XMLBridgeMapperException, SWRLBuiltInBridgeException, OWLFactoryException
	{
		OWLNamedIndividual owlElementIndividual = bridge.injectOWLIndividualDeclaration(owlDataFactory.getOWLClass(XMLElementOWLClassName));
		String elementName = element.getName();
		String elementNamespacePrefix = element.getNamespace().getPrefix();
		String elementNamespaceURI = element.getNamespace().getURI();
		Filter textFilter = new ContentFilter(ContentFilter.TEXT);
		String content = "";

		if (parentOWLElementIndividual == null)
			bridge.injectOWLAxiom(owlDataFactory.getOWLObjectPropertyAssertionAxiom(owlDocumentIndividual, rootElementProperty, owlElementIndividual));
		else
			bridge.injectOWLAxiom(owlDataFactory.getOWLObjectPropertyAssertionAxiom(parentOWLElementIndividual, subElementsProperty, owlElementIndividual));

		bridge.injectOWLAxiom(owlDataFactory.getOWLObjectPropertyAssertionAxiom(owlDocumentIndividual, elementsProperty, owlElementIndividual));

		bridge
				.injectOWLAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(owlElementIndividual, nameProperty, owlDataValueFactory.getOWLDataValue(elementName)));

		bridge.injectOWLAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(owlElementIndividual, namespacePrefixProperty,
				owlDataValueFactory.getOWLDataValue(elementNamespacePrefix)));

		bridge.injectOWLAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(owlElementIndividual, namespaceURIProperty,
				owlDataValueFactory.getOWLDataValue(elementNamespaceURI)));

		for (Object o : element.getContent(textFilter)) {
			Text text = (Text)o;
			content += text.getValue();
		}

		bridge.injectOWLAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(owlElementIndividual, contentProperty, owlDataValueFactory.getOWLDataValue(content)));

		for (Attribute attribute : xmlProcessor.getAttributes(element))
			attribute2OWLAttribute(doc, bridge, owlElementIndividual, attribute);

		for (Element subElement : xmlProcessor.getSubElements(element))
			element2OWLElement(doc, bridge, owlDocumentIndividual, owlElementIndividual, subElement);
	}

	private void attribute2OWLAttribute(Document doc, SWRLBuiltInBridge bridge, OWLNamedIndividual owlElementIndividual, Attribute attribute)
			throws XMLBridgeMapperException, SWRLBuiltInBridgeException, OWLFactoryException
	{
		OWLNamedIndividual owlAttributeIndividual = bridge.injectOWLIndividualDeclaration(owlDataFactory.getOWLClass(XMLAttributeOWLClassName));
		String attributeName = attribute.getName();
		String attributeValue = attribute.getValue();
		String attributeNamespacePrefix = attribute.getNamespace().getPrefix();
		String attributeNamespaceURI = attribute.getNamespace().getURI();

		bridge.injectOWLAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(owlAttributeIndividual, nameProperty, owlDataValueFactory.getOWLDataValue(attributeName)));

		bridge
				.injectOWLAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(owlAttributeIndividual, valueProperty, owlDataValueFactory.getOWLDataValue(attributeValue)));

		bridge.injectOWLAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(owlAttributeIndividual, namespacePrefixProperty,
				owlDataValueFactory.getOWLDataValue(attributeNamespacePrefix)));

		bridge.injectOWLAxiom(owlDataFactory.getOWLDataPropertyAssertionAxiom(owlAttributeIndividual, namespaceURIProperty,
				owlDataValueFactory.getOWLDataValue(attributeNamespaceURI)));

		bridge.injectOWLAxiom(owlDataFactory.getOWLObjectPropertyAssertionAxiom(owlElementIndividual, attributesProperty, owlAttributeIndividual));
	}
}
