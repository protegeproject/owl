
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLAxiomProcessor;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLBuiltInBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLRuleEngineException;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDeclarationAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDifferentIndividualsAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLOntology;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSameIndividualAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLBuiltInAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLClassAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLLiteralArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLRuleReference;
import edu.stanford.smi.protegex.owl.swrl.portability.p3.P3OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLNames;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.DataValueConversionException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.InvalidQueryNameException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.SQWRLResultImpl;

public class OWLAxiomProcessorImpl implements OWLAxiomProcessor
{
	private OWLOntology activeOntology;
	private OWLDataFactory dataFactory;

	private HashMap<String, SWRLRuleReference> rules, queries;

	private Map<String, Set<String>> relevantOWLClassURIMap, relevantOWLPropertyURIMap, relevantOWLIndividualURIMap;
	private Map<String, Set<String>> referencedVariableNameMap;

	private Map<String, SQWRLResultImpl> sqwrlResultMap;
	private Map<String, String> ruleGroupNameMap;
	private Map<String, Boolean> hasSQWRLBuiltInsMap, hasSQWRLCollectionBuiltInsMap, enabledMap;
	private Map<String, Map<String, List<BuiltInArgument>>> collectionGroupArgumentsMap;

	private HashMap<String, SWRLRuleReference> swrlRules;
	private HashMap<String, OWLDeclarationAxiomReference> relevantOWLDeclarationAxioms;
	private HashMap<String, OWLDeclarationAxiomReference> relevantOWLClassDeclarationAxioms;
	private HashMap<String, OWLDeclarationAxiomReference> relevantOWLPropertyDeclarationAxioms;
	private HashMap<String, OWLDeclarationAxiomReference> relevantOWLIndividualDeclarationAxioms;
	private Set<String> relevantOWLObjectPropertyURIs, relevantOWLDataPropertyURIs;
	private Set<OWLAxiomReference> relevantOWLAxioms;

	// All entities
	private Map<String, Map<String, Set<OWLPropertyAssertionAxiomReference>>> allOWLPropertyAssertionAxioms; // individualURI <propertyURI, axiom>
	private Map<String, OWLNamedIndividualReference> allOWLIndividuals;

	public OWLAxiomProcessorImpl(OWLOntology activeOntology)
	{
		this.activeOntology = activeOntology;
		this.dataFactory = new P3OWLDataFactory(activeOntology);
		reset();
	}

	public void reset()
	{
		rules = new HashMap<String, SWRLRuleReference>();
		queries = new HashMap<String, SWRLRuleReference>();

		relevantOWLClassURIMap = new HashMap<String, Set<String>>();
		relevantOWLPropertyURIMap = new HashMap<String, Set<String>>();
		relevantOWLIndividualURIMap = new HashMap<String, Set<String>>();
		referencedVariableNameMap = new HashMap<String, Set<String>>();

		sqwrlResultMap = new HashMap<String, SQWRLResultImpl>();
		ruleGroupNameMap = new HashMap<String, String>();

		hasSQWRLBuiltInsMap = new HashMap<String, Boolean>();
		hasSQWRLCollectionBuiltInsMap = new HashMap<String, Boolean>();
		enabledMap = new HashMap<String, Boolean>();

		collectionGroupArgumentsMap = new HashMap<String, Map<String, List<BuiltInArgument>>>();

		swrlRules = new HashMap<String, SWRLRuleReference>();

		relevantOWLDeclarationAxioms = new HashMap<String, OWLDeclarationAxiomReference>();
		relevantOWLClassDeclarationAxioms = new HashMap<String, OWLDeclarationAxiomReference>();
		relevantOWLPropertyDeclarationAxioms = new HashMap<String, OWLDeclarationAxiomReference>();
		relevantOWLIndividualDeclarationAxioms = new HashMap<String, OWLDeclarationAxiomReference>();

		relevantOWLAxioms = new HashSet<OWLAxiomReference>();
		relevantOWLObjectPropertyURIs = new HashSet<String>();
		relevantOWLDataPropertyURIs = new HashSet<String>();
		relevantOWLAxioms = new HashSet<OWLAxiomReference>();

		allOWLPropertyAssertionAxioms = new HashMap<String, Map<String, Set<OWLPropertyAssertionAxiomReference>>>();
		allOWLIndividuals = new HashMap<String, OWLNamedIndividualReference>();
	}

	public void processSWRLRules() throws SWRLRuleEngineException
	{
		reset();
		importSWRLRules();
		importReferencedOWLKnowledge();
	}

	public void processSQWRLQuery(String queryName) throws SWRLRuleEngineException
	{
		reset();
		importSQWRLQuery(queryName);
		importReferencedOWLKnowledge();
	}

	private void importSWRLRuleOrSQWRLQuery(SWRLRuleReference ruleOrQuery) throws BuiltInException
	{
		for (SWRLAtomReference atom : ruleOrQuery.getBodyAtoms())
			processSWRLAtom(ruleOrQuery, atom, false);
		for (SWRLAtomReference atom : ruleOrQuery.getHeadAtoms())
			processSWRLAtom(ruleOrQuery, atom, true);

		buildReferencedVariableNames(ruleOrQuery);
		processUnboundBuiltInArguments(ruleOrQuery);
		processSQWRLBuiltIns(ruleOrQuery);
		processBuiltInArgumentDependencies(ruleOrQuery);

		if (isSQWRLQuery(ruleOrQuery))
			queries.put(ruleOrQuery.getURI(), ruleOrQuery);

		rules.put(ruleOrQuery.getURI(), ruleOrQuery);
	}

	public void importReferencedOWLAxioms() throws SWRLRuleEngineException
	{
		importReferencedOWLKnowledge();
	}

	public SWRLRuleReference getSQWRLQuery(String queryURI) throws SQWRLException
	{
		if (!queries.containsKey(queryURI))
			throw new SQWRLException("invalid query name " + queryURI);

		return queries.get(queryURI);
	}

	public SWRLRuleReference getSWRLRule(String ruleURI) throws SWRLRuleEngineException
	{
		if (!swrlRules.containsKey(ruleURI))
			throw new SWRLRuleEngineException("invalid rule name " + ruleURI);

		return swrlRules.get(ruleURI);
	}

	public int getNumberOfReferencedSWRLRules()
	{
		return swrlRules.values().size();
	}

	public int getNumberOfReferencedOWLDeclarationAxioms()
	{
		return relevantOWLDeclarationAxioms.values().size();
	}

	public int getNumberOfReferencedOWLClassDeclarationAxioms()
	{
		return relevantOWLClassDeclarationAxioms.values().size();
	}

	public int getNumberOfReferencedOWLPropertyDeclarationAxioms()
	{
		return relevantOWLPropertyDeclarationAxioms.values().size();
	}

	public int getNumberOfReferencedOWLIndividualDeclarationAxioms()
	{
		return relevantOWLIndividualDeclarationAxioms.values().size();
	}

	public int getNumberOfReferencedOWLAxioms()
	{
		return relevantOWLAxioms.size();
	}

	public Set<SWRLRuleReference> getSWRLRules()
	{
		return new HashSet<SWRLRuleReference>(swrlRules.values());
	}

	public Set<OWLDeclarationAxiomReference> getRelevantOWLDeclarationAxioms()
	{
		return new HashSet<OWLDeclarationAxiomReference>(relevantOWLDeclarationAxioms.values());
	}

	public Set<OWLDeclarationAxiomReference> getRelevantOWLClassDeclarationsAxioms()
	{
		return new HashSet<OWLDeclarationAxiomReference>(relevantOWLClassDeclarationAxioms.values());
	}

	public Set<OWLDeclarationAxiomReference> getRelevantOWLPropertyDeclarationAxioms()
	{
		return new HashSet<OWLDeclarationAxiomReference>(relevantOWLPropertyDeclarationAxioms.values());
	}

	public Set<OWLDeclarationAxiomReference> getRelevantOWLIndividualDeclarationAxioms()
	{
		return new HashSet<OWLDeclarationAxiomReference>(relevantOWLIndividualDeclarationAxioms.values());
	}

