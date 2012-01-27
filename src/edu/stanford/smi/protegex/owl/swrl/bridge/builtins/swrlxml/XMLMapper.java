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

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtil;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtilException;

/*
 * Class that converts between an XML document and an OWL representation of an XML document as defined by the ontology 
 * http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlxml.owl.
 * 
 * Deals only with relatively basic XML documents at the moment.
 */
public class XMLMapper
{
	public static final String XMLAlias = "swrlxml:";

	public static final String XMLDocumentOWLClassName = XMLAlias + "XMLDocument";
	public static final String XMLDocumentHasRootElementPropertyName = XMLAlias + "hasRootElement";
	public static final String XMLDocumentHasElementsPropertyName = XMLAlias + "hasElements";

	public static final String XMLElementOWLClassName = XMLAlias + "XMLElement";
	public static final String XMLHasNamePropertyName = XMLAlias + "hasName";
	public static final String XMLHasNamespacePrefixPropertyName = XMLAlias + "hasNamespacePrefix";
	public static final String XMLHasNamespaceURIPropertyName = XMLAlias + "hasNamespaceURI";
	public static final String XMLElementHasMappedIndividualsPropertyName = XMLAlias + "hasMappedIndividuals";
	public static final String XMLElementHasSubElementsPropertyName = XMLAlias + "hasSubElements";
	public static final String XMLElementHasAttributesPropertyName = XMLAlias + "hasAttributes";
	public static final String XMLElementHasContentPropertyName = XMLAlias + "hasContent";

	public static final String XMLAttributeOWLClassName = XMLAlias + "XMLAttribute";
	public static final String XMLAttributeHasValuePropertyName = XMLAlias + "hasValue";

	private OWLModel owlModel;

	private XMLProcessor xmlProcessor;

	private OWLProperty rootElementProperty, elementsProperty, subElementsProperty, nameProperty, namespacePrefixProperty, namespaceURIProperty, contentProperty,
			valueProperty, attributesProperty;

	public XMLMapper(OWLModel owlModel) throws XMLMapperException
	{
		this.owlModel = owlModel;

		this.xmlProcessor = new XMLProcessor();

		try {
			rootElementProperty = P3OWLUtil.createOWLObjectProperty(owlModel, XMLDocumentHasRootElementPropertyName);
			elementsProperty = P3OWLUtil.createOWLObjectProperty(owlModel, XMLDocumentHasElementsPropertyName);
			nameProperty = P3OWLUtil.createOWLDatatypeProperty(owlModel, XMLHasNamePropertyName);
			namespacePrefixProperty = P3OWLUtil.createOWLDatatypeProperty(owlModel, XMLHasNamespacePrefixPropertyName);
			namespaceURIProperty = P3OWLUtil.createOWLDatatypeProperty(owlModel, XMLHasNamespaceURIPropertyName);
			contentProperty = P3OWLUtil.createOWLDatatypeProperty(owlModel, XMLElementHasContentPropertyName);
			subElementsProperty = P3OWLUtil.createOWLObjectProperty(owlModel, XMLElementHasSubElementsPropertyName);
			valueProperty = P3OWLUtil.createOWLDatatypeProperty(owlModel, XMLAttributeHasValuePropertyName);
			attributesProperty = P3OWLUtil.createOWLObjectProperty(owlModel, XMLElementHasAttributesPropertyName);
		} catch (P3OWLUtilException e) {
			throw new XMLMapperException("error creating properties: " + e.getMessage());
		}
	}

