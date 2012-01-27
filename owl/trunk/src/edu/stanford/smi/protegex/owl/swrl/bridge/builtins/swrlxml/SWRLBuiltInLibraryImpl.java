
package org.protege.swrltab.bridge.builtins.swrlxml;

import java.net.URI;
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
import org.protege.swrlapi.adapters.OWLClassAdapter;
import org.protege.swrlapi.adapters.OWLNamedIndividualAdapter;
import org.protege.swrlapi.arguments.SWRLBuiltInArgument;
import org.protege.swrlapi.arguments.SWRLMultiArgument;
import org.protege.swrlapi.builtins.AbstractSWRLBuiltInLibrary;
import org.protege.swrlapi.exceptions.BuiltInException;
import org.protege.swrlapi.exceptions.InvalidBuiltInArgumentException;
import org.protege.swrlapi.exceptions.SWRLBuiltInLibraryException;

import edu.stanford.smi.protegex.owl.model.OWLModel;

public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
	private static String SWRLXMLLibraryName = "SWRLXMLBuiltIns";

	private OWLClassAdapter xmlElementOWLClass = null;

	public SWRLBuiltInLibraryImpl() throws SWRLBuiltInLibraryException
	{
		super(SWRLXMLLibraryName);

		reset();
	}

	private XMLProcessor xmlProcessor;
	private XMLBridgeMapper xmlMapper;

	private Map<String, OWLNamedIndividualAdapter> documentMappings; // File name to OWL document individuals
	private Map<URI, Document> documentMap; // Individual URI to Document

	private Map<String, Set<OWLNamedIndividualAdapter>> elementMappings; // XML path to element individuals
	private Map<URI, Element> elementMap; // Individual URI to Element

	public void reset() throws SWRLBuiltInLibraryException
	{
		xmlProcessor = new XMLProcessor();

		documentMappings = new HashMap<String, OWLNamedIndividualAdapter>();
		documentMap = new HashMap<URI, Document>();

		elementMappings = new HashMap<String, Set<OWLNamedIndividualAdapter>>();
		elementMap = new HashMap<URI, Element>();

		try {
			xmlMapper = new XMLBridgeMapper(getBuiltInBridge(), getOWLModel());
		} catch (XMLBridgeMapperException e) {
			throw new SWRLBuiltInLibraryException("error initializing the XML mapper: " + e.getMessage());
		}
	}

	public boolean makeXMLDocument(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());
		checkForUnboundNonFirstArguments(arguments);

		if (isUnboundArgument(0, arguments)) {
			String inputXMLStreamName = getArgumentAsAString(1, arguments);
			OWLNamedIndividualAdapter owlDocument = null;
			Document doc;

			if (!documentMappings.containsKey(inputXMLStreamName)) {
				try {
					doc = xmlProcessor.processXMLStream(inputXMLStreamName);
					owlDocument = xmlMapper.document2OWLDocument(doc, getBuiltInBridge());
					documentMappings.put(inputXMLStreamName, owlDocument);
					documentMap.put(owlDocument.getURI(), doc);
				} catch (XMLProcessorException e) {
					throw new BuiltInException("error processing XML stream " + inputXMLStreamName + ": " + e.getMessage());
				} catch (XMLBridgeMapperException e) {
					throw new BuiltInException("error mapping XML stream " + inputXMLStreamName + ": " + e.getMessage());
				}
			} else
				// We have a cached copy
				owlDocument = documentMappings.get(inputXMLStreamName);

			arguments.get(0).setBuiltInResult(createIndividualArgument(owlDocument)); // Bind the result to the first parameter
			result = true;
		}
		return result;
	}

	public boolean element(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(3, arguments.size());
		checkForUnboundNonFirstArguments(arguments);
		boolean result = false;

		if (isUnboundArgument(0, arguments)) {
			Document doc = getArgumentAsADocument(1, arguments);
			String path = getArgumentAsAString(2, arguments);
			Set<OWLNamedIndividualAdapter> xmlElementIndividuals;

			if (!elementMappings.containsKey(path)) {
				try {
					XPath xPath = XPath.newInstance(path);
					Iterator<?> elementIterator = xPath.selectNodes(doc).iterator();
					xmlElementIndividuals = new HashSet<OWLNamedIndividualAdapter>();

					while (elementIterator.hasNext()) {
						Object o = elementIterator.next();
						OWLNamedIndividualAdapter xmlElement = injectOWLNamedIndividualOfClass(getXMLElementOWLClass());

						Element element;

						if (!(o instanceof Element))
							throw new BuiltInException("path " + path + " must only refer to XML elements, found " + o.getClass());

						element = (Element)o;
						elementMap.put(xmlElement.getURI(), element);

						xmlElementIndividuals.add(xmlElement);
					}
					if (!xmlElementIndividuals.isEmpty())
						elementMappings.put(path, xmlElementIndividuals);
				} catch (JDOMException e) {
					throw new BuiltInException("JDOM error processing XML path " + path + ": " + e.getMessage());
				}
			} else
				xmlElementIndividuals = elementMappings.get(path);

			if (xmlElementIndividuals.isEmpty())
				result = false;
			else if (xmlElementIndividuals.size() == 1) { // Bind the single individual to the first parameter
				OWLNamedIndividualAdapter individual = (OWLNamedIndividualAdapter)xmlElementIndividuals.toArray()[0];
				arguments.get(0).setBuiltInResult(createIndividualArgument(individual));
				result = true;
			} else {
				SWRLMultiArgument multiArgument = createMultiArgument();
				for (OWLNamedIndividualAdapter xmlElementIndividual : xmlElementIndividuals)
					multiArgument.addArgument(createIndividualArgument(xmlElementIndividual));
				result = !multiArgument.hasNoArguments();
				arguments.get(0).setBuiltInResult(multiArgument);
			}
		}
		return result;
	}

	public boolean subElement(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		boolean result = false;

		checkNumberOfArgumentsEqualTo(3, arguments.size());
		checkForUnboundNonFirstArguments(arguments);

		if (isUnboundArgument(0, arguments)) {
			Element parent = getArgumentAsAnElement(1, arguments);
			String path = getArgumentAsAString(2, arguments);
			Set<OWLNamedIndividualAdapter> xmlElementIndividuals;

			try {
				XPath xPath = XPath.newInstance(path);
				Iterator<?> elementIterator = xPath.selectNodes(parent).iterator();
				xmlElementIndividuals = new HashSet<OWLNamedIndividualAdapter>();

				while (elementIterator.hasNext()) {
					Object o = elementIterator.next();
					OWLNamedIndividualAdapter xmlElement = injectOWLNamedIndividualOfClass(getXMLElementOWLClass());
					Element element;

					if (!(o instanceof Element))
						throw new BuiltInException("path " + path + " must only refer to XML elements, found " + o.getClass());

					element = (Element)o;
					elementMap.put(xmlElement.getURI(), element);

					xmlElementIndividuals.add(xmlElement);
				}
			} catch (JDOMException e) {
				throw new BuiltInException("JDOM error processing XML path " + path + ": " + e.getMessage());
			}

			if (xmlElementIndividuals.isEmpty())
				result = false;
			else if (xmlElementIndividuals.size() == 1) { // Bind the single individual to the first parameter
				OWLNamedIndividualAdapter xmlElementIndividual = (OWLNamedIndividualAdapter)xmlElementIndividuals.toArray()[0];
				arguments.get(0).setBuiltInResult(createIndividualArgument(xmlElementIndividual));
				result = true;
			} else {
				SWRLMultiArgument multiArgument = createMultiArgument();
				for (OWLNamedIndividualAdapter xmlElementIndividual : xmlElementIndividuals)
					multiArgument.addArgument(createIndividualArgument(xmlElementIndividual));
				arguments.get(0).setBuiltInResult(multiArgument);
				result = !multiArgument.hasNoArguments();
			}
		}
		return result;
	}

	public boolean attributeValue(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		boolean result = false;

		checkNumberOfArgumentsEqualTo(3, arguments.size());
		checkForUnboundNonFirstArguments(arguments);

		if (isUnboundArgument(0, arguments)) {
			Element element = getArgumentAsAnElement(1, arguments);
			String attributeName = getArgumentAsAString(2, arguments);
			String attributeValue = element.getAttributeValue(attributeName);

			if (attributeValue != null) {
				arguments.get(0).setBuiltInResult(createLiteralArgument(attributeValue));
				result = true;
			}
		}
		return result;
	}

	private Document getArgumentAsADocument(int argumentNumber, List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		Document document = null;

		if (isArgumentAnIndividual(argumentNumber, arguments)) {
			URI individualURI = getArgumentAsAnIndividualURI(argumentNumber, arguments);

			if (isOWLIndividualOfType(individualURI, createURI(XMLBridgeMapper.XMLDocumentOWLClassName))) {
			} else
				throw new InvalidBuiltInArgumentException(argumentNumber, "individual " + individualURI + " is not a " + XMLBridgeMapper.XMLDocumentOWLClassName);

			if (documentMap.containsKey(individualURI))
				document = documentMap.get(individualURI);
			else
				throw new InvalidBuiltInArgumentException(argumentNumber, "" + XMLBridgeMapper.XMLDocumentOWLClassName + " individual " + individualURI
						+ " does not refer to a valid document");
		} else
			throw new InvalidBuiltInArgumentException(argumentNumber, "expecting a " + XMLBridgeMapper.XMLDocumentOWLClassName + " individual" + ", got "
					+ arguments.get(argumentNumber));
		return document;
	}

	private Element getArgumentAsAnElement(int argumentNumber, List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		Element element = null;

		if (isArgumentAnIndividual(argumentNumber, arguments)) {
			URI individualURI = getArgumentAsAnIndividualURI(argumentNumber, arguments);

			if (isOWLIndividualOfType(individualURI, createURI(XMLBridgeMapper.XMLElementOWLClassName))) {
			} else
				throw new InvalidBuiltInArgumentException(argumentNumber, "individual " + individualURI + " is not a " + XMLBridgeMapper.XMLElementOWLClassName);

			if (elementMap.containsKey(individualURI))
				element = elementMap.get(individualURI);
			else
				throw new InvalidBuiltInArgumentException(argumentNumber, "" + XMLBridgeMapper.XMLElementOWLClassName + " individual " + individualURI
						+ " does not refer to a valid element");
		} else
			throw new InvalidBuiltInArgumentException(argumentNumber, "expecting a " + XMLBridgeMapper.XMLElementOWLClassName + " individual" + ", got "
					+ arguments.get(argumentNumber) + "");
		return element;
	}
	
	private OWLClassAdapter getXMLElementOWLClass() throws BuiltInException
	{
		if (xmlElementOWLClass == null) 
			xmlElementOWLClass = getBuiltInBridge().getOWLAdapterFactory().getOWLClass(createURI(XMLBridgeMapper.XMLElementOWLClassName));

		return xmlElementOWLClass;
	}
	
	private boolean isOWLIndividualOfType(URI individualURI, URI classURI) throws SWRLBuiltInLibraryException
	{
		return getBuiltInBridge().getActiveOntology().isOWLIndividualOfType(individualURI, classURI);
	}
	
	private OWLModel getOWLModel()
	{
		return null;
	}
}
