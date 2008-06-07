package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class OWLImportsCache {
	
	public static HashMap<String, Set<String>> owlOntoloyToImportsMap = new HashMap<String, Set<String>>();
	
	public static Set<String> getOWLImportsURI(String owlOntologyURI) {
		Set<String> imports = owlOntoloyToImportsMap.get(owlOntologyURI);
		
		return (imports == null ? new HashSet<String>() : imports);
	}
	
	public static void addOWLImport(String owlOntologyURI, String importURI) {
		Set<String> imports = getOWLImportsURI(owlOntologyURI);
		
		imports.add(importURI);
		owlOntoloyToImportsMap.put(owlOntologyURI, imports);
	}
	
	

}
