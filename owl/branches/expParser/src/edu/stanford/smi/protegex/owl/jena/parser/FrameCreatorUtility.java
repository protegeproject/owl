package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.Collection;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;

public class FrameCreatorUtility {
	private static SimpleFrameStore simpleFrameStore;
	
	//TODO: for RDFProperties set the rdf:type!! for all!!
	
	public static Frame createFrameWithType(OWLModel owlModel, FrameID id, String frameUri, String typeUri) {
		Frame type = owlModel.getFrame(typeUri);
		
		if (type == null)
			return null;
		
		Frame frame = owlModel.getFrame(frameUri);
		
		if (frame != null)
			return frame;
		
		if (typeUri.equals(OWL.Class.getURI())) {
			frame = new DefaultOWLNamedClass(owlModel, id );				
		} else if (typeUri.equals(RDF.Property)) {
			frame = new DefaultRDFProperty(owlModel, id);
		} else if (typeUri.equals(RDF.List.getURI())) {
			frame = new DefaultRDFList(owlModel, id);
		} else {
			//maybe this is an RDF individual
			frame = new DefaultOWLIndividual(owlModel, id);
		}
		
		setFrameName(frame, frameUri);
		setInstanceType((Instance)frame, (Cls)type);
		
		//maybe move this somewhere
		
		Slot rdfType = owlModel.getSlot(RDF.type.getURI());
		addOwnSlotValue(frame, rdfType, type);
		
		return frame;		
	}
	
	
	public static void setInstanceType(Instance inst, Cls type) {
		if (inst == null || type == null) {
			//Log.getLogger().warning("Error at setting instance type. Instance: " +inst + " type: " + type);
			return;
		}
		simpleFrameStore.addDirectType(inst, type);	
	}

	public static boolean setFrameName(Frame frame, String name) {
		if (frame == null)
			return false;
		
		simpleFrameStore.setFrameName(frame, name);
		
		return true;
	}
	
	public static boolean createSubclassOf(Cls cls, Cls superCls) {
		if (cls == null || superCls == null) {
			//Log.getLogger().warning("Error at creating subclass of relationship. Cls: " + cls + " Superclass: " + superCls);
			return false;
		}
				
		simpleFrameStore.addDirectSuperclass(cls, superCls);
		
		return true;
	}

	public static boolean createSubpropertyOf(Slot slot, Slot superSlot) {
		if (slot == null || superSlot == null) {
			return false;
		}
				
		simpleFrameStore.addDirectSuperslot(slot, superSlot);
		
		return true;
	}
	
	
	public static SimpleFrameStore getSimpleFrameStore() {
		return simpleFrameStore;
	}

	public static void setSimpleFrameStore(SimpleFrameStore simpleFrameStore) {
		FrameCreatorUtility.simpleFrameStore = simpleFrameStore;
	}
	
	
	public static boolean addOwnSlotValue(Frame frame, Slot slot, Object value) {
		if (frame == null || slot == null)
			return false;
						
		FrameCreatorUtility.simpleFrameStore.addDirectOwnSlotValue(frame, slot, value);
		
		return true;
	}
	
	
	//maybe move to another class or delete it when you have a good metamodel or other initialization method
	public static void initOWLModel(OWLModel owlModel) {
		/*
		Slot rdfsSubclassOf = owlModel.getSlot(RDFS.subClassOf.getURI());
		Cls owlThing = owlModel.getCls(OWL.Thing.getURI());
		
		rdfsSubclassOf.setDefaultValues(CollectionUtilities.createCollection(owlThing));
		
		Slot directSuperclassesSlot = owlModel.getSlot(Model.Slot.DIRECT_SUPERCLASSES);
		directSuperclassesSlot.setDefaultValues(CollectionUtilities.createCollection(owlThing));
		*/
		//Cls rdfsResource = owlModel.createCls(RDFS.Resource.getURI(), CollectionUtilities.createCollection(owlModel.getCls(OWLNames.Cls.NAMED_CLASS)));
		
		//Cls owlClass = owlModel.createCls(OWL.Class.getURI(), CollectionUtilities.createCollection(rdfsResource));
		
		//Cls owlThing = owlModel.createCls(OWL.Thing.getURI(), CollectionUtilities.createCollection(owlModel.getRootCls()), owlClass);		
		//Collection owlThingCollection  = CollectionUtilities.createCollection(owlModel.getOWLThingClass());
		
		//Collection slotRootCollection = CollectionUtilities.createCollection(owlModel.getRootSlotMetaCls());
		
		//owlModel.createCls(RDF.Property.getURI(), slotRootCollection);
		
		//owlModel.createCls(OWL.TransitiveProperty.getURI(), slotRootCollection);
		//owlModel.createCls(OWL.ObjectProperty.getURI(), slotRootCollection);
		//owlModel.createCls(OWL.InverseFunctionalProperty.getURI(), slotRootCollection);
		//owlModel.createCls(OWL.FunctionalProperty.getURI(), slotRootCollection);
		
		//Collection restrictionRootCollection = CollectionUtilities.createCollection(owlModel.getCls(OWLNames.Cls.RESTRICTION));
		//owlModel.createCls(OWL.Restriction.getURI(), restrictionRootCollection);
		
		//owlModel.createCls(OWL.Ontology.getURI(), owlThingCollection);
	}



	

}
