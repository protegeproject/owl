
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlm;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;

import org.nfunk.jep.JEP; // Expecting JEP 2.4.0; may work with other versions but has not been tested.

/**
 ** Implementations library for SWRL mathematical built-in methods. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLTabMathematicalBuiltIns">here</a> for documentation on this library.
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary
{
  private static String SWRLMLibraryName = "SWRLTabMathematicalBuiltIns";
  private JEP jep = null;

  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLMLibraryName); 
  } // SWRLMLibraryImpl

  public void reset() 
  {
    jep = null;
  } // reset

  /**
   ** Returns true if the first argument is equal to the square root of the second argument. If the first argument is unbound, bind it to
   ** the square root of the second argument.
   */
  public boolean sqrt(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result;
    double argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    argument2 = SWRLBuiltInUtil.getArgumentAsADouble(1, arguments);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      argument1 = java.lang.Math.sqrt(argument2);
      arguments.set(0, BridgeFactory.createOWLDatatypeValue(argument2));
      result = true;
    } else {
      argument1 = SWRLBuiltInUtil.getArgumentAsADouble(0, arguments);
      result = argument1 == java.lang.Math.sqrt(argument2);
    } // if
    
    return result;
  } // sqrt

  /**
   ** Returns true if the first argument is equal to the natural logarithm (base e) of the second argument. If the first argument is
   ** unbound, bind it to the natural logarithm of the second argument.
   */
  public boolean log(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result;
    double argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    argument2 = SWRLBuiltInUtil.getArgumentAsADouble(1, arguments);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      argument1 = java.lang.Math.log(argument2);
      arguments.set(0, BridgeFactory.createOWLDatatypeValue(argument2));
      result = true;
    } else {
      argument1 = SWRLBuiltInUtil.getArgumentAsADouble(0, arguments);
      result = argument1 == java.lang.Math.log(argument2);
    } // if
    
    return result;
  } // log

  /*
  ** Returns true if the first argument is an individual of class XMLDocument (defined in the <a
  ** href="http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlxml.owl">OWL XML Ontology</a>) that corresponds to an OWL XML
  ** representation of the contents of the XML document named by the second argument. If the first argument is unbound, bind it to the
  ** individual that corresponds to this document.
  */ 
  public boolean eval(List<BuiltInArgument> arguments) throws BuiltInException
  {
    List<BuiltInArgument> variables;
    double value;
    String expression;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    expression = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    if (arguments.size() > 2) {
      List<BuiltInArgument> variableArguments = arguments.subList(2, arguments.size());

      SWRLBuiltInUtil.checkForUnboundArguments(variableArguments, "unexpected unbound expression argument");
      SWRLBuiltInUtil.checkForNonVariableArguments(variableArguments, "unexpected non variable argument");

      for (BuiltInArgument argument : variableArguments) { 
        String variableName = argument.getVariableName(); // We will have already checked that they are all variables
        double variableValue = SWRLBuiltInUtil.getArgumentAsADouble(argument);
        getJEP().addVariable(variableName, variableValue);
      } // for
    } // if

    getJEP().parseExpression(expression);
    if (getJEP().hasError()) throw new BuiltInException("exception parsing expression '" + expression + "': " + getJEP().getErrorInfo());
    value = getJEP().getValue();
    if (getJEP().hasError()) throw new BuiltInException("exception parsing expression '" + expression + "': " + getJEP().getErrorInfo());

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, BridgeFactory.createOWLDatatypeValue(value));
      result = true;
    } else {
      result = value == SWRLBuiltInUtil.getArgumentAsADouble(0, arguments);
    } // if
    
    return result;
  } // eval

  private JEP getJEP()
  {
    if (jep == null) jep = new JEP();

    return jep;
  } // getJEP

} // SWRLBuiltInLibraryImpl
