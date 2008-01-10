package edu.stanford.smi.protegex.owl.jena.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import com.hp.hpl.jena.rdf.arp.StatementHandler;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFUntypedResource;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.repository.util.XMLBaseExtractor;

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

    private boolean importing = false;
    
	private static Collection errors;

	private static URI errorOntologyURI;

	/**
	 * A rather ugly flag that can be activated to prompt the user if a local
	 * file was found as a possible import redirection.
	 */
	//public static boolean inUI = false;

	private boolean isRDFList = false;

	private ProtegeOWLParserLogger logger;

	private OWLModel owlModel;

	private static final String RDFS_RESOURCE_URI = RDFS.Resource.getURI();
	
	private Map<String, RDFUntypedResource> untypedResources = new HashMap<String, RDFUntypedResource>();

	private int tripleCount;
	
	private TripleFrameCache tfc;
	
//	private AbstractTask task;


	public ProtegeOWLParser(OWLModel owlModel,
	                        boolean incremental) {
		tripleCount = 0;
		errorOntologyURI = null;
		errors = new ArrayList();
		
		this.owlModel = owlModel;

		if(incremental) {
			populateUntypedResourcesMap();
		}
		logger = createLogger();
	}
	
	
	public void setImporting(boolean importing) {
	    this.importing = importing;
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
		
		loadTriples(uri, XMLBaseExtractor.getXMLBase(uri), invokation);
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
	
	
	public void loadTriples(String ontologyName, URI xmlBase, InputStream is) throws IOException {
	    loadTriples(ontologyName, xmlBase, createARPInvokation(is, ontologyName.toString()));
	}
	
	private void loadTriples(final String ontologyName, URI xmlBase, final ARPInvokation invokation) throws IOException {
	    final TripleStore tripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
	    boolean eventsEnabled = owlModel.setGenerateEventsEnabled(false);
		
		tfc = new TripleFrameCache(owlModel, tripleStore);
		
		Log.getLogger().info("Loading triples");

		ARP arp = createARP();

        if (xmlBase != null) {
            tripleStore.setOriginalXMLBase(xmlBase.toString());
        }
		
		long startTime = System.currentTimeMillis();	
		
		try {
		    invokation.invokeARP(arp);
		} catch (IOException e) {
		    throw e;
		} catch (Exception e) {
		    IOException ioe = new IOException(e.getMessage());
		    ioe.initCause(e);
		    throw ioe;
		}
		
		long endTime = System.currentTimeMillis();

		Log.getLogger().info("[ProtegeOWLParser] Completed triple loading after " + (endTime - startTime) + " ms");

		log.info("\nDump before end processing. Size: " + tfc.getUndefTripleManager().getUndefTriples().size());
		// tfc.getUndefTripleManager().dumpUndefTriples();

		tfc.processUndefTriples();
		//tfc.doPostProcessing();

		//TT - This check should not be here.. The load method should be called only at the first parsing, the imports should call other methods
		if (!importing) {
			/* Delete the old default ontology and set the new one. The old default ontology has been created artifically at initialization,
			 * and it should not just stay around in the ontology.
			 */
			OWLOntology newDefaultOWLOntology = (OWLOntology) ((KnowledgeBase) owlModel).getFrame(tripleStore.getName());			
			OWLOntology oldDefaultOntology = owlModel.getDefaultOWLOntology();
			
			if (!oldDefaultOntology.equals(newDefaultOWLOntology) && oldDefaultOntology.getName().equals(ProtegeNames.DEFAULT_ONTOLOGY)) {
				oldDefaultOntology.delete();
			}
			
			((AbstractOWLModel)owlModel).setDefaultOWLOntology(newDefaultOWLOntology);
		}
		
		//tfc.getUndefTripleManager().dumpUndefTriples();
		log.info("Dump after end processing. Size: "	+ tfc.getUndefTripleManager().getUndefTriples().size());
		
		log.info("Start processing imports ...");
				
		processImports(tripleStore);
		
		log.info("End processing imports");

		tfc.processUndefTriples();
		tfc.doPostProcessing();

		owlModel.setGenerateEventsEnabled(eventsEnabled);
	}

	protected ARP createARP() {
		ARP arp = new ARP();
		ARPHandlers handlers = arp.getHandlers();
		//handlers.setStatementHandler(new MyStatementHandler());
		handlers.setStatementHandler(new NewStatementHandler());
		handlers.setErrorHandler(new MyErrorHandler());
		handlers.setNamespaceHandler(new NewNamespaceHandler());
		//handlers.setNamespaceHandler(new MyNamespaceHandler());
		arp.setHandlersWith(handlers);
		return arp;
	}

	protected ProtegeOWLParserLogger createLogger() {
		return new DefaultProtegeOWLParserLogger();
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

	@SuppressWarnings("unchecked")
    private void populateUntypedResourcesMap() {
		RDFSNamedClass cls = owlModel.getRDFUntypedResourcesClass();
		Iterator it = cls.getInstances(false).iterator();
		while(it.hasNext()) {
			RDFUntypedResource r = (RDFUntypedResource) it.next();
			String uri = r.getURI();
			untypedResources.put(uri, r);
		}
	}
	

	private void processImports(TripleStore tripleStore) throws IOException {				
		Set<String> thisOntoImports = OWLImportsCache.getOWLImportsURI(tripleStore.getName());
		
		for (String import_ : thisOntoImports) {
			owlModel.addImport(URIUtilities.createURI(import_));
		}	
		
	}
	



	public void setLogger(ProtegeOWLParserLogger logger) {
		this.logger = logger;
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

	/**
	 * An interface needed as an abstraction of the various methods to invoke the Jena ARP
	 * (the various load methods with different parameters).
	 */
	public interface ARPInvokation {

		public void invokeARP(ARP arp)
		        throws Exception;
	}	

	public static InputStream getInputStream(URL url)
	        throws IOException {
		if(url.getProtocol().equals("http")) {
			URLConnection conn = url.openConnection();
						
			conn.setConnectTimeout(ApplicationProperties.getUrlConnectTimeout()*1000);
			conn.setReadTimeout(ApplicationProperties.getUrlConnectReadTimeout()*1000);
			
			conn.setRequestProperty("Accept", "application/rdf+xml");
			conn.addRequestProperty("Accept", "text/xml");
			conn.addRequestProperty("Accept", "*/*");
			return conn.getInputStream();
		}
		else {
			return url.openStream();
		}
	}

	
	class NewStatementHandler implements StatementHandler {

		public void statement(AResource subj, AResource pred, AResource obj) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("NewStatementHandler: " + subj + "  " + pred + "  " + obj);
                    }
                    tripleCount++;
                    if(tripleCount % 5000 == 0) {			
                        Log.getLogger().info("Loaded " + tripleCount + " triples");
                    }
			
                    tfc.processTriple(subj, pred, obj, false);	
			
		}


		public void statement(AResource subj, AResource pred, ALiteral lit) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine(subj + "  " + pred + "  " + lit);
                    }
			
                    tripleCount++;
                    if(tripleCount % 5000 == 0) {			
                        Log.getLogger().info("Loaded " + tripleCount + " triples");
                    }
			
                    tfc.processTriple(subj, pred, lit, false);	
			
		}
		
	}

	
	class NewNamespaceHandler implements NamespaceHandler {

	    public void endPrefixMapping(String prefix) {
	        NamespaceManager namespaceManager = owlModel.getNamespaceManager();
	        log.info("*** " + prefix + " -> " + namespaceManager.getNamespaceForPrefix(prefix));
	    }

		public void startPrefixMapping(String prefix, String namespace) {
		    NamespaceManager namespaceManager = owlModel.getNamespaceManager();
		    namespaceManager.setPrefix(namespace, prefix);
		}
		
	}
	

}
