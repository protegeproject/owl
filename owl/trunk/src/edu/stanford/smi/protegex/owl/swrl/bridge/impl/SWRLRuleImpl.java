
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.Atom;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInAtom;
import edu.stanford.smi.protegex.owl.swrl.bridge.ClassAtom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRule;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLNames;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.ResultImpl;

/**
 ** Class implementing a SWRL rule
 */
public class SWRLRuleImpl implements SWRLRule
{
  private String ruleName;
  private List<Atom> bodyAtoms, headAtoms;
  private Set<String> referencedVariableNames;
  private ResultImpl sqwrlResult = null;
  private boolean hasSQWRLBuiltIns, hasSQWRLSetBuiltIns;
  
  public SWRLRuleImpl(String ruleName, List<Atom> bodyAtoms, List<Atom> headAtoms) throws SQWRLException, BuiltInException
  {
    this.ruleName = ruleName;
    this.bodyAtoms = bodyAtoms;
    this.headAtoms = headAtoms;
    hasSQWRLBuiltIns = false;
    hasSQWRLSetBuiltIns = false;
    buildReferencedVariableNames();
    processBodyAtoms();
    processSQWRLAtoms();
  } // SWRLRuleImpl
  
  public String getRuleName() { return ruleName; }
  public List<Atom> getHeadAtoms() { return headAtoms; }
  public List<Atom> getBodyAtoms() { return bodyAtoms; }
  public boolean isSQWRL() { return hasSQWRLBuiltIns || hasSQWRLSetBuiltIns; }
  public boolean usesSQWRLSets() { return hasSQWRLSetBuiltIns; }

  public List<BuiltInAtom> getBuiltInAtomsFromHead() { return getBuiltInAtoms(headAtoms); }
  public List<BuiltInAtom> getBuiltInAtomsFromHead(Set<String> builtInNames) { return getBuiltInAtoms(headAtoms, builtInNames); }

  public List<BuiltInAtom> getBuiltInAtomsFromBody() { return getBuiltInAtoms(bodyAtoms); }
  public List<BuiltInAtom> getBuiltInAtomsFromBody(Set<String> builtInNames) { return getBuiltInAtoms(bodyAtoms, builtInNames); }

  public void appendAtomsToBody(List<Atom> atoms)
  {
    bodyAtoms.addAll(atoms);
  } // appendAtomToBody

  public List<Atom> getSQWRLPhase1BodyAtoms()
  {
    List<Atom> result = new ArrayList<Atom>();

    for (Atom atom : bodyAtoms) {
      if ((atom instanceof BuiltInAtom) && ((BuiltInAtom)atom).usesSQWRLVariables()) continue;
      result.add(atom);
    } // for

    return result;
  } // getSQWRLPhase1BodyAtoms

  public List<Atom> getSQWRLPhase2BodyAtoms()
  {
    List<Atom> result = new ArrayList<Atom>();

    for (Atom atom : bodyAtoms) {
      if ((atom instanceof BuiltInAtom) && ((BuiltInAtom)atom).isSQWRLMakeSet()) continue;
      result.add(atom);
    } // for

    //System.err.println("SWRLRuleImpl.getSQWRLPhase2BodyAtoms: " + result);

    return result;
  } // getSQWRLPhase2BodyAtoms

  public ResultImpl getSQWRLResult() { return sqwrlResult; }

  /**
   ** Find all built-in atoms with unbound arguments and tell them which of their arguments are unbound by setting each non bound parameter
   ** to null. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge#nid88T">here</a> for a discussion of the role of this
   ** method.
   */
  private void processBodyAtoms() throws SQWRLException, BuiltInException
  {
    List<BuiltInAtom> bodyBuiltInAtoms = new ArrayList<BuiltInAtom>();
    List<Atom> bodyNonBuiltInAtoms = new ArrayList<Atom>();
    Set<String> variableNamesUsedByNonBuiltInBodyAtoms = new HashSet<String>();
    Set<String> boundBuiltInVariableNames = new HashSet<String>(); // Names of variables bound by built-ins in this rule.
 
    // Process the body atoms and build up list of (1) built-in body atoms, and (2) the variables used by non-built body in atoms.
    for (Atom atom : getBodyAtoms()) {
      if (atom instanceof BuiltInAtom) bodyBuiltInAtoms.add((BuiltInAtom)atom);
      else {
        bodyNonBuiltInAtoms.add(atom); variableNamesUsedByNonBuiltInBodyAtoms.addAll(atom.getReferencedVariableNames());
      } // if
    } // for

    // Process the body built-in atoms and determine if they bind any of their arguments.
    for (BuiltInAtom builtInAtom : bodyBuiltInAtoms) { // Read through built-in arguments and determine which are unbound.
      for (BuiltInArgument argument : builtInAtom.getArguments()) {
        if (argument.isVariable()) {
          String argumentVariableName = argument.getVariableName();

          // If a variable argument is not used by any non built-in body atom or is not bound by another body built-in atom it will therefore be
          // unbound when this built-in is called. We thus set this built-in argument to unbound. If a built-in binds an argument, all later
          // built-ins (proceeding from left to right) will be passed the bound value of this variable during rule execution.
          if (!variableNamesUsedByNonBuiltInBodyAtoms.contains(argumentVariableName) &&
              !boundBuiltInVariableNames.contains(argumentVariableName)) {
            argument.setUnbound(); // Tell the built-in that it is expected to bind this argument.
            boundBuiltInVariableNames.add(argumentVariableName); // Flag this as a bound variable for later built-ins.
          } // if
        } // if
      } // for
    } // for

    // If we have built-in atoms, construct a new body with built-in atoms moved to the end of the list. Some rule engines (e.g., Jess)
    // expect variables used as parameters to functions to have been defined before their use in a left to right fashion.
    bodyAtoms = processBodyNonBuiltInAtoms(bodyNonBuiltInAtoms);
    bodyAtoms.addAll(bodyBuiltInAtoms);
  } // processBodyAtoms

