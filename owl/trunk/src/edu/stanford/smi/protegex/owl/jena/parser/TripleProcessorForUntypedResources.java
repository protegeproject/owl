package edu.stanford.smi.protegex.owl.jena.parser;


import java.util.logging.Level;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.vocabulary.OWL;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;

class TripleProcessorForUntypedResources extends AbstractStatefulTripleProcessor {

	public TripleProcessorForUntypedResources(TripleProcessor processor) {
		super(processor);
	}


	public void processUndefTriples() {
		processRemainingUndefinedTriples();
	}


	protected void processRemainingUndefinedTriples() {
		for (UndefTriple undefTriple2 : processor.getGlobalParserCache().getUndefTriples()) {
			UndefTriple undefTriple = undefTriple2;
			Object obj = undefTriple.getTripleObj();

			TripleStore undefTripleStore = undefTriple.getTripleStore();

			//special handling of owl:oneOf
			if (ParserUtil.getResourceName(undefTriple.getTriplePred()).equals(OWL.oneOf.getURI())) {
				handleCreationOfOneOf(undefTriple);
			}

			boolean success = false;

			if (obj instanceof AResource) {
				success = processor.processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (AResource) undefTriple.getTripleObj(), undefTripleStore, true);
			} else if (obj instanceof ALiteral) {
				success = processor.processTriple(undefTriple.getTripleSubj(), undefTriple.getTriplePred(), (ALiteral) undefTriple.getTripleObj(), undefTripleStore, true);
			}

			if (success) {
				processor.getGlobalParserCache().removeUndefTriple(undefTriple.getUndef(), undefTriple);
			}
		}
	}



	private void handleCreationOfOneOf(UndefTriple undefTriple) {
		AResource tripleSubj = undefTriple.getTripleSubj();
		TripleStore ts = undefTriple.getTripleStore();
		if (ParserUtil.getResourceName(tripleSubj).equals(undefTriple.getUndef())) {
			try {
				//this means it is an enumeration cls (data range classes are created elsewhere)
				OWLEnumeratedClass cls = new DefaultOWLEnumeratedClass(owlModel, new FrameID(ParserUtil.getResourceName(tripleSubj)));
				FrameCreatorUtility.assertFrameName(ts, cls);
				RDFSNamedClass owlEnumeratedClassClass = owlModel.getSystemFrames().getOwlEnumeratedClassClass();
				FrameCreatorUtility.addInstanceType(cls, owlEnumeratedClassClass, ts);
				FrameCreatorUtility.addOwnSlotValue(cls, owlModel.getRDFTypeProperty(), owlModel.getOWLNamedClassClass(), ts);
			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING, "Error at creating enumeration class", e);
			}
		}

	}


	/**
	 * This method will create untyped resources for all undefined triples.
	 */
	public void createUntypedResources() {
		/*
		 * There might be triples with duplicate undef entities.
		 * Test subject, predicate and object from the triple
		 */
		for (UndefTriple undefTriple2 : processor.getGlobalParserCache().getUndefTriples()) {
			UndefTriple undefTriple = undefTriple2;
			resolveUndefinedTriple(undefTriple);
		}

		processUndefTriples();
		//should call here postProcessor.processPossiblyTypedResources()
	}

	protected void resolveUndefinedTriple(UndefTriple undefTriple) {
		String pred = ParserUtil.getResourceName(undefTriple.getTriplePred());
		resolvUndefinedTriple(undefTriple, pred);

		String subj = ParserUtil.getResourceName(undefTriple.getTripleSubj());
		resolvUndefinedTriple(undefTriple, subj);

		Object objObj = undefTriple.getTripleObj();
		if (objObj instanceof AResource) {
			String obj = ParserUtil.getResourceName((AResource)undefTriple.getTripleObj());
			resolvUndefinedTriple(undefTriple, obj);
		}
	}

	protected void resolvUndefinedTriple(UndefTriple undefTriple, String undef) {
		if (owlModel.getRDFResource(undef) == null) {
			RDFResource resource = createUntypedObject(undefTriple, undef);
			if (resource != null) {
				checkUndefinedResources(undef);
			}
		}
	}


	//TODO: create in the right TS!!
	//if property system -> create class, if not -> create instance
	/**
	 * Creates either a untyped class, property or resource depending on the triple.
	 * @param undefTriple - the undef triple
	 */
	protected RDFResource createUntypedObject(UndefTriple undefTriple, String undef) {
		AResource subject = undefTriple.getTripleSubj();
		AResource predicate = undefTriple.getTriplePred();
		Object object = undefTriple.getTripleObj();

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
		return createUntypedPredicate(owlModel, predicateName);
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
			return createUntypedClassOrResource(property, subjectName);
		}

		//domain is not null
		if (domain.equals(owlModel.getOWLThingClass())) {
			//return owlModel.createRDFUntypedResource(subjectName);
			//it's not clear what the default should be..
			return createUntypedClassOrResource(property, subjectName);
		}

		if (domain.equals(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
			return untypedClassClass.createInstance(subjectName);
		}

		if (domain.isMetaclass()) {
			RDFResource untypedClass = domain.createInstance(subjectName);
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

		if (property.equals(owlModel.getRDFTypeProperty())) {
			// it's a class
			return untypedClassClass.createInstance(objectName);
		}


		/*
		 * Try to create the new resource using the range of the property
		 */
		RDFSClass range = (RDFSClass) property.getPropertyValue(owlModel.getRDFSRangeProperty());

		if (range == null) {
			//return owlModel.createRDFUntypedResource(objectName);
			//it's not clear what the default should be..
			return createUntypedClassOrResource(property, objectName);
		}

		//range is not null
		if (range.equals(owlModel.getOWLThingClass())) {
			//return owlModel.createRDFUntypedResource(objectName);
			//it's not clear what the default should be..
			return createUntypedClassOrResource(property, objectName);
		}

		if (range.equals(((AbstractOWLModel)owlModel).getOWLClassMetaCls())) {
			return untypedClassClass.createInstance(objectName);
		}

		if (range.isMetaclass()) {
			RDFResource untypedClass = range.createInstance(objectName);
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


	protected RDFResource createUntypedClassOrResource(RDFProperty prop, String name) {
		/*
		 * Make a guess: If the property is user defined it is likely that the entity is
		 * an individual. Otherwise, we will guess that it is a class (less class cast exceptions)
		 */

		if (prop.isSystem()) {
			return createUntypedClass(owlModel, name);
		} else {
			return owlModel.createRDFUntypedResource(name);
		}

		/*
		 * More complicated algorithms can be added here.
		 * I hope that this will work for most cases.
		 */
	}

	//should be created in the right ts
	public static RDFProperty createUntypedPredicate(OWLModel owlModel, String predicateName) {
		return (RDFProperty) ((AbstractOWLModel)owlModel).getRDFExternalPropertyClass().createInstance(predicateName);
	}

	public static RDFResource createUntypedClass(OWLModel owlModel, String className) {
		RDFSClass untypedClassClass = ((AbstractOWLModel)owlModel).getRDFExternalClassClass();
		return untypedClassClass.createInstance(className);
	}

	public static RDFResource createUntypedResource(OWLModel owlModel, String name) {
		return owlModel.createRDFUntypedResource(name);
	}


}
