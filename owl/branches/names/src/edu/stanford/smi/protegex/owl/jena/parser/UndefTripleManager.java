package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.util.Log;

public class UndefTripleManager {
	Logger log = Log.getLogger(UndefTripleManager.class);
	HashMap<String, Collection<UndefTriple>> undefTriplesMap = new HashMap<String, Collection<UndefTriple>>();
	

	public void addUndefTriple(UndefTriple triple) {	
		if (log.isLoggable(Level.FINE)) {
			log.fine(" +++ Adding undef triple: " + triple);
		}
		
		if (undefTriplesMap.keySet().size() % 1000 == 0) {
			Log.getLogger().info("*** Undef triples count: " + undefTriplesMap.keySet().size());
		}
		
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
            if (log.isLoggable(Level.FINE)) {
		log.fine(" --- Removing undef triple: " + undefTriple);
            }
            Collection<UndefTriple> undefTriples = getUndefTriples(uri);		
            undefTriples.remove(undefTriple);
		
            if (undefTriples.isEmpty()) {
                undefTriplesMap.remove(uri);
            } else {
                undefTriplesMap.put(uri, undefTriples);
            }
	}
	
	public void dumpUndefTriples() {
		Log.getLogger().info("\n --------------- Begin undef triples dump ----------------");
		for (Iterator iter = undefTriplesMap.keySet().iterator(); iter.hasNext();) {
			String uri = (String) iter.next();
			for (Iterator iterator = undefTriplesMap.get(uri).iterator(); iterator.hasNext();) {
				UndefTriple triple = (UndefTriple) iterator.next();
				Log.getLogger().info(" * " + triple);
			}			
		}
		Log.getLogger().info(" --------------- End undef triples dump ----------------\n");
	}

}
