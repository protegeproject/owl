package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.framestore.SimpleFrameStore;


public class TripleFrameCache {
	private final static HashSet<String> restrictionPredicates = new HashSet<String>();  
	
	static {
		restrictionPredicates.add(OWL.someValuesFrom.getURI());
		restrictionPredicates.add(OWL.allValuesFrom.getURI());
		restrictionPredicates.add(OWL.hasValue.getURI());
		restrictionPredicates.add(OWL.maxCardinality.getURI());
		restrictionPredicates.add(OWL.minCardinality.getURI());
		restrictionPredicates.add(OWL.cardinality.getURI());
	}
	
	private final static HashSet<String> logicalPredicates = new HashSet<String>();
	
	static {
		logicalPredicates.add(OWL.intersectionOf.getURI());
		logicalPredicates.add(OWL.unionOf.getURI());
		logicalPredicates.add(OWL.complementOf.getURI());
		logicalPredicates.add(OWL.oneOf.getURI());
	}
	
	
	UndefTripleManager undefTripleManager = new UndefTripleManager();

	private OWLModel owlModel;
	private TripleStore tripleStore;

	
	public TripleFrameCache(OWLModel owlModel) {
		this(owlModel, owlModel.getTripleStoreModel().getTopTripleStore());
	}

	public TripleFrameCache(OWLModel owlModel, TripleStore tripleStore) {
		this.owlModel = owlModel;
		this.tripleStore = tripleStore;
		
		FrameCreatorUtility.setSimpleFrameStore((SimpleFrameStore)((DefaultKnowledgeBase)owlModel).getTerminalFrameStore());
		
		FrameCreatorUtility.initOWLModel(this.owlModel);
	}
	
	
	public boolean processTriple(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		
		//move this to a common place
		if (pred.getURI().equals(RDF.first.getURI()) || pred.getURI().equals(RDF.rest.getURI())) {
			createRDFList(subj, pred, obj, alreadyInUndef);
		};
		
		if (logicalPredicates.contains(pred.getURI())) {
			return createLogicalClass(subj, pred, obj, alreadyInUndef);
			//remove the isAnonymous condition
		} else if (pred.getURI().equals(RDF.type.getURI()) && !subj.isAnonymous()) {
			return createFrameWithType(subj, pred, obj, alreadyInUndef);			
		} else if (pred.getURI().equals(RDFS.subClassOf.getURI())) {
			return createSubclassOf(subj, pred, obj, alreadyInUndef);
		} else if (pred.getURI().equals(RDFS.subPropertyOf.getURI())) {
			return createSubpropertyOf(subj, pred, obj, alreadyInUndef);
		} else if (restrictionPredicates.contains(pred.getURI())) {
			return createRestriction(subj, pred, obj, alreadyInUndef);
		} else {
			//System.out.println("*** Create directly " + subj + " " + pred + " " + obj + " already in undef: " + alreadyInUndef);
			return addFrameSlotValue(subj, pred, obj, alreadyInUndef);
		}	
		
		
		//return false;		
	}

	public boolean processTriple(AResource subj, AResource pred, ALiteral lit, boolean alreadyInUndef) {
		return createRDFSLiteral(subj, pred, lit, alreadyInUndef);
		
	}
	
	//reimplement this method
	private boolean createRDFSLiteral(AResource subj, AResource pred, ALiteral lit, boolean alreadyInUndef) {
		Frame frame = getFrame(getResourceName(subj), subj, pred, lit, alreadyInUndef);
		
		if (frame == null)
			return false;
		
		Slot prop = (Slot) getFrame(getResourceName(pred), subj, pred, lit, alreadyInUndef);
		
		if (prop == null) {
			return false;
		}
		
		return addFrameSlotValue(subj, pred, lit, alreadyInUndef);
			
	}



