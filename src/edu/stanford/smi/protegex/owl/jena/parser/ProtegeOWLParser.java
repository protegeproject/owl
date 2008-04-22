package edu.stanford.smi.protegex.owl.jena.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
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

import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.NamespaceManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.factory.AlreadyImportedException;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
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
    public final static String CREATE_UNTYPED_RESOURCES = "protegeowl.parser.create.untyped.resources";

    private boolean importing = false;
    
	private static Collection errors;

	private static URI errorOntologyURI;

	private ProtegeOWLParserLogger logger;

	private OWLModel owlModel;

	private int tripleCount;
	
	private TripleProcessor tripleProcessor;


	public ProtegeOWLParser(OWLModel owlModel) {
		tripleCount = 0;
		errorOntologyURI = null;
		errors = new ArrayList();
		
		this.owlModel = owlModel;
		logger = createLogger();
	}


	
	/************************************ ARP ****************************************/
	
	/**
	 * An interface needed as an abstraction of the various methods to invoke the Jena ARP
	 * (the various load methods with different parameters).
	 */
	public interface ARPInvokation {

		public void invokeARP(ARP arp)
		        throws Exception;
	}
	
	
	private ARPInvokation createARPInvokation(final InputStream inputStream, final String uri) {
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


	private ARPInvokation createARPInvokation(final Reader reader, final String uri) {
		ARPInvokation invokation = new ARPInvokation() {
			public void invokeARP(ARP arp) throws Exception {
				setErrorLevel(arp);				
				arp.load(reader, uri);					
				reader.close();
			}
		};
		return invokation;
	}
	

	protected ARP createARP(TripleStore tripleStore) {
		ARP arp = new ARP();
		ARPHandlers handlers = arp.getHandlers();
		handlers.setStatementHandler(new ProtegeOWLStatementHandler(tripleStore));
		handlers.setErrorHandler(new ProtegeOWLErrorHandler());
		handlers.setNamespaceHandler(new ProtegeOWLNamespaceHandler(tripleStore));
		arp.setHandlersWith(handlers);
		return arp;
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

	
	/**
	 * This method loads an ontology pointed to by the specified URI.
	 *
	 * @param uri The <code>URI</code> that points to the ontology.
	 */
	public void run(final URI uri)
	        throws IOException {
		
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
	        throws IOException {
		
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
	        throws IOException {
		
		run(xmlBase, createARPInvokation(reader, xmlBase));
	}


	protected void run(final String uri,
	                   final ARPInvokation invokation)
	        throws IOException {
		
		errors = new ArrayList();
		errorOntologyURI = null;
		URI xmlBase = null;
		if (uri != null) {
		    xmlBase = XMLBaseExtractor.getXMLBase(uri);
		}
		if (xmlBase == null) {
		    xmlBase = URIUtilities.createURI(uri);
		}
		loadTriples(uri, xmlBase, invokation);
	}


	/************************************** Loading triples *********************************/

	
	public void loadTriples(String ontologyName, URI xmlBase, InputStream is) throws IOException {
	    loadTriples(ontologyName, xmlBase, createARPInvokation(is, ontologyName.toString()));
	}
	
	private void loadTriples(final String ontologyName, URI xmlBase, final ARPInvokation invokation) throws IOException {
		//the triple store where the parsing will write the parsed triples
	    final TripleStore tripleStore = owlModel.getTripleStoreModel().getActiveTripleStore();
	    
	    if (ontologyName != null) { 
	    	tripleStore.addIOAddress(ontologyName.toString()); 
	    }
	    
	    if (xmlBase != null) {
	    	tripleStore.addIOAddress(xmlBase.toString());
	    	tripleStore.setOriginalXMLBase(xmlBase.toString());
	    }
	    
	    boolean eventsEnabled = owlModel.setGenerateEventsEnabled(false);
	    try {
	        tripleProcessor = ((AbstractOWLModel) owlModel).getUndefTripleManager().getTripleProcessor();

	        Log.getLogger().info("Loading triples");

	        ARP arp = createARP(tripleStore);

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
	        tripleProcessor.getUndefTripleManager().dumpUndefTriples(Level.FINE);	
	        //tripleProcessor.processUndefTriples();
	        if (log.isLoggable(Level.FINE)) {
	            log.fine("Start processing imports ...");
	        }

	        processImports(tripleStore);

	        if (log.isLoggable(Level.FINE)) {
	            log.fine("End processing imports");
	        }

	        //tripleProcessor.processUndefTriples();
	        
	        //post processing is done only at the very end..
	        //tripleProcessor.doPostProcessing();
	        
	        handleNoOntologyDeclarationFound(tripleStore, ontologyName, xmlBase);
	        
	        if (!importing) {
	            doFinalPostProcessing(owlModel);
	        }
	        
	    } catch (AlreadyImportedException e) {
	        Log.getLogger().warning("Broken import led to attempt to import the same ontology twice");
	        owlModel.getTripleStoreModel().deleteTripleStore(tripleStore);
	    }
	    finally {
	        owlModel.setGenerateEventsEnabled(eventsEnabled);
	    }
	}
	
	
	private void handleNoOntologyDeclarationFound(TripleStore tripleStore, String ontologyName, URI xmlBase) throws AlreadyImportedException {
		 // no ontology declaration was found
        if (tripleStore.getName() == null) {
            String oname = null;
            
            if (ontologyName != null) {
            	oname = ontologyName; 
            } else if (xmlBase != null) {
            	oname = xmlBase.toString(); 
            }
            else { 
            	oname = FactoryUtils.generateOntologyURIBase(); 
            }

            FactoryUtils.addOntologyToTripleStore(owlModel, tripleStore, oname);
        }
	}
	
	
	public static void doFinalPostProcessing(OWLModel owlModel) {
	    TripleProcessor tripleProcessor = ((AbstractOWLModel) owlModel).getUndefTripleManager().getTripleProcessor();
		tripleProcessor.doPostProcessing();
       
        //copy restrictions in facets
        if (owlModel instanceof JenaOWLModel)  {
            ((JenaOWLModel) owlModel).copyFacetValuesIntoNamedClses();
        }
        
		//sort subclasses if needed
        TripleStoreUtil.sortSubclasses(owlModel);
	}	
	
	
	/************************************* Imports *************************************/


	public void setImporting(boolean importing) {
	    this.importing = importing;
	}

	private void processImports(TripleStore tripleStore) throws IOException {				
		Set<String> thisOntoImports = OWLImportsCache.getOWLImportsURI(tripleStore.getName());
		
		for (String import_ : thisOntoImports) {
			((AbstractOWLModel) owlModel).loadImportedAssertions(URIUtilities.createURI(import_));
		}		
	}

	
	/*************************************** Management  *******************************/
	
	/**
	 * Gets the ontology name of the most recently parsed file.  This can be used to diagnose
	 * where an exception has occured.	 
	 * @return the error ontology URI or null
	 */	
	public static URI getErrorURI() {
		return errorOntologyURI;
	}	

	public static Collection getErrors() {
		return errors;
	}
	
	protected ProtegeOWLParserLogger createLogger() {
		return new DefaultProtegeOWLParserLogger();
	}

	protected ProtegeOWLParserLogger getLogger() {
		return logger;
	}

	public void setLogger(ProtegeOWLParserLogger logger) {
		this.logger = logger;
	}
	
	public static InputStream getInputStream(URL url) throws IOException {
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
	

	/*********************************** ARP Interface Implementations *****************************/

	private class ProtegeOWLErrorHandler implements ErrorHandler {

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

	
	class ProtegeOWLStatementHandler implements StatementHandler {
		
		TripleStore tripleStore;
		
		public ProtegeOWLStatementHandler(TripleStore tripleStore) {
			this.tripleStore = tripleStore;
		}


		public void statement(AResource subj, AResource pred, AResource obj) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("NewStatementHandler: " + subj + "  " + pred + "  " + obj);
                    }
                    tripleCount++;
                    if(tripleCount % 5000 == 0) {			
                        Log.getLogger().info("Loaded " + tripleCount + " triples");
                    }
			
                    tripleProcessor.processTriple(subj, pred, obj, tripleStore, false);			
		}


		public void statement(AResource subj, AResource pred, ALiteral lit) {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine(subj + "  " + pred + "  " + lit);
                    }
			
                    tripleCount++;
                    if(tripleCount % 5000 == 0) {			
                        Log.getLogger().info("Loaded " + tripleCount + " triples");
                    }
			
                    tripleProcessor.processTriple(subj, pred, lit, tripleStore, false);			
		}		
	}

	
	class ProtegeOWLNamespaceHandler implements NamespaceHandler {
		
		TripleStore tripleStore;

	    public ProtegeOWLNamespaceHandler(TripleStore tripleStore) {
	    	this.tripleStore = tripleStore;
		}

		public void endPrefixMapping(String prefix) { // does nothing, just for logging
	        NamespaceManager namespaceManager = owlModel.getNamespaceManager();
	        
	        if (log.isLoggable(Level.FINE)) {
                log.fine("*** " + prefix + " -> " + namespaceManager.getNamespaceForPrefix(prefix));
            }

	    }

		public void startPrefixMapping(String prefix, String namespace) {
		    NamespaceManager namespaceManager = owlModel.getNamespaceManager();
		    namespaceManager.setPrefix(namespace, prefix);
		}		
	}	

}
