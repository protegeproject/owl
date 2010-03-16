
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.Atom;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInAtom;
import edu.stanford.smi.protegex.owl.swrl.bridge.ClassAtom;
import edu.stanford.smi.protegex.owl.swrl.bridge.DataValueArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.SWRLRule;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DataValueConversionException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLNames;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.SQWRLResultImpl;

/**
 * Class implementing a SWRL rule
 */
public class SWRLRuleImpl implements SWRLRule
{
  private String ruleURI;
  private List<Atom> bodyAtoms, headAtoms;
  private String ruleGroupName;
  
  private Set<String> referencedVariableNames;
  private SQWRLResultImpl sqwrlResult = null;
  private boolean hasSQWRLBuiltIns, hasSQWRLCollectionBuiltIns;
  private Map<String, List<BuiltInArgument>> collectionGroupArgumentsMap;
  private boolean enabled = true;
  
  public SWRLRuleImpl(String ruleURI, List<Atom> bodyAtoms, List<Atom> headAtoms) throws SQWRLException, BuiltInException
  {
    this.ruleURI = ruleURI;
    this.bodyAtoms = bodyAtoms;
    this.headAtoms = headAtoms;
    hasSQWRLBuiltIns = false;
    hasSQWRLCollectionBuiltIns = false;
    ruleGroupName = "";
    collectionGroupArgumentsMap = new HashMap<String, List<BuiltInArgument>>();
    buildReferencedVariableNames();
    processUnboundBuiltInArguments(); 
    processSQWRLBuiltIns();
    processBuiltInArgumentDependencies();
  } 
  
  public String getURI() { return ruleURI; }
  public void setURI(String ruleURI) { this.ruleURI = ruleURI; }
  public void setRuleText(String text) {}
  public List<Atom> getHeadAtoms() { return headAtoms; }
  public List<Atom> getBodyAtoms() { return bodyAtoms; }
  