	private Frame getFrame(String frameName, AResource subj, AResource pred, ALiteral lit, boolean alreadyInUndef) {
		Frame frame = owlModel.getFrame(frameName);
		if (frame == null) {
			if (!alreadyInUndef) {
				undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, lit, frameName));				
			}
			return null;
		}
		
		return frame;
	}

	//method copied from old parser. Clean it!
	private RDFSLiteral createRDFSLiteral(ALiteral literal, RDFProperty property) {
		if(literal.getLang() != null && literal.getLang().length() > 0) {
			//return owlModel.createRDFSLiteral(literal.toString(), literal.getLang());
			return DefaultRDFSLiteral.create(owlModel, literal.toString(), literal.getLang());
		}
		else if(literal.getDatatypeURI() != null) {
			RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(literal.getDatatypeURI());
			if(datatype == null) {
				//return owlModel.createRDFSLiteral(literal.toString());
				return DefaultRDFSLiteral.create(owlModel, literal.toString());
			}
			else {
				//return owlModel.createRDFSLiteral(literal.toString(), datatype);
				return DefaultRDFSLiteral.create(owlModel, literal.toString(), datatype);
			}
		}
		else {
//			If literal has no datatype, make a qualified guess using the property's range
			RDFResource range = property.getRange();
			if(range instanceof RDFSDatatype) {
				RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(range.getURI());
				//return owlModel.createRDFSLiteral(literal.toString(), datatype);
				return DefaultRDFSLiteral.create(owlModel, literal.toString(), datatype);
			}
			else {
				return DefaultRDFSLiteral.create(owlModel, literal.toString());
				//return owlModel.createRDFSLiteral(literal.toString());
			}
		}
	}

	private boolean createSubpropertyOf(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		Slot superSlot = (Slot) getFrame(getResourceName(obj), subj, pred, obj, alreadyInUndef);
		
		//poate trebe testat daca e deja adaugat
		Slot slot = (Slot) getFrame(getResourceName(subj), subj, pred, obj, (superSlot == null) | alreadyInUndef);
			
		//maybe this can be done in a centralized fashion
		if (FrameCreatorUtility.createSubpropertyOf(slot, superSlot)) {
			//update also the rdfs:subPropertyOf
			return FrameCreatorUtility.addOwnSlotValue(slot, owlModel.getSlot(OWLFramesMapping.getFramesSlotMapName(getResourceName(pred))), superSlot);
		}
		
		return false;
	}

	//special treatment of RDFList. Move this to a utility class
	private void createRDFList(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		Cls rdfListMetaClass = owlModel.getCls(RDFNames.Cls.LIST);
		
		//if (rdfListMetaClass == null)
		
		Frame subjList = owlModel.getFrame(getResourceName(subj));
		
		//applies both for rdf:fist and rdf:rest
		if (subjList == null) {
			//move this to a RDFListCreator
			FrameID id = tripleStore.generateFrameID();
			subjList = new DefaultRDFList(owlModel, id);
			
			FrameCreatorUtility.setFrameName(subjList, getResourceName(subj));
			FrameCreatorUtility.setInstanceType((Instance) subjList, rdfListMetaClass);
			
			checkUndefinedResources(getResourceName(subj));
		}

		if (!pred.getURI().equals(RDF.rest)) {
			return;
		}
		
		Frame objList = owlModel.getFrame(getResourceName(obj));
		
		if (objList == null) {
			//move this to a RDFListCreator
			FrameID id = tripleStore.generateFrameID();
			objList = new DefaultRDFList(owlModel, id);
			
			FrameCreatorUtility.setFrameName(objList, getResourceName(obj));
			FrameCreatorUtility.setInstanceType((Instance) objList, rdfListMetaClass);
			
			checkUndefinedResources(getResourceName(obj));
		}	
		
	}

	private boolean addFrameSlotValue(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		boolean owlPropAddSuccess = false;
		boolean framesSlotAddSuccess = true;
		
		Frame frame = getFrame(getResourceName(subj), subj, pred, obj, alreadyInUndef);
		
		Slot slot = (Slot) getFrame(getResourceName(pred), subj, pred, obj, alreadyInUndef);
		
		Frame value = getFrame(getResourceName(obj), subj, pred, obj, alreadyInUndef);
		
		owlPropAddSuccess = FrameCreatorUtility.addOwnSlotValue(frame, slot, value);
		
		String framesEquivalentSlotName = OWLFramesMapping.getFramesSlotMapName(getResourceName(pred));
			
		if (framesEquivalentSlotName != null) {
			Slot frameSlot = owlModel.getSlot(framesEquivalentSlotName);
			framesSlotAddSuccess = FrameCreatorUtility.addOwnSlotValue(frame, frameSlot, value);
		}
		
		return owlPropAddSuccess & framesSlotAddSuccess;			
	}
	
	
	private boolean addFrameSlotValue(AResource subj, AResource pred, ALiteral lit, boolean alreadyInUndef) {
	
		Frame frame = getFrame(getResourceName(subj), subj, pred, lit, alreadyInUndef);
		
		Slot slot = (Slot) getFrame(getResourceName(pred), subj, pred, lit, alreadyInUndef);
		
		RDFSLiteral literal = createRDFSLiteral(lit, (RDFProperty) slot);
		
		return FrameCreatorUtility.addOwnSlotValue(frame, slot, literal);			
		
	}
	

	private boolean createRestriction(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		FrameID id = tripleStore.generateFrameID();
		Frame restriction = RestrictionCreatorUtility.createRestriction(owlModel, id, getResourceName(subj), getResourceName(pred));
		
		if (restriction == null)
			return false;
		
		checkUndefinedResources(getResourceName(subj));
		
		Frame filler = getFrame(getResourceName(obj), subj, pred, obj, alreadyInUndef);
		
		return RestrictionCreatorUtility.addRestrictionFiller(owlModel, restriction, filler, pred.getURI());
	}


	private boolean createLogicalClass(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		FrameID id = tripleStore.generateFrameID();
		Frame logicalClass = LogicalClassCreatorUtility.createLogicalClass(owlModel, id, getResourceName(subj), getResourceName(pred));
		
		if (logicalClass == null)
			return false;
		
		checkUndefinedResources(getResourceName(subj));
		
		Frame filler = getFrame(getResourceName(obj), subj, pred, obj, alreadyInUndef);
		
		return LogicalClassCreatorUtility.addLogicalFiller(owlModel, logicalClass, filler, pred.getURI());		
	}
	

	private boolean createFrameWithType(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		
		//test for restrictions
		if (obj.getURI().equals(OWL.Restriction.getURI())) {
			//discard the triple for now
			//you could put it into a cache and check at the end whether all the restrictions are well defined
			System.out.println("\tIgnoring type triple for restriction: " + subj +" " + pred + " " + obj);
			return true;
		}	
		
		Frame type = getFrame(getResourceName(obj), subj, pred, obj, alreadyInUndef);
		
		if (type == null)
			return false;
		
		if (!(type instanceof Cls) && !(type instanceof Slot)) {
			Log.getLogger().warning("Attempt to create an individual " + subj.getURI() + " of " + obj.getURI() + " , but the latter is not a class.");
			return false;
		}
		
			
		return createFrameWithType(getResourceName(subj), getResourceName(obj));
	}
		

	private boolean createFrameWithType(String frameUri, String typeUri) {
		Frame frame = owlModel.getFrame(frameUri);
		
		if (frame == null) {					
			FrameID id = tripleStore.getNarrowFrameStore().generateFrameID();
						
			frame = FrameCreatorUtility.createFrameWithType(owlModel, id, frameUri, typeUri);
						
			if (frame != null) {
				checkUndefinedResources(frameUri);
			}
		}
		
		return (frame != null);
	}
		

	private void checkUndefinedResources(String uri) {
		Collection undefTriples = undefTripleManager.getUndefTriples(uri); 
		
		for (Iterator iter = undefTriples.iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			boolean success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), undefTriple.getTripleObj(), true);
			
			//FIXME! concurrent modification exception
			if (success)
				//undefTripleManager.removeUndefTriple(uri, undefTriple);
				iter.remove();
		}
		
	}
	
	private boolean createSubclassOf(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
	
		Cls superCls = (Cls) getFrame(getResourceName(obj), subj, pred, obj, alreadyInUndef);
			
		//poate trebe testat daca e deja adaugat
		Cls cls = (Cls) getFrame(getResourceName(subj), subj, pred, obj, (superCls == null) | alreadyInUndef);
			
		//maybe this can be done in a centralized fashion
		if (FrameCreatorUtility.createSubclassOf(cls, superCls)) {
			//update also the rdfs:SubclassOf
			return FrameCreatorUtility.addOwnSlotValue(cls, owlModel.getSlot(OWLFramesMapping.getFramesSlotMapName(getResourceName(pred))), superCls);
		}
		
		return false;
	}
	

	public UndefTripleManager getUndefTripleManager() {
		return undefTripleManager;
	}
	

	private Frame getFrame(String frameName, AResource subj, AResource pred, AResource obj, boolean alreadyInUndef){
		Frame frame = owlModel.getFrame(frameName);
		if (frame == null) {
			if (!alreadyInUndef) {
				undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, frameName));				
			}
			return null;
		}
		
		return frame;
	}

	public void processUndefTriples() {
		for (Iterator iter = getUndefTripleManager().getUndefTriples().iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			
			boolean success = (undefTriple.getTripleObj() == null) ? processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), undefTriple.getTripleLiteral(), true):
				processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), undefTriple.getTripleObj(), true);
			
			if (success) {
				getUndefTripleManager().removeUndefTriple(undefTriple.getUndef(), undefTriple);
			}
			
		}
		
	}

	//move to some utility class
	public static String getResourceName(AResource resource) {
		if (resource.isAnonymous()) {			
			return AbstractOWLModel.ANONYMOUS_BASE + resource.getAnonymousID();
		} else {
			return resource.getURI();
		}
	}


	

}
