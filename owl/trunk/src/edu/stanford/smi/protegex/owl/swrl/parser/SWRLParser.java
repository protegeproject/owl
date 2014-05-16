package edu.stanford.smi.protegex.owl.swrl.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protegex.owl.model.OWLDataRange;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFObject;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLAtomList;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLDifferentIndividualsAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

public class SWRLParser
{
	private final SWRLParserSupport swrlParserSupport;

	public SWRLParser(OWLModel owlModel)
	{
		this.swrlParserSupport = new SWRLParserSupport(owlModel);
	}

	/**
	 * If the rule is correct and incomplete return 'true'; if the rule has errors or is correct and complete, return
	 * 'false'.
	 */
	public boolean isSWRLRuleCorrectAndIncomplete(String ruleText)
	{
		boolean result = false;

		try {
			parseSWRLRule(ruleText, true);
		} catch (SWRLParseException e) {
			if (e instanceof SWRLIncompleteRuleException)
				result = true;
		}

		return result;
	}

	/**
	 * This parser will throw a {@link SWRLParseException} if it finds an error in the supplied rule. If the rule is
	 * correct but incomplete, a {@link SWRLIncompleteRuleException} (which is a subclass of {@link SWRLParseException})
	 * will be thrown.
	 * <p>
	 * If {@link #parseOnly} is true, only checking is performed - no SWRL rules are created; if it is false, rules are
	 * created.
	 */
	public SWRLImp parseSWRLRule(String ruleText, boolean parseOnly) throws SWRLParseException
	{
		return parseSWRLRule(ruleText, null, parseOnly);
	}

	public SWRLImp parseSWRLRule(String ruleText, SWRLImp imp, boolean parseOnly) throws SWRLParseException
	{
		SWRLTokenizer tokenizer = new SWRLTokenizer(ruleText.trim(), parseOnly);
		SWRLAtomList head = !tokenizer.isParseOnly() ? swrlParserSupport.getSWRLHeadAtomList() : null;
		SWRLAtomList body = !tokenizer.isParseOnly() ? swrlParserSupport.getSWRLBodyAtomList() : null;
		boolean atLeastOneAtom = false, justProcessedAtom = true, isInHead = false;
		String message;

		if (!tokenizer.isParseOnly() && !tokenizer.hasMoreTokens())
			throw new SWRLParseException("Empty rule");

		do {
			if (justProcessedAtom)
				message = isInHead ? "Expecting " + SWRLTokenizer.AND_CHAR : "Expecting " + SWRLTokenizer.IMP_CHAR + " or "
						+ SWRLTokenizer.AND_CHAR + " or " + SWRLTokenizer.RING_CHAR;
			else
				message = isInHead ? "Expecting atom" : "Expecting atom or " + SWRLTokenizer.IMP_CHAR;

			String currentToken = tokenizer.getNextNonSpaceToken(message);

			if (currentToken.equals("" + SWRLTokenizer.IMP_CHAR) || currentToken.equals("->")) { // An empty body is ok
				if (isInHead)
					throw new SWRLParseException("Second occurence of " + SWRLTokenizer.IMP_CHAR);
				isInHead = true;
				justProcessedAtom = false;
			} else if (currentToken.equals("-")) {
				continue; // Ignore "->" while we build up IMP_CHAR.
			} else if (currentToken.equals("" + SWRLTokenizer.AND_CHAR) || currentToken.equals("^")) {
				if (!justProcessedAtom)
					throw new SWRLParseException(SWRLTokenizer.AND_CHAR + " may occur only after an atom");
				justProcessedAtom = false;
			} else if (currentToken.equals("" + SWRLTokenizer.RING_CHAR) || currentToken.equals(".")) {
				if (isInHead || !justProcessedAtom)
					throw new SWRLParseException(SWRLTokenizer.RING_CHAR + " may only occur in query body");
				justProcessedAtom = false;
			} else {
				String predicate = currentToken;
				SWRLAtom atom = parseSWRLAtom(predicate, tokenizer, isInHead);
				atLeastOneAtom = true;
				if (!tokenizer.isParseOnly()) {
					if (isInHead)
						head.append(atom);
					else
						body.append(atom);
				}
				justProcessedAtom = true;
			}
		} while (tokenizer.hasMoreTokens());

		if (!tokenizer.isParseOnly()) {
			if (!atLeastOneAtom)
				throw new SWRLParseException("Incomplete SWRL rule - no antecedent or consequent");
			if (imp == null)
				imp = swrlParserSupport.getSWRLRule(head, body);
			else {
				imp.setHead(head);
				imp.setBody(body);
			}
			return imp;
		} else
			return null;
	}

