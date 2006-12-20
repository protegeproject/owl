package edu.stanford.smi.protegex.owl.jena.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.ARP;
import com.hp.hpl.jena.rdf.arp.ARPHandlers;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.NamespaceHandler;
import com.hp.hpl.jena.rdf.arp.ParseException;
import com.hp.hpl.jena.rdf.arp.StatementHandler;
import com.hp.hpl.jena.rdf.arp.impl.DefaultErrorHandler;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.factory.OWLJavaFactoryUpdater;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLOntology;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFList;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral;
import edu.stanford.smi.protegex.owl.model.patcher.DefaultOWLModelPatcher;
import edu.stanford.smi.protegex.owl.model.patcher.OWLModelPatcher;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.impl.RDFListPostProcessor;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.model.factory.SWRLJavaFactory;
import edu.stanford.smi.protegex.owl.ui.repository.UnresolvedImportUIHandler;

/**
 * An OWL parser that reads an OWL stream triple-by-triple and writes the
 * corresponding Protege-OWL Triples into one or more TripleStores.
 * The current implementation uses the Jena ARP parser for triple loading.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @author Matthew Horridge
 */
public class ProtegeOWLParser {
    private static transient Logger log = Log.getLogger(ProtegeOWLParser.class);
    
    private final static String JENA_ERROR_LEVEL_PROPERTY = "jena.parser.error_level";          

	/**
	 * The default namespace to use if the current file does not define one by itself.
	 * This is typically the file's URL (import URI).
	 */
	private String currentDefaultNamespace;

	private Frame currentType;
	
	private static Collection errors;

	private static URI errorOntologyURI;

	/**
	 * A rather ugly flag that can be activated to prompt the user if a local
	 * file was found as a possible import redirection.
	 */
	public static boolean inUI = false;

	private boolean isRDFList = false;

	private KnowledgeBase kb;

	private ProtegeOWLParserLogger logger;

	private Cls owlNamedClassClass;

	private OWLModel owlModel;

	private OWLModelPatcher patcher;

	private String prefixForDefaultNamespace;

	private RDFProperty rdfFirstProperty;

	private RDFProperty rdfRestProperty;

	private static final String RDFS_RESOURCE_URI = RDFS.Resource.getURI();

	private RDFProperty rdfTypeProperty;

	private TripleStore tripleStore;

	private TripleStoreModel tripleStoreModel;

	private Collection tripleStores = new ArrayList();

	private Map untypedResources = new HashMap();

	private URI2NameConverter uri2NameConverter;

	private static UnresolvedImportHandler unresolvedImportHandler = new UnresolvedImportUIHandler();

	private int tripleCount;

//	private AbstractTask task;


	public ProtegeOWLParser(OWLModel owlModel,
	                        boolean incremental) {
	
		tripleCount = 0;
		errorOntologyURI = null;
		errors = new ArrayList();
		
		this.owlModel = owlModel;
		this.kb = owlModel;
		this.tripleStoreModel = owlModel.getTripleStoreModel();
		this.rdfTypeProperty = owlModel.getRDFTypeProperty();
		
		rdfFirstProperty = owlModel.getRDFFirstProperty();
		rdfRestProperty = owlModel.getRDFRestProperty();
		
		if(incremental) {
			populateUntypedResourcesMap();
		}
		
		logger = createLogger();
		
		owlNamedClassClass = owlModel.getOWLNamedClassClass();
		uri2NameConverter = createURI2NameConverter(owlModel, incremental);
	}


	/**
	 * This method loads an ontology pointed to by the specified URI.
	 *
	 * @param uri The <code>URI</code> that points to the ontology.
	 */
	public void run(final URI uri)
	        throws Exception {
		
		URL url = uri.toURL();		
		ProtegeOWLParser.this.run(getInputStream(url), url.toString());
	}


	/**
	 * This method loads an ontology from the specified input stream using
	 * the specified base uri.
	 *
	 * @param is
	 * @param xmlBase The XML base to use when loading the ontology.  This is typically
	 *                the URI from where the ontology was loaded.
	 * @throws Exception
	 */
	public void run(final InputStream is,
	                final String xmlBase)
	        throws Exception {
		
		run(xmlBase, createARPInvokation(is, xmlBase));
	}


	/**
	 * This method loads an ontology using the specified Reader and using
	 * the specified base uri.  Note that the preferred method of loading an
	 * ontology is to use the run method that takes an <code>InputStream</code>
	 * as a parameter rather than a <code>Reader</code>.
	 *
	 * @param reader  The reader used to read the ontology
	 * @param xmlBase The XML base to use when loading the ontology.  This is typically
	 *                the URI from where the ontology was loaded.
	 * @throws Exception
	 */
	public void run(final Reader reader,
	                final String xmlBase)
	        throws Exception {
		
		run(xmlBase, createARPInvokation(reader, xmlBase));
	}


