package edu.stanford.smi.protegex.owl.model.impl;

import java.util.Collection;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;

public class NewDatabaseNamespaceManager extends NewNamespaceManager {
	

	@Override
	public void init(OWLModel owlModel) {
		super.init(owlModel);
		//initializeNamespacesFromDatabase();		
	}
	

	void initializeNamespacesFromDatabase() {
		Slot prefixesSlot = owlModel.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES);
		//check whether this is OK at imports..
		OWLOntology defaultOntology = owlModel.getDefaultOWLOntology();
		
        Collection<String> values = defaultOntology.getDirectOwnSlotValues(prefixesSlot);
        
        for (String value : values) {
            int index = value.indexOf(':');
            if (index > 0) {
                String prefix = value.substring(0, index);
                String namespace = value.substring(index + 1);
                setPrefix(namespace, prefix);
            }			
		}
	}
	
	@Override
	public void update() {
		//TT - I don't like the name of the method
		initializeNamespacesFromDatabase();
	}
	
}