  public boolean isSQWRL() { return hasSQWRLBuiltIns || hasSQWRLCollectionBuiltIns; }
  public boolean usesSQWRLCollections() { return hasSQWRLCollectionBuiltIns; }
  public String getRuleGroupName() { return ruleGroupName; }
  public void setRuleGroupName(String ruleGroupName) { this.ruleGroupName = ruleGroupName; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(Boolean enable) { this.enabled = enable; }
  
  public List<BuiltInAtom> getBuiltInAtomsFromHead() { return getBuiltInAtoms(headAtoms); }
  public List<BuiltInAtom> getBuiltInAtomsFromHead(Set<String> builtInNames) { return getBuiltInAtoms(headAtoms, builtInNames); }

  public List<BuiltInAtom> getBuiltInAtomsFromBody() { return getBuiltInAtoms(bodyAtoms); }
  public List<BuiltInAtom> getBuiltInAtomsFromBody(Set<String> builtInNames) { return getBuiltInAtoms(bodyAtoms, builtInNames); }
  
  public void appendAtomsToBody(List<Atom> atoms)
  {
    bodyAtoms.addAll(atoms);
  }

  public void setBodyAtoms(List<Atom> atoms)
  {
    bodyAtoms = atoms;
  }

  public List<Atom> getSQWRLPhase1BodyAtoms()
  {
    List<Atom> result = new ArrayList<Atom>();

    for (Atom atom : bodyAtoms) {
      if (atom instanceof BuiltInAtom) {
    	BuiltInAtom builtInAtom = (BuiltInAtom)atom;	
    	if (builtInAtom.usesSQWRLCollectionResults() || builtInAtom.isSQWRLGroupCollection()) continue;
      } // if
      result.add(atom);
    } // for

    return result;
  } 

  public List<Atom> getSQWRLPhase2BodyAtoms()
  {
    List<Atom> result = new ArrayList<Atom>();

    for (Atom atom : bodyAtoms) {
    	if (atom instanceof BuiltInAtom) {
    	  BuiltInAtom builtInAtom = (BuiltInAtom)atom;
    	  if (builtInAtom.isSQWRLMakeCollection() || builtInAtom.isSQWRLGroupCollection()) continue;
      } // if
      result.add(atom);
    } // for

    return result;
  } 

  public SQWRLResultImpl getSQWRLResult() { return sqwrlResult; }

  /**
   * Find all built-in atoms with unbound arguments and tell them which of their arguments are unbound.
   * See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge#nid88T">here</a> for a discussion of the role of this method.
   */
  private void processUnboundBuiltInArguments()
  {
    List<BuiltInAtom> bodyBuiltInAtoms = new ArrayList<BuiltInAtom>();
    List<Atom> bodyNonBuiltInAtoms = new ArrayList<Atom>();
    Set<String> variableNamesUsedByNonBuiltInBodyAtoms = new HashSet<String>(); // By definition, these will always be bound.
    Set<String> variableNamesBoundByBuiltIns = new HashSet<String>(); // Names of variables bound by built-ins in this rule
   
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
              !variableNamesBoundByBuiltIns.contains(argumentVariableName)) {
            argument.setUnbound(); // Tell the built-in that it is expected to bind this argument.
            variableNamesBoundByBuiltIns.add(argumentVariableName); // Flag this as a bound variable for later built-ins.
          } // if
        } // if
      } // for
    } // for
    // If we have built-in atoms, construct a new body with built-in atoms moved to the end of the list. Some rule engines (e.g., Jess)
    // expect variables used as parameters to functions to have been defined before their use in a left to right fashion.
    bodyAtoms = processBodyNonBuiltInAtoms(bodyNonBuiltInAtoms);
    bodyAtoms.addAll(bodyBuiltInAtoms);
  } 
  
  // For every built-in, record the variables it depends from preceding atoms (directly and indirectly). 
  // Should be called after processBuiltInArguments and processSQWRLArguments.
  private void processBuiltInArgumentDependencies() throws BuiltInException
  {
  	Map<String, Set<Set<String>>> pathMap = new HashMap<String, Set<Set<String>>>();
  	Set<String> rootVariableNames = new HashSet<String>();
  	
    for (Atom atom : getBodyAtoms()) {
    	Set<String> thisAtomReferencedVariableNames = new HashSet<String>(atom.getReferencedVariableNames());

    	buildPaths(atom, rootVariableNames, pathMap);
    	
    	if (atom instanceof BuiltInAtom) {
    		BuiltInAtom builtInAtom = (BuiltInAtom)atom;
    		
    		if (builtInAtom.isSQWRLGroupCollection()) continue;
    		if (builtInAtom.isSQWRLCollectionOperation()) break;
    		
    		if (builtInAtom.hasReferencedVariables()) {
        	Set<String> dependsOnVariableNames = new HashSet<String>();
        	
        	for (String rootVariableName : pathMap.keySet()) {
        		for (Set<String> path : pathMap.get(rootVariableName)) {
        			if (!Collections.disjoint(path, thisAtomReferencedVariableNames)) { 
        			  dependsOnVariableNames.addAll(path);
        			  dependsOnVariableNames.add(rootVariableName);
        		  } // if
        		} // for
        	} // for
      
        	if (!dependsOnVariableNames.isEmpty()) {
          	dependsOnVariableNames.removeAll(thisAtomReferencedVariableNames); // Remove our own variables
          	/* TODO: Need to think about correct operation of this
          	if (builtInAtom.isSQWRLMakeCollection()) {
          		String collectionName = builtInAtom.getArgumentVariableName(0); // First argument is the collection name
          		if (collectionGroupArgumentsMap.containsKey(collectionName)) {
          			List<BuiltInArgument> groupArguments = collectionGroupArgumentsMap.get(collectionName);
          			Set<String> groupVariableNames = getVariableNames(groupArguments);
          			if (!groupVariableNames.isEmpty() && !dependsOnVariableNames.containsAll(groupVariableNames)) 
          				throw new BuiltInException("all group arguments must be on variable path for corresponding collection make");
          		} // if
          	} // if
          	 */
          	builtInAtom.setDependsOnVariableNames(dependsOnVariableNames);
        	} // if
    		} // if
    	} // if
    } // for
  }

  /** 
   * Incrementally build variable paths up to and including the current atom. 
   * 
   * Note: Sets of sets in Java require care because of hash code issues. The enclosed set should not be modified or the outer set may 
   * return inconsistent results.  
   */
  private void buildPaths(Atom atom, Set<String> rootVariableNames, Map<String, Set<Set<String>>> pathMap)
  {
  	Set<String> currentAtomReferencedVariableNames = atom.getReferencedVariableNames();
		Set<String> matchingRootVariableNames;
	
		if (currentAtomReferencedVariableNames.size() == 1) { // Make variable a root if we have not yet encountered it 
			String variableName = currentAtomReferencedVariableNames.iterator().next();
			if (getMatchingPaths(pathMap, variableName).isEmpty() && !rootVariableNames.contains(variableName)) {
				Set<Set<String>> paths = new HashSet<Set<String>>();
				pathMap.put(variableName, paths);
				rootVariableNames.add(variableName); 
			} // if
		} else if (currentAtomReferencedVariableNames.size() > 1) {
			Set<String> currentKnownAtomRootVariableNames = new HashSet<String>(currentAtomReferencedVariableNames);
			currentKnownAtomRootVariableNames.retainAll(rootVariableNames);
			
			if (!currentKnownAtomRootVariableNames.isEmpty()) { // At least one of atom's variables reference already known root(s)
				for (String rootVariableName : currentKnownAtomRootVariableNames) {
					Set<String> dependentVariables = new HashSet<String>(currentAtomReferencedVariableNames);
					dependentVariables.remove(rootVariableName);
					
	  			matchingRootVariableNames = getMatchingRootVariableNames(pathMap, dependentVariables);
	  			if (!matchingRootVariableNames.isEmpty()) { // Found existing path(s) that use these variables - add them to existing path(s)
	  				for (String matchingRootVariableName : matchingRootVariableNames) {
	  					Set<Set<String>> paths = pathMap.get(matchingRootVariableName);
	  					Set<Set<String>> matchedPaths = new HashSet<Set<String>>();
	  					for (Set<String> path : paths)
	  					  if (!Collections.disjoint(path, dependentVariables)) matchedPaths.add(path);
	  				  for (Set<String> matchedPath : matchedPaths) {
	  				  	Set<String> newPath = new HashSet<String>(matchedPath);   				  	
	  				  	newPath.addAll(dependentVariables);
	  				  	paths.remove(matchedPath); // Remove the original matched path for this root's path
	  				  	paths.add(Collections.unmodifiableSet(newPath)); // Add the updated path
	  				  } // for
	  				} // for
	  			} else { // Did not find an existing path for this root that uses these variables - add dependent variables as new path 
	  				Set<Set<String>> paths = pathMap.get(rootVariableName);
	  				paths.add(Collections.unmodifiableSet(dependentVariables));
	  			} //if
				} // for
			} else { // No known roots referenced by any of the atom's variables
				matchingRootVariableNames = getMatchingRootVariableNames(pathMap, currentAtomReferencedVariableNames);
				if (!matchingRootVariableNames.isEmpty()) { 
					// Found existing paths that use the atom's variables - add all the variables (none of which is a root) to each path
  				for (String matchingRootVariableName : matchingRootVariableNames) {
  					Set<Set<String>> paths = pathMap.get(matchingRootVariableName);
  					Set<Set<String>> matchedPaths = new HashSet<Set<String>>();

  					for (Set<String> path : paths) 
  					  if (!Collections.disjoint(path, currentAtomReferencedVariableNames)) matchedPaths.add(path);
  				  for (Set<String> matchedPath : matchedPaths) {  // Add the new variables to the matched path and add it to this root's path
  				  	Set<String> newPath = new HashSet<String>(matchedPath);   				  	
  				  	newPath.addAll(currentAtomReferencedVariableNames); // Add all the non-root variable names to this path
  				  	paths.remove(matchedPath); // Remove the original matched path
  				  	paths.add(Collections.unmodifiableSet(newPath)); // Add the updated path
  				  } // for
  				} // for
				} else { // No existing paths have variables from this atom - every variable becomes a root and depends on every other root variable
					for (String rootVariableName : currentAtomReferencedVariableNames) {
						Set<Set<String>> paths = new HashSet<Set<String>>();
						Set<String> dependentVariables = new HashSet<String>(currentAtomReferencedVariableNames);
						dependentVariables.remove(rootVariableName); // Remove the root from its own dependent variables
						paths.add(Collections.unmodifiableSet(dependentVariables));
						pathMap.put(rootVariableName, paths);
						rootVariableNames.add(rootVariableName);
					} // For
				} // if
			} // if
		} // if
  }
		
  @SuppressWarnings("unused") // Used by commented-out group argument checking in processBuiltInArgumentDependencies
  private Set<String> getVariableNames(List<BuiltInArgument> arguments) 
  {
  	Set<String> variableNames = new HashSet<String>();
  
  	for (BuiltInArgument argument : arguments)
  		if (argument.isVariable())
  			variableNames.add(argument.getVariableName());
  	
  	return variableNames;
  }

  private Set<String> getMatchingPaths(Map<String, Set<Set<String>>> pathMap, String variableName)
  { 
  	return getMatchingRootVariableNames(pathMap, Collections.singleton(variableName));
  }
  
  private Set<String> getMatchingRootVariableNames(Map<String, Set<Set<String>>> pathMap, Set<String> variableNames)
  {
    Set<String> matchingRootVariableNames = new HashSet<String>();
  	
  	for (String rootVariableName : pathMap.keySet()) {
  		Set<Set<String>> pathsWithSameRoot = pathMap.get(rootVariableName); 
  		for (Set<String> path : pathsWithSameRoot)
  		 if (!Collections.disjoint(path, variableNames))
  			 matchingRootVariableNames.add(rootVariableName);
  	} // for
  		
  	return matchingRootVariableNames;
  }
  
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
    
    result.addAll(bodyClassAtoms);
    result.addAll(bodyNonClassNonBuiltInAtoms);
    
    return result;
  } 

  private List<BuiltInAtom> getBuiltInAtoms(List<Atom> atoms, Set<String> builtInNames) 
  {
    List<BuiltInAtom> result = new ArrayList<BuiltInAtom>();
    
    for (Atom atom : atoms) {
      if (atom instanceof BuiltInAtom) {
        BuiltInAtom builtInAtom = (BuiltInAtom)atom;
        if (builtInNames.contains(builtInAtom.getBuiltInURI())) result.add(builtInAtom);
        } // if
    } // for
    return result;
  } 

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

  /**
   * Give each built-in a unique index proceeding from left to right.
   */
  private void processBuiltInIndexes()
  {
    int builtInIndex = 0;

    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody()) builtInAtom.setBuiltInIndex(builtInIndex++);
    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromHead()) builtInAtom.setBuiltInIndex(builtInIndex++);
  } // processBuiltInIndexes

  //TODO: too long- refactor
  private void processSQWRLHeadBuiltIns() throws DataValueConversionException, SQWRLException, BuiltInException
  {
     List<String> selectedVariableNames = new ArrayList<String>();

     processBuiltInIndexes();

     for (BuiltInAtom builtInAtom : getBuiltInAtomsFromHead(SQWRLNames.getHeadBuiltInNames())) {
       String builtInName = builtInAtom.getBuiltInURI();
       hasSQWRLBuiltIns = true;
          
       for (BuiltInArgument argument : builtInAtom.getArguments()) {
         boolean isArgumentAVariable = argument.isVariable();
         String variableName = null, columnName;
         int argumentIndex = 0, columnIndex;

         if (SQWRLNames.isSQWRLHeadSelectionBuiltIn(builtInName) || SQWRLNames.isSQWRLHeadAggregationBuiltIn(builtInName)) {
	         if (isArgumentAVariable) { variableName = argument.getVariableName(); selectedVariableNames.add(variableName); }
	                 
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
	           if (!isArgumentAVariable) throw new SQWRLException("only variables allowed for ordered columns - found " + argument);
	           columnIndex = selectedVariableNames.indexOf(variableName);
	           if (columnIndex != -1) sqwrlResult.addOrderByColumn(columnIndex, true);
	           else throw new SQWRLException("variable ?" + variableName + " must be selected before it can be ordered");
	         } else if (builtInName.equalsIgnoreCase(SQWRLNames.OrderByDescending)) {
	           if (!isArgumentAVariable) throw new SQWRLException("only variables allowed for ordered columns - found " + argument);
	           columnIndex = selectedVariableNames.indexOf(variableName);
	           if (columnIndex != -1) sqwrlResult.addOrderByColumn(columnIndex, false);
	           else throw new SQWRLException("variable ?" + variableName + " must be selected before it can be ordered");
	         } else if (builtInName.equalsIgnoreCase(SQWRLNames.ColumnNames)) {
	           if (argument instanceof DataValueArgument && ((DataValueArgument)argument).getDataValue().isString()) {
	             DataValueArgument dataValueArgument = (DataValueArgument)argument;
	             sqwrlResult.addColumnDisplayName(dataValueArgument.getDataValue().getString());
	           } else throw new SQWRLException("only string literals allowed as column names - found " + argument);
	         } // if
	         argumentIndex++;
         } // if
       } // for
         
       if (SQWRLNames.isSQWRLHeadSlicingBuiltIn(builtInName)) {
      	 if (!sqwrlResult.isOrdered() && !builtInName.equals(SQWRLNames.Limit)) throw new SQWRLException("slicing operator used without an order clause");
        	 
      	 if (builtInName.equalsIgnoreCase(SQWRLNames.Least) || builtInName.equalsIgnoreCase(SQWRLNames.First)) {
      		 if (!builtInAtom.getArguments().isEmpty()) throw new SQWRLException("first or least do not accept arguments");
      		 sqwrlResult.setFirst();
      	 } else if (builtInName.equalsIgnoreCase(SQWRLNames.NotLeast) || builtInName.equalsIgnoreCase(SQWRLNames.NotFirst)) {
        		 if (!builtInAtom.getArguments().isEmpty()) throw new SQWRLException("not first or least do not accept arguments");
        		 sqwrlResult.setNotFirst();
      	 } else if (builtInName.equalsIgnoreCase(SQWRLNames.Greatest) || builtInName.equalsIgnoreCase(SQWRLNames.Last)) {
      		 if (!builtInAtom.getArguments().isEmpty()) throw new SQWRLException("greatest or last do not accept arguments");
      		 sqwrlResult.setLast();
      	 } else if (builtInName.equalsIgnoreCase(SQWRLNames.NotGreatest) || builtInName.equalsIgnoreCase(SQWRLNames.NotLast)) {
      		 if (!builtInAtom.getArguments().isEmpty()) throw new SQWRLException("not greatest or last do not accept arguments");
      		 sqwrlResult.setNotLast();
      	 } else {
      		 BuiltInArgument nArgument = builtInAtom.getArguments().get(0);
      		 int n;
      		 
        	 if (nArgument instanceof DataValueArgument && ((DataValueArgument)nArgument).getDataValue().isLong()) {
             n = (int)((DataValueArgument)nArgument).getDataValue().getLong();
             if (n < 1) throw new SQWRLException("nth argument to slicing operator " + builtInName + " must be a positive integer");
        	 } else throw new SQWRLException("expecing integer to slicing operator " + builtInName);

      		 if (builtInAtom.getArguments().size() == 1) {
	        		 if (builtInName.equalsIgnoreCase(SQWRLNames.Limit)) sqwrlResult.setLimit(n);
	             else if (builtInName.equalsIgnoreCase(SQWRLNames.Nth)) sqwrlResult.setNth(n);
	             else if (builtInName.equalsIgnoreCase(SQWRLNames.NotNth)) sqwrlResult.setNotNth(n);
	             else if (builtInName.equalsIgnoreCase(SQWRLNames.FirstN) || builtInName.equalsIgnoreCase(SQWRLNames.LeastN)) sqwrlResult.setFirst(n);
	             else if (builtInName.equalsIgnoreCase(SQWRLNames.LastN) || builtInName.equalsIgnoreCase(SQWRLNames.GreatestN)) sqwrlResult.setLast(n);
	             else if (builtInName.equalsIgnoreCase(SQWRLNames.NotLastN) || builtInName.equalsIgnoreCase(SQWRLNames.NotGreatestN)) sqwrlResult.setNotLast(n);
	             else if (builtInName.equalsIgnoreCase(SQWRLNames.NotFirstN) || builtInName.equalsIgnoreCase(SQWRLNames.NotLeastN)) sqwrlResult.setNotFirst(n);
	             else throw new SQWRLException("unknown slicing operator " + builtInName);
      	 } else if (builtInAtom.getArguments().size() == 2) {
	      		 BuiltInArgument sliceArgument = builtInAtom.getArguments().get(1);
	      		 int sliceSize;
	      		 
	        	 if (sliceArgument instanceof DataValueArgument && ((DataValueArgument)sliceArgument).getDataValue().isLong()) {
	             sliceSize = (int)((DataValueArgument)sliceArgument).getDataValue().getLong();
	             if (sliceSize < 1) throw new SQWRLException("slice size argument to slicing operator " + builtInName + " must be a positive integer");
	        	 } else throw new SQWRLException("expecing integer to slicing operator " + builtInName);
	        	 
	        	 if (builtInName.equalsIgnoreCase(SQWRLNames.NthSlice)) sqwrlResult.setNthSlice(n, sliceSize);
	        	 else if (builtInName.equalsIgnoreCase(SQWRLNames.NotNthSlice)) sqwrlResult.setNotNthSlice(n, sliceSize);
	        	 else if (builtInName.equalsIgnoreCase(SQWRLNames.NthLastSlice) ||
     			 		  builtInName.equalsIgnoreCase(SQWRLNames.NthGreatestSlice)) sqwrlResult.setNthLastSlice(n, sliceSize);
	        	 else if (builtInName.equalsIgnoreCase(SQWRLNames.NotNthLastSlice) ||
     			 		  builtInName.equalsIgnoreCase(SQWRLNames.NotNthGreatestSlice)) sqwrlResult.setNotNthLastSlice(n, sliceSize);
	        	 else throw new SQWRLException("unknown slicing operator " + builtInName);    			
      	 } else throw new SQWRLException("unknown slicing operator " + builtInName);
      	 } // if
       } // if
     } // for       
  } 

  private void processSQWRLBuiltIns() throws DataValueConversionException, SQWRLException, BuiltInException
  { 
    Set<String> collectionNames = new HashSet<String>();
    Set<String> cascadedUnboundVariableNames = new HashSet<String>();

    sqwrlResult = new SQWRLResultImpl();
    
    processSQWRLHeadBuiltIns();
    processSQWRLCollectionMakeBuiltIns(collectionNames); // Find all make collection built-ins
    processSQWRLCollectionGroupByBuiltIns(collectionNames, collectionGroupArgumentsMap); // Find the group arguments for each collection
    processSQWRLCollectionMakeGroupArguments(collectionNames, collectionGroupArgumentsMap); // Add the group arguments to the make built-ins for its collection
    processSQWRLCollectionOperationBuiltIns(collectionNames, collectionGroupArgumentsMap, cascadedUnboundVariableNames);
    processBuiltInsThatUseSQWRLCollectionOperationResults(cascadedUnboundVariableNames);
    
    sqwrlResult.configured();
    sqwrlResult.openRow();
    
    if (hasSQWRLCollectionBuiltIns) sqwrlResult.setIsDistinct(); 
  } 

  // Process all make collection built-ins.
  private void processSQWRLCollectionMakeBuiltIns(Set<String> collectionNames) 
    throws SQWRLException, BuiltInException
  {
    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody(SQWRLNames.getCollectionMakeBuiltInNames())) {
      String collectionName = builtInAtom.getArgumentVariableName(0); // First argument is the collection name
      hasSQWRLCollectionBuiltIns = true;
       
      if (!collectionNames.contains(collectionName)) collectionNames.add(collectionName);
    } // for
  } 

  // We store the group arguments for each collection specified in the make operation; these arguments are later appended to the collection
  // operation built-ins
  private void processSQWRLCollectionGroupByBuiltIns(Set<String> collectionNames, Map<String, List<BuiltInArgument>> collectionGroupArgumentsMap) 
    throws SQWRLException, BuiltInException
  {
    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody(SQWRLNames.getCollectionGroupByBuiltInNames())) {
      String collectionName = builtInAtom.getArgumentVariableName(0); // The first argument is the collection name.
      List<BuiltInArgument> builtInArguments = builtInAtom.getArguments();
      List<BuiltInArgument> groupArguments = builtInArguments.subList(1, builtInArguments.size());

      hasSQWRLCollectionBuiltIns = true;
    
      if (builtInAtom.getNumberOfArguments() < 2) throw new SQWRLException("groupBy must have at least two arguments");
      if (!collectionNames.contains(collectionName)) throw new SQWRLException("groupBy applied to undefined collection ?" + collectionName);
      if (collectionGroupArgumentsMap.containsKey(collectionName)) throw new SQWRLException("groupBy specified more than once for same collection ?" + collectionName);
      if (hasUnboundArgument(groupArguments)) throw new SQWRLException("unbound group argument passed to groupBy for collection ?" + collectionName);
        
      collectionGroupArgumentsMap.put(collectionName, groupArguments); // Store group arguments.          
    } // for
  }

  private void processSQWRLCollectionMakeGroupArguments(Set<String> collectionNames, Map<String, List<BuiltInArgument>> collectionGroupArgumentsMap)
    throws SQWRLException, BuiltInException
  {
    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody(SQWRLNames.getCollectionMakeBuiltInNames())) {
      String collectionName = builtInAtom.getArgumentVariableName(0); // First argument is the collection name
       
      if (!collectionNames.contains(collectionName)) throw new SQWRLException("groupBy applied to undefined collection ?" + collectionName);
      
      if (collectionGroupArgumentsMap.containsKey(collectionName))  
    		builtInAtom.addArguments(collectionGroupArgumentsMap.get(collectionName)); // Append each collections's group arguments to make built-in.     
    } // for
  } 

  private void processSQWRLCollectionOperationBuiltIns(Set<String> collectionNames, Map<String, List<BuiltInArgument>> collectionGroupArgumentsMap,
  																										 Set<String> cascadedUnboundVariableNames) 
  throws SQWRLException, BuiltInException
  {
  	for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody(SQWRLNames.getCollectionOperationBuiltInNames())) {
  		List<BuiltInArgument> allOperandCollectionGroupArguments = new ArrayList<BuiltInArgument>(); // The group arguments from the operand collections
  		List<String> variableNames;
  		
  		builtInAtom.setUsesSQWRLCollectionResults();

  		if (builtInAtom.hasUnboundArguments()) { // Keep track of built-in's unbound arguments so that we can mark dependent built-ins.
  			Set<String> unboundVariableNames = builtInAtom.getUnboundArgumentVariableNames();
  			cascadedUnboundVariableNames.addAll(unboundVariableNames);
  		} // if
  		
  	  // Append the group arguments to built-ins for each of its collection arguments; also append group arguments
  	  // to collections created by operation built-ins.
  		if (builtInAtom.isSQWRLCollectionCreateOperation()) variableNames = builtInAtom.getArgumentsVariableNamesExceptFirst();
  		else variableNames = builtInAtom.getArgumentsVariableNames();
  		
      for (String variableName : variableNames) {
      	if (collectionNames.contains(variableName) && collectionGroupArgumentsMap.containsKey(variableName)) { // The variable refers to a grouped collection.
      		List<BuiltInArgument> collectionGroupArguments = collectionGroupArgumentsMap.get(variableName); // Get the grouping arguments.
      		builtInAtom.addArguments(collectionGroupArguments); // Append each collections's group arguments to built-in.
      		allOperandCollectionGroupArguments.addAll(collectionGroupArguments);
      	} // if
      } // for
      	
      if (builtInAtom.isSQWRLCollectionCreateOperation()) { // If a collection is created we need to record it and store necessary group arguments. 
      	String createdCollectionName = builtInAtom.getArgumentVariableName(0); // The first argument is the collection name.
      		
      	if (!collectionNames.contains(createdCollectionName)) collectionNames.add(createdCollectionName);
      		
      	if (!allOperandCollectionGroupArguments.isEmpty()) 
      			collectionGroupArgumentsMap.put(createdCollectionName, allOperandCollectionGroupArguments); // Store group arguments from all operand collections.
      } // if
    } // for
  }

  private void processBuiltInsThatUseSQWRLCollectionOperationResults(Set<String> cascadedUnboundVariableNames) 
    throws SQWRLException, BuiltInException
  {
    for (BuiltInAtom builtInAtom : getBuiltInAtomsFromBody()) {
      if (!builtInAtom.isSQWRLBuiltIn()) { // Mark later non SQWRL built-ins that (directly or indirectly) use variables bound by collection operation built-ins.
      	if (builtInAtom.usesAtLeastOneVariableOf(cascadedUnboundVariableNames)) {
      		builtInAtom.setUsesSQWRLCollectionResults(); // Mark this built-in as dependent on collection built-in bindings.
      		if (builtInAtom.hasUnboundArguments())  // Cascade the dependency from this built-in to others using its arguments.
            cascadedUnboundVariableNames.addAll(builtInAtom.getUnboundArgumentVariableNames()); // Record its unbound variables too.
        } // if
      } // if
    } // for
  }
  
  private boolean hasUnboundArgument(List<BuiltInArgument> arguments)
  {
  	for (BuiltInArgument argument : arguments) if (argument.isUnbound()) return true;
  	return false;
  }

  public String getRuleText()
  {
    String result = "";
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
  }
  
  public String toString()
  {
  	return ruleURI;
  }

} // SWRLRuleImpl