	public Set<OWLAxiomReference> getRelevantOWLAxioms()
	{
		return new HashSet<OWLAxiomReference>(relevantOWLAxioms);
	}

	public boolean isRelevantOWLClass(String uri)
	{
		return relevantOWLClassDeclarationAxioms.containsKey(uri);
	}

	public boolean isRelevantOWLIndividual(String uri)
	{
		return relevantOWLIndividualDeclarationAxioms.containsKey(uri);
	}

	public boolean isRelevantOWLObjectProperty(String uri)
	{
		return relevantOWLObjectPropertyURIs.contains(uri);
	}

	public boolean isRelevantOWLDataProperty(String uri)
	{
		return relevantOWLDataPropertyURIs.contains(uri);
	}

	public Set<OWLNamedIndividualReference> getAllOWLIndividuals()
	{
		return new HashSet<OWLNamedIndividualReference>(allOWLIndividuals.values());
	}

	public boolean isSQWRLQuery(String uri)
	{
		return (hasSQWRLBuiltInsMap.containsKey(uri) && hasSQWRLBuiltInsMap.get(uri))
				|| (hasSQWRLCollectionBuiltInsMap.containsKey(uri) && hasSQWRLCollectionBuiltInsMap.get(uri));
	}

	public boolean usesSQWRLCollections(SWRLRuleReference ruleOrQuery)
	{
		String uri = ruleOrQuery.getURI();

		return hasSQWRLCollectionBuiltInsMap.containsKey(uri) && hasSQWRLCollectionBuiltInsMap.get(uri);
	}

	public String getRuleGroupName(String uri)
	{
		if (ruleGroupNameMap.containsKey(uri))
			return ruleGroupNameMap.get(uri);
		else
			return "";
	}

	public void setRuleGroupName(String uri, String ruleGroupName)
	{
		ruleGroupNameMap.put(uri, ruleGroupName);
		// TODO: set annotation
	}

	public boolean isEnabled(String uri)
	{
		return enabledMap.containsKey(uri) && enabledMap.get(uri);
	}

	public void setEnabled(String uri, boolean isEnabled)
	{
		enabledMap.put(uri, isEnabled);
		// TODO: set annotation
	}

	public Set<String> getRelevantOWLClassURIs()
	{
		Set<String> result = new HashSet<String>();

		for (Set<String> relevantOWLClassURIs : relevantOWLClassURIMap.values())
			result.addAll(relevantOWLClassURIs);

		return result;
	}

	public Set<String> getRelevantOWLPropertyURIs()
	{
		Set<String> result = new HashSet<String>();

		for (Set<String> relevantOWLPropertyURIs : relevantOWLPropertyURIMap.values())
			result.addAll(relevantOWLPropertyURIs);

		return result;
	}

	public void addReferencedIndividualURI(String uri)
	{
		// Use the empty string to index indirectly referenced URIs
		if (relevantOWLIndividualURIMap.containsKey(""))
			relevantOWLIndividualURIMap.get("").add(uri);
		else {
			Set<String> uris = new HashSet<String>();
			uris.add(uri);
			relevantOWLIndividualURIMap.put("", uris);
		}
	}

	public Set<String> getRelevantOWLIndividualURIs()
	{
		Set<String> result = new HashSet<String>();

		for (Set<String> relevantOWLIndividualURIs : relevantOWLIndividualURIMap.values())
			result.addAll(relevantOWLIndividualURIs);

		return result;
	}

	public Set<String> getRelevantOWLClassURIs(SWRLRuleReference ruleOrQuery)
	{
		return relevantOWLClassURIMap.get(ruleOrQuery.getURI());
	}

	public Set<String> getRelevantOWLPropertyURIs(SWRLRuleReference ruleOrQuery)
	{
		return relevantOWLPropertyURIMap.get(ruleOrQuery.getURI());
	}

	public Set<String> getRelevantOWLIndividualURIs(SWRLRuleReference ruleOrQuery)
	{
		return relevantOWLIndividualURIMap.get(ruleOrQuery.getURI());
	}

	/**
	 * Get the results from a previously executed SQWRL query.
	 */
	public SQWRLResultImpl getSQWRLResult(String uri) throws SQWRLException
	{
		SQWRLResultImpl result;

		if (!queries.containsKey(uri))
			throw new InvalidQueryNameException(uri);

		result = sqwrlResultMap.get(uri);

		if (!result.isPrepared())
			result.prepared();

		return result;
	}

	/**
	 * Get the results from a SQWRL query.
	 */
	public SQWRLResultImpl getSQWRLUnpreparedResult(String uri) throws SQWRLException
	{
		if (!queries.containsKey(uri))
			throw new InvalidQueryNameException(uri);

		return sqwrlResultMap.get(uri);
	}

	public boolean isSQWRLQuery(SWRLRuleReference ruleOrQuery)
	{
		return !getBuiltInAtomsFromHead(ruleOrQuery, SQWRLNames.getSQWRLBuiltInNames()).isEmpty()
				|| !getBuiltInAtomsFromBody(ruleOrQuery, SQWRLNames.getSQWRLBuiltInNames()).isEmpty();
	}

	public boolean hasSQWRLCollectionBuiltIns(SWRLRuleReference ruleOrQuery)
	{
		return !getBuiltInAtomsFromBody(ruleOrQuery, SQWRLNames.getCollectionMakeBuiltInNames()).isEmpty();
	}

	public List<SWRLAtomReference> getSQWRLPhase1BodyAtoms(SWRLRuleReference query)
	{
		List<SWRLAtomReference> result = new ArrayList<SWRLAtomReference>();

		for (SWRLAtomReference atom : query.getBodyAtoms()) {
			if (atom instanceof SWRLBuiltInAtomReference) {
				SWRLBuiltInAtomReference builtInAtom = (SWRLBuiltInAtomReference)atom;
				if (builtInAtom.usesSQWRLCollectionResults() || builtInAtom.isSQWRLGroupCollection())
					continue;
			}
			result.add(atom);
		}

		return result;
	}

	public List<SWRLAtomReference> getSQWRLPhase2BodyAtoms(SWRLRuleReference query)
	{
		List<SWRLAtomReference> result = new ArrayList<SWRLAtomReference>();

		for (SWRLAtomReference atom : query.getBodyAtoms()) {
			if (atom instanceof SWRLBuiltInAtomReference) {
				SWRLBuiltInAtomReference builtInAtom = (SWRLBuiltInAtomReference)atom;
				if (builtInAtom.isSQWRLMakeCollection() || builtInAtom.isSQWRLGroupCollection())
					continue;
			}
			result.add(atom);
		}

		return result;
	}

	public Set<SWRLRuleReference> getSQWRLQueries() throws SQWRLException
	{
		Set<SWRLRuleReference> sqwrlQueries = new HashSet<SWRLRuleReference>();

		try {
			for (SWRLRuleReference ruleOrQuery : dataFactory.getSWRLRules())
				if (isSQWRLQuery(ruleOrQuery))
					sqwrlQueries.add(ruleOrQuery);
		} catch (OWLFactoryException e) {
			throw new SQWRLException("factory error importing SQWRL queries: " + e.getMessage());
		} // try

		return sqwrlQueries;
	}

	public Set<String> getSQWRLQueryNames() throws SQWRLException
	{
		Set<String> sqwrlQueryNames = new HashSet<String>();

		try {
			for (SWRLRuleReference ruleOrQuery : dataFactory.getSWRLRules())
				if (isSQWRLQuery(ruleOrQuery))
					sqwrlQueryNames.add(ruleOrQuery.getURI());
		} catch (OWLFactoryException e) {
			throw new SQWRLException("factory error importing SQWRL queries: " + e.getMessage());
		} // try

		return sqwrlQueryNames;
	}

