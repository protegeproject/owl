
package org.protege.swrltab.bridge.builtins.rdfb;

import java.net.URI;
import java.util.List;

import org.protege.swrlapi.arguments.SWRLBuiltInArgument;
import org.protege.swrlapi.arguments.SWRLMultiArgument;
import org.protege.swrlapi.builtins.AbstractSWRLBuiltInLibrary;
import org.protege.swrlapi.exceptions.BuiltInException;
import org.protege.swrlapi.exceptions.BuiltInNotImplementedException;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtil;

/**
 * Implementations library for RDFB built-in methods. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?RDFBuiltIns">here</a> for documentation on this
 * library.
 * <p>
 * See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
	private static String SWRLRDFLibraryName = "SWRLRDFBuiltIns";

	public SWRLBuiltInLibraryImpl()
	{
		super(SWRLRDFLibraryName);
	}

	public void reset()
	{
	}

	/**
	 * Returns true if the RDF resource named by the first argument has any label identified by the second argument. If the second argument is unbound, bind it to
	 * labels of the resource.
	 */
	public boolean hasLabel(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		boolean isUnboundArgument = isUnboundArgument(1, arguments);
		boolean hasLanguage = (arguments.size() == 3);
		String language;
		URI resourceURI;
		boolean result = false;

		checkNumberOfArgumentsAtLeast(2, arguments.size());
		checkThatArgumentIsAClassPropertyOrIndividual(0, arguments);

		resourceURI = getArgumentAsAURI(0, arguments);
		language = hasLanguage ? getArgumentAsAString(2, arguments) : "";

		if (isUnboundArgument) {
			SWRLMultiArgument multiArgument = createMultiArgument();
			for (String label : P3OWLUtil.getRDFSLabels(getOWLModel(), resourceURI, language))
				multiArgument.addArgument(createLiteralArgument(label));
			arguments.get(1).setBuiltInResult(multiArgument);
			result = !multiArgument.hasNoArguments();
		} else { // Bound argument
			String label = getArgumentAsAString(1, arguments);
			result = P3OWLUtil.getRDFSLabels(getOWLModel(), resourceURI, language).contains(label);
		}

		return result;
	}

	/**
	 * Returns true if the RDF resource named by the first argument has any label language identified by the second argument. If the second argument is unbound,
	 * bind it to label languages of the resource.
	 */
	public boolean hasLabelLanguage(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		boolean isUnboundArgument = isUnboundArgument(1, arguments);
		URI resourceURI;
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());
		checkThatArgumentIsAClassPropertyOrIndividual(0, arguments);

		resourceURI = getArgumentAsAURI(0, arguments);

		if (isUnboundArgument) {
			SWRLMultiArgument multiArgument = createMultiArgument();
			for (String language : P3OWLUtil.getRDFSLabelLanguages(getOWLModel(), resourceURI))
				multiArgument.addArgument(createLiteralArgument(language));
			arguments.get(1).setBuiltInResult(multiArgument);
			result = !multiArgument.hasNoArguments();
		} else { // Bound argument
			String language = getArgumentAsAString(1, arguments);
			result = P3OWLUtil.getRDFSLabelLanguages(getOWLModel(), resourceURI).contains(language);
		}

		return result;
	}

	/**
	 * isClass(c)
	 */
	@SuppressWarnings("unused")
	public boolean isClass(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * isList(l)
	 */
	@SuppressWarnings("unused")
	public boolean isList(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * isProperty(p)
	 */
	@SuppressWarnings("unused")
	public boolean isProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * isResource(r)
	 */
	@SuppressWarnings("unused")
	public boolean isResource(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	private OWLModel getOWLModel()
	{
		return null;
	}
}
