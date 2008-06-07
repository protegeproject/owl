package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

public class GlobalParserCache {
	Logger log = Log.getLogger(GlobalParserCache.class);
	
	//We need to make sure that there is only one UndefTripleManager per owlModel
	private OWLModel owlModel;
	
	private HashMap<String, Collection<UndefTriple>> undefTriplesMap = new HashMap<String, Collection<UndefTriple>>();
	
	/*
	 * Global caches
	 */
	private SuperClsCache superClsCache = new SuperClsCache();
	private MultipleTypesInstanceCache multipleTypesInstanceCache = new MultipleTypesInstanceCache();
	private Set<String> framesWithWrongJavaType = new HashSet<String>();
	
	//the GCI caches will be refactored
	private Collection<RDFProperty> possibleGCIPredicates = new ArrayList<RDFProperty>();
	private Collection<RDFSClass> gciAxioms = new ArrayList<RDFSClass>();
	private Map<String, Cls> objectToNamedLogicalClassSurrogate = new HashMap<String, Cls>();
	
	private TripleProcessor tripleProcessor;
	
	
	public GlobalParserCache(OWLModel owlModel) {
		this.owlModel = owlModel;
		initGCIPredicates();
	}
	
	public TripleProcessor getTripleProcessor() {
	    if (tripleProcessor == null) {
	        tripleProcessor = new TripleProcessor(owlModel);
	    }
	    return tripleProcessor;
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

	
	/*
	 * Cache methods
	 */
	
	public SuperClsCache getSuperClsCache() {
		return superClsCache;
	}

	public MultipleTypesInstanceCache getMultipleTypesInstanceCache() {
		return multipleTypesInstanceCache;
	}

	public Collection<RDFProperty> getPossibleGCIPredicates() {
		return possibleGCIPredicates;
	}

	public Collection<RDFSClass> getGciAxioms() {
		return gciAxioms;
	}

	public Map<String, Cls> getObjectToNamedLogicalClassSurrogate() {
		return objectToNamedLogicalClassSurrogate;
	}
	
	protected void initGCIPredicates() {
		possibleGCIPredicates.add(owlModel.getOWLDisjointWithProperty());
		possibleGCIPredicates.add(owlModel.getRDFSSubClassOfProperty());
		possibleGCIPredicates.add(owlModel.getOWLEquivalentClassProperty());
	}

	public Set<String> getFramesWithWrongJavaType() {
		return framesWithWrongJavaType;
	}

}
