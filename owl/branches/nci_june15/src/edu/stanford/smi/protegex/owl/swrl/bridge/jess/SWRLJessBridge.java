
package edu.stanford.smi.protegex.owl.swrl.bridge.jess;

import edu.stanford.smi.protegex.owl.swrl.bridge.jess.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.jess.ui.icons.JessIcons;
import jess.*;

import java.util.*;

public class SWRLJessBridge extends SWRLRuleEngineBridge
{
  private Rete rete;

  public SWRLJessBridge(OWLModel owlModel, Rete rete) throws SWRLRuleEngineBridgeException
  {
    super(owlModel);
    this.rete = rete;
    initializeRuleEngine();

    SWRLJessBridgeAdapter.setBridge(this);
  } // SWRLJessBridge

  public void runRuleEngine() throws SWRLRuleEngineBridgeException
  {
    try {
      rete.run();
    } catch (JessException e) {
      throw new SWRLJessBridgeException("Error running Jess rule engine: " + e.toString());
    } // try
  } // runRuleEngine

  protected void initializeRuleEngine() throws SWRLRuleEngineBridgeException
  {
    try {
      configureRete();
    } catch (JessException e) {
      throw new SWRLJessBridgeException("Error initializing Jess rule engine: " + e.toString());
    } // try
  } // initializeRuleEngine

  protected void defineRule(RuleInfo ruleInfo) throws SWRLRuleEngineBridgeException
  {
    executeCommand(getRuleRepresentation(ruleInfo, true));
  } // defineRule
    
  protected void defineClass(ClassInfo classInfo) throws SWRLRuleEngineBridgeException
  {
    executeCommand(getClassRepresentation(classInfo));
  } // defineClass    
  
  protected void defineProperty(PropertyInfo propertyInfo) throws SWRLRuleEngineBridgeException
  {
    executeCommand(getPropertyRepresentation(propertyInfo));
  } // defineProperty

  protected void defineIndividual(IndividualInfo individualInfo) throws SWRLRuleEngineBridgeException
  {
    executeCommand(getIndividualRepresentation(individualInfo));
  } // defineIndividual

  // Jess class that contains an imlpementation of the invokeSWRLBuiltIn user function.

  private class InvokeSWRLBuiltIn implements Userfunction
  {
    SWRLRuleEngineBridge bridge;

    public InvokeSWRLBuiltIn(SWRLRuleEngineBridge bridge)
    {
      this.bridge = bridge;
    } // InvokeSWRLBuiltIn

    public String getName() { return "invokeSWRLBuiltIn"; }
  
    public Value call(ValueVector vv, Context context) throws JessException 
    {
      boolean result;
      List arguments;

      String builtInName = vv.get(1).stringValue(context).toUpperCase();

      arguments = new ArrayList();

      for (int argumentNumber = 2; argumentNumber < vv.size(); argumentNumber++) {
        Argument argument;
        Value value = (Value)vv.get(argumentNumber);

        if (value.type() == RU.VARIABLE) {
          Variable variable = (Variable)value;
          value = variable.resolveValue(context); // overwrite value
        } // if

        if (value.type() == RU.SYMBOL) argument = new IndividualInfo(value.symbolValue(context));
        else argument = new LiteralInfo(value.stringValue(context)); // Treat everything else as a string literal.

        arguments.add(argument);
      } // for

      try {
        result = bridge.invokeSWRLBuiltIn(builtInName, arguments);
      } catch (BuiltInException e) {
        throw new JessException(builtInName, "Error invoking built-in from Jess", e);
      } // try

      return new Value(result);
    } // call
  } // InvokeSWRLBuiltIn

  private void configureRete() throws SWRLRuleEngineBridgeException, JessException
  {
    try {
      rete.clear();    
      
      rete.executeCommand("defclass simple edu.stanford.smi.protegex.owl.swrl.bridge.jess.SWRLJessBridgeAdapter");
      rete.executeCommand("(bind ?sjba (new edu.stanford.smi.protegex.owl.swrl.bridge.jess.SWRLJessBridgeAdapter))");

      rete.addUserfunction(new InvokeSWRLBuiltIn(this));
      
    } catch (JessException e) {
      throw new SWRLJessBridgeException("Error configuring Jess rule engine: " + e.toString());
    } // try
  } // configureRete