  /**
   * Build up a list of body class atoms and non class, non built-in atoms. 
   */
  private List<Atom> processBodyNonBuiltInAtoms(List<Atom> bodyNonBuiltInAtoms)
  {
    List<Atom> bodyClassAtoms = new ArrayList<Atom>(); 
    List<Atom> bodyNonClassNonBuiltInAtoms = new ArrayList<Atom>();
    List<Atom> result = new ArrayList<Atom>();

    for (Atom atom : bodyNonBuiltInAtoms) {
      if (atom instanceof ClassAtom) bodyClassAtoms.add(atom);
      else bodyNonClassNonBuiltInAtoms.add(atom);
    } // for
    
    result.addAll(bodyNonClassNonBuiltInAtoms);
    result.addAll(bodyClassAtoms);

    return result;
  } // processBodyNonBuiltInAtoms

  private List<BuiltInAtom> getBuiltInAtoms(List<Atom> atoms, Set<String> builtInNames) 
  {
    List<BuiltInAtom> result = new ArrayList<BuiltInAtom>();
    
    for (Atom atom : atoms) {
      if (atom instanceof BuiltInAtom) {
        BuiltInAtom builtInAtom = (BuiltInAtom)atom;
        if (builtInNames.contains(builtInAtom.getBuiltInPrefixedName())) result.add(builtInAtom);
        } // if
    } // for
    return result;
  } // getBuiltInAtoms

  private List<BuiltInAtom> getBuiltInAtoms(List<Atom> atoms) 
  {
    List<BuiltInAtom> result = new ArrayList<BuiltInAtom>();
    
    for (Atom atom : atoms) if (atom instanceof BuiltInAtom) result.add((BuiltInAtom)atom);

    return result;
  } // getBuiltInAtoms

  private void buildReferencedVariableNames()
  {
    referencedVariableNames = new HashSet<String>();
    
    for (Atom atom : getBodyAtoms()) referencedVariableNames.addAll(atom.getReferencedVariableNames());
  } // buildReferencedVariableNames

  private void processSQWRLAtoms() throws DatatypeConversionException, SQWRLException, BuiltInException
  {
    Map<String, List<BuiltInArgument>> setGroupArgumentsMap = new HashMap<String, List<BuiltInArgument>>(); 
    Set<String> setNames = new HashSet<String>();

    sqwrlResult = new ResultImpl();
    
    preprocessSQWRLHeadBuiltIns();
    preprocessSQWRLSetBuildBuiltIns(setNames, setGroupArgumentsMap);
    preprocessSQWRLSetOperationBuiltIns(setNames, setGroupArgumentsMap);
    
    sqwrlResult.configured();
    sqwrlResult.openRow();
    
    if (hasSQWRLSetBuiltIns) sqwrlResult.setIsDistinct(); 
  } // configureResult

