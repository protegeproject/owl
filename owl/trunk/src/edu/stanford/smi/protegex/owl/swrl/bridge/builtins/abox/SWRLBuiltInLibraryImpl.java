// TODO  P3 dependent
// TODO lot of repetition in methods. Clean up.
// TODO :has prefix

package org.protege.swrltab.bridge.builtins.abox;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.XSDDuration;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtil;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtilException;

/**
 * Implementations library for SWRL ABox built-in methods. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLABoxBuiltIns">here</a> for documentation on
 * this library.
 * <p>
 * See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
	private static String SWRLABoxLibraryName = "SWRLABoxBuiltIns";

	public SWRLBuiltInLibraryImpl()
	{
		super(SWRLABoxLibraryName);
	}

	public void reset()
	{
	}

	/**
	 * Determine if a single argument is an OWL individual. If the argument is unbound, bind it to all OWL individuals in an ontology.
	 */
	public boolean isIndividual(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		boolean isUnboundArgument = isUnboundArgument(0, arguments);
		boolean result = false;

		if (isUnboundArgument) {
			SWRLMultiArgument multiArgument = createMultiArgument();
			for (OWLNamedIndividualAdapter individual : getBuiltInBridge().getOWLIndividuals()) {
				SWRLIndividualBuiltInArgument argument = createIndividualArgument(individual);
				multiArgument.addArgument(argument);
			}
			arguments.get(0).setBuiltInResult(multiArgument);
			result = !multiArgument.hasNoArguments();
		} else {
			URI individualURI = getArgumentAsAnIndividualURI(0, arguments);
			result = getBuiltInBridge().isOWLIndividual(individualURI);
		}

		return result;
	}

	// TODO: this needs serious cleanup.
	/**
	 * Returns true if the individual named by the first argument has a property specified by the second argument with the value specified by the third argument.
	 * If the third argument in unbound, bind it to all the values for this property for the specified individual.
	 */
	public boolean hasValue(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		URI individualURI, propertyURI = null;
		Object propertyValue = null;
		boolean propertyValueSupplied;
		boolean result = false, isObjectProperty;

		checkNumberOfArgumentsEqualTo(3, arguments.size());
		individualURI = getArgumentAsAnIndividualURI(0, arguments);
		propertyURI = getArgumentAsAPropertyURI(1, arguments);

		propertyValueSupplied = !isUnboundArgument(2, arguments);

		try {
			isObjectProperty = getBuiltInBridge().isOWLObjectProperty(propertyURI);

			if (getIsInConsequent()) {
				OWLNamedIndividualAdapter subject = getBuiltInBridge().getOWLAdapterFactory().getOWLIndividual(individualURI);

				if (isObjectProperty) {
					OWLObjectPropertyAssertionAxiomAdapter axiom;
					SWRLIndividualBuiltInArgument argument = getArgumentAsAnIndividual(2, arguments);
					OWLNamedIndividualAdapter value = getBuiltInBridge().getOWLAdapterFactory().getOWLIndividual(argument.getURI());
					OWLObjectPropertyAdapter property = getBuiltInBridge().getOWLAdapterFactory().getOWLObjectProperty(propertyURI);
					axiom = getBuiltInBridge().getOWLAdapterFactory().getOWLObjectPropertyAssertionAxiom(subject, property, value);
					getBuiltInBridge().injectOWLAxiom(axiom);
				} else {
					OWLDataPropertyAssertionAxiomAdapter axiom;
					OWLLiteralAdapter literal = getArgumentAsALiteral(2, arguments);
					OWLDataPropertyAdapter property = getBuiltInBridge().getOWLAdapterFactory().getOWLDataProperty(propertyURI);
					axiom = getBuiltInBridge().getOWLAdapterFactory().getOWLDataPropertyAssertionAxiom(subject, property, literal);
					getBuiltInBridge().injectOWLAxiom(axiom);
				}

			} else { // In antecedent
				if (propertyValueSupplied) {
					propertyValue = getArgumentAsAPropertyValue(2, arguments);
					if (isObjectProperty) {
						RDFResource p3RDFResource = P3OWLUtil.getObjectPropertyValue(getOWLModel(), individualURI, propertyURI);
						result = propertyValue.equals(p3RDFResource.getURI());
					} else
						result = P3OWLUtil.getDatavaluedPropertyValues(getOWLModel(), individualURI, propertyURI).contains(propertyValue);
				} else { // Property value unbound
					SWRLMultiArgument multiArgument = createMultiArgument();
					if (isObjectProperty) {
						for (RDFResource p3RDFResourceValue : P3OWLUtil.getObjectPropertyValues(getOWLModel(), individualURI, propertyURI)) {
							if (p3RDFResourceValue instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
								OWLIndividual p3OWLIndividual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)p3RDFResourceValue;
								multiArgument.addArgument(createIndividualArgumentFromP3OWLIndividual(p3OWLIndividual));
							} else if (p3RDFResourceValue instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
								OWLNamedClass p3OWLNamedClass = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)p3RDFResourceValue;
								multiArgument.addArgument(createClassArgumentFromP3OWLNamedClass(p3OWLNamedClass));
							} else if (p3RDFResourceValue instanceof edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty) {
								OWLDatatypeProperty p3OWLDataProperty = (edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)p3RDFResourceValue;
								multiArgument.addArgument(createDataPropertyArgumentFromP3OWLDataProperty(p3OWLDataProperty));
							} else if (p3RDFResourceValue instanceof edu.stanford.smi.protegex.owl.model.OWLObjectProperty) {
								OWLObjectProperty p3OWLObjectProperty = (edu.stanford.smi.protegex.owl.model.OWLObjectProperty)p3RDFResourceValue;
								multiArgument.addArgument(createObjectPropertyArgumentFromP3OWLObjectProperty(p3OWLObjectProperty));
							}
						}
					} else { // Data property
						for (Object value : P3OWLUtil.getDatavaluedPropertyValues(getOWLModel(), individualURI, propertyURI)) {
							if (value instanceof edu.stanford.smi.protegex.owl.model.RDFSLiteral) {
								edu.stanford.smi.protegex.owl.model.RDFSLiteral p3RDFSLiteral = (edu.stanford.smi.protegex.owl.model.RDFSLiteral)value;
								multiArgument.addArgument(p3RDFSLiteral2SWRLLiteralBuiltInArgument(getOWLModel(), p3RDFSLiteral));
							} else { // A string is stored as a Java String in Protege-OWL
								multiArgument.addArgument(createLiteralArgument(value.toString()));
							}
						}
					}
					arguments.get(2).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				}
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		} catch (TargetAPIException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Returns true if the individual named by the first argument has a property specified by the second argument. If the second argument in unbound, bind it to
	 * all the properties that currently have a value for this individual.
	 */
	public boolean hasProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		URI individualURI, propertyURI = null;
		boolean hasUnboundPropertyArgument = isUnboundArgument(1, arguments);
		boolean result = false;

		checkNumberOfArgumentsInRange(2, 3, arguments.size());
		individualURI = getArgumentAsAnIndividualURI(0, arguments);
		propertyURI = getArgumentAsAPropertyURI(1, arguments);

		try {
			if (hasUnboundPropertyArgument) {
				SWRLMultiArgument multiArgument = createMultiArgument();
				for (OWLProperty p3OWLProperty : P3OWLUtil.getOWLPropertiesOfIndividual(getOWLModel(), individualURI)) {
					if (p3OWLProperty.isObjectProperty())
						multiArgument.addArgument(createObjectPropertyArgumentFromP3OWLObjectProperty(p3OWLProperty));
					else
						multiArgument.addArgument(createDataPropertyArgumentFromP3OWLDataProperty(p3OWLProperty));
				}
				arguments.get(1).setBuiltInResult(multiArgument);
				result = !multiArgument.hasNoArguments();
			} else
				result = P3OWLUtil.getNumberOfPropertyValues(getOWLModel(), individualURI, propertyURI, true) != 0;
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Returns true if the class named by the first argument has an individual identified by the second argument. If the second argument is unbound, bind it to
	 * all individuals of the class.
	 */
	public boolean hasIndividual(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		boolean isUnboundArgument = isUnboundArgument(1, arguments);
		URI classURI;
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());
		checkThatArgumentIsAClass(0, arguments);

		classURI = getArgumentAsAClassURI(0, arguments);

		try {
			if (isUnboundArgument) {
				SWRLMultiArgument multiArgument = createMultiArgument();
				for (OWLIndividual p3OWLIndividual : P3OWLUtil.getOWLIndividualsOfClass(getOWLModel(), classURI))
					multiArgument.addArgument(createIndividualArgumentFromP3OWLIndividual(p3OWLIndividual));
				arguments.get(1).setBuiltInResult(multiArgument);
				result = !multiArgument.hasNoArguments();
			} else { // Bound argument
				URI individualURI = getArgumentAsAnIndividualURI(1, arguments);
				result = isOWLIndividualOfType(individualURI, classURI);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}
		return result;
	}

	/**
	 * Returns true if the OWL class, property, or individual named by the first argument has a URI identified by the second argument. If the second argument is
	 * unbound, bind it to URI of the resource.
	 */
	public boolean hasURI(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(2, arguments.size());
		checkThatArgumentIsAClassPropertyOrIndividual(0, arguments);

		boolean isUnboundArgument = isUnboundArgument(1, arguments);
		URI resourceURI = getArgumentAsAURI(0, arguments);

		if (isUnboundArgument) {
			arguments.get(1).setBuiltInResult(createLiteralArgument(resourceURI));
			return true;
		} else { // Bound argument
			URI argumentURI = getArgumentAsAURI(1, arguments);
			return argumentURI.equals(resourceURI);
		}
	}

	/**
	 * Returns true if the individual named by the first argument is an instance of the class identified by the second argument. If the second argument is
	 * unbound, bind it to all defining classes of the individual.
	 */
	public boolean hasClass(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(2, arguments.size());

		boolean isUnboundArgument = isUnboundArgument(1, arguments);
		URI individualURI = getArgumentAsAnIndividualURI(0, arguments);
		boolean result = false;

		try {
			if (getIsInConsequent()) {
				URI classURI;
				OWLClassAdapter cls;
				OWLNamedIndividualAdapter individual;
				OWLClassAssertionAxiomAdapter axiom;

				if (isArgumentAString(1, arguments))
					classURI = P3OWLUtil.getURIFromName(getOWLModel(), getArgumentAsAString(1, arguments));
				else
					classURI = getArgumentAsAClassURI(1, arguments);
				cls = getBuiltInBridge().getOWLAdapterFactory().getOWLClass(classURI);

				if (!getBuiltInBridge().isOWLClass(classURI)) {
					OWLClassDeclarationAxiomAdapter declarationAxiom = getBuiltInBridge().getOWLAdapterFactory().getOWLClassDeclarationAxiom(cls);
					getBuiltInBridge().injectOWLAxiom(declarationAxiom);
				}

				individual = getBuiltInBridge().getOWLAdapterFactory().getOWLIndividual(individualURI);
				axiom = getBuiltInBridge().getOWLAdapterFactory().getOWLClassAssertionAxiom(individual, cls);

				getBuiltInBridge().injectOWLAxiom(axiom);
			} else {
				if (isUnboundArgument) {
					SWRLMultiArgument multiArgument = createMultiArgument();
					for (OWLNamedClass p3OWLNamedClass : P3OWLUtil.getOWLClassesOfIndividual(getOWLModel(), individualURI))
						multiArgument.addArgument(createClassArgumentFromP3OWLNamedClass(p3OWLNamedClass));
					arguments.get(1).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				} else { // Bound argument
					URI classURI = getArgumentAsAClassURI(1, arguments);
					result = isOWLIndividualOfType(individualURI, classURI);
				}
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Returns true if the individual named by the second argument has the number of values specified by the first argument of the property specified by the third
	 * argument.
	 */
	public boolean hasNumberOfPropertyValues(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(3, arguments.size());
		boolean hasUnboundNumberOfPropertyValuesArgument = isUnboundArgument(0, arguments);
		URI individualURI = getArgumentAsAnIndividualURI(1, arguments);
		URI propertyURI = getArgumentAsAPropertyURI(2, arguments);
		boolean result = false;

		try {
			int numberOfPropertyValues;
			if (hasUnboundNumberOfPropertyValuesArgument) {
				numberOfPropertyValues = P3OWLUtil.getNumberOfPropertyValues(getOWLModel(), individualURI, propertyURI, true);
				arguments.get(0).setBuiltInResult(createLiteralArgument(numberOfPropertyValues));
				result = true;
			} else {
				numberOfPropertyValues = getArgumentAsAnInteger(0, arguments);
				result = P3OWLUtil.getNumberOfPropertyValues(getOWLModel(), individualURI, propertyURI, true) == numberOfPropertyValues;
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	public boolean isLiteral(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());

		return isArgumentALiteral(0, arguments);
	}

	public boolean notLiteral(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());

		return !isArgumentALiteral(0, arguments);
	}

	public boolean isNumeric(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());

		return isArgumentNumeric(0, arguments);
	}

	public boolean notNumeric(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return !isNumeric(arguments);
	}

	private SWRLClassBuiltInArgument createClassArgumentFromP3OWLNamedClass(OWLNamedClass p3OWLNamedClass) throws BuiltInException
	{
		return createClassArgument(getURI(p3OWLNamedClass), p3OWLNamedClass.getPrefixedName());
	}

	private SWRLObjectPropertyBuiltInArgument createObjectPropertyArgumentFromP3OWLObjectProperty(OWLProperty p3OWLProperty) throws BuiltInException
	{
		if (!p3OWLProperty.isObjectProperty())
			throw new BuiltInException("trying to convert non data property " + p3OWLProperty.getPrefixedName() + " to object property argument");

		return createObjectPropertyArgument(getURI(p3OWLProperty), p3OWLProperty.getPrefixedName());
	}

	private SWRLDataPropertyBuiltInArgument createDataPropertyArgumentFromP3OWLDataProperty(OWLProperty p3OWLProperty) throws BuiltInException
	{
		if (p3OWLProperty.isObjectProperty())
			throw new BuiltInException("trying to convert object property " + p3OWLProperty.getPrefixedName() + " to data property argument");

		return createDataPropertyArgument(getURI(p3OWLProperty), p3OWLProperty.getPrefixedName());
	}

	private SWRLIndividualBuiltInArgument createIndividualArgumentFromP3OWLIndividual(OWLIndividual p3OWLIndividual) throws BuiltInException
	{
		return createIndividualArgument(getURI(p3OWLIndividual), p3OWLIndividual.getPrefixedName());
	}

	private URI getURI(RDFResource p3RDFResource) throws BuiltInException
	{
		if (p3RDFResource == null)
			throw new BuiltInException("internal error: null resources passed to getURI");

		try {
			return new URI(p3RDFResource.getURI());
		} catch (URISyntaxException e) {
			throw new BuiltInException("error converting " + p3RDFResource.getURI() + " to URI: " + e.getMessage());
		}
	}

	// TODO: copied and pasted from @P3OWLOntologyModel
	private SWRLLiteralBuiltInArgument p3RDFSLiteral2SWRLLiteralBuiltInArgument(OWLModel owlModel, RDFSLiteral p3RDFSLiteral)
		throws TargetAPIException, BuiltInException
	{
		SWRLBuiltInArgumentFactory swrlBuiltInArgumentFactory = SWRLBuiltInArgumentFactory.create();
		RDFSDatatype p3RDFSDatatype = p3RDFSLiteral.getDatatype();
		SWRLLiteralBuiltInArgument literalArgument = null;

		try {
			if ((p3RDFSDatatype == owlModel.getXSDint()) || (p3RDFSDatatype == owlModel.getXSDinteger()))
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(p3RDFSLiteral.getInt());
			else if (p3RDFSDatatype == owlModel.getXSDshort())
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(p3RDFSLiteral.getShort());
			else if (p3RDFSDatatype == owlModel.getXSDlong())
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(p3RDFSLiteral.getLong());
			else if (p3RDFSDatatype == owlModel.getXSDboolean())
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(p3RDFSLiteral.getBoolean());
			else if (p3RDFSDatatype == owlModel.getXSDfloat())
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(p3RDFSLiteral.getFloat());
			else if (p3RDFSDatatype == owlModel.getXSDdouble())
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(p3RDFSLiteral.getDouble());
			else if ((p3RDFSDatatype == owlModel.getXSDstring()))
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(p3RDFSLiteral.getString());
			else if ((p3RDFSDatatype == owlModel.getXSDtime()))
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(new XSDTime(p3RDFSLiteral.getString()));
			else if ((p3RDFSDatatype == owlModel.getXSDanyURI()))
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(createURI(p3RDFSLiteral.getString()));
			else if ((p3RDFSDatatype == owlModel.getXSDbyte()))
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(Byte.valueOf(p3RDFSLiteral.getString()));
			else if ((p3RDFSDatatype == owlModel.getXSDduration()))
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(new XSDDuration(p3RDFSLiteral.getString()));
			else if ((p3RDFSDatatype == owlModel.getXSDdateTime()))
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(new XSDDateTime(p3RDFSLiteral.getString()));
			else if ((p3RDFSDatatype == owlModel.getXSDdate()))
				literalArgument = swrlBuiltInArgumentFactory.createLiteralArgument(new XSDDate(p3RDFSLiteral.getString()));
			else
				throw new TargetAPIException("cannot create a SWRL literal argument from RDFS literal " + p3RDFSLiteral.getString() + " of type " + p3RDFSDatatype);
		} catch (OWLLiteralException e) {
			throw new TargetAPIException("error creating a SWRL literal argument from RDFS literal value " + p3RDFSLiteral.getString() + " with type "
					+ p3RDFSDatatype.getURI() + ": " + e.getMessage());
		}

		return literalArgument;
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