  private void executeCommand(String command) throws SWRLRuleEngineBridgeException
  {
    try {
      rete.executeCommand(command);
    } catch (JessException e) {
      throw new SWRLJessBridgeException(e.toString());
    } // try
  } // executeCommand

  private String getRuleRepresentation(RuleInfo ruleInfo, boolean isExecuteable) throws SWRLRuleEngineBridgeException
  {
    AtomInfo atomInfo;
    Iterator iterator;
    String representation;

    representation = "(defrule " + ruleInfo.getName() + " ";
    iterator = ruleInfo.getBody().iterator();
    while (iterator.hasNext()) {
      atomInfo = (AtomInfo)iterator.next();
      representation += getAtomRepresentation(atomInfo, false, isExecuteable) + " ";
    } // while
    representation += " => ";
    iterator = ruleInfo.getHead().iterator();
    while (iterator.hasNext()) {
      atomInfo = (AtomInfo)iterator.next();
      representation += getAtomRepresentation(atomInfo, true, isExecuteable) + " ";
    } // while
    representation += ")";
    
    return representation;
  } // getRuleRepresentation

  private String getClassRepresentation(ClassInfo info) throws SWRLRuleEngineBridgeException
  {
    Iterator iterator = info.getDirectSuperClassNames().iterator();
    String representation = "";

    if (!iterator.hasNext()) { // Has no superclass.
      representation = "(deftemplate " + info.getName() + " (slot name))";
    } else { // Has one or more superclasses.
      String superclassName = (String)iterator.next();
      representation = "(deftemplate " + info.getName() + " extends " + superclassName + ")";
      // If we have multiple superclasses we can't use Jess templates to indicate a hierarchy because templates do not allow multiple
      // inheritance. However, we already exhaustively define the class membership for each individual so the template mechanism is purely
      // an optimization and is not required.
    } // if
    return representation;
  } // getClassRepresentation

  private String getPropertyRepresentation(PropertyInfo propertyInfo) throws SWRLRuleEngineBridgeException
  {
    String representation = "";
    
    representation += "(assert (" + propertyInfo.getName() + " ";
    representation += getArgumentRepresentation(propertyInfo.getSubject()) + " ";
    representation += getArgumentRepresentation(propertyInfo.getPredicate()) + ")) ";
    
    return representation;
  } // getPropertyRepresentation

  private String getIndividualRepresentation(IndividualInfo individualInfo) throws SWRLRuleEngineBridgeException
  {
    String representation = "";

    Iterator iterator = individualInfo.getClassNames().iterator();
    while (iterator.hasNext()) {
      String className = (String)iterator.next();
      representation += "(assert (" + className + " (name " + individualInfo.getName() + ")))";      
    } // while

    return representation;
  } // getIndividualRepresentation

  private String getLiteralRepresentation(LiteralInfo info) throws SWRLRuleEngineBridgeException
  {
    if (info.isNumeric()) return info.getValue();
    else return "\"" + info.getValue() + "\"";
  } // getLiteralRepresentation

  private String getVariableRepresentation(VariableInfo info) throws SWRLRuleEngineBridgeException
  {
    return "?" + info.getName();
  } // getVariableRepresentation

  private String getArgumentRepresentation(Argument argument) throws SWRLRuleEngineBridgeException
  {
    String representation = "";

    if (argument instanceof VariableInfo) representation = getVariableRepresentation((VariableInfo)argument);
    else if (argument instanceof LiteralInfo) representation = getLiteralRepresentation((LiteralInfo)argument);
    else if (argument instanceof IndividualInfo) representation = ((IndividualInfo)argument).getName();
    else throw new SWRLJessBridgeException("Unknown atom argument type: " + argument.getName());

    return representation;
  } // getArgumentRepresentation