	protected void run(final String uri,
	                   final ARPInvokation invokation)
	        throws Exception {
		
		errors = new ArrayList();
		errorOntologyURI = null;
		
		loadTriples(owlModel.getTripleStoreModel().getActiveTripleStore(), uri, invokation);
	}


	private ARPInvokation createARPInvokation(final InputStream inputStream,
	                                          final String uri) {
		ARPInvokation invokation = new ARPInvokation() {
			public void invokeARP(ARP arp)
			        throws Exception {
				
				setErrorLevel(arp);
				arp.load(inputStream, uri);
				inputStream.close();
			}
		};
		return invokation;
	}


	private ARPInvokation createARPInvokation(final Reader reader,
	                                          final String uri) {
		ARPInvokation invokation = new ARPInvokation() {
			public void invokeARP(ARP arp)
			        throws Exception {
				
				setErrorLevel(arp);				
				arp.load(reader, uri);					
				reader.close();
			}
		};
		return invokation;
	}

	private void setErrorLevel(ARP arp) {
		String errorLevel = ApplicationProperties.getApplicationOrSystemProperty(ProtegeOWLParser.JENA_ERROR_LEVEL_PROPERTY, "lax");

		if (errorLevel.equalsIgnoreCase("default")) {
			arp.getOptions().setDefaultErrorMode();
		} else if (errorLevel.equalsIgnoreCase("lax")) {
			arp.getOptions().setLaxErrorMode();
		} else if (errorLevel.equalsIgnoreCase("strict")) {
			arp.getOptions().setStrictErrorMode();
		} else {
			arp.getOptions().setLaxErrorMode();
		}				
	}
	

	public void loadTriples(final TripleStore tripleStore,
	                        final String ontologyName,
	                        final ARPInvokation invokation)
	        throws Exception {
				boolean eventsEnabled = owlModel.setGenerateEventsEnabled(false);
                Log.getLogger().info("Loading triples");
                
				Set imports = owlModel.getAllImports();

				// To avoid cyclic imports: mark this as already imported
				addNamespaceToImports(ontologyName, imports);
				currentDefaultNamespace = ontologyName;
				
				ProtegeOWLParser.this.tripleStore = tripleStore;
				tripleStores.addAll(tripleStoreModel.getTripleStores());
				
				ARP arp = createARP();
				
				long startTime = System.currentTimeMillis();
				
				OWLOntology defaultOntology = owlModel.getDefaultOWLOntology();
				if(tripleStore.contains(defaultOntology, owlModel.getRDFTypeProperty(), owlModel.getOWLOntologyClass())) {
					defaultOntology.delete();
				}
				
				errorOntologyURI = URIUtilities.createURI(ontologyName);                        
				
				invokation.invokeARP(arp);
								
				if(owlModel.getRDFResource(":") == null) {
					createDefaultNamespace(tripleStore);
				}
				
				String dns = owlModel.getNamespaceManager().getDefaultNamespace();
				if(dns != null) {
					addNamespaceToImports(dns, imports);
				}
                
				processImports(tripleStore, imports);
				
				long endTime = System.currentTimeMillis();
				
				Log.getLogger().info("[ProtegeOWLParser] Completed triple loading after " + (endTime - startTime) + " ms");
				
				do {
					tripleStoreModel.setActiveTripleStore(tripleStore);
					owlModel.getNamespaceManager().update();
					owlModel.flushCache();
				}
				while(runImplicitImports(imports));
				
				owlModel.setUndoEnabled(false);

				// Assign rdf:List to any untyped lists
				new RDFListPostProcessor(owlModel);
				
				tripleStoreModel.setActiveTripleStore(tripleStore);
				uri2NameConverter.updateInternalState();

				// Replace all temporary names with the final ones
				replaceTemporaryNames();
				owlModel.getNamespaceManager().update();

				// Clean up the temporary mess
				tripleStoreModel.endTripleStoreChanges();
				TripleStoreUtil.updateFrameInclusion(MergingNarrowFrameStore.get(owlModel),
				                                     ((KnowledgeBase) owlModel).getSlot(Model.Slot.NAME));
				// TT:ensure that the triple store of the default ontology has the right name set
				// TODO: This check should not be here! The default ontology should never be null!
				// This is caused by a problem in the namespace code
				if (owlModel.getDefaultOWLOntology() != null)
					tripleStoreModel.getTopTripleStore().setName(owlModel.getDefaultOWLOntology().getURI());
				
				endTime = System.currentTimeMillis();
				
				activateSWRLFactoryIfNecessary(imports);
				
				owlModel.setUndoEnabled(true);
				
				errorOntologyURI = null;
				
				Log.getLogger().info("... Loading completed after " + (System.currentTimeMillis() - startTime) + " ms");
//			};
//		};
//		owlModel.getTaskManager().run(task);
		owlModel.setGenerateEventsEnabled(eventsEnabled);
	}


