package edu.stanford.smi.protegex.owl.jena.parser;

import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class ParserUtility {
	
    
    
	public static String getResourceName(AResource resource) {
		if (resource.isAnonymous()) {	
		    /*
		     * Argh...  Must ensure that this cannot conflict with owlModel.getNextAnonymousId();
		     *          See the stuff involving the creation of logical named classes in the 
		     *          TripleFrameCache code.  This is *nasty* but I don't yet the better way.
		     */
			return AbstractOWLModel.ANONYMOUS_BASE + "PARSE_" + resource.getAnonymousID();
		} else {
			return resource.getURI();
		}
	}
	
}
