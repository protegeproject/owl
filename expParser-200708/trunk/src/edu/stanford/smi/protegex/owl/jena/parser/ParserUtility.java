package edu.stanford.smi.protegex.owl.jena.parser;

import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class ParserUtility {
	
	public static String getResourceName(AResource resource) {
		if (resource.isAnonymous()) {			
			return AbstractOWLModel.ANONYMOUS_BASE + resource.getAnonymousID();
		} else {
			return resource.getURI();
		}
	}
	
}