	private void activateSWRLFactoryIfNecessary(Set imports) {
		if(imports.contains(SWRLNames.SWRL_IMPORT) || imports.contains(SWRLNames.SWRL_ALT_IMPORT)) {
			SWRLJavaFactory factory = new SWRLJavaFactory(owlModel);
			owlModel.setOWLJavaFactory(factory);
			if(owlModel instanceof JenaOWLModel) {
				OWLJavaFactoryUpdater.run((JenaOWLModel) owlModel);
			}
		}
	}


	public static void setUnresolvedImportHandler(UnresolvedImportHandler handler) {
		if(handler != null) {
			ProtegeOWLParser.unresolvedImportHandler = handler;
		}
		else {
			ProtegeOWLParser.unresolvedImportHandler = new DefaultUnresolvedImportHandler();
		}
	}


	private void addNamespaceToImports(String defaultNamespace,
	                                   Set imports) {
		if(Jena.namespaceEndsWithSeparator(defaultNamespace)) {
			imports.add(defaultNamespace.substring(0, defaultNamespace.length() - 1));
		}
		else {
			imports.add(defaultNamespace);
		}
	}


	protected ARP createARP() {
		ARP arp = new ARP();
		ARPHandlers handlers = arp.getHandlers();
		handlers.setStatementHandler(new MyStatementHandler());
		handlers.setErrorHandler(new MyErrorHandler());
		handlers.setNamespaceHandler(new MyNamespaceHandler());
		arp.setHandlersWith(handlers);
		return arp;
	}


