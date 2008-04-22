
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;

import java.util.*;

/**
 ** Class implementing a SWRL rule
 */
public class SWRLRuleImpl implements SWRLRule
{
  private String ruleName;
 private List<Atom> bodyAtoms, headAtoms;
  private Set<String> referencedVariableNames;
  private ResultImpl sqwrlResult = null;
  private boolean hasSQWRLBuiltIns, hasSQWRLCollectionBuiltIns;
  
  public SWRLRuleImpl(String ruleName, List<Atom> bodyAtoms, List<Atom> headAtoms) throws BuiltInException, SQWRLException
  {
    this.ruleName = ruleName;
    this.bodyAtoms = bodyAtoms;
    this.headAtoms = headAtoms;
    hasSQWRLBuiltIns = false;
    hasSQWRLCollectionBuiltIns = false;
    buildReferencedVariableNames();
    processBodyAtoms();
    processSQWRLAtoms();
  } // SWRLRuleImpl
  
  public String getRuleName() { return ruleName; }
  public List<Atom> getHeadAtoms() { return headAtoms; }
  public List<Atom> getBodyAtoms() { return bodyAtoms; }
  public boolean isSQWRL() { return hasSQWRLBuiltIns; }
  public boolean usesSQWRLCollections() { return hasSQWRLCollectionBuiltIns; }

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
      if ((atom instanceof BuiltInAtom) && ((BuiltInAtom)atom).isSQWRLMakeCollection()) continue;
      result.add(atom);
    } // for

    return result;
  } // getSQWRLPhase2BodyAtoms

  public ResultImpl getSQWRLResult() { return sqwrlResult; }

  /**
   ** Find all built-in atoms with unbound arguments and tell them which of their arguments are unbound by setting each non bound parameter
   ** to null. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge#nid88T">here</a> for a discussion of the role of this
   ** method.
   */
  private void processBodyAtoms() throws BuiltInException
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

          // If a variable argument is not used by any non built-in body atom or is not bound by another body built-in atom it will be
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

    // If we have built-in atoms, construct a new head with built-in atoms moved to the end of the list. Some rule engines (e.g., Jess)
    // expect variables used as parameters to functions to have been defined before their use in a left to right fashion.
    bodyAtoms = processBodyNonBuiltInAtoms(bodyNonBuiltInAtoms);
    bodyAtoms.addAll(bodyBuiltInAtoms);
  } // processBodyAtoms

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
        if (builtInNames.contains(builtInAtom.getBuiltInName())) result.add(builtInAtom);
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

  private void processSQWRLAtoms() throws DatatypeConversionException, BuiltInException
  {
    Map<String, List<BuiltInArgument>> makeCollectionPatternArguments = new HashMap<String, List<BuiltInArgument>>(); 
    Set<String> collectionNames = new HashSet<String>();

    sqwrlResult = new ResultImpl();
    
    preprocessSQWRLHeadBuiltIns();
    preprocessSQWRLMakeBuiltIns(collectionNames, makeCollectionPatternArguments);
    preprocessSQWRLOperationBuiltIns(collectionNames, makeCollectionPatternArguments);
    
    sqwrlResult.configured();
    sqwrlResult.openRow();
    
    if (hasSQWRLCollectionBuiltIns) sqwrlResult.setIsDistinct(); 
  } // configureResult

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
       String builtInName = builtInAtom.getBuiltInName();
       hasSQWRLBuiltIns = true;
       
       for (BuiltInArgument argument : builtInAtom.getArguments()) {
         boolean isArgumentAVariable = argument.isVariable();
         String variableName = null, columnName;
         int argumentIndex = 0, columnIndex;

         if (isArgumentAVariable) {
           variableName = argument.getVariableName(); selectedVariableNames.add(variableName);
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

  private void preprocessSQWRLMakeBuiltIns(Set<String> collectionNames, Map<String, List<BuiltInArgument>> makeCollectionPatternArguments) 
    throws BuiltInException
  {
    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody(SQWRLNames.getCollectionMakeBuiltInNames())) {
      if (builtInAtom.getNumberOfArguments() < 2) throw new SQWRLException("make-collection built-ins must have at least two arguments");
      
      String collectionName = builtInAtom.getArgumentVariableName(0); // First argument is the collection name

      hasSQWRLCollectionBuiltIns = true;
      System.err.println("preprocessSQWRLMakeBuiltIns");

      builtInAtom.setIsSQWRLMakeCollection();
      
      if (!collectionNames.contains(collectionName)) collectionNames.add(collectionName);
      
      if (builtInAtom.getNumberOfArguments() > 2) { // Has pattern arguments
        List<BuiltInArgument> builtInArguments = builtInAtom.getArguments();
        List<BuiltInArgument> patternArguments = builtInArguments.subList(2, builtInArguments.size());

        if (makeCollectionPatternArguments.containsKey(collectionName)) // Pattern should only be supplied once
          throw new SQWRLException("pattern specified more than once for collection ?" + collectionName);
        
        makeCollectionPatternArguments.put(collectionName, patternArguments); // Store pattern arguments
      } else { // No pattern arguments
        if (makeCollectionPatternArguments.containsKey(collectionName)) { // See if we have stored ones for this collection
          builtInAtom.addArguments(makeCollectionPatternArguments.get(collectionName)); // Append stored pattern arguments to built-in
          //System.err.println("make-collection - adding stored patternArguments: " + makeCollectionPatternArguments.get(collectionName));
        } // if
      } // if
    } // for
  } // preprocessSQWRLMakeBuiltIns

  private void preprocessSQWRLOperationBuiltIns(Set<String> collectionNames, Map<String, List<BuiltInArgument>> makeCollectionPatternArguments) 
    throws SQWRLException, BuiltInException
  {
    Set<String> cascadedUnboundVariableNames = new HashSet<String>();

    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody()) {
      String builtInName = builtInAtom.getBuiltInName();
      if (SQWRLNames.getCollectionOperationBuiltInNames().contains(builtInName)) {

        if (builtInAtom.getNumberOfArguments() < 1) 
          throw new SQWRLException("collection-operation built-ins must have at least one argument");

        hasSQWRLCollectionBuiltIns = true;
        System.err.println("preprocessSQWRLOperationBuiltIns");

        for (String variableName : builtInAtom.getArgumentsVariableNames()) {
          if (makeCollectionPatternArguments.containsKey(variableName)) { // Variable refers to a set
            //System.err.println("appending arguments for built-in '" + builtInName + "': " + makeCollectionPatternArguments.get(variableName));
            builtInAtom.addArguments(makeCollectionPatternArguments.get(variableName)); // Append each set's pattern arguments to built-in
          } // if
        } // for

        builtInAtom.setUsesSQWRLVariables();
        //System.err.println("adding unbound variables for built-in '" + builtInName + "': " + builtInAtom.getUnboundArgumentVariableNames());
        cascadedUnboundVariableNames.addAll(builtInAtom.getUnboundArgumentVariableNames());
      } else if (!SQWRLNames.getSQWRLBuiltInNames().contains(builtInName)) {
        // Mark later non SQWRL built-ins that (directly or indirectly) use variables bound by collection operation built-ins
        if (builtInAtom.usesAtLeastOneVariableOf(cascadedUnboundVariableNames)) {
          builtInAtom.setUsesSQWRLVariables();
          cascadedUnboundVariableNames.addAll(builtInAtom.getUnboundArgumentVariableNames()); // Record its unbound variables.
        } // if
      } // if
    } // for
  } // preprocessSQWRLOperationBuiltIns

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
