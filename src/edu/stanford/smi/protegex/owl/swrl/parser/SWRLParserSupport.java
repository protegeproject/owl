package edu.stanford.smi.protegex.owl.swrl.parser;

import edu.stanford.smi.protegex.owl.model.NamespaceUtil;
import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltin;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

public class SWRLParserSupport
{
	public final OWLModel owlModel;
	private final SWRLFactory swrlFactory;

	public SWRLParserSupport(OWLModel owlModel)
	{
		this.owlModel = owlModel;
		this.swrlFactory = new SWRLFactory(owlModel);
	}

	public boolean isValidOWLIndividualName(String name)
	{
		try {
			return ParserUtils.getOWLIndividualFromName(this.owlModel, name) != null;
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean isValidOWLClassName(String name)
	{
		try {
			RDFResource resource = getRDFResource(name);

			if (resource == null)
				return false;

			if (resource instanceof OWLNamedClass)
				return true;
			else
				return false;
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean isValidSWRLVariableName(String s)
	{
		if (s.length() == 0)
			return false;

		if (!Character.isJavaIdentifierStart(s.charAt(0)))
			return false;

		for (int i = 1; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!(Character.isJavaIdentifierPart(c) || c == ':' || c == '-')) {
				return false;
			}
		}
		return true;
	}

	public boolean isValidOWLPropertyName(String name) throws SWRLParseException
	{
		try {
			RDFResource resource = getRDFResource(name);

			if (resource == null)
				return false;

			if (resource instanceof OWLProperty)
				return true;
			else
				return false;
		} catch (Throwable t) {
			return false;
		}
	}

	public boolean isSWRLBuiltIn(String identifier)
	{
		RDFResource resource = getRDFResource(identifier);
		return resource != null && resource.getProtegeType().getName().equals(SWRLNames.Cls.BUILTIN);
	}

	public boolean isOWLClass(String identifier)
	{
		return ParserUtils.getOWLClassFromName(this.owlModel, identifier) != null;
	}

	public boolean isOWLObjectProperty(String identifier)
	{
		return ParserUtils.getOWLObjectPropertyFromName(this.owlModel, identifier) != null;
	}

	public boolean isOWLDataProperty(String identifier)
	{
		return ParserUtils.getOWLDatatypePropertyFromName(this.owlModel, identifier) != null;
	}

	public boolean isXSDDatatype(String identifier)
	{
		return (identifier.startsWith("xsd:") && XMLSchemaDatatypes.getSlotSymbols().contains(identifier.substring(4)));
	}

	public OWLIndividual getOWLNamedIndividual(String name) throws SWRLParseException
	{
		OWLIndividual resource = ParserUtils.getOWLIndividualFromName(this.owlModel, name);

		if (resource == null)
			throw new SWRLParseException(name + " is not a valid OWL individual");

		return resource;
	}

	public OWLNamedClass getOWLClass(String name) throws SWRLParseException
	{
		OWLNamedClass resource = ParserUtils.getOWLClassFromName(this.owlModel, name);

		if (resource == null)
			throw new SWRLParseException(name + " is not a valid OWL class");

		return resource;
	}

	public OWLProperty getOWLProperty(String name) throws SWRLParseException
	{
		RDFProperty resource = ParserUtils.getRDFPropertyFromName(this.owlModel, name);

		if (resource == null || !(resource instanceof OWLProperty))
			throw new SWRLParseException(name + " is not a valid OWL property");

		return (OWLProperty)resource;
	}

	public OWLObjectProperty getOWLObjectProperty(String name) throws SWRLParseException
	{
		RDFProperty resource = ParserUtils.getRDFPropertyFromName(this.owlModel, name);

		if (resource == null || !(resource instanceof OWLObjectProperty))
			throw new SWRLParseException(name + " is not a valid OWL object property");

		return (OWLObjectProperty)resource;
	}

	public OWLDatatypeProperty getOWLDataProperty(String name) throws SWRLParseException
	{
		RDFProperty resource = ParserUtils.getRDFPropertyFromName(this.owlModel, name);

		if (resource == null || !(resource instanceof OWLDatatypeProperty))
			throw new SWRLParseException(name + " is not a valid OWL data property");

		return (OWLDatatypeProperty)resource;
	}

	public SWRLBuiltin getSWRLBuiltIn(String builtInName)
	{
		return getSWRLFactory().getBuiltin(NamespaceUtil.getFullName(getOWLModel(), builtInName));
	}

	public SWRLVariable getSWRLVariable(String name) throws SWRLParseException
	{
		RDFResource resource = getOWLModel().getRDFResource(NamespaceUtil.getFullName(getOWLModel(), name));

		if (resource instanceof SWRLVariable)
			return (SWRLVariable)resource;
		else if (resource == null)
			return getSWRLFactory().createVariable(NamespaceUtil.getFullName(getOWLModel(), name));
		else
			throw new SWRLParseException(name + " cannot be used as a SWRL variable name");
	}

	private RDFResource getRDFResource(String resourceName)
	{
		return ParserUtils.getRDFResourceFromName(this.owlModel, resourceName);
	}

	public void checkThatSWRLVariableNameIsValid(String variableName) throws SWRLParseException
	{
		if (!isValidSWRLVariableName(variableName))
			throw new SWRLParseException("Invalid SWRL variable name " + variableName);

		RDFResource resource = getRDFResource(variableName);

		if ((resource != null) && !(resource instanceof SWRLVariable))
			throw new SWRLParseException("Invalid SWRL variable name " + variableName
					+ ". Cannot use name of existing OWL class, individual, property, or datatype");
	}

	public OWLDataRange getOWLDataRange()
	{
		return getOWLModel().createOWLDataRange();
	}

	public RDFProperty getOWLOneOfProperty()
	{
		return getOWLModel().getOWLOneOfProperty();
	}

	public RDFObject getOWLXSDStringLiteral(String value)
	{
		return getOWLModel().createRDFSLiteral(value, getOWLModel().getXSDstring());
	}

	public RDFObject getOWLXSDBooleanLiteral(String value)
	{
		return getOWLModel().createRDFSLiteral(value, getOWLModel().getXSDboolean());
	}

	public RDFObject getOWLXSDIntLiteral(String value)
	{
		return getOWLModel().createRDFSLiteral(value, getOWLModel().getXSDint());
	}

	public RDFObject getOWLXSDDoubleLiteral(String value)
	{
		return getOWLModel().createRDFSLiteral(value, getOWLModel().getXSDdouble());
	}

	public RDFObject getOWLLiteral(String rawValue, String datatypeName)
	{
		return getOWLModel().createRDFSLiteral(rawValue, datatypeName);
	}

	// Possible valid identifiers include 'http://swrl.stanford.edu/ontolgies/built-ins/3.3/swrlx.owl#createIndividual'.
	@SuppressWarnings("unused")
	private boolean isValidIdentifier(String s)
	{
		if (s.length() == 0)
			return false;

		if (!Character.isJavaIdentifierStart(s.charAt(0)) && s.charAt(0) != ':')
			return false; // HACK to deal with ":TO" and ":FROM".

		for (int i = 1; i < s.length(); i++) {
			char c = s.charAt(i);
			if (!(Character.isJavaIdentifierPart(c) || c == ':' || c == '-' || c == '#' || c == '/' || c == '.')) {
				return false;
			}
		}
		return true;
	}

	private OWLModel getOWLModel()
	{
		return this.owlModel;
	}

	private SWRLFactory getSWRLFactory()
	{
		return this.swrlFactory;
	}
}