  private String getAtomRepresentation(AtomInfo atomInfo, boolean isConsequent, boolean isExecuteable) throws SWRLRuleEngineBridgeException
  {
    String representation = "";

    if (atomInfo instanceof ClassAtomInfo) 
      representation = getClassAtomRepresentation((ClassAtomInfo)atomInfo, isConsequent, isExecuteable);
    else if (atomInfo instanceof DatavaluedPropertyAtomInfo) 
      representation = getDatavaluedPropertyAtomRepresentation((DatavaluedPropertyAtomInfo)atomInfo, isConsequent, isExecuteable);
    else if (atomInfo instanceof IndividualPropertyAtomInfo) 
      representation = getIndividualPropertyAtomRepresentation((IndividualPropertyAtomInfo)atomInfo, isConsequent, isExecuteable);
    else if (atomInfo instanceof SameIndividualAtomInfo) 
      representation = getSameIndividualAtomRepresentation((SameIndividualAtomInfo)atomInfo, isConsequent);
    else if (atomInfo instanceof DifferentIndividualsAtomInfo) 
      representation = getDifferentIndividualsAtomRepresentation((DifferentIndividualsAtomInfo)atomInfo, isConsequent);
    else if (atomInfo instanceof BuiltInAtomInfo) 
      representation = getBuiltInAtomRepresentation((BuiltInAtomInfo)atomInfo, isConsequent, isExecuteable);
    else if (atomInfo instanceof DataRangeAtomInfo) 
      representation = getDataRangeAtomRepresentation((DataRangeAtomInfo)atomInfo, isConsequent);
    else throw new SWRLJessBridgeException("Unknown atom: " + atomInfo.getName());

    return representation;
  } // getAtomRepresentation

  private String getClassAtomRepresentation(ClassAtomInfo info, boolean isConsequent, boolean isExecuteable) throws SWRLRuleEngineBridgeException
  {
    String representation;

    if (isConsequent) 
      representation = "(assert (" + info.getName() + " (name " + getArgumentRepresentation(info.getArgument1()) + ")))";
    else 
      representation = "(" + info.getName() + " (name " + getArgumentRepresentation(info.getArgument1()) + "))";

    if (isConsequent && isExecuteable) representation += " (call ?sjba assertIndividual " + getArgumentRepresentation(info.getArgument1()) + 
                                         " \"" + info.getName() + "\")";

    return representation;
      
  } // getClassAtomRepresentation

  private String getDatavaluedPropertyAtomRepresentation(DatavaluedPropertyAtomInfo info, boolean isConsequent, boolean isExecuteable) 
    throws SWRLRuleEngineBridgeException
  {
    String representation;
    
    if (isConsequent) 
      representation = "(assert (" + info.getName() + " " +
        getArgumentRepresentation(info.getArgument1()) + " " + getArgumentRepresentation(info.getArgument2()) + "))";
    else 
      representation = "(" + info.getName() + " " +
        getArgumentRepresentation(info.getArgument1()) + " " + getArgumentRepresentation(info.getArgument2()) + ")";

    if (isConsequent && isExecuteable) representation += "(call ?sjba assertProperty \"" +  info.getName() + "\" " +
                                         getArgumentRepresentation(info.getArgument1()) + " " + getArgumentRepresentation(info.getArgument2()) + ")";
    
    return representation;
  } // getDatavaluedPropertyAtomRepresentation

  private String getIndividualPropertyAtomRepresentation(IndividualPropertyAtomInfo info, boolean isConsequent, boolean isExecuteable) 
    throws SWRLRuleEngineBridgeException
  {
    String representation;
    
    if (isConsequent) 
      representation = "(assert (" + info.getName() + " " +
        getArgumentRepresentation(info.getArgument1()) + " " + getArgumentRepresentation(info.getArgument2()) + "))";
    else 
      representation = "(" + info.getName() + " " +
        getArgumentRepresentation(info.getArgument1()) + " " + getArgumentRepresentation(info.getArgument2()) + ")";

    if (isConsequent && isExecuteable) representation += "(call ?sjba assertProperty \"" + info.getName() + "\" " +
                                         getArgumentRepresentation(info.getArgument1()) + " " + 
                                         getArgumentRepresentation(info.getArgument2()) + ")";

    return representation;
  } // getIndividualPropertyAtomRepresentation

  private String getSameIndividualAtomRepresentation(SameIndividualAtomInfo info, boolean isConsequent) 
    throws SWRLRuleEngineBridgeException
  {
    String representation;
    
    representation = "(assert (sameAs " +
      getArgumentRepresentation(info.getArgument1()) + " " + getArgumentRepresentation(info.getArgument2()) + "))";

    return representation;
  } // getSameIndividualAtomRepresentation

