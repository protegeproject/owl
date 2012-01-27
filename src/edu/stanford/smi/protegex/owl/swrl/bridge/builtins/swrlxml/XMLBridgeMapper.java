
package org.protege.swrltab.bridge.builtins.swrlxml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.filter.ContentFilter;
import org.jdom.filter.Filter;
import org.protege.swrlapi.adapters.OWLClassAdapter;
import org.protege.swrlapi.adapters.OWLDataPropertyAdapter;
import org.protege.swrlapi.adapters.OWLLiteralFactory;
import org.protege.swrlapi.adapters.OWLNamedIndividualAdapter;
import org.protege.swrlapi.adapters.OWLObjectAdapterFactory;
import org.protege.swrlapi.adapters.OWLObjectPropertyAdapter;
import org.protege.swrlapi.adapters.axioms.OWLClassAssertionAxiomAdapter;
import org.protege.swrlapi.adapters.axioms.OWLClassDeclarationAxiomAdapter;
import org.protege.swrlapi.adapters.axioms.OWLDataPropertyAssertionAxiomAdapter;
import org.protege.swrlapi.adapters.axioms.OWLIndividualDeclarationAxiomAdapter;
import org.protege.swrlapi.adapters.axioms.OWLObjectPropertyAssertionAxiomAdapter;
import org.protege.swrlapi.adapters.impl.DefaultOWLObjectAdapterFactory;
import org.protege.swrlapi.adapters.impl.OWLLiteralFactoryImpl;
import org.protege.swrlapi.core.SWRLBuiltInBridge;
import org.protege.swrlapi.exceptions.SWRLBuiltInBridgeException;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtil;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtilException;

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

	private OWLObjectPropertyAdapter rootElementProperty, elementsProperty, subElementsProperty, attributesProperty;

	private OWLDataPropertyAdapter nameProperty, namespacePrefixProperty, namespaceURIProperty, contentProperty, valueProperty;

	private OWLObjectAdapterFactory objectAdapterFactory;
	private OWLLiteralFactory literalReferenceFactory;

	private XMLProcessor xmlProcessor;

	private OWLModel owlModel;

	public XMLBridgeMapper(SWRLBuiltInBridge bridge, OWLModel owlModel) throws XMLBridgeMapperException
	{
		this.owlModel = owlModel;
		this.objectAdapterFactory = new DefaultOWLObjectAdapterFactory(bridge.getActiveOntology());
		this.literalReferenceFactory = new OWLLiteralFactoryImpl();
		this.xmlProcessor = new XMLProcessor();

		rootElementProperty = objectAdapterFactory.getOWLObjectProperty(createURI(XMLDocumentHasRootElementPropertyName));
		elementsProperty = objectAdapterFactory.getOWLObjectProperty(createURI(XMLDocumentHasElementsPropertyName));
		nameProperty = objectAdapterFactory.getOWLDataProperty(createURI(XMLHasNamePropertyName));
		namespacePrefixProperty = objectAdapterFactory.getOWLDataProperty(createURI(XMLHasNamespacePrefixPropertyName));
		namespaceURIProperty = objectAdapterFactory.getOWLDataProperty(createURI(XMLHasNamespaceURIPropertyName));
		contentProperty = objectAdapterFactory.getOWLDataProperty(createURI(XMLElementHasContentPropertyName));
		subElementsProperty = objectAdapterFactory.getOWLObjectProperty(createURI(XMLElementHasSubElementsPropertyName));
		valueProperty = objectAdapterFactory.getOWLDataProperty(createURI(XMLAttributeHasValuePropertyName));
		attributesProperty = objectAdapterFactory.getOWLObjectProperty(createURI(XMLElementHasAttributesPropertyName));
	}

	public Document owlDocument2Document(SWRLBuiltInBridge bridge) throws XMLBridgeMapperException
	{

		edu.stanford.smi.protegex.owl.model.OWLNamedClass owlDocumentClass;
		edu.stanford.smi.protegex.owl.model.OWLIndividual owlDocumentIndividual, owlRootElementIndividual = null;
		edu.stanford.smi.protegex.owl.model.RDFResource propertyValue;
		Document doc = new Document();

		try {
			owlDocumentClass = P3OWLUtil.getNamedClass(getOWLModel(), XMLDocumentOWLClassName);
			owlDocumentIndividual = P3OWLUtil.getOWLIndividual(owlDocumentClass, true, 1);
			propertyValue = P3OWLUtil.getObjectPropertyValue(getOWLModel(), owlDocumentIndividual, XMLDocumentHasRootElementPropertyName, false);

			if (propertyValue == null)
				throw new XMLBridgeMapperException("no document root element specified");
			else if (propertyValue instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual)
				owlRootElementIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)propertyValue;
			else
				throw new XMLBridgeMapperException("invalid document root element '" + propertyValue + "'");

			owlElement2Element(doc, bridge, owlRootElementIndividual, null);
		} catch (P3OWLUtilException e) {
			throw new XMLBridgeMapperException("error mapping OWL XML ontology to Document: " + e.getMessage());
		}

		return doc;
	}

	public OWLNamedIndividualAdapter document2OWLDocument(Document doc, SWRLBuiltInBridge bridge) throws XMLBridgeMapperException
	{
		Element rootElement = doc.getRootElement();
		OWLNamedIndividualAdapter owlDocumentIndividual;

		if (xmlProcessor.isSchema(rootElement))
			throw new XMLBridgeMapperException("not expecting 'schema' root element");

		try {
			owlDocumentIndividual = injectOWLNamedIndividualOfClass(bridge, objectAdapterFactory.getOWLClass(createURI(XMLDocumentOWLClassName)));
			element2OWLElement(doc, bridge, owlDocumentIndividual, null, rootElement);
		} catch (SWRLBuiltInBridgeException e) {
			throw new XMLBridgeMapperException("bridge error mapping Document to OWL XML ontology: " + e.getMessage());
		}

		return owlDocumentIndividual;
	}

	private void owlElement2Element(Document doc, SWRLBuiltInBridge bridge, edu.stanford.smi.protegex.owl.model.OWLIndividual owlElementIndividal,
																	Element parentElement) throws XMLBridgeMapperException, P3OWLUtilException
	{
		String elementName = P3OWLUtil.getDatavaluedPropertyValueAsString(getOWLModel(), owlElementIndividal, XMLHasNamePropertyName, true);
		String elementNamespacePrefix = P3OWLUtil.getDatavaluedPropertyValueAsString(getOWLModel(), owlElementIndividal, XMLHasNamespacePrefixPropertyName, false,
				"");
		String elementNamespaceURI = P3OWLUtil.getDatavaluedPropertyValueAsString(getOWLModel(), owlElementIndividal, XMLHasNamespaceURIPropertyName, false, "");
		Set<edu.stanford.smi.protegex.owl.model.RDFResource> attributes = P3OWLUtil.getObjectPropertyValues(getOWLModel(), owlElementIndividal,
				XMLElementHasAttributesPropertyName, false);
		Set<edu.stanford.smi.protegex.owl.model.RDFResource> subElements = P3OWLUtil.getObjectPropertyValues(getOWLModel(), owlElementIndividal,
				XMLElementHasSubElementsPropertyName, false);
		String content = P3OWLUtil.getDatavaluedPropertyValueAsString(getOWLModel(), owlElementIndividal, XMLElementHasContentPropertyName, false, "");
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
				owlAttribute2Attribute(owlAttributeIndividual, element);
			}
		}
	}

	private void owlAttribute2Attribute(edu.stanford.smi.protegex.owl.model.OWLIndividual owlAttributeIndividual, Element element)
		throws XMLBridgeMapperException, P3OWLUtilException
	{
		String attributeName = P3OWLUtil.getDatavaluedPropertyValueAsString(getOWLModel(), owlAttributeIndividual, XMLHasNamePropertyName, true);
		String attributeValue = P3OWLUtil.getDatavaluedPropertyValueAsString(getOWLModel(), owlAttributeIndividual, XMLAttributeHasValuePropertyName, true);
		String attributeNamespacePrefix = P3OWLUtil.getDatavaluedPropertyValueAsString(getOWLModel(), owlAttributeIndividual, XMLHasNamespacePrefixPropertyName,
				false, "");
		String attributeNamespaceURI = P3OWLUtil.getDatavaluedPropertyValueAsString(getOWLModel(), owlAttributeIndividual, XMLHasNamespaceURIPropertyName, false,
				"");

		xmlProcessor.setAttribute(element, attributeName, attributeValue, attributeNamespacePrefix, attributeNamespaceURI);
	}

	private void element2OWLElement(Document doc, SWRLBuiltInBridge bridge, OWLNamedIndividualAdapter owlDocumentIndividual,
																	OWLNamedIndividualAdapter parentOWLElementIndividual, Element element)
		throws XMLBridgeMapperException, SWRLBuiltInBridgeException
	{
		OWLNamedIndividualAdapter owlElementIndividual = injectOWLNamedIndividualOfClass(bridge,
				objectAdapterFactory.getOWLClass(createURI(XMLElementOWLClassName)));
		String elementName = element.getName();
		String elementNamespacePrefix = element.getNamespace().getPrefix();
		String elementNamespaceURI = element.getNamespace().getURI();
		Filter textFilter = new ContentFilter(ContentFilter.TEXT);
		String content = "";

		if (parentOWLElementIndividual == null)
			bridge.injectOWLAxiom(objectAdapterFactory.getOWLObjectPropertyAssertionAxiom(owlDocumentIndividual, rootElementProperty, owlElementIndividual));
		else
			bridge.injectOWLAxiom(objectAdapterFactory.getOWLObjectPropertyAssertionAxiom(parentOWLElementIndividual, subElementsProperty, owlElementIndividual));

		bridge.injectOWLAxiom(objectAdapterFactory.getOWLObjectPropertyAssertionAxiom(owlDocumentIndividual, elementsProperty, owlElementIndividual));

		bridge.injectOWLAxiom(objectAdapterFactory.getOWLDataPropertyAssertionAxiom(owlElementIndividual, nameProperty,
				literalReferenceFactory.getOWLLiteral(elementName)));

		bridge.injectOWLAxiom(objectAdapterFactory.getOWLDataPropertyAssertionAxiom(owlElementIndividual, namespacePrefixProperty,
				literalReferenceFactory.getOWLLiteral(elementNamespacePrefix)));

		bridge.injectOWLAxiom(objectAdapterFactory.getOWLDataPropertyAssertionAxiom(owlElementIndividual, namespaceURIProperty,
				literalReferenceFactory.getOWLLiteral(elementNamespaceURI)));

		for (Object o : element.getContent(textFilter)) {
			Text text = (Text)o;
			content += text.getValue();
		}

		bridge.injectOWLAxiom(objectAdapterFactory.getOWLDataPropertyAssertionAxiom(owlElementIndividual, contentProperty,
				literalReferenceFactory.getOWLLiteral(content)));

		for (Attribute attribute : xmlProcessor.getAttributes(element))
			attribute2OWLAttribute(bridge, owlElementIndividual, attribute);

		for (Element subElement : xmlProcessor.getSubElements(element))
			element2OWLElement(doc, bridge, owlDocumentIndividual, owlElementIndividual, subElement);
	}

	private void attribute2OWLAttribute(SWRLBuiltInBridge bridge, OWLNamedIndividualAdapter owlElementIndividual, Attribute attribute)
		throws XMLBridgeMapperException, SWRLBuiltInBridgeException
	{
		String attributeName = attribute.getName();
		String attributeValue = attribute.getValue();
		String attributeNamespacePrefix = attribute.getNamespace().getPrefix();
		String attributeNamespaceURI = attribute.getNamespace().getURI();
		OWLClassAdapter attributeClass = objectAdapterFactory.getOWLClass(createURI(XMLAttributeOWLClassName));
		OWLNamedIndividualAdapter attributeIndividual = objectAdapterFactory.getOWLNamedIndividual();
		OWLClassDeclarationAxiomAdapter classDeclarationAxiom;
		OWLIndividualDeclarationAxiomAdapter individualDeclarationAxiom;
		OWLDataPropertyAssertionAxiomAdapter dataPropertyAssertionAxiom;
		OWLObjectPropertyAssertionAxiomAdapter objectPropertyAssertionAxiom;

		classDeclarationAxiom = objectAdapterFactory.getOWLClassDeclarationAxiom(attributeClass);
		bridge.injectOWLAxiom(classDeclarationAxiom);

		individualDeclarationAxiom = objectAdapterFactory.getOWLIndividualDeclarationAxiom(attributeIndividual);
		bridge.injectOWLAxiom(individualDeclarationAxiom);

		dataPropertyAssertionAxiom = objectAdapterFactory.getOWLDataPropertyAssertionAxiom(attributeIndividual, nameProperty,
				literalReferenceFactory.getOWLLiteral(attributeName));
		bridge.injectOWLAxiom(dataPropertyAssertionAxiom);

		dataPropertyAssertionAxiom = objectAdapterFactory.getOWLDataPropertyAssertionAxiom(attributeIndividual, valueProperty,
				literalReferenceFactory.getOWLLiteral(attributeValue));
		bridge.injectOWLAxiom(dataPropertyAssertionAxiom);

		dataPropertyAssertionAxiom = objectAdapterFactory.getOWLDataPropertyAssertionAxiom(attributeIndividual, namespacePrefixProperty,
				literalReferenceFactory.getOWLLiteral(attributeNamespacePrefix));
		bridge.injectOWLAxiom(dataPropertyAssertionAxiom);

		dataPropertyAssertionAxiom = objectAdapterFactory.getOWLDataPropertyAssertionAxiom(attributeIndividual, namespaceURIProperty,
				literalReferenceFactory.getOWLLiteral(attributeNamespaceURI));
		bridge.injectOWLAxiom(dataPropertyAssertionAxiom);

		objectPropertyAssertionAxiom = objectAdapterFactory.getOWLObjectPropertyAssertionAxiom(owlElementIndividual, attributesProperty, attributeIndividual);
		bridge.injectOWLAxiom(objectPropertyAssertionAxiom);
	}

	private URI createURI(String fullName) throws XMLBridgeMapperException
	{
		try {
			return new URI(fullName);
		} catch (URISyntaxException e) {
			throw new XMLBridgeMapperException("error converting " + fullName + " to URI: " + e.getMessage());
		}
	}

	private OWLNamedIndividualAdapter injectOWLNamedIndividualOfClass(SWRLBuiltInBridge bridge, OWLClassAdapter cls) throws SWRLBuiltInBridgeException
	{
		OWLNamedIndividualAdapter individual = bridge.getOWLAdapterFactory().getOWLNamedIndividual();
		OWLIndividualDeclarationAxiomAdapter declarationAxiom = bridge.getOWLAdapterFactory().getOWLIndividualDeclarationAxiom(individual);
		OWLClassAssertionAxiomAdapter classAssertionAxiom = bridge.getOWLAdapterFactory().getOWLClassAssertionAxiom(individual, cls);
		bridge.injectOWLAxiom(declarationAxiom);
		bridge.injectOWLAxiom(classAssertionAxiom);

		return individual;
	}

	private OWLModel getOWLModel()
	{
		return owlModel;
	}
}