	private void importSWRLRules() throws SWRLRuleEngineException
	{
		try {
			for (SWRLRuleReference rule : dataFactory.getSWRLRules())
				if (!isSQWRLQuery(rule))
					importSWRLRule(rule); // Ignore SQWRL queries
		} catch (OWLFactoryException e) {
			throw new SWRLRuleEngineBridgeException("factory error importing rules: " + e.getMessage());
		} // try
	}

	private void importSWRLRule(SWRLRuleReference rule) throws SWRLRuleEngineBridgeException
	{
		try {
			swrlRules.put(rule.getURI(), rule);
			importSWRLRuleOrSQWRLQuery(rule);
		} catch (SQWRLException e) {
			throw new SWRLRuleEngineBridgeException("SQWRL error importing rules: " + e.getMessage());
		} catch (BuiltInException e) {
			throw new SWRLRuleEngineBridgeException("built-in error importing rules: " + e.getMessage());
		} // try
	}

	private void importSQWRLQuery(String queryName) throws SWRLRuleEngineException
	{
		try {
			for (SWRLRuleReference rule : dataFactory.getSWRLRules()) {
				if (isSQWRLQuery(rule) && !rule.getURI().equals(queryName))
					continue; // Ignore SQWRL queries apart from the named one
				importSWRLRule(rule);
			}
		} catch (OWLFactoryException e) {
			throw new SWRLRuleEngineBridgeException("factory error importing rule " + queryName + ": " + e.getMessage());
		} // try
	}

	private void importReferencedOWLKnowledge() throws SWRLRuleEngineBridgeException
	{
		// Import all (directly or indirectly) referenced classes.
		importOWLClassDeclarationAxiomsByName(getRelevantOWLClassURIs());

		// Import property assertion axioms for (directly or indirectly) referenced properties
		importOWLPropertyAssertionAxiomsByName(getRelevantOWLPropertyURIs());

		// Import all directly referenced individuals.
		importOWLIndividualsByName(getRelevantOWLIndividualURIs());

		// Import all individuals that are members of imported classes.
		importAllOWLIndividualsOfClassesByName(getRelevantOWLClassURIs());

		importOWLAxioms(); // Import axioms
	}

	/**
	 * Find all built-in atoms with unbound arguments and tell them which of their arguments are unbound. See <a
	 * href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge#nid88T">here</a> for a discussion of the role of this method.
	 */
	private void processUnboundBuiltInArguments(SWRLRuleReference ruleOrQuery)
	{
		List<SWRLBuiltInAtomReference> bodyBuiltInAtoms = new ArrayList<SWRLBuiltInAtomReference>();
		List<SWRLAtomReference> bodyNonBuiltInAtoms = new ArrayList<SWRLAtomReference>();
		List<SWRLAtomReference> finalBodyAtoms = new ArrayList<SWRLAtomReference>();
		Set<String> variableNamesUsedByNonBuiltInBodyAtoms = new HashSet<String>(); // By definition, these will always be bound.
		Set<String> variableNamesBoundByBuiltIns = new HashSet<String>(); // Names of variables bound by built-ins in this rule

		// Process the body atoms and build up list of (1) built-in body atoms, and (2) the variables used by non-built body in atoms.
		for (SWRLAtomReference atom : ruleOrQuery.getBodyAtoms()) {
			if (atom instanceof SWRLBuiltInAtomReference)
				bodyBuiltInAtoms.add((SWRLBuiltInAtomReference)atom);
			else {
				bodyNonBuiltInAtoms.add(atom);
				variableNamesUsedByNonBuiltInBodyAtoms.addAll(atom.getReferencedVariableNames());
			}
		}

		// Process the body built-in atoms and determine if they bind any of their arguments.
		for (SWRLBuiltInAtomReference builtInAtom : bodyBuiltInAtoms) { // Read through built-in arguments and determine which are unbound.
			for (BuiltInArgument argument : builtInAtom.getArguments()) {
				if (argument.isVariable()) {
					String argumentVariableName = argument.getVariableName();

					// If a variable argument is not used by any non built-in body atom or is not bound by another body built-in atom it will therefore be
					// unbound when this built-in is called. We thus set this built-in argument to unbound. If a built-in binds an argument, all later
					// built-ins (proceeding from left to right) will be passed the bound value of this variable during rule execution.
					if (!variableNamesUsedByNonBuiltInBodyAtoms.contains(argumentVariableName) && !variableNamesBoundByBuiltIns.contains(argumentVariableName)) {
						argument.setUnbound(); // Tell the built-in that it is expected to bind this argument.
						variableNamesBoundByBuiltIns.add(argumentVariableName); // Flag this as a bound variable for later built-ins.
					}
				}
			}
		}
		// If we have built-in atoms, construct a new body with built-in atoms moved to the end of the list. Some rule engines (e.g., Jess)
		// expect variables used as parameters to functions to have been defined before their use in a left to right fashion.
		finalBodyAtoms = processBodyNonBuiltInAtoms(bodyNonBuiltInAtoms);
		ruleOrQuery.setBodyAtoms(finalBodyAtoms);
		finalBodyAtoms.addAll(bodyBuiltInAtoms);
	}

	// For every built-in, record the variables it depends from preceding atoms (directly and indirectly).
	// Should be called after processBuiltInArguments and processSQWRLArguments.
	private void processBuiltInArgumentDependencies(SWRLRuleReference ruleOrQuery) throws BuiltInException
	{
		Map<String, Set<Set<String>>> pathMap = new HashMap<String, Set<Set<String>>>();
		Set<String> rootVariableNames = new HashSet<String>();

		for (SWRLAtomReference atom : ruleOrQuery.getBodyAtoms()) {
			Set<String> thisAtomReferencedVariableNames = new HashSet<String>(atom.getReferencedVariableNames());

			buildPaths(atom, rootVariableNames, pathMap);

			if (atom instanceof SWRLBuiltInAtomReference) {
				SWRLBuiltInAtomReference builtInAtom = (SWRLBuiltInAtomReference)atom;

				if (builtInAtom.isSQWRLGroupCollection())
					continue;
				if (builtInAtom.isSQWRLCollectionOperation())
					break;

				if (builtInAtom.hasReferencedVariables()) {
					Set<String> pathVariableNames = new HashSet<String>();

					for (String rootVariableName : pathMap.keySet()) {
						for (Set<String> path : pathMap.get(rootVariableName)) {
							if (!Collections.disjoint(path, thisAtomReferencedVariableNames)) {
								pathVariableNames.addAll(path);
								pathVariableNames.add(rootVariableName);
							}
						}
					}

					if (!pathVariableNames.isEmpty()) {
						pathVariableNames.removeAll(thisAtomReferencedVariableNames); // Remove our own variables
						/*
						 * TODO: Need to think about correct operation of this if (builtInAtom.isSQWRLMakeCollection()) { String collectionName =
						 * builtInAtom.getArgumentVariableName(0); // First argument is the collection name if (collectionGroupArgumentsMap.containsKey(collectionName)) {
						 * List<BuiltInArgument> groupArguments = collectionGroupArgumentsMap.get(collectionName); Set<String> groupVariableNames =
						 * getVariableNames(groupArguments); if (!groupVariableNames.isEmpty() && !pathVariableNames.containsAll(groupVariableNames)) throw new
						 * BuiltInException("all group arguments must be on path for corresponding collection make"); } // if }
						 */
						builtInAtom.setPathVariableNames(pathVariableNames);
					}
				}
			}
		}
	}