  private String getDifferentIndividualsAtomRepresentation(DifferentIndividualsAtomInfo info, boolean isConsequent) 
    throws SWRLRuleEngineBridgeException
  {
    String representation;
    
    representation = "(assert (differentFrom " +
      getArgumentRepresentation(info.getArgument1()) + " " + getArgumentRepresentation(info.getArgument2()) + "))";

    return representation;
  } // getDifferentIndividualsAtomRepresentation

  private String getBuiltInAtomRepresentation(BuiltInAtomInfo info, boolean isConsequent, boolean isExecuteable) 
    throws SWRLRuleEngineBridgeException
  {
    Iterator iterator;
    String representation = "";
    String builtInName = info.getName();

    if (isConsequent) new NotImplementedException("Built-ins are not yet supported in rule consequents.");

    if (isExecuteable) representation = "(call ?sjba invokeSWRLBuiltIn " + builtInName + " ";
    else representation = "(" + builtInName + " (";

    iterator = info.getArguments().iterator();
    while (iterator.hasNext()) {
      Argument argument = (Argument)iterator.next();
      representation += getArgumentRepresentation(argument);
      if (iterator.hasNext()) representation += " ";
    } // while
    
    representation += ")";
    
    return representation;
  } // getBuiltInAtomRepresentation

  private String getDataRangeAtomRepresentation(DataRangeAtomInfo info, boolean isConsequent) throws SWRLRuleEngineBridgeException
  {
    String representation = "";

    if (isConsequent) throw new NotImplementedException("Data range atoms are not supported in rule consequents.");

    if (true) throw new NotImplementedException("Data range atoms are not yet implemented.");

    return representation;
  } // getDataRangeAtomRepreentation

  // Methods used to display the contents of the bridge.

  public String getImportedRuleDisplayRepresentation(int count) throws SWRLRuleEngineBridgeException
  {
    if (count < 0 || count > getNumberOfImportedSWRLRules()) throw new SWRLJessBridgeException("Rule number out of range");
    
    return getRuleRepresentation((RuleInfo)getImportedSWRLRules().get(count), false);
  } // getImportedRuleDisplayRepresentation

  public String getImportedClassDisplayRepresentation(int count) throws SWRLRuleEngineBridgeException
  {
    if (count < 0 || count > getNumberOfImportedClasses()) throw new SWRLJessBridgeException("Class number out of range");
    
    return getClassRepresentation((ClassInfo)getImportedClasses().get(count));
  } // getImportedClassDisplayRepresentation
  
  public String getImportedPropertyDisplayRepresentation(int count) throws SWRLRuleEngineBridgeException
  {
    if (count < 0 || count > getNumberOfImportedProperties()) throw new SWRLJessBridgeException("Property number out of range");
    
    return getPropertyRepresentation((PropertyInfo)getImportedProperties().get(count));
  } // getImportedPropertyDisplayRepresentation

  public String getImportedIndividualDisplayRepresentation(int count) throws SWRLRuleEngineBridgeException
  {
    if (count < 0 || count > getNumberOfImportedIndividuals()) throw new SWRLJessBridgeException("Individual number out of range");
    
    return getIndividualRepresentation((IndividualInfo)getImportedIndividuals().get(count));
  } // getImportedIndividualDisplayRepresentation

  public String getAssertedPropertyDisplayRepresentation(int count) throws SWRLRuleEngineBridgeException
  {
    if (count < 0 || count > getNumberOfAssertedProperties()) throw new SWRLJessBridgeException("Property number out of range");
    
    return getPropertyRepresentation((PropertyInfo)getAssertedProperties().get(count));
  } // getAssertedPropertyDisplayRepresentation

  public String getAssertedIndividualDisplayRepresentation(int count) throws SWRLRuleEngineBridgeException
  {
    if (count < 0 || count > getNumberOfAssertedIndividuals()) throw new SWRLJessBridgeException("Individual number out of range");
    
    return getIndividualRepresentation((IndividualInfo)getAssertedIndividuals().get(count));
  } // getAssertedIndividualDisplayRepresentation

} // SWRLJessBridge