	private void createDefaultNamespace(TripleStore tripleStore) {
		Slot prefixesSlot = kb.getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES);
		Instance owlOntology = TripleStoreUtil.getFirstOntology(owlModel, tripleStore);
		String name = owlOntology.getName();
		if(uri2NameConverter.isTemporaryRDFResourceName(name)) {
			String namespace = uri2NameConverter.getURIFromTemporaryName(name);
			namespace = Jena.getNamespaceFromURI(namespace);
			String value = ":" + namespace;
			final List oldValues = owlOntology.getDirectOwnSlotValues(prefixesSlot);
			if(!oldValues.contains(value)) {
				uri2NameConverter.addPrefix(namespace, "");
				for(Iterator it = new ArrayList(oldValues).iterator(); it.hasNext();) {
					String s = (String) it.next();
					if(s.startsWith(":")) {
						owlOntology.removeOwnSlotValue(prefixesSlot, s);
					}
				}
				owlOntology.addOwnSlotValue(prefixesSlot, ":" + namespace);
			}
		}
	}


	private Object createLiteralObject(ALiteral node,
	                                   RDFProperty property) {
		DefaultRDFSLiteral literal = (DefaultRDFSLiteral) createRDFSLiteral(node, property);
		Object plain = literal.getPlainValue();
		if(plain != null) {
			return plain;
		}
		else {
			return literal.getRawValue();
		}
	}


	protected ProtegeOWLParserLogger createLogger() {
		return new DefaultProtegeOWLParserLogger();
	}


	protected OWLModelPatcher createPatcher() {
		return new DefaultOWLModelPatcher(owlModel);
	}


	private RDFSLiteral createRDFSLiteral(ALiteral literal,
	                                      RDFProperty property) {
		if(literal.getLang() != null && literal.getLang().length() > 0) {
			return owlModel.createRDFSLiteral(literal.toString(), literal.getLang());
		}
		else if(literal.getDatatypeURI() != null) {
			RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(literal.getDatatypeURI());
			if(datatype == null) {
				return owlModel.createRDFSLiteral(literal.toString());
			}
			else {
				return owlModel.createRDFSLiteral(literal.toString(), datatype);
			}
		}
		else {
			// If literal has no datatype, make a qualified guess using the property's range
			RDFResource range = property.getRange();
			if(range instanceof RDFSDatatype) {
				RDFSDatatype datatype = owlModel.getRDFSDatatypeByURI(range.getURI());
				return owlModel.createRDFSLiteral(literal.toString(), datatype);
			}
			else {
				return owlModel.createRDFSLiteral(literal.toString());
			}
		}
	}


	private RDFResource createRDFResource(String name) {
		FrameID id = tripleStore.generateFrameID();
		RDFResource r = null;
		if(owlNamedClassClass.equals(currentType)) {
			r = new DefaultOWLNamedClass(owlModel, id);
		}
		else if(isRDFList) {
			r = new DefaultRDFList(owlModel, id);
		}
		else {
			r = new DefaultRDFProperty(owlModel, id);
		}
		if(name == null) {
			name = owlModel.getNextAnonymousResourceName();
		}
		tripleStore.setRDFResourceName(r, name);
		return r;
	}


	protected URI2NameConverter createURI2NameConverter(OWLModel owlModel,
	                                                    boolean incremental) {
		return new DefaultURI2NameConverter(owlModel, logger, incremental);
	}

	
	/**
	 * Gets the ontology name of the most recently parsed file.  This can be used to diagnose
	 * where an exception has occured.	 
	 * @return the error ontology URI or null
	 */	
	public static URI getErrorURI() {
		return errorOntologyURI;
	}

	private RDFResource findResource(String name) {
		for(Iterator it = tripleStores.iterator(); it.hasNext();) {
			TripleStore ts = (TripleStore) it.next();
			RDFResource resource = ts.getHomeResource(name);
			if(resource != null) {
				return resource;
			}
		}
		return null;
	}


	protected String getImplicitImport(String namespace) {
		if(ImplicitImports.isImplicitImport(namespace)) {
			return namespace;
		}
		else {
			return null;
		}
	}


	protected ProtegeOWLParserLogger getLogger() {
		return logger;
	}


	private RDFProperty getRDFProperty(AResource propertyNode) {
		return (RDFProperty) getRDFResource(propertyNode);
	}


	private RDFResource getRDFResource(AResource aResource) {
		final String uri = aResource.isAnonymous() ? aResource.getAnonymousID() : aResource.getURI();
		if(uri.equals(RDFS_RESOURCE_URI)) {
			return owlModel.getOWLThingClass();
		}
		else {
			// Special handling for incremental parsing with external resources
			if(!untypedResources.isEmpty() && !aResource.isAnonymous()) {
				RDFUntypedResource r = (RDFUntypedResource) untypedResources.get(uri);
				if(r != null) {
					return r;
				}
			}
			final String tempName = uri2NameConverter.getTemporaryRDFResourceName(aResource);
			RDFResource existingTemp = findResource(tempName);
			if(existingTemp != null) {
				return existingTemp;
			}
			String name = uri2NameConverter.getRDFResourceName(uri);
			if(name == null) {
				name = tempName;
			}
			RDFResource resource = findResource(name);
			if(resource == null) {
				resource = createRDFResource(name);
			}
			return resource;
		}
	}


	/**
	 * Gets all RDFResources that don't have any rdf:type.
	 *
	 * @return the untyped resources
	 */
	private Collection getUntypedResources() {
		List results = new ArrayList();
		Iterator tripleStores = owlModel.getTripleStoreModel().listUserTripleStores();
		while(tripleStores.hasNext()) {
			TripleStore ts = (TripleStore) tripleStores.next();
			Iterator resources = ts.listHomeResources();
			while(resources.hasNext()) {
				RDFResource resource = (RDFResource) resources.next();
				if(resource.getRDFTypes().isEmpty()) {
					results.add(resource);
				}
			}
		}
		return results;
	}


	private void populateUntypedResourcesMap() {
		RDFSNamedClass cls = owlModel.getRDFUntypedResourcesClass();
		Iterator it = cls.getInstances(false).iterator();
		while(it.hasNext()) {
			RDFUntypedResource r = (RDFUntypedResource) it.next();
			String uri = r.getURI();
			untypedResources.put(uri, r);
		}
	}


	private void replaceNamespace(TripleStore tripleStore,
	                              RDFResource ontology,
	                              String prefix,
	                              String namespace) {
		RDFProperty property = ontology.getOWLModel().getRDFProperty(OWLNames.Slot.ONTOLOGY_PREFIXES);
		Collection values = ontology.getPropertyValues(property);
		for(Iterator it = values.iterator(); it.hasNext();) {
			String value = (String) it.next();
			if(value.startsWith(prefix + ":")) {
				ontology.removePropertyValue(property, value);
			}
		}
		tripleStore.add(ontology, property, prefix + ":" + namespace);
	}


	private void replaceTemporaryNames() {
		TripleStore activeTripleStore = tripleStoreModel.getActiveTripleStore();
		Iterator it = owlModel.getTripleStoreModel().listUserTripleStores();
		while(it.hasNext()) {
			TripleStore tripleStore = (TripleStore) it.next();
			tripleStoreModel.setActiveTripleStore(tripleStore);
			replaceTemporaryNames(tripleStore);
		}
		tripleStoreModel.setActiveTripleStore(activeTripleStore);
	}


	private void replaceTemporaryNames(TripleStore tripleStore) {
		Collection homeResources = Jena.set(tripleStore.listHomeResources());
		OWLNamedClass owlOntologyClass = owlModel.getOWLOntologyClass();
		boolean changed = false;
		for(Iterator it = homeResources.iterator(); it.hasNext();) {
			RDFResource ontology = (RDFResource) it.next();
			//if(ontology.getName().equals("^http://www.daml.org/services/owl-s/1.1/generic/Expression.owl#Condition")) {
			if(ontology.hasRDFType(owlOntologyClass)) {
				String name = ontology.getName();
				if(uri2NameConverter.isTemporaryRDFResourceName(name)) {
					String uri = uri2NameConverter.getURIFromTemporaryName(name);
					uri = Jena.getNamespaceFromURI(uri);
					name = uri2NameConverter.getTemporaryRDFResourceName(uri);
					replaceTemporaryName(tripleStore, ontology, name, true);
					changed = true;
				}
			}
		}
		if(changed) {
			owlModel.flushCache();
			owlModel.getNamespaceManager().update();
			uri2NameConverter.updateInternalState();
		}
		final Slot directTypesSlot = kb.getSlot(Model.Slot.DIRECT_TYPES);
		for(Iterator it = homeResources.iterator(); it.hasNext();) {
			final Instance instance = (Instance) it.next();
			if(!instance.isSystem()) {
				final String name = instance.getName();
				if(instance.getDirectOwnSlotValues(directTypesSlot).isEmpty()) {  // External resource
					instance.getDirectOwnSlotValues(directTypesSlot);
					if(instance instanceof RDFProperty && !tripleStore.getNarrowFrameStore().getFramesWithAnyValue((Slot) instance,
					                                                                                               null, false).isEmpty()) {
						// Convert into rdf:Property if this has been used as any property value
						if(uri2NameConverter.isTemporaryRDFResourceName(name)) {
							replaceTemporaryName(tripleStore, (RDFResource) instance, name, false);
						}
						tripleStore.add((RDFResource) instance, owlModel.getRDFTypeProperty(), owlModel.getRDFPropertyClass());
					}
					else if(tripleStore.listSubjects(owlModel.getRDFSRangeProperty(), instance).hasNext() || tripleStore.listSubjects(
					        owlModel.getRDFSDomainProperty(), instance).hasNext() || tripleStore.listSubjects(
					                owlModel.getRDFSSubClassOfProperty(), instance).hasNext()) {
						if(uri2NameConverter.isTemporaryRDFResourceName(name)) {
							replaceTemporaryName(tripleStore, (RDFResource) instance, name, false);
						}
						tripleStore.add((RDFResource) instance, owlModel.getRDFTypeProperty(), owlModel.getRDFSNamedClassClass());
					}
					else {
						instance.setDirectType(kb.getCls(RDFNames.Cls.EXTERNAL_RESOURCE));
						RDFUntypedResource externalResource = (RDFUntypedResource) kb.getFrame(name);
						if(uri2NameConverter.isTemporaryRDFResourceName(name)) {
							String uri = uri2NameConverter.getURIFromTemporaryName(name);
							owlModel.getOWLFrameStore().setFrameName(externalResource, uri);
						}
						else {
							String uri = owlModel.getURIForResourceName(name);
							if(uri != null && !name.equals(uri)) {
								owlModel.getOWLFrameStore().setFrameName(externalResource, uri);
							}
						}
						// System.out.println("External Resource for " + uri);
					}
				}
				else {
					if(uri2NameConverter.isTemporaryRDFResourceName(name)) {
						replaceTemporaryName(tripleStore, (RDFResource) instance, name, false);
					}
				}
			}
		}
		owlModel.flushCache();
	}


	private void replaceTemporaryName(TripleStore tripleStore,
	                                  RDFResource resource,
	                                  String name,
	                                  boolean isOntology) {
		if(uri2NameConverter.isAnonymousRDFResourceName(name)) {
			String newName = uri2NameConverter.createAnonymousRDFResourceName();
			tripleStore.setRDFResourceName(resource, newName);
		}
		else {
			String uri = uri2NameConverter.getURIFromTemporaryName(name);
			String newName = uri2NameConverter.getRDFResourceName(uri);
			if(newName == null) {
				if(isOntology) {
					uri = Jena.getNamespaceFromURI(uri);
				}
				String prefix = uri2NameConverter.createNewPrefix(uri);
				String namespace = uri2NameConverter.getResourceNamespace(uri);
				newName = uri2NameConverter.getRDFResourceName(uri);
				RDFResource ontology = TripleStoreUtil.getFirstOntology(owlModel, tripleStore);
				replaceNamespace(tripleStore, ontology, prefix, namespace);
				if(newName == null) {
					namespace = uri2NameConverter.getResourceNamespace(uri);
					prefix = uri2NameConverter.createNewPrefix(namespace);
					replaceNamespace(tripleStore, ontology, prefix, namespace);
					newName = uri2NameConverter.getRDFResourceName(uri);
				}
			}
			//System.out.println("Replacing " + name + " with \"" + newName + "\"");
			tripleStore.setRDFResourceName(resource, newName);
		}
	}


	/**
	 * Applies some heuristics to resolve missing owl:imports, especially for RDF Schema files.
	 */
	private boolean runImplicitImports(Set imports)
	        throws Exception {
		long startTime = System.currentTimeMillis();
		Set specialProperties = new HashSet();
		specialProperties.add(owlModel.getRDFSSubPropertyOfProperty());
		specialProperties.add(owlModel.getRDFSSubClassOfProperty());
		specialProperties.add(owlModel.getRDFSRangeProperty());
		specialProperties.add(owlModel.getRDFSDomainProperty());
		specialProperties.add(owlModel.getRDFTypeProperty());
		specialProperties.add(owlModel.getRDFProperty(OWLNames.Slot.ON_PROPERTY));
		Collection us = getUntypedResources();
		Iterator it = us.iterator();
		while(it.hasNext()) {
			RDFResource resource = (RDFResource) it.next();
			Iterator refs = kb.getReferences(resource, 1000).iterator();
			while(refs.hasNext()) {
				Reference ref = (Reference) refs.next();
				if(specialProperties.contains(ref.getSlot())) {
					if(runImplicitImport(resource, imports, us)) {
						return true;
					}
				}
			}
			RDFProperty dummy = new DefaultRDFProperty(owlModel, resource.getFrameID());
			Iterator frames = owlModel.getHeadFrameStore().getFramesWithAnyDirectOwnSlotValue(dummy).iterator();
			if(frames.hasNext()) {
				if(runImplicitImport(resource, imports, us)) {
					return true;
				}
			}
		}
		long endTime = System.currentTimeMillis();
		return false;
	}


	private boolean runImplicitImport(RDFResource resource,
	                                  Set imports,
	                                  Collection untypedResources)
	        throws Exception {
		String uri = resource.getURI();
		if(uri2NameConverter.isTemporaryRDFResourceName(uri)) {
			uri = uri2NameConverter.getURIFromTemporaryName(uri);
		}
		String namespace = owlModel.getNamespaceForURI(uri);
		if(namespace != null) {
			String u = namespace;
			if(u.endsWith("#")) {
				u = u.substring(0, u.length() - 1);
			}
			if(!imports.contains(u)) {
				String importURI = getImplicitImport(u);
				if(importURI != null) {
					logger.logWarning("Trying to add import for external resource: " + importURI);
					runImport(importURI, imports);
					return true;
				}
				else {
					if(patcher == null) {
						patcher = createPatcher();
					}
					if(patcher != null) {
						patcher.patch(untypedResources.iterator(), namespace);
					}
					imports.add(u);
				}
			}
		}
		return false;
	}


	/**
	 * Imports the ontologies that are objects of any owl:imports triples
	 * in the specified <code>TripleStore</code>. Ontologies that have already
	 * been imported will not be imported again.
	 */
	private void processImports(TripleStore tripleStore,
	                            Set imports)
	        throws Exception {
		
		//TODO: This is wrong and will be fixed when db inclusion will work.
		if (owlModel instanceof OWLDatabaseModel) {
			return;
		}
		
		prefixForDefaultNamespace = null;
		RDFProperty owlImportsProperty = owlModel.getRDFProperty(OWLNames.Slot.IMPORTS);
		Iterator ontologies = tripleStore.listSubjects(owlImportsProperty);
		while(ontologies.hasNext()) {
			RDFResource ontology = (RDFResource) ontologies.next();
			Iterator imps = ontology.listPropertyValues(owlImportsProperty);
			while(imps.hasNext()) {
				Instance imp = (Instance) imps.next();
				String impName = imp.getName();
				String uri = null;
				if(uri2NameConverter.isTemporaryRDFResourceName(impName)) {
					uri = uri2NameConverter.getURIFromTemporaryName(impName);
				}
				else if(imp instanceof RDFResource) {
					uri = ((RDFResource) imp).getURI();
				}
				if(uri != null) {
					//System.out.println("Importing " + uri);
					runImport(uri, imports);
				}
			}
		}
	}


	/**
	 * Imports the ontology that has the name specified by <code>uri</code>.
	 *
	 * @param uri     The name of the ontology to be imported.
	 * @param imports A <code>Set</code> that contains a list of strings
	 *                that represent the names of the ontologies that have already been
	 *                imported.
	 */
	private void runImport(final String uri,
	                       final Set imports)
	        throws Exception {
//		AbstractTask task = new AbstractTask("Importing " + uri, false, owlModel.getTaskManager()) {
//			public void runTask()
//			        throws Exception {
				if(imports.contains(uri) == false) {
					imports.add(uri);
					
					URI ontologyNameURI = null;
					try {
						ontologyNameURI = new URI(uri);
					}
					catch(Exception ex) {
                        Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
					}
					
					Repository repository = getRepository(owlModel, ProtegeOWLParser.this.tripleStore, ontologyNameURI);
					if(repository != null) {
						currentDefaultNamespace = uri;
						ProtegeOWLParser.this.tripleStore = tripleStoreModel.createTripleStore(uri);
						tripleStores.add(ProtegeOWLParser.this.tripleStore);
						tripleStoreModel.setActiveTripleStore(ProtegeOWLParser.this.tripleStore);
						
						ARP arp = createARP();
						logger.logImport(uri, repository.getOntologyLocationDescription(ontologyNameURI));
						
						InputStream is = repository.getInputStream(ontologyNameURI);
						// Double check we can get an input stream to read from
						if(is != null) {
							errorOntologyURI = ontologyNameURI;
							
							arp.load(is, uri);
							// Do imports for this import
							processImports(ProtegeOWLParser.this.tripleStore, imports);
						}
						else {
							logger.logWarning("Couldn't get an input stream to read " + uri + " from.");
						}
					}
					else {
						// Something went wrong.  We can't load the import.
						logger.logWarning("Ignoring import " + uri);
					}
				}
//			}
//		};
//		owlModel.getTaskManager().run(task);
	}


	public void setLogger(ProtegeOWLParserLogger logger) {
		this.logger = logger;
	}


	private void setPrefixForDefaultNamespace(String prefixForDefaultNamespace) {
		this.prefixForDefaultNamespace = prefixForDefaultNamespace;
	}


	public void setURI2NameConverter(URI2NameConverter converter) {
		this.uri2NameConverter = converter;
	}

	public static Collection getErrors() {
		return errors;
	}
	
	/**
	 * *****************************************************************************
	 * ARP Interface Implementations
	 */

	private class MyErrorHandler implements ErrorHandler {

		public void error(SAXParseException exception)
		        throws SAXException {
			saveErrors(exception);
		}


		public void fatalError(SAXParseException exception)
		        throws SAXException {
			saveErrors(exception);
		}


		public void warning(SAXParseException exception)
		        throws SAXException {
            saveErrors(exception, false);
		}
		
		protected void saveErrors(SAXParseException ex) {
			saveErrors(ex, true);
		}
		
		protected void saveErrors(SAXParseException ex, boolean isError) {			
			
			String message = (isError ? "An error " : "A warning ") + "occurred at parsing the OWL ontology ";
			
			message = message + "\n\n    " + errorOntologyURI + "\n\n";
           	message = message + "    at line " + ex.getLineNumber() + " and column " + ex.getColumnNumber() + ".\n";            	
        	message = message + "    Jena parse error message: " + ex.getMessage();
        	
        	Log.getLogger().log(isError ? Level.SEVERE : Level.WARNING, message, ex);
        	
        	errors.add(new MessageError(ex, message));
					            
		}
						
	}

	private class MyNamespaceHandler implements NamespaceHandler {

		private RDFResource ontology;

		private Map prefix2Namespace = new HashMap();


		public void startPrefixMapping(String prefix,
		                               String namespace) {
			if(!namespace.endsWith("#") && !namespace.endsWith("/") && !namespace.endsWith(":")) {
				logger.logWarning("Invalid namespace \"" + namespace + "\" for prefix \"" + prefix + "\" ignored.");
			}
			else {
				// System.out.println("Starting mapping \"" + prefix + "\" -> " + namespace);
				if(prefixForDefaultNamespace != null && prefix.length() == 0) {
					owlModel.getNamespaceManager().setPrefix(namespace, prefixForDefaultNamespace);
					prefix = prefixForDefaultNamespace;
					prefixForDefaultNamespace = null;
				}
				prefix = uri2NameConverter.addPrefix(namespace, prefix);
				prefix2Namespace.put(prefix, namespace);
			}
		}


		public void endPrefixMapping(String prefix) {
			if (ontology == null) {
				ontology = TripleStoreUtil.getFirstOntology(owlModel, tripleStore);
				if(ontology == null) {
					String defaultNamespace = (String) prefix2Namespace.get("");
					if(defaultNamespace == null) {
						logger.logWarning("No default namespace found in file.  Will use " + currentDefaultNamespace);
						defaultNamespace = currentDefaultNamespace;
						prefix2Namespace.put("", defaultNamespace);
					}
					FrameID id = tripleStore.generateFrameID();
					ontology = new DefaultOWLOntology(owlModel, id);
					String tempName = uri2NameConverter.getTemporaryRDFResourceName(defaultNamespace);
					tripleStore.setRDFResourceName(ontology, tempName);
					tripleStore.add(ontology, owlModel.getRDFTypeProperty(), owlModel.getOWLOntologyClass());
					tripleStore.getNarrowFrameStore().addValues(ontology,
					                                            ((KnowledgeBase) owlModel).getSlot(OWLNames.Slot.ONTOLOGY_PREFIXES),
					                                            null, false, Collections.singleton(":" + defaultNamespace));
					// ontology = owlModel.getDefaultOWLOntology();
				}
			}
			String namespace = (String) prefix2Namespace.get(prefix);
                        if (namespace != null) {
				//System.out.println("- Registering \"" + prefix + "\" : " + namespace);
				replaceNamespace(tripleStore, ontology, prefix, namespace);
				owlModel.getNamespaceManager().update();
                        }
		}
	}

	private class MyStatementHandler implements StatementHandler {

		public void statement(AResource aSubject,
		                      AResource aPredicate,
		                      AResource aObject) {
			RDFProperty predicate = getRDFProperty(aPredicate);
			if(rdfRestProperty.equals(predicate)) {
				isRDFList = true;
			}
			RDFResource object = getRDFResource(aObject);
			if(predicate.equals(rdfTypeProperty)) {
				currentType = object;
			}
			isRDFList = rdfFirstProperty.equals(predicate) || rdfRestProperty.equals(predicate);
			RDFResource subject = getRDFResource(aSubject);
			isRDFList = false;
			currentType = null;
			if(tripleStoreModel.getPropertyValues(subject, predicate).contains(object)) {
				//System.out.println(" -> Duplicated");
			}
			else {
				tripleStore.add(subject, predicate, object);
				logger.logTripleAdded(subject, predicate, object);
				tripleCount++;
				if(tripleCount % 5000 == 0 /*&& task != null*/) {
					//task.setMessage("Loaded " + tripleCount + " triples");
                    Log.getLogger().info("Loaded " + tripleCount + " triples");
                }
			}
		}


		public void statement(AResource aSubject,
		                      AResource aPredicate,
		                      ALiteral aLiteral) {
			// System.out.println("Lit: " + aSubject + " " + aPredicate + " " + " " + aLiteral);
			RDFResource subject = getRDFResource(aSubject);
			RDFProperty predicate = getRDFProperty(aPredicate);
			Object object = createLiteralObject(aLiteral, predicate);
			tripleStore.add(subject, predicate, object);
			logger.logTripleAdded(subject, predicate, object);
			tripleCount++;
			if(tripleCount % 5000 == 0 /*&& task != null*/) {
				//task.setMessage("Loaded " + tripleCount + " triples");
                Log.getLogger().info("Loaded " + tripleCount + " triples");
            }
		}
	}

	/**
	 * An interface needed as an abstraction of the various methods to invoke the Jena ARP
	 * (the various load methods with different parameters).
	 */
	public interface ARPInvokation {

		public void invokeARP(ARP arp)
		        throws Exception;
	}


	/**
	 * A convenience method that dynamically adds an import to a JenaOWLModel.
	 * This will immediately load the file into a new TripleStore.  Prior to
	 * invoking this method, the caller should define a prefix for the expected
	 * namespace (e.g., URI + "#").  Following the call, the caller should add
	 * an import statement to an existing OWLOntology (usually the default ontology).
	 *
	 * <i>Note that the
	 * preferred method of adding imports is to use the <code>ImportHelper</code>, since
	 * this takes care of "house keeping" tasks that this "raw" addImport method does not.</i>
	 *
	 * @param owlModel
	 * @param ontologyName The name of the imported ontology.
	 * @throws Exception
	 */
	public static void addImport(JenaOWLModel owlModel,
	                             URI ontologyName)
	        throws Exception {
		addImport(owlModel, ontologyName, null);
	}


	/**
	 * A convenience method that dynamically adds an import to a JenaOWLModel.
	 * This will immediately load the file into a new TripleStore.
	 * Following the call, the caller should add an import statement to an
	 * existing OWLOntology (usually the default ontology).
	 *
	 * <i>Note that the
	 * preferred method of adding imports is to use the <code>ImportHelper</code>, since
	 * this takes care of "house keeping" tasks that this "raw" addImport method does not.</i>
	 *
	 * @param owlModel
	 * @param ontologyName
	 * @param prefixForDefaultNamespace an (optional) prefix that shall be used for the
	 *                                  the default namespace of the directly imported ontology
	 * @throws Exception
	 */
	public static void addImport(JenaOWLModel owlModel,
	                             URI ontologyName,
	                             String prefixForDefaultNamespace)
	        throws Exception {
		// Get hold of the input stream for the ontology.
		// The ontology repository will do this.
		TripleStore activeTripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
		Repository rep = getRepository(owlModel, activeTripleStore, ontologyName);
		if(rep != null) {
			TripleStore ts = owlModel.getTripleStoreModel().createTripleStore(ontologyName.toString());
			
			ProtegeOWLParser parser = new ProtegeOWLParser(owlModel, true);
			
			if(prefixForDefaultNamespace != null) {
				parser.setPrefixForDefaultNamespace(prefixForDefaultNamespace);
			}
			InputStream is = rep.getInputStream(ontologyName);
			
			parser.loadTriples(ts, ontologyName.toString(), parser.createARPInvokation(is, ontologyName.toString()));
			
			owlModel.getTripleStoreModel().setActiveTripleStore(activeTripleStore);
			owlModel.getOWLFrameStore().copyFacetValuesIntoNamedClses();
		}
	}


	private static Repository getRepository(OWLModel owlModel,
	                                        TripleStore tripleStore,
	                                        URI ontologyName) {
		RepositoryManager rm = owlModel.getRepositoryManager();
		// Get the repository (ask the system to create a HTTP repository if necessary)
		Repository rep = rm.getRepository(ontologyName, true);
		if(rep == null) {
			rep = unresolvedImportHandler.handleUnresolvableImport(owlModel, tripleStore, ontologyName);
			if(rep != null) {
				rm.addProjectRepository(0, rep);
			}
		}
		return rep;
	}


	public static InputStream getInputStream(URL url)
	        throws IOException {
		if(url.getProtocol().equals("http")) {
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Accept", "application/rdf+xml");
			conn.addRequestProperty("Accept", "text/xml");
			conn.addRequestProperty("Accept", "*/*");
			return conn.getInputStream();
		}
		else {
			return url.openStream();
		}
	}


}