	/**
	 * Incrementally build variable paths up to and including the current atom.
	 * 
	 * Note: Sets of sets in Java require care because of hash code issues. The enclosed set should not be modified or the outer set may return inconsistent
	 * results.
	 */
	private void buildPaths(SWRLAtomReference atom, Set<String> rootVariableNames, Map<String, Set<Set<String>>> pathMap)
	{
		Set<String> currentAtomReferencedVariableNames = atom.getReferencedVariableNames();
		Set<String> matchingRootVariableNames;

		if (currentAtomReferencedVariableNames.size() == 1) { // Make variable a root if we have not yet encountered it
			String variableName = currentAtomReferencedVariableNames.iterator().next();
			if (getMatchingPaths(pathMap, variableName).isEmpty() && !rootVariableNames.contains(variableName)) {
				Set<Set<String>> paths = new HashSet<Set<String>>();
				pathMap.put(variableName, paths);
				rootVariableNames.add(variableName);
			}
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
								if (!Collections.disjoint(path, dependentVariables))
									matchedPaths.add(path);
							for (Set<String> matchedPath : matchedPaths) {
								Set<String> newPath = new HashSet<String>(matchedPath);
								newPath.addAll(dependentVariables);
								paths.remove(matchedPath); // Remove the original matched path for this root's path
								paths.add(Collections.unmodifiableSet(newPath)); // Add the updated path
							}
						}
					} else { // Did not find an existing path for this root that uses these variables - add dependent variables as new path
						Set<Set<String>> paths = pathMap.get(rootVariableName);
						paths.add(Collections.unmodifiableSet(dependentVariables));
					}
				}
			} else { // No known roots referenced by any of the atom's variables
				matchingRootVariableNames = getMatchingRootVariableNames(pathMap, currentAtomReferencedVariableNames);
				if (!matchingRootVariableNames.isEmpty()) {
					// Found existing paths that use the atom's variables - add all the variables (none of which is a root) to each path
					for (String matchingRootVariableName : matchingRootVariableNames) {
						Set<Set<String>> paths = pathMap.get(matchingRootVariableName);
						Set<Set<String>> matchedPaths = new HashSet<Set<String>>();

						for (Set<String> path : paths)
							if (!Collections.disjoint(path, currentAtomReferencedVariableNames))
								matchedPaths.add(path);
						for (Set<String> matchedPath : matchedPaths) { // Add the new variables to the matched path and add it to this root's path
							Set<String> newPath = new HashSet<String>(matchedPath);
							newPath.addAll(currentAtomReferencedVariableNames); // Add all the non-root variable names to this path
							paths.remove(matchedPath); // Remove the original matched path
							paths.add(Collections.unmodifiableSet(newPath)); // Add the updated path
						}
					}
				} else { // No existing paths have variables from this atom - every variable becomes a root and depends on every other root variable
					for (String rootVariableName : currentAtomReferencedVariableNames) {
						Set<Set<String>> paths = new HashSet<Set<String>>();
						Set<String> dependentVariables = new HashSet<String>(currentAtomReferencedVariableNames);
						dependentVariables.remove(rootVariableName); // Remove the root from its own dependent variables
						paths.add(Collections.unmodifiableSet(dependentVariables));
						pathMap.put(rootVariableName, paths);
						rootVariableNames.add(rootVariableName);
					}
				}
			}
		}
	}

	@SuppressWarnings("unused")
	// Used by commented-out group argument checking in processBuiltInArgumentDependencies
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
		}

		return matchingRootVariableNames;
	}

	/**
	 * Build up a list of body class atoms and non class, non built-in atoms.
	 */
	private List<SWRLAtomReference> processBodyNonBuiltInAtoms(List<SWRLAtomReference> bodyNonBuiltInAtoms)
	{
		List<SWRLAtomReference> bodyClassAtoms = new ArrayList<SWRLAtomReference>();
		List<SWRLAtomReference> bodyNonClassNonBuiltInAtoms = new ArrayList<SWRLAtomReference>();
		List<SWRLAtomReference> result = new ArrayList<SWRLAtomReference>();

		for (SWRLAtomReference atom : bodyNonBuiltInAtoms) {
			if (atom instanceof SWRLClassAtomReference)
				bodyClassAtoms.add(atom);
			else
				bodyNonClassNonBuiltInAtoms.add(atom);
		}

		result.addAll(bodyClassAtoms); // We arrange the class atoms first.
		result.addAll(bodyNonClassNonBuiltInAtoms);

		return result;
	}

	// TODO: too long- refactor
	private void processSQWRLHeadBuiltIns(SWRLRuleReference query) throws DataValueConversionException, SQWRLException, BuiltInException
	{
		List<String> selectedVariableNames = new ArrayList<String>();
		SQWRLResultImpl sqwrlResult = sqwrlResultMap.get(query.getURI());

		processBuiltInIndexes(query);

		for (SWRLBuiltInAtomReference builtInAtom : getBuiltInAtomsFromHead(query, SQWRLNames.getHeadBuiltInNames())) {
			String builtInName = builtInAtom.getPredicate();
			hasSQWRLBuiltInsMap.put(query.getURI(), true);

			for (BuiltInArgument argument : builtInAtom.getArguments()) {
				boolean isArgumentAVariable = argument.isVariable();
				String variableName = null, columnName;
				int columnIndex;

				if (SQWRLNames.isSQWRLHeadSelectionBuiltIn(builtInName) || SQWRLNames.isSQWRLHeadAggregationBuiltIn(builtInName)) {
					if (isArgumentAVariable) {
						variableName = argument.getVariableName();
						selectedVariableNames.add(variableName);
					}

					if (builtInName.equalsIgnoreCase(SQWRLNames.Select)) {
						if (isArgumentAVariable)
							columnName = "?" + variableName;
						else
							columnName = "[" + argument + "]";
						sqwrlResult.addColumn(columnName);
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.SelectDistinct)) {
						if (isArgumentAVariable)
							columnName = "?" + variableName;
						else
							columnName = "[" + argument + "]";
						sqwrlResult.addColumn(columnName);
						sqwrlResult.setIsDistinct();
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.Count)) {
						if (isArgumentAVariable)
							columnName = "count(?" + variableName + ")";
						else
							columnName = "[" + argument + "]";
						sqwrlResult.addAggregateColumn(columnName, SQWRLNames.CountAggregateFunction);
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.CountDistinct)) {
						if (isArgumentAVariable)
							columnName = "countDistinct(?" + variableName + ")";
						else
							columnName = "[" + argument + "]";
						sqwrlResult.addAggregateColumn(columnName, SQWRLNames.CountDistinctAggregateFunction);
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.Min)) {
						if (isArgumentAVariable)
							columnName = "min(?" + variableName + ")";
						else
							columnName = "min[" + argument + "]";
						sqwrlResult.addAggregateColumn(columnName, SQWRLNames.MinAggregateFunction);
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.Max)) {
						if (isArgumentAVariable)
							columnName = "max(?" + variableName + ")";
						else
							columnName = "max[" + argument + "]";
						sqwrlResult.addAggregateColumn(columnName, SQWRLNames.MaxAggregateFunction);
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.Sum)) {
						if (isArgumentAVariable)
							columnName = "sum(?" + variableName + ")";
						else
							columnName = "sum[" + argument + "]";
						sqwrlResult.addAggregateColumn(columnName, SQWRLNames.SumAggregateFunction);
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.Median)) {
						if (isArgumentAVariable)
							columnName = "median(?" + variableName + ")";
						else
							columnName = "median[" + argument + "]";
						sqwrlResult.addAggregateColumn(columnName, SQWRLNames.MedianAggregateFunction);
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.Avg)) {
						if (isArgumentAVariable)
							columnName = "avg(?" + variableName + ")";
						else
							columnName = "avg[" + argument + "]";
						sqwrlResult.addAggregateColumn(columnName, SQWRLNames.AvgAggregateFunction);
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.OrderBy)) {
						if (!isArgumentAVariable)
							throw new SQWRLException("only variables allowed for ordered columns - found " + argument);
						columnIndex = selectedVariableNames.indexOf(variableName);
						if (columnIndex != -1)
							sqwrlResult.addOrderByColumn(columnIndex, true);
						else
							throw new SQWRLException("variable ?" + variableName + " must be selected before it can be ordered");
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.OrderByDescending)) {
						if (!isArgumentAVariable)
							throw new SQWRLException("only variables allowed for ordered columns - found " + argument);
						columnIndex = selectedVariableNames.indexOf(variableName);
						if (columnIndex != -1)
							sqwrlResult.addOrderByColumn(columnIndex, false);
						else
							throw new SQWRLException("variable ?" + variableName + " must be selected before it can be ordered");
					} else if (builtInName.equalsIgnoreCase(SQWRLNames.ColumnNames)) {
						if (argument instanceof SWRLLiteralArgumentReference && ((SWRLLiteralArgumentReference)argument).getLiteral().isString()) {
							SWRLLiteralArgumentReference dataValueArgument = (SWRLLiteralArgumentReference)argument;
							sqwrlResult.addColumnDisplayName(dataValueArgument.getLiteral().getString());
						} else
							throw new SQWRLException("only string literals allowed as column names - found " + argument);
					}
				}
			}

			if (SQWRLNames.isSQWRLHeadSlicingBuiltIn(builtInName)) {
				if (!sqwrlResult.isOrdered() && !builtInName.equals(SQWRLNames.Limit))
					throw new SQWRLException("slicing operator used without an order clause");

				if (builtInName.equalsIgnoreCase(SQWRLNames.Least) || builtInName.equalsIgnoreCase(SQWRLNames.First)) {
					if (!builtInAtom.getArguments().isEmpty())
						throw new SQWRLException("first or least do not accept arguments");
					sqwrlResult.setFirst();
				} else if (builtInName.equalsIgnoreCase(SQWRLNames.NotLeast) || builtInName.equalsIgnoreCase(SQWRLNames.NotFirst)) {
					if (!builtInAtom.getArguments().isEmpty())
						throw new SQWRLException("not first or least do not accept arguments");
					sqwrlResult.setNotFirst();
				} else if (builtInName.equalsIgnoreCase(SQWRLNames.Greatest) || builtInName.equalsIgnoreCase(SQWRLNames.Last)) {
					if (!builtInAtom.getArguments().isEmpty())
						throw new SQWRLException("greatest or last do not accept arguments");
					sqwrlResult.setLast();
				} else if (builtInName.equalsIgnoreCase(SQWRLNames.NotGreatest) || builtInName.equalsIgnoreCase(SQWRLNames.NotLast)) {
					if (!builtInAtom.getArguments().isEmpty())
						throw new SQWRLException("not greatest or last do not accept arguments");
					sqwrlResult.setNotLast();
				} else {
					BuiltInArgument nArgument = builtInAtom.getArguments().get(0);
					int n;

					if (nArgument instanceof SWRLLiteralArgumentReference && ((SWRLLiteralArgumentReference)nArgument).getLiteral().isLong()) {
						n = (int)((SWRLLiteralArgumentReference)nArgument).getLiteral().getLong();
						if (n < 1)
							throw new SQWRLException("nth argument to slicing operator " + builtInName + " must be a positive integer");
					} else
						throw new SQWRLException("expecing integer to slicing operator " + builtInName);

					if (builtInAtom.getArguments().size() == 1) {
						if (builtInName.equalsIgnoreCase(SQWRLNames.Limit))
							sqwrlResult.setLimit(n);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.Nth))
							sqwrlResult.setNth(n);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.NotNth))
							sqwrlResult.setNotNth(n);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.FirstN) || builtInName.equalsIgnoreCase(SQWRLNames.LeastN))
							sqwrlResult.setFirst(n);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.LastN) || builtInName.equalsIgnoreCase(SQWRLNames.GreatestN))
							sqwrlResult.setLast(n);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.NotLastN) || builtInName.equalsIgnoreCase(SQWRLNames.NotGreatestN))
							sqwrlResult.setNotLast(n);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.NotFirstN) || builtInName.equalsIgnoreCase(SQWRLNames.NotLeastN))
							sqwrlResult.setNotFirst(n);
						else
							throw new SQWRLException("unknown slicing operator " + builtInName);
					} else if (builtInAtom.getArguments().size() == 2) {
						BuiltInArgument sliceArgument = builtInAtom.getArguments().get(1);
						int sliceSize;

						if (sliceArgument instanceof SWRLLiteralArgumentReference && ((SWRLLiteralArgumentReference)sliceArgument).getLiteral().isLong()) {
							sliceSize = (int)((SWRLLiteralArgumentReference)sliceArgument).getLiteral().getLong();
							if (sliceSize < 1)
								throw new SQWRLException("slice size argument to slicing operator " + builtInName + " must be a positive integer");
						} else
							throw new SQWRLException("expecing integer to slicing operator " + builtInName);

						if (builtInName.equalsIgnoreCase(SQWRLNames.NthSlice))
							sqwrlResult.setNthSlice(n, sliceSize);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.NotNthSlice))
							sqwrlResult.setNotNthSlice(n, sliceSize);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.NthLastSlice) || builtInName.equalsIgnoreCase(SQWRLNames.NthGreatestSlice))
							sqwrlResult.setNthLastSlice(n, sliceSize);
						else if (builtInName.equalsIgnoreCase(SQWRLNames.NotNthLastSlice) || builtInName.equalsIgnoreCase(SQWRLNames.NotNthGreatestSlice))
							sqwrlResult.setNotNthLastSlice(n, sliceSize);
						else
							throw new SQWRLException("unknown slicing operator " + builtInName);
					} else
						throw new SQWRLException("unknown slicing operator " + builtInName);
				}
			}
		}
	}

	private void processSQWRLBuiltIns(SWRLRuleReference query) throws DataValueConversionException, SQWRLException, BuiltInException
	{
		Set<String> collectionNames = new HashSet<String>();
		Set<String> cascadedUnboundVariableNames = new HashSet<String>();
		SQWRLResultImpl sqwrlResult = new SQWRLResultImpl();
		sqwrlResultMap.put(query.getURI(), sqwrlResult);

		processSQWRLHeadBuiltIns(query);
		processSQWRLCollectionMakeBuiltIns(query, collectionNames); // Find all make collection built-ins
		processSQWRLCollectionGroupByBuiltIns(query, collectionNames); // Find the group arguments for each collection
		processSQWRLCollectionMakeGroupArguments(query, collectionNames); // Add the group arguments to the make built-ins for its collection
		processSQWRLCollectionOperationBuiltIns(query, collectionNames, cascadedUnboundVariableNames);
		processBuiltInsThatUseSQWRLCollectionOperationResults(query, cascadedUnboundVariableNames);

		sqwrlResult.configured();
		sqwrlResult.openRow();

		if (hasSQWRLCollectionBuiltIns(query))
			sqwrlResult.setIsDistinct();
	}

	// Process all make collection built-ins.
	private void processSQWRLCollectionMakeBuiltIns(SWRLRuleReference query, Set<String> collectionNames) throws SQWRLException, BuiltInException
	{
		for (SWRLBuiltInAtomReference builtInAtom : getBuiltInAtomsFromBody(query, SQWRLNames.getCollectionMakeBuiltInNames())) {
			String collectionName = builtInAtom.getArgumentVariableName(0); // First argument is the collection name
			hasSQWRLCollectionBuiltInsMap.put(query.getURI(), true);

			if (!collectionNames.contains(collectionName))
				collectionNames.add(collectionName);
		}
	}

	// We store the group arguments for each collection specified in the make operation; these arguments are later appended to the collection
	// operation built-ins
	private void processSQWRLCollectionGroupByBuiltIns(SWRLRuleReference ruleOrQuery, Set<String> collectionNames) throws SQWRLException, BuiltInException
	{
		for (SWRLBuiltInAtomReference builtInAtom : getBuiltInAtomsFromBody(ruleOrQuery, SQWRLNames.getCollectionGroupByBuiltInNames())) {
			String collectionName = builtInAtom.getArgumentVariableName(0); // The first argument is the collection name.
			List<BuiltInArgument> builtInArguments = builtInAtom.getArguments();
			List<BuiltInArgument> groupArguments = builtInArguments.subList(1, builtInArguments.size());
			String uri = ruleOrQuery.getURI();
			Map<String, List<BuiltInArgument>> collectionGroupArguments;

			hasSQWRLCollectionBuiltInsMap.put(uri, true);

			if (builtInAtom.getArguments().size() < 2)
				throw new SQWRLException("groupBy must have at least two arguments");
			if (!collectionNames.contains(collectionName))
				throw new SQWRLException("groupBy applied to undefined collection ?" + collectionName);
			if (collectionGroupArgumentsMap.containsKey(collectionName))
				throw new SQWRLException("groupBy specified more than once for same collection ?" + collectionName);
			if (hasUnboundArgument(groupArguments))
				throw new SQWRLException("unbound group argument passed to groupBy for collection ?" + collectionName);

			if (collectionGroupArgumentsMap.containsKey(uri))
				collectionGroupArguments = collectionGroupArgumentsMap.get(uri);
			else {
				collectionGroupArguments = new HashMap<String, List<BuiltInArgument>>();
				collectionGroupArgumentsMap.put(uri, collectionGroupArguments);
			}

			collectionGroupArguments.put(collectionName, groupArguments); // Store group arguments.
		}
	}

	private void processSQWRLCollectionMakeGroupArguments(SWRLRuleReference ruleOrQuery, Set<String> collectionNames) throws SQWRLException, BuiltInException
	{
		for (SWRLBuiltInAtomReference builtInAtom : getBuiltInAtomsFromBody(ruleOrQuery, SQWRLNames.getCollectionMakeBuiltInNames())) {
			String collectionName = builtInAtom.getArgumentVariableName(0); // First argument is the collection name
			Map<String, List<BuiltInArgument>> collectionGroupArguments;
			String uri = ruleOrQuery.getURI();

			if (!collectionNames.contains(collectionName))
				throw new SQWRLException("groupBy applied to undefined collection ?" + collectionName);

			if (collectionGroupArgumentsMap.containsKey(uri))
				collectionGroupArguments = collectionGroupArgumentsMap.get(uri);
			else {
				collectionGroupArguments = new HashMap<String, List<BuiltInArgument>>();
				collectionGroupArgumentsMap.put(uri, collectionGroupArguments);
			}

			if (collectionGroupArguments.containsKey(collectionName))
				builtInAtom.addArguments(collectionGroupArguments.get(collectionName)); // Append each collections's group arguments to make built-in.
		}
	}

	private void processSQWRLCollectionOperationBuiltIns(SWRLRuleReference ruleOrQuery, Set<String> collectionNames, Set<String> cascadedUnboundVariableNames)
		throws SQWRLException, BuiltInException
	{
		for (SWRLBuiltInAtomReference builtInAtom : getBuiltInAtomsFromBody(ruleOrQuery, SQWRLNames.getCollectionOperationBuiltInNames())) {
			List<BuiltInArgument> allOperandCollectionGroupArguments = new ArrayList<BuiltInArgument>(); // The group arguments from the operand collections
			Map<String, List<BuiltInArgument>> collectionGroupArguments;
			String uri = ruleOrQuery.getURI();
			List<String> variableNames;

			builtInAtom.setUsesSQWRLCollectionResults();

			if (builtInAtom.hasUnboundArguments()) { // Keep track of built-in's unbound arguments so that we can mark dependent built-ins.
				Set<String> unboundVariableNames = builtInAtom.getUnboundArgumentVariableNames();
				cascadedUnboundVariableNames.addAll(unboundVariableNames);
			}

			// Append the group arguments to built-ins for each of its collection arguments; also append group arguments
			// to collections created by operation built-ins.
			if (builtInAtom.isSQWRLCollectionCreateOperation())
				variableNames = builtInAtom.getArgumentsVariableNamesExceptFirst();
			else
				variableNames = builtInAtom.getArgumentsVariableNames();

			if (collectionGroupArgumentsMap.containsKey(uri))
				collectionGroupArguments = collectionGroupArgumentsMap.get(uri);
			else {
				collectionGroupArguments = new HashMap<String, List<BuiltInArgument>>();
				collectionGroupArgumentsMap.put(uri, collectionGroupArguments);
			}

			for (String variableName : variableNames) {
				if (collectionNames.contains(variableName) && collectionGroupArguments.containsKey(variableName)) { // The variable refers to a grouped collection.
					builtInAtom.addArguments(collectionGroupArguments.get(variableName)); // Append each collections's group arguments to built-in.
					allOperandCollectionGroupArguments.addAll(collectionGroupArguments.get(variableName));
				}
			}

			if (builtInAtom.isSQWRLCollectionCreateOperation()) { // If a collection is created we need to record it and store necessary group arguments.
				String createdCollectionName = builtInAtom.getArgumentVariableName(0); // The first argument is the collection name.

				if (!collectionNames.contains(createdCollectionName))
					collectionNames.add(createdCollectionName);

				if (!allOperandCollectionGroupArguments.isEmpty())
					collectionGroupArguments.put(createdCollectionName, allOperandCollectionGroupArguments); // Store group arguments from all operand collections.
			}
		}
	}

	private void processBuiltInsThatUseSQWRLCollectionOperationResults(SWRLRuleReference ruleOrQuery, Set<String> cascadedUnboundVariableNames)
		throws SQWRLException, BuiltInException
	{
		for (SWRLBuiltInAtomReference builtInAtom : getBuiltInAtomsFromBody(ruleOrQuery)) {
			if (!builtInAtom.isSQWRLBuiltIn()) { // Mark later non SQWRL built-ins that (directly or indirectly) use variables bound by collection operation
																						// built-ins.
				if (builtInAtom.usesAtLeastOneVariableOf(cascadedUnboundVariableNames)) {
					builtInAtom.setUsesSQWRLCollectionResults(); // Mark this built-in as dependent on collection built-in bindings.
					if (builtInAtom.hasUnboundArguments()) // Cascade the dependency from this built-in to others using its arguments.
						cascadedUnboundVariableNames.addAll(builtInAtom.getUnboundArgumentVariableNames()); // Record its unbound variables too.
				}
			}
		}
	}

	private void buildReferencedVariableNames(SWRLRuleReference ruleOrQuery)
	{
		String uri = ruleOrQuery.getURI();

		for (SWRLAtomReference atom : ruleOrQuery.getBodyAtoms())
			if (referencedVariableNameMap.containsKey(uri))
				referencedVariableNameMap.get(uri).addAll(atom.getReferencedVariableNames());
			else
				referencedVariableNameMap.put(uri, new HashSet<String>(atom.getReferencedVariableNames()));
	}

	/**
	 * Give each built-in a unique index proceeding from left to right.
	 */
	private void processBuiltInIndexes(SWRLRuleReference ruleOrQuery)
	{
		int builtInIndex = 0;

		for (SWRLBuiltInAtomReference builtInAtom : getBuiltInAtomsFromBody(ruleOrQuery))
			builtInAtom.setBuiltInIndex(builtInIndex++);
		for (SWRLBuiltInAtomReference builtInAtom : getBuiltInAtomsFromHead(ruleOrQuery))
			builtInAtom.setBuiltInIndex(builtInIndex++);
	}

	private boolean hasUnboundArgument(List<BuiltInArgument> arguments)
	{
		for (BuiltInArgument argument : arguments)
			if (argument.isUnbound())
				return true;
		return false;
	}

	private void processSWRLAtom(SWRLRuleReference ruleOrQuery, SWRLAtomReference atom, boolean isConsequent)
	{
		String uri = ruleOrQuery.getURI();

		if (atom.hasReferencedClasses())
			if (relevantOWLClassURIMap.containsKey(uri))
				relevantOWLClassURIMap.get(uri).addAll(atom.getReferencedClassURIs());
			else
				relevantOWLClassURIMap.put(uri, atom.getReferencedClassURIs());

		if (atom.hasReferencedProperties())
			if (relevantOWLPropertyURIMap.containsKey(uri))
				relevantOWLPropertyURIMap.get(uri).addAll(atom.getReferencedPropertyURIs());
			else
				relevantOWLPropertyURIMap.put(uri, atom.getReferencedPropertyURIs());

		if (atom.hasReferencedIndividuals())
			if (relevantOWLIndividualURIMap.containsKey(uri))
				relevantOWLIndividualURIMap.get(uri).addAll(atom.getReferencedIndividualURIs());
			else
				relevantOWLIndividualURIMap.put(uri, atom.getReferencedIndividualURIs());
	}

	private List<SWRLBuiltInAtomReference> getBuiltInAtoms(List<SWRLAtomReference> atoms, Set<String> builtInNames)
	{
		List<SWRLBuiltInAtomReference> result = new ArrayList<SWRLBuiltInAtomReference>();

		for (SWRLAtomReference atom : atoms) {
			if (atom instanceof SWRLBuiltInAtomReference) {
				SWRLBuiltInAtomReference builtInAtom = (SWRLBuiltInAtomReference)atom;
				if (builtInNames.contains(builtInAtom.getPredicate()))
					result.add(builtInAtom);
			}
		}
		return result;
	}

	private List<SWRLBuiltInAtomReference> getBuiltInAtoms(List<SWRLAtomReference> atoms)
	{
		List<SWRLBuiltInAtomReference> result = new ArrayList<SWRLBuiltInAtomReference>();

		for (SWRLAtomReference atom : atoms)
			if (atom instanceof SWRLBuiltInAtomReference)
				result.add((SWRLBuiltInAtomReference)atom);

		return result;
	}

	public List<SWRLBuiltInAtomReference> getBuiltInAtomsFromHead(SWRLRuleReference ruleOrQuery)
	{
		return getBuiltInAtoms(ruleOrQuery.getHeadAtoms());
	}

	public List<SWRLBuiltInAtomReference> getBuiltInAtomsFromHead(SWRLRuleReference ruleOrQuery, Set<String> builtInNames)
	{
		return getBuiltInAtoms(ruleOrQuery.getHeadAtoms(), builtInNames);
	}

	public List<SWRLBuiltInAtomReference> getBuiltInAtomsFromBody(SWRLRuleReference ruleOrQuery)
	{
		return getBuiltInAtoms(ruleOrQuery.getBodyAtoms());
	}

	public List<SWRLBuiltInAtomReference> getBuiltInAtomsFromBody(SWRLRuleReference ruleOrQuery, Set<String> builtInNames)
	{
		return getBuiltInAtoms(ruleOrQuery.getBodyAtoms(), builtInNames);
	}

	private void importOWLClassDeclarationAxiomsByName(Set<String> classURIs) throws SWRLRuleEngineBridgeException
	{
		for (String classURI : classURIs)
			importOWLClassDeclarationAxiom(classURI);
	}

	private void importOWLClassDeclarationAxioms(Set<OWLClassReference> classes) throws SWRLRuleEngineBridgeException
	{
		for (OWLClassReference owlClass : classes)
			importOWLClassDeclarationAxiom(owlClass.getURI());
	}

	private void importOWLClassDeclarationAxiom(String classURI) throws SWRLRuleEngineBridgeException
	{
		try {
			if (activeOntology.isOWLNamedClass(classURI)) {
				OWLClassReference owlClass = activeOntology.getOWLClass(classURI);

				if (!relevantOWLClassDeclarationAxioms.containsKey(classURI)) {
					OWLDeclarationAxiomReference axiom = dataFactory.getOWLDeclarationAxiom(owlClass);
					relevantOWLDeclarationAxioms.put(classURI, axiom);
					relevantOWLClassDeclarationAxioms.put(classURI, axiom);
					relevantOWLAxioms.add(axiom);
					importOWLClassDeclarationAxioms(owlClass.getSuperClasses());
					importOWLClassDeclarationAxioms(owlClass.getSubClasses());
					importOWLClassDeclarationAxioms(owlClass.getEquivalentClasses());
				}
			}
		} catch (OWLConversionFactoryException e) {
			throw new SWRLRuleEngineBridgeException("error importing owl class " + classURI + ": " + e.getMessage());
		}
	}

	private void importAllOWLIndividualsOfClassesByName(Set<String> classURIs) throws SWRLRuleEngineBridgeException
	{
		for (String classURI : classURIs)
			importAllOWLIndividualsOfClass(classURI);
	}

	private void importAllOWLIndividualsOfClass(String classURI) throws SWRLRuleEngineBridgeException
	{
		try {
			for (OWLNamedIndividualReference individual : activeOntology.getAllOWLIndividualsOfClass(classURI)) {
				importOWLIndividualDeclarationAxiom(individual.getURI());
				importOWLClassAssertionAxiom(classURI, individual.getURI());
			}
		} catch (OWLConversionFactoryException e) {
			throw new SWRLRuleEngineBridgeException("error importing OWL individual declaration axioms: " + e);
		} // try
	}

	private void importOWLClassAssertionAxiom(String classURI, String individualURI) throws SWRLRuleEngineBridgeException
	{
		OWLClassReference cls = dataFactory.getOWLClass(classURI);
		OWLNamedIndividualReference individual = dataFactory.getOWLIndividual(individualURI);
		OWLClassAssertionAxiomReference axiom = dataFactory.getOWLClassAssertionAxiom(individual, cls);
		relevantOWLAxioms.add(axiom);
	}

	private void importOWLPropertyAssertionAxiomsByName(Set<String> propertyURIs) throws SWRLRuleEngineBridgeException
	{
		for (String propertyURI : propertyURIs)
			importOWLPropertyAssertionAxioms(propertyURI);
	}

	private void importOWLPropertyAssertionAxioms(Set<OWLPropertyReference> properties) throws SWRLRuleEngineBridgeException
	{
		for (OWLPropertyReference property : properties)
			importOWLPropertyAssertionAxioms(property.getURI());
	}

	private void importOWLPropertyAssertionAxioms(String propertyURI) throws SWRLRuleEngineBridgeException
	{
		if (!(relevantOWLObjectPropertyURIs.contains(propertyURI) || relevantOWLDataPropertyURIs.contains(propertyURI))) {
			Set<OWLPropertyAssertionAxiomReference> axioms = null;

			try {
				if (!relevantOWLPropertyDeclarationAxioms.containsKey(propertyURI)) {
					if (activeOntology.containsDataPropertyInSignature(propertyURI, true)) {
						OWLDeclarationAxiomReference axiom = dataFactory.getOWLDeclarationAxiom(activeOntology.getOWLDataProperty(propertyURI));
						relevantOWLPropertyDeclarationAxioms.put(propertyURI, axiom);
						relevantOWLDeclarationAxioms.put(propertyURI, axiom);
					} else if (activeOntology.containsObjectPropertyInSignature(propertyURI, true)) {
						OWLDeclarationAxiomReference axiom = dataFactory.getOWLDeclarationAxiom(activeOntology.getOWLObjectProperty(propertyURI));
						relevantOWLPropertyDeclarationAxioms.put(propertyURI, axiom);
						relevantOWLDeclarationAxioms.put(propertyURI, axiom);
					} else
						throw new SWRLRuleEngineBridgeException("referenced property " + propertyURI + " not in active ontology");
				}

				axioms = activeOntology.getOWLPropertyAssertionAxioms(propertyURI);
			} catch (OWLConversionFactoryException e) {
				throw new SWRLBuiltInBridgeException("error importing OWL property assertion axiom for property " + propertyURI + " :" + e);
			} // try

			relevantOWLAxioms.addAll(axioms);

			for (OWLPropertyAssertionAxiomReference axiom : axioms) {
				String subjectURI = axiom.getSubject().getURI();
				OWLPropertyReference property = axiom.getProperty();

				cacheOWLPropertyAssertionAxiom(axiom);

				addReferencedIndividualURI(subjectURI);

				if (axiom instanceof OWLObjectPropertyAssertionAxiomReference) {
					OWLObjectPropertyAssertionAxiomReference objectPropertyAssertionAxiom = (OWLObjectPropertyAssertionAxiomReference)axiom;
					String objectURI = objectPropertyAssertionAxiom.getObject().getURI();
					addReferencedIndividualURI(objectURI);
					relevantOWLObjectPropertyURIs.add(propertyURI);
				} else
					relevantOWLDataPropertyURIs.add(propertyURI);

				importOWLClassDeclarationAxioms(property.getDomainClasses());
				importOWLClassDeclarationAxioms(property.getRangeClasses());

				importOWLPropertyAssertionAxioms(property.getSuperProperties());
				importOWLPropertyAssertionAxioms(property.getEquivalentProperties());
			}
		}
	}

	private void cacheOWLPropertyAssertionAxiom(OWLPropertyAssertionAxiomReference axiom)
	{
		String subjectURI = axiom.getSubject().getURI();
		String propertyURI = axiom.getProperty().getURI();
		Map<String, Set<OWLPropertyAssertionAxiomReference>> propertyAxiomsMap;
		Set<OWLPropertyAssertionAxiomReference> axiomSet;

		if (allOWLPropertyAssertionAxioms.containsKey(subjectURI))
			propertyAxiomsMap = allOWLPropertyAssertionAxioms.get(subjectURI);
		else {
			propertyAxiomsMap = new HashMap<String, Set<OWLPropertyAssertionAxiomReference>>();
			allOWLPropertyAssertionAxioms.put(subjectURI, propertyAxiomsMap);
		}

		if (propertyAxiomsMap.containsKey(propertyURI))
			axiomSet = propertyAxiomsMap.get(propertyURI);
		else {
			axiomSet = new HashSet<OWLPropertyAssertionAxiomReference>();
			propertyAxiomsMap.put(propertyURI, axiomSet);
		}

		axiomSet.add(axiom);
	}

	private void cacheOWLIndividual(OWLNamedIndividualReference owlIndividual)
	{
		String individualURI = owlIndividual.getURI();

		if (!allOWLIndividuals.containsKey(individualURI))
			allOWLIndividuals.put(individualURI, owlIndividual);
	}

	private void cacheOWLIndividuals(Set<OWLNamedIndividualReference> individuals)
	{
		for (OWLNamedIndividualReference individual : individuals)
			cacheOWLIndividual(individual);
	}

	private void importOWLIndividualsByName(Set<String> individualURIs) throws SWRLRuleEngineBridgeException
	{
		for (String individualURI : individualURIs)
			importOWLIndividualDeclarationAxiom(individualURI);
	}

	private void importOWLIndividualDeclarationAxiom(String individualURI) throws SWRLRuleEngineBridgeException
	{
		if (!relevantOWLIndividualDeclarationAxioms.containsKey(individualURI)) {
			OWLNamedIndividualReference owlIndividual = dataFactory.getOWLIndividual(individualURI);
			OWLDeclarationAxiomReference axiom = dataFactory.getOWLDeclarationAxiom(owlIndividual);
			relevantOWLIndividualDeclarationAxioms.put(individualURI, axiom);
			relevantOWLDeclarationAxioms.put(individualURI, axiom);
			cacheOWLIndividual(owlIndividual);
			importOWLClassDeclarationAxioms(owlIndividual.getTypes());
		}
	}

	private void importOWLAxioms() throws SWRLRuleEngineBridgeException
	{
		importOWLClassDescriptions(); // cf. http://www.w3.org/TR/owl-ref, Section 3.1
		importOWLClassAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section 3
		importOWLPropertyAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section 4
		importOWLIndividualAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section 5
		importOWLDataValuedPropertyAxioms(); // cf. http://www.w3.org/TR/owl-ref, Section 6
		importOWLAnnotations(); // cf. http://www.w3.org/TR/owl-ref, Section 7
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.2
	private void importOWLClassAxioms() throws SWRLRuleEngineBridgeException
	{
		importOWLSubClassOfAxioms();
		importOWLEquivalentClassAxioms();
		importOWLDisjointWithAxioms();
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 4
	private void importOWLPropertyAxioms() throws SWRLRuleEngineBridgeException
	{
		// importRDFSchemaPropertyAxioms() - rdfs:subPropertyOf, rdfs:domain, rdfs:range
		importOWLEquivalentPropertyAxioms();
		importOWLInverseOfAxioms();
		importOWLFunctionalPropertyAxioms();
		importOWLInverseFunctionalPropertyAxioms();
		importOWLTransitivePropertyAxioms();
		importOWLSymmetricPropertyAxioms();
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 5
	private void importOWLIndividualAxioms() throws SWRLRuleEngineBridgeException
	{
		importOWLSameIndividualAxioms();
		importOWLDifferentIndividualsAxioms();
		importOWLAllDifferentsAxioms();
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.2
	private void importOWLPropertyRestrictions() throws SWRLRuleEngineBridgeException
	{
		importOWLCardinalityRestrictions();
		importOWLMinCardinalityRestrictions();
		importOWLMaxCardinalityRestrictions();
		importOWLAllValuesFromRestrictions();
		importOWLSomeValuesFromRestrictions();
		importOWLHasValueRestrictions();
	}

	private void importOWLSameIndividualAxioms() throws SWRLRuleEngineBridgeException
	{
		try {
			for (OWLSameIndividualAxiomReference axiom : activeOntology.getSameIndividualAxioms()) {
				relevantOWLAxioms.add(axiom);
				cacheOWLIndividuals(axiom.getIndividuals());
			}
		} catch (OWLConversionFactoryException e) {
			throw new SWRLBuiltInBridgeException("error importing OWL same individuals axioms: " + e);
		}
	}

	private void importOWLDifferentIndividualsAxioms() throws SWRLRuleEngineBridgeException
	{
		try {
			for (OWLDifferentIndividualsAxiomReference axiom : activeOntology.getOWLDifferentIndividualsAxioms()) {
				relevantOWLAxioms.add(axiom);
				cacheOWLIndividuals(axiom.getIndividuals());
			}
		} catch (OWLConversionFactoryException e) {
			throw new SWRLBuiltInBridgeException("error importing OWL different individuals axioms: " + e);
		}
	}

	private void importOWLClassDescriptions() throws SWRLRuleEngineBridgeException
	{
		importOWLClassEnumerationDescriptions(); // 3.1.1
		importOWLPropertyRestrictions(); // 3.1.2
		importOWLIntersectionOfDescriptions(); // 3.1.3.1
		importOWLUnionOfDescriptions(); // 3.1.3.2
		importOWLComplementOfDescriptions(); // 3.1.3.3
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.1
	private void importOWLClassEnumerationDescriptions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.2
	private void importOWLCardinalityRestrictions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.2
	private void importOWLMinCardinalityRestrictions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.2
	private void importOWLMaxCardinalityRestrictions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.2.1.1
	private void importOWLAllValuesFromRestrictions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.2.1.2
	private void importOWLSomeValuesFromRestrictions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.2.1.3
	private void importOWLHasValueRestrictions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.3.1
	private void importOWLIntersectionOfDescriptions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.3.2
	private void importOWLUnionOfDescriptions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 3.1.3.3
	private void importOWLComplementOfDescriptions() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 4.2.1
	private void importOWLEquivalentPropertyAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 4.2.2
	private void importOWLInverseOfAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 4.3.1
	private void importOWLFunctionalPropertyAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 4.3.2
	private void importOWLInverseFunctionalPropertyAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 4.4.1
	private void importOWLTransitivePropertyAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 4.4.2
	private void importOWLSymmetricPropertyAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	private void importOWLSubClassOfAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	private void importOWLEquivalentClassAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	private void importOWLDisjointWithAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	private void importOWLAllDifferentsAxioms() throws SWRLRuleEngineBridgeException
	{
		// importOWLDifferentIndividualsAxioms effectively subsumes this method.
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 6
	private void importOWLDataValuedPropertyAxioms() throws SWRLRuleEngineBridgeException
	{
	}

	// cf. http://www.w3.org/TR/owl-ref, Section 7
	private void importOWLAnnotations() throws SWRLRuleEngineBridgeException
	{
		// owl:versionInfo, rdfs:label, rdfs:comment, rdfs:seeAlso, rdfs:isDefinedBy
	}

}