	private SWRLAtom parseSWRLAtom(String predicate, SWRLTokenizer tokenizer, boolean isInHead) throws SWRLParseException
	{
		if (predicate.equalsIgnoreCase("sameAs")) {
			tokenizer.checkAndSkipToken("(", "Expecting parentheses-enclosed arguments for same individual atom");
			return parseSWRLSameAsAtomArguments(tokenizer, isInHead);
		} else if (predicate.equalsIgnoreCase("differentFrom")) {
			tokenizer.checkAndSkipToken("(", "Expecting parentheses-enclosed arguments for different individuals atom");
			return parseSWRLDifferentFromAtomArguments(tokenizer, isInHead);
		} else if (swrlParserSupport.isOWLClass(predicate)) {
			tokenizer.checkAndSkipToken("(", "Expecting parentheses-enclosed arguments for class atom");
			return parseSWRLClassAtomArguments(predicate, tokenizer, isInHead);
		} else if (swrlParserSupport.isOWLObjectProperty(predicate)) {
			tokenizer.checkAndSkipToken("(", "Expecting parentheses-enclosed arguments for object property atom");
			return parseSWRLIndividualPropertyAtomArguments(predicate, tokenizer, isInHead);
		} else if (swrlParserSupport.isOWLDataProperty(predicate)) {
			tokenizer.checkAndSkipToken("(", "Expecting parentheses-enclosed arguments for data property atom");
			return parseSWRLDataPropertyAtomArguments(predicate, tokenizer, isInHead);
		} else if (swrlParserSupport.isSWRLBuiltIn(predicate)) {
			tokenizer.checkAndSkipToken("(", "Expecting parentheses-enclosed arguments for built-in atom");
			return parseSWRLBuiltinAtomArguments(predicate, tokenizer, isInHead);
		} else if (predicate.startsWith("[")) {
			List<RDFObject> enumeratedList = parseDObjectList(tokenizer, isInHead);
			tokenizer.checkAndSkipToken("(", "Expecting parentheses-enclosed arguments for data range atom");
			return parseSWRLDataRangeAtomArguments(enumeratedList, tokenizer, isInHead);
		} else
			throw new SWRLParseException("Invalid SWRL atom predicate " + predicate);
	}

	private SWRLClassAtom parseSWRLClassAtomArguments(String predicate, SWRLTokenizer tokenizer, boolean isInHead)
			throws SWRLParseException
	{
		RDFResource iObject = parseIObject(tokenizer, isInHead);

		tokenizer.checkAndSkipToken(")", "Expecting closing parenthesis for argument for class atom " + predicate);

		return !tokenizer.isParseOnly() ? swrlParserSupport.getSWRLClassAtom(predicate, iObject) : null;
	}

	private SWRLIndividualPropertyAtom parseSWRLIndividualPropertyAtomArguments(String predicate,
			SWRLTokenizer tokenizer, boolean isInHead) throws SWRLParseException
	{
		RDFResource iObject1 = parseIObject(tokenizer, isInHead);
		tokenizer.checkAndSkipToken(",", "Expecting comma-separated second argument for object property atom " + predicate);
		RDFResource iObject2 = parseIObject(tokenizer, isInHead);
		tokenizer.checkAndSkipToken(")", "Expecting closing parenthesis after second argument of object property atom "
				+ predicate);

		return !tokenizer.isParseOnly() ? swrlParserSupport.getSWRLObjectPropertyAtom(predicate, iObject1, iObject2) : null;
	}

	private SWRLDatavaluedPropertyAtom parseSWRLDataPropertyAtomArguments(String predicate, SWRLTokenizer tokenizer,
			boolean isInHead) throws SWRLParseException
	{
		RDFResource iObject = parseIObject(tokenizer, isInHead);
		tokenizer.checkAndSkipToken(",", "Expecting comma-separated second parameter for data property atom " + predicate);
		RDFObject dObject = parseDObject(tokenizer, isInHead);
		tokenizer.checkAndSkipToken(")", "Expecting closing parenthesis after second argument of data property atom "
				+ predicate);

		return !tokenizer.isParseOnly() ? swrlParserSupport.getSWRLDataPropertyAtom(predicate, iObject, dObject) : null;
	}

	private SWRLBuiltinAtom parseSWRLBuiltinAtomArguments(String predicate, SWRLTokenizer tokenizer, boolean isInHead)
			throws SWRLParseException
	{
		List<RDFObject> objectList = parseObjectList(tokenizer, isInHead); // Swallows ')'

		return !tokenizer.isParseOnly() ? swrlParserSupport.getSWRLBuiltInAtom(predicate, objectList) : null;
	}

