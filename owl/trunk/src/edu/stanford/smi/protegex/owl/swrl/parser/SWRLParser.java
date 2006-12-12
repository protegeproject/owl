
// TODO: need to rewrite this using JavaCC to make it more extensible.

package edu.stanford.smi.protegex.owl.swrl.parser;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.XMLSchemaDatatypes;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.*;

/**
 * @author Martin O'Connor  <moconnor@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SWRLParser {

  public final static char AND_CHAR = '\u2227';   // ^
  public final static char IMP_CHAR = '\u2192';   // >
  private OWLModel owlModel;
  private SWRLFactory swrlFactory;
  private boolean parseOnly;
  private StringTokenizer tokenizer;
    private String delimiters = " ?\n\t()[],#\"" + AND_CHAR + IMP_CHAR; // Note space.
  private Collection xmlSchemaSymbols = XMLSchemaDatatypes.getSlotSymbols();
  private HashSet variables;
  private boolean inHead = false;
  
  public SWRLParser(OWLModel owlModel) 
  {
    this.owlModel = owlModel;
    swrlFactory = new SWRLFactory(owlModel);
    parseOnly = true;
    variables = new HashSet();
  } // SWRLParser

  public void setParseOnly(boolean parseOnly) {
    this.parseOnly = parseOnly;
  } // setParseOnly

  // This parser will throw a SWRLParseException if it finds errors in the supplied rule. If the rule is correct but incomplete, a
  // SWRLIncompleteRuleException (which is a subclass of SWRLParseException) will be thrown.
  //
  // If parseOnly is true, only checking is performed - no OWL individuals are created; if it is false, individuals are created.

  public SWRLImp parse(String rule) throws SWRLParseException 
  {
    return parse(rule, null);
  } // parse

  public SWRLImp parse(String rule, SWRLImp imp) throws SWRLParseException {
    String token, message;
    SWRLAtomList head = null, body = null;
    SWRLAtom atom = null;
    boolean atLeastOneAtom = false, justProcessedAtom = false;
    
    inHead = false;
    
    // rule = getParsableRuleString(rule);
    variables.clear();
    tokenizer = new StringTokenizer(rule.trim(), delimiters, true);
    
    if (!parseOnly) {
      head = swrlFactory.createAtomList();
      body = swrlFactory.createAtomList();
    } // if
    
    if (!parseOnly && !tokenizer.hasMoreTokens()) throw new SWRLParseException("Empty rule.");
    
    do {
      if (justProcessedAtom) {
        if (inHead) message = "Expecting '" + AND_CHAR + "'";
        else message = "Expecting '" + IMP_CHAR + "' or '" + AND_CHAR + "'";
      } else {
        if (inHead) message = "Expecting atom.";
        else message = "Expecting atom or '" + IMP_CHAR + "'.";
      } // if
      
      token = getNextNonSpaceToken(message);
      
      if (token.equals("" + IMP_CHAR)) { // A rule can have an empty body.
        if (inHead) throw new SWRLParseException("Second occurence of '" + IMP_CHAR + "'.");
        inHead = true; 
        justProcessedAtom = false;
      } else if (token.equals("-") || token.equals("->")) {
        continue; // Ignore "->" while we build up IMP_CHAR.
      } else if (token.equals("" + AND_CHAR)) {
        if (!justProcessedAtom) throw new SWRLParseException("'" + AND_CHAR + "' may occur only after an atom.");
        justProcessedAtom = false;
      } else {
        atom = parseAtom(token);
        atLeastOneAtom = true;
        if (!parseOnly) {
          if (inHead) head.append(atom);
          else body.append(atom);
        } // if
        justProcessedAtom = true;
      } // if
    } while (tokenizer.hasMoreTokens());
    
    if (!parseOnly) {
      if (!atLeastOneAtom) throw new SWRLParseException("Incomplete rule - no antecedent or consequent.");
      if (imp == null) imp = swrlFactory.createImp(head, body);
      else {
        imp.setHead(head);
        imp.setBody(body);
      } // if
    } else imp = null;
    
    return imp;
  } // parse

  //public static String getParsableRuleString(String unicodeRule) { return unicodeRule.replace(AND_CHAR, '^').replace(IMP_CHAR, '>'); }

  private SWRLAtom parseAtom(String identifier) throws SWRLParseException 
  {
    SWRLAtom atom = null;
    List enumeratedList = null;
    boolean isEnumeratedList = false;
    
    checkThatIdentifierIsValid(identifier);
    
    if (identifier.startsWith("[")) { // A data range with an enumerated literal list
      enumeratedList = parseDObjectList();
      isEnumeratedList = true;
    } // if
    
    if (isEnumeratedList) checkAndSkipToken("(", "Expecting parameters enclosed in parentheses for data range atom.");
    else checkAndSkipToken("(", "Expecting parameters enclosed in parentheses for atom '" + identifier + "'.");
    
    if (isEnumeratedList) atom = parseEnumeratedListParameters(enumeratedList);
    else if (isSameAs(identifier)) atom = parseSameAsAtomParameters();
    else if (isDifferentFrom(identifier)) atom = parseDifferentFromAtomParameters();
    else if (isOWLClassName(identifier)) atom = parseClassAtomParameters(identifier);
    else if (isOWLObjectPropertyName(identifier)) atom = parseIndividualPropertyAtomParameters(identifier);
    else if (isOWLDatatypePropertyName(identifier)) atom = parseDatavaluedPropertyAtomParameters(identifier);
    else if (isBuiltinName(identifier)) atom = parseBuiltinParameters(identifier);
    else if (isXSDDatatype(identifier)) atom = parseXSDDatatypeParameters(identifier);
    else throw new SWRLParseException("Invalid atom name '" + identifier + "'.");
    
    return atom;
  } // parseAtom

  private void checkAndSkipToken(String skipToken, String unexpectedTokenMessage) throws SWRLParseException 
  {
    String token = getNextNonSpaceToken(unexpectedTokenMessage);
    
    if (!token.equalsIgnoreCase(skipToken)) 
      throw new SWRLParseException("Expecting '" + skipToken + "', got '" + token + "'. " + unexpectedTokenMessage);
  } // checkAndSkipToken

  private String getNextStringToken(String noTokenMessage) throws SWRLParseException 
  {
    String token = "";
    String errorMessage = "Incomplete rule. " + noTokenMessage;
    
    if (!tokenizer.hasMoreTokens()) {
      if (parseOnly) throw new SWRLIncompleteRuleException(errorMessage);
      else throw new SWRLParseException(errorMessage);
    } // if
    
    while (tokenizer.hasMoreTokens()) {
      token = tokenizer.nextToken("\"");
      return token;
    } // while
    
    if (parseOnly) throw new SWRLIncompleteRuleException(errorMessage);
    else throw new SWRLParseException(errorMessage); // Should not get here  
  } // getNextStringToken

  private String getNextNonSpaceToken(String noTokenMessage) throws SWRLParseException 
  {
    String token = "";
    String errorMessage = "Incomplete rule. " + noTokenMessage;
    
    if (!tokenizer.hasMoreTokens()) {
      if (parseOnly) throw new SWRLIncompleteRuleException(errorMessage);
      else throw new SWRLParseException(errorMessage);
    } // if

    while (tokenizer.hasMoreTokens()) {
      token = tokenizer.nextToken(delimiters);
      if (!(token.equals(" ") || token.equals("\n") || token.equals("\t"))) return token;
    } // while
    
    if (parseOnly) throw new SWRLIncompleteRuleException(errorMessage);
    else throw new SWRLParseException(errorMessage); // Should not get here
  } // getNextNonSpaceToken

  private boolean hasMoreNonSpaceTokens() 
  {
    if (!tokenizer.hasMoreTokens()) return false;
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken(delimiters);
      if (!(token.equals(" ") || token.equals("\n") || token.equals("\t"))) return true;
    } // while
    return false;  
  } // hasMoreNonSpaceTokens

  private SWRLAtom parseSameAsAtomParameters() throws SWRLParseException 
  {
    RDFResource iObject1, iObject2;
    SWRLAtom atom = null;
    
    iObject1 = parseIObject();
    checkAndSkipToken(",", "Expecting comma-separated second parameter for SameAsAtom.");
    iObject2 = parseIObject();
    
    if (!parseOnly) atom = swrlFactory.createSameIndividualAtom(iObject1, iObject2);
    
    checkAndSkipToken(")", "Expecting closing parenthesis after second parameters in SameAsAtom");
    
    return atom;
  } // parseSameAsAtomParameters

  private SWRLAtom parseDifferentFromAtomParameters() throws SWRLParseException 
  {
    RDFResource iObject1, iObject2;
    SWRLAtom atom = null;
    
    iObject1 = parseIObject();
    checkAndSkipToken(",", "Expecting comma-separated second parameters for DifferentFromAtom");
    iObject2 = parseIObject();
    
    if (!parseOnly) atom = swrlFactory.createDifferentIndividualsAtom(iObject1, iObject2);

    checkAndSkipToken(")", "Only two parameters allowed for DifferentFromAtom");
    
    return atom;
  } // parseDifferentFromAtomParameters

  private SWRLAtom parseClassAtomParameters(String identifier) throws SWRLParseException 
  {
    RDFResource iObject;
    SWRLAtom atom = null;
    
    iObject = parseIObject();
    
    if (!parseOnly) {
      RDFSNamedClass aClass = owlModel.getOWLNamedClass(identifier);
      atom = swrlFactory.createClassAtom(aClass, iObject);
    } // if
    
    checkAndSkipToken(")", "Expecting closing parenthesis for parameter for ClassAtom '" + identifier + "'.");
    
    return atom;
  } // parseClassAtomParameters

  private SWRLAtom parseIndividualPropertyAtomParameters(String identifier) throws SWRLParseException 
  {
    RDFResource iObject1, iObject2;
    SWRLAtom atom = null;
    
    iObject1 = parseIObject();
    checkAndSkipToken(",", "Expecting comma-separated second parameter for IndividualPropertyAtom '" + identifier + "'");
    iObject2 = parseIObject();
    
    if (!parseOnly) {
      OWLObjectProperty objectProperty = owlModel.getOWLObjectProperty(identifier);
      if (objectProperty == null) throw new SWRLParseException("no datatype slot found for IndividualPropertyAtom: " + identifier);
      atom = swrlFactory.createIndividualPropertyAtom(objectProperty, iObject1, iObject2);
    } // if
    
    checkAndSkipToken(")", "Expecting closing parenthesis after second parameter of IndividualPropertyAtom '" + identifier + "'.");
    
    return atom;
  } // parseClassAtomParameters

  private SWRLAtom parseDatavaluedPropertyAtomParameters(String identifier) throws SWRLParseException 
  {
    RDFResource iObject;
    RDFObject dObject;
    SWRLAtom atom = null;
    OWLDatatypeProperty datatypeSlot;
    String token, errorMessage = "Expecting literal qualification symbol '#' or closing parenthesis after second parameter of DatavaluedPropertyAtom' ";
    
    iObject = parseIObject();
    checkAndSkipToken(",", "Expecting comma-separated second parameter for DatavaluedPropertyAtom '" + identifier + "'.");
    dObject = parseDObject();
    
    token = getNextNonSpaceToken(errorMessage + identifier + "'.");
    
    if (token.equals("#")) { // Literal qualification.
      token = getNextNonSpaceToken("Expecting XML Schema datatype.");
      if (tokenizer.hasMoreTokens() && !isXSDDatatype(token)) 
        throw new SWRLParseException("Invalid XML Schema datatype name: '" + token + "'.");
      if (!parseOnly) dObject = owlModel.createRDFSLiteral(dObject.getBrowserText(), token);
      checkAndSkipToken(")", "Expecting closing parenthesis after second parameter of DatavaluedPropertyAtom");
    } else if (!token.equals(")")) throw new SWRLParseException(errorMessage + identifier + "'.");
    
    if (!parseOnly) {
      datatypeSlot = owlModel.getOWLDatatypeProperty(identifier);
      atom = swrlFactory.createDatavaluedPropertyAtom(datatypeSlot, iObject, dObject);
    } // if
    
    return atom;
  } // parseDatavaluedPropertyAtomParameters

  private SWRLAtom parseBuiltinParameters(String identifier) throws SWRLParseException 
  {
    SWRLBuiltin builtin;
    SWRLAtom atom = null;
    List objects = new ArrayList();

    objects = parseObjectList(); // Swallows ')'

    if (!parseOnly) {
      builtin = swrlFactory.getBuiltin(identifier);
      atom = swrlFactory.createBuiltinAtom(builtin, objects.iterator());
    } // if
    
    return atom;
  } // parseBuiltinParameters

  private SWRLAtom parseXSDDatatypeParameters(String identifier)
    throws SWRLParseException {
    RDFObject dObject;
    SWRLAtom atom = null;
    RDFSDatatype datatype;
    
    dObject = parseDObject();
    
    if (!parseOnly) {
      datatype = owlModel.getRDFSDatatypeByName(identifier);
      atom = swrlFactory.createDataRangeAtom(datatype, dObject);
    } // if
    
    checkAndSkipToken(")", "Expecting closing parenthesis after DataRangeAtom '" + identifier + "'.");
    
    return atom;
  } // parseXSDDatatypeParameters

  private SWRLAtom parseEnumeratedListParameters(List enumeratedList)
    throws SWRLParseException {
    RDFObject dObject;
    SWRLAtom atom = null;
    Object literalValue;
    Iterator iterator;
    
    dObject = parseDObject();
    
    if (!parseOnly) {
      OWLDataRange dataRange = owlModel.createOWLDataRange();
      RDFProperty oneOfProperty = owlModel.getOWLOneOfProperty();
      
      iterator = enumeratedList.iterator();
      while (iterator.hasNext()) {
        literalValue = (Object) iterator.next();
        dataRange.addPropertyValue(oneOfProperty, literalValue);
      } // while
      atom = swrlFactory.createDataRangeAtom(dataRange, dObject);
    } // if
    checkAndSkipToken(")", "Expecting closing parenthesis after parameter in DataRangeAtom.");
    
    return atom;
  } // parseEnumeratedListParameters

  // Parse a list of variables and literals.
  private List parseDObjectList() throws SWRLParseException 
  {
    RDFObject dObject;
    List dObjects = null;
    
    if (!parseOnly) dObjects = new ArrayList();
    
    dObject = parseDObject();   
    if (!parseOnly) dObjects.add(dObject);
    
    String token = getNextNonSpaceToken("Expecting additional comma-separated variables or literals or closing parenthesis.");
    while (token.equals(",")) {
      dObject = parseDObject();
      if (!parseOnly) dObjects.add(dObject);      
      token = getNextNonSpaceToken("Expecting ',' or ')'.");      
      if (!(token.equals(",") || token.equals(")"))) throw new SWRLParseException("Expecting ',' or ')', got '" + token + "'.");
    } // if    
    return dObjects;
  } // parseDObjectList

  // Parse a list of variables, literals and individual names. 
  private List parseObjectList() throws SWRLParseException 
  {
    RDFObject object;
    List objects = null;
    
    if (!parseOnly) objects = new ArrayList();
    
    object = parseObject();   
    if (!parseOnly) objects.add(object);
    
    String token = getNextNonSpaceToken("Expecting additional comma-separated variables, literals or individual names or closing parenthesis.");
    while (token.equals(",")) {
      object = parseObject();
      if (!parseOnly) objects.add(object);      
      token = getNextNonSpaceToken("Expecting ',' or ')'.");      
      if (!(token.equals(",") || token.equals(")"))) throw new SWRLParseException("Expecting ',' or ')', got '" + token + "'.");
    } // if    
    return objects;
  } // parseObjectList

  // Parse a variable, literal or an individual name.
  private RDFObject parseObject() throws SWRLParseException 
  {
    RDFObject parsedEntity = null;
    String parsedString = getNextNonSpaceToken("Expecting variable or individual name or literal.");
    
    if (parsedString.equals("?")) parsedEntity = parseVariable();
    else { // The entity is an individual name or literal
      if (isValidIndividualName(parsedString)) {
        if (!parseOnly) parsedEntity = getIndividual(parsedString);
      } else parsedEntity = parseLiteral(parsedString);
    } // if
    
    return parsedEntity;
  } // parseObject

  // Parse a variable or an individual name.
  private RDFResource parseIObject() throws SWRLParseException 
  {
    RDFResource parsedEntity = null;
    String parsedString = getNextNonSpaceToken("Expecting variable or individual name.");
    
    if (parsedString.equals("?")) parsedEntity = parseVariable();
    else { // The entity is an individual name
      if (!isValidIndividualName(parsedString) && tokenizer.hasMoreTokens())
        throw new SWRLParseException("Invalid individual name: '" + parsedString + "'.");
      if (!parseOnly) parsedEntity = getIndividual(parsedString);
    } // if
    return parsedEntity;
  } // parseIObject

  // Parse a variable or a literal.
  private RDFObject parseDObject() throws SWRLParseException 
  {
    RDFObject parsedEntity = null;
    String parsedString = getNextNonSpaceToken("Expecting variable or literal.");

    if (parsedString.equals("?")) parsedEntity = parseVariable();
    else parsedEntity = parseLiteral(parsedString);

    return parsedEntity;
  } // parseDObject

  private RDFResource parseVariable() throws SWRLParseException
  {
    RDFResource parsedEntity = null;
    String variableName = getNextNonSpaceToken("Expected variable name");      
    checkThatVariableNameIsValid(variableName);
    
    if (tokenizer.hasMoreTokens()) {
      if (!inHead) variables.add(variableName);
      else if (!variables.contains(variableName))
        throw new SWRLParseException("Variable '" + variableName + "' referred to in consequent is not present in antecedent.");
    } // if      
    if (!parseOnly) parsedEntity = getSWRLVariable(variableName);
    return parsedEntity;
  } // parseVariable

  private RDFObject parseLiteral(String parsedString) throws SWRLParseException
  {
    RDFObject parsedEntity = null;

    if (parsedString.equals("\"")) { // The parsed entity is a string
      String stringValue = getNextStringToken("Expected a string.");
      if (!parseOnly) parsedEntity = owlModel.createRDFSLiteral(stringValue, owlModel.getXSDstring());
      checkAndSkipToken("\"", "Expected \" to close string.");
    } // if
    // According to the XSD spec, xsd:boolean's have the lexical space: {true, false, 1, 0}. We don't allow {1, 0} since these are parsed
    // as XSDints.
    else if (parsedString.startsWith("t") || parsedString.startsWith("T") || parsedString.startsWith("f") || parsedString.startsWith("F")) {
      if (tokenizer.hasMoreTokens()) {
        if (parsedString.equalsIgnoreCase("true") || parsedString.equalsIgnoreCase("false")) {
          if (!parseOnly) parsedEntity = owlModel.createRDFSLiteral(parsedString, owlModel.getXSDboolean());
        } else throw new SWRLParseException("Invalid literal '" + parsedString + "'.");
      } // if
    } else { // Is it an integer, float, long or double then?
      try {
        int integerValue = Integer.parseInt(parsedString);
        if (!parseOnly) parsedEntity = owlModel.createRDFSLiteral(parsedString, owlModel.getXSDint());
      } catch (NumberFormatException e1) {
        try {
          float floatValue = Float.parseFloat(parsedString);
          if (!parseOnly) parsedEntity = owlModel.createRDFSLiteral(parsedString, owlModel.getXSDfloat());
        } catch (NumberFormatException e3) {
          try {
            double doubleValue = Double.parseDouble(parsedString);
            if (!parseOnly) parsedEntity = owlModel.createRDFSLiteral(parsedString, owlModel.getXSDdouble());
          } catch (NumberFormatException e4) {
            try { 
              long longValue = Long.parseLong(parsedString);
              if (!parseOnly) parsedEntity = owlModel.createRDFSLiteral(parsedString, owlModel.getXSDlong());
            } catch (Exception e5) { 
              throw new SWRLParseException("Invalid literal '" + parsedString + "'."); 
            } // try
          } // try
        } // try
      } // try
    } // if
    
    return parsedEntity;
  } // parseLiteral

  private void checkThatVariableNameIsValid(String variableName) throws SWRLParseException
  {
    RDFResource resource;
    checkThatIdentifierIsValid(variableName);
    
    resource = owlModel.getRDFResource(variableName);

    if ((resource != null) && !(resource instanceof SWRLVariable)) 
	throw new SWRLParseException("Invalid variable name: '" + variableName + 
				     "'. Cannot use name of existing OWL class, property, or individual.");
  } // checkThatVariableNameIsValid

  private boolean isSameAs(String identifier) throws SWRLParseException 
  {
    return identifier.equalsIgnoreCase("sameAs");
  } // isSameAs

  private boolean isDifferentFrom(String identifier) throws SWRLParseException 
  {
    return identifier.equalsIgnoreCase("differentFrom");
  } // isDifferentFrom

  private boolean isOWLClassName(String identifier) throws SWRLParseException 
  {
    return owlModel.getRDFResource(identifier) instanceof OWLNamedClass;
  } // isOWLClassName

  private boolean isOWLObjectPropertyName(String identifier) throws SWRLParseException 
  {
    return owlModel.getRDFResource(identifier) instanceof OWLObjectProperty;
  } // isOWLObjectPropertyName

  private boolean isOWLDatatypePropertyName(String identifier) throws SWRLParseException 
  {
    return owlModel.getRDFResource(identifier) instanceof OWLDatatypeProperty;
  } // isOWLDatatypePropertyName

  private boolean isBuiltinName(String identifier) throws SWRLParseException 
  {
    RDFResource resource = owlModel.getRDFResource(identifier);
    return resource != null && resource.getProtegeType().getName().equals(SWRLNames.Cls.BUILTIN);
  } // isBuiltinName

  private boolean isXSDDatatype(String identifier) throws SWRLParseException 
  {
    return (identifier.startsWith("xsd:") && xmlSchemaSymbols.contains(identifier.substring(4)));
  } // isXSDDatatype
  
  private void checkThatIdentifierIsValid(String identifier) throws SWRLParseException
  {
    if (!isValidIdentifier(identifier)) throw new SWRLParseException("Invalid identifier: '" + identifier + "'.");
  } // checkThatIdentifierIsValid

  private boolean isValidIdentifier(String s) 
  {
    if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) return false;

    for (int i = 1; i < s.length(); i++) {
      char c = s.charAt(i);
      if (!(Character.isJavaIdentifierPart(c) || c == ':' || c == '-')) {
        return false;
      } // if
    } // for
    return true;
  } // isValidIdentifier

  private boolean isValidIndividualName(String name) throws SWRLParseException 
  {
    RDFResource resource = owlModel.getRDFResource(name);

    if (resource == null) return false;

    if (resource instanceof OWLIndividual) return true;
    else return false;
  } // isValidIndividualName

  private RDFResource getIndividual(String name) throws SWRLParseException 
  {
    RDFResource resource = owlModel.getRDFResource(name);

    if (resource == null) throw new SWRLParseException("'" + name + "' is not a valid individual name");

    if (resource instanceof SWRLVariable) 
      throw new SWRLParseException("'" + name + "' is the name of an OWL individual representing a SWRL variable - " +
                                   "it cannot be referenced directly in a SWRL rule");
    return resource;
  } // getIndividual


  private SWRLVariable getSWRLVariable(String name) throws SWRLParseException 
  {
    RDFResource resource = owlModel.getRDFResource(name);
    
    if (resource instanceof SWRLVariable) return (SWRLVariable) resource;
    else if (resource == null) return swrlFactory.createVariable(name);
    else throw new SWRLParseException(name + " cannot be used as a variable name");
  } // getSWRLVariable
} // SWRLParser