  /**
   * Give each built-in a unique index proceeding from left to right.
   */
  private void preprocessBuiltInIndexes()
  {
    int builtInIndex = 0;

    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody()) builtInAtom.setBuiltInIndex(builtInIndex++);
    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromHead()) builtInAtom.setBuiltInIndex(builtInIndex++);
  } // preprocessBuiltInIndexes

  private void preprocessSQWRLHeadBuiltIns() throws DatatypeConversionException, SQWRLException, BuiltInException
  {
     List<String> selectedVariableNames = new ArrayList<String>();

     preprocessBuiltInIndexes();

     for (BuiltInAtom builtInAtom : getBuiltInAtomsFromHead(SQWRLNames.getHeadBuiltInNames())) {
       String builtInName = builtInAtom.getBuiltInPrefixedName();
       hasSQWRLBuiltIns = true;
       
       for (BuiltInArgument argument : builtInAtom.getArguments()) {
         boolean isArgumentAVariable = argument.isVariable();
         String variableName = null, columnName;
         int argumentIndex = 0, columnIndex;

         if (isArgumentAVariable) {
           variableName = argument.getPrefixedVariableName(); selectedVariableNames.add(variableName);
         } // if
         
         if (builtInName.equalsIgnoreCase(SQWRLNames.Select)) {
           if (isArgumentAVariable) columnName = "?" + variableName;
           else columnName = "[" + argument + "]";
           sqwrlResult.addColumn(columnName);
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.SelectDistinct)) {
           if (isArgumentAVariable) columnName = "?" + variableName; else columnName = "[" + argument + "]";
           sqwrlResult.addColumn(columnName); sqwrlResult.setIsDistinct();
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.Count)) {
           if (isArgumentAVariable) columnName = "count(?" + variableName + ")"; else columnName = "[" + argument + "]";
           sqwrlResult.addAggregateColumn(columnName, SQWRLNames.CountAggregateFunction);
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.CountDistinct)) {
           if (isArgumentAVariable) columnName = "countDistinct(?" + variableName + ")"; else columnName = "[" + argument + "]";
           sqwrlResult.addAggregateColumn(columnName, SQWRLNames.CountDistinctAggregateFunction);
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.Min)) {
           if (isArgumentAVariable) columnName = "min(?" + variableName + ")"; else columnName = "min[" + argument + "]";
           sqwrlResult.addAggregateColumn(columnName, SQWRLNames.MinAggregateFunction);
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.Max)) {
           if (isArgumentAVariable) columnName = "max(?" + variableName + ")"; else columnName = "max[" + argument + "]";
           sqwrlResult.addAggregateColumn(columnName, SQWRLNames.MaxAggregateFunction);
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.Sum)) {
           if (isArgumentAVariable) columnName = "sum(?" + variableName + ")"; else columnName = "sum[" + argument + "]";
           sqwrlResult.addAggregateColumn(columnName, SQWRLNames.SumAggregateFunction);
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.Median)) {
           if (isArgumentAVariable) columnName = "median(?" + variableName + ")"; else columnName = "median[" + argument + "]";
           sqwrlResult.addAggregateColumn(columnName, SQWRLNames.MedianAggregateFunction);
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.Avg)) {
           if (isArgumentAVariable) columnName = "avg(?" + variableName + ")"; else columnName = "avg[" + argument + "]";
           sqwrlResult.addAggregateColumn(columnName, SQWRLNames.AvgAggregateFunction);
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.OrderBy)) {
           if (!isArgumentAVariable) throw new SQWRLException("only variables allowed for ordered columns - found '" + argument + "'");
           columnIndex = selectedVariableNames.indexOf(variableName);
           if (columnIndex != -1) sqwrlResult.addOrderByColumn(columnIndex, true);
           else throw new SQWRLException("variable ?" + variableName + " must be selected before it can be ordered");
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.OrderByDescending)) {
           if (!isArgumentAVariable) throw new SQWRLException("only variables allowed for ordered columns - found '" + argument + "'");
           columnIndex = selectedVariableNames.indexOf(variableName);
           if (columnIndex != -1) sqwrlResult.addOrderByColumn(columnIndex, false);
           else throw new SQWRLException("variable ?" + variableName + " must be selected before it can be ordered");
         } else if (builtInName.equalsIgnoreCase(SQWRLNames.ColumnNames)) {
           if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isString()) {
             OWLDatatypeValue literal = (OWLDatatypeValue)argument; sqwrlResult.addColumnDisplayName(literal.getString());
           } else throw new SQWRLException("only string literals allowed as column names - found '" + argument + "'");
         } // if
         argumentIndex++;
       } // for
     } // for
  } // preprocessSQWRLHeadBuiltIns

  // We store the group arguments for each set specified in the make operation; these arguments are later appended to the set
  // operation built-ins
  private void preprocessSQWRLSetBuildBuiltIns(Set<String> setNames, Map<String, List<BuiltInArgument>> setGroupArgumentsMap) 
    throws SQWRLException, BuiltInException
  {
    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody(SQWRLNames.getSetBuildBuiltInNames())) {
      String setName = builtInAtom.getArgumentVariableName(0); // First argument is the set name

      hasSQWRLSetBuiltIns = true;
      builtInAtom.setIsSQWRLMakeSet();

      if (builtInAtom.getBuiltInName().equals(SQWRLNames.MakeSet)) {
    	if (builtInAtom.getNumberOfArguments() < 2) throw new SQWRLException("makeSet built-in must have at least two arguments");
        if (!setNames.contains(setName)) setNames.add(setName);

      } else if (builtInAtom.getBuiltInName().equals(SQWRLNames.GroupBy)) {
        List<BuiltInArgument> builtInArguments = builtInAtom.getArguments();
        List<BuiltInArgument> groupArguments = builtInArguments.subList(1, builtInArguments.size() - 1);

        if (builtInAtom.getNumberOfArguments() < 2) throw new SQWRLException("groupBy built-in must have at least two arguments");
        if (!setNames.contains(setName)) throw new SQWRLException("groupBy applied to undefined set ?" + setName);
        if (setGroupArgumentsMap.containsKey(setName)) throw new SQWRLException("group specified more than once for same set ?" + setName);
        
        setGroupArgumentsMap.put(setName, groupArguments); // Store group arguments

        System.err.println("found group argument for set ?" + setName + " and it is " + groupArguments);
      } // if          
    } // for
  } // preprocessSQWRLSetBuildBuiltIns

  // Append the group arguments to all set operation built-ins for each of it the set arguments; also append group arguments
  // to sets created by operation built-ins.
  private void preprocessSQWRLSetOperationBuiltIns(Set<String> setNames, Map<String, List<BuiltInArgument>> setGroupArgumentsMap) 
    throws SQWRLException, BuiltInException
  {
    Set<String> cascadedUnboundVariableNames = new HashSet<String>();

    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody()) {
      String builtInName = builtInAtom.getBuiltInPrefixedName();
      if (SQWRLNames.isSetOperationBuiltIn(builtInName)) {
        Set<String> argumentsVariableNames;

        if (builtInAtom.getNumberOfArguments() < 1) throw new SQWRLException("set-operation built-ins must have at least one argument");

        hasSQWRLSetBuiltIns = true;
        
        if (builtInName.equals(SQWRLNames.MakeSet)) { // Process set make operator
          String createdSetName = builtInAtom.getArgumentVariableName(0); // Created set is always first argument
          if (setNames.contains(createdSetName)) throw new SQWRLException("set '" + createdSetName + "' can only be created once");
          setNames.add(createdSetName);
          setGroupArgumentsMap.put(createdSetName, new ArrayList<BuiltInArgument>());

          argumentsVariableNames = builtInAtom.getArgumentsVariableNamesExceptFirst(); // Exclude created set
        } else {
          argumentsVariableNames = builtInAtom.getArgumentsVariableNames();
        } // if

        for (String variableName : argumentsVariableNames) {
          if (setGroupArgumentsMap.containsKey(variableName)) { // Variable refers to a set with a group
            //System.err.println("appending arguments from variable '" + variableName + "' for built-in '" + builtInName + "': " + setGroupArgumentsMap.get(variableName));
            List<BuiltInArgument> setGroupArguments = setGroupArgumentsMap.get(variableName);
            builtInAtom.addArguments(setGroupArguments); // Append each set's group arguments to built-in

            // The set generated by an built-in that creates a set will get groups from all the input sets to the built-in
            if (SQWRLNames.isCreateSetOperationBuiltIn(builtInName)) { 
              String createdSetName = builtInAtom.getArgumentVariableName(0); 
              if (!setGroupArgumentsMap.containsKey(createdSetName)) 
                throw new SQWRLException("internal error: set '" + createdSetName + "' has no group set");
              setGroupArgumentsMap.get(createdSetName).addAll(setGroupArguments);
            } // if
          } // if
        } // for

        builtInAtom.setUsesSQWRLVariables();
        //System.err.println("adding unbound variables for built-in '" + builtInName + "': " + builtInAtom.getUnboundArgumentVariableNames());
        cascadedUnboundVariableNames.addAll(builtInAtom.getUnboundArgumentVariableNames());
      } else if (!SQWRLNames.isSQWRLBuiltIn(builtInName)) {
        // Mark later non SQWRL built-ins that (directly or indirectly) use variables bound by set operation built-ins
        if (builtInAtom.usesAtLeastOneVariableOf(cascadedUnboundVariableNames)) {
          builtInAtom.setUsesSQWRLVariables();
          //System.err.println("marking built-in as using SQWRL variables '" + builtInAtom + "'");
          cascadedUnboundVariableNames.addAll(builtInAtom.getUnboundArgumentVariableNames()); // Record its unbound variables.
        } // if
      } // if
    } // for
  } // preprocessSQWRLSetOperationBuiltIns

  public String toString()
  {
    String result = getRuleName() + ": ";
    boolean isFirst = true;

    for (Atom atom : getBodyAtoms()) {
      if (!isFirst) result += " ^ ";
      result += "" + atom;
      isFirst = false;
    } // for
    
    result += " -> ";

    isFirst = true;
    for (Atom atom : getHeadAtoms()) {
      if (!isFirst) result += " ^ ";
      result += "" + atom;
      isFirst = false;
    } // for

    return result;
  } // toString

} // SWRLRuleImpl