	private SWRLSameIndividualAtom parseSWRLSameAsAtomArguments(SWRLTokenizer tokenizer, boolean isInHead)
			throws SWRLParseException
	{
		RDFResource iObject1 = parseIObject(tokenizer, isInHead);
		tokenizer.checkAndSkipToken(",", "Expecting comma-separated second argument for same individual atom");
		RDFResource iObject2 = parseIObject(tokenizer, isInHead);

		tokenizer.checkAndSkipToken(")", "Expecting closing parenthesis after second argument to same individual atom");

		return tokenizer.isParseOnly() ? null : swrlParserSupport.getSWRLSameIndividualAtom(iObject1, iObject2);
	}

	private SWRLDifferentIndividualsAtom parseSWRLDifferentFromAtomArguments(SWRLTokenizer tokenizer, boolean isInHead)
			throws SWRLParseException
	{
		RDFResource iObject1 = parseIObject(tokenizer, isInHead);
		tokenizer.checkAndSkipToken(",", "Expecting comma-separated second argument for different individuals atom");
		RDFResource iObject2 = parseIObject(tokenizer, isInHead);

		tokenizer.checkAndSkipToken(")",
				"Expecting closing parenthesis after second argument to different individuals atom");

		return tokenizer.isParseOnly() ? null : swrlParserSupport.getSWRLDifferentIndividualsAtom(iObject1, iObject2);
	}

	private SWRLDataRangeAtom parseSWRLDataRangeAtomArguments(List<RDFObject> enumeratedList, SWRLTokenizer tokenizer,
			boolean isInHead) throws SWRLParseException
	{
		RDFObject dObject = parseDObject(tokenizer, isInHead);
		tokenizer.checkAndSkipToken(")", "Expecting closing parenthesis after argument in data range atom");

		if (!tokenizer.isParseOnly()) {
			OWLDataRange dataRange = swrlParserSupport.getOWLDataRange();
			RDFProperty oneOfProperty = swrlParserSupport.getOWLOneOfProperty();

			Iterator<RDFObject> iterator = enumeratedList.iterator();
			while (iterator.hasNext()) {
				Object literalValue = iterator.next();
				dataRange.addPropertyValue(oneOfProperty, literalValue);
			}
			return swrlParserSupport.getSWRLDataRangeAtom(dataRange, dObject);
		} else
			return null;
	}

	private SWRLVariable parseSWRLVariable(SWRLTokenizer tokenizer, boolean isInHead) throws SWRLParseException
	{
		String variableName = tokenizer.getNextNonSpaceToken("Expecting variable name");
		swrlParserSupport.checkThatSWRLVariableNameIsValid(variableName);

		if (tokenizer.hasMoreTokens()) {
			if (!isInHead)
				tokenizer.addVariable(variableName);
			else if (!tokenizer.hasVariable(variableName))
				throw new SWRLParseException("Variable ?" + variableName + " used in consequent is not present in antecedent");
		}

		return !tokenizer.isParseOnly() ? swrlParserSupport.getSWRLVariable(variableName) : null;
	}

	private List<RDFObject> parseDObjectList(SWRLTokenizer tokenizer, boolean isInHead) throws SWRLParseException
	{ // Parse a list of variables and literals
		List<RDFObject> dObjects = !tokenizer.isParseOnly() ? new ArrayList<RDFObject>() : null;

		RDFObject dObject = parseDObject(tokenizer, isInHead);
		if (!tokenizer.isParseOnly())
			dObjects.add(dObject);

		String token = tokenizer
				.getNextNonSpaceToken("Expecting additional comma-separated variables or literals or closing parenthesis");
		while (token.equals(",")) {
			dObject = parseDObject(tokenizer, isInHead);
			if (!tokenizer.isParseOnly())
				dObjects.add(dObject);
			token = tokenizer.getNextNonSpaceToken("Expecting ',' or ')'");
			if (!(token.equals(",") || token.equals(")")))
				throw new SWRLParseException("Expecting ',' or ')', got " + token);
		}
		return dObjects;
	}

	private List<RDFObject> parseObjectList(SWRLTokenizer tokenizer, boolean isInHead) throws SWRLParseException
	{ // Parse a list of SWRL variables, OWL literals, or OWL named entities
		List<RDFObject> objects = !tokenizer.isParseOnly() ? new ArrayList<RDFObject>() : null;

		RDFObject object = parseObject(tokenizer, isInHead);

		if (!tokenizer.isParseOnly())
			objects.add(object);

		String token = tokenizer
				.getNextNonSpaceToken("Expecting additional comma-separated variables, literals, OWL entity names or closing parenthesis");
		while (token.equals(",")) {
			object = parseObject(tokenizer, isInHead);
			if (!tokenizer.isParseOnly())
				objects.add(object);
			token = tokenizer.getNextNonSpaceToken("Expecting ',' or ')'");
			if (!(token.equals(",") || token.equals(")")))
				throw new SWRLParseException("Expecting ',' or ')', got'" + token);
		}
		return objects;
	}

