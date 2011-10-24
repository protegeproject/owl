package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlxml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.MultiArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.AbstractSWRLBuiltInLibrary;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLBuiltInLibraryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidBuiltInArgumentException;
import edu.stanford.smi.protegex.owl.swrl.owlapi.OWLClass;
import edu.stanford.smi.protegex.owl.swrl.owlapi.OWLNamedIndividual;

public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
	private static String SWRLXMLLibraryName = "SWRLXMLBuiltIns";

	private OWLClass xmlElementOWLClass = null;

	public SWRLBuiltInLibraryImpl() throws SWRLBuiltInLibraryException
	{
		super(SWRLXMLLibraryName);

		reset();
	}

	private XMLProcessor xmlProcessor;
	private XMLBridgeMapper xmlMapper;

	private Map<String, OWLNamedIndividual> documentMappings; // File name to OWL document individuals
	private Map<String, Document> documents; // Individual name to Document

	private Map<String, Set<OWLNamedIndividual>> elementMappings; // XML path to element individuals
	private Map<String, Element> elements; // Individual name to Element

	public void reset() throws SWRLBuiltInLibraryException
	{
		xmlProcessor = new XMLProcessor();

		xmlMapper = new XMLBridgeMapper();

		documentMappings = new HashMap<String, OWLNamedIndividual>();
		documents = new HashMap<String, Document>();

		elementMappings = new HashMap<String, Set<OWLNamedIndividual>>();
		elements = new HashMap<String, Element>();
	}

	public boolean makeXMLDocument(List<BuiltInArgument> arguments) throws BuiltInException
	{
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());
		checkForUnboundNonFirstArguments(arguments);

		if (isUnboundArgument(0, arguments)) {
			String inputXMLStreamName = getArgumentAsAString(1, arguments);
			OWLNamedIndividual owlDocument = null;
			Document doc;

			if (!documentMappings.containsKey(inputXMLStreamName)) {
				try {
					doc = xmlProcessor.processXMLStream(inputXMLStreamName);
					owlDocument = xmlMapper.document2OWLDocument(doc, getBuiltInBridge());
					documentMappings.put(inputXMLStreamName, owlDocument);
					documents.put(owlDocument.getURI(), doc);
				} catch (XMLProcessorException e) {
					throw new BuiltInException("error processing XML stream " + inputXMLStreamName + ": " + e.getMessage());
				} catch (XMLBridgeMapperException e) {
					throw new BuiltInException("error mapping XML stream " + inputXMLStreamName + ": " + e.getMessage());
				}
			} else // We have a cached copy
				owlDocument = documentMappings.get(inputXMLStreamName);

			arguments.get(0).setBuiltInResult(createIndividualArgument(owlDocument.getURI())); // Bind the result to the first parameter
			result = true;
		}
		return result;
	}

	public boolean element(List<BuiltInArgument> arguments) throws BuiltInException
	{
		boolean result = false;

		checkNumberOfArgumentsEqualTo(3, arguments.size());
		checkForUnboundNonFirstArguments(arguments);

		if (isUnboundArgument(0, arguments)) {
			Document doc = getArgumentAsADocument(1, arguments);
			String path = getArgumentAsAString(2, arguments);
			Set<OWLNamedIndividual> owlElements;

			if (!elementMappings.containsKey(path)) {
				try {
					XPath xPath = XPath.newInstance(path);
					Iterator elementIterator = xPath.selectNodes(doc).iterator();
					owlElements = new HashSet<OWLNamedIndividual>();

					while (elementIterator.hasNext()) {
						Object o = elementIterator.next();
						OWLNamedIndividual xmlElement = getBuiltInBridge().injectOWLIndividualDeclaration(getXMLElementOWLClass());
						Element element;

						if (!(o instanceof Element))
							throw new BuiltInException("path " + path + " must only refer to XML elements, found " + o.getClass());

						element = (Element)o;
						elements.put(xmlElement.getURI(), element);

						owlElements.add(xmlElement);
					}
					if (!owlElements.isEmpty())
						elementMappings.put(path, owlElements);
				} catch (JDOMException e) {
					throw new BuiltInException("JDOM error processing XML path " + path + ": " + e.getMessage());
				}
			} else
				owlElements = elementMappings.get(path);

			if (owlElements.isEmpty())
				result = false;
			else if (owlElements.size() == 1) {
				OWLNamedIndividual individual = (OWLNamedIndividual)owlElements.toArray()[0]; // Bind the single individual to the first parameter
				arguments.get(0).setBuiltInResult(createIndividualArgument(individual.getURI()));
				result = true;
			} else {
				MultiArgument multiArgument = createMultiArgument();
				for (OWLNamedIndividual xmlElement : owlElements)
					multiArgument.addArgument(createIndividualArgument(xmlElement.getURI()));
				result = !multiArgument.hasNoArguments();
				arguments.get(0).setBuiltInResult(multiArgument);
			}
		}
		return result;
	}

	public boolean subElement(List<BuiltInArgument> arguments) throws BuiltInException
	{
		boolean result = false;

		checkNumberOfArgumentsEqualTo(3, arguments.size());
		checkForUnboundNonFirstArguments(arguments);

		if (isUnboundArgument(0, arguments)) {
			Element parent = getArgumentAsAnElement(1, arguments);
			String path = getArgumentAsAString(2, arguments);
			Set<OWLNamedIndividual> owlElements;

			try {
				XPath xPath = XPath.newInstance(path);
				Iterator elementIterator = xPath.selectNodes(parent).iterator();
				owlElements = new HashSet<OWLNamedIndividual>();

				while (elementIterator.hasNext()) {
					Object o = elementIterator.next();
					OWLNamedIndividual xmlElement = getBuiltInBridge().injectOWLIndividualDeclaration(getXMLElementOWLClass());
					Element element;

					if (!(o instanceof Element))
						throw new BuiltInException("path " + path + " must only refer to XML elements, found " + o.getClass());

					element = (Element)o;
					elements.put(xmlElement.getURI(), element);

					owlElements.add(xmlElement);
				}
			} catch (JDOMException e) {
				throw new BuiltInException("JDOM error processing XML path " + path + ": " + e.getMessage());
			}

			if (owlElements.isEmpty())
				result = false;
			else if (owlElements.size() == 1) {
				OWLNamedIndividual individual = (OWLNamedIndividual)owlElements.toArray()[0]; // Bind the single individual to the first parameter
				arguments.get(0).setBuiltInResult(createIndividualArgument(individual.getURI()));
				result = true;
			} else {
				MultiArgument multiArgument = createMultiArgument();
				for (OWLNamedIndividual xmlElement : owlElements)
					multiArgument.addArgument(createIndividualArgument(xmlElement.getURI()));
				arguments.get(0).setBuiltInResult(multiArgument);
				result = !multiArgument.hasNoArguments();
			}
		}
		return result;
	}

	public boolean attributeValue(List<BuiltInArgument> arguments) throws BuiltInException
	{
		boolean result = false;

		checkNumberOfArgumentsEqualTo(3, arguments.size());
		checkForUnboundNonFirstArguments(arguments);

		if (isUnboundArgument(0, arguments)) {
			Element element = getArgumentAsAnElement(1, arguments);
			String attributeName = getArgumentAsAString(2, arguments);
			String attributeValue = element.getAttributeValue(attributeName);

			if (attributeValue != null) {
				arguments.get(0).setBuiltInResult(createDataValueArgument(attributeValue));
				result = true;
			}
		}
		return result;
	}

	private Document getArgumentAsADocument(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
	{
		Document document = null;

		if (isArgumentAnIndividual(argumentNumber, arguments)) {
			String individualURI = getArgumentAsAnIndividualURI(argumentNumber, arguments);

			if (getBuiltInBridge().isOWLIndividualOfClass(individualURI, XMLBridgeMapper.XMLDocumentOWLClassName)) {
			} else
				throw new InvalidBuiltInArgumentException(argumentNumber, "individual " + individualURI + " is not a " + XMLBridgeMapper.XMLDocumentOWLClassName);

			if (documents.containsKey(individualURI))
				document = documents.get(individualURI);
			else
				throw new InvalidBuiltInArgumentException(argumentNumber, "" + XMLBridgeMapper.XMLDocumentOWLClassName + " individual " + individualURI
						+ " does not refer to a valid document");
		} else
			throw new InvalidBuiltInArgumentException(argumentNumber, "expecting a " + XMLBridgeMapper.XMLDocumentOWLClassName + " individual" + ", got "
					+ arguments.get(argumentNumber));
		return document;
	} 

	private Element getArgumentAsAnElement(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
	{
		Element element = null;

		if (isArgumentAnIndividual(argumentNumber, arguments)) {
			String individualURI = getArgumentAsAnIndividualURI(argumentNumber, arguments);

			if (getBuiltInBridge().isOWLIndividualOfClass(individualURI, XMLBridgeMapper.XMLElementOWLClassName)) {
			} else
				throw new InvalidBuiltInArgumentException(argumentNumber, "individual " + individualURI + " is not a " + XMLBridgeMapper.XMLElementOWLClassName);

			if (elements.containsKey(individualURI))
				element = elements.get(individualURI);
			else
				throw new InvalidBuiltInArgumentException(argumentNumber, "" + XMLBridgeMapper.XMLElementOWLClassName + " individual " + individualURI
						+ " does not refer to a valid element");
		} else
			throw new InvalidBuiltInArgumentException(argumentNumber, "expecting a " + XMLBridgeMapper.XMLElementOWLClassName + " individual" + ", got "
					+ arguments.get(argumentNumber) + "");
		return element;
	} 

	private OWLClass getXMLElementOWLClass() throws BuiltInException
	{
		if (xmlElementOWLClass == null) {
			xmlElementOWLClass = getBuiltInBridge().getOWLDataFactory().getOWLClass(XMLBridgeMapper.XMLElementOWLClassName);
		}

		return xmlElementOWLClass;
	} 
}
