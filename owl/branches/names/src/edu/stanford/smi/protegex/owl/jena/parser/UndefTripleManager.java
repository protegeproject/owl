package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class UndefTripleManager {
	Logger log = Log.getLogger(UndefTripleManager.class);
	
	//We need to make sure that there is only one UndefTripleManager per owlModel
	private OWLModel owlModel;
	
	private HashMap<String, Collection<UndefTriple>> undefTriplesMap = new HashMap<String, Collection<UndefTriple>>();
	
	
	public UndefTripleManager(OWLModel owlModel) {
		this.owlModel = owlModel;
	}

	public void addUndefTriple(UndefTriple triple) {	
		if (log.isLoggable(Level.FINE)) {
			log.fine(" +++ Adding: " + triple);
		}
		
		if (log.isLoggable(Level.FINE)) {			
			if (undefTriplesMap.keySet().size() % 1000 == 0) {
				log.fine(" Undef triples count: " + undefTriplesMap.keySet().size());
			}
		}		
		
		Collection<UndefTriple> undefTriples = getUndefTriples(triple.getUndef());
		undefTriples.add(triple);
		undefTriplesMap.put(triple.getUndef(), undefTriples);
	}

	public Collection<UndefTriple> getUndefTriples(String uri) {
		Collection<UndefTriple> undefTriples = (Collection<UndefTriple>) undefTriplesMap.get(uri);
		
		if (undefTriples == null)
			return new HashSet<UndefTriple>();
		
		return undefTriples;
	}
	
	public Collection<UndefTriple> getUndefTriples() {
		ArrayList<UndefTriple> values = new ArrayList<UndefTriple>();
		
		for (Iterator<String> iter = undefTriplesMap.keySet().iterator(); iter.hasNext();) {
			String uri = (String) iter.next();			
			values.addAll(undefTriplesMap.get(uri));			
		}
		
		return values;
	}

	public void removeUndefTriple(String uri, UndefTriple undefTriple) {
            if (log.isLoggable(Level.FINE)) {
            	log.fine(" --- Removing: " + undefTriple);
            }
            Collection<UndefTriple> undefTriples = getUndefTriples(uri);		
            undefTriples.remove(undefTriple);
		
            if (undefTriples.isEmpty()) {
                undefTriplesMap.remove(uri);
            } else {
                undefTriplesMap.put(uri, undefTriples);
            }
	}
	
	public void dumpUndefTriples(Level level) {
	    if (!log.isLoggable(level)) {
	        return;
	    }
        
		log.log(level, "\n --------------- Begin undef triples dump ----------------");
		for (Iterator<String> iter = undefTriplesMap.keySet().iterator(); iter.hasNext();) {
			String uri = (String) iter.next();
			for (Iterator<UndefTriple> iterator = undefTriplesMap.get(uri).iterator(); iterator.hasNext();) {
				UndefTriple triple = (UndefTriple) iterator.next();
				log.log(level, " * " + triple);
			}			
		}
		log.log(level, " --------------- End undef triples dump ----------------\n");
	}

}
