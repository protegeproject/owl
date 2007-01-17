package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class UndefTripleManager {
	HashMap<String, Collection<UndefTriple>> undefTriplesMap = new HashMap<String, Collection<UndefTriple>>();

	public void addUndefTriple(UndefTriple triple) {
		//System.out.println(" +++ Adding undef triple: " + triple);
		Collection<UndefTriple> undefTriples = getUndefTriples(triple.getUndef());
		undefTriples.add(triple);
		undefTriplesMap.put(triple.getUndef(), undefTriples);
	}

	public Collection<UndefTriple> getUndefTriples(String uri) {
		Collection<UndefTriple> undefTriples = (Collection) undefTriplesMap.get(uri);
		
		if (undefTriples == null)
			return new HashSet<UndefTriple>();
		
		return undefTriples;
	}
	
	public Collection<UndefTriple> getUndefTriples() {
		ArrayList<UndefTriple> values = new ArrayList<UndefTriple>();
		
		for (Iterator iter = undefTriplesMap.keySet().iterator(); iter.hasNext();) {
			String uri = (String) iter.next();			
			values.addAll(undefTriplesMap.get(uri));			
		}
		
		return values;
	}

	public void removeUndefTriple(String uri, UndefTriple undefTriple) {
		//System.out.println(" --- Removing undef triple: " + undefTriple);
		Collection<UndefTriple> undefTriples = getUndefTriples(uri);		
		undefTriples.remove(undefTriple);
		undefTriplesMap.put(uri, undefTriples);
	}
	
	public void dumpUndefTriples() {
		System.out.println("\n --------------- Begin undef triples dump ----------------");
		for (Iterator iter = undefTriplesMap.keySet().iterator(); iter.hasNext();) {
			String uri = (String) iter.next();
			for (Iterator iterator = undefTriplesMap.get(uri).iterator(); iterator.hasNext();) {
				UndefTriple triple = (UndefTriple) iterator.next();
				System.out.println(" * " + triple);
			}			
		}
		System.out.println(" --------------- End undef triples dump ----------------\n");
	}

}
