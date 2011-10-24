package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlxml;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/*
 * Class that provides some simple XML processing functionality. 
 */
public class XMLProcessor
{
	private XMLOutputter serializer;

	public XMLProcessor()
	{
		serializer = new XMLOutputter(Format.getPrettyFormat());
	}

	/*
	 * Method that writes an XML file from an instance of a Document.
	 */
	public void generateXMLFile(Document doc, String outputXMLFileName) throws XMLProcessorException
	{
		OutputStream xmlStream = createOutputXMLStream(outputXMLFileName);

		if ((doc == null) || !doc.hasRootElement())
			throw new XMLProcessorException("document is empty");

		try {
			serializer.output(doc, xmlStream);
			xmlStream.close();
		} catch (IOException e) {
			throw new XMLProcessorException("error writing XML file '" + outputXMLFileName + "': " + e.getMessage());
		}
	}

	/*
	 * Method that returns XML string representing an instance of a Document.
	 */
	public String generateXMLString(Document doc) throws XMLProcessorException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		if ((doc == null) || !doc.hasRootElement())
			throw new XMLProcessorException("document is empty");

		try {
			serializer.output(doc, outputStream);
		} catch (IOException e) {
			throw new XMLProcessorException("error writing XML string: " + e.getMessage());
		}

		return outputStream.toString();
	}

	/*
	 * Method that reads a simple XML file and generates an instance of a Document from it.
	 */
	public Document processXMLStream(String inputXMLStreamName) throws XMLProcessorException
	{
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			InputStream xmlStream = createInputXMLStream(inputXMLStreamName);
			doc = builder.build(xmlStream);
			xmlStream.close();
		} catch (Exception e) {
			throw new XMLProcessorException("error opening XML file '" + inputXMLStreamName + "': " + e.getMessage());
		}

		return doc;
	}

	/*
	 * Method that reads an XML string and generates an instance of a Document from it.
	 */
	public Document processXMLString(String inputXMLString) throws XMLProcessorException
	{
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(new StringReader(inputXMLString));
		} catch (Exception e) {
			throw new XMLProcessorException("error processing XML string: " + e.getMessage());
		}

		return doc;
	}

	public Element createElement(Document doc, Element parentElement, String elementName)
	{
		Element element = new Element(elementName);

		if (parentElement == null)
			doc.setRootElement(element);
		else
			parentElement.addContent(element);

		return element;
	}

	public void setAttribute(Element element, String attributeName, String attributeValue, String namespacePrefix, String namespaceURI)
	{
		Attribute attribute = new Attribute(attributeName, attributeValue, Namespace.getNamespace(namespacePrefix, namespaceURI));

		element.setAttribute(attribute);
	}

	@SuppressWarnings("unchecked")
	public List<Attribute> getAttributes(Element element)
	{
		return new ArrayList<Attribute>(element.getAttributes());
	}

	public List<Element> getSubElements(Element element) 
	{
		List<Element> result = new ArrayList<Element>();

		for (Object o : element.getChildren())
			if (o instanceof Element)
				result.add((Element)o);

		return result;
	} 

	public boolean isSchema(Element element)
	{
		return hasName(element, "schema");
	}

	public boolean isElement(Element element)
	{
		return hasName(element, "element");
	}

	public boolean isAll(Element element)
	{
		return hasName(element, "all");
	}

	public boolean isComplexType(Element element)
	{
		return hasName(element, "complexType");
	}

	public boolean isSequence(Element element)
	{
		return hasName(element, "sequence");
	}

	public boolean isGroup(Element element)
	{
		return hasName(element, "group");
	}
	public boolean isAttributeGroup(Element element)
	{
		return hasName(element, "attributeGroup");
	}

	public boolean isChoice(Element element)
	{
		return hasName(element, "choice");
	}

	public boolean isAny(Element element)
	{
		return hasName(element, "any");
	}

	public boolean isAnyAttribute(Element element)
	{
		return hasName(element, "anyAttribute");
	}

	public boolean isAttribute(Element element)
	{
		return hasName(element, "attribute");
	}

	public boolean isComplexContent(Element element)
	{
		return hasName(element, "complexContent");
	}

	public boolean isSimpleContent(Element element)
	{
		return hasName(element, "simpleContent");
	}

	public boolean isSimpleContext(Element element)
	{
		return hasName(element, "simpleContext");
	}

	public boolean isSimpleType(Element element)
	{
		return hasName(element, "simpleType");
	}

	public boolean isRefElement(Element element)
	{
		return isElement(element) && hasRefAttribute(element);
	}

	public String getNameAttribute(Element element) throws XMLMapperException
	{
		return getNameAttributeValue(element);
	}

	public String getReafAttribute(Element element) throws XMLMapperException
	{
		return getRefAttributeValue(element);
	}

	public String getTypeAttribute(Element element) throws XMLMapperException
	{
		return getTypeAttributeValue(element);
	}
	public String getUseAttribute(Element element) throws XMLMapperException
	{
		return getUseAttributeValue(element);
	}

	public String getMinOccursAttribute(Element element) throws XMLMapperException
	{
		return getMinOccursAttributeValue(element);
	}

	public String getMaxOccursAttribute(Element element) throws XMLMapperException
	{
		return getMaxOccursAttributeValue(element);
	}

	public boolean hasNameAttribute(Element element)
	{
		return hasAttribute(element, "name");
	}

	public boolean hasValueAttribute(Element element)
	{
		return hasAttribute(element, "value");
	}

	public boolean hasBaseAttribute(Element element)
	{
		return hasAttribute(element, "base");
	}

	public boolean hasMixedAttribute(Element element)
	{
		return hasAttribute(element, "mixed");
	}

	public boolean hasTypeAttribute(Element element)
	{
		return hasAttribute(element, "type");
	}

	public boolean hasDefaultAttribute(Element element)
	{
		return hasAttribute(element, "default");
	}

	public boolean hasFixedAttribute(Element element)
	{
		return hasAttribute(element, "fixed");
	}

	public boolean hasRefAttribute(Element element)
	{
		return hasAttribute(element, "ref");
	}

	public boolean hasUseAttribute(Element element)
	{
		return hasAttribute(element, "use");
	}

	public boolean hasMaxOccursAttribute(Element element)
	{
		return hasAttribute(element, "maxOccurs");
	}

	public boolean hasMinOccursAttribute(Element element)
	{
		return hasAttribute(element, "minOccurs");
	}

	public String getNameAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "name");
	}

	public String getValueAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "value");
	}

	public String getMixedAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "mixed");
	}

	public String getBaseAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "base");
	}

	public String getTypeAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "type");
	}

	public String getDefaultAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "default");
	}
	
	public String getFixedAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "fixed");
	}

	public String getRefAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "ref");
	}

	public String getUseAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "use");
	}

	public String getMaxOccursAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "maxOccurs");
	}

	public String getMinOccursAttributeValue(Element element) throws XMLMapperException
	{
		return getAttributeValue(element, "minOccurs");
	}

	public Element getComplexTypeChild(Element element) throws XMLMapperException
	{
		if (!hasComplexTypeChild(element))
			throw new XMLMapperException("expecting complexType child for element " + getNameAttributeValue(element));
		return element.getChild("complexType", element.getNamespace());
	}

	public String getAttributeValue(Element element, String attributeName) throws XMLMapperException
	{
		if (!hasAttribute(element, attributeName))
			throw new XMLMapperException("no " + attributeName + " attribute found in element " + element.getName());

		return element.getAttributeValue(attributeName);
	}

	public Element getFirstChild(Element element) throws XMLMapperException
	{
		if (element.getChildren() == null)
			throw new XMLMapperException("getFirstChild called on non-parent element " + getNameAttributeValue(element));

		return (Element)element.getChildren().get(0);
	}

	public boolean hasChildren(Element element)
	{
		return element.getChildren() != null;
	}

	public boolean hasComplexTypeChild(Element element)
	{
		return isElement(element) && element.getChild("complexType", element.getNamespace()) != null;
	}

	public boolean hasSimpleTypeChild(Element element)
	{
		return isElement(element) && element.getChild("simpleTypeChild", element.getNamespace()) != null;
	}

	private boolean hasName(Element element, String name)
	{
		return element.getName() != null && element.getName().equals(name);
	}

	private boolean hasAttribute(Element element, String attributeName)
	{
		return element.getAttributeValue(attributeName) != null;
	}

	private OutputStream createOutputXMLStream(String outputXMLStreamName) throws XMLProcessorException
	{
		try {
			return new FileOutputStream(outputXMLStreamName);
		} catch (IOException e) {
			throw new XMLProcessorException("error creating XML serializer for XML stream '" + outputXMLStreamName + "': " + e.getMessage());
		}
	}

	private InputStream createInputXMLStream(String inputXMLStreamName) throws XMLProcessorException
	{
		InputStream xmlStream = null;

		try {
			URL url = new URL(inputXMLStreamName);
			String protocol = url.getProtocol();
			if (protocol.equals("file")) {
				String path = url.getPath();
				xmlStream = new FileInputStream(path);
			} else
				xmlStream = url.openStream();
		} catch (MalformedURLException e) {
			throw new XMLProcessorException("invalid URL for XML stream '" + inputXMLStreamName + "': " + e.getMessage());
		} catch (IOException e) {
			throw new XMLProcessorException("IO error opening XML stream '" + inputXMLStreamName + "': " + e.getMessage());
		}

		return xmlStream;
	}
}