	private RDFObject parseObject(SWRLTokenizer tokenizer, boolean isInHead) throws SWRLParseException
	{ // Parse a SWRL variable, OWL literal, or an OWL named entity
		String token = tokenizer.getNextNonSpaceToken("Expecting variable, literal, or OWL entity name");

		if (token.equals("?"))
			return parseSWRLVariable(tokenizer, isInHead);
		else { // The entity is an OWL named entity or a literal
			if (swrlParserSupport.isOWLNamedIndividual(token)) {
				return !tokenizer.isParseOnly() ? swrlParserSupport.getOWLNamedIndividual(token) : null;
			} else if (swrlParserSupport.isOWLClassName(token)) {
				return !tokenizer.isParseOnly() ? swrlParserSupport.getOWLClass(token) : null;
			} else if (swrlParserSupport.isOWLObjectProperty(token)) {
				return !tokenizer.isParseOnly() ? swrlParserSupport.getOWLObjectProperty(token) : null;
			} else if (swrlParserSupport.isOWLDataProperty(token)) {
				return !tokenizer.isParseOnly() ? swrlParserSupport.getOWLDataProperty(token) : null;
			} else
				return parseOWLLiteral(token, tokenizer);
		}
	}

	private RDFResource parseIObject(SWRLTokenizer tokenizer, boolean isInHead) throws SWRLParseException
	{ // Parse a SWRL variable or an OWL named individual. For SWRL Full, also allow OWL class and property names.
		String token = tokenizer.getNextNonSpaceToken("Expecting variable or OWL entity name");

		if (token.equals("?"))
			return parseSWRLVariable(tokenizer, isInHead);
		else {
			String entityName = token;

			if (swrlParserSupport.isOWLNamedIndividual(entityName)) {
				return tokenizer.isParseOnly() ? null : swrlParserSupport.getOWLNamedIndividual(entityName);
			} else if (swrlParserSupport.isOWLClassName(entityName)) { // SWRL Full
				return tokenizer.isParseOnly() ? null : swrlParserSupport.getOWLClass(entityName);
			} else if (swrlParserSupport.isOWLObjectProperty(entityName)) { // SWRL Full
				return tokenizer.isParseOnly() ? null : swrlParserSupport.getOWLObjectProperty(entityName);
			} else if (swrlParserSupport.isOWLDataProperty(entityName)) { // SWRL Full
				return tokenizer.isParseOnly() ? null : swrlParserSupport.getOWLDataProperty(entityName);
			} else {
				if (tokenizer.hasMoreTokens())
					throw new SWRLParseException("Invalid OWL entity name " + entityName);
				else
					throw new SWRLIncompleteRuleException("Incomplete rule - OWL entity name " + entityName + " not valid");
			}
		}
	}

	private RDFObject parseDObject(SWRLTokenizer tokenizer, boolean isInHead) throws SWRLParseException
	{ // Parse a SWRL variable or an OWL literal
		String token = tokenizer.getNextNonSpaceToken("Expecting variable or literal");

		return token.equals("?") ? parseSWRLVariable(tokenizer, isInHead) : parseOWLLiteral(token, tokenizer);
	}

	private RDFObject parseOWLLiteral(String token, SWRLTokenizer tokenizer) throws SWRLParseException
	{
		if (token.equals("\"")) { // The parsed entity is a string
			String stringValue = tokenizer.getNextStringToken("Expected a string");

			return !tokenizer.isParseOnly() ? swrlParserSupport.getOWLXSDStringLiteral(stringValue) : null;
		} else if (token.startsWith("t") || token.startsWith("T") || token.startsWith("f") || token.startsWith("F")) {
			// According to the XSD Specification, xsd:boolean's have the lexical space: {true, false, 1, 0}. We don't allow
			// {1, 0} since these are parsed as xsd:ints.
			if (tokenizer.hasMoreTokens()) {
				if (token.equalsIgnoreCase("true") || token.equalsIgnoreCase("false")) {
					return !tokenizer.isParseOnly() ? swrlParserSupport.getOWLXSDBooleanLiteral(token) : null;
				} else
					throw new SWRLParseException("Invalid OWL literal " + token);
			} else
				return null;
		} else { // Is it an integer, float, long or double then?
			try {
				if (token.contains(".")) {
					Double.parseDouble(token); // Check it
					return !tokenizer.isParseOnly() ? swrlParserSupport.getOWLXSDDoubleLiteral(token) : null;
				} else {
					Integer.parseInt(token); // Check it
					return !tokenizer.isParseOnly() ? swrlParserSupport.getOWLXSDIntLiteral(token) : null;
				}
			} catch (NumberFormatException e) {
				String errorMessage = "Invalid OWL literal " + token;
				if (tokenizer.isParseOnly())
					throw new SWRLIncompleteRuleException(errorMessage);
				else
					throw new SWRLParseException(errorMessage);
			}
		}
	}
}