	public Document owlDocument2Document() throws XMLMapperException
	{
		OWLNamedClass xmlDocumentClass;
		OWLIndividual xmlDocument, xmlRootElement = null;
		RDFResource propertyValue;
		Document doc = new Document();

		try {
			xmlDocumentClass = P3OWLUtil.getNamedClass(owlModel, XMLDocumentOWLClassName);
			xmlDocument = P3OWLUtil.getOWLIndividual(xmlDocumentClass, true, 1);
			propertyValue = P3OWLUtil.getObjectPropertyValue(owlModel, xmlDocument, XMLDocumentHasRootElementPropertyName, false);

			if (propertyValue == null)
				throw new XMLMapperException("no document root element specified");
			else if (propertyValue instanceof OWLIndividual)
				xmlRootElement = (OWLIndividual)propertyValue;
			else
				throw new XMLMapperException("invalid document root element " + propertyValue);

			owlElement2Element(doc, xmlRootElement, null);
		} catch (P3OWLUtilException e) {
			throw new XMLMapperException("error mapping OWL XML ontology to Document: " + e.getMessage());
		}

		return doc;
	}

	public OWLIndividual document2OWLDocument(Document doc) throws XMLMapperException
	{
		Element rootElement = doc.getRootElement();
		OWLIndividual xmlDocument;

		if (xmlProcessor.isSchema(rootElement))
			throw new XMLMapperException("not expecting schema root element");

		addSWRLXMLImport();

		try {
			xmlDocument = P3OWLUtil.createIndividualOfClass(owlModel, XMLDocumentOWLClassName);
			element2OWLElement(doc, xmlDocument, rootElement, null);
		} catch (P3OWLUtilException e) {
			throw new XMLMapperException("error mapping Document to OWL XML ontology: " + e.getMessage());
		}

		return xmlDocument;
	}

	private void owlElement2Element(Document doc, OWLIndividual xmlElement, Element parentElement) throws XMLMapperException, P3OWLUtilException
	{
		String elementName = P3OWLUtil.getDatavaluedPropertyValueAsString(owlModel, xmlElement, XMLHasNamePropertyName, true);
		String elementNamespacePrefix = P3OWLUtil.getDatavaluedPropertyValueAsString(owlModel, xmlElement, XMLHasNamespacePrefixPropertyName, false, "");
		String elementNamespaceURI = P3OWLUtil.getDatavaluedPropertyValueAsString(owlModel, xmlElement, XMLHasNamespaceURIPropertyName, false, "");
		Set<RDFResource> attributes = P3OWLUtil.getObjectPropertyValues(owlModel, xmlElement, XMLElementHasAttributesPropertyName, false);
		Set<RDFResource> subElements = P3OWLUtil.getObjectPropertyValues(owlModel, xmlElement, XMLElementHasSubElementsPropertyName, false);
		String content = P3OWLUtil.getDatavaluedPropertyValueAsString(owlModel, xmlElement, XMLElementHasContentPropertyName, false, "");
		Element element = xmlProcessor.createElement(doc, parentElement, elementName);

		element.setNamespace(Namespace.getNamespace(elementNamespacePrefix, elementNamespaceURI));

		if (content != null)
			element.addContent(content);

		for (RDFResource value : subElements) {
			if (value instanceof OWLIndividual) {
				OWLIndividual xmlSubElement = (OWLIndividual)value;
				owlElement2Element(doc, xmlSubElement, element);
			}
		}

		for (RDFResource value : attributes) {
			if (value instanceof OWLIndividual) {
				OWLIndividual xmlAttribute = (OWLIndividual)value;
				owlAttribute2Attribute(xmlAttribute, element);
			}
		}
	}

	private void owlAttribute2Attribute(OWLIndividual xmlAttribute, Element element) throws XMLMapperException, P3OWLUtilException
	{
		String attributeName = P3OWLUtil.getDatavaluedPropertyValueAsString(owlModel, xmlAttribute, XMLHasNamePropertyName, true);
		String attributeValue = P3OWLUtil.getDatavaluedPropertyValueAsString(owlModel, xmlAttribute, XMLAttributeHasValuePropertyName, true);
		String attributeNamespacePrefix = P3OWLUtil.getDatavaluedPropertyValueAsString(owlModel, xmlAttribute, XMLHasNamespacePrefixPropertyName, false, "");
		String attributeNamespaceURI = P3OWLUtil.getDatavaluedPropertyValueAsString(owlModel, xmlAttribute, XMLHasNamespaceURIPropertyName, false, "");

		xmlProcessor.setAttribute(element, attributeName, attributeValue, attributeNamespacePrefix, attributeNamespaceURI);
	}

