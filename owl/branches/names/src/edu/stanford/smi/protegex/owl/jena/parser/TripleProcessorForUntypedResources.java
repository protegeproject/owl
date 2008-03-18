package edu.stanford.smi.protegex.owl.jena.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;

import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class TripleProcessorForUntypedResources extends AbstractStatefulTripleProcessor {
	
	public static final String PROCESS_UNTYPED_RESOURCES = "protegeowl.parser.process.untyped.resources";

	public TripleProcessorForUntypedResources(TripleProcessor processor) {
		super(processor);
	}
		
	
	/**
	 * This method will do the following:
	 * <ol>
	 * <li>Check if there are untyped resources. If yes, check if they are already defined by other triplestore.
	 * If yes, then remove the UntypedResource type and swizzle them.</li>
	 * <li>Go again through all the undefined triples and see if it can resolve them.</li>
	 * <li>For all remaining unresolved triples: add to them the UntypedResource type (maybe future imports will resolve them)
	 * </ol>
	 */
	public void processUndefTriples() {
		if (!processUndefTriplesEnabled()) {
			processRemainingUndefinedTriples();
			return;
		}
		
		boolean modified = true;
		
		while (modified) {
			long intialUndefSize = processor.getUndefTripleManager().getUndefTriples().size();
			
			processRemainingUndefinedTriples();
			checkForUntypedResources();
			createUntypedResources();
			
			modified = processor.getUndefTripleManager().getUndefTriples().size() != intialUndefSize;
		}

		if (processor.getUndefTripleManager().getUndefTriples().size() > 0) {
			processor.getUndefTripleManager().dumpUndefTriples(Level.INFO);
		}
	}
	
	public boolean processUndefTriplesEnabled() {
		return ApplicationProperties.getBooleanProperty(PROCESS_UNTYPED_RESOURCES, true);
	}
	
	
	/**
	 * This method will go through all the untyped resources and see if there are other types defined for them.
	 * If yes, remove the UntypedResource type and swizzle them.
	 */
	@SuppressWarnings("deprecation")
	protected void checkForUntypedResources() {
		RDFSNamedClass untypedResourceClass = owlModel.getRDFUntypedResourcesClass();
		
		for (Iterator iterator = untypedResourceClass.getInstances(true).iterator(); iterator.hasNext();) {
			RDFResource untypedResource = (RDFResource) iterator.next();
			Collection types = new ArrayList(untypedResource.getDirectTypes());
			
			types.remove(untypedResourceClass);
			if (types.size() > 1) {
				untypedResource.removeDirectType(untypedResourceClass); //this will trigger a swizzle
			}
		}
	}
	
	protected void processRemainingUndefinedTriples() {
		for (Iterator<UndefTriple> iter = processor.getUndefTripleManager().getUndefTriples().iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			Object obj = undefTriple.getTripleObj();
			
			boolean success = false;

			if (obj instanceof AResource) {			
				success = processor.processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (AResource) undefTriple.getTripleObj(), true);
			} else if (obj instanceof ALiteral) {
				success = processor.processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (ALiteral) undefTriple.getTripleObj(), true);
			}	

			if (success) {
				processor.getUndefTripleManager().removeUndefTriple(undefTriple.getUndef(), undefTriple);
			}
		}
	}
	
	/**
	 * This method will create untyped resources for all undefined triples.
	 */
	protected void createUntypedResources() {
		for (Iterator<UndefTriple> iter = processor.getUndefTripleManager().getUndefTriples().iterator(); iter.hasNext();) {
			UndefTriple undefTriple = (UndefTriple) iter.next();
			String undefEntity = undefTriple.getUndef();
			
			if (owlModel.getRDFResource(undefEntity) == null) {
				RDFResource resource = createUntypedObject(undefTriple);
				if (resource != null) {
					checkUndefinedResources(undefEntity);				
				}
				//don't drop the undef triple - the processor will hopefully drop it later
			}
		}
	}
	
	/**
	 * Creates either a untyped class, property or resource depending on the triple.
	 * @param undefTriple - the undef triple
	 */
	protected RDFResource createUntypedObject(UndefTriple undefTriple) {
		AResource subject = undefTriple.getTripleSubj();
		AResource predicate = undefTriple.getTriplePred();
		Object object = undefTriple.getTripleObj();
		String undef = undefTriple.getUndef();

		//what to do with resources that already exist?
		RDFResource undefResource = owlModel.getRDFResource(undef); 
		if (undefResource != null) {
			return undefResource;
		}
		
		//The predicate is undefined
		if (undef.equals(ParserUtil.getResourceName(predicate))) {
			return handleUndefinedPredicate(undefTriple);
		}
				
		//The subject is undefined
		if (undef.equals(ParserUtil.getResourceName(subject))) {
			return handleUndefinedSubject(undefTriple);
		}
		
		//The object is undefined
		if (undef.equals(object.toString())) {
			return handleUndefinedObject(undefTriple);
		}
		
		return null;
	}


	protected RDFProperty handleUndefinedPredicate(UndefTriple undefTriple) {		
		String predicateName = ParserUtil.getResourceName(undefTriple.getTriplePred());
		return (RDFProperty) ((AbstractOWLModel)owlModel).getRDFExternalPropertyClass().createInstance(predicateName);
	}
	
	@SuppressWarnings("deprecation")
	protected RDFResource handleUndefinedSubject(UndefTriple undefTriple) {	
		RDFSClass untypedClassClass = ((AbstractOWLModel)owlModel).getRDFExternalClassClass();
		
		/*
		 * Hopefully the predicate is defined. If not, create an untyped predicate (it will be an RDFProperty).
		 * If the predicate will become defined in future, then a new type will be added to it and it will be swizzled.
		 */
		RDFProperty property = owlModel.getRDFProperty(ParserUtil.getResourceName(undefTriple.getTriplePred()));
				
		if (property == null) {
			property = handleUndefinedPredicate(undefTriple);
		}
		
		String subjectName = ParserUtil.getResourceName(undefTriple.getTripleSubj());
		
		/*
		 * Try to create the new resource using the domain of the property
		 */
		RDFSClass domain = (RDFSClass) property.getPropertyValue(owlModel.getRDFSDomainProperty());
	
		if (domain == null) {
			//return owlModel.createRDFUntypedResource(subjectName);
			//it's not clear what the default should be..
			return untypedClassClass.createInstance(subjectName); 
		}
		
		//domain is not null
		if (domain.equals(owlModel.getOWLThingClass())) {
			//return owlModel.createRDFUntypedResource(subjectName);
			//it's not clear what the default should be..
			return untypedClassClass.createInstance(subjectName);
		}
	
		if (domain.equals(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
			return untypedClassClass.createInstance(subjectName);
		}
		
		if (domain.isMetaclass()) {
			RDFResource untypedClass = (RDFResource) domain.createInstance(subjectName);
			//this is fishy - 
			if (domain.hasSuperclass(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
				untypedClass.addDirectType(untypedClassClass);
			} else if (domain.hasSuperclass(owlModel.getRDFPropertyClass()) ) {
				untypedClass.addDirectType(((AbstractOWLModel)owlModel).getRDFExternalPropertyClass());
			}
			return untypedClass;
		} else {
			//domain is not metaclass
			return owlModel.createRDFUntypedResource(subjectName);
		}		
	}

	
	@SuppressWarnings("deprecation")
	protected RDFResource handleUndefinedObject(UndefTriple undefTriple) {	
		RDFSClass untypedClassClass = ((AbstractOWLModel)owlModel).getRDFExternalClassClass();
		
		/*
		 * Hopefully the predicate is defined. If not, create an untyped predicate (it will be an RDFProperty).
		 * If the predicate will become defined in future, then a new type will be added to it and it will be swizzled.
		 */
		RDFProperty property = owlModel.getRDFProperty(ParserUtil.getResourceName(undefTriple.getTriplePred()));
				
		if (property == null) {
			property = handleUndefinedPredicate(undefTriple);
		}
		
		String objectName = undefTriple.getTripleObj().toString();
		
		/*
		 * Try to create the new resource using the range of the property
		 */
		RDFSClass range = (RDFSClass) property.getPropertyValue(owlModel.getRDFSRangeProperty());
	
		if (range == null) {
			//return owlModel.createRDFUntypedResource(objectName);
			//it's not clear what the default should be..
			return untypedClassClass.createInstance(objectName);
		}
		
		//range is not null
		if (range.equals(owlModel.getOWLThingClass())) {
			//return owlModel.createRDFUntypedResource(objectName);
			//it's not clear what the default should be..
			return untypedClassClass.createInstance(objectName);
		}
	
		if (range.equals(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
			return untypedClassClass.createInstance(objectName);
		}
		
		if (range.isMetaclass()) {
			RDFResource untypedClass = (RDFResource) range.createInstance(objectName);
			//this is fishy - 
			if (range.hasSuperclass(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
				untypedClass.addDirectType(untypedClassClass);
			} else if ( range.hasSuperclass(owlModel.getRDFPropertyClass()) ) {
				untypedClass.addDirectType(((AbstractOWLModel)owlModel).getRDFExternalPropertyClass());
			}
			return untypedClass;
		} else {
			//domain is not metaclass
			return owlModel.createRDFUntypedResource(objectName);
		}
		
		//TODO: how to handle dataranges?
		
	}
	
	
	

}
