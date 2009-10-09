package edu.stanford.smi.protegex.owl.repository.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.parser.ProtegeOWLParser;
import edu.stanford.smi.protegex.owl.repository.factory.RepositoryFactory;
import edu.stanford.smi.protegex.owl.repository.util.OntologyNameExtractor;
import edu.stanford.smi.protegex.owl.repository.util.URLInputSource;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 18, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class HTTPRepository extends AbstractStreamBasedRepositoryImpl {
    private static transient Logger log = Log.getLogger(HTTPRepository.class);

    private URL ontologyURL;

    private URI ontologyName;


    static {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        factory.registerRepositoryFactoryPlugin(new HTTPRepositoryFactoryPlugin());
    }


    public HTTPRepository(URL ontologyURL) {
        this.ontologyURL = ontologyURL;
        this.ontologyName = null;
        update();
    }


    public String getRepositoryDescriptor() {
        return ontologyURL.toString();
    }


    public String getOntologyLocationDescription(URI ontologyName) {
        String s = "";
        if (this.ontologyName.equals(ontologyName)) {
            s = ontologyURL.toString();
        }
        return s;
    }


    private void update() {
        try {
            PrintStream oldErr = System.err;
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int b)
                        throws IOException {
                }
            }));
            OntologyNameExtractor extractor = new OntologyNameExtractor(new URLInputSource(ontologyURL));
            ontologyName = extractor.getOntologyName();
            System.setErr(oldErr);
        } catch (IOException e) {
        	Log.getLogger().warning("Could not get ontology from URL: " + ontologyURL);
		}
    }


    public boolean isSystem() {
        return false;
    }


    public URL getOntologyURL() {
        return ontologyURL;
    }


    public boolean contains(URI ontologyName) {
        if (this.ontologyName != null) {
            return this.ontologyName.equals(ontologyName);
        }
        return false;
    }


    public void refresh() {
        update();
    }


    public Collection<URI> getOntologies() {
        if (ontologyName != null) {
            return Collections.singleton(ontologyName);
        }
        else {
            return Collections.emptyList();
        }
    }


    public URL getLocation(URI ontologyName) {
        if (ontologyName == null) {
            return null;
        }
        if (ontologyName.equals(this.ontologyName)) {
            return ontologyURL;
        }
        else {
            return null;
        }
    }


    @Override
    public InputStream getInputStream(URI ontologyName)
            throws OntologyLoadException {
        return ProtegeOWLParser.getInputStream(ontologyURL);
    }


    public boolean isWritable(URI ontologyName) {
        try {
            File file = new File(new URI(ontologyURL.toString()));
            return file.canWrite();
        }
        catch (Exception e) {
          Log.emptyCatchBlock(e);
          return false;
        }
    }


    public OutputStream getOutputStream(URI ontologyName)
            throws IOException {
        OutputStream os = null;
        if (isWritable(ontologyName)) {
            try {
                File file = new File(new URI(ontologyURL.toString()));
                os = new FileOutputStream(file);
            }
            catch (URISyntaxException e) {
              Log.getLogger().log(Level.SEVERE, "Exception caught", e);
            }
        }
        return os;
    }


    public String getRepositoryDescription() {
        return "URL";
    }
}

