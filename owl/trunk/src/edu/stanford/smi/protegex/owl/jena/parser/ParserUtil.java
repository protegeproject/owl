package edu.stanford.smi.protegex.owl.jena.parser;

import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class ParserUtil {
	 
    
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

	public static SimpleFrameStore getSimpleFrameStore(KnowledgeBase kb) {
	    return (SimpleFrameStore) ((DefaultKnowledgeBase) kb).getTerminalFrameStore();
	}

	
	public static SimpleFrameStore getSimpleFrameStore(Frame frame) {
	    return getSimpleFrameStore(frame.getKnowledgeBase());
	}
	
}
