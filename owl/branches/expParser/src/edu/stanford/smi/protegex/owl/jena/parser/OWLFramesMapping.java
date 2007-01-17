package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.HashMap;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Model;

public class OWLFramesMapping {
	//make it object?
	private static HashMap<String, String> owlProp2FramesSlotMap = new HashMap<String, String>();
	
	static {
		owlProp2FramesSlotMap.put(RDFS.subClassOf.getURI(), Model.Slot.DIRECT_SUPERCLASSES);
		owlProp2FramesSlotMap.put(RDF.type.getURI(), Model.Slot.DIRECT_TYPES);
		owlProp2FramesSlotMap.put(RDFS.subPropertyOf.getURI(), Model.Slot.DIRECT_SUPERSLOTS);
	}
	
	public static String getFramesSlotMapName(String propertyName) {
		return owlProp2FramesSlotMap.get(propertyName);
	}

}
