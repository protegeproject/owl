package edu.stanford.smi.protegex.owl.repository.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.arp.ALiteral;
import com.hp.hpl.jena.rdf.arp.ARP;
import com.hp.hpl.jena.rdf.arp.ARPHandlers;
import com.hp.hpl.jena.rdf.arp.AResource;
import com.hp.hpl.jena.rdf.arp.StatementHandler;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 12, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A utility class that extracts the name (URI) of
 * an ontology from an OWL file.  The name of an ontology is
 * the xml:base URI.
 */
public class OntologyNameExtractor {
    private static Logger log = Log.getLogger(OntologyNameExtractor.class);
    
    private InputStreamSource source;

    private URI uri;
    private URL documentURL;

    private boolean valid = true;


    /**
     * Constructs an <code>OntologyNameExtractor</code> that
     * should obtain the name of an ontology which can be
     * read via the specified input stream.
     *
     * @param is The input stream from which the ontology
     *           can be read.
     */
    public OntologyNameExtractor(InputStreamSource source) {
        this.source = source;
        init();
    }
    
    private void init() {
        searchForOntologyDeclaration();
        if (uri == null && valid) {
            searchForXMLBaseDeclaration();
        }
        if (uri == null && valid) {
            useDocumentLocation();
        }
    }


    private void searchForOntologyDeclaration() {
        ARP arp = new ARP();
        ARPHandlers handlers = arp.getHandlers();
        handlers.setStatementHandler(new StatementHandler() {
            
            public void statement(AResource subject, AResource predicate, ALiteral value) {

            }
            
            public void statement(AResource subject, AResource predicate, AResource object) {
                if (predicate.getURI().equals(RDFNames.Slot.TYPE) && object.getURI().equals(OWLNames.Cls.ONTOLOGY)) {
                    uri = URI.create(subject.getURI());
                    throw new OntologyDeclarationFoundException();
                }
            }
        });
        handlers.setErrorHandler(new ErrorHandler() {
            
            public void warning(SAXParseException exception) throws SAXException {

            }
            
            public void fatalError(SAXParseException exception) throws SAXException {
                valid = false;
            }
            
            public void error(SAXParseException exception) throws SAXException {
                valid = false;
            }
        });
        InputStream is = null; 
        try {
            is = source.getInputStream();
            arp.load(is);
        }
        catch (OntologyDeclarationFoundException odfe) {
            ;
        }
        catch (Throwable t) {
            log.log(Level.WARNING, "Exception caught trying to parse " + documentURL + " for an ontology declaration", t);
            valid = false;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Exception found closing ontology", e);
            }
        }
    }
    
    private void searchForXMLBaseDeclaration() {
        InputStream is = null;
        try {
            is = source.getInputStream();
            uri = XMLBaseExtractor.getXMLBase(is);
        }
        catch (Throwable t) {
            valid  = false;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.log(Level.WARNING, "Exception found closing ontology", e);
            }  
        }
        
    }
    
    private void useDocumentLocation() {
        try {
            uri = documentURL.toURI();
        }
        catch (URISyntaxException urise) {
            log.log(Level.WARNING, "Could not find name for ontology.  Even the document location didn't work", urise);
        }
    }


    public boolean isPossiblyValidOntology() {
        return valid;
    }


    public URI getOntologyName() throws IOException {
        return uri;
    }
    
    public static class OntologyDeclarationFoundException extends RuntimeException {
        
    }
    
    
}