	private void element2OWLElement(Document doc, OWLIndividual xmlDocument, Element element, OWLIndividual parentXMLElement)
			throws XMLMapperException, P3OWLUtilException
	{
		OWLIndividual xmlElement = P3OWLUtil.createIndividualOfClass(owlModel, XMLElementOWLClassName);
		String elementName = element.getName();
		String elementNamespacePrefix = element.getNamespace().getPrefix();
		String elementNamespaceURI = element.getNamespace().getURI();
		Filter textFilter = new ContentFilter(ContentFilter.TEXT);
		String content = "";

		if (parentXMLElement == null)
			P3OWLUtil.addPropertyValue(xmlDocument, rootElementProperty, xmlElement);
		else
			P3OWLUtil.addPropertyValue(parentXMLElement, subElementsProperty, xmlElement);

		P3OWLUtil.addPropertyValue(xmlDocument, elementsProperty, xmlElement);
		P3OWLUtil.addPropertyValue(xmlElement, nameProperty, elementName);
		P3OWLUtil.addPropertyValue(xmlElement, namespacePrefixProperty, elementNamespacePrefix);
		P3OWLUtil.addPropertyValue(xmlElement, namespaceURIProperty, elementNamespaceURI);

		for (Object o : element.getContent(textFilter)) {
			Text text = (Text)o;
			content += text.getValue();
		}

		P3OWLUtil.addPropertyValue(xmlElement, contentProperty, content);

		for (Attribute attribute : xmlProcessor.getAttributes(element))
			attribute2OWLAttribute(attribute, xmlElement);

		for (Element subElement : xmlProcessor.getSubElements(element))
			element2OWLElement(doc, xmlDocument, subElement, xmlElement);
	}

	private void attribute2OWLAttribute(Attribute attribute, OWLIndividual xmlElement) throws XMLMapperException, P3OWLUtilException
	{
		OWLIndividual xmlAttribute = P3OWLUtil.createIndividualOfClass(owlModel, XMLAttributeOWLClassName);
		String attributeName = attribute.getName();
		String attributeValue = attribute.getValue();
		String attributeNamespacePrefix = attribute.getNamespace().getPrefix();
		String attributeNamespaceURI = attribute.getNamespace().getURI();

		P3OWLUtil.addPropertyValue(xmlAttribute, nameProperty, attributeName);
		P3OWLUtil.addPropertyValue(xmlAttribute, valueProperty, attributeValue);
		P3OWLUtil.addPropertyValue(xmlAttribute, namespacePrefixProperty, attributeNamespacePrefix);
		P3OWLUtil.addPropertyValue(xmlAttribute, namespaceURIProperty, attributeNamespaceURI);
		P3OWLUtil.addPropertyValue(xmlElement, attributesProperty, xmlAttribute);
	}

	private void addSWRLXMLImport() throws XMLMapperException
	{
		ImportHelper importHelper = new ImportHelper(owlModel);

		try {
			owlModel.getNamespaceManager().setPrefix(new URI(SWRLNames.SWRLXML_NAMESPACE), SWRLNames.SWRLXML_PREFIX);

			if (owlModel.getTripleStoreModel().getTripleStore(SWRLNames.SWRLXML_IMPORT) == null)
				importHelper.addImport(new URI(SWRLNames.SWRLXML_IMPORT));

			importHelper.importOntologies(false);
		} catch (URISyntaxException e) {
			throw new XMLMapperException("error importing SWRLXML ontology: " + e.getMessage());
		} catch (OntologyLoadException e) {
			throw new XMLMapperException("error loading SWRLXML ontology: " + e.getMessage());
		}
	}

}
