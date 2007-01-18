package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import com.hp.hpl.jena.graph.GetTriple;
import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.ibm.icu.impl.UBiDiProps;

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


 // TODO: ClassCastException at sortSubclasses
 // TODO: Put owl:Thing as superclass to all classes without superclass
 // TODO: Add not-implemented facets/prop (range)
 // TODO: Create AnnotationProperty class as subclass of AbstractOWLPropertyClass, set isAnnotation to true, and false for the rest
 // TODO: Use getFrame from the NFS
 // TODO: Complete the FrameCreatorUtility.createClassWithTYpe with the rest of type
 // TODO: Try to use reflection to see how fast it is
 // TODO: Try to create Java objects as soon as possible (by using heuristics) 
 // TODO: Try to process remaining undef triples by using heuristics
 

public class TripleFrameCache {
	
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
		
		//FrameCreatorUtility.initOWLModel(this.owlModel);
	}
	
	
	public boolean processTriple(AResource subj, AResource pred, AResource obj, boolean alreadyInUndef) {
		//System.out.println(subj + " " + pred + " " + obj);
		
		String predName = ParserUtility.getResourceName(pred);		
		Slot predSlot = (Slot) owlModel.getFrame(predName);

		if (predSlot == null) {
			if (!alreadyInUndef) {
				undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, predName));
			}
			return false;
		}

		//do some checks if it already exists and is twice defined?
		String subjName = ParserUtility.getResourceName(subj);
		Frame subjFrame = owlModel.getFrame(subjName);
		
		String objName = ParserUtility.getResourceName(obj);
		Frame objFrame = owlModel.getFrame(objName);

		
		//creation
		if (predName.equals(RDF.type.getURI()) ) {
			if (objName.equals(OWL.Restriction.getURI())) {
				return true;
			}
			
			if (subj.isAnonymous() && objName.equals(OWL.Class.getURI())) {
				return true;
			}
			
			if (objFrame == null) {
				addUndefTriple(subj, pred, obj, objName, alreadyInUndef);
				return false;
			}
			
			subjFrame = createFrameWithType(subjName, (Cls)objFrame, subj.isAnonymous());			
		}//split this in two conditions and two methods
		else if (predName.equals(RDF.first.getURI()) || predName.equals(RDF.rest.getURI())) {
			createRDFList(subjName, predName, objName);
		} else	if (OWLFramesMapping.getLogicalPredicatesNames().contains(predName)) {
			subjFrame = createLogicalClass(subjName, predName);
		} else if (OWLFramesMapping.getRestrictionPredicatesNames().contains(predName)) {
			subjFrame = createRestriction(subjName, predName);
		}
		
		//do this nicer
		subjFrame = owlModel.getFrame(subjName);		
		objFrame = owlModel.getFrame(objName);
		
		
		//checking and adding to undefined
		if (subjFrame == null) {
			addUndefTriple(subj, pred, obj, subjName, alreadyInUndef);
			//System.out.println("^^^ Should add undef triple: " + subj + " " + pred + " " + obj + " undef:" + subjName);
			return false;
		}
		
		
		if (objFrame == null) {
			addUndefTriple(subj, pred, obj, objName, alreadyInUndef);
			//System.out.println("^^^ Should add undef triple: " + subj + " " + pred + " " + obj + " undef:" + objName);
			return false;
		}

		//add fillers
		
		//do some try catch?
		
		//add what it is really in the triple
		FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, objFrame);
		//add frame correspondent
		String frameMapSlot = OWLFramesMapping.getFramesSlotMapName(predName);
		if (frameMapSlot != null) {
			FrameCreatorUtility.addOwnSlotValue(subjFrame, owlModel.getSlot(frameMapSlot), objFrame);
		}
		//add frame pair (inverse) correspondent
		String frameMapInvSlot = OWLFramesMapping.getFramesInvSlotMapName(predName);
		if (frameMapInvSlot != null) {
			FrameCreatorUtility.addOwnSlotValue(objFrame, owlModel.getSlot(frameMapInvSlot), subjFrame);
		}
		
		return true;
							
	}



	private void addUndefTriple(AResource subj, AResource pred, AResource obj, String undefName, boolean alreadyInUndef) {
		if (!alreadyInUndef) {
			undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, obj, undefName)); //check this!!
		}		
	}

	public boolean processTriple(AResource subj, AResource pred, ALiteral lit, boolean alreadyInUndef) {
		//System.out.println(subj + " " + pred + " " + lit);
		
		String predName = ParserUtility.getResourceName(pred);		
		Slot predSlot = (Slot) owlModel.getFrame(predName);

		if (predSlot == null) {
			if (!alreadyInUndef) {
				undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, lit, predName));
			}
			return false;
		}

		//do some checks if it already exists and is twice defined?
		String subjName = ParserUtility.getResourceName(subj);
		Frame subjFrame = owlModel.getFrame(subjName);

		//checking and adding to undefined
		if (subjFrame == null) {
			if (!alreadyInUndef) {
				undefTripleManager.addUndefTriple(new UndefTriple(subj, pred, lit, subjName));
			}
			return false;
		}
		
		RDFSLiteral rdfsLiteral = createRDFSLiteral(lit, (RDFProperty) predSlot);
		
		if (rdfsLiteral == null) {
			return false;
		}
		
		//add what it is really in the triple
		FrameCreatorUtility.addOwnSlotValue(subjFrame, predSlot, rdfsLiteral);
		
		return true;
		
	}
	
	
	
	//reimplement this method

	//special treatment of RDFList. Move this to a utility class
	private void createRDFList(String subjName, String predName, String objName) {
		
		Frame subjList = owlModel.getFrame(subjName);
		
		//applies both for rdf:fist and rdf:rest
		if (subjList == null) {
			//move this to a RDFListCreator
			FrameID id = tripleStore.generateFrameID();
			
			Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, subjName, RDF.List.getURI(), true);
			
			if (listFrame != null) {
				checkUndefinedResources(subjName);
			}
		}

		if (!predName.equals(RDF.rest.getURI())) {
			return;
		}
		
		Frame objList = owlModel.getFrame(objName);
		
		if (objList == null) {
			//move this to a RDFListCreator
			FrameID id = tripleStore.generateFrameID();

			Frame listFrame = FrameCreatorUtility.createFrameWithType(owlModel, id, objName, RDF.List.getURI(), true);
			
			if (listFrame != null) {
				checkUndefinedResources(objName);
			}					
		}		
	}

	
		
	private Frame createFrameWithType(String frameUri, Cls type, boolean isSubjAnon) {
	
		Frame frame = owlModel.getFrame(frameUri);
		
		if (frame != null)
			return frame;
					
		FrameID id = tripleStore.getNarrowFrameStore().generateFrameID();

		frame = FrameCreatorUtility.createFrameWithType(owlModel, id, frameUri, type, isSubjAnon);

		if (frame != null) {
			checkUndefinedResources(frameUri);
		}
		
		return frame;
	}
	
	private Frame createRestriction(String restrName, String predName) {
		Frame restriction = owlModel.getFrame(restrName);
		
		if (restriction != null)
			return restriction;
		
		FrameID id = tripleStore.generateFrameID();
		restriction = RestrictionCreatorUtility.createRestriction(owlModel, id, restrName, predName);
		
		if (restriction != null){		
			checkUndefinedResources(restrName);
		}
						
		return restriction;
	}
	

	private Frame createLogicalClass(String logClassName, String predName) {
		Frame logClass = owlModel.getFrame(logClassName);
		
		if (logClass != null)
			return logClass;
		
		FrameID id = tripleStore.generateFrameID();
		logClass = LogicalClassCreatorUtility.createLogicalClass(owlModel, id, logClassName, predName);
		
		if (logClass != null){		
			checkUndefinedResources(logClassName);
		}
						
		return logClass;
	}

	
	

	private void checkUndefinedResources(String uri) {
		Collection undefTriples = undefTripleManager.getUndefTriples(uri); 
		
		for (Iterator iter = undefTriples.iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			
			Object obj = undefTriple.getTripleObj();
			
			boolean success = false;
			
			if (obj instanceof AResource) {			
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (AResource) undefTriple.getTripleObj(), true);
			} else if (obj instanceof ALiteral) {
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (ALiteral) undefTriple.getTripleObj(), true);
			}
						
			if (success) {			
				iter.remove();
				undefTripleManager.removeUndefTriple(uri, undefTriple);
			}
		}
		
	}
	

	public UndefTripleManager getUndefTripleManager() {
		return undefTripleManager;
	}
	


	public void processUndefTriples() {
		for (Iterator iter = getUndefTripleManager().getUndefTriples().iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			
			boolean success = false;
			
			Object obj = undefTriple.getTripleObj();
			
			if (obj instanceof AResource) {			
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (AResource) undefTriple.getTripleObj(), true);
			} else if (obj instanceof ALiteral) {
				success = processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (ALiteral) undefTriple.getTripleObj(), true);
			}		
			
			if (success) {
				getUndefTripleManager().removeUndefTriple(undefTriple.getUndef(), undefTriple);
			}
			
		}
		
	}


	
	//move somewhere
	// copied from old parser
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
	

}